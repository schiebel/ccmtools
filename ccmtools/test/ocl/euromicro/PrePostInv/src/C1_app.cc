
//==============================================================================
// C1 - business logic implementation
//==============================================================================

#include <iostream>
#include <WX/Utils/debug.h>

#include "C1_app.h"

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;

namespace CCM_Local {
namespace CCM_Session_C1 {


//==============================================================================
// business logic functionality
//==============================================================================

CCM_BasicTypes*
CCM_C1_impl::get_i1 (  )
{
  DEBUGNL ( " CCM_C1_impl->get_i1 (  )" );
  i1_impl* facet = new i1_impl(this);
  return dynamic_cast<CCM_BasicTypes*> ( facet );
}



//==============================================================================
// i1 - facet implementation
//==============================================================================

i1_impl::i1_impl ( CCM_C1_impl* component_impl )
  : component ( component_impl ), _a1(1), _a2(2.3), _a3("Hello World!")
{
  DEBUGNL ( "+i1_impl->i1_impl (  )" );
}

i1_impl::~i1_impl (  )
{
  DEBUGNL ( "-i1_impl->~i1_impl (  )" );
}

long
i1_impl::a1 (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " i1_impl->a1 (  )" );
  return _a1;
}

void
i1_impl::a1 ( const long value )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " i1_impl->a1 ( value )" );
  _a1 = value;
}

double
i1_impl::a2 (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " i1_impl->a2 (  )" );
  return _a2;
}

void
i1_impl::a2 ( const double value )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " i1_impl->a2 ( value )" );
  _a2 = value;
}

std::string
i1_impl::a3 (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " i1_impl->a3 (  )" );
  return _a3;
}

void
i1_impl::a3 ( const std::string value )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " i1_impl->a3 ( value )" );
  _a3 = value;
}


long
i1_impl::f1_2 ( const long p1, const long p2 )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " i1_impl->f1_2 ( p1, p2 )" );
  return p1+p2;
}

long
i1_impl::f1_10 ( const long p1, const long p2, const long p3, const long p4, const long p5, const long p6, const long p7, const long p8, const long p9, const long p10 )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " i1_impl->f1_10 ( p1, p2, p3, p4, p5, p6, p7, p8, p9, p10 )" );
  return p1+p2+p3+p4+p5+p6+p7+p8+p9+p10;
}

double
i1_impl::f2_2 ( const double p1, const double p2 )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " i1_impl->f2_2 ( p1, p2 )" );
  return p1+p2;
}

double
i1_impl::f2_10 ( const double p1, const double p2, const double p3, const double p4, const double p5, const double p6, const double p7, const double p8, const double p9, const double p10 )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " i1_impl->f2_10 ( p1, p2, p3, p4, p5, p6, p7, p8, p9, p10 )" );
  return p1+p2+p3+p4+p5+p6+p7+p8+p9+p10;
}

std::string
i1_impl::f3_2 ( const std::string& p1, const std::string& p2 )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " i1_impl->f3_2 ( p1, p2 )" );
  return p1+p2;
}

std::string
i1_impl::f3_10 ( const std::string& p1, const std::string& p2, const std::string& p3, const std::string& p4, const std::string& p5, const std::string& p6, const std::string& p7, const std::string& p8, const std::string& p9, const std::string& p10 )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " i1_impl->f3_10 ( p1, p2, p3, p4, p5, p6, p7, p8, p9, p10 )" );
  return p1+p2+p3+p4+p5+p6+p7+p8+p9+p10;
}





//==============================================================================
// class implementation
//==============================================================================

CCM_C1_impl::CCM_C1_impl (  )
{
  DEBUGNL ( "+CCM_C1_impl->CCM_C1_impl (  )" );
}

CCM_C1_impl::~CCM_C1_impl (  )
{
  DEBUGNL ( "-CCM_C1_impl->~CCM_C1_impl (  )" );
}




void
CCM_C1_impl::set_session_context ( LocalComponents::SessionContext* context )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_C1_impl->set_session_context (  )" );
  ctx = dynamic_cast<CCM_C1_Context*> ( context );
}

void
CCM_C1_impl::ccm_activate (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_C1_impl->ccm_activate (  )" );
}

void
CCM_C1_impl::ccm_passivate (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_C1_impl->ccm_passivate (  )" );
}

void
CCM_C1_impl::ccm_remove (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_C1_impl->ccm_remove (  )" );
}

} // /namespace CCM_Session_C1
} // /namespace CCM_Local



