// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: type_struct.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#ifndef wx_utils_types_type_struct_h
#define wx_utils_types_type_struct_h

#include "type.h"
#include "value_struct.h"

#include <WX/Utils/error.h>
#include <WX/Utils/linkassert.h>

#include <vector>

namespace WX {
namespace Utils {

/**
   
   \brief Struct

   \ingroup utils_types

   \todo implement struct type \em meaningfully

 */
class StructType : public Type {
public:
   class DuplicateMembers : public Error {};
   class NoSuchMember : public Error {};
   class TypeMismatch : public Error {};

public:
   typedef std::pair<std::string, SmartPtr<Type> > StructMember;
   typedef std::vector<StructMember> StructMembers;

public:
   /** Boilerplate */
   //@{
   /** Constructor.

   \param name The name of the type

   \param members An array of name/type pairs

   */
   StructType(
      const std::string& name,
      const StructMembers& members);

   // jjjj
//    StructType(
//       const std::string& name);

   StructType();

   const std::string& name() const { return name_; }

   StructValue* narrow(Value*) const;
   const StructValue* const_narrow(const Value*) const;
   //@}


   /** Type interface */
   //@{
   virtual StructValue* create() const;
   virtual bool can_assign(const Value&, Error* =NULL) const;
   virtual void assign(Value* self, const Value* that) const;
   virtual Value* fit(Value*) const;
   virtual const std::string& typestr() const { return typestr_; }
   virtual const Value* default_value() const;
   virtual const SmartPtr<Value>& default_valueP() const;
   virtual int compare(const Value* self, const Value* that) const;
   virtual Value* shallow_copy(const Value* self) const;
   virtual Value* toplevel_copy(const Value* self) const;
   virtual Value* deep_copy(const Value* self) const;
   //@}


   /** My own interface */
   //@{

   /** Incremental construction.

   @todo This is very expensive because the default value is
   calculated everytime a new member is added.
   */
   // jjjj void add_member(const std::string& name, const SmartPtr<Type>& type);

   /** Get singleton which holds the description of the anonymous
       struct. An anonymous struct can have any members of any
       type. */
   static const StructType* anonymous();

   /** Official way to determine if this is the type of an anonymous
       struct. */
   bool is_anonymous() const { return name_.size() == 0; }

   /** Struct members access method. Preferably only used in rare
       circumstances because the implementation may likely change. */
   const StructMembers& members() const { return members_; }

   /** Iterating over members */
   //@{
   bool first_member(std::string& name, SmartPtr<Type>& type) const;
   bool next_member(std::string& name, SmartPtr<Type>& type) const;
   //@}

   /** Get the type of a member.
       
   @return NULL if the member is not defined.
   */
   const Type* get_member_type(const std::string& member) const;

   /** Fill the given Struct instance with default values, following
       the member specification. */
   void make_default(Struct&) const;
   //@}


   /** Value manipulation */
   //@{
   const Value& get(const StructValue* self, const std::string& name) const;
   const SmartPtr<Value>& getP(const StructValue* self, const std::string& name) const;
   void set(StructValue* self, const std::string& name, const Value& value) const;
   void setP(StructValue* self, const std::string& name, const SmartPtr<Value>& value) const;

   int n(const StructValue* self) const;
   bool first(const StructValue* self, std::string& name, SmartPtr<Value>& value) const;
   bool next(const StructValue* self, std::string& name, SmartPtr<Value>& value) const;
   //@}

private:
   const std::string name_;
   mutable std::string typestr_;

   StructMembers members_;
   mutable StructMembers::const_iterator members_iterator_;

   SmartPtr<StructValue> default_;
   SmartPtr<Value> defaultP_;
   
public:
   LTA_MEMDECL(3);
};
LTA_STATDEF(StructType, 3);

} // /namespace
} // /namespace

#endif
