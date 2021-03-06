
/***
 * Test_mirror component business logic implementation.
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

#include "Test_mirror_impl.h"

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;

namespace CCM_Local {
namespace CCM_Session_Test_mirror {

//==============================================================================
// CCM_Test_mirror - component implementation
//==============================================================================

CCM_Test_mirror_impl::CCM_Test_mirror_impl (  )
{
  DEBUGNL ( "+CCM_Test_mirror_impl->CCM_Test_mirror_impl (  )" );
}

CCM_Test_mirror_impl::~CCM_Test_mirror_impl (  )
{
  DEBUGNL ( "-CCM_Test_mirror_impl->~CCM_Test_mirror_impl (  )" );
}

void
CCM_Test_mirror_impl::set_session_context ( LocalComponents::SessionContext* context )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Test_mirror_impl->set_session_context (  )" );
  ctx = dynamic_cast<CCM_Test_mirror_Context*> ( context );
}

void
CCM_Test_mirror_impl::ccm_activate (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Test_mirror_impl->ccm_activate (  )" );
}

void
CCM_Test_mirror_impl::ccm_passivate (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Test_mirror_impl->ccm_passivate (  )" );
}

void
CCM_Test_mirror_impl::ccm_remove (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Test_mirror_impl->ccm_remove (  )" );
}

//==============================================================================
// CCM_InterfaceType facet implementation
//==============================================================================

CCM_InterfaceType*
CCM_Test_mirror_impl::get_a_receptacle_mirror (  )
{
  DEBUGNL ( " CCM_Test_mirror_impl->get_a_receptacle_mirror (  )" );
  a_receptacle_mirror_impl* facet = new a_receptacle_mirror_impl(this);
  return dynamic_cast<CCM_InterfaceType*> ( facet );
}

a_receptacle_mirror_impl::a_receptacle_mirror_impl ( CCM_Test_mirror_impl* component_impl )
  : component ( component_impl )
{
  DEBUGNL ( "+a_receptacle_mirror_impl->a_receptacle_mirror_impl (  )" );
}

a_receptacle_mirror_impl::~a_receptacle_mirror_impl (  )
{
  DEBUGNL ( "-a_receptacle_mirror_impl->~a_receptacle_mirror_impl (  )" );
}

long
a_receptacle_mirror_impl::op3 ( const std::string& str )
  throw (LocalComponents::CCMException)
{
  DEBUGNL ( " a_receptacle_mirror_impl->op3 ( str )" );
  cout << str << endl;
  return str.length();
}

long
a_receptacle_mirror_impl::op2 ( const std::string& str )
  throw (LocalComponents::CCMException)
{
  DEBUGNL ( " a_receptacle_mirror_impl->op2 ( str )" );
  cout << str << endl;
  return str.length();
}

long
a_receptacle_mirror_impl::op1 ( const std::string& str )
  throw (LocalComponents::CCMException)
{
  DEBUGNL ( " a_receptacle_mirror_impl->op1 ( str )" );
  cout << str << endl;
  return str.length();
}

} // /namespace CCM_Session_Test_mirror
} // /namespace CCM_Local

