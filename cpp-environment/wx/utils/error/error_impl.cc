// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: error_impl.cc,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#include "error_impl.h"

#include "errortrace.h"
#include "errorout_ostream.h"

#include <sstream>

#ifndef WIN32
#  include <unistd.h>
#endif

namespace WX {
namespace Utils {

using namespace std;

static const ErrorTrace empty_trace;

// --------------------------------------------------------------------
LTA_MEMDEF(Error, 2, "$Id: error_impl.cc,v 1.1 2005/01/11 08:59:24 teiniker Exp $");

Error::Error(Badness b)
: badness_(b),
  line_(0),
  trace_(0) {}

Error::Error(const Error& e)
: trace_(0) {
   operator=(e);
}

Error::Error(const char* file, int line)
: badness_(Bad),
  file_(file),
  line_(line),
  trace_(0) {}

Error::Error(const char* file, int line, const ErrorTrace& t)
: badness_(Bad),
  file_(file),
  line_(line),
  trace_(new ErrorTrace(t)) {}

Error::~Error() throw() {
   try {
      delete trace_;
   }
   catch (...) {
      // we caught an exception from code we assumed to be
      // trivial. obviously we cannot assume iostream is still ok, so
      // we write this message with brute raw force.
      static const char* msg = "Error::~Error(): caught an exception\n";
#ifdef WIN32
#     warning Carlo: at this point, stdio is no safer than iostream
      fprintf(stderr,"%s",msg);
#else
      ::write(STDERR_FILENO, msg, strlen(msg));
#endif
      abort();
   }
}

Error& Error::operator=(const Error& e) {
   badness_ = e.badness_;
   file_ = e.file_;
   line_ = e.line_;
   message_ = e.message_;
   trace(e.trace());
   //what_ = string(); // we do not copy this one.
   return *this;
}

const ErrorTrace& Error::trace() const {
   return trace_? *trace_: empty_trace;
}

Error& Error::trace(const ErrorTrace& t) {
   if (trace_)
      *trace_ = t;
   else {
      if (&t != &empty_trace)
         trace_ = new ErrorTrace(t);
   }
   return *this;
}

const char*
Error::what()
const
throw()
{
   try {
      ostringstream os;
      os << *this;
      what_ = os.str();
      return what_.c_str();
   }
   catch (...) {
      // we caught an exception from code we assumed to be
      // trivial. obviously we cannot assume iostream is still ok, so
      // we write this message with brute raw force.
      static const char* msg = "Error::what(): caught an exception\n";
#ifdef WIN32
      fprintf(stderr,"%s",msg);
#else
      ::write(STDERR_FILENO, msg, strlen(msg));
#endif
      abort();
   }
}

} // /namespace
} // /namespace
