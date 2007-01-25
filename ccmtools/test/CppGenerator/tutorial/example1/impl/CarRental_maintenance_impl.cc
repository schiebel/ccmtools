
/**
 * CCM_CustomerMaintenance facet class implementation. 
 *
 * // TODO: WRITE YOUR DESCRIPTION HERE! 
 *
 * @author
 * @version 
 *
 * This class implements a facet's methods and attributes.
 **/

#include <cassert>
#include <iostream>
#include <WX/Utils/debug.h>

#include "CarRental_maintenance_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace BigBusiness {
namespace CCM_Session_CarRental {

maintenance_impl::maintenance_impl(CCM_Local::BigBusiness::CCM_Session_CarRental::CCM_CarRental_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+maintenance_impl->maintenance_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

maintenance_impl::~maintenance_impl()
{
    DEBUGNL ( "-maintenance_impl->~maintenance_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

void
maintenance_impl::createCustomer(const BigBusiness::Customer& person)
    throw (LocalComponents::CCMException, CreateCustomerException )
{
    DEBUGNL("maintenance_impl->createCustomer(person)");
    component->CustomerDB.push_back(person);
}

BigBusiness::Customer
maintenance_impl::retrieveCustomer(const long id)
    throw (LocalComponents::CCMException, NoCustomerException )
{
    DEBUGNL("maintenance_impl->retrieveCustomer(id)");
    std::vector<CCM_Local::BigBusiness::Customer>::iterator pos;
    for(pos = component->CustomerDB.begin(); pos != component->CustomerDB.end(); ++pos) {
      if(pos->id == id) {
	return *pos;
      }
    }
    throw NoCustomerException();
}

BigBusiness::CustomerList
maintenance_impl::retrieveAllCustomers()
    throw (LocalComponents::CCMException, NoCustomerException )
{
    DEBUGNL("maintenance_impl->retrieveAllCustomers()");
    if(component->CustomerDB.size() == 0)
      throw NoCustomerException();
    
    CCM_Local::BigBusiness::CustomerList customer_list;
    std::vector<CCM_Local::BigBusiness::Customer>::iterator pos;
    for(pos = component->CustomerDB.begin(); pos != component->CustomerDB.end(); ++pos) {
      customer_list.push_back(*pos);
    }
    return customer_list;
}

void
maintenance_impl::updateCustomer(const BigBusiness::Customer& person)
    throw (LocalComponents::CCMException, NoCustomerException )
{
    DEBUGNL("maintenance_impl->updateCustomer(person)");
    std::vector<CCM_Local::BigBusiness::Customer>::iterator pos;
    for(pos = component->CustomerDB.begin(); pos != component->CustomerDB.end(); ++pos) {
      if(pos->id == person.id) {
	*pos = person;
	return;
      }
    }
    throw NoCustomerException();  
}

void
maintenance_impl::deleteCustomer(const long id)
    throw (LocalComponents::CCMException, RemoveCustomerException )
{
    DEBUGNL("maintenance_impl->deleteCustomer(id)");
    std::vector<CCM_Local::BigBusiness::Customer>::iterator pos;
    for(pos = component->CustomerDB.begin(); pos != component->CustomerDB.end(); ++pos) {
      if(pos->id == id) {
	component->CustomerDB.erase(pos);
	return;
      }
    }
    throw RemoveCustomerException();  
}

} // /namespace CCM_Session_CarRental
} // /namespace BigBusiness
} // /namespace CCM_Local
