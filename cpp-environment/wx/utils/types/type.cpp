// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: type.cpp,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#include "type.h"

#include "properties.h"
#include "value.h"

#include <WX/Utils/error_macros.h>

#include <map>
#include <cassert>

using namespace std;

namespace WX {
namespace Utils {

static const string empty_string;

LTA_MEMDEF(Type, 7, "$Id: type.cpp,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

Type::~Type()
{
   delete properties_;
}

void
Type::set_property(
   const string& key,
   const string& value)
{
   if (!properties_)
      properties_ = new Properties;
   try {
      properties_->set(key, value);
   }
   catch (const Error& e) {
      THROW_ERROR_ERROR_MSG(Error, e, "Type \""<<this->typestr()<<"\": "
                            "cannot set property \""<<key<<'"');
   }
}

bool
Type::get_property(
   const string& key,
   string& v)
const
{
   if (!properties_)
      return false;
   return properties_->get(key, v);
}

const string&
Type::get_property(
   const std::string& key)
const
{
   const string* value;
   if (!properties_ || (value = properties_->get(key)) == NULL) {
      THROW_ERROR_MSG(Error, "Type \""<<this->typestr()<<"\": "
                      "no property \""<<key<<'"');
   }
   return *value;
}

void
Type::convert_from(
   Value* self,
   const Value* that)
const
{
   assert(self->type()==this);
   THROW_ERROR_MSG(IncompatibleConversion, "Cannot convert "<<that->typestr()<<" to "<<this->typestr());
}

}// /namespace
}// /namespace
