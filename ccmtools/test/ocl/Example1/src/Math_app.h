/*
    $Id: Math_app.h,v 1.2 2004/03/02 19:37:38 teiniker Exp $
*/

//==============================================================================
// Math - business logic class definition
//==============================================================================

#ifndef __COMPONENT_CCM_Local_CCM_Session_Math_Math_APP__H__
#define __COMPONENT_CCM_Local_CCM_Session_Math_Math_APP__H__

#include <CCM_Local/CCM_Session_Math/Math_share.h>

namespace CCM_Local {
namespace CCM_Session_Math {


//==============================================================================
// component implementation object
//==============================================================================

class CCM_Math_impl
  : public CCM_Math
{
 private:


 public:
  CCM_Math_Context* ctx;

  CCM_Math_impl (  );
  virtual ~CCM_Math_impl (  );


  virtual CCM_IntegerStack* get_stack (  );



  // Callback methods

  virtual void set_session_context ( LocalComponents::SessionContext* ctx )
    throw ( LocalComponents::CCMException );
  virtual void ccm_activate (  )
    throw ( LocalComponents::CCMException );
  virtual void ccm_passivate (  )
    throw ( LocalComponents::CCMException );
  virtual void ccm_remove (  )
    throw ( LocalComponents::CCMException );
};

//==============================================================================
// stack - facet adapter implementation class
//==============================================================================

class stack_impl
  : public CCM_IntegerStack
{
 private:
  CCM_Math_impl* component;
  IntegerVector field_;
  long maxSize_;

 public:
  stack_impl ( CCM_Math_impl* component_impl );
  virtual ~stack_impl (  );

  virtual IntegerVector field (  ) throw ( LocalComponents::CCMException );
  virtual void field ( const IntegerVector value ) throw ( LocalComponents::CCMException );
  virtual long maxSize (  ) throw ( LocalComponents::CCMException );
  virtual void maxSize ( const long value ) throw ( LocalComponents::CCMException );

  virtual bool isEmpty (  )  throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  bool call_python_isEmpty (  )  throw ( LocalComponents::CCMException );
#endif
  virtual bool isFull (  )  throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  bool call_python_isFull (  )  throw ( LocalComponents::CCMException );
#endif
  virtual void push ( const long value )  throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  void call_python_push ( const long value )  throw ( LocalComponents::CCMException );
#endif
  virtual long pop (  )  throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  long call_python_pop (  )  throw ( LocalComponents::CCMException );
#endif
  virtual long top (  )  throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  long call_python_top (  )  throw ( LocalComponents::CCMException );
#endif

};



} // /namespace CCM_Session_Math
} // /namespace CCM_Local


#endif


