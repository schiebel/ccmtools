
//==============================================================================
// HelloHome - home business logic implementation
//==============================================================================

#include <cassert>
#include <iostream>
#include <WX/Utils/debug.h>

#include "Hello_impl.h"
#include "HelloHome_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_Hello {


//==============================================================================
// implementation of component home
//==============================================================================

CCM_HelloHome_impl::CCM_HelloHome_impl (  )
{
  DEBUGNL ( "+CCM_HelloHome_impl->CCM_HelloHome_impl (  )" );
}

CCM_HelloHome_impl::~CCM_HelloHome_impl (  )
{
  DEBUGNL ( "-CCM_HelloHome_impl->~CCM_HelloHome_impl (  )" );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_HelloHome_impl->create (  )" );
  return dynamic_cast<LocalComponents::EnterpriseComponent*>(new CCM_Hello_impl (  ));
}

/* basic type factories. */

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_short ( const short id )
  throw ( LocalComponents::CCMException,LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_short (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();
  lc->short_value(id);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_long ( const long id )
  throw ( LocalComponents::CCMException,LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_long (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();
  lc->long_value(id);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_ushort ( const unsigned short id )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_ushort (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();
  lc->ushort_value(id);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_ulong ( const unsigned long id )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_ulong (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();
  lc->ulong_value(id);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_float ( const float id )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_float (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();
  lc->float_value(id);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_double ( const double id )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_double (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();
  lc->double_value(id);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_char ( const char id )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_char (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();
  lc->char_value(id);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_string ( const std::string& id )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_string (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();
  lc->string_value(id);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_boolean ( const bool id )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_boolean (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();
  lc->boolean_value(id);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_octet ( const unsigned char id )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_octet (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();
  lc->octet_value(id);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

/* complex type factories. */

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_typedef ( const time_t& p )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_typedef (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();

  // TODO : IMPLEMENT FACTORY DETAILS HERE !
  lc->typedef_value(p);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_enum ( const Color& p )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_enum (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();

  // TODO : IMPLEMENT FACTORY DETAILS HERE !
  lc->enum_value(p);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_struct ( const Value& p )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_struct (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();

  // TODO : IMPLEMENT FACTORY DETAILS HERE !
  lc->struct_value(p);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_sequence ( const map& p )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_sequence (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();

  // TODO : IMPLEMENT FACTORY DETAILS HERE !
  lc->sequence_value(p);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

LocalComponents::EnterpriseComponent*
CCM_HelloHome_impl::create_with_array ( const doubleArray& p )
  throw (LocalComponents::CCMException, LocalComponents::CreateFailure )
{
  DEBUGNL ( " CCM_HelloHome_impl->create_with_array (  )" );
  CCM_Hello_impl* lc = new CCM_Hello_impl();

  // TODO : IMPLEMENT FACTORY DETAILS HERE !
  lc->array_value(p);
  return dynamic_cast<LocalComponents::EnterpriseComponent*> ( lc );
}

} // /namespace CCM_Session_Hello
} // /namespace CCM_Local


//==============================================================================
// entry point
//==============================================================================

extern "C" {
  LocalComponents::HomeExecutorBase*
  create_HelloHome (  )
  {
    DEBUGNL ( " create_HelloHome (  )" );
    return new CCM_Local::CCM_Session_Hello::CCM_HelloHome_impl (  );
  }
}

