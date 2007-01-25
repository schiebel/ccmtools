
/**
 * CCM_Benchmark facet class implementation. 
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

#include "Test_bm_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_Test {

bm_impl::bm_impl(CCM_Local::CCM_Session_Test::CCM_Test_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+bm_impl->bm_impl()");
}

bm_impl::~bm_impl()
{
    DEBUGNL ( "-bm_impl->~bm_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}



const long
bm_impl::long_attr() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bm_impl->long_attr()");
    return long_attr_;
}

void
bm_impl::long_attr(const long value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bm_impl->long_attr(value)");
    long_attr_ = value;
}

const std::string
bm_impl::string_attr() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bm_impl->string_attr()");
    return string_attr_;
}

void
bm_impl::string_attr(const std::string value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bm_impl->string_attr(value)");
    string_attr_ = value;
}

const LongList
bm_impl::LongList_attr() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bm_impl->LongList_attr()");
    return LongList_attr_;
}

void
bm_impl::LongList_attr(const LongList value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bm_impl->LongList_attr(value)");
    LongList_attr_ = value;
}



void
bm_impl::f0()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f0()");

    // TODO : IMPLEMENT ME HERE !
}

void
bm_impl::f_in1(const long l1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_in1(l1)");

    // TODO : IMPLEMENT ME HERE !
}

void
bm_impl::f_in2(const std::string& s1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_in2(s1)");

    // TODO : IMPLEMENT ME HERE !
}

void
bm_impl::f_in3(const LongList& ll1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_in3(ll1)");

    // TODO : IMPLEMENT ME HERE !
}

void
bm_impl::f_inout1(long& l1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_inout1(l1)");
    l1 = long_attr_;
}

void
bm_impl::f_inout2(std::string& s1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_inout2(s1)");
    s1 = string_attr_;
}

void
bm_impl::f_inout3(LongList& ll1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_inout3(ll1)");
    ll1 = LongList_attr_;
}

void
bm_impl::f_out1(long& l1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_out1(l1)");
    l1 = long_attr_;
}

void
bm_impl::f_out2(std::string& s1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_out2(s1)");
    s1 = string_attr_;
}

void
bm_impl::f_out3(LongList& ll1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_out3(ll1)");
    ll1 = LongList_attr_;
}

long
bm_impl::f_ret1()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_ret1()");

    return long_attr_;
}

std::string
bm_impl::f_ret2()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_ret2()");

    return string_attr_;
}

LongList
bm_impl::f_ret3()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_ret3()");

    return LongList_attr_;
}

} // /namespace CCM_Session_Test
} // /namespace CCM_Local
