
/**
 * CCM_DbcBenchmarkInv1 facet class implementation. 
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

#include "DbcTest_bmInv1_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_DbcTest {

bmInv1_impl::bmInv1_impl(CCM_Local::CCM_Session_DbcTest::CCM_DbcTest_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+bmInv1_impl->bmInv1_impl()");

    // inv i1: self.longAttr >= 0 and longAttr < 1000000
    longAttr_ = 0;          
}

bmInv1_impl::~bmInv1_impl()
{
    DEBUGNL ( "-bmInv1_impl->~bmInv1_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

const long
bmInv1_impl::longAttr() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bmInv1_impl->longAttr()");
    return longAttr_;
}

void
bmInv1_impl::longAttr(const long value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bmInv1_impl->longAttr(value)");
    longAttr_ = value;
}

void
bmInv1_impl::f0()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bmInv1_impl->f0()");

    // TODO : IMPLEMENT ME HERE !
}

} // /namespace CCM_Session_DbcTest
} // /namespace CCM_Local
