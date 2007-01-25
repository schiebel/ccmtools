// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: value_enum.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#ifndef wx_utils_types_value_enum_h
#define wx_utils_types_value_enum_h

#include "value.h"

namespace WX {
namespace Utils {

class EnumerationType;

/**

   \brief Enumeration value - instance of EnumerationType

   \ingroup utils_types

 */
class EnumerationValue : public Value {
public:
   /** Boilerplate */
   //@{
   EnumerationValue(const EnumerationType* type);
   EnumerationValue(const Value&);
   EnumerationValue(const EnumerationValue&);

   Value& operator=(const EnumerationValue& v) { Value::assign(v); return *this; }
   Value& operator=(const Value& v) { Value::assign(v); return *this; }
   //@}


   /** My own interface */
   //@{

   /** Set the enumeration value. The value must be a valid member of
       the enumeration's type. 

       \throw WX::Utils::Error if the value is not a valid enumeration
       member

   */
   void set_as_string(const std::string& value);

   /** Set the enumeration value. The value must be a valid index into
       the enumeration value's member vector.

       \throw WX::Utils::Error if the index is out of range

   */
   void set_as_int(int value);

   int get_as_int() const;
   const std::string& get_as_string() const;

   EnumerationValue* clone() const;
   //@}


   /** Interface used by my type */
   //@{
   int get_as_int_unchecked() const { return value_; }
   void set_as_int_unchecked(int i) { value_=i; }
   //@}

private:
   int value_;

public:
   LTA_MEMDECL(1);
};
LTA_STATDEF(EnumerationValue, 1);


} // /namespace
} // /namespace

#endif
