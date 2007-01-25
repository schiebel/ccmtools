
/**
 * CCM_I2 facet class implementation. 
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

#include "Test_my_facet_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_Test {

my_facet_impl::my_facet_impl(CCM_Local::CCM_Session_Test::CCM_Test_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+my_facet_impl->my_facet_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

my_facet_impl::~my_facet_impl()
{
    DEBUGNL ( "-my_facet_impl->~my_facet_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

long
my_facet_impl::op1(const std::string& str)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("my_facet_impl->op1(str)");
    cout << ">>> " << str << endl;
    return str.length();
}

} // /namespace CCM_Session_Test
} // /namespace CCM_Local
