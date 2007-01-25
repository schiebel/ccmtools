
//==============================================================================
// Bank - business logic class definition
//==============================================================================

#ifndef __COMPONENT_CCM_Local_CCM_Session_Bank_Bank_APP__H__
#define __COMPONENT_CCM_Local_CCM_Session_Bank_Bank_APP__H__

#include <CCM_Local/CCM_Session_Bank/Bank_share.h>

namespace CCM_Local {
namespace CCM_Session_Bank {


//==============================================================================
// component implementation object
//==============================================================================

class CCM_Bank_impl
  : public CCM_Bank
{
 private:


 public:
  CCM_Bank_Context* ctx;

  CCM_Bank_impl (  );
  virtual ~CCM_Bank_impl (  );


  virtual CCM_BankAccount* get_account (  );



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
// account - facet adapter implementation class
//==============================================================================

class account_impl
  : public CCM_BankAccount
{
 private:
  CCM_Bank_impl* component;

  long balance_, overdraftLimit_;

 public:
  account_impl ( CCM_Bank_impl* component_impl );
  virtual ~account_impl (  );


  virtual void deposit ( const long amount ) throw (LocalComponents::CCMException);
#ifdef CCM_TEST_PYTHON
  void call_python_deposit ( const long amount ) throw (LocalComponents::CCMException);
#endif
  virtual void withdraw ( const long amount ) throw (LocalComponents::CCMException);
#ifdef CCM_TEST_PYTHON
  void call_python_withdraw ( const long amount ) throw (LocalComponents::CCMException);
#endif
  virtual long balance (  ) throw (LocalComponents::CCMException);
#ifdef CCM_TEST_PYTHON
  long call_python_balance (  ) throw (LocalComponents::CCMException);
#endif
  virtual long overdraftLimit (  ) throw (LocalComponents::CCMException);
#ifdef CCM_TEST_PYTHON
  long call_python_overdraftLimit (  ) throw (LocalComponents::CCMException);
#endif
  virtual void setOverdraftLimit ( const long newLimit ) throw (LocalComponents::CCMException);
#ifdef CCM_TEST_PYTHON
  void call_python_setOverdraftLimit ( const long newLimit ) throw (LocalComponents::CCMException);
#endif

};



} // /namespace CCM_Session_Bank
} // /namespace CCM_Local


#endif


