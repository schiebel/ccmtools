
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

#include "Test_console_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_Test {

console_impl::console_impl(CCM_Local::CCM_Session_Test::CCM_Test_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+console_impl->console_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

console_impl::~console_impl()
{
    DEBUGNL ( "-console_impl->~console_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

const long
console_impl::max_size() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" console_impl->max_size()");
    return max_size_;
}

void
console_impl::max_size(const long value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" console_impl->max_size(value)");
    max_size_ = value;
}

long
console_impl::print(const std::string& msg)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("console_impl->print(msg)");
    
    cout << ">> " << msg << endl;
    if(msg.length() < max_size())
      return msg.length();
    else
      return max_size();
}

} // /namespace CCM_Session_Test
} // /namespace CCM_Local
