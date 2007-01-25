// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: type_enum.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#ifndef wx_utils_types_type_enum_h
#define wx_utils_types_type_enum_h

#include "type.h"
#include "value_enum.h"

#include <vector>
#include <string>

namespace WX {
namespace Utils {

/**

   \brief Enumeration type

   \ingroup utils_types

 */
class EnumerationType : public Type {
public:
   class OutOfRange : public Error {};

public:
   typedef std::vector<std::string> Members;
   enum { UndefinedValue = -1 };

public:
   /** Boilerplate */
   //@{
   /** Constructor.
       
       \param name The name of the enumeration

       \param members The members of the enumeration

    */
   EnumerationType(
      const std::string& name,
      const Members& members);

   virtual ~EnumerationType();

   EnumerationValue* narrow(Value*) const;
   const EnumerationValue* const_narrow(const Value*) const;
   //@}


   /** Type interface */
   //@{
   virtual EnumerationValue* create() const;
   virtual bool can_assign(const Value&, Error* =NULL) const;
   virtual void assign(Value* self, const Value* that) const;
   virtual const std::string& typestr() const { return typestr_; }
   virtual const Value* default_value() const;
   virtual const SmartPtr<Value>& default_valueP() const;
   /** Compare two enumeration values. One can only compare
       enumeration values of the same exact type. Comparison is done
       using their positional integer values. */
   virtual int compare(const Value* self, const Value* that) const;
   virtual Value* shallow_copy(const Value* self) const;
   virtual Value* toplevel_copy(const Value* self) const;
   virtual Value* deep_copy(const Value* self) const;
   virtual Value* fit(Value*) const;
   //@}


   /** My own interface */
   //@{
   /** The name of the enumeration type */
   const std::string& name() const { return name_; }

   /** The default value of the enumeration, as an integer (an index
       into the member list. */
   int default_int() const;
   /** The default value of the enumeration, as a string. */
   const std::string& default_string() const;

   int member_index(const std::string&) const;
   int member_index(int i) const;

   int n_members() const { return members_.size(); }

   bool first_member(int&, std::string& member_name) const;
   bool next_member(int&, std::string& member_name) const;

   bool first_member(std::string& member_name) const {
      return first_member(members_iter_, member_name);
   }
   bool next_member(std::string& member_name) const {
      return next_member(members_iter_, member_name);
   }

   int get_as_int(const EnumerationValue* self) const;
   const std::string& get_as_string(const EnumerationValue* self) const;
   void set_as_string(EnumerationValue* self, const std::string& value) const;
   void set_as_int(EnumerationValue* self, int value) const;
   //@}

private:
   const std::string name_;
   Members members_;
   mutable int members_iter_;
   SmartPtr<EnumerationValue> default_;
   SmartPtr<Value> defaultP_;
   std::string typestr_;

private:
   EnumerationType(const EnumerationType&);
   EnumerationType& operator=(const EnumerationType&);

public:
   LTA_MEMDECL(3);
};
LTA_STATDEF(EnumerationType, 3);

inline 
const std::string&
EnumerationType::default_string()
const
{
   return members_[default_int()];
}

} // /namespace
} // /namespace

#endif
