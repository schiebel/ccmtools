
//==============================================================================
// Grfx - business logic class definition
//==============================================================================

#ifndef __COMPONENT_CCM_Local_CCM_Session_Grfx_Grfx_APP__H__
#define __COMPONENT_CCM_Local_CCM_Session_Grfx_Grfx_APP__H__

#include <CCM_Local/CCM_Session_Grfx/Grfx_share.h>

namespace CCM_Local {
namespace CCM_Session_Grfx {


//==============================================================================
// component implementation object
//==============================================================================

class CCM_Grfx_impl
  : public CCM_Grfx
{
 private:


 public:
  CCM_Grfx_Context* ctx;

  CCM_Grfx_impl (  );
  virtual ~CCM_Grfx_impl (  );


  virtual CCM_Point* get_point (  ) throw ( LocalComponents::CCMException );
  virtual CCM_Line* get_line (  ) throw ( LocalComponents::CCMException );



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
// point - facet adapter implementation class
//==============================================================================

class point_impl
  : public CCM_Point
{
 private:
  CCM_Grfx_impl* component;
  double x_, y_;

 public:
  point_impl ( CCM_Grfx_impl* component_impl );
  virtual ~point_impl (  );

  virtual double x (  ) throw ( LocalComponents::CCMException );
  virtual void x ( const double value ) throw ( LocalComponents::CCMException );
  virtual double y (  ) throw ( LocalComponents::CCMException );
  virtual void y ( const double value ) throw ( LocalComponents::CCMException );

  virtual void move ( const double dx, const double dy ) throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  void call_python_move ( const double dx, const double dy ) throw ( LocalComponents::CCMException );
#endif

};

//==============================================================================
// line - facet adapter implementation class
//==============================================================================

class line_impl
  : public CCM_Line
{
 private:
  CCM_Grfx_impl* component;
  WX::Utils::SmartPtr<Point> startPt_, endPt_;

 public:
  line_impl ( CCM_Grfx_impl* component_impl );
  virtual ~line_impl (  );

  virtual WX::Utils::SmartPtr<Point> startPt (  ) 
    throw ( LocalComponents::CCMException );
  virtual void startPt ( const WX::Utils::SmartPtr<Point> value ) 
    throw ( LocalComponents::CCMException );
  virtual WX::Utils::SmartPtr<Point> endPt (  ) 
    throw ( LocalComponents::CCMException );
  virtual void endPt ( const WX::Utils::SmartPtr<Point> value ) 
    throw ( LocalComponents::CCMException );

  virtual void move ( const double dx, const double dy ) 
    throw ( LocalComponents::CCMException );
#ifdef CCM_TEST_PYTHON
  void call_python_move ( const double dx, const double dy ) 
    throw ( LocalComponents::CCMException );
#endif

};



} // /namespace CCM_Session_Grfx
} // /namespace CCM_Local


#endif


