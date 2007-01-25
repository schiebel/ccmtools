#include <LocalComponents/CCM.h>
#include <CCM_Local/HomeFinder.h>
#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>

// DbC
#include <CCM_OCL/OclException.h>

#ifdef CCM_TEST_PYTHON
#include <Python.h>
#endif

#include <CCM_Local/CCM_Session_MyString_mirror/MyString_mirror_gen.h>
#include <CCM_Local/CCM_Session_MyString_mirror/MyStringHome_mirror_gen.h>
#include <CCM_Local/CCM_Session_MyString/MyString_dbc.h>
#include <CCM_Local/CCM_Session_MyString/MyStringHome_dbc.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace CCM_Session_MyString;
using namespace CCM_Session_MyString_mirror;


//==============================================================================
// implementation of local client test
//==============================================================================

int main ( int argc, char *argv[] )
{
  int result = 0;
  int error = 0;

  LocalComponents::HomeFinder* homeFinder;

  SmartPtr<MyString> myMyString;
  SmartPtr<MyString_mirror> myMyStringMirror;

  Debug::instance().set_global(true);

  DEBUGNL ( "test_client_MyString_component_main (  )" );

  // get an instance of the local HomeFinder and register component homes

  homeFinder = HomeFinder::Instance (  );
  error  = DbC_deploy_MyStringHome("MyStringHome",false);
  error +=    local_deploy_MyStringHome_mirror("MyStringHome_mirror");	
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

    SmartPtr<MyStringHome> myMyStringHome ( dynamic_cast<MyStringHome*>
      ( homeFinder->find_home_by_name ( "MyStringHome" ).ptr (  ) ) );
    SmartPtr<MyStringHome_mirror> myMyStringHomeMirror ( dynamic_cast<MyStringHome_mirror*>
      ( homeFinder->find_home_by_name ( "MyStringHome_mirror" ).ptr (  ) ) );

    myMyString = myMyStringHome.ptr (  )->create (  );
    myMyStringMirror = myMyStringHomeMirror.ptr (  )->create (  );

    // create facets, connect components
    myMyString.ptr (  )->configuration_complete (  );
    myMyStringMirror.ptr (  )->configuration_complete (  );
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
         << myMyString.ptr (  )->getComponentVersion (  ) << endl;
    cout << "> getComponentDate (  ) = "
         << myMyString.ptr (  )->getComponentDate (  ) << endl;

    DEBUGNL("==== Begin Test Case =============================================" );

    try
    {
        const string ref("hello world");
        myMyString.ptr()->value1(ref);
        if( myMyString.ptr()->value1()==ref )
            cout << "myMyString.ptr()->value1()  OK" << endl;
        else
            cout << "myMyString.ptr()->value1()  ERROR" << endl;
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
    myMyString.ptr (  )->remove (  );
    myMyStringMirror.ptr (  )->remove (  );

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

  error =  local_undeploy_MyStringHome("MyStringHome");
  error += local_undeploy_MyStringHome_mirror("MyStringHome_mirror");
  if(error) {
    cerr << "ERROR: Can't undeploy component homes!" << endl;
    assert(0);
  }

#ifdef CCM_TEST_PYTHON
  Py_Finalize();
#endif

  DEBUGNL ( "exit test_client_MyString_component_main (  )" );

  return result;
}
