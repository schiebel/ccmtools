//
//$Id: struct_out.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//

#include "struct_out.h"
#include "value_out.h"

namespace WX {
namespace Utils {

using namespace std;

ostream& output(ostream& os, const Struct& st) {

   //assert(st);

   string name;
   SmartPtr<Value> v;

   bool sv = st.first(name, v);
   while (sv) {
      os << '(';
      os << name << ':';
      output (os, v);
      os << ')' << endl;
      sv = st.next(name, v);
   }

   return os;
}

}// /namespace
}// /namespace
