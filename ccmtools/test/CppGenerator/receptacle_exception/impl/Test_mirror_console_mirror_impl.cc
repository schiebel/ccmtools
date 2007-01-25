
/**
 * CCM_Console facet class implementation. 
 *
 * // TODO: WRITE YOUR DESCRIPTION HERE! 
 *
 * @author
 * @version 
 *
 * This class implements a facet's methods and attributes.
 **/

#include <cassert>
#include <iostream>
#include <WX/Utils/debug.h>

#include "Test_mirror_console_mirror_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_Test_mirror {

console_mirror_impl::console_mirror_impl(CCM_Local::CCM_Session_Test_mirror::CCM_Test_mirror_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+console_mirror_impl->console_mirror_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

console_mirror_impl::~console_mirror_impl()
{
    DEBUGNL ( "-console_mirror_impl->~console_mirror_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

long
console_mirror_impl::print(const std::string& msg)
    throw (LocalComponents::CCMException, SuperError, Error, FatalError )
{
    DEBUGNL("console_mirror_impl->print(msg)");

    cout << ">> " << msg << endl;

    if(msg == "Error") {
        Error error;
        ErrorInfoList error_info_list;
        ErrorInfo error_info;
        error_info.code = 7;
        error_info.message = "A simple error!";
        error_info_list.push_back(error_info);
        error.info = error_info_list;
        throw error;
    }

    if(msg == "SuperError")
        throw SuperError();

    if(msg == "FatalError")
        throw FatalError();

    return msg.length();
}

} // /namespace CCM_Session_Test_mirror
} // /namespace CCM_Local
