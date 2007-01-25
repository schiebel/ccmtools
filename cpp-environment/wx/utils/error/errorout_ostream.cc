// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: errorout_ostream.cc,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#include "errorout_ostream.h"

#include "error_impl.h"
#include "errortrace.h"

namespace WX {
namespace Utils {

using namespace std;

// --------------------------------------------------------------------
static inline void output_offset(ostream& o, int offset) {
   for (int i=0; i<offset; i++)
      o << ' ';
}

void output(const ErrorTrace& t, ostream& o, int offset) {
   for (int i=0; i<(int)t.trace().size(); i++)
      output(t.trace()[i], o, offset);
}

void output(const Error& e, ostream& o, int offset) {
   output_offset(o, offset);
   o << e.message()
     << " (" << (e.file().size()? e.file(): "(unknown)") 
     << ':' 
     << e.line() << ")\n";
   if (e.trace().trace().size())
      output(e.trace(), o, offset+2);
}

} // /namespace
} // /namespace
