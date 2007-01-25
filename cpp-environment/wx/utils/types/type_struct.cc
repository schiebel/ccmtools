// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: type_struct.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#include "type_struct.h"

#include "value_struct.h"

#include <WX/Utils/error_macros.h>

#include <algorithm>
#include <functional>
#include <cassert>

namespace WX {
namespace Utils {

LTA_MEMDEF(StructType, 3, "$Id: type_struct.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

using namespace std;

struct MembersEqual : 
   public std::binary_function<StructType::StructMember, StructType::StructMember, bool> {
   bool operator()(
      const StructType::StructMember& l,
      const StructType::StructMember& r)
   const
   {
      return l.first == r.first;
   }
};

static inline
void
is_unique(
   const StructType::StructMembers& members)
{
   StructType::StructMembers::const_iterator end = members.end();
   if (members.size() == 0)
      return;
   for (StructType::StructMembers::const_iterator i = members.begin();
        i != end-1;
        ++i) {
      if (std::find_if(i+1, end, std::bind1st(MembersEqual(), *i)) != end) {
         THROW_ERROR_MSG(StructType::DuplicateMembers, "Duplicate struct member \""<<i->first<<'"');
      }
   }
}

static const SmartPtr<Type> __wx_empty_type__;
static
const SmartPtr<Type>&
find_member_type(
   const StructType::StructMembers& mems,
   const string& name)
{
   StructType::StructMembers::const_iterator pos =
      find_if(mems.begin(), mems.end(), std::bind1st(MembersEqual(), make_pair(name, SmartPtr<Type>())));
   if (pos == mems.end())
      return __wx_empty_type__;
   return pos->second;
}

// static const std::string sv_typestr("struct");
static const StructType st_anonymous;
static const StructValue sv_default;

StructType::StructType(
   const std::string& name,
   const StructMembers& members)
: name_(name),
  members_(members)
{
   assert(name_.size());
   typestr_ = "struct ";
   typestr_ += name;
   is_unique(members);

   default_ = SmartPtr<StructValue>(new StructValue(this));
   defaultP_ = SmartPtr<Value>(default_.ptr());
}

// jjj

// StructType::StructType(
//    const std::string& name)
// : name_(name)
// {
//    assert(name_.size());
//    typestr_ = "struct ";
//    typestr_ += name;

//    default_ = SmartPtr<StructValue>(new StructValue(this));
//    defaultP_ = SmartPtr<Value>(default_.ptr());
// }

StructType::StructType()
{
   typestr_ = "anonymous struct";
   default_ = SmartPtr<StructValue>(new StructValue(this));
   defaultP_ = SmartPtr<Value>(default_.ptr());
}

// jjjj

// void
// StructType::add_member(
//    const std::string& name,
//    const SmartPtr<Type>& type)
// {
//    members_.push_back(make_pair(name, type));
//    is_unique(members_);
//    default_ = SmartPtr<StructValue>(new StructValue(this));
//    defaultP_ = SmartPtr<Value>(default_.ptr());
// }

const StructType*
StructType::anonymous()
{
   return &st_anonymous;
}

bool
StructType::first_member(
   string& name,
   SmartPtr<Type>& type)
const
{
   members_iterator_ = members_.begin();
   if (members_iterator_ == members_.end())
      return false;
   name = members_iterator_->first;
   type = members_iterator_->second;
   return true;
}

bool
StructType::next_member(
   string& name,
   SmartPtr<Type>& type)
const
{
   if (++members_iterator_ == members_.end())
      return false;
   name = members_iterator_->first;
   type = members_iterator_->second;
   return true;
}

const Type*
StructType::get_member_type(
   const string& member)
const
{
   for (StructMembers::const_iterator i = members_.begin();
        i != members_.end();
        ++i) {
      if (i->first == member)
         return i->second.cptr();
   }
   return NULL;
}

void
StructType::make_default(
   Struct& s)
const
{
   s = Struct();
   for (StructMembers::const_iterator i = this->members().begin();
        i != this->members().end();
        ++i)
      s.setP(i->first, SmartPtr<Value>(i->second.ptr()->default_value()->deep_copy()));
}

StructValue*
StructType::narrow(
   Value* v)
const
{
   if (v->type() != this)
      return NULL;
   StructValue* ret = dynamic_cast<StructValue*>(v);
   assert(ret); // paranoia
   return ret;
}

const StructValue*
StructType::const_narrow(
   const Value* v)
const
{
   return narrow(const_cast<Value*>(v));
}

StructValue*
StructType::create()
const
{
   return new StructValue(this);
}

bool
StructType::can_assign(
   const Value& v,
   Error* e)
const
{
   // any StructValue (no matter what type) can be assigned to an
   // anonymous struct.

   if (this->is_anonymous() && dynamic_cast<const StructValue*>(&v))
      return true;

   // StructValue instances of the same type (me) can be assigned to
   // each other (of course).

   if (this->const_narrow(&v) != NULL)
      return true;

   // an anonymous StructValue (one with no type) can be assigned to a
   // typed StructValue, as long as the members are defined in the
   // type of the target StructValue.

   {
      const StructValue* vthat = dynamic_cast<const StructValue*>(&v);
      const StructType* tthat = dynamic_cast<const StructType*>(v.type());
      // we checked above that the value is a StructValue, so we can
      // safely assert:
      assert(vthat);
      assert(tthat);
      
      if (tthat->is_anonymous()) {
         string name;
         SmartPtr<Value> value;
         bool rv = vthat->first(name, value);
         while (rv) {
            // I believe we do not permit members with null values, but
            // I'm not at all sure.
            assert(value);

            const Type* t = this->get_member_type(name);
            if (!t) {
               if (e) {
                  COMPOSE_ERROR_MSG(Error, why,
                                    "Member \""<<name<<"\" not defined in \""<<this->typestr()<<'"');
                  *e = why;
               }
               return false;
            }

            Error cannot_assign;
            if (!t->can_assign(*value.cptr(), &cannot_assign)) {
               if (e) {
                  COMPOSE_ERROR_ERROR_MSG(Error, why, cannot_assign,
                                          "Member \""<<name<<"\" cannot be assigned to");
                  *e = why;
               }
               return false;
            }
         
            rv = vthat->next(name, value);
         }
         return true;
      }
   }

   if (e) {
      COMPOSE_ERROR_MSG(Error, why, 
                        "Cannot assign \""<<v.type()->typestr()<<"\" to \""<<this->typestr()<<'"');
      *e = why;
   }
   return false;
}

void
StructType::assign(
   Value* self,
   const Value* that)
const
{
   if (that == self)
      return;

   StructValue* sself = dynamic_cast<StructValue*>(self);
   assert(sself); // who's been calling us, then?
   const StructValue* sthat = dynamic_cast<const StructValue*>(that);

   if (!sthat) {
      THROW_ERROR_MSG(Type::IncompatibleAssignment,
                      "Cannot assign "<<that->typestr()<<" to "<<self->typestr());
   }

   // any StructValue (no matter what type) can be assigned to an
   // anonymous struct.

   // StructValue instances of the same type (me) can be assigned to
   // each other (of course).

   if (this->is_anonymous() ||
       this->const_narrow(that) != NULL) {
      sself->value_ = sthat->value_;
      return;
   }

   // an anonymous StructValue (one with no type) can be assigned to a
   // typed StructValue (self), as long as the members are defined in
   // the type of the target StructValue.

   const StructType* tthat = dynamic_cast<const StructType*>(that->type());
   assert(tthat);

   if (tthat->is_anonymous()) {

      // first, see if that has any members that aren't defined in the
      // target type. refuse assignment in this case.
      {
         string name;
         SmartPtr<Value> value;
         bool rv = sthat->first(name, value);
         while (rv) {
            // I believe we do not permit members with null values, but
            // I'm not at all sure.
            assert(value);

            
            if (!this->get_member_type(name)) {
               THROW_ERROR_MSG(Type::IncompatibleAssignment,
                               "Member \""<<name<<"\" not defined in \""<<this->typestr()<<'"');
            }

            rv = sthat->next(name, value);
         }
      }

      for (StructMembers::const_iterator i = this->members().begin();
           i != this->members().end();
           ++i) {
         const string& memname = i->first;
         const SmartPtr<Type>& memtype = i->second;

         SmartPtr<Value> self_value = sself->getP(memname);

         // paranoia: we know that a typed struct (self) must have
         // all members defined. even though we know that, we
         // cowardly check it.
         assert(self_value);

         // assign from a meaningfule value if any, or assign from
         // the default value.

         const SmartPtr<Value>& that_value = sthat->getP(memname);
         if (that_value)
            memtype.cptr()->assign(self_value.ptr(), that_value.cptr());
         else
            memtype.cptr()->assign(self_value.ptr(), memtype.cptr()->default_value());
      }

      return;
   }

   THROW_ERROR_MSG(Type::IncompatibleAssignment,
                   "No idea how to assign "<<that->typestr()<<" to "<<self->typestr());   
}

const Value*
StructType::default_value()
const
{
   return default_.cptr();
}

const SmartPtr<Value>&
StructType::default_valueP()
const
{
   return defaultP_;
}

int
StructType::compare(
   const Value* self,
   const Value* that)
const
{
   // self must have the exact StructType (i.e., this - we have been
   // called by self, after all)
   const StructValue* sself = this->const_narrow(self);
   assert(sself);

   // that must have at least struct type.
   const StructValue* sthat = dynamic_cast<const StructValue*>(that);
   if (!sthat) {
      THROW_ERROR_MSG(Type::IncompatibleComparison,
                      "Cannot compare "<<this->typestr()<<" with "<<that->type()->typestr());
   }

   const StructType* that_type = dynamic_cast<const StructType*>(sthat->type());
   assert(that_type);

   // two structs are comparable if they have the same exact type, or
   // if at least one of them is an anonymous struct.
   if (!(this == that_type || this->is_anonymous() || that_type->is_anonymous())) {
      THROW_ERROR_MSG(Type::IncompatibleComparison,
                      "Cannot compare "<<this->typestr()<<" with "<<that_type->typestr());
   }

   // finally, go compare them. comparison is done using
   // Struct::compare().

   return sself->value_.compare(sthat->value_);
}

Value*
StructType::shallow_copy(
   const Value* self)
const
{
   const StructValue* sself = this->const_narrow(self);
   assert(sself);
   StructValue* ret = new StructValue(this);
   ret->value_ = sself->value_;
   return ret;
}

Value*
StructType::toplevel_copy(
   const Value* self)
const
{
   const StructValue* sself = this->const_narrow(self);
   assert(sself);
   StructValue* ret = new StructValue(this);
   sself->value_.toplevel_copy(ret->value_);
   return ret;
}

Value*
StructType::deep_copy(
   const Value* self)
const
{
   const StructValue* sself = this->const_narrow(self);
   assert(sself);
   StructValue* ret = new StructValue(this);
   sself->value_.deep_copy(ret->value_);
   return ret;
}

Value*
StructType::fit(
   Value* v)
const
{
   assert(v);
   if (v->type() == this)
      return v;

   if (this->can_assign(*v)) {
      try {
         StructValue* rv = new StructValue(this);
         this->assign(rv, v);
         return rv;
      }
      catch (const Error&) {
         assert(0);
      }
   }

   THROW_ERROR_MSG(Error, "Cannot fit "<<v->type()->typestr()<<" into "<<this->typestr());
}

const Value&
StructType::get(
   const StructValue* self,
   const std::string& name)
const
{
   if (!this->is_anonymous()) {
      const SmartPtr<Type>& t = find_member_type(members_, name);
      if (!t) {
         THROW_ERROR_MSG(NoSuchMember,
                         "No member \""<<name<<"\" in "<<this->typestr());
      }
   }

   try {
      return self->value_.get(name);
   }
   catch (const Error&) {

      // get() can only fail if the struct is of anonymous type. if it
      // has "strong type", then we guarantee to return a meaningful
      // value, simply because we fill it with its default members on
      // first set(), or because we retrieve the value from our
      // default_ instance if nothing was set() before.
      assert(this->is_anonymous());

      throw;
   }
}
 
const SmartPtr<Value>&
StructType::getP(
   const StructValue* self,
   const std::string& name)
const
{
   if (!this->is_anonymous()) {
      const SmartPtr<Type>& t = find_member_type(members_, name);
      if (!t) {
         THROW_ERROR_MSG(NoSuchMember,
                         "No member \""<<name<<"\" in "<<this->typestr());
      }
   }

   const SmartPtr<Value>& ret = self->value_.getP(name);

   // if this is not an anonymous struct, we insist in having a value
   // because we filled in the default struct with its default
   // members.
   assert(this->is_anonymous() || ret);

   return ret;
}

void
StructType::set(
   StructValue* self,
   const std::string& name,
   const Value& value)
const
{
   setP(self, name, SmartPtr<Value>(value.shallow_copy()));
}

void
StructType::setP(
   StructValue* self,
   const string& n,
   const SmartPtr<Value>& v)
const
{
   assert(v);

   Value* fit_v = v.ptr();
   assert(fit_v);

   // if self is not an anonymous struct, we insist in having the
   // member defined. the given value must fit with the member type.

   if (!this->is_anonymous()) {
      const SmartPtr<Type> t = find_member_type(members_, n);
      if (!t) {
         THROW_ERROR_MSG(StructType::NoSuchMember,
                         "No member "<<n<<" in "<<this->typestr());
      }

      try {
         fit_v = t.ptr()->fit(fit_v);
      }
      catch (const Error& e) {
         THROW_ERROR_ERROR_MSG(TypeMismatch, e,
                               "Cannot set member \""<<n<<'"');
      }
   }
   
   self->value_.setP(n, SmartPtr<Value>(fit_v));
}

int
StructType::n(
   const StructValue* self)
const
{
   return self->value_.n();
}

bool
StructType::first(
   const StructValue* self,
   std::string& name,
   SmartPtr<Value>& value)
const
{
   return self->value_.first(name, value);
}

bool
StructType::next(
   const StructValue* self,
   std::string& name,
   SmartPtr<Value>& value)
const
{
   return self->value_.next(name, value);
}

} // /namespace
} // /namespace
