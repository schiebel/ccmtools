// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: extern_c.h,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#ifndef wx_utils_code_extern_c_h
#define wx_utils_code_extern_c_h

#ifdef __cplusplus

// we use confix's fancy namespace-to-includedir mapping. as there is
// nothing in this file but macros, namespaces don't apply at all, so
// there's no other reason to include namespaces here.

namespace WX {
namespace Utils {

#endif


#ifdef __cplusplus
#  define BEGIN_CPLUSPLUS extern "C" {
#  define END_CPLUSPLUS }
#else
#  define BEGIN_CPLUSPLUS
#  define END_CPLUSPLUS
#endif

#ifdef __cplusplus
}; // /namespace
}; // /namespace
#endif

#endif
