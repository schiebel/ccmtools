// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: struct.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#include "struct.h"

#include <WX/Utils/error_macros.h>

#include <vector>
#include <algorithm>
#include <cassert>

using namespace std;

// --------------------------------------------------------------------
namespace WX {
namespace Utils {

void
Struct::setP(
   const string& n,
   const SmartPtr<Value>& v)
{
   non_const_();
   ptr()->setP(n, v);
}

SmartPtr<Value>
Struct::remove(
   const string& n)
{
   if (ptr()) {
      non_const_();
      return ptr()->remove(n);
   }
   else
      return Struct_impl::empty_value;
}

const Value&
Struct::get(
   const string& name)
const
{
   if (ptr())
      return ptr()->get(name);
   else {
      THROW_ERROR_MSG(Error, "Value of attribute \""<<name<<"\" is nil");
   }
}

const SmartPtr<Value>&
Struct::getP(
   const string& n)
const
{
   if (ptr())
      return ptr()->getP(n);
   else
      return Struct_impl::empty_value;
}

bool
Struct::getP(
   const string& n,
   SmartPtr<Value>& v)
const
{
   if (ptr())
      return ptr()->getP(n, v);
   else
      return false;
}

int
Struct::n()
const
{
   if (ptr())
      return ptr()->n();
   else
      return 0;
}

bool
Struct::first(
   string& n,
   SmartPtr<Value>& v)
const
{
   if (ptr())
      return ptr()->first(n, v);
   else
      return false;
}

bool
Struct::next(
   string& n,
   SmartPtr<Value>& v)
const
{
   // one must not call next() when first() has failed.
   assert(ptr());
   return ptr()->next(n, v);
}

void
Struct::clear() {
   if (ptr()) {
      // If we are not the only owner of our impl, simply decrement
      // the refcount and set ours to null, as if we never had an
      // impl. If we are the only owner of the impl then only clear()
      // it since that may perform better in the general
      // case. Rationale: if our user explicitly clear()s us we assume
      // he intends to re-fill us, so it is better to leave clear() to
      // the map implementation instead of destroying the map
      // unconditionally.
      if (ptr()->refcount() > 1)
         forget();
      else
         ptr()->clear();
   }
}

void
Struct::toplevel_copy(
   Struct& s)
const
{
   if (ptr())
      s.eat(ptr()->toplevel_copy());
   else
      s = Struct();
}

void
Struct::deep_copy(
   Struct& s)
const
{
   if (ptr())
      s.eat(ptr()->deep_copy());
   else
      s = Struct();
}

int
Struct::compare(const Struct& that) const {
   if (ptr()) {
      if (that.ptr())
         return ptr()->compare(*that.ptr());
      else {
         if (ptr()->n() > 0)
            return 1;
         else
            return 0;
      }
   }
   else {
      if (!that.ptr())
         return 0;
      else {
         if (that.ptr()->n() > 0)
            return -1;
         else
            return 0;
      }
   }
}

void
Struct::non_const_()
{
   if (ptr()) {
      if (ptr()->refcount() > 1)
         eat(ptr()->deep_copy());
   }
   else
      eat(new Struct_impl);
}

} // /namespace
} // /namespace
