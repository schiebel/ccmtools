// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: type_enum.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#include "type_enum.h"

#include "value_enum.h"
#include "value_simple.h"
#include "type_simple.h"

#include <WX/Utils/error_macros.h>

#include <vector>
#include <cassert>

namespace WX {
namespace Utils {

using namespace std;

LTA_MEMDEF(EnumerationType, 3, "$Id: type_enum.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

EnumerationType::EnumerationType(
   const string& name,
   const vector<string>& members)
: name_(name),
  members_(members)
{
   default_ = SmartPtr<EnumerationValue>(new EnumerationValue(this));
   default_.ptr()->set_as_int_unchecked(members_.size()? 0: UndefinedValue);

   defaultP_ = SmartPtr<Value>(default_.ptr());

   typestr_ = "enum ";
   typestr_ += name;
}

EnumerationType::~EnumerationType() {}

EnumerationValue*
EnumerationType::narrow(
   Value* v)
const
{
   // if the types aren't equal, we save cycles as there is no need to
   // downcast.
   if (v->type() != this)
      return NULL;

   EnumerationValue* rv = dynamic_cast<EnumerationValue*>(v);
   assert(rv); // the types were equal
   return rv;
}

const EnumerationValue*
EnumerationType::const_narrow(
   const Value* v)
const
{
   return narrow(const_cast<Value*>(v));
}

EnumerationValue*
EnumerationType::create()
const
{
   return new EnumerationValue(this);
}


bool
EnumerationType::can_assign(
   const Value& v,
   Error* e)
const
{
   {
      const IntValue* iv = IntType::instance()->const_narrow(&v);
      if (iv) {
         if (member_index(iv->value()) != UndefinedValue)
            return true;
         if (e) {
            COMPOSE_ERROR_MSG(Error, err, 
                              "Cannot assign integer value "
                              <<iv->value()<<" to enumeration type "
                              <<this->typestr()<<" because "<<iv->value()<<" "
                              "is not a valid enumeration member index");
            *e = err;
         }
         return false;
      }
   }

   {
      const StringValue* sv = StringType::instance()->const_narrow(&v);
      if (sv) {
         if (member_index(sv->value()) != UndefinedValue)
            return true;
         if (e) {
            COMPOSE_ERROR_MSG(Error, err, 
                              "Cannot assign string value "
                              <<sv->value()<<" to enumeration type "
                              <<this->typestr()<<" because "<<sv->value()<<" "
                              "is not a valid enumeration member");
            *e = err;
         }
         return false;
      }
   }

   {
      const EnumerationValue* ev = this->const_narrow(&v);
      if (ev)
         return true;
      if (e) {
         COMPOSE_ERROR_MSG(Error, err,
                           "Cannot assign "<<v.typestr()<<" to "
                           "enumeration type "<<this->typestr());
         *e = err;
      }
      return false;
   }

   if (e) {
      COMPOSE_ERROR_MSG(Error, err,
                        "Cannot assign "<<v.typestr()<<" to "
                        "enumeration type "<<this->typestr());
      *e = err;
   }
   return false;
}

void
EnumerationType::assign(
   Value* self,
   const Value* that)
const 
{
   if (that == self)
      return;

   // downcast the target value
   EnumerationValue* target = narrow(self);
   assert(target);

   {
      const IntValue* iv = IntType::instance()->const_narrow(that);
      if (iv) {
         try {
            set_as_int(target, iv->value());
         }
         catch (const OutOfRange& e) {
            THROW_ERROR_ERROR_MSG(OutOfRange, e,
                                  "Assignment from IntValue(=="<<iv->value()<<") "
                                  "to EnumerationValue \""<<name_<<"\" failed");
         }
         catch (const Error& e) {
            THROW_ERROR_ERROR_MSG(Error, e,
                                  "Assignment from IntValue(=="<<iv->value()<<") "
                                  "to EnumerationValue \""<<name_<<"\" failed");
         }
         return;
      }
   }

   {
      const StringValue* sv = StringType::instance()->const_narrow(that);
      if (sv) {
         try {
            set_as_string(target, sv->value());
         }
         catch (const OutOfRange& e) {
            THROW_ERROR_ERROR_MSG(OutOfRange, e, 
                                  "Assignment from StringValue(\""<<sv->value()<<"\") "
                                  "to EnumerationValue of type \""<<name_<<"\" failed");
         }
         catch (const Error& e) {
            THROW_ERROR_ERROR_MSG(Error, e, 
                                  "Assignment from StringValue(\""<<sv->value()<<"\") "
                                  "to EnumerationValue of type \""<<name_<<"\" failed");
         }
         return;
      }
   }

   {
      const EnumerationValue* ev = dynamic_cast<const EnumerationValue*>(that);
      if (!ev) {
         THROW_ERROR_MSG(Error, "Cannot assign "<<that->typestr()<<" to "<<typestr());
      }
      if (this != ev->type()) {
         THROW_ERROR_MSG(Error, 
                         "Assignment of incompatible enumeration values: "<<
                         typestr()<<'='<<ev->type()->typestr());
      }
      target->set_as_int_unchecked(ev->get_as_int());
      return;
   }
}

const Value*
EnumerationType::default_value()
const
{
   return default_.cptr();
}

const SmartPtr<Value>&
EnumerationType::default_valueP()
const
{
   return defaultP_;
}

int
EnumerationType::compare(
   const Value* self,
   const Value* that)
const
{
   const EnumerationValue* eself = this->const_narrow(self);
   assert(eself);
   const EnumerationValue* ethat = this->const_narrow(that);
   if (!ethat) {
      THROW_ERROR_MSG(Type::IncompatibleComparison,
                      "Bad comparison: right hand side operand"
                      "is not of type \""<<this->typestr()<<'"');
   }
   return eself->get_as_int_unchecked() - ethat->get_as_int_unchecked();
}

Value*
EnumerationType::shallow_copy(
   const Value* self)
const
{
   const EnumerationValue* eself = dynamic_cast<const EnumerationValue*>(self);
   assert(eself);
   return eself->clone();
}

Value*
EnumerationType::toplevel_copy(
   const Value* self)
const
{
   const EnumerationValue* eself = dynamic_cast<const EnumerationValue*>(self);
   assert(eself);
   return eself->clone();
}

Value*
EnumerationType::deep_copy(
   const Value* self)
const
{
   const EnumerationValue* eself = dynamic_cast<const EnumerationValue*>(self);
   assert(eself);
   return eself->clone();
}

Value*
EnumerationType::fit(
   Value* v)
const
{
   if (v->type() == this) 
      return v;

   {
      const IntValue* iv = dynamic_cast<const IntValue*>(v);
      if (iv) {
         EnumerationValue* ret = new EnumerationValue(this);
         try {
            ret->set_as_int(iv->value());
         }
         catch (const Error& e) {
            delete ret;
            THROW_ERROR_ERROR_MSG(Error, e,
                                  "Cannot convert "<<v->typestr()<<'('<<iv->value()<<") "
                                  "to "<<this->typestr());
         }
         return ret;
      }
   }

   {
      const StringValue* sv = dynamic_cast<const StringValue*>(v);
      if (sv) {
         EnumerationValue* ret = new EnumerationValue(this);
         try {
            ret->set_as_string(sv->value());
         }
         catch (const Error& e) {
            delete ret;
            THROW_ERROR_ERROR_MSG(Error,e, 
                            "Cannot convert "<<v->typestr()<<'('<<sv->value()<<") "
                            "to "<<this->typestr());
         }
         return ret;
      }
   }

   THROW_ERROR_MSG(Error,
                   "Cannot convert object of type "<<
                   v->type()->typestr()<<" to "<<this->typestr());
}

int
EnumerationType::default_int()
const
{
   if (!members_.size()) {
      THROW_ERROR_MSG(Error, "No default value available for "
                      "enumeration type \""<<name_<<"\" because "
                      "it has no members");
   }
   return 0;
}

int
EnumerationType::member_index(
   const string& s)
const
{
   for (int i=0; i<(int)members_.size(); i++)
      if (members_[i] == s)
         return i;
   return UndefinedValue;
}

int
EnumerationType::member_index(
   int i)
const
{
   return (i>=0&&i<(int)members_.size())? i: UndefinedValue;
}

bool
EnumerationType::first_member(
   int& iter,
   std::string& member_name)
const
{
   if (members_.size() > 0) {
      iter = 0;
      member_name = members_[iter];
      return true;
   }
   return false;
}

bool
EnumerationType::next_member(
   int& iter,
   std::string& member_name)
const
{
   if (++iter < (int)members_.size()) {
      member_name = members_[iter];
      return true;
   }
   return false;
}

int
EnumerationType::get_as_int(
   const EnumerationValue* self)
const
{
   return member_index(self->get_as_int_unchecked());
}

const std::string&
EnumerationType::get_as_string(
   const EnumerationValue* self)
const
{
   int i = member_index(self->get_as_int_unchecked());
   if (i == UndefinedValue) {
      THROW_ERROR_MSG(Error,
                      "Cannot get enumeration value as string: "
                      "value is uninitialized");
   }
   return members_[i];
}

void
EnumerationType::set_as_string(
   EnumerationValue* self,
   const string& value)
const
{
   int i = member_index(value);
   if (i == UndefinedValue) {
      THROW_ERROR_MSG(OutOfRange, '"'<<value<<"\" is not a member of type "<<typestr());
   }
   self->set_as_int_unchecked(i);
}

void
EnumerationType::set_as_int(
   EnumerationValue* self,
   int value)
const
{
   int i = member_index(value);
   if (i == UndefinedValue) {
      THROW_ERROR_MSG(OutOfRange, '"'<<value<<"\" is not a "
                      "valid member index of type "<<typestr());
   }
   self->set_as_int_unchecked(i);
}

} // /namespace
} // /namespace
