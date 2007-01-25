/* -*- mode: C++; c-basic-offset: 4 -*-
 *
 * CCM Tools : C++ Code Generator 
 * Egon Teiniker <egon.teiniker@tugraz.at>
 * copyright (c) 2002, 2003 Salomon Automation
 *
 * $Id: CCMContainer.cc,v 1.2 2005/03/08 08:03:05 teiniker Exp $
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

/* 
 * This code is based on the MicoCCM implementation of a CCM session container.
 */

#ifdef HAVE_CONFIG_H
#  include <config.h>
#endif 

#ifdef HAVE_MICO  

#include <WX/Utils/debug.h>

#include "CCMContainer.h"

using namespace std;
using namespace WX::Utils;


//============================================================================
// CCM Containers
//============================================================================

/*
 * There is one container per home. Multiple containers may be active in
 * a single process. Each container creates a new POA, where it activates
 * the home, all components and all facets.
 */

CORBA::ULong CCM::ContainerBase::globalContainerNumber_ = 0;

CCM::ContainerBase::ContainerBase()
{
    LDEBUGNL(CCM_CONTAINER, "+CCM::ContainerBase::ContainerBase()");
    containerNumber_ = ++globalContainerNumber_;
}

CCM::ContainerBase::~ContainerBase ()
{    
    LDEBUGNL(CCM_CONTAINER, "-CCM::ContainerBase::~ContainerBase()");
}



//============================================================================
// Session Container Implementation 
//============================================================================

CCM::SessionContainer::SessionContainer (CORBA::ORB_ptr orb)
{
    LDEBUGNL(CCM_CONTAINER, "+SessionContainer()");
    
    CORBA::Object_var obj;
    char name[256];
    _have_info = 0;
    orb_ = CORBA::ORB::_duplicate(orb);

    // Create a private POA
    // TODO: Don't use RootPOA
    sprintf(name, "SessionContainer-%lu", containerNumber_);
    obj = orb_->resolve_initial_references ("RootPOA");
    PortableServer::POA_var poa = PortableServer::POA::_narrow(obj);
    
    CORBA::PolicyList pl;
    poa_ = poa->create_POA(name, poa->the_POAManager(), pl);
}


CCM::SessionContainer::~SessionContainer ()
{
    LDEBUGNL(CCM_CONTAINER, "-SessionContainer()");
}


void 
CCM::SessionContainer::load(const ComponentInfo& info)
{
    LDEBUGNL(CCM_CONTAINER, " load()");

    _info = info;
    _have_info = 1;

    //Activate home singleton
    PortableServer::ObjectId_var oid =
	poa_->activate_object(_info.home_glue.in());
    _home_ref = poa_->id_to_reference(oid.in());
}


void 
CCM::SessionContainer::activate ()
{
    LDEBUGNL(CCM_CONTAINER, " activate()");

    // TODO:
    // Set instances to active, if their configuration is complete
    // iterate through an InstanceMap and call ccm_activate()
    
    // Activate our POA
    PortableServer::POAManager_var mgr;
    mgr = poa_->the_POAManager ();
    mgr->activate ();
}


void 
CCM::SessionContainer::passivate ()
{
    LDEBUGNL(CCM_CONTAINER, " passivate()");

    // TODO:
    // Set instances to inactive
    // iterate through an InstanceMap and call instance->ccm_passivate()

    // Deactivate our POA
    PortableServer::POAManager_var mgr = poa_->the_POAManager();
    mgr->hold_requests (1);
}


void 
CCM::SessionContainer::remove ()
{
    LDEBUGNL(CCM_CONTAINER, " remove()");

    // TODO:
    // Prepare instances for destruction
    // iterate through an InstanceMap and call instance->ccm_remove()
    
    passivate ();
    poa_->destroy(1, 1);  // Destroy our POA
}


CORBA::Boolean 
CCM::SessionContainer::compare(Components::CCMHome_ptr ohome)
{
    LDEBUGNL(CCM_CONTAINER, " compare()");
    
    PortableServer::ObjectId_var myid = poa_->reference_to_id(_home_ref);
    PortableServer::ObjectId_var oid;
    
    try {
	oid = poa_->reference_to_id (ohome);
    }
    catch (PortableServer::POA::WrongAdapter &) {
	return 0;
    }
    
    // HACK !!!!!!!!!!!!
    //    return ((myid->length() == oid->length()) &&
    // 	  memcmp (myid->get_buffer(), oid->get_buffer(), myid->length()) == 0);
    return false;
}


Components::CCMHome_ptr 
CCM::SessionContainer::get_reference_for_home ()
{
    LDEBUGNL(CCM_CONTAINER, " get_reference_for_home()");
    return Components::CCMHome::_narrow (_home_ref.in());
}


