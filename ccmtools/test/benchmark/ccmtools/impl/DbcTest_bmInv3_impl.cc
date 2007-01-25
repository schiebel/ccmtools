
/**
 * CCM_DbcBenchmarkInv3 facet class implementation. 
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

#include "DbcTest_bmInv3_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_DbcTest {

bmInv3_impl::bmInv3_impl(CCM_Local::CCM_Session_DbcTest::CCM_DbcTest_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+bmInv3_impl->bmInv3_impl()");

    for(long i=0; i<10; i++)
      seqAttr_.push_back(i);
}

bmInv3_impl::~bmInv3_impl()
{
    DEBUGNL ( "-bmInv3_impl->~bmInv3_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

const LongList
bmInv3_impl::seqAttr() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bmInv3_impl->seqAttr()");
    return seqAttr_;
}

void
bmInv3_impl::seqAttr(const LongList value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bmInv3_impl->seqAttr(value)");
    seqAttr_ = value;
}

void
bmInv3_impl::f0()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bmInv3_impl->f0()");

    // TODO : IMPLEMENT ME HERE !
}

} // /namespace CCM_Session_DbcTest
} // /namespace CCM_Local
