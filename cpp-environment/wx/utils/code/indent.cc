// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: indent.cc,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#include "indent.h"

namespace WX {
namespace Utils {

std::ostream& indent(std::ostream& o, int indent) {
   while (indent--)
      o << ' ';
   return o;
}

} // /namespace
} // /namespace
