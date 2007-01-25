//
//$Id: struct_out.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//

#ifndef wx_utils_types_struct_out_h
#define wx_utils_types_struct_out_h

#include "struct.h"
#include "value.h"
#include <WX/Utils/smartptr.h>
#include <iosfwd>

namespace WX {
namespace Utils {

std::ostream& output (std::ostream&, const Struct&);
inline std::ostream& operator<<(std::ostream& o, const Struct& s) {
    return output(o, s);
}
    
} // /namespace
} // / namespace

#endif

