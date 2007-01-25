//
//$Id: value_out.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//

#include "value_out.h"
#include "value_simple.h"
#include "value_struct.h"
#include "struct_out.h"

#include <WX/Utils/smartptr.h>

#include <iostream>
#include <iomanip>

using namespace std;

namespace WX {
  namespace Utils {

  using namespace WX::Utils;

ostream& output (ostream& os, const SmartPtr<Value>& v) {

  if (!v)
    return os << "(null)";

  IntValue i;
  DoubleValue d;
  StringValue s;
  BooleanValue b;
  StructValue st;

  if (i.can_assign(*v.cptr())) {
    i = *v.cptr();
    return os << i.value();
  } 
  else if (d.can_assign(*v.cptr())) {
    d = *v.cptr();
    int p = os.precision();
    os.precision(10);
    return os << d.value();
    os.precision(p);
  } 
  else if (s.can_assign(*v.cptr())) {
    s = *v.cptr();
    return os << s.value();
  } 
  else if (b.can_assign(*v.cptr())) {
    b = *v.cptr();
    return os << b.value();
  }
  else if (st.can_assign(*v.ptr())) {
      st = *v.ptr();
      return os << st.value() << endl;
  }
  return os << "(unknown type)";
}

  }// /namespace
}// /namespace
