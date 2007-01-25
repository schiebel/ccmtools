
/**
 * CCM_CustomerBusiness facet class implementation. 
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

#include "CarRental_business_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace BigBusiness {
namespace CCM_Session_CarRental {

business_impl::business_impl(CCM_Local::BigBusiness::CCM_Session_CarRental::CCM_CarRental_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+business_impl->business_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

business_impl::~business_impl()
{
    DEBUGNL ( "-business_impl->~business_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

const double
business_impl::dollars_per_mile() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" business_impl->dollars_per_mile()");
    return dollars_per_mile_;
}

void
business_impl::dollars_per_mile(const double value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" business_impl->dollars_per_mile(value)");
    dollars_per_mile_ = value;
}

void
business_impl::addCustomerMiles(const long id, const double miles)
    throw (LocalComponents::CCMException, NoCustomerException )
{
    DEBUGNL("business_impl->addCustomerMiles(id, miles)");
    std::vector<CCM_Local::BigBusiness::Customer>::iterator pos;
    for(pos = component->CustomerDB.begin(); pos != component->CustomerDB.end(); ++pos) {
      if(pos->id == id) {
	pos->mileage += miles;
	return;
      }
    }
    throw NoCustomerException();    
}

void
business_impl::resetCustomerMiles(const long id)
    throw (LocalComponents::CCMException, NoCustomerException )
{
    DEBUGNL("business_impl->resetCustomerMiles(id)");
    std::vector<CCM_Local::BigBusiness::Customer>::iterator pos;
    for(pos = component->CustomerDB.begin(); pos != component->CustomerDB.end(); ++pos) {
      if(pos->id == id) {
	pos->mileage = 0.0;
	return;
      }
    }
    throw NoCustomerException(); 
}

double
business_impl::getCustomerMiles(const long id)
    throw (LocalComponents::CCMException, NoCustomerException )
{
    DEBUGNL("business_impl->getCustomerMiles(id)");
    std::vector<CCM_Local::BigBusiness::Customer>::iterator pos;
    for(pos = component->CustomerDB.begin(); pos != component->CustomerDB.end(); ++pos) {
      if(pos->id == id) {
	return pos->mileage;
      }
    }
    throw NoCustomerException(); 
}

double
business_impl::getCustomerDollars(const long id)
    throw (LocalComponents::CCMException, NoCustomerException )
{
    DEBUGNL("business_impl->getCustomerDollars(id)");
    return getCustomerMiles(id) * dollars_per_mile_;
}

} // /namespace CCM_Session_CarRental
} // /namespace BigBusiness
} // /namespace CCM_Local
