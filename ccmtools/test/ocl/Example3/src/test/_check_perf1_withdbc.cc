/*
    $Id: _check_perf1_withdbc.cc,v 1.2 2004/03/03 16:12:49 rlechner Exp $
*/
#include <LocalComponents/CCM.h>
#include <CCM_Local/HomeFinder.h>
#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>

#ifdef CCM_TEST_PYTHON
#include <Python.h>
#endif

#include <CCM_Local/CCM_Session_Grfx_mirror/Grfx_mirror_gen.h>
#include <CCM_Local/CCM_Session_Grfx_mirror/GrfxHome_mirror_gen.h>
#include <CCM_Local/CCM_Session_Grfx/Grfx_dbc.h>
#include <CCM_Local/CCM_Session_Grfx/GrfxHome_dbc.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace CCM_Session_Grfx;
using namespace CCM_Session_Grfx_mirror;


//==============================================================================
// implementation of local client test
//==============================================================================

int main ( int argc, char *argv[] )
{
  int result = 0;
  int error = 0;

  LocalComponents::HomeFinder* homeFinder;

  SmartPtr<Grfx> myGrfx;
  SmartPtr<Grfx_mirror> myGrfxMirror;

  SmartPtr<LocalComponents::Object> Grfx_provides_point;  SmartPtr<LocalComponents::Object> Grfx_provides_line;
  LocalComponents::Cookie Grfx_ck_point;  LocalComponents::Cookie Grfx_ck_line;




  Debug::instance().set_global(true);

  DEBUGNL ( "test_client_Grfx_component_main (  )" );

  // get an instance of the local HomeFinder and register component homes

  homeFinder = HomeFinder::Instance (  );
  error  = DbC_deploy_GrfxHome("GrfxHome",false);
  error +=    local_deploy_GrfxHome_mirror("GrfxHome_mirror");	
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

    SmartPtr<GrfxHome> myGrfxHome ( dynamic_cast<GrfxHome*>
      ( homeFinder->find_home_by_name ( "GrfxHome" ).ptr (  ) ) );
    SmartPtr<GrfxHome_mirror> myGrfxHomeMirror ( dynamic_cast<GrfxHome_mirror*>
      ( homeFinder->find_home_by_name ( "GrfxHome_mirror" ).ptr (  ) ) );

    myGrfx = myGrfxHome.ptr (  )->create (  );
    myGrfxMirror = myGrfxHomeMirror.ptr (  )->create (  );

    // create facets, connect components

    Grfx_provides_point =
      myGrfx.ptr (  )->provide_facet ( "point" );    Grfx_provides_line =
      myGrfx.ptr (  )->provide_facet ( "line" );


    Grfx_ck_point = myGrfxMirror.ptr (  )->connect
      ( "point_mirror", Grfx_provides_point );    Grfx_ck_line = myGrfxMirror.ptr (  )->connect
      ( "line_mirror", Grfx_provides_line );


    myGrfx.ptr (  )->configuration_complete (  );
    myGrfxMirror.ptr (  )->configuration_complete (  );
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
         << myGrfx.ptr (  )->getComponentVersion (  ) << endl;
    cout << "> getComponentDate (  ) = "
         << myGrfx.ptr (  )->getComponentDate (  ) << endl;

    DEBUGNL("==== Begin Test Case =============================================" );

    cout << endl << "performance test with DbC" << endl << endl;
    try {
    #include "performance1.hxx"
    }
     catch(CCM_OCL::OclException& e)
     {
        cout << e.what();
        result = -1;
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

    myGrfxMirror.ptr (  )->disconnect ( "point_mirror", Grfx_ck_point );
    myGrfxMirror.ptr (  )->disconnect ( "line_mirror", Grfx_ck_line );



    myGrfx.ptr (  )->remove (  );
    myGrfxMirror.ptr (  )->remove (  );

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

  error =  local_undeploy_GrfxHome("GrfxHome");
  error += local_undeploy_GrfxHome_mirror("GrfxHome_mirror");
  if(error) {
    cerr << "ERROR: Can't undeploy component homes!" << endl;
    assert(0);
  }

#ifdef CCM_TEST_PYTHON
  Py_Finalize();
#endif

  DEBUGNL ( "exit test_client_Grfx_component_main (  )" );

  return result;
}
