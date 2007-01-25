
/**
 * CCM_IFace facet class implementation. 
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

#include "Test_iface_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_Test {

iface_impl::iface_impl(CCM_Local::CCM_Session_Test::CCM_Test_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+iface_impl->iface_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

iface_impl::~iface_impl()
{
    DEBUGNL ( "-iface_impl->~iface_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

short
iface_impl::op1(const short p1, short& p2, short& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("iface_impl->op1(p1, p2, p3)");
    p3=p2;
    p2=p1;
    return p3+p1;
}

long
iface_impl::op2(const long p1, long& p2, long& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("iface_impl->op2(p1, p2, p3)");
    p3=p2;
    p2=p1;
    return p3+p1; 
}

unsigned short
iface_impl::op3(const unsigned short p1, unsigned short& p2, unsigned short& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("iface_impl->op3(p1, p2, p3)");
    p3=p2;
    p2=p1;
    return p3+p1; 
}

unsigned long
iface_impl::op4(const unsigned long p1, unsigned long& p2, unsigned long& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("iface_impl->op4(p1, p2, p3)");
    p3=p2;
    p2=p1;
    return p3+p1; 
}

float
iface_impl::op5(const float p1, float& p2, float& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("iface_impl->op5(p1, p2, p3)");
    p3=p2;
    p2=p1;
    return p3+p1; 
}

double
iface_impl::op6(const double p1, double& p2, double& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("iface_impl->op6(p1, p2, p3)");
    p3=p2;
    p2=p1;
    return p3+p1; 
}

char
iface_impl::op7(const char p1, char& p2, char& p3)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("iface_impl->op7(p1, p2, p3)");
    p3=p2;
    p2=p1;
    return p3+p1; 
}

std::string
iface_impl::op8(const std::string& p1, std::string& p2, std::string& p3)
    throw (LocalComponents::CCMException)
{
  DEBUGNL("iface_impl->op8(p1, p2, p3)");
  cout << p1 << endl;
  cout << p2 << endl;
  cout << p3 << endl;
  
  p3=p2;
  p2=p1;
  return p3+p1;
}

bool
iface_impl::op9(const bool p1, bool& p2, bool& p3)
    throw (LocalComponents::CCMException)
{
  DEBUGNL("iface_impl->op9(p1, p2, p3)");
  p3=p2;
  p2=p1;
  return p3 && p1;
}

unsigned char
iface_impl::op10(const unsigned char p1, unsigned char& p2, unsigned char& p3)
  throw (LocalComponents::CCMException)
{
  DEBUGNL("iface_impl->op10(p1, p2, p3)");
  p3=p2;
  p2=p1;
  return p3+p1; 
}

} // /namespace CCM_Session_Test
} // /namespace CCM_Local
