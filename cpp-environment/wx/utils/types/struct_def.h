// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: struct_def.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#ifndef wx_utils_types_struct_def_h
#define wx_utils_types_struct_def_h

#include "type.h"

#include <WX/Utils/smartptr.h>

#include <string>

namespace WX {
namespace Utils {

/**

   \brief Definition of the type of a Struct instance

   \ingroup utils_types

 */
class StructDef {
public:
   StructDef();
   StructDef(const StructDef&);
   StructDef& operator=(const StructDef&);
   ~StructDef();

   void insert(const std::string&, const SmartPtr<Type>&);
   const SmartPtr<Type>& get(const std::string&) const;

private:
   class StructDef_Map_* map_;
};
} // /namespace
} // /namespace

#endif
