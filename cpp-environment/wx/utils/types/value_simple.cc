// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: value_simple.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#include "value_simple.h"

#include "type_simple.h"
#include "value_enum.h"

#include <WX/Utils/linkassert.h>
#include <WX/Utils/error_macros.h>

#include <cstdio>

namespace WX {
namespace Utils {

using namespace std;

// --------------------------------------------------------------------
LTA_MEMDEF(IntValue, 1, "$Id: value_simple.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

IntValue::IntValue(
   int i)
: Value(IntType::instance()),
  value_(i) {}

IntValue::IntValue(
   const Value& v)
: Value(IntType::instance()) 
{
   Value::assign(v);
}

IntValue::IntValue(
   const IntValue& v)
: Value(IntType::instance()) 
{
   Value::assign(v);
}

IntValue::IntValue(
   const IntType* t,
   int i)
: Value(t),
  value_(i) {}

IntValue*
IntValue::narrow(
   Value* v)
{
   return dynamic_cast<IntValue*>(v);
}

// --------------------------------------------------------------------
LTA_MEMDEF(DoubleValue, 3, "$Id: value_simple.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

DoubleValue::DoubleValue(
   double d)
: Value(DoubleType::instance()),
  value_(d) {}

DoubleValue::DoubleValue(
   const Value& v)
: Value(DoubleType::instance())
{
   Value::assign(v);
}

DoubleValue::DoubleValue(
   const DoubleType* t,
   double d)
: Value(t),
  value_(d) {}

DoubleValue::DoubleValue(
   const DoubleValue& v)
: Value(DoubleType::instance())
{
   Value::assign(v);
}

// --------------------------------------------------------------------
LTA_MEMDEF(StringValue, 2, "$Id: value_simple.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

StringValue::StringValue()
: Value(StringType::instance()) {}

StringValue::StringValue(
   const std::string& s)
: Value(StringType::instance()),
  value_(s) {}

StringValue::StringValue(
   const Value& v)
: Value(StringType::instance())
{
   Value::assign(v);
}

StringValue::StringValue(
   const StringType* t,
   const string& s)
: Value(t),
  value_(s) {}

StringValue::StringValue(
   const StringValue& v)
: Value(StringType::instance())
{
   Value::assign(v);
}

StringValue&
StringValue::operator+=(
   const StringValue& v)
{
   value_ += v.value_;
   return *this;
}

StringValue&
StringValue::operator+=(
   const std::string& s)
{
   value_ += s;
   return *this;
}

StringValue
operator+(
   const StringValue& a,
   const StringValue& b)
{
   StringValue v(a.value());
   v += b;
   return v;
}

// --------------------------------------------------------------------
LTA_MEMDEF(BooleanValue, 1, "$Id: value_simple.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

BooleanValue::BooleanValue(
   bool b)
: Value(BooleanType::instance()),
  value_(b) {}

BooleanValue::BooleanValue(
   const Value& v)
: Value(BooleanType::instance())
{
   Value::assign(v);
}

BooleanValue::BooleanValue(
   const BooleanType* t,
   bool b)
: Value(t),
  value_(b) {}

BooleanValue::BooleanValue(
   const BooleanValue& v)
: Value(BooleanType::instance())
{
   Value::assign(v);
}

} // /namespace
} // /namespace
