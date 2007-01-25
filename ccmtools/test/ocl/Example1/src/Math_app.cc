/*
    $Id: Math_app.cc,v 1.2 2004/03/02 19:37:38 teiniker Exp $
*/

//==============================================================================
// Math - business logic implementation
//==============================================================================

#include <iostream>
#include <WX/Utils/debug.h>

#include "Math_app.h"

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;

namespace CCM_Local {
namespace CCM_Session_Math {


//==============================================================================
// business logic functionality
//==============================================================================

CCM_IntegerStack*
CCM_Math_impl::get_stack (  )
{
  DEBUGNL ( " CCM_Math_impl->get_stack (  )" );
  stack_impl* facet = new stack_impl(this);
  return dynamic_cast<CCM_IntegerStack*> ( facet );
}



//==============================================================================
// stack - facet implementation
//==============================================================================

stack_impl::stack_impl ( CCM_Math_impl* component_impl )
  : component ( component_impl ), maxSize_(123456)
{
  DEBUGNL ( "+stack_impl->stack_impl (  )" );
}

stack_impl::~stack_impl (  )
{
  DEBUGNL ( "-stack_impl->~stack_impl (  )" );
}

IntegerVector
stack_impl::field (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " stack_impl->field (  )" );

  return field_;
}

void
stack_impl::field ( const IntegerVector value )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " stack_impl->field ( value )" );

  field_ = value;
}

long
stack_impl::maxSize (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " stack_impl->maxSize (  )" );

  return maxSize_;
}

void
stack_impl::maxSize ( const long value )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " stack_impl->maxSize ( value )" );

  maxSize_ = value;
}


bool
stack_impl::isEmpty (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " stack_impl->isEmpty (  )" );

  return field_.empty();
}

bool
stack_impl::isFull (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " stack_impl->isFull (  )" );

  return field_.size()>=maxSize_;
}

void
stack_impl::push ( const long value )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " stack_impl->push ( value )" );

  field_.push_back(value);
}

long
stack_impl::pop (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " stack_impl->pop (  )" );

  long result = field_.back();
  field_.pop_back();
  return result;
}

long
stack_impl::top (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " stack_impl->top (  )" );

  return field_.back();
}





//==============================================================================
// class implementation
//==============================================================================

CCM_Math_impl::CCM_Math_impl (  )
{
  DEBUGNL ( "+CCM_Math_impl->CCM_Math_impl (  )" );
}

CCM_Math_impl::~CCM_Math_impl (  )
{
  DEBUGNL ( "-CCM_Math_impl->~CCM_Math_impl (  )" );
}




void
CCM_Math_impl::set_session_context ( LocalComponents::SessionContext* context )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Math_impl->set_session_context (  )" );
  ctx = dynamic_cast<CCM_Math_Context*> ( context );
}

void
CCM_Math_impl::ccm_activate (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Math_impl->ccm_activate (  )" );
}

void
CCM_Math_impl::ccm_passivate (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Math_impl->ccm_passivate (  )" );
}

void
CCM_Math_impl::ccm_remove (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Math_impl->ccm_remove (  )" );
}

} // /namespace CCM_Session_Math
} // /namespace CCM_Local



