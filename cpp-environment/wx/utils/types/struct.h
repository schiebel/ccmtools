// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: struct.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#ifndef wx_utils_types_struct_h
#define wx_utils_types_struct_h

#include "struct_impl.h"
#include "value.h"

#include <string>

namespace WX {
namespace Utils {

/**

   \brief A map of name/value pairs
   
   \ingroup utils_types

   It works about as a Python dictionary in that the keys are strings
   and the values can be of arbitrary type (as long as the base type
   is a Value). The difference between a Python dictionary and a
   Struct is that a copy of a Python dictionary is in fact only a copy
   of the reference to the dictionary (which means that modifications
   are visible by everyone else who own a copy of the same
   dictionary). A copy of a Struct is a real copy, however, so it is
   safe to make modifications without disturbing others.

   As a side note, copying a Struct is fairly cheap because it is
   internally implemented as copy-on-write.

   \todo Performance: Inline a bit. Check how std::hash_map (?) works.

 */
class Struct : public SmartPtr<Struct_impl> {
public:
   /** Copy */

   void toplevel_copy(Struct&) const;
   void deep_copy(Struct&) const;

   /** Struct member access */

   //@{
   /** Set an attribute's value. The (the pointer to the) value is
       taken <I>as is</I>; no copy is performed whatsoever. This means
       that if the value is modified outside, the corresponding struct
       value will reflect that modification.

       \param name The name of the attribute

       \param value A smart pointer that points to a Value instance

   */
   void
   setP(
      const std::string& name,
      const SmartPtr<Value>& value);

   /** Set an attribute's value. The value is copied
       (shallow_copy()).

       \param name The name of the attribute

       \param value A concrete Value instance, passed as a reference
       to the base class

   */
   void
   set(
      const std::string& name,
      const Value& value)
   {
      setP(name, SmartPtr<Value>(value.shallow_copy()));
   }

   /** Get an attribute's value. If the attribute has been set, the
       smart pointer is returned that points to its value. If the
       attribute has not been set, a null smart pointer is
       returned.

       \param name The name of the attribute

   */
   const SmartPtr<Value>&
   getP(
      const std::string& name)
   const;
   
   /** Get an attribute's value. Same as above, but without the
       combined null-is-unset semantics. 

       \param name The name of the attribute

       \param value (Output parameter) The value of the
       attribute. Null if the function returns false, else it contains
       something meaningful.

       \returns true is the attribute has been set, else false.
       
   */
   bool
   getP(
      const std::string& name,
      SmartPtr<Value>& value)
   const;

   /** Get an attribute's value. If the attribute has been set, a
       reference to the value the smart pointer points to is returned,
       else an exception of is thrown.

       \param name The name of the attribute

       \return A reference to the value the smart pointer points to

       \throw WX::Utils::Error if the attribute hasn't been set

    */
   const Value& get(const std::string& name) const;

   /** Remove (unset) an attribute.

       \param name The name of the attribute

       \return The value that has been set, if any. Else NULL.

   */
   SmartPtr<Value> remove(const std::string& name);

   /** Unset all attributes. */
   void clear();

   //@}

   /** Iterator. */

   //@{
   int n() const;
   bool first(std::string&, SmartPtr<Value>&) const;
   bool next(std::string&, SmartPtr<Value>&) const;
   //@}

   /** Compare this with that, following the strcmp() semantics. This
       is, compare() returns and integer less than, equal to, or
       greater than zero if \a this is found to compare less than,
       equal to, or greater than \a that, respectively.

       Unlike string comparison, however, Struct comparison is more
       difficult and less obvious; it works as follows (let x, y be
       Struct instances). First, if x has less (more) items (members,
       keys) than y, then x is less (greater) than y. Next, if both
       have an equal number of keys, the lists of the keys of x and y
       are compared lexicographically. (These lists are lists of
       strings, where the strings are compared as defined in the C++
       standard.) Next, if both x and y have equal keys, the
       associated values are compared using their comparison
       methods. Note that this is the point where type mismatches can
       occur; you cannot compare an integer value with a string value,
       for example, let alone an integer value with a struct value. In
       such cases an exception will be thrown. */
   int compare(const Struct& that) const;

private:
   /** To be called internally, before a modifying operation. Needed
       because (a) an empty Struct has a NULL impl (which has to be
       allocated then), and (b) several Struct instances may share the
       same Struct_impl (which has to be copied then in order to not
       modify the other's view on it - a.k.a. "Copy On Write")*/
   void non_const_();
};

} // /namespace
} // /namespace


#endif
