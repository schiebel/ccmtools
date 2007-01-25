//
//$Id: value_out.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
//writes a value in an output stream

#ifndef wx_utils_types_value_out_h
#define wx_utils_types_value_out_h

#include "value.h"

#include <WX/Utils/smartptr.h>

#include <iosfwd>

namespace WX {
namespace Utils {

std::ostream& output(std::ostream&, const SmartPtr<Value>&);

inline std::ostream& operator<<(std::ostream& o, const SmartPtr<Value>& v) {
   return output(o, v);
}

}// /namespace
}// /namespace

#endif
