
/***
 * CarRental_mirror component business logic implementation.
 * 
 * // TODO: WRITE YOUR DESCRIPTION HERE! 
 *
 * @author
 * @version 
 *
 * This file structure was automatically generated by CCM Tools
 * <http://ccmtools.sourceforge.net/> and contains a component's
 * implementation classes. 
 ***/

#include <cassert>
#include <iostream>
#include <WX/Utils/debug.h>

#include "CarRental_mirror_impl.h"

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;

namespace CCM_Local {
namespace BigBusiness {
namespace CCM_Session_CarRental_mirror {

//==============================================================================
// CCM_CarRental_mirror - component implementation
//==============================================================================

CCM_CarRental_mirror_impl::CCM_CarRental_mirror_impl()
{
    DEBUGNL("+CCM_CarRental_mirror_impl->CCM_CarRental_mirror_impl()");
}

CCM_CarRental_mirror_impl::~CCM_CarRental_mirror_impl()
{
    DEBUGNL("-CCM_CarRental_mirror_impl->~CCM_CarRental_mirror_impl()");
}

void
CCM_CarRental_mirror_impl::set_session_context(
    LocalComponents::SessionContext* context)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" CCM_CarRental_mirror_impl->set_session_context()");
    ctx = dynamic_cast<CCM_CarRental_mirror_Context*>(context);
}

void
CCM_CarRental_mirror_impl::ccm_activate()
    throw(LocalComponents::CCMException)
{
  DEBUGNL(" CCM_CarRental_mirror_impl->ccm_activate()");
  
  try {
    {
      CCM_Local::BigBusiness::Customer person;
      person.id = 1;
      person.first_name = "Franz";
      person.last_name = "Kafka";
      person.mileage = 0.0;
      ctx->get_connection_maintenance_mirror()->createCustomer(person);
    }

    {
      CCM_Local::BigBusiness::Customer person;
      person.id = 2;
      person.first_name = "Thomas";
      person.last_name = "Bernhard";
      person.mileage = 0.0;
      ctx->get_connection_maintenance_mirror()->createCustomer(person);
    }

    {
      CCM_Local::BigBusiness::Customer person;
      person.id = 3;
      person.first_name = "Karl";
      person.last_name = "Kraus";
      person.mileage = 0.0;
      ctx->get_connection_maintenance_mirror()->createCustomer(person);
    }

    {
      CCM_Local::BigBusiness::Customer person;
      long id = 2;
      person = ctx->get_connection_maintenance_mirror()->retrieveCustomer(id);
      assert(person.id == 2);
      assert(person.first_name == "Thomas");
      assert(person.last_name == "Bernhard");
    }

    {
      CCM_Local::BigBusiness::CustomerList person_list;
      person_list = ctx->get_connection_maintenance_mirror()->retrieveAllCustomers();
      assert(person_list.at(2).id == 3);
      assert(person_list.at(2).first_name == "Karl");
      assert(person_list.at(2).last_name == "Kraus");

      assert(person_list.at(1).id == 2);
      assert(person_list.at(1).first_name == "Thomas");
      assert(person_list.at(1).last_name == "Bernhard");

      assert(person_list.at(0).id == 1);
      assert(person_list.at(0).first_name == "Franz");
      assert(person_list.at(0).last_name == "Kafka");
    }      

    {
      CCM_Local::BigBusiness::Customer person;
      person.id = 1;
      person.first_name = "Werner";
      person.last_name = "Schwab";
      person.mileage = 0.0;
      ctx->get_connection_maintenance_mirror()->updateCustomer(person);      

      CCM_Local::BigBusiness::Customer another_person;
      another_person = ctx->get_connection_maintenance_mirror()->retrieveCustomer(person.id);
      assert(another_person.id == 1);
      assert(another_person.first_name == "Werner");
      assert(another_person.last_name == "Schwab");
    }

    {
      long id = 1;
      ctx->get_connection_maintenance_mirror()->deleteCustomer(id);
      cout << "Customer removed" << endl;

      CCM_Local::BigBusiness::Customer person;
      person = ctx->get_connection_maintenance_mirror()->retrieveCustomer(id);
      assert(false); // Customer found => failer
    }
  }
  catch(CCM_Local::BigBusiness::CreateCustomerException) {
    cerr << "MAINTENANCE ERROR: Can't create customer!" << endl;
  }
  catch(CCM_Local::BigBusiness::NoCustomerException) {
    cerr << "MAINTENANCE ERROR: no customer found!" << endl;
  }


  try {
    {
      long id = 2;
      double miles = 7.7;
      ctx->get_connection_business_mirror()->addCustomerMiles(id, miles); 

      double other_miles;
      other_miles = ctx->get_connection_business_mirror()->getCustomerMiles(id); 
      cout << other_miles << endl;
      cout << miles << endl;
      assert( abs(other_miles - miles) < 0.001);
    }

    {
      long id = 2;
      double dollars;
      double miles = 1.1;
      double other_miles;
      double factor = 5.3;
      ctx->get_connection_business_mirror()->addCustomerMiles(id, miles); 
      ctx->get_connection_business_mirror()->dollars_per_mile(factor);
      other_miles = ctx->get_connection_business_mirror()->getCustomerMiles(id);
      dollars = ctx->get_connection_business_mirror()->getCustomerDollars(id); 
      assert( abs(dollars - other_miles*factor) < 0.001);
    }

    {
      long id = 2;
      double dollars;
      ctx->get_connection_business_mirror()->resetCustomerMiles(id);
      dollars = ctx->get_connection_business_mirror()->getCustomerDollars(id); 
      assert( abs(dollars) < 0.001);
    }
  }
  catch(CCM_Local::BigBusiness::NoCustomerException) {
    cerr << "MAINTENANCE ERROR: no customer found!" << endl;
    assert(false);
  }
}

void
CCM_CarRental_mirror_impl::ccm_passivate()
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" CCM_CarRental_mirror_impl->ccm_passivate()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

void
CCM_CarRental_mirror_impl::ccm_remove()
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" CCM_CarRental_mirror_impl->ccm_remove()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

} // /namespace CCM_Session_CarRental_mirror
} // /namespace BigBusiness
} // /namespace CCM_Local

