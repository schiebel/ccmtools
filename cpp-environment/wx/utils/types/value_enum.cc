// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: value_enum.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#include "value_enum.h"

#include "type_enum.h"
#include "value_simple.h"

#include <WX/Utils/error_macros.h>

#include <cassert>

namespace WX {
namespace Utils {

using namespace std;

static inline
const EnumerationType*
narrow_type(
   const Type* t)
{
   const EnumerationType* ret = dynamic_cast<const EnumerationType*>(t);
   assert(ret);
   return ret;
}

LTA_MEMDEF(EnumerationValue, 1, "$Id: value_enum.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

EnumerationValue::EnumerationValue(
   const EnumerationType* t
)
: Value(t),
  value_(EnumerationType::UndefinedValue) {}

EnumerationValue::EnumerationValue(
   const Value& that)
: Value(that.type()),
  value_(EnumerationType::UndefinedValue) {

   // note that we did not yet assure that that has
   // EnumerationType. we only constructed Value with its type,
   // whatever type that is. fortunately Value didn't do anything with
   // it yet (hah, sounds like good design), so we are able to check.

   const EnumerationType* et = dynamic_cast<const EnumerationType*>(that.type());
   if (!et) {
      THROW_ERROR_MSG(Type::IncompatibleAssignment,
                      "Cannot make a EnumerationType from a "<<that.type()->typestr());
   }

   // now that we have assured we actually have EnumerationType, we
   // can safely perform assignment.

   et->assign(this, &that);
}

EnumerationValue::EnumerationValue(
   const EnumerationValue& ev)
: Value(ev.type()),
  value_(EnumerationType::UndefinedValue)
{
   narrow_type(Value::type())->assign(this, &ev);
}   

void
EnumerationValue::set_as_string(
   const std::string& s)
{
   narrow_type(Value::type())->set_as_string(this, s);
}

void
EnumerationValue::set_as_int(
   int i)
{
   narrow_type(Value::type())->set_as_int(this, i);
}

int
EnumerationValue::get_as_int()
const
{
   return narrow_type(Value::type())->get_as_int(this);
}

const string&
EnumerationValue::get_as_string()
const
{
   return narrow_type(Value::type())->get_as_string(this);
}

EnumerationValue*
EnumerationValue::clone()
const
{
   EnumerationValue* ev = new EnumerationValue(narrow_type(Value::type()));
   ev->value_ = value_;
   return ev;
}

} // /namespace
} // /namespace
