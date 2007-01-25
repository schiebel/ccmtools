
//==============================================================================
// Hello - business logic implementation
//==============================================================================

#include <iostream>
#include <CCM_Utils/Debug.h>

#include "Hello_app.h"

using namespace std;
using namespace CCM_Utils;
using namespace CCM_Local;

namespace CCM_Local {
namespace world {
namespace europe {
namespace austria {
namespace CCM_Session_Hello {


//==============================================================================
// business logic functionality
//==============================================================================

CCM_Console*
CCM_Hello_impl::get_console (  )
{
  DEBUGNL ( " CCM_Hello_impl->get_console (  )" );
  console_impl* facet = new console_impl(this);
  return dynamic_cast<CCM_Console*> ( facet );
}



//==============================================================================
// console - facet implementation
//==============================================================================

console_impl::console_impl ( CCM_Hello_impl* component_impl )
  : component ( component_impl )
{
  DEBUGNL ( "+console_impl->console_impl (  )" );
}

console_impl::~console_impl (  )
{
  DEBUGNL ( "-console_impl->~console_impl (  )" );
}


world::europe::austria::LongList
console_impl::foo1 ( const world::europe::austria::LongList& l1, world::europe::austria::LongList& l2, world::europe::austria::LongList& l3 )
  
{
  DEBUGNL ( " console_impl->foo1 ( l1, l2, l3 )" );

  LongList r;
  for(int i=0;i<l1.size();i++) {
    r.push_back(i);
    l3.push_back(l2.at(i));
    l2.at(i) = l1.at(i);
  }
  return r;
}

world::europe::austria::StringList
console_impl::foo2 ( const world::europe::austria::StringList& s1, world::europe::austria::StringList& s2, world::europe::austria::StringList& s3 )
  
{
  DEBUGNL ( " console_impl->foo2 ( s1, s2, s3 )" );

  StringList r;
  for(int i=0;i<s1.size();i++) {
    r.push_back("test");
    s3.push_back(s2.at(i));
    s2.at(i) = s1.at(i);
  }
  return r;
}

world::europe::austria::Person
console_impl::foo3 ( const world::europe::austria::Person& p1, world::europe::austria::Person& p2, world::europe::austria::Person& p3 )
  
{
  DEBUGNL ( " console_impl->foo3 ( p1, p2, p3 )" );

  cout << p1.name << endl;
  cout << p2.name << endl;
  cout << p3.name << endl;

  Person r;
  r.name = p1.name + p2.name;
  r.id = p1.id + p2.id; 
  p3=p2;  
  p2=p1;
  return r; 
}

world::europe::austria::PersonMap
console_impl::foo4 ( const world::europe::austria::PersonMap& p1, world::europe::austria::PersonMap& p2, world::europe::austria::PersonMap& p3 )
  
{
  DEBUGNL ( " console_impl->foo4 ( p1, p2, p3 )" );

  PersonMap r;
  for(int i=0;i<p1.size();i++) {
    Person v;
    v.name = "test";
    v.id = i;
    r.push_back(v);
    p3.push_back(p2.at(i));
    p2.at(i) = p1.at(i);
  }
  return r;
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




void
CCM_Hello_impl::set_session_context ( localComponents::SessionContext* context )
  throw ( localComponents::CCMException )
{
  DEBUGNL ( " CCM_Hello_impl->set_session_context (  )" );
  ctx = dynamic_cast<CCM_Hello_Context*> ( context );
}

void
CCM_Hello_impl::ccm_activate (  )
  throw ( localComponents::CCMException )
{
  DEBUGNL ( " CCM_Hello_impl->ccm_activate (  )" );
}

void
CCM_Hello_impl::ccm_passivate (  )
  throw ( localComponents::CCMException )
{
  DEBUGNL ( " CCM_Hello_impl->ccm_passivate (  )" );
}

void
CCM_Hello_impl::ccm_remove (  )
  throw ( localComponents::CCMException )
{
  DEBUGNL ( " CCM_Hello_impl->ccm_remove (  )" );
}

} // /namespace CCM_Session_Hello
} // /namespace austria
} // /namespace europe
} // /namespace world
} // /namespace CCM_Local



