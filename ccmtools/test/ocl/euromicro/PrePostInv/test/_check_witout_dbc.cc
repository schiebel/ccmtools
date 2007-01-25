#include <LocalComponents/CCM.h>
#include <CCM_Local/HomeFinder.h>
#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>

#ifdef CCM_TEST_PYTHON
#include <Python.h>
#endif


#include <CCM_Local/CCM_Session_C1/C1_gen.h>
#include <CCM_Local/CCM_Session_C1/H1_gen.h>

#include <CCM_Local/CCM_Session_C1/C1_dbc.h>
#include <CCM_Local/CCM_Session_C1/H1_dbc.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace CCM_Session_C1;



//==============================================================================
// implementation of local client test
//==============================================================================

int main ( int argc, char *argv[] )
{
  cout << endl << "##  " __FILE__ "  ##" << endl << endl;
  int result = 0;
  int error = 0;

  LocalComponents::HomeFinder* homeFinder;

  SmartPtr<C1> myC1;

  SmartPtr<CCM_Local::BasicTypes> C1_provides_i1;
  LocalComponents::Cookie C1_ck_i1;

  Debug::instance().set_global(true);

  DEBUGNL ( "test_client_C1_component_main (  )" );

  // get an instance of the local HomeFinder and register component homes

  homeFinder = HomeFinder::Instance (  );
  error  = local_deploy_H1("H1");
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

    SmartPtr<H1> myH1 ( dynamic_cast<H1*>
      ( homeFinder->find_home_by_name ( "H1" ).ptr (  ) ) );

    myC1 = myH1.ptr (  )->create (  );

    // create facets, connect components

    C1_provides_i1 = myC1.ptr (  )->provide_i1( );

    myC1.ptr (  )->configuration_complete (  );
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
    DEBUGNL("==== Begin Test Case =============================================" );
    // test facet attribute

    // Common variables for time measurement
    clock_t start, stop;
    double cpu_time_used;
    const long NUMBER_OF_ITERATIONS = 1000000;

    // Invariants
    {
      long a = 10;
      long r;
      start = clock();
      for(int i=0;i<NUMBER_OF_ITERATIONS;++i) {
	C1_provides_i1->a1(a);
	r = C1_provides_i1->a1();
      }
      stop = clock();
      cpu_time_used = ((double)(stop-start)) / CLOCKS_PER_SEC * 1000.0;
      cout << "  a1()  times: " << cpu_time_used << "ms" << endl;
    }

    {
      double a = 10.0;
      double r;
      start = clock();
      for(int i=0;i<NUMBER_OF_ITERATIONS;++i) {
	C1_provides_i1->a2(a);
	r = C1_provides_i1->a2();    
      }
      stop = clock();
      cpu_time_used = ((double)(stop-start)) / CLOCKS_PER_SEC * 1000.0;
      cout << "  a2()  times: " << cpu_time_used << "ms" << endl;
    }

    {
      string a = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
      string r;
      start = clock();
      for(int i=0;i<NUMBER_OF_ITERATIONS;++i) {
	C1_provides_i1->a3(a);
	r = C1_provides_i1->a3();    
      }
      stop = clock();
      cpu_time_used = ((double)(stop-start)) / CLOCKS_PER_SEC * 1000.0;
      cout << "  a3()  times: " << cpu_time_used << "ms" << endl;
    }


    {
      long p1 = 7;
      long p2 = 13;

      start = clock();
      for(int i=0;i<NUMBER_OF_ITERATIONS;++i) {
        long r  = C1_provides_i1->f1_2(p1,p2);
      }
      stop = clock();
      cpu_time_used = ((double)(stop-start)) / CLOCKS_PER_SEC * 1000.0;
      cout << "  f1_2()  times: " << cpu_time_used << "ms" << endl;
    }

    {
      long p1 = 1;
      long p2 = 2;
      long p3 = 3;
      long p4 = 4;
      long p5 = 5;
      long p6 = 6;
      long p7 = 7;
      long p8 = 8;
      long p9 = 9;
      long p10 = 10;

      start = clock();
      for(int i=0;i<NUMBER_OF_ITERATIONS;++i) {
        long r  = C1_provides_i1->f1_10(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10);
      }
      stop = clock();
      cpu_time_used = ((double)(stop-start)) / CLOCKS_PER_SEC * 1000.0;
      cout << "  f1_10()  times: " << cpu_time_used << "ms" << endl;
    }


    {
      double p1 = 7.7;
      double p2 = 13.13;
      
      start = clock();
      for(int i=0;i<NUMBER_OF_ITERATIONS;++i) {
        double r = C1_provides_i1->f2_2(p1,p2);
      }
      stop = clock();
      cpu_time_used = ((double)(stop-start)) / CLOCKS_PER_SEC * 1000.0;
      cout << "  f2_2()  times: " << cpu_time_used << "ms" << endl;
    }

    {
      double p1 = 1.1;
      double p2 = 2.2;
      double p3 = 3.3;
      double p4 = 4.4;
      double p5 = 5.5;
      double p6 = 6.6;
      double p7 = 7.7;
      double p8 = 8.8;
      double p9 = 9.9;
      double p10 = 10.01;
      
      start = clock();
      for(int i=0;i<NUMBER_OF_ITERATIONS;++i) {
        double r = C1_provides_i1->f2_10(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10);
      }
      stop = clock();
      cpu_time_used = ((double)(stop-start)) / CLOCKS_PER_SEC * 1000.0;
      cout << "  f2_10()  times: " << cpu_time_used << "ms" << endl;
    }


    {
      string p1 = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
      string p2 = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
      
           start = clock();
      for(int i=0;i<NUMBER_OF_ITERATIONS;++i) {
        string r = C1_provides_i1->f3_2(p1,p2);
      }
      stop = clock();
      cpu_time_used = ((double)(stop-start)) / CLOCKS_PER_SEC * 1000.0;
      cout << "  f3_2()  times: " << cpu_time_used << "ms" << endl;
    }

    {
      string p1 = "01234567890";
      string p2 = "01234567890";
      string p3 = "01234567890";
      string p4 = "01234567890";
      string p5 = "01234567890";
      string p6 = "01234567890";
      string p7 = "01234567890";
      string p8 = "01234567890";
      string p9 = "01234567890";
      string p10 ="01234567890";
      
      start = clock();
      for(int i=0;i<NUMBER_OF_ITERATIONS;++i) {
        string r = C1_provides_i1->f3_10(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10);
      }
      stop = clock();
      cpu_time_used = ((double)(stop-start)) / CLOCKS_PER_SEC * 1000.0;
      cout << "  f3_10()  times: " << cpu_time_used << "ms" << endl;
    }




    DEBUGNL("==== End Test Case ===============================================" );
  } catch (CCM_OCL::OclException& e) {
    cout << "OCL-TEST: " << e.what(); 
    result = -1;
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
    myC1.ptr (  )->remove (  );

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

  error =  local_undeploy_H1("H1");
  if(error) {
    cerr << "ERROR: Can't undeploy component homes!" << endl;
    assert(0);
  }

#ifdef CCM_TEST_PYTHON
  Py_Finalize();
#endif

  DEBUGNL ( "exit test_client_C1_component_main (  )" );

  return result;
}
