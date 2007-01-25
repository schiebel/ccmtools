
/***
 * Test component business logic implementation.
 * 
 * // TODO: WRITE YOUR DESCRIPTION HERE! 
 *
 * @author
 * @version 
 *
 * This file structure was automatically generated by CCM Tools
 * <http://ccmtools.sourceforge.net/> and contains a component's
 * implementation classes. 
 ***/

#include <cassert>
#include <iostream>
#include <WX/Utils/debug.h>

#include "Test_impl.h"

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;

namespace CCM_Local {
namespace world {
namespace europe {
namespace austria {
namespace CCM_Session_Test {

//==============================================================================
// CCM_Test - component implementation
//==============================================================================

CCM_Test_impl::CCM_Test_impl()
{
    DEBUGNL("+CCM_Test_impl->CCM_Test_impl()");
}

CCM_Test_impl::~CCM_Test_impl()
{
    DEBUGNL("-CCM_Test_impl->~CCM_Test_impl()");
}

long
CCM_Test_impl::op1(const std::string& str)
    throw (LocalComponents::CCMException)
{
    DEBUGNL(" CCM_Test_impl->op1(str)");

    cout << str << endl;
    return str.length();
}

void
CCM_Test_impl::set_session_context(
    LocalComponents::SessionContext* context)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" CCM_Test_impl->set_session_context()");
    ctx = dynamic_cast<CCM_Test_Context*>(context);
}

void
CCM_Test_impl::ccm_activate()
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" CCM_Test_impl->ccm_activate()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

void
CCM_Test_impl::ccm_passivate()
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" CCM_Test_impl->ccm_passivate()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

void
CCM_Test_impl::ccm_remove()
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" CCM_Test_impl->ccm_remove()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

} // /namespace CCM_Session_Test
} // /namespace austria
} // /namespace europe
} // /namespace world
} // /namespace CCM_Local

