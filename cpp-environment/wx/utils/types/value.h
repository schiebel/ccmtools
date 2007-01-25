// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: value.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#ifndef wx_utils_types_value_h
#define wx_utils_types_value_h

#include "type.h"

#include <WX/Utils/linkassert.h>
#include <WX/Utils/smartptr.h>
#include <WX/Utils/error.h>

namespace WX {
namespace Utils {

/** 
    \class Value

    \brief The analog (well, a subset) of a CORBA::Any.

    \ingroup utils_types

 */
class Value : public WX::Utils::RefCounted {
public:
   Value(const Type* t) : type_(t) {}
   virtual ~Value() {}

   virtual Value& operator=(const Value& v) = 0;

   /** Get the type the concrete object is an instance of. */
   const Type* type() const { return type_; }
   const std::string& typestr() const { return type_->typestr(); }

   bool can_assign(const Value& v) const { return type_->can_assign(v); }
   /** Assignment to this from that. */
   void assign(const Value& that) { type_->assign(this, &that); }

   Value* shallow_copy() const { return type_->shallow_copy(this); }
   Value* toplevel_copy() const { return type_->toplevel_copy(this); };
   Value* deep_copy() const { return type_->deep_copy(this); }

   /** Returns an integer <0 if this is less than that, >0 if greater,
       and ==0 if equal. */
   int compare(const Value& that) const { return type_->compare(this, &that); }

   /** FIXME: quick hack. Will become a more general convert facility
       with configurable conversions, once time is right. */
   void convert_from(const Value& v) { type_->convert_from(this, &v); }

private:
   const Type* type_;

private:
   // we force derived classes to implement their copy ctors in terms
   // of Value::assign(const Value&). this is as safe as can be
   // because this way they are required to make their state look ok
   // before.
   Value(const Value& v);

public:
   LTA_MEMDECL(3);

};
LTA_STATDEF(Value, 3);

inline bool operator==(const Value& l, const Value& r) {
   return l.compare(r) == 0;
}

inline bool operator!=(const Value& l, const Value& r) {
   return l.compare(r) != 0;
}

inline bool operator<(const Value& l, const Value& r) {
   return l.compare(r) < 0;
}

inline bool operator<=(const Value& l, const Value& r) {
   return l.compare(r) <= 0;
}

inline bool operator>(const Value& l, const Value& r) {
   return l.compare(r) > 0;
}

inline bool operator>=(const Value& l, const Value& r) {
   return l.compare(r) >= 0;
}

} // /namespace
} // /namespace

#endif
