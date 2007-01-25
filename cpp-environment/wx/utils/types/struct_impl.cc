// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: struct_impl.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#include "struct.h"

#include <WX/Utils/error.h>

#include <map>
#include <vector>
#include <algorithm>
#include <cassert>

using namespace std;

// --------------------------------------------------------------------
namespace WX {
namespace Utils {

struct  Struct_impl_map : public std::map<std::string, SmartPtr<Value> > {
   mutable Struct_impl_map::const_iterator iter;
};
static inline Struct_impl_map* make_map(void* m) {
   return static_cast<Struct_impl_map*>(m);
}

LTA_MEMDEF(Struct_impl, 1, "$Id: struct_impl.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

const SmartPtr<Value> Struct_impl::empty_value;

Struct_impl::Struct_impl()
: map_(new Struct_impl_map) {}

Struct_impl::~Struct_impl() {
   delete make_map(map_);
}

Struct_impl*
Struct_impl::toplevel_copy()
const
{
   Struct_impl* impl = new Struct_impl;
   string name;
   SmartPtr<Value> v;
   bool rv = first(name, v);
   while (rv) {
      assert(v);
      impl->setP(name, SmartPtr<Value>(v.ptr()->shallow_copy()));
   }
   return impl;
}

Struct_impl*
Struct_impl::deep_copy()
const
{
   Struct_impl* impl = new Struct_impl;
   string name;
   SmartPtr<Value> v;
   bool rv = first(name, v);
   while (rv) {
      assert(v);
      impl->setP(name, SmartPtr<Value>(v.ptr()->deep_copy()));
      rv = next(name, v);
   }
   return impl;
}

void
Struct_impl::setP(
   const string& n,
   const SmartPtr<Value>& v)
{
   assert(v);
   remove(n);
   make_map(map_)->insert(make_pair(n, v));
}

SmartPtr<Value>
Struct_impl::remove(
   const string& n)
{
   Struct_impl_map::iterator pos = make_map(map_)->find(n);
   SmartPtr<Value> ret;
   if (pos != make_map(map_)->end()) {
      ret = pos->second;
      assert(ret);
      make_map(map_)->erase(pos);
   }
   return ret;
}

const Value&
Struct_impl::get(
   const string& name)
const
{
   const SmartPtr<Value>& vp = getP(name);
   if (!vp) {
      THROW_ERROR_MSG(Error, "Value of attribute \""<<name<<"\" is nil");
   }
   return *vp.cptr();
}

const SmartPtr<Value>&
Struct_impl::getP(
   const string& n)
const
{
   map<string, SmartPtr<Value> >::const_iterator i = make_map(map_)->find(n);
   if (i == make_map(map_)->end())
      return empty_value;
   return i->second;
}

bool
Struct_impl::getP(
   const string& n,
   SmartPtr<Value>& v)
const {
   map<string, SmartPtr<Value> >::const_iterator i = make_map(map_)->find(n);
   if (i == make_map(map_)->end())
      return false;
   v = i->second;
   return true;
}

int
Struct_impl::n()
const
{
   return static_cast<int>(make_map(map_)->size());
}

bool
Struct_impl::first(
   string& n,
   SmartPtr<Value>& v)
const
{
   make_map(map_)->iter = make_map(map_)->begin();
   if (make_map(map_)->iter == make_map(map_)->end())
      return false;
   n = (*make_map(map_)->iter).first;
   v = (*make_map(map_)->iter).second;
   return true;
}

bool
Struct_impl::next(
   string& n,
   SmartPtr<Value>& v)
const
{
   if (++make_map(map_)->iter == make_map(map_)->end()) return false;
   n = (*make_map(map_)->iter).first;
   v = (*make_map(map_)->iter).second;
   return true;
}

void
Struct_impl::clear()
{
   make_map(map_)->clear();
}

int
Struct_impl::compare(
   const Struct_impl& that)
const
{
   // first compare lengths, saving lotsa cycles.

   if (n() < that.n()) return -1;
   if (n() > that.n()) return 1;

   // bad news ...

   // in order to reliably compare two Struct's, we have to compare in
   // the same order every time. so, collect the keys, sort them, ...

   vector<string> this_keys;
   vector<string> that_keys;
   {
      string key;
      SmartPtr<Value> value;
      bool rv = this->first(key, value);
      while (rv) {
         this_keys.push_back(key);
         rv = this->next(key, value);
      }
      sort(this_keys.begin(), this_keys.end());
   }
   {
      string key;
      SmartPtr<Value> value;
      bool rv = that.first(key, value);
      while (rv) {
         that_keys.push_back(key);
         rv = that.next(key, value);
      }
      sort(that_keys.begin(), that_keys.end());
   }

   // now that we have extracted the keys, compare them
   // lexicographically.

   {
      vector<string>::const_iterator ithis = this_keys.begin();
      vector<string>::const_iterator ithat = that_keys.begin();
      while (ithis != this_keys.end()) {
         int rv = ithis->compare(*ithat);
         if (rv != 0)
            return rv;
         ++ithis;
         ++ithat;
      }
   }

   // more bad news: still not different. they even have the same
   // keys. compare the values of each key in order.
   {
      bool have_result = false;
      int result = 0;
      ErrorTrace collected_errors;

      vector<string>::const_iterator ithis = this_keys.begin();
      while (ithis != this_keys.end()) {
         int local_result;

         SmartPtr<Value> this_value, that_value;
         bool rv = getP(*ithis, this_value); assert(rv);
         rv = that.getP(*ithis, that_value); assert(rv);
         assert(this_value);
         assert(that_value);

         try {
            local_result = this_value.ptr()->compare(*that_value.ptr());
            // if we have a result already, we continue only to check if
            // there are errors (paranoia satisfaction, so to say). we
            // could stop the iteration as well and save lotsa cycles,
            // but, being paranoid...
         
            // if we don't have one, check if there is one.
         
            if (!have_result) {
               if (local_result != 0) {
                  have_result = true;
                  result = local_result;
               }
            }
         }
         catch (const Error& e) {
            ErrorTrace et;
            et.append(e);
            COMPOSE_ERROR_TRACE_MSG(
               Error, 
               er, et,
               "Cannot compare values with name \""<<*ithis<<'"');
            collected_errors.append(er);
         }
         
         ++ithis;
      }
      if (collected_errors.trace().size()) {
         THROW_ERROR_TRACE_MSG(
            Error, 
            collected_errors,
            "Cannot compare structs");
      }

      // if we don't have_result, we fell off the end which means
      // "equal".
      return have_result? result: 0;
   }
}

} // /namespace
} // /namespace
