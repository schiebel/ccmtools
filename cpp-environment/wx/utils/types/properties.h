
// -*- mode: C++; c-basic-offset: 4 -*-
// 
// $Id: properties.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//
#ifndef wx_utils_types_properties_h
#define wx_utils_types_properties_h

#include <WX/Utils/linkassert.h>

#include <map>
#include <string>

namespace WX {
namespace Utils {

class Properties {
public:
    void
    set(
        const std::string& key,
        const std::string& value);

    bool
    get(
        const std::string& key,
        std::string& value)
    const;

    const std::string*
    get(
        const std::string& key)
    const;

    bool
    first(
        std::string& key,
        std::string& value)
    const;

    bool
    next(
        std::string& key,
        std::string& value)
    const;

private:
    typedef std::map<std::string, std::string> strstrmap;
    strstrmap map_;
    mutable strstrmap::const_iterator iter_;

public:
    LTA_MEMDECL(2);
};
LTA_STATDEF(Properties, 2);

} // /namespace
} // /namespace

#endif
