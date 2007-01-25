// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: errorout_ostream.h,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#ifndef wx_utils_error_errorout_ostream_h
#define wx_utils_error_errorout_ostream_h

#include <iosfwd>

namespace WX {
namespace Utils {

class Error;
class ErrorTrace;

void output(const ErrorTrace&, std::ostream&, int offset=0);
void output(const Error&, std::ostream&, int offset=0);

inline std::ostream& operator<<(std::ostream& s, const ErrorTrace& e) {
   output(e, s);
   return s;
}
inline std::ostream& operator<<(std::ostream& s, const Error& e) {
   output(e, s);
   return s;
}

} // /namespace
} // /namespace

#endif
