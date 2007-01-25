
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

#include "SuperTest_bm_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_SuperTest {

bm_impl::bm_impl(CCM_Local::CCM_Session_SuperTest::CCM_SuperTest_impl* component_impl)
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
    return component->ctx->get_connection_delegate()->long_attr();
}

void
bm_impl::long_attr(const long value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bm_impl->long_attr(value)");
    component->ctx->get_connection_delegate()->long_attr(value);
}

const std::string
bm_impl::string_attr() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bm_impl->string_attr()");
    return component->ctx->get_connection_delegate()->string_attr();
}

void
bm_impl::string_attr(const std::string value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bm_impl->string_attr(value)");
    component->ctx->get_connection_delegate()->string_attr(value);
}

const LongList
bm_impl::LongList_attr() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bm_impl->LongList_attr()");
    return component->ctx->get_connection_delegate()->LongList_attr();
}

void
bm_impl::LongList_attr(const LongList value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bm_impl->LongList_attr(value)");
    component->ctx->get_connection_delegate()->LongList_attr(value);
}



void
bm_impl::f0()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f0()");
    component->ctx->get_connection_delegate()->f0();
}

void
bm_impl::f_in1(const long l1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_in1(l1)");
    component->ctx->get_connection_delegate()->f_in1(l1);
}

void
bm_impl::f_in2(const std::string& s1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_in2(s1)");
    component->ctx->get_connection_delegate()->f_in2(s1);
}

void
bm_impl::f_in3(const LongList& ll1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_in3(ll1)");
    component->ctx->get_connection_delegate()->f_in3(ll1);
}


void
bm_impl::f_inout1(long& l1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_inout1(l1)");
    component->ctx->get_connection_delegate()->f_inout1(l1);
}

void
bm_impl::f_inout2(std::string& s1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_inout2(s1)");
    component->ctx->get_connection_delegate()->f_inout2(s1);
}

void
bm_impl::f_inout3(LongList& ll1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_inout3(ll1)");
    component->ctx->get_connection_delegate()->f_inout3(ll1);
}


void
bm_impl::f_out1(long& l1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_out1(l1)");
    component->ctx->get_connection_delegate()->f_out1(l1);
}

void
bm_impl::f_out2(std::string& s1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_out2(s1)");
    component->ctx->get_connection_delegate()->f_out2(s1);
}

void
bm_impl::f_out3(LongList& ll1)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_out3(ll1)");
    component->ctx->get_connection_delegate()->f_out3(ll1);
}


long
bm_impl::f_ret1()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_ret1()");
    return component->ctx->get_connection_delegate()->f_ret1();
}

std::string
bm_impl::f_ret2()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_ret2()");
    return component->ctx->get_connection_delegate()->f_ret2();
}

LongList
bm_impl::f_ret3()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bm_impl->f_ret3()");
    return component->ctx->get_connection_delegate()->f_ret3();
}

} // /namespace CCM_Session_SuperTest
} // /namespace CCM_Local
