
/**
 * CCM_IFace facet class implementation. 
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

#include "Test_iface_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_Test {

iface_impl::iface_impl(CCM_Local::CCM_Session_Test::CCM_Test_impl* component_impl)
  : component(component_impl)
{
    DEBUGNL("+iface_impl->iface_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

iface_impl::~iface_impl()
{
    DEBUGNL ( "-iface_impl->~iface_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

long
iface_impl::foo(const std::string& msg)
    throw (LocalComponents::CCMException, ErrorException, FatalError, SuperError )
{
    DEBUGNL("iface_impl->foo(msg)");

    if(msg == "Error") {
        ErrorException error;
        ErrorInfoList errorInfoList;
        ErrorInfo errorInfo1;
        errorInfo1.code = 7;
        errorInfo1.message = "First error";
        errorInfoList.push_back(errorInfo1);

        ErrorInfo errorInfo2;
        errorInfo2.code = 13;
        errorInfo2.message = "Second error";
        errorInfoList.push_back(errorInfo2);

        error.info = errorInfoList;
        throw error;
    }
    else if(msg == "SuperError") {
      throw SuperError();
    }
    else if(msg == "FatalError") {
      FatalError fatalError;
      fatalError.what = "This is a fatal error condition!";
      throw fatalError;
    }
    else {
      cout << ">> " << msg << endl;
    }
    return msg.length();
}

} // /namespace CCM_Session_Test
} // /namespace CCM_Local
