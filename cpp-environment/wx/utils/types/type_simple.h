 // -*- mode: C++; c-basic-offset: 3 -*-
//
// $Id: type_simple.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#ifndef wx_utils_types_type_simple_h
#define wx_utils_types_type_simple_h

#include "type.h"
#include "value_simple.h"

#include <WX/Utils/linkassert.h>

#include <string>


namespace WX {
namespace Utils {

/**

   \brief Integer type

   \ingroup utils_types

 */
class IntType : public Type {
public:
   /** Boilerplate */
   //@{
   IntType();

   IntValue* narrow(Value*) const;
   const IntValue* const_narrow(const Value*) const;

   /** A singleton. One needs only one integer type instance; we
       provide it for convenience. */
   static const IntType* instance();
   static const SmartPtr<IntType>& instanceP();
   //@}

   /** Type interface */
   //@{
   virtual IntValue* create() const;
   virtual bool can_assign(const Value&, Error* =NULL) const;
   virtual void assign(Value* self, const Value* that) const;
   virtual const std::string& typestr() const;
   virtual int compare(const Value* self, const Value* that) const;
   virtual Value* shallow_copy(const Value* self) const;
   virtual Value* toplevel_copy(const Value* self) const;
   virtual Value* deep_copy(const Value* self) const;
   virtual Value* fit(Value*) const;

   /** The default value: an IntValue instance with value zero (0) */
   virtual const Value* default_value() const { return &default_value_; }

   void convert_from(Value* self, const Value* that) const;
   //@}

private:
   const IntValue default_value_;

public:
  LTA_MEMDECL(4);
};
LTA_STATDEF(IntType, 4);

/**

   \brief Floating point type (double)

   \ingroup utils_types

 */
class DoubleType : public Type {
public:
   /** Boilerplate */
   //@{
   DoubleType();

   DoubleValue* narrow(Value*) const;
   const DoubleValue* const_narrow(const Value*) const;

   /** Singleton */
   static const DoubleType* instance();
   static const SmartPtr<DoubleType>& instanceP();
   //@}

   /** Type interface */
   //@{
   virtual DoubleValue* create() const;
   virtual bool can_assign(const Value&, Error* =NULL) const;
   virtual void assign(Value* self, const Value* that) const;
   virtual const std::string& typestr() const;
   virtual int compare(const Value* self, const Value* that) const;
   virtual Value* shallow_copy(const Value* self) const;
   virtual Value* toplevel_copy(const Value* self) const;
   virtual Value* deep_copy(const Value* self) const;
   virtual Value* fit(Value*) const;

   /** The default value: an DoubleValue instance with value being zero
       (0.0) */
   virtual const Value* default_value() const { return &default_value_; }
   //@}

private:
   const DoubleValue default_value_;

public:
  LTA_MEMDECL(6);
};
LTA_STATDEF(DoubleType, 6);

/**

   \brief String type

   \ingroup utils_types

 */
class StringType : public Type {
public:
   /** Boilerplate */
   //@{
   StringType();

   StringValue* narrow(Value*) const;
   const StringValue* const_narrow(const Value*) const;

   /** Singleton */
   static const StringType* instance();
   static const SmartPtr<StringType>& instanceP();
   //@}

   /** Type interface. */
   //@{
   virtual StringValue* create() const;
   virtual bool can_assign(const Value&, Error* =NULL) const;
   virtual void assign(Value* self, const Value* that) const;
   virtual const std::string& typestr() const;
   virtual int compare(const Value* self, const Value* that) const;
   virtual Value* shallow_copy(const Value* self) const;
   virtual Value* toplevel_copy(const Value* self) const;
   virtual Value* deep_copy(const Value* self) const;
   virtual Value* fit(Value*) const;

   /** The default value: a StringValue instance with value being the
       empty string */
   virtual const Value* default_value() const { return &default_value_; }

   virtual void convert_from(Value* self, const Value* that) const;
   //@}

private:
   const StringValue default_value_;

public:
  LTA_MEMDECL(4);
};
LTA_STATDEF(StringType, 4);

/**

   \brief Boolean type

   \ingroup utils_types

 */
class BooleanType : public Type {
public:
   /** Boilerplate */
   //@{
   BooleanType();

   BooleanValue* narrow(Value*) const;
   const BooleanValue* const_narrow(const Value*) const;

   /** Singleton */
   static const BooleanType* instance();
   static const SmartPtr<BooleanType>& instanceP();
   //@}

   /** Type interface */
   //@{
   virtual BooleanValue* create() const;
   virtual bool can_assign(const Value&, Error* =NULL) const;
   virtual void assign(Value* self, const Value* that) const;
   virtual const std::string& typestr() const;
   virtual int compare(const Value* self, const Value* that) const;
   virtual Value* shallow_copy(const Value* self) const;
   virtual Value* toplevel_copy(const Value* self) const;
   virtual Value* deep_copy(const Value* self) const;
   virtual Value* fit(Value*) const;

   /** The default value: a BooleanValue instance with value being
       false */
   virtual const Value* default_value() const { return &default_value_; }
   //@}

private:
   const BooleanValue default_value_;
   
public:
  LTA_MEMDECL(4);
};
LTA_STATDEF(BooleanType, 4);

class Init_Globals {
public:
   Init_Globals();
};
static Init_Globals type_simple_init_globals;

} // /namespace
} // /namespace

#endif