Components::CCMObject_ptr
CCM::SessionContainer::activate_component(PortableServer::Servant skel)
{
    LDEBUGNL(CCM_CONTAINER, " CCM::SessionContainer->activate_component()");
    PortableServer::ObjectId_var oid = poa_->activate_object(skel);
    CORBA::Object_var ref = poa_->id_to_reference (oid.in());
    return Components::CCMObject::_narrow (ref);
}


Components::CCMObject_ptr
CCM::SessionContainer::get_reference_for_component (PortableServer::Servant s)
{
    LDEBUGNL(CCM_CONTAINER, " get_reference_for_component()");
    CORBA::Object_var o = poa_->servant_to_reference (s);
    return Components::CCMObject::_narrow (o);
}


PortableServer::Servant
CCM::SessionContainer::get_skeleton_for_reference (CORBA::Object_ptr o)
{
    LDEBUGNL(CCM_CONTAINER, " get_skeleton_for_reference()");
    return poa_->reference_to_servant (o);
}


void 
CCM::SessionContainer::deactivate_component (CORBA::Object_ptr o)
{
    LDEBUGNL(CCM_CONTAINER, " deactivate_component()");
    //PortableServer::ServantBase_var skel = get_skeleton_for_reference (o);
    PortableServer::Servant skel = get_skeleton_for_reference(o);
    deactivate_component (skel);
}


void 
CCM::SessionContainer::deactivate_component (PortableServer::Servant skel)
{
    LDEBUGNL(CCM_CONTAINER, " deactivate_component()");
    PortableServer::ObjectId_var oid = poa_->servant_to_id (skel);
  /*
    PerComponentData & data = active_components[oid.in()];
    map<string, PortableServer::ServantBase_var, less<string> >::iterator it;
    
    for (it=data.facet_glue.begin(); it!=data.facet_glue.end(); it++) {
    PortableServer::ObjectId_var fid =
    poa_->servant_to_id ((*it).second);
    poa_->deactivate_object (fid.in());
    }
  */
    poa_->deactivate_object (oid);
    //active_components.erase (oid.in());
}


CORBA::Object_ptr
CCM::SessionContainer::activate_facet(PortableServer::Servant comp_glue,
				      const char * name,
				      void* facet_instance,
				      PortableServer::Servant facet_glue)
{
    LDEBUGNL(CCM_CONTAINER, " activate_facet()");
    PortableServer::ObjectId_var oid = poa_->servant_to_id (comp_glue);
    PortableServer::ObjectId_var fid = poa_->activate_object (facet_glue);
    CORBA::Object_var fref = poa_->id_to_reference (fid.in());
    return fref._retn ();
}

/*
 * Facet management
 */

void 
CCM::SessionContainer::configuration_complete(PortableServer::Servant comp_glue)
{
    LDEBUGNL(CCM_CONTAINER, " CCM::SessionContainer::configuration_complete()");
    PortableServer::ObjectId_var oid = poa_->servant_to_id (comp_glue);
    //PerComponentData & data = active_components[oid.in()];
    //data.configuration_complete = 1;
}

/*
 * for Service Context
 */

Components::CCMHome_ptr 
CCM::SessionContainer::get_CCM_home ()
{
    LDEBUGNL(CCM_CONTAINER, " get_CCM_home()");
    return get_reference_for_home ();
}


CORBA::Object_ptr 
CCM::SessionContainer::get_CCM_object(LocalComponents::EnterpriseComponent* o)
{
    LDEBUGNL(CCM_CONTAINER, " get_CCM_object()");
    // TODO:
    return Components::CCMObject::_nil();
}



//============================================================================
// Valuetype implementations
//============================================================================

long CCM::Cookie_impl::globalId_ = 0;

CCM::Cookie_impl::Cookie_impl()
{
    LDEBUGNL(CCM_CONTAINER, " Cookie_impl()");
    id_ = ++globalId_;
}


/*
 * ----------------------------------------------------------------------
 * Valuetype factories
 * ----------------------------------------------------------------------
 */

CORBA::ValueBase*
CCM::Cookie_Factory::create_for_unmarshal()
{
    LDEBUGNL(CCM_CONTAINER, " create_for_unmarshal()");
    return new Cookie_impl;
}


void
CCM::register_all_factories(CORBA::ORB_ptr orb)
{
    LDEBUGNL(CCM_CONTAINER, " register_all_factories()");
    CORBA::ValueFactoryBase_var vf;
    vf = new Cookie_Factory;
    orb->register_value_factory ("IDL:omg.org/Components/Cookie:1.0", vf);
}

#endif // HAVE_MICO
