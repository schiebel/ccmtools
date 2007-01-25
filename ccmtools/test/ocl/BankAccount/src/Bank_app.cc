
//==============================================================================
// Bank - business logic implementation
//==============================================================================

#include <iostream>
#include <WX/Utils/debug.h>

#include "Bank_app.h"

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;

namespace CCM_Local {
namespace CCM_Session_Bank {


//==============================================================================
// business logic functionality
//==============================================================================

CCM_BankAccount*
CCM_Bank_impl::get_account (  )
{
  DEBUGNL ( " CCM_Bank_impl->get_account (  )" );
  account_impl* facet = new account_impl(this);
  return dynamic_cast<CCM_BankAccount*> ( facet );
}



//==============================================================================
// account - facet implementation
//==============================================================================

account_impl::account_impl ( CCM_Bank_impl* component_impl )
  : component ( component_impl ), balance_(0), overdraftLimit_(0)
{
  DEBUGNL ( "+account_impl->account_impl (  )" );
}

account_impl::~account_impl (  )
{
  DEBUGNL ( "-account_impl->~account_impl (  )" );
}


void
account_impl::deposit ( const long amount )
  throw (LocalComponents::CCMException)
{
  DEBUGNL ( " account_impl->deposit ( amount )" );

  balance_ += amount;
}

void
account_impl::withdraw ( const long amount )
  throw (LocalComponents::CCMException)
{
  DEBUGNL ( " account_impl->withdraw ( amount )" );

  balance_ -= amount;
}

long
account_impl::balance (  )
  throw (LocalComponents::CCMException)
{
  DEBUGNL ( " account_impl->balance (  )" );

  return balance_;
}

long
account_impl::overdraftLimit (  )
  throw (LocalComponents::CCMException)
{
  DEBUGNL ( " account_impl->overdraftLimit (  )" );

  return overdraftLimit_;
}

void
account_impl::setOverdraftLimit ( const long newLimit )
  throw (LocalComponents::CCMException)
{
  DEBUGNL ( " account_impl->setOverdraftLimit ( newLimit )" );

  overdraftLimit_ = newLimit;
}





//==============================================================================
// class implementation
//==============================================================================

CCM_Bank_impl::CCM_Bank_impl (  )
{
  DEBUGNL ( "+CCM_Bank_impl->CCM_Bank_impl (  )" );
}

CCM_Bank_impl::~CCM_Bank_impl (  )
{
  DEBUGNL ( "-CCM_Bank_impl->~CCM_Bank_impl (  )" );
}




void
CCM_Bank_impl::set_session_context ( LocalComponents::SessionContext* context )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Bank_impl->set_session_context (  )" );
  ctx = dynamic_cast<CCM_Bank_Context*> ( context );
}

void
CCM_Bank_impl::ccm_activate (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Bank_impl->ccm_activate (  )" );
}

void
CCM_Bank_impl::ccm_passivate (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Bank_impl->ccm_passivate (  )" );
}

void
CCM_Bank_impl::ccm_remove (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Bank_impl->ccm_remove (  )" );
}

} // /namespace CCM_Session_Bank
} // /namespace CCM_Local



