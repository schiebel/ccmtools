
//==============================================================================
// Grfx - business logic implementation
//==============================================================================

#include <iostream>
#include <WX/Utils/debug.h>

#include "Grfx_app.h"

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;

namespace CCM_Local {
namespace CCM_Session_Grfx {


//==============================================================================
// business logic functionality
//==============================================================================

CCM_Point*
CCM_Grfx_impl::get_point (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Grfx_impl->get_point (  )" );
  point_impl* facet = new point_impl(this);
  return dynamic_cast<CCM_Point*> ( facet );
}

CCM_Line*
CCM_Grfx_impl::get_line (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Grfx_impl->get_line (  )" );
  line_impl* facet = new line_impl(this);
  return dynamic_cast<CCM_Line*> ( facet );
}



//==============================================================================
// point - facet implementation
//==============================================================================

point_impl::point_impl ( CCM_Grfx_impl* component_impl )
  : component ( component_impl ), x_(0), y_(0)
{
  DEBUGNL ( "+point_impl->point_impl (  )" );
}

point_impl::~point_impl (  )
{
  DEBUGNL ( "-point_impl->~point_impl (  )" );
}

double
point_impl::x (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " point_impl->x (  )" );

  return x_;
}

void
point_impl::x ( const double value )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " point_impl->x ( value )" );

  x_ = value;
}

double
point_impl::y (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " point_impl->y (  )" );

  return y_;
}

void
point_impl::y ( const double value )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " point_impl->y ( value )" );

  y_ = value;
}


void
point_impl::move ( const double dx, const double dy )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " point_impl->move ( dx, dy )" );

  x_ += dx;
  y_ += dy;
}



//==============================================================================
// line - facet implementation
//==============================================================================

line_impl::line_impl ( CCM_Grfx_impl* component_impl )
  : component ( component_impl ),
    startPt_(new point_impl(component_impl)),
    endPt_(new point_impl(component_impl))
{
  DEBUGNL ( "+line_impl->line_impl (  )" );
}

line_impl::~line_impl (  )
{
  DEBUGNL ( "-line_impl->~line_impl (  )" );
}

WX::Utils::SmartPtr<Point>
line_impl::startPt (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " line_impl->startPt (  )" );

  return WX::Utils::SmartPtr<Point>(startPt_);
}

void
line_impl::startPt ( const WX::Utils::SmartPtr<Point> value )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " line_impl->startPt ( value )" );

  startPt_ = value;
}

WX::Utils::SmartPtr<Point>
line_impl::endPt (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " line_impl->endPt (  )" );

  return WX::Utils::SmartPtr<Point>(endPt_);
}

void
line_impl::endPt ( const WX::Utils::SmartPtr<Point> value )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " line_impl->endPt ( value )" );

  endPt_ = value;
}


void
line_impl::move ( const double dx, const double dy )
  throw ( LocalComponents::CCMException )  
{
  DEBUGNL ( " line_impl->move ( dx, dy )" );

  //startPt_->move(dx, dy);
  startPt_->x(startPt_->x()+dx);
  startPt_->y(startPt_->y()+dy);
  endPt_->move(dx, dy);
}





//==============================================================================
// class implementation
//==============================================================================

CCM_Grfx_impl::CCM_Grfx_impl (  )
{
  DEBUGNL ( "+CCM_Grfx_impl->CCM_Grfx_impl (  )" );
}

CCM_Grfx_impl::~CCM_Grfx_impl (  )
{
  DEBUGNL ( "-CCM_Grfx_impl->~CCM_Grfx_impl (  )" );
}




void
CCM_Grfx_impl::set_session_context ( LocalComponents::SessionContext* context )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Grfx_impl->set_session_context (  )" );
  ctx = dynamic_cast<CCM_Grfx_Context*> ( context );
}

void
CCM_Grfx_impl::ccm_activate (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Grfx_impl->ccm_activate (  )" );
}

void
CCM_Grfx_impl::ccm_passivate (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Grfx_impl->ccm_passivate (  )" );
}

void
CCM_Grfx_impl::ccm_remove (  )
  throw ( LocalComponents::CCMException )
{
  DEBUGNL ( " CCM_Grfx_impl->ccm_remove (  )" );
}

} // /namespace CCM_Session_Grfx
} // /namespace CCM_Local



