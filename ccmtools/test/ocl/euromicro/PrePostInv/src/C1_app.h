
//==============================================================================
// C1 - business logic class definition
//==============================================================================

#ifndef __COMPONENT_CCM_Local_CCM_Session_C1_C1_APP__H__
#define __COMPONENT_CCM_Local_CCM_Session_C1_C1_APP__H__

#include <CCM_Local/CCM_Session_C1/C1_share.h>

namespace CCM_Local {
namespace CCM_Session_C1 {


//==============================================================================
// component implementation object
//==============================================================================

class CCM_C1_impl
  : public CCM_C1
{
 private:


 public:
  CCM_C1_Context* ctx;

  CCM_C1_impl (  );
  virtual ~CCM_C1_impl (  );


  virtual CCM_BasicTypes* get_i1 (  );



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
// i1 - facet adapter implementation class
//==============================================================================

class i1_impl
  : public CCM_BasicTypes
{
 private:
  CCM_C1_impl* component;
  long _a1;
  double _a2;
  std::string _a3;

 public:
  i1_impl ( CCM_C1_impl* component_impl );
  virtual ~i1_impl (  );

  virtual long a1 (  )
    throw ( LocalComponents::CCMException );
  virtual void a1 ( const long value )
    throw ( LocalComponents::CCMException );
  virtual double a2 (  )
    throw ( LocalComponents::CCMException );
  virtual void a2 ( const double value )
    throw ( LocalComponents::CCMException );
  virtual std::string a3 (  )
    throw ( LocalComponents::CCMException );
  virtual void a3 ( const std::string value )
    throw ( LocalComponents::CCMException );

  virtual long f1_2 ( const long p1, const long p2 ) 
    throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  long call_python_f1_2 ( const long p1, const long p2 ) 
    throw ( LocalComponents::CCMException );
#endif
  virtual long f1_10 ( const long p1, const long p2, const long p3, const long p4, const long p5, const long p6, const long p7, const long p8, const long p9, const long p10 ) 
    throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  long call_python_f1_10 ( const long p1, const long p2, const long p3, const long p4, const long p5, const long p6, const long p7, const long p8, const long p9, const long p10 ) 
    throw ( LocalComponents::CCMException );
#endif
  virtual double f2_2 ( const double p1, const double p2 ) 
    throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  double call_python_f2_2 ( const double p1, const double p2 ) 
    throw ( LocalComponents::CCMException );
#endif
  virtual double f2_10 ( const double p1, const double p2, const double p3, const double p4, const double p5, const double p6, const double p7, const double p8, const double p9, const double p10 ) 
    throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  double call_python_f2_10 ( const double p1, const double p2, const double p3, const double p4, const double p5, const double p6, const double p7, const double p8, const double p9, const double p10 ) 
    throw ( LocalComponents::CCMException );
#endif
  virtual std::string f3_2 ( const std::string& p1, const std::string& p2 ) 
    throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  std::string call_python_f3_2 ( const std::string& p1, const std::string& p2 ) 
    throw ( LocalComponents::CCMException );
#endif
  virtual std::string f3_10 ( const std::string& p1, const std::string& p2, const std::string& p3, const std::string& p4, const std::string& p5, const std::string& p6, const std::string& p7, const std::string& p8, const std::string& p9, const std::string& p10 ) 
    throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  std::string call_python_f3_10 ( const std::string& p1, const std::string& p2, const std::string& p3, const std::string& p4, const std::string& p5, const std::string& p6, const std::string& p7, const std::string& p8, const std::string& p9, const std::string& p10 ) 
    throw ( LocalComponents::CCMException );
#endif

};



} // /namespace CCM_Session_C1
} // /namespace CCM_Local


#endif


