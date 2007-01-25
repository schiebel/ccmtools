
/**
 * CCM_Console facet class implementation. 
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

#include "Test_mirror_console_mirror_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_Test_mirror {

console_mirror_impl::console_mirror_impl(CCM_Local::CCM_Session_Test_mirror::CCM_Test_mirror_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+console_mirror_impl->console_mirror_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

console_mirror_impl::~console_mirror_impl()
{
    DEBUGNL ( "-console_mirror_impl->~console_mirror_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

long
console_mirror_impl::println(const std::string& s2)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("console_mirror_impl->println(s2)");
    cout << s2 << endl;
    return s2.length();
}

} // /namespace CCM_Session_Test
} // /namespace CCM_Local
