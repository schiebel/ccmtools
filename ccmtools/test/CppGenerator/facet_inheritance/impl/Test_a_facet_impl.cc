
/**
 * CCM_InterfaceType facet class implementation. 
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

#include "Test_a_facet_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_Test {

a_facet_impl::a_facet_impl(CCM_Local::CCM_Session_Test::CCM_Test_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+a_facet_impl->a_facet_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

a_facet_impl::~a_facet_impl()
{
    DEBUGNL ( "-a_facet_impl->~a_facet_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

long
a_facet_impl::op3(const std::string& str)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("a_facet_impl->op3(str)");
    cout << str << endl;
    return str.length();
}

long
a_facet_impl::op2(const std::string& str)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("a_facet_impl->op2(str)");
    cout << str << endl;
    return str.length();
}

long
a_facet_impl::op1(const std::string& str)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("a_facet_impl->op1(str)");
    cout << str << endl;
    return str.length();
}

} // /namespace CCM_Session_Test
} // /namespace CCM_Local
