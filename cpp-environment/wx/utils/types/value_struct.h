// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: value_struct.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#ifndef wx_utils_types_value_struct_h
#define wx_utils_types_value_struct_h

#include "value.h"
#include "struct.h"

#include <WX/Utils/linkassert.h>

namespace WX {
namespace Utils {

class StructType;

/**

   \brief Struct value - carries a Struct

   \ingroup utils_types

 */
class StructValue : public Value {
public:
   /** Boilerplate */
   //@{
   StructValue();
   StructValue(const Value&);
   StructValue(const StructValue&);
   StructValue(const Struct&);
   StructValue(const StructType*);

   Value& operator=(const StructValue& v) { Value::assign(v); return *this; }
   Value& operator=(const Value& v) { Value::assign(v); return *this; }
   //@}

   /** My own interface */
   //@{
   void set(const std::string& name, const Value& value);
   void setP(const std::string& name, const SmartPtr<Value>& value);

   /** Get value associated with name. If the struct is of anonymous
       type, this will throw an exception (Error) if the value is not
       there, so if you want to accommodate this case, you'd better
       use getP() instead which never throws with anonymous
       structs. With "strongly typed" structs, a value is guaranteed
       to be returned, as long as the type has a valid member of that
       name (in which case StructType::NoSuchMember is thrown). */
   const Value& get(const std::string& name) const;

   /** Get value associated with name. If the struct has the anonymous
       type, the returned value can be null. If the struct has "strong
       type", the return value is guaranteed to point to a meaningful
       value. If the type has no member named \a name,
       StructType::NoSuchMember is thrown. */
   const SmartPtr<Value>& getP(const std::string& name) const;

   int n() const;
   bool first(std::string& n, SmartPtr<Value>& v) const;
   bool next(std::string& n, SmartPtr<Value>& v) const;

   const Struct& value() const { return value_; }
   //@}

private:
   friend class StructType;
   Struct value_;

public:
   LTA_MEMDECL(2);
};
LTA_STATDEF(StructValue, 2);

} // /namespace
} // /namespace

#endif
