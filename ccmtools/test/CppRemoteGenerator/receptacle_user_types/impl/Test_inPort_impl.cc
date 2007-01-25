
/**
 * CCM_Console facet class implementation. 
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

#include "Test_inPort_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_Test {

inPort_impl::inPort_impl(CCM_Local::CCM_Session_Test::CCM_Test_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+inPort_impl->inPort_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

inPort_impl::~inPort_impl()
{
    DEBUGNL ( "-inPort_impl->~inPort_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

long
inPort_impl::f1(const long p1, long& p2, long& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("inPort_impl->f1(p1, p2, p3)");
    p3=p2;
    p2=p1;
    return p3+p1; 
}

Person
inPort_impl::f2(const Person& p1, Person& p2, Person& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("inPort_impl->f2(p1, p2, p3)");
    Person r;
    r.name = p1.name + p2.name;
    r.id = p1.id + p2.id; 
    p3=p2;  
    p2=p1;
    return r; 
}

Address
inPort_impl::f3(const Address& p1, Address& p2, Address& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("inPort_impl->f3(p1, p2, p3)");
    Address r;
    r.street = p1.street + p2.street;
    r.number = p1.number + p2.number;
    r.resident.id   = p1.resident.id + p2.resident.id;
    r.resident.name = p1.resident.name + p2.resident.name;
    p3 = p2;
    p2 = p1;
    return r;
}

LongList
inPort_impl::f4(const LongList& p1, LongList& p2, LongList& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("inPort_impl->f4(p1, p2, p3)");
    LongList r;
    for(unsigned long i=0;i<p1.size();i++) {
      r.push_back(i);
      p3.push_back(p2.at(i));
      p2.at(i) = p1.at(i);
    }
    return r;
}

StringList
inPort_impl::f5(const StringList& p1, StringList& p2, StringList& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("inPort_impl->f5(p1, p2, p3)");
    StringList r;
    for(unsigned long i=0;i<p1.size();i++) {
      r.push_back("Test");
      p3.push_back(p2.at(i));
      p2.at(i) = p1.at(i);
    }
    return r;
}

PersonList
inPort_impl::f6(const PersonList& p1, PersonList& p2, PersonList& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("inPort_impl->f6(p1, p2, p3)");
    PersonList r;
    for(unsigned long i=0; i < p1.size(); i++) {
      Person v;
      v.name = "Test";
      v.id = i;
      r.push_back(v);
      p3.push_back(p2.at(i));
      p2.at(i) = p1.at(i);
    }
    return r;
}

time_t
inPort_impl::f7(const time_t& t1, time_t& t2, time_t& t3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("inPort_impl->f7(t1, t2, t3)");
    t3=t2;
    t2=t1;
    return t3+t1; 
}

Color
inPort_impl::f8(const Color& p1, Color& p2, Color& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("inPort_impl->f8(p1, p2, p3)");
    p3=p2;
    p2=p1;
    return CCM_Local::orange; 
}

} // /namespace CCM_Session_Test
} // /namespace CCM_Local
