/*
$Id: _check_CCM_Local_CCM_Session_MyInteger.cc,v 1.3 2004/03/12 13:20:34 rlechner Exp $
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

#include <CCM_Local/CCM_Session_MyInteger_mirror/MyInteger_mirror_gen.h>
#include <CCM_Local/CCM_Session_MyInteger_mirror/MyIntegerHome_mirror_gen.h>
#include <CCM_Local/CCM_Session_MyInteger/MyInteger_dbc.h>
#include <CCM_Local/CCM_Session_MyInteger/MyIntegerHome_dbc.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace CCM_Session_MyInteger;
using namespace CCM_Session_MyInteger_mirror;


//==============================================================================
// implementation of local client test
//==============================================================================

int main ( int argc, char *argv[] )
{
  int result = 0;
  int error = 0;

  LocalComponents::HomeFinder* homeFinder;

  SmartPtr<MyInteger> myMyInteger;
  SmartPtr<MyInteger_mirror> myMyIntegerMirror;

  Debug::instance().set_global ( true );

  DEBUGNL ( "test_client_MyInteger_component_main (  )" );

  // get an instance of the local HomeFinder and register component homes

  homeFinder = HomeFinder::Instance (  );
  error  = DbC_deploy_MyIntegerHome("MyIntegerHome",false);
  error +=    local_deploy_MyIntegerHome_mirror("MyIntegerHome_mirror");	
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

    SmartPtr<MyIntegerHome> myMyIntegerHome ( dynamic_cast<MyIntegerHome*>
      ( homeFinder->find_home_by_name ( "MyIntegerHome" ).ptr (  ) ) );
    SmartPtr<MyIntegerHome_mirror> myMyIntegerHomeMirror ( dynamic_cast<MyIntegerHome_mirror*>
      ( homeFinder->find_home_by_name ( "MyIntegerHome_mirror" ).ptr (  ) ) );

    myMyInteger = myMyIntegerHome.ptr (  )->create (  );
    myMyIntegerMirror = myMyIntegerHomeMirror.ptr (  )->create (  );

    // create facets, connect components

    myMyInteger.ptr (  )->configuration_complete (  );
    myMyIntegerMirror.ptr (  )->configuration_complete (  );
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
         << myMyInteger.ptr (  )->getComponentVersion (  ) << endl;
    cout << "> getComponentDate (  ) = "
         << myMyInteger.ptr (  )->getComponentDate (  ) << endl;

    DEBUGNL("==== Begin Test Case =============================================" );

     try{
        myMyInteger.ptr()->value1(123);
        if( myMyInteger.ptr()->value1()==123 )
        {
            cout << "# value1 ok" << endl;
        }
     }
     catch(CCM_OCL::OclException& e)
     {
        cout << e.what();
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

    myMyInteger.ptr (  )->remove (  );
    myMyIntegerMirror.ptr (  )->remove (  );

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

  error =  local_undeploy_MyIntegerHome("MyIntegerHome");
  error += local_undeploy_MyIntegerHome_mirror("MyIntegerHome_mirror");
  if(error) {
    cerr << "ERROR: Can't undeploy component homes!" << endl;
    assert(0);
  }

#ifdef CCM_TEST_PYTHON
  Py_Finalize();
#endif

  DEBUGNL ( "exit test_client_MyInteger_component_main (  )" );

  return result;
}
