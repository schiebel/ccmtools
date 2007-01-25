#include<cstdio>
#include<string>

#include "Math_impl.h"

using namespace std;

/**
 * Implementation of the console facet class.
 *
 *
 */
IntegerStack_impl::IntegerStack_impl(CCM_Math_Session* c)
  : maxSize_(123456)
{
  cout << "+IntegerStack_impl::IntegerStack_impl()" << endl;
}

IntegerStack_impl::~IntegerStack_impl()
{
  cout << "-IntegerStack_impl::~IntegerStack_impl()" << endl;
}


::IntegerVector* IntegerStack_impl::field()
{
  // TODO
}

void IntegerStack_impl::field( const ::IntegerVector& value )
{
  // TODO
}

CORBA::Long IntegerStack_impl::maxSize()
{
  return maxSize_;
}

void IntegerStack_impl::maxSize( CORBA::Long value )
{
  maxSize_ = value;
}
  
CORBA::Boolean IntegerStack_impl::isEmpty()
{
  return field_.empty();
}

CORBA::Boolean IntegerStack_impl::isFull()
{
  return field_.size()>=maxSize_;
}

void IntegerStack_impl::push( CORBA::Long value )
{
  field_.push_back((long)value);
}

CORBA::Long IntegerStack_impl::pop()
{
  long result = field_.back();
  field_.pop_back();
  return (CORBA::Long)result;
}

CORBA::Long IntegerStack_impl::top()
{
  return (CORBA::Long)field_.back();
}




/**
 * Implementation of the component class.
 *
 *
 */ 

CCM_Math_Session_impl::CCM_Math_Session_impl()
{
  cout << "+CCM_Math_Session_impl::CCM_Math_Session_impl()" << endl;
}


CCM_Math_Session_impl::~CCM_Math_Session_impl()
{
  cout << "-CCM_Math_Session_impl::~CCM_Math_Session_impl()" << endl;
}

void CCM_Math_Session_impl::set_session_context(Components::SessionContext_ptr x)
{
  cout << " CCM_Math_Session_impl::set_session_context()" << endl;
  ctx = CCM_Math_Context::_narrow(x);
}

void CCM_Math_Session_impl::ccm_activate()
{
  cout << " CCM_Math_Session_impl::ccm_activate()" << endl;
}

void CCM_Math_Session_impl::ccm_passivate()
{
  cout << " CCM_Math_Session_impl::ccm_passivate()" << endl;
}

void CCM_Math_Session_impl::ccm_remove()
{
  cout << " CCM_Math_Session_impl::ccm_remove()" << endl;
}



/**
 * Implementation of the component home class.
 *
 *
 */

CCM_MathHome_impl::CCM_MathHome_impl()
{
  cout << "+CCM_MathHome_impl::CCM_MathHome_impl()" << endl;
}

CCM_MathHome_impl::~CCM_MathHome_impl()
{
  cout << "-CCM_MathHome_impl::~CCM_MathHome_impl()" << endl;
}

Components::EnterpriseComponent_ptr CCM_MathHome_impl::create ()
{ 
  cout << " CCM_MathHome_impl::create()" << endl;
  return new CCM_Math_Session_impl;
}

CCM_IntegerStack_ptr CCM_Math_Session_impl::get_stack()
{
  cout << " CCM_Math_Session_impl::get_stack()" << endl;
  return new IntegerStack_impl(this);
}





/**
 * The CCM component container needs an entry point to bootstrap the component
 * implementation. The create_MathHome() function returns a reference to a 
 * component home instance.
 *
 * Note that the Components::HomeExecutorBase class is a base class of every
 * CCM component home.
 */
extern "C" {

  Components::HomeExecutorBase_ptr create_MathHome()
  {
    cout << "create_MathHome()" << endl;
    return new CCM_MathHome_impl;
  }
};



