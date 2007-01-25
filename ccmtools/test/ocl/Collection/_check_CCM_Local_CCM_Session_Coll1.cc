#include <LocalComponents/CCM.h>
#include <CCM_Local/HomeFinder.h>
#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>
#include <CCM_OCL/OclException.h>

#ifdef CCM_TEST_PYTHON
#include <Python.h>
#endif

#include <CCM_Local/CCM_Session_Coll1_mirror/Coll1_mirror_gen.h>
#include <CCM_Local/CCM_Session_Coll1_mirror/Coll1Home_mirror_gen.h>
#include <CCM_Local/CCM_Session_Coll1/Coll1_dbc.h>
#include <CCM_Local/CCM_Session_Coll1/Coll1Home_dbc.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace CCM_Session_Coll1;
using namespace CCM_Session_Coll1_mirror;


//==============================================================================
// implementation of local client test
//==============================================================================

int main ( int argc, char *argv[] )
{
  int result = 0;
  int error = 0;

  LocalComponents::HomeFinder* homeFinder;

  SmartPtr<Coll1> myColl1;
  SmartPtr<Coll1_mirror> myColl1Mirror;

  Debug::instance().set_global ( true );

  DEBUGNL ( "test_client_Coll1_component_main (  )" );

  // get an instance of the local HomeFinder and register component homes

  homeFinder = HomeFinder::Instance (  );
  error  = DbC_deploy_Coll1Home("Coll1Home",false);
  error +=    local_deploy_Coll1Home_mirror("Coll1Home_mirror");	
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

    SmartPtr<Coll1Home> myColl1Home ( dynamic_cast<Coll1Home*>
      ( homeFinder->find_home_by_name ( "Coll1Home" ).ptr (  ) ) );
    SmartPtr<Coll1Home_mirror> myColl1HomeMirror ( dynamic_cast<Coll1Home_mirror*>
      ( homeFinder->find_home_by_name ( "Coll1Home_mirror" ).ptr (  ) ) );

    myColl1 = myColl1Home.ptr (  )->create (  );
    myColl1Mirror = myColl1HomeMirror.ptr (  )->create (  );

    // create facets, connect components







    myColl1.ptr (  )->configuration_complete (  );
    myColl1Mirror.ptr (  )->configuration_complete (  );
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
         << myColl1.ptr (  )->getComponentVersion (  ) << endl;
    cout << "> getComponentDate (  ) = "
         << myColl1.ptr (  )->getComponentDate (  ) << endl;

    DEBUGNL("==== Begin Test Case =============================================" );

    cout << "###   DbC   ###" << endl;

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




    myColl1.ptr (  )->remove (  );
    myColl1Mirror.ptr (  )->remove (  );

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

  error =  local_undeploy_Coll1Home("Coll1Home");
  error += local_undeploy_Coll1Home_mirror("Coll1Home_mirror");
  if(error) {
    cerr << "ERROR: Can't undeploy component homes!" << endl;
    assert(0);
  }

#ifdef CCM_TEST_PYTHON
  Py_Finalize();
#endif

  DEBUGNL ( "exit test_client_Coll1_component_main (  )" );

  return result;
}
