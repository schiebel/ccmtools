// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: align.h,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#ifndef wx_utils_code_align_h
#define wx_utils_code_align_h

namespace WX {
namespace Utils {

// FIXME: is this ALIGNMENT calculation correct? >>>
struct _al {
   void* p ;
   char c ;
} ;
static const size_t ALIGNMENT = sizeof(_al) - sizeof(void*);

} // /namespace
} // /namespace

#endif
