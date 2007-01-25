/*
$Id: _check_CCM_Local_CCM_Session_Hello.cc,v 1.6 2004/03/03 16:12:49 rlechner Exp $
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

#include <CCM_Local/CCM_Session_Hello_mirror/Hello_mirror_gen.h>
#include <CCM_Local/CCM_Session_Hello_mirror/HelloHome_mirror_gen.h>
#include <CCM_Local/CCM_Session_Hello/Hello_gen.h>
#include <CCM_Local/CCM_Session_Hello/HelloHome_gen.h>

// DbC
#include <CCM_Local/CCM_Session_Hello/Hello_dbc.h>
#include <CCM_Local/CCM_Session_Hello/HelloHome_dbc.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace CCM_Session_Hello;
using namespace CCM_Session_Hello_mirror;


//==============================================================================
// implementation of local client test
//==============================================================================

int main ( int argc, char *argv[] )
{
  int result = 0;
  int error = 0;

  LocalComponents::HomeFinder* homeFinder;

  SmartPtr<Hello> myHello;
  SmartPtr<Hello_mirror> myHelloMirror;

  SmartPtr<LocalComponents::Object> Hello_provides_console;
  LocalComponents::Cookie Hello_ck_console;

  Debug::instance().set_global ( true );

  DEBUGNL ( "test_client_Hello_component_main (  )" );

  // get an instance of the local HomeFinder and register component homes

  homeFinder = HomeFinder::Instance (  );

  // DbC
  error  = DbC_deploy_HelloHome("HelloHome",false);

  error +=    local_deploy_HelloHome_mirror("HelloHome_mirror");
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

    SmartPtr<HelloHome> myHelloHome ( dynamic_cast<HelloHome*>
      ( homeFinder->find_home_by_name ( "HelloHome" ).ptr (  ) ) );
    SmartPtr<HelloHome_mirror> myHelloHomeMirror ( dynamic_cast<HelloHome_mirror*>
      ( homeFinder->find_home_by_name ( "HelloHome_mirror" ).ptr (  ) ) );

    myHello = myHelloHome.ptr (  )->create (  );
    myHelloMirror = myHelloHomeMirror.ptr (  )->create (  );

    // create facets, connect components

    Hello_provides_console =
      myHello.ptr (  )->provide_facet ( "console" );


    Hello_ck_console = myHelloMirror.ptr (  )->connect
      ( "console_mirror", Hello_provides_console );


    myHello.ptr (  )->configuration_complete (  );
    myHelloMirror.ptr (  )->configuration_complete (  );
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
         << myHello.ptr (  )->getComponentVersion (  ) << endl;
    cout << "> getComponentDate (  ) = "
         << myHello.ptr (  )->getComponentDate (  ) << endl;

    DEBUGNL("==== Begin Test Case =============================================" );

    cout << endl << "You should now get an OCL exception:" << endl
                 << "------------------------------------" << endl;

     try{

     double x = myHello.ptr()->sqrt(123.456);
     cout << "# myHello.ptr()->sqrt(123.456) = " << x << endl;
     //assert(false);
     }
     catch(CCM_OCL::OclException& e)
     {
        cout << e.what();
     }

     cout << endl;

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

    myHelloMirror.ptr (  )->disconnect ( "console_mirror", Hello_ck_console );



    myHello.ptr (  )->remove (  );
    myHelloMirror.ptr (  )->remove (  );

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

  error =  local_undeploy_HelloHome("HelloHome");
  error += local_undeploy_HelloHome_mirror("HelloHome_mirror");
  if(error) {
    cerr << "ERROR: Can't undeploy component homes!" << endl;
    assert(0);
  }

#ifdef CCM_TEST_PYTHON
  Py_Finalize();
#endif

  DEBUGNL ( "exit test_client_Hello_component_main (  )" );

  return result;
}
