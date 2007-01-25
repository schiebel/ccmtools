// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: struct_def.cpp,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#include "struct_def.h"

#include <map>

namespace WX {
namespace Utils {

using namespace std;

class StructDef_Map_ : public map<string, SmartPtr<Type> > {};

static const SmartPtr<Type> defaulttype;

StructDef::StructDef()
: map_(new StructDef_Map_) {};

StructDef::StructDef(const StructDef& d)
: map_(0) {
   operator=(d);
}

StructDef& StructDef::operator=(const StructDef& d) {
   if (this != &d) {
      delete map_;
      map_ = new StructDef_Map_(*d.map_);
   }
   return *this;
}

StructDef::~StructDef() {
   delete map_;
}
  
void StructDef::insert(const string& n, const SmartPtr<Type>& v) {
   map_->insert(pair<string, SmartPtr<Type> >(n, v));
}

const SmartPtr<Type>& StructDef::get(const string& n) const {
   StructDef_Map_::iterator i = map_->find(n);
   if (i == map_->end())
      return defaulttype;
   return i->second;
}

} // /namespace
} // /namespace
