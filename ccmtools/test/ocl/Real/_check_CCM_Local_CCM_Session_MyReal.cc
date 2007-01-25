/*
$Id: _check_CCM_Local_CCM_Session_MyReal.cc,v 1.3 2004/03/12 13:20:35 rlechner Exp $
*/

#include <LocalComponents/CCM.h>
#include <CCM_Local/HomeFinder.h>
#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>

// DbC
#include <CCM_OCL/OclException.h>

#ifdef CCM_TEST_PYTHON
#include <Python.h>
#endif

#include <CCM_Local/CCM_Session_MyReal_mirror/MyReal_mirror_gen.h>
#include <CCM_Local/CCM_Session_MyReal_mirror/MyRealHome_mirror_gen.h>
#include <CCM_Local/CCM_Session_MyReal/MyReal_dbc.h>
#include <CCM_Local/CCM_Session_MyReal/MyRealHome_dbc.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace CCM_Session_MyReal;
using namespace CCM_Session_MyReal_mirror;


//==============================================================================
// implementation of local client test
//==============================================================================

int main ( int argc, char *argv[] )
{
  int result = 0;
  int error = 0;

  LocalComponents::HomeFinder* homeFinder;

  SmartPtr<MyReal> myMyReal;
  SmartPtr<MyReal_mirror> myMyRealMirror;

  Debug::instance().set_global ( true );

  DEBUGNL ( "test_client_MyReal_component_main (  )" );

  // get an instance of the local HomeFinder and register component homes

  homeFinder = HomeFinder::Instance (  );
  error  = DbC_deploy_MyRealHome("MyRealHome",false);
  error +=    local_deploy_MyRealHome_mirror("MyRealHome_mirror");
  if(error) {
    cerr << "ERROR: Can't deploy component homes!" << endl;
    assert(0);
  }

#ifdef CCM_TEST_PYTHON
  Py_Initialize();
#endif

  /* SET UP / DEPLOYMENT */

  try {
    // find component/mirror homes, instantiate components

    SmartPtr<MyRealHome> myMyRealHome ( dynamic_cast<MyRealHome*>
      ( homeFinder->find_home_by_name ( "MyRealHome" ).ptr (  ) ) );
    SmartPtr<MyRealHome_mirror> myMyRealHomeMirror ( dynamic_cast<MyRealHome_mirror*>
      ( homeFinder->find_home_by_name ( "MyRealHome_mirror" ).ptr (  ) ) );

    myMyReal = myMyRealHome.ptr (  )->create (  );
    myMyRealMirror = myMyRealHomeMirror.ptr (  )->create (  );

    // create facets, connect components

    myMyReal.ptr (  )->configuration_complete (  );
    myMyRealMirror.ptr (  )->configuration_complete (  );
  } catch ( LocalComponents::HomeNotFound ) {
    cout << "DEPLOY: can't find a home!" << endl;
    result = -1;
  } catch ( LocalComponents::NotImplemented& e ) {
    cout << "DEPLOY: function not implemented: " << e.what (  ) << endl;
    result = -1;
  } catch ( LocalComponents::InvalidName& e ) {
    cout << "DEPLOY: invalid name during connection: " << e.what (  ) << endl;
    result = -1;
  } catch ( ... )  {
    cout << "DEPLOY: there is something wrong!" << endl;
    result = -1;
  }

  if (result < 0) return result;

  /* TESTING */

  try {
    // check basic functionality

    cout << "> getComponentVersion (  ) = "
         << myMyReal.ptr (  )->getComponentVersion (  ) << endl;
    cout << "> getComponentDate (  ) = "
         << myMyReal.ptr (  )->getComponentDate (  ) << endl;

    DEBUGNL("==== Begin Test Case =============================================" );

    myMyReal.ptr()->value1(123.456);
    if( myMyReal.ptr()->value1()==123.456 )
    {
        cout << endl << "myMyReal.ptr()->value1()  OK" << endl << endl;
    }

    DEBUGNL("==== End Test Case ===============================================" );
  } catch ( LocalComponents::NotImplemented& e ) {
    cout << "TEST: function not implemented: " << e.what (  ) << endl;
    result = -1;
  } catch ( ... )  {
    cout << "TEST: there is something wrong!" << endl;
    result = -1;
  }

  if (result < 0) return result;

  /* TEAR DOWN */

  try {
    // disconnect components, destroy instances, unregister homes

    myMyReal.ptr (  )->remove (  );
    myMyRealMirror.ptr (  )->remove (  );

  } catch ( LocalComponents::HomeNotFound ) {
    cout << "TEARDOWN: can't find a home!" << endl;
    result = -1;
  } catch ( LocalComponents::NotImplemented& e ) {
    cout << "TEARDOWN: function not implemented: " << e.what (  ) << endl;
    result = -1;
  } catch ( ... )  {
    cout << "TEARDOWN: there is something wrong!" << endl;
    result = -1;
  }

  error =  local_undeploy_MyRealHome("MyRealHome");
  error += local_undeploy_MyRealHome_mirror("MyRealHome_mirror");
  if(error) {
    cerr << "ERROR: Can't undeploy component homes!" << endl;
    assert(0);
  }

#ifdef CCM_TEST_PYTHON
  Py_Finalize();
#endif

  DEBUGNL ( "exit test_client_MyReal_component_main (  )" );

  return result;
}
