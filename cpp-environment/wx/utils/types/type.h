// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: type.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#ifndef wx_utils_types_type_h
#define wx_utils_types_type_h

#include "properties_fwd.h"

#include <WX/Utils/linkassert.h>
#include <WX/Utils/smartptr.h>
#include <WX/Utils/error.h>

#include <string>

namespace WX {
namespace Utils {

class Value;

/**

\brief Type definition interface

\ingroup utils_types

*/
class Type : public WX::Utils::RefCounted {
public:
   class IncompatibleAssignment : public Error {};
   class IncompatibleComparison : public Error {};
   class IncompatibleConversion : public Error {};

public:
   virtual ~Type();

   /** The official "constructor" for a Value instance. Derived
       classes are should implement this to return their concrete type
       (as long as the C++ compiler supports covariant return
       types). */ 
   virtual Value* create() const = 0;

   /** Can the value be assigned to an instance of this? If not, and
       an Error object is given, this will be filled with a meaningful
       message (outside you don't know about the details why the
       assignment cannot be made, so this is sometimes needed). */
   virtual bool can_assign(const Value&, Error* =NULL) const = 0;

   /**
      Assign that to \a self. \a self must be an instance of this.

      \throw Type::IncompatibleAssignment that cannot be assigned to
      self because their types are incompatible.
   */
   virtual void assign(Value* self, const Value* that) const = 0;

   /** Fit the given value to have myself as type. This can either
       create a new Value instance, or simply return the given value
       if that has already the right type. For example, if the given
       value is an enumeration value (EnumerationValue), and I am an
       integer type (IntType), I create a new integer value that has
       the enumeration's integer value. If the given value is already
       an integer value, then I return that since it already fits. */
   virtual Value* fit(Value*) const = 0;

   /** String description of this type. Should only be used for
       informational purposes, such as exception messages. */
   virtual const std::string& typestr() const = 0;

   virtual Value* shallow_copy(const Value* self) const = 0;
   virtual Value* toplevel_copy(const Value* self) const = 0;
   virtual Value* deep_copy(const Value* self) const = 0;

   /** The default value of the concrete type */
   virtual const Value* default_value() const = 0;

   /**
      Compare self with that.

      \return an integer less than, equal to, or greater than zero if
      self is found, respectively, to be less than, to match, or be
      greater than that. */

   virtual int compare(const Value* self, const Value* that) const = 0;

   /** Property interface */
   //@{
   
   /** Throws Error if property with key is already set. */
   void set_property(const std::string& key,
                     const std::string& value);
   /** Returns false if key is not set. */
   bool get_property(const std::string& key, std::string& value) const;
   /** Throws Error if key is not set. */
   const std::string& get_property(const std::string& key) const;
   
   //@}

   // HACK ALERT!
   virtual void convert_from(Value* self, const Value* that) const;

protected:
   Type() : properties_(NULL) {}

private:
   Properties* properties_;

private:
   friend class anti__only_defines_private_constructors_and_has_no_friends;
   Type(const Type&);
   const Type& operator=(const Type&);

public:
   LTA_MEMDECL(7);
};
LTA_STATDEF(Type, 7);

}// /namespace
}// /namespace

#endif
