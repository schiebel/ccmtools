// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: error_impl.h,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#ifndef wx_utils_error_error_impl_h
#define wx_utils_error_error_impl_h

#include "errorout_ostream.h"
#include "error_macros.h"

#include <WX/Utils/linkassert.h>

#include <string>
#include <sstream>

#include <exception>

namespace WX {
namespace Utils {

// we cannot have an ErrorTrace automatic member because errortrace.h
// in turn includes this file, which would end up in circular
// includes. so we have to forward declare it and manage it in the .cc
// file instead.
class ErrorTrace;


/**

   \ingroup utils_error

   \brief Conceptually, An Error object is a description of an error
   that happened, either directly or indirectly. Supposed to be thrown
   as exception.

   As objects of type Error are supposed to be thrown as exceptions,
   it makes sense to derive from them (Error itself derives from
   std::exception) to emphasize a specific failure condition at a
   particular point in code, but still provide the common Error
   interface (which is described in the remainder).

   An Error carries a punctual description (message()) of an error
   that has happened. Along with this description it can (should)
   carry the file name and a line number (file(), line()) of the code
   location where it happened.

   An Error can own a nested ErrorTrace object, which is basically a
   list of Error objects. For example, a common use for this is if you
   are calling a function which throws an Error object as
   exception. Then you may want to decribe this error in your own
   (more high level) terms, like "Could not write this record to disk
   because the marshaller failed (detailed description follows)" (what
   follows is the marshaller's Error, contained in the trace).

   Here's a small example of how to use it.

   \code
   Error caught; // e.g. caught by an exception handler
   ErrorTrace trace;
   trace.append(caught);
   throw Error().file(__FILE__).
                 line(__LINE__).
                 message("My own high level description").
                 trace(trace));
   \endcode

   So, this example isn't really small as it introduces all of Error
   at once. Fortunately, there are helper macros which make life a lot
   easier. Typical uses are

   \code
   #include <WX/Utils/error_macros.h>

   COMPOSE_ERROR_MSG(DerivedError, err,
                   "My " << 2 << " bucks here");
   \endcode

   which declares a variable err of type DerivedError (which, as the
   name suggests, is derived from class Error). Note the demonstrative
   use of the output operator: you compose a message just as you do
   with ostreams.

   Another typical use is

   \code
   #include <WX/Utils/error_macros.h>

   THROW_ERROR_MSG(DerivedError,
                   "My " << 2 << " bucks here");
   \endcode

   which does the same as above, except that it throws the object
   immediately instead.

 */
class Error : public std::exception {
public:
   enum Badness {
     Good,
     Bad
   };

public:
   /** All members are nilled out. */
   Error(Badness=Bad);
   /** The parameters are supposed to be filled with the __FILE__ and
       __LINE__ preprocessor macros. */
   Error(const char* file, int line);
   /** Same as previous, but with ErrorTrace.  */
   Error(const char* file, int line, const ErrorTrace&);
   /** Copy constructor. */
   Error(const Error&);
   /** Destructor. */
   ~Error() throw();
   /** Assignment operator. */
   Error& operator=(const Error&);

   ///@{ Access methods.
   Badness badness() const { return badness_; }
   bool good() const { return badness_==Good; }
   operator bool() const { return !good(); }

   const std::string& file() const { return file_; }
   int line() const { return line_; }
   const std::string& message() const { return message_; }
   const ErrorTrace& trace() const;
   ///@}

   Error& file(const std::string& f) { file_=f; return *this; }
   Error& line(int l) { line_=l; return *this; }
   Error& message(const std::string& m) { message_=m; return *this; }
   Error& trace(const ErrorTrace&);

   /// exception interface
   virtual const char* what() const throw();

private:
   Badness badness_;
   std::string file_;
   int line_;
   std::string message_;
   ErrorTrace* trace_; 

   mutable std::string what_;

public:
   LTA_MEMDECL(2);
};
LTA_STATDEF(Error, 2);

} // /namespace
} // /namespace

#endif
