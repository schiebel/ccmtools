#include <LocalComponents/CCM.h>
#include <CCM_Local/HomeFinder.h>
#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>
#include <CCM_OCL/OclException.h>

#ifdef CCM_TEST_PYTHON
#include <Python.h>
#endif

#include <CCM_Local/CCM_Session_Set1_mirror/Set1_mirror_gen.h>
#include <CCM_Local/CCM_Session_Set1_mirror/Set1Home_mirror_gen.h>
#include <CCM_Local/CCM_Session_Set1/Set1_dbc.h>
#include <CCM_Local/CCM_Session_Set1/Set1Home_dbc.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace CCM_Session_Set1;
using namespace CCM_Session_Set1_mirror;


//==============================================================================
// implementation of local client test
//==============================================================================

int main ( int argc, char *argv[] )
{
  int result = 0;
  int error = 0;

  LocalComponents::HomeFinder* homeFinder;

  SmartPtr<Set1> mySet1;
  SmartPtr<Set1_mirror> mySet1Mirror;

  Debug::instance().set_global ( true );

  DEBUGNL ( "test_client_Set1_component_main (  )" );

  // get an instance of the local HomeFinder and register component homes

  homeFinder = HomeFinder::Instance (  );
  error  = DbC_deploy_Set1Home("Set1Home",false);
  error +=    local_deploy_Set1Home_mirror("Set1Home_mirror");	
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

    SmartPtr<Set1Home> mySet1Home ( dynamic_cast<Set1Home*>
      ( homeFinder->find_home_by_name ( "Set1Home" ).ptr (  ) ) );
    SmartPtr<Set1Home_mirror> mySet1HomeMirror ( dynamic_cast<Set1Home_mirror*>
      ( homeFinder->find_home_by_name ( "Set1Home_mirror" ).ptr (  ) ) );

    mySet1 = mySet1Home.ptr (  )->create (  );
    mySet1Mirror = mySet1HomeMirror.ptr (  )->create (  );

    // create facets, connect components
    mySet1.ptr (  )->configuration_complete (  );
    mySet1Mirror.ptr (  )->configuration_complete (  );
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
         << mySet1.ptr (  )->getComponentVersion (  ) << endl;
    cout << "> getComponentDate (  ) = "
         << mySet1.ptr (  )->getComponentDate (  ) << endl;

    DEBUGNL("==== Begin Test Case =============================================" );

    try
    {
        vector<long> ref;
        ref.push_back(1);
        ref.push_back(2);
        ref.push_back(3);
        mySet1.ptr()->mySequence(ref);
        if( mySet1.ptr()->mySequence().size()==3 )
            cout << "mySet1.ptr()->mySequence()  OK" << endl;
        else
            cout << "mySet1.ptr()->mySequence()  ERROR" << endl;
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
    mySet1.ptr (  )->remove (  );
    mySet1Mirror.ptr (  )->remove (  );

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

  error =  local_undeploy_Set1Home("Set1Home");
  error += local_undeploy_Set1Home_mirror("Set1Home_mirror");
  if(error) {
    cerr << "ERROR: Can't undeploy component homes!" << endl;
    assert(0);
  }

#ifdef CCM_TEST_PYTHON
  Py_Finalize();
#endif

  DEBUGNL ( "exit test_client_Set1_component_main (  )" );

  return result;
}
