// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: type_simple.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#include "type_simple.h"

#include "value_simple.h"
#include "value_enum.h"

#include <WX/Utils/error_macros.h>

#include <cstdio>
#include <cstdlib>
#include <cassert>
#include <cstring>

namespace WX {
namespace Utils {

using namespace std;

struct TypeSingletons {
   TypeSingletons();
   SmartPtr<IntType> it_singleton;
   SmartPtr<StringType> st_singleton;
   SmartPtr<BooleanType> bt_singleton;
   SmartPtr<DoubleType> dt_singleton;
};
inline
TypeSingletons::TypeSingletons()
: it_singleton(new IntType),
  st_singleton(new StringType),
  bt_singleton(new BooleanType),
  dt_singleton(new DoubleType) {}

static TypeSingletons* my_tsingletons = NULL;

static inline
const TypeSingletons*
init_tsingletons()
{
   if (!my_tsingletons)
      my_tsingletons = new TypeSingletons;
   return my_tsingletons;
}

Init_Globals::Init_Globals()
{
   init_tsingletons();
}

// human readable type descriptions
static const std::string iv_typestr("integer");
static const std::string sv_typestr("string");
static const std::string bv_typestr("boolean");
static const std::string dv_typestr("double");

static inline int compare_double(double l, double r) {
   if (l < r) return -1;
   if (l > r) return 1;
   return 0;
}

static inline int compare_int(int l, int r) {
   if (l < r) return -1;
   if (l > r) return 1;
   return 0;
}

// --------------------------------------------------------------------
LTA_MEMDEF(IntType, 4, "$Id: type_simple.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

IntType::IntType()
: default_value_(this, 0)
{}

IntValue*
IntType::narrow(Value* v)
const
{
   return (v->type() == this)? dynamic_cast<IntValue*>(v) : NULL;
}

const IntValue*
IntType::const_narrow(
   const Value* v)
const
{
   return (v->type() == this)? dynamic_cast<const IntValue*>(v) : NULL;
}

const IntType*
IntType::instance()
{
   return init_tsingletons()->it_singleton.cptr();
}

const SmartPtr<IntType>&
IntType::instanceP()
{
   return init_tsingletons()->it_singleton;
}

IntValue*
IntType::create()
const {
   return new IntValue(this, 0);
}

bool
IntType::can_assign(const Value& v, Error* e) const {
   if (dynamic_cast<const IntValue*>(&v))
      return true;
   if (dynamic_cast<const EnumerationValue*>(&v))
      return true;
   if (e) {
      COMPOSE_ERROR_MSG(Error, err, v.typestr()<<" "
                        "does not match "<<typestr());
      *e = err;
   }
   return false;
}

void
IntType::assign(
   Value* self,
   const Value* that)
const
{
   if (self == that) return;
   IntValue* iself = this->narrow(self);
   assert(iself);

   // we consider an IntValue which has a different IntType to be
   // assignable to self (iow, we do not use const_narrow(), but
   // rather dynamic_cast()). this is mainly for historical reasons
   // where there were different IntTypes with different formatting
   // parameters around.
   const IntValue* ithat = dynamic_cast<const IntValue*>(that);
   if (ithat) {
      iself->set_value(ithat->value());
      return;
   }

   // any instance of EnumerationType can be assigned to an IntValue.
   const EnumerationValue* ethat = dynamic_cast<const EnumerationValue*>(that);
   if (ethat) {
      iself->set_value(ethat->get_as_int());
      return;
   }

   THROW_ERROR_MSG(Type::IncompatibleAssignment,
                   "Cannot assign "<<that->type()->typestr()<<" to "<<this->typestr());
}

const std::string&
IntType::typestr()
const
{
   return iv_typestr;
}

int
IntType::compare(
   const Value* self,
   const Value* that)
const
{
   if (self == that)
      return 0;

   const IntValue* iself = this->const_narrow(self);
   assert(iself);

   const IntValue* ithat = dynamic_cast<const IntValue*>(that);
   if (ithat)
      return compare_int(iself->value(), ithat->value());

   const DoubleValue* dthat = dynamic_cast<const DoubleValue*>(that);
   if (dthat)
      return compare_double(iself->value(), dthat->value());

   const EnumerationValue* ethat = dynamic_cast<const EnumerationValue*>(that);
   if (ethat)
      return compare_int(iself->value(), ethat->get_as_int());

   THROW_ERROR_MSG(Type::IncompatibleComparison,
                   "Cannot compare "<<this->typestr()<<" with "<<that->type()->typestr());
}

Value*
IntType::shallow_copy(
   const Value* self)
const
{
   const IntValue* iself = dynamic_cast<const IntValue*>(self);
   assert(iself);
   return new IntValue(this, iself->value());
}

Value*
IntType::toplevel_copy(
   const Value* self)
const
{
   const IntValue* iself = dynamic_cast<const IntValue*>(self);
   assert(iself);
   return new IntValue(this, iself->value());
}

Value*
IntType::deep_copy(
   const Value* self)
const
{
   const IntValue* iself = dynamic_cast<const IntValue*>(self);
   assert(iself);
   return new IntValue(this, iself->value());
}

Value*
IntType::fit(
   Value* v)
const
{
   if (v->type() == this)
      return v;
   IntValue* iv = dynamic_cast<IntValue*>(v);
   if (iv)
      return new IntValue(this, iv->value());
   const EnumerationValue* ev = dynamic_cast<const EnumerationValue*>(v);
   if (ev)
      return new IntValue(this, ev->get_as_int());
   THROW_ERROR_MSG(Error, "Cannot convert "<<v->type()->typestr()<<" to "<<this->typestr());
}

void
IntType::convert_from(
   Value* self,
   const Value* that)
const
{
   IntValue* iself = dynamic_cast<IntValue*>(self);
   assert(iself);

   const IntValue* ithat = dynamic_cast<const IntValue*>(that);
   if (ithat) {
      iself->set_value(ithat->value());
      return;
   }
   const StringValue* sthat = dynamic_cast<const StringValue*>(that);
   if (sthat) {
      const char* str = sthat->value().c_str();
      int len = strlen(str);
      char* end;
      int i = strtol(str, &end, 10);
      if (end != str+len) {
         THROW_ERROR_MSG(Error, 
                         "Could not read integer value. "<<
                         end-str<<" characters of "<<len<<" were consumed, "
                         "buffer was \""<<str<<"\".");
      }
      iself->set_value(i);
      return;
   }
   const EnumerationValue* ethat = dynamic_cast<const EnumerationValue*>(that);
   if (ethat) {
      iself->set_value(ethat->get_as_int());
      return;
   }
   // hey, this is a quick hack. so don't complain about that
   // assertion.
   assert(0);
}

// --------------------------------------------------------------------
LTA_MEMDEF(DoubleType, 6, "$Id: type_simple.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

DoubleType::DoubleType()
: default_value_(this, 0.0)
{}

DoubleValue*
DoubleType::narrow(Value* v)
const
{
   return dynamic_cast<DoubleValue*>(v);
}

const DoubleValue*
DoubleType::const_narrow(
   const Value* v)
const
{
   return dynamic_cast<const DoubleValue*>(v);
}

const DoubleType*
DoubleType::instance()
{
   return init_tsingletons()->dt_singleton.cptr();
}

const SmartPtr<DoubleType>&
DoubleType::instanceP()
{
   return init_tsingletons()->dt_singleton;
}

DoubleValue*
DoubleType::create()
const
{
   return new DoubleValue(this, 0.0);
}

bool
DoubleType::can_assign(
   const Value& v,
   Error* e) const
{
   if (dynamic_cast<const DoubleValue*>(&v))
      return true;
   if (e) {
      COMPOSE_ERROR_MSG(Error, err, v.typestr()<<" "
                        "does not match "<<typestr());
      *e = err;
   }
   return false;
}

void
DoubleType::assign(
   Value* self,
   const Value* that)
const
{
   if (self == that) return;
   DoubleValue* dself = this->narrow(self);
   assert(dself);

   // we consider a DoubleValue which has a different DoubleType to be
   // assignable to self (iow, we do not use const_narrow(), but
   // rather dynamic_cast()). this is mainly for historical reasons
   // where there were different DoubleTypes with different formatting
   // parameters around.
   const DoubleValue* dthat = dynamic_cast<const DoubleValue*>(that);
   if (dthat) {
      dself->set_value(dthat->value());
      return;
   }
   // same rational for not using IntType::instance()->const_narrow()
   // at this point.
   const IntValue* ithat = dynamic_cast<const IntValue*>(that);
   if (ithat) {
      dself->set_value(ithat->value());
      return;
   }

   THROW_ERROR_MSG(Type::IncompatibleAssignment,
                   "Cannot assign "<<that->type()->typestr()<<" to double");
}

const std::string&
DoubleType::typestr()
const
{
   return dv_typestr;
}

int
DoubleType::compare(
   const Value* self,
   const Value* that)
const
{
   if (self == that)
      return 0;

   const DoubleValue* dself = this->const_narrow(self);
   assert(dself);

   const IntValue* ithat = dynamic_cast<const IntValue*>(that);
   if (ithat)
      return compare_double(dself->value(), ithat->value());

   const DoubleValue* dthat = dynamic_cast<const DoubleValue*>(that);
   if (dthat)
      return compare_double(dself->value(), dthat->value());

   THROW_ERROR_MSG(Type::IncompatibleComparison,
                   "Cannot compare "<<this->typestr()<<" with "<<that->type()->typestr());
}

Value*
DoubleType::shallow_copy(
   const Value* self)
const
{
   const DoubleValue* dself = dynamic_cast<const DoubleValue*>(self);
   assert(dself);
   return new DoubleValue(this, dself->value());
}

Value*
DoubleType::toplevel_copy(
   const Value* self)
const
{
   const DoubleValue* dself = dynamic_cast<const DoubleValue*>(self);
   assert(dself);
   return new DoubleValue(this, dself->value());
}

Value*
DoubleType::deep_copy(
   const Value* self)
const
{
   const DoubleValue* dself = dynamic_cast<const DoubleValue*>(self);
   assert(dself);
   return new DoubleValue(this, dself->value());
}

Value*
DoubleType::fit(
   Value* v)
const
{
   if (v->type() == this)
      return v;
   DoubleValue* dv = dynamic_cast<DoubleValue*>(v);
   if (dv)
      return new DoubleValue(this, dv->value());
   THROW_ERROR_MSG(Error, "Cannot convert "<<v->type()->typestr()<<" to "<<this->typestr());
}

// --------------------------------------------------------------------
LTA_MEMDEF(StringType, 4, "$Id: type_simple.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

StringType::StringType()
: default_value_(this, string())
{}

StringValue*
StringType::narrow(
   Value* v)
const
{
   return dynamic_cast<StringValue*>(v);
}

const StringValue*
StringType::const_narrow(
   const Value* v)
const
{
   return dynamic_cast<const StringValue*>(v);
}

const StringType*
StringType::instance()
{
   return init_tsingletons()->st_singleton.cptr();
}

const SmartPtr<StringType>&
StringType::instanceP()
{
   return init_tsingletons()->st_singleton;
}

StringValue*
StringType::create()
const
{
   return new StringValue(this, string());
}

bool
StringType::can_assign(
   const Value& v,
   Error* e)
const
{
   if (dynamic_cast<const StringValue*>(&v))
      return true;
   if (e) {
      COMPOSE_ERROR_MSG(Error, err, v.typestr()<<" "
                        "does not match "<<typestr());
      *e = err;
   }
   return false;
}

void
StringType::assign(
   Value* self,
   const Value* that)
const
{
   if (self == that) return;
   StringValue* sself = this->narrow(self);
   assert(sself);

   // we consider a StringValue which has a different StringType to be
   // assignable to self (iow, we do not use const_narrow(), but
   // rather dynamic_cast()). this is mainly for historical reasons
   // where there were different StringTypes with different formatting
   // parameters around.
   const StringValue* sthat = dynamic_cast<const StringValue*>(that);
   if (sthat) {
      sself->set_value(sthat->value());
      return;
   }
   const EnumerationValue* ethat = dynamic_cast<const EnumerationValue*>(that);
   if (ethat) {
      sself->set_value(ethat->get_as_string());
      return;
   }
   THROW_ERROR_MSG(Type::IncompatibleAssignment,
                   "Cannot assign "<<that->type()->typestr()<<" to string");
}

const std::string&
StringType::typestr()
const
{
   return sv_typestr;
}

int
StringType::compare(
   const Value* self,
   const Value* that)
const
{
   if (self == that)
      return 0;

   const StringValue* sself = this->const_narrow(self);
   assert(sself);

   const StringValue* sthat = dynamic_cast<const StringValue*>(that);
   if (sthat)
      return sself->value().compare(sthat->value());

   THROW_ERROR_MSG(Type::IncompatibleComparison,
                   "Cannot compare "<<this->typestr()<<" with "<<that->type()->typestr());
}

Value*
StringType::shallow_copy(
   const Value* self)
const
{
   const StringValue* sself = dynamic_cast<const StringValue*>(self);
   assert(sself);
   return new StringValue(this, sself->value());
}

Value*
StringType::toplevel_copy(
   const Value* self)
const
{
   const StringValue* sself = dynamic_cast<const StringValue*>(self);
   assert(sself);
   return new StringValue(this, sself->value());
}

Value*
StringType::deep_copy(
   const Value* self)
const
{
   const StringValue* sself = dynamic_cast<const StringValue*>(self);
   assert(sself);
   return new StringValue(this, sself->value());
}

Value*
StringType::fit(
   Value* v)
const
{
   if (v->type() == this)
      return v;
   StringValue* sv = dynamic_cast<StringValue*>(v);
   if (sv)
      return new StringValue(this, sv->value());
   const EnumerationValue* ev = dynamic_cast<const EnumerationValue*>(v);
   if (ev)
      return new StringValue(this, ev->get_as_string());
   THROW_ERROR_MSG(Error, "Cannot convert "<<v->type()->typestr()<<" to "<<this->typestr());
}

void
StringType::convert_from(
   Value* self,
   const Value* that)
const
{
   StringValue* sself = dynamic_cast<StringValue*>(self);
   assert(sself);
   
   const StringValue* sthat = dynamic_cast<const StringValue*>(that);
   if (sthat) {
      sself->set_value(sthat->value());
      return;
   }
   const IntValue* ithat = dynamic_cast<const IntValue*>(that);
   if (ithat) {
      int value = ithat->value();
      char buf[64];
      std::sprintf(buf, "%d", value);
      sself->set_value(buf);
      return;
   }
   const EnumerationValue* ethat = dynamic_cast<const EnumerationValue*>(that);
   if (ethat) {
      sself->set_value(ethat->get_as_string());
      return;
   }
   // hey, this is a quick hack. so don't complain about that
   // assertion.
   assert(0);
}

// --------------------------------------------------------------------
LTA_MEMDEF(BooleanType, 4, "$Id: type_simple.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

BooleanType::BooleanType()
: default_value_(this, false)
{}

BooleanValue*
BooleanType::narrow(Value* v)
const
{
   return dynamic_cast<BooleanValue*>(v);
}

const BooleanValue*
BooleanType::const_narrow(
   const Value* v)
const
{
   return dynamic_cast<const BooleanValue*>(v);
}

const BooleanType*
BooleanType::instance()
{
   return init_tsingletons()->bt_singleton.cptr();
}

const SmartPtr<BooleanType>&
BooleanType::instanceP()
{
   return init_tsingletons()->bt_singleton;
}

BooleanValue*
BooleanType::create()
const
{
   return new BooleanValue(this,false);
}

bool
BooleanType::can_assign(
   const Value& v,
   Error* e)
const
{
   if (dynamic_cast<const BooleanValue*>(&v))
      return true;
   if (e) {
      COMPOSE_ERROR_MSG(Error, err, v.typestr()<<" "
                        "does not match "<<typestr());
      *e = err;
   }
   return false;
}

void
BooleanType::assign(
   Value* self,
   const Value* that)
const
{
   if (self == that)
      return;

   BooleanValue* bself = dynamic_cast<BooleanValue*>(self);
   assert(bself);

   // we consider a BooleanValue which has a different BooleanType to
   // be assignable to self (iow, we do not use const_narrow(), but
   // rather dynamic_cast()). this is mainly for historical reasons
   // where there were different BooleanTypes with different
   // formatting parameters around.
   const BooleanValue* bthat = dynamic_cast<const BooleanValue*>(that);
   if (bthat) {
      bself->set_value(bthat->value());
      return;
   }

   THROW_ERROR_MSG(Type::IncompatibleAssignment,
                   "Cannot assign "<<that->type()->typestr()<<" to "<<this->typestr());
}

const std::string&
BooleanType::typestr()
const
{
   return bv_typestr;
}

int
BooleanType::compare(
   const Value* self,
   const Value* that)
const
{
   if (self == that) 
      return 0;
   
   const BooleanValue* bself = this->const_narrow(self);
   assert(bself);

   const BooleanValue* bthat = dynamic_cast<const BooleanValue*>(that);
   if (bthat)
      return bself->value() - bthat->value();

   THROW_ERROR_MSG(Type::IncompatibleComparison,
                   "Cannot compare "<<this->typestr()<<" with "<<that->type()->typestr());
}

Value*
BooleanType::shallow_copy(
   const Value* self)
const
{
   const BooleanValue* bself = dynamic_cast<const BooleanValue*>(self);
   assert(bself);
   return new BooleanValue(this, bself->value());
}

Value*
BooleanType::toplevel_copy(
   const Value* self)
const
{
   const BooleanValue* bself = dynamic_cast<const BooleanValue*>(self);
   assert(bself);
   return new BooleanValue(this, bself->value());
}

Value*
BooleanType::deep_copy(
   const Value* self)
const
{
   const BooleanValue* bself = dynamic_cast<const BooleanValue*>(self);
   assert(bself);
   return new BooleanValue(this, bself->value());
}

Value*
BooleanType::fit(
   Value* v)
const
{
   if (v->type() == this)
      return v;
   BooleanValue* bv = dynamic_cast<BooleanValue*>(v);
   if (bv)
      return new BooleanValue(this, bv->value());
   THROW_ERROR_MSG(Error, "Cannot convert "<<v->type()->typestr()<<" to "<<this->typestr());
}

} // /namespace
} // /namespace
