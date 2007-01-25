// -*- mode: C++; c-basic-offset: 4 -*-
// 
// $Id: properties.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#include "properties.h"

#include <WX/Utils/error.h>

namespace WX {
namespace Utils {

using namespace std;

LTA_MEMDEF(Properties, 2, "$Id: properties.cc,v 1.1 2005/01/11 08:59:25 teiniker Exp $");

void
Properties::set(
    const string& key,
    const string& value)
{
    strstrmap::const_iterator found = map_.find(key);
    if (found != map_.end()) {
        THROW_ERROR_MSG(Error, "Property \""<<key<<"\" is already set");
    }
    map_.insert(make_pair(key, value));
}

bool
Properties::get(
    const string& key,
    string& value)
const
{
    const string* v = get(key);
    if (v) {
        value = *v;
        return true;
    }
    return false;
}

const std::string*
Properties::get(
    const string& key)
const
{
    strstrmap::const_iterator found = map_.find(key);
    return found==map_.end()? NULL: &found->second;
}

bool
Properties::first(
    string& key,
    string& value)
const
{
    iter_ = map_.begin();
    if (iter_ == map_.end())
        return false;
    key = iter_->first;
    value = iter_->second;
    return true;
}

bool
Properties::next(
    std::string& key,
    std::string& value)
const
{
    if (++iter_ == map_.end())
        return false;
    key = iter_->first;
    value = iter_->second;
    return true;
}


} // /namespace
} // /namespace

