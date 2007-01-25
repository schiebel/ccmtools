// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: errortrace.h,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#ifndef wx_utils_error_errortrace_h
#define wx_utils_error_errortrace_h

#include "error.h"

#include <WX/Utils/smartptr.h>
#include <deque>

namespace WX {
namespace Utils {

class ErrorTraceImpl : public RefCounted {
public:
   void append(const Error&);
   const std::deque<Error>& trace() const { return trace_; }
private:
   std::deque<Error> trace_;
};


/**
   \ingroup utils_error

   \brief A list (possibly nested - a tree) of Error objects, each
   carrying descriptions of particular errors that have happened along
   execution of a code path. Supposed to be thrown as exception. For a
   more detailed description and examples see the documentation of
   class Error.

 */
class ErrorTrace : private SmartPtr<ErrorTraceImpl> {
public:
   ErrorTrace() {}
   ErrorTrace(const ErrorTrace&);
   ErrorTrace& operator=(const ErrorTrace&);
   ErrorTrace& append(const Error&);
   int n() const { return trace().size(); }
   const std::deque<Error>& trace() const;
};

inline ErrorTrace::ErrorTrace(const ErrorTrace& t)
: SmartPtr<ErrorTraceImpl>(t) {}

inline ErrorTrace& ErrorTrace::operator=(const ErrorTrace& t) {
   SmartPtr<ErrorTraceImpl>::operator=(t);
   return *this; 
}

} // /namespace
} // /namespace

#endif
