// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: value_struct.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#include "value_struct.h"

#include "type_struct.h"
#include "struct.h"

#include <WX/Utils/error_macros.h>

#include <cassert>

namespace WX {
namespace Utils {

using namespace std;

LTA_MEMDEF(StructValue, 2, "$Id: value_struct.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

static inline
const StructType*
narrow_type(
   const Type* t)
{
#  ifdef NDEBUG
   return static_cast<const StructType*>(t);
#  else
   const StructType* ret = dynamic_cast<const StructType*>(t);
   assert(ret);
   return ret;
#  endif
}

StructValue::StructValue()
: Value(StructType::anonymous())
{
   StructType::anonymous()->make_default(value_);
}

StructValue::StructValue(
   const Value& that)
: Value(that.type())
{

   // note that we did not yet assure that that has StructType. we
   // only constructed Value with its type, whatever type that
   // is. fortunately Value didn't do anything with it yet (hah,
   // sounds like good design), so we are able to check.
   
   const StructType* st = dynamic_cast<const StructType*>(that.type());
   if (!st) {
      THROW_ERROR_MSG(Type::IncompatibleAssignment,
                      "Cannot make a StructType from a "<<that.type()->typestr());
   }

   // now that we have assured we actually have StructType, we can
   // safely perform assignment.

   st->assign(this, &that);
}

StructValue::StructValue(
   const StructValue& that)
: Value(that.type())
{
   narrow_type(Value::type())->assign(this, &that);
}

StructValue::StructValue(
   const Struct& s)
: Value(StructType::anonymous()),
  value_(s) {}

StructValue::StructValue(
   const StructType* t)
: Value(t)
{
   t->make_default(value_);
}

void
StructValue::set(
   const std::string& name,
   const Value& value)
{
   narrow_type(Value::type())->setP(this, name, SmartPtr<Value>(value.shallow_copy()));
}

void
StructValue::setP(
   const std::string& name,
   const SmartPtr<Value>& value)
{
   narrow_type(Value::type())->setP(this, name, value);
}

const Value&
StructValue::get(
   const std::string& name)
const
{
   return narrow_type(Value::type())->get(this, name);
}

const SmartPtr<Value>&
StructValue::getP(
   const std::string& name)
const
{
   return narrow_type(Value::type())->getP(this, name);
}

int
StructValue::n()
const
{
   return narrow_type(Value::type())->n(this);
}

bool
StructValue::first(
   std::string& n,
   SmartPtr<Value>& v)
const
{
   return narrow_type(Value::type())->first(this, n, v);
}

bool
StructValue::next(
   std::string& n,
   SmartPtr<Value>& v)
const
{
   return narrow_type(Value::type())->next(this, n, v);
}

} // /namespace
} // /namespace
