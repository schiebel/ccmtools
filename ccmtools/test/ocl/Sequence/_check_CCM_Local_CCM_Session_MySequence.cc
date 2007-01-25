#include <LocalComponents/CCM.h>
#include <CCM_Local/HomeFinder.h>
#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>
#include <CCM_OCL/OclException.h>

#ifdef CCM_TEST_PYTHON
#include <Python.h>
#endif

#include <CCM_Local/CCM_Session_MySequence_mirror/MySequence_mirror_gen.h>
#include <CCM_Local/CCM_Session_MySequence_mirror/MySequenceHome_mirror_gen.h>
#include <CCM_Local/CCM_Session_MySequence/MySequence_dbc.h>
#include <CCM_Local/CCM_Session_MySequence/MySequenceHome_dbc.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace CCM_Session_MySequence;
using namespace CCM_Session_MySequence_mirror;


//==============================================================================
// implementation of local client test
//==============================================================================

int main ( int argc, char *argv[] )
{
  int result = 0;
  int error = 0;

  LocalComponents::HomeFinder* homeFinder;

  SmartPtr<MySequence> myMySequence;
  SmartPtr<MySequence_mirror> myMySequenceMirror;

  Debug::instance().set_global ( true );

  DEBUGNL ( "test_client_MySequence_component_main (  )" );

  // get an instance of the local HomeFinder and register component homes

  homeFinder = HomeFinder::Instance (  );
  error  = DbC_deploy_MySequenceHome("MySequenceHome",false);
  error +=    local_deploy_MySequenceHome_mirror("MySequenceHome_mirror");	
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

    SmartPtr<MySequenceHome> myMySequenceHome ( dynamic_cast<MySequenceHome*>
      ( homeFinder->find_home_by_name ( "MySequenceHome" ).ptr (  ) ) );
    SmartPtr<MySequenceHome_mirror> myMySequenceHomeMirror ( dynamic_cast<MySequenceHome_mirror*>
      ( homeFinder->find_home_by_name ( "MySequenceHome_mirror" ).ptr (  ) ) );

    myMySequence = myMySequenceHome.ptr (  )->create (  );
    myMySequenceMirror = myMySequenceHomeMirror.ptr (  )->create (  );

    // create facets, connect components
    myMySequence.ptr (  )->configuration_complete (  );
    myMySequenceMirror.ptr (  )->configuration_complete (  );
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
         << myMySequence.ptr (  )->getComponentVersion (  ) << endl;
    cout << "> getComponentDate (  ) = "
         << myMySequence.ptr (  )->getComponentDate (  ) << endl;

    DEBUGNL("==== Begin Test Case =============================================" );

    try
    {
        vector<long> ref;
        ref.push_back(1);
        ref.push_back(2);
        ref.push_back(3);
        myMySequence.ptr()->seq1(ref);
        if( myMySequence.ptr()->seq1().size()==3 )
            cout << "myMySequence.ptr()->seq1()  OK" << endl;
        else
            cout << "myMySequence.ptr()->seq1()  ERROR" << endl;
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
    myMySequence.ptr (  )->remove (  );
    myMySequenceMirror.ptr (  )->remove (  );

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

  error =  local_undeploy_MySequenceHome("MySequenceHome");
  error += local_undeploy_MySequenceHome_mirror("MySequenceHome_mirror");
  if(error) {
    cerr << "ERROR: Can't undeploy component homes!" << endl;
    assert(0);
  }

#ifdef CCM_TEST_PYTHON
  Py_Finalize();
#endif

  DEBUGNL ( "exit test_client_MySequence_component_main (  )" );

  return result;
}
