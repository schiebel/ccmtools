#include <LocalComponents/CCM.h>
#include <CCM_Local/HomeFinder.h>
#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>
#include <CCM_OCL/OclException.h>

#ifdef CCM_TEST_PYTHON
#include <Python.h>
#endif

#include <CCM_Local/CCM_Session_MyBag_mirror/MyBag_mirror_gen.h>
#include <CCM_Local/CCM_Session_MyBag_mirror/MyBagHome_mirror_gen.h>
#include <CCM_Local/CCM_Session_MyBag/MyBag_dbc.h>
#include <CCM_Local/CCM_Session_MyBag/MyBagHome_dbc.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace CCM_Session_MyBag;
using namespace CCM_Session_MyBag_mirror;


//==============================================================================
// implementation of local client test
//==============================================================================

int main ( int argc, char *argv[] )
{
  int result = 0;
  int error = 0;

  LocalComponents::HomeFinder* homeFinder;

  SmartPtr<MyBag> myMyBag;
  SmartPtr<MyBag_mirror> myMyBagMirror;

  Debug::instance().set_global ( true );

  DEBUGNL ( "test_client_MyBag_component_main (  )" );

  // get an instance of the local HomeFinder and register component homes

  homeFinder = HomeFinder::Instance (  );
  error  = DbC_deploy_MyBagHome("MyBagHome",false);
  error +=    local_deploy_MyBagHome_mirror("MyBagHome_mirror");	
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

    SmartPtr<MyBagHome> myMyBagHome ( dynamic_cast<MyBagHome*>
      ( homeFinder->find_home_by_name ( "MyBagHome" ).ptr (  ) ) );
    SmartPtr<MyBagHome_mirror> myMyBagHomeMirror ( dynamic_cast<MyBagHome_mirror*>
      ( homeFinder->find_home_by_name ( "MyBagHome_mirror" ).ptr (  ) ) );

    myMyBag = myMyBagHome.ptr (  )->create (  );
    myMyBagMirror = myMyBagHomeMirror.ptr (  )->create (  );

    // create facets, connect components







    myMyBag.ptr (  )->configuration_complete (  );
    myMyBagMirror.ptr (  )->configuration_complete (  );
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
         << myMyBag.ptr (  )->getComponentVersion (  ) << endl;
    cout << "> getComponentDate (  ) = "
         << myMyBag.ptr (  )->getComponentDate (  ) << endl;

    DEBUGNL("==== Begin Test Case =============================================" );

   try
    {
        if( myMyBag.ptr()->array1().size()==0 )
        {
            cout << "-->  OK  <--" << endl;
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




    myMyBag.ptr (  )->remove (  );
    myMyBagMirror.ptr (  )->remove (  );

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

  error =  local_undeploy_MyBagHome("MyBagHome");
  error += local_undeploy_MyBagHome_mirror("MyBagHome_mirror");
  if(error) {
    cerr << "ERROR: Can't undeploy component homes!" << endl;
    assert(0);
  }

#ifdef CCM_TEST_PYTHON
  Py_Finalize();
#endif

  DEBUGNL ( "exit test_client_MyBag_component_main (  )" );

  return result;
}
