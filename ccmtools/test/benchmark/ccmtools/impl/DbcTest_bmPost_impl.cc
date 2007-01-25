
/**
 * CCM_DbcBenchmarkPost facet class implementation. 
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

#include "DbcTest_bmPost_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_DbcTest {

bmPost_impl::bmPost_impl(CCM_Local::CCM_Session_DbcTest::CCM_DbcTest_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+bmPost_impl->bmPost_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

bmPost_impl::~bmPost_impl()
{
    DEBUGNL ( "-bmPost_impl->~bmPost_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}


const long
bmPost_impl::f1_result() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bmPost_impl->f1_result()");
    return f1_result_;
}

void
bmPost_impl::f1_result(const long value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bmPost_impl->f1_result(value)");
    f1_result_ = value;
}

const std::string
bmPost_impl::f2_result() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bmPost_impl->f2_result()");
    return f2_result_;
}

void
bmPost_impl::f2_result(const std::string value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bmPost_impl->f2_result(value)");
    f2_result_ = value;
}

const LongList
bmPost_impl::f3_result() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bmPost_impl->f3_result()");
    return f3_result_;
}

void
bmPost_impl::f3_result(const LongList value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" bmPost_impl->f3_result(value)");
    f3_result_ = value;
}


long
bmPost_impl::f_ret1()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bmPost_impl->f_ret1()");
    return f1_result_;
}

std::string
bmPost_impl::f_ret2()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bmPost_impl->f_ret2()");
    return f2_result_;
}

LongList
bmPost_impl::f_ret3()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("bmPost_impl->f_ret3()");
    return f3_result_;
}

} // /namespace CCM_Session_DbcTest
} // /namespace CCM_Local
