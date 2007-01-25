
/**
 * CCM_DbcBenchmarkInv2 facet class implementation. 
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

#include "DbcTest_bmInv2_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_DbcTest {

bmInv2_impl::bmInv2_impl(CCM_Local::CCM_Session_DbcTest::CCM_DbcTest_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+bmInv2_impl->bmInv2_impl()");

    // inv i2: self.stringAttr.size >= 0 and stringAttr.size < 1000000
    stringAttr_ = "";   
}

bmInv2_impl::~bmInv2_impl()
{
    DEBUGNL ( "-bmInv2_impl->~bmInv2_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

const std::string
bmInv2_impl::stringAttr() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bmInv2_impl->stringAttr()");
    return stringAttr_;
}

void
bmInv2_impl::stringAttr(const std::string value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bmInv2_impl->stringAttr(value)");
    stringAttr_ = value;
}

void
bmInv2_impl::f0()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bmInv2_impl->f0()");

    // TODO : IMPLEMENT ME HERE !
}

} // /namespace CCM_Session_DbcTest
} // /namespace CCM_Local
