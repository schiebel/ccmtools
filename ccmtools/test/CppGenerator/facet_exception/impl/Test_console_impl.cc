
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

#include "Test_console_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_Test {

console_impl::console_impl(CCM_Local::CCM_Session_Test::CCM_Test_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+console_impl->console_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

console_impl::~console_impl()
{
    DEBUGNL ( "-console_impl->~console_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

long
console_impl::println(const std::string& msg)
    throw (LocalComponents::CCMException, Error, SuperError, FatalError )
{
    DEBUGNL("console_impl->println(msg)");

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

} // /namespace CCM_Session_Test
} // /namespace CCM_Local
