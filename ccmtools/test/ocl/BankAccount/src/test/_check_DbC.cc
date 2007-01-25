#include <LocalComponents/CCM.h>
#include <CCM_Local/HomeFinder.h>
#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>

#ifdef CCM_TEST_PYTHON
#include <Python.h>
#endif


// uncomment this if you want to use 'design by contract'
#define CCM_USE_DBC


#include <CCM_Local/CCM_Session_Bank_mirror/Bank_mirror_gen.h>
#include <CCM_Local/CCM_Session_Bank_mirror/BankHome_mirror_gen.h>

#ifdef CCM_USE_DBC
#include <CCM_Local/CCM_Session_Bank/Bank_dbc.h>
#include <CCM_Local/CCM_Session_Bank/BankHome_dbc.h>
#else
#include <CCM_Local/CCM_Session_Bank/Bank_gen.h>
#include <CCM_Local/CCM_Session_Bank/BankHome_gen.h>
#endif

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace CCM_Session_Bank;
using namespace CCM_Session_Bank_mirror;


//==============================================================================
// implementation of local client test
//==============================================================================

int main ( int argc, char *argv[] )
{
  int result = 0;
  int error = 0;

  LocalComponents::HomeFinder* homeFinder;

  SmartPtr<Bank> myBank;
  SmartPtr<Bank_mirror> myBankMirror;

  SmartPtr<LocalComponents::Object> Bank_provides_account;
  LocalComponents::Cookie Bank_ck_account;




  Debug::instance().set_global(true);

  DEBUGNL ( "test_client_Bank_component_main (  )" );

  // get an instance of the local HomeFinder and register component homes

  homeFinder = HomeFinder::Instance (  );
#ifdef CCM_USE_DBC
  error  = DbC_deploy_BankHome("BankHome", false);
#else
  error  = local_deploy_BankHome("BankHome");
#endif
  error +=    local_deploy_BankHome_mirror("BankHome_mirror");	
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

    SmartPtr<BankHome> myBankHome ( dynamic_cast<BankHome*>
      ( homeFinder->find_home_by_name ( "BankHome" ).ptr (  ) ) );
    SmartPtr<BankHome_mirror> myBankHomeMirror ( dynamic_cast<BankHome_mirror*>
      ( homeFinder->find_home_by_name ( "BankHome_mirror" ).ptr (  ) ) );

    myBank = myBankHome.ptr (  )->create (  );
    myBankMirror = myBankHomeMirror.ptr (  )->create (  );

    // create facets, connect components

    Bank_provides_account =
      myBank.ptr (  )->provide_facet ( "account" );


    Bank_ck_account = myBankMirror.ptr (  )->connect
      ( "account_mirror", Bank_provides_account );


    myBank.ptr (  )->configuration_complete (  );
    myBankMirror.ptr (  )->configuration_complete (  );
  } catch ( LocalComponents::HomeNotFound ) {
    cout << "DEPLOY: can't find a home!" << endl;
    result = -1;
  } catch ( LocalComponents::NotImplemented& e ) {
    cout << "DEPLOY: function not implemented: " << e.what (  ) << endl;
    result = -1;
  } catch ( LocalComponents::InvalidName& e ) {
    cout << "DEPLOY: invalid name during connection: " << e.what (  ) << endl;
    result = -1;
  }
#ifdef CCM_USE_DBC
  catch(CCM_OCL::OclException& e)
  {
    cout << "DEPLOY: 'design by contract' error:" << endl << e.what();
    result = -1;
  }
#endif
  catch ( ... )  {
    cout << "DEPLOY: there is something wrong!" << endl;
    result = -1;
  }

  if (result < 0) return result;

  /* TESTING */

  try {
    // check basic functionality

    cout << "> getComponentVersion (  ) = "
         << myBank.ptr (  )->getComponentVersion (  ) << endl;
    cout << "> getComponentDate (  ) = "
         << myBank.ptr (  )->getComponentDate (  ) << endl;

    DEBUGNL("==== Begin Test Case =============================================" );

    SmartPtr<BankAccount> ba = myBank->provide_account();
    ba->deposit(1000);
    ba->withdraw(100);
    if( ba->balance()!=900 )
    {
        cout << "wrong balance: " << ba->balance() << endl;
        result = -1;
    }

    DEBUGNL("==== End Test Case ===============================================" );
  } catch ( LocalComponents::NotImplemented& e ) {
    cout << "TEST: function not implemented: " << e.what (  ) << endl;
    result = -1;
  }
#ifdef CCM_USE_DBC
  catch(CCM_OCL::OclException& e)
  {
    cout << "TEST: 'design by contract' error:" << endl << e.what();
    result = -1;
  }
#endif
  catch ( ... )  {
    cout << "TEST: there is something wrong!" << endl;
    result = -1;
  }

  if (result < 0) return result;

  /* TEAR DOWN */

  try {
    // disconnect components, destroy instances, unregister homes

    myBankMirror.ptr (  )->disconnect ( "account_mirror", Bank_ck_account );



    myBank.ptr (  )->remove (  );
    myBankMirror.ptr (  )->remove (  );

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

  error =  local_undeploy_BankHome("BankHome");
  error += local_undeploy_BankHome_mirror("BankHome_mirror");
  if(error) {
    cerr << "ERROR: Can't undeploy component homes!" << endl;
    assert(0);
  }

#ifdef CCM_TEST_PYTHON
  Py_Finalize();
#endif

  DEBUGNL ( "exit test_client_Bank_component_main (  )" );

  return result;
}
