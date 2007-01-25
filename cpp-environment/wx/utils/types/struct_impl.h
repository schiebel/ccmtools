// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: struct_impl.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#ifndef wx_utils_types_struct_impl_h
#define wx_utils_types_struct_impl_h

#include "value.h"

#include <WX/Utils/smartptr.h>
#include <WX/Utils/linkassert.h>

namespace WX {
namespace Utils {

class Struct_impl : public RefCounted {
public:
   static const SmartPtr<Value> empty_value;

public:
   Struct_impl();
   ~Struct_impl();

   /** Copy operations. note that there is no shallow_copy() because a
       shallow_copy() is done by simply incrementing the refcount of
       this. and this is done outside. */
   //@{
   /** A toplevel copy is implemented in terms of a shallow copy of
       each of the toplevel elements */
   Struct_impl* toplevel_copy() const;
   /** A deep copy is a real deep recursive copy */
   Struct_impl* deep_copy() const;
   //@}

   void
   setP(
      const std::string& name,
      const SmartPtr<Value>& value);
   void
   set(const std::string& name, const Value& value) {
      setP(name, SmartPtr<Value>(value.shallow_copy()));
   }

   const SmartPtr<Value>&
   getP(
      const std::string& name)
   const;
   bool
   getP(
      const std::string& name,
      SmartPtr<Value>& value)
   const;
   const Value& get(const std::string& name) const;

   SmartPtr<Value> remove(const std::string& name);

   void clear();

   int n() const;
   bool first(std::string&, SmartPtr<Value>&) const;
   bool next(std::string&, SmartPtr<Value>&) const;

   int compare(const Struct_impl& that) const;

private:
   void* map_;

public:
   LTA_MEMDECL(1);
};
LTA_STATDEF(Struct_impl, 1);

} // /namespace
} // /namespace

#endif
