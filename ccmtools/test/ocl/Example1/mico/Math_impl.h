
#ifndef __MATH__IMPL__H__
#define __MATH__IMPL__H__

#include "Math.h"
#include "Math_ccm.h"


//******************************************************************************
// Math Component
//******************************************************************************
typedef std::vector<long> LocalIntegerVector;

class IntegerStack_impl
: virtual public CCM_IntegerStack
{
 private:
  LocalIntegerVector field_;
  long maxSize_;

 public:
  IntegerStack_impl(CCM_Math_Session* c);
  virtual ~IntegerStack_impl();

  virtual ::IntegerVector* field();
  virtual void field( const ::IntegerVector& value );
  virtual CORBA::Long maxSize();
  virtual void maxSize( CORBA::Long value );
  
  virtual CORBA::Boolean isEmpty();
  virtual CORBA::Boolean isFull();
  virtual void push( CORBA::Long value );
  virtual CORBA::Long pop();
  virtual CORBA::Long top();
};


class CCM_Math_Session_impl 
: virtual public CCM_Math_Session
{
 private:

 public:
  CCM_Math_Context_var ctx;

  CCM_Math_Session_impl();
  virtual ~CCM_Math_Session_impl();

  CCM_IntegerStack_ptr get_stack();

  void set_session_context(Components::SessionContext_ptr ctx);
  void ccm_activate();
  void ccm_passivate();
  void ccm_remove();
};


class CCM_MathHome_impl
: public CCM_MathHome
{
 public:
  CCM_MathHome_impl();
  virtual ~CCM_MathHome_impl();

  Components::EnterpriseComponent_ptr create ();
};

#endif 







