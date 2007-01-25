
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

#include "Test_in_port_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace world {
namespace europe {
namespace austria {
namespace CCM_Session_Test {

in_port_impl::in_port_impl(CCM_Local::world::europe::austria::CCM_Session_Test::CCM_Test_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+in_port_impl->in_port_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

in_port_impl::~in_port_impl()
{
    DEBUGNL ( "-in_port_impl->~in_port_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

long
in_port_impl::op1(const std::string& str)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("in_port_impl->op1(str)");

    cout << str << endl;
    return str.length();
}

} // /namespace CCM_Session_Test
} // /namespace austria
} // /namespace europe
} // /namespace world
} // /namespace CCM_Local
