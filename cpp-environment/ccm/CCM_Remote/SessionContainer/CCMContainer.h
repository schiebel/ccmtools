/* -*- mode: C++; c-basic-offset: 4 -*-
 *
 * CCM Tools : C++ Code Generator 
 * Egon Teiniker <egon.teiniker@tugraz.at>
 * copyright (c) 2002, 2003 Salomon Automation
 *
 * $Id: CCMContainer.h,v 1.2 2005/03/08 08:03:05 teiniker Exp $
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

#ifndef __CCM_CONTAINER_H__
#define __CCM_CONTAINER_H__

#ifdef HAVE_CONFIG_H
#  include <config.h>
#endif 

#ifdef HAVE_MICO 

#include <CORBA.h>
#include <map>
#include <string>

#include <WX/Utils/smartptr.h>
#include <LocalComponents/CCM.h>

#include <Components/CCM.h>

namespace CCM {
  
  //============================================================================
  // Base for all Containers
  //============================================================================
  
    class ContainerBase
	: virtual public WX::Utils::RefCounted
    {
    protected:
	static CORBA::ULong globalContainerNumber_;
	CORBA::ULong containerNumber_;
	
    public:
	ContainerBase ();
	virtual ~ContainerBase();
	virtual void activate() = 0;
	virtual void passivate() = 0;
	virtual void remove() = 0;
	
	virtual CORBA::Boolean compare(Components::CCMHome_ptr) = 0;
	
	virtual Components::CCMHome_ptr get_CCM_home () = 0;
	virtual CORBA::Object_ptr
	get_CCM_object(LocalComponents::EnterpriseComponent*) = 0;
    };
    


    //==========================================================================
    // Session Container
    //==========================================================================
    
    class SessionContainer 
	: virtual public ContainerBase 
    {
    private:
	CORBA::ORB_var orb_;
	PortableServer::POA_var poa_;
	
    public:
	struct ComponentInfo {
	    std::string home_short_name;
	    std::string home_absolute_name;
	    std::string home_id;
	    std::string component_short_name;
	    std::string component_absolute_name;
	    std::string component_id;
	    LocalComponents::HomeExecutorBase* home_instance;
	    PortableServer::ServantBase_var home_glue;
	};
	
    private:
	ComponentInfo _info;
	bool _have_info;
	
	struct PerComponentData {
	    CORBA::Boolean configuration_complete;
	    PortableServer::ServantBase_var glue;
	    LocalComponents::EnterpriseComponent* instance;
	    CORBA::Object_var reference;
	    std::map<std::string, PortableServer::ServantBase_var> facet_glue;
	    std::map<std::string, void*> facet_instance;
	    std::map<std::string, CORBA::Object_var> facet_reference;
	};
      
	CORBA::Object_var _home_ref;
	typedef std::map<std::string, PortableServer::ObjectId> InstanceMap;
	InstanceMap active_components;
	
    public:
	SessionContainer(CORBA::ORB_ptr orb);
	~SessionContainer();
	
	void load(const ComponentInfo & info); 
	void activate();  
	void passivate(); 
	void remove();    
	
	virtual CORBA::Boolean compare (Components::CCMHome_ptr); 
	
	
	// Session Container API
	Components::CCMHome_ptr get_reference_for_home (); 
	
	Components::CCMObject_ptr
	activate_component(PortableServer::Servant skel);
	
	Components::CCMObject_ptr
	get_reference_for_component(PortableServer::Servant skel); 
	
	PortableServer::Servant
	get_skeleton_for_reference(CORBA::Object_ptr ref); 
	
	void deactivate_component(CORBA::Object_ptr ref); 
	void deactivate_component(PortableServer::Servant skel);
	
	// for SessionContext
	Components::CCMHome_ptr get_CCM_home (); 
	
	CORBA::Object_ptr 
	get_CCM_object(LocalComponents::EnterpriseComponent*); 
	
	// Facet management
	CORBA::Object_ptr
	activate_facet(PortableServer::Servant comp_glue,  
		       const char * name,
		       void* facet_instance,
		       PortableServer::Servant facet_glue);
	
	// notify container of configuration_complete
	void configuration_complete (PortableServer::Servant comp_glue); 
    };
  
  

  //==========================================================================
  // Valuetype implementations
  //==========================================================================
  
  class Cookie_impl 
    : virtual public OBV_Components::Cookie,
      virtual public CORBA::DefaultValueRefCountBase
    {
    private:
      static long globalId_;
      long id_;
    public:
      Cookie_impl();
  };
  
  // Valuetype factories
  class Cookie_Factory 
    : virtual public CORBA::ValueFactoryBase
    {
    public:
      CORBA::ValueBase* create_for_unmarshal ();
    };
  
  void register_all_factories (CORBA::ORB_ptr);
} // /namespace CCM

#endif // HAVE_MICO
#endif // __CCM_CONTAINER_H__




