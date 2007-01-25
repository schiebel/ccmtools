
//==============================================================================
// Hello - business logic implementation
//==============================================================================

#include <cassert>
#include <iostream>
#include <WX/Utils/debug.h>

#include "Hello_impl.h"

using namespace WX::Utils;

namespace CCM_Local {
namespace WORLD {
namespace CCM_Session_Hello {


//==============================================================================
// business logic functionality
//==============================================================================

CCM_Console*
CCM_Hello_impl::get_console (  )
{
  DEBUGNL ( " CCM_Hello_impl->get_console (  )" );
  console_impl* facet = new console_impl ( this );
  return dynamic_cast<CCM_Console*> ( facet );
}



//==============================================================================
// console - facet implementation
//==============================================================================

console_impl::console_impl ( CCM_Hello_impl* c )
  : component(c)
{
  DEBUGNL ( "+console_impl->console_impl (  )" );
}

console_impl::~console_impl (  )
{
  DEBUGNL ( "-console_impl->~console_impl (  )" );
}

std::string
console_impl::read_string ( const std::string& prompt )
  throw (LocalComponents::CCMException) 
{
  DEBUGNL ( " console_impl->read_string ( prompt )" );

  return component->prompt (  ) + " Hello component world!";
}





//==============================================================================
// class implementation
//==============================================================================

CCM_Hello_impl::CCM_Hello_impl (  )
{
  DEBUGNL ( "+CCM_Hello_impl->CCM_Hello_impl (  )" );
}

CCM_Hello_impl::~CCM_Hello_impl (  )
{
  DEBUGNL ( "-CCM_Hello_impl->~CCM_Hello_impl (  )" );
}

std::string
CCM_Hello_impl::prompt (  )
  throw ( LocalComponents::CCMException)
{
  DEBUGNL ( " CCM_Hello_impl->prompt (  )" );
  return prompt_;
}

void
CCM_Hello_impl::prompt ( const std::string value )
  throw ( LocalComponents::CCMException)
{
  DEBUGNL ( " CCM_Hello_impl->prompt (  )" );
  prompt_ = value;
}



void
CCM_Hello_impl::set_session_context ( LocalComponents::SessionContext* context )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Hello_impl->set_session_context (  )" );
  ctx = (CCM_Hello_Context*) context;
}

void
CCM_Hello_impl::ccm_activate (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Hello_impl->ccm_activate (  )" );
}

void
CCM_Hello_impl::ccm_passivate (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Hello_impl->ccm_passivate (  )" );
}

void
CCM_Hello_impl::ccm_remove (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Hello_impl->ccm_remove (  )" );
}

} // /namespace CCM_Session_Hello
} // /namespace WORLD
} // /namespace CCM_Local



