/***
 * CCM Tools Test Client 
 *
 ***/

#include <cstdlib> 
#include <iostream>
#include <string>

#include <WX/Utils/debug.h>
#include <WX/Utils/Timer.h>
#include <WX/Utils/TimerEvaluation.h>

#include <CORBA.h>
#include <coss/CosNaming.h>

#include <CCM/CCMContainer.h>

#include <CCM_Remote/CCM_Session_Test/TestHome_remote.h>
#include <CORBA_Stubs_Test.h>

using namespace std;
using namespace WX::Utils;

//==============================================================================
// Implementation of remote client test
//==============================================================================

int main (int argc, char *argv[])
{
    WX::Utils::Timer globalTimer;
    WX::Utils::TimerEvaluation eval;
    globalTimer.start();

    cout << ">>>> Start Test Client: " << __FILE__ << endl;

    int error = 0;

    char* NameServiceLocation = getenv("CCM_NAME_SERVICE");
    if(NameServiceLocation == NULL) { 
        cerr << "Error: Environment variable CCM_NAME_SERVICE is not set!" 
	     << endl;
        return -1;
    }

    // Initialize ORB 
    ostringstream os;
    os << "NameService=" << NameServiceLocation;
    char* argv_[] = { "", "-ORBInitRef", (char*)os.str().c_str()}; 
    int   argc_   = 3;
    DEBUGNL(">> " << argv_[0] << " "<< argv_[1] << argv_[2]);
    CORBA::ORB_var orb = CORBA::ORB_init(argc_, argv_);


    /**
     * Client-side code
     */
    CORBA::Object_var obj = orb->resolve_initial_references("NameService");
    CosNaming::NamingContextExt_var nc =
        CosNaming::NamingContextExt::_narrow(obj);

    // Deployment 

    // Find ComponentHomes in the Naming-Service
    obj = nc->resolve_str("TestHome:1.0");
    assert (!CORBA::is_nil (obj));
    ::CORBA_Stubs::TestHome_var myTestHome = 
      ::CORBA_Stubs::TestHome::_narrow (obj);

    // Create component instances
    ::CORBA_Stubs::Test_var myTest = myTestHome->create();

    // Provide facets   
    ::CORBA_Stubs::Benchmark_var bm = myTest->provide_bm();

    myTest->configuration_complete();

    try {
      cout << "--- Start Test Case -----------------------------------" << endl;

      // Test configuration
      WX::Utils::Timer timer;

      const long MAX_LOOP_COUNT = 10000;

      const long SEQUENCE_SIZE_MAX = 10000;
      const long SEQUENCE_SIZE_STEP = 1000;


      //----------------------------------------------------------
      // ping test case
      //----------------------------------------------------------

      {
	// Ping
	cout << endl;
	cout << "Remote CCM Test: void f0() "; 

	timer.start();
	for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	  bm->f0();
	}
	timer.stop();
	cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,1);
      }
      

      //----------------------------------------------------------
      // in parameter test cases 
      //----------------------------------------------------------

      {
	// in long parameter
	cout << endl;
	cout << "Remote CCM Test: void f_in1(in long l1) "; 

	CORBA::Long value = 7;

	timer.start();
	for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	  bm->f_in1(value);
	}
	timer.stop();
	cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,1);
      }


      {
	// in string parameter with increasing size
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Remote CCM Test: void f_in2(in string s1) "; 

	  string s;
	  for(int i=0; i<size; i++)
	    s += "X";
	  CORBA::String_var value = CORBA::string_dup(s.c_str());

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    bm->f_in2(value.in());
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }


      {
	// in sequence of long parameter with increasing size
	cout << endl;
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Remote CCM Test: void f_in3(in LongList ll1) "; 

	  ::CORBA_Stubs::LongList_var value = new ::CORBA_Stubs::LongList;
	  value->length(size);

	  for(long i=0; i<size; i++)
	    (*value)[i] = i;

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    bm->f_in3(value.in());
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }	



      //----------------------------------------------------------
      // inout parameter test cases 
      //----------------------------------------------------------

      {
	// inout long parameter
	cout << endl;
	cout << "Collocated CCM Test: void f_inout1(inout long l1) "; 

	CORBA::Long value = 7;
	bm->long_attr(value);

	timer.start();
	for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	  bm->f_inout1(value);
	}
	timer.stop();
	cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,1);
      }

      {
	// inout string parameter with increasing size
	cout << endl;
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Collocated CCM Test: void f_inout2(inout string s1) "; 

	  string s;
	  for(int i=0; i<size; i++)
	    s += "X";
	  CORBA::String_var value = CORBA::string_dup(s.c_str());
	  bm->string_attr(value.in());

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    bm->f_inout2(value.inout());
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }

      {
	// inout sequence of long parameter with increasing size
	cout << endl;
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Collocated CCM Test: void f_inout3(inout LongList ll1) "; 

	  ::CORBA_Stubs::LongList_var value = new ::CORBA_Stubs::LongList;
	  value->length(size);
	  for(long i=0; i<size; i++)
	    (*value)[i] = i;
	  bm->LongList_attr(value.in());  

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    bm->f_inout3(value.inout());
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }


      //----------------------------------------------------------
      // out parameters test cases 
      //----------------------------------------------------------

      {
	// out long parameter
	cout << endl;
	cout << "Collocated CCM Test: void f_out1(out long l1) "; 

	CORBA::Long value = 7;
	CORBA::Long result;
	bm->long_attr(value);

	timer.start();
	for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	  bm->f_out1(result);
	}
	timer.stop();
	cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,1);
      }

      {
	// out string parameter with increasing size
	cout << endl;
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Collocated CCM Test: void f_out2(out string s1) "; 

	  string s;
	  for(int i=0; i<size; i++)
	    s += "X";
	  CORBA::String_var value = CORBA::string_dup(s.c_str());
	  CORBA::String_var result;
	  bm->string_attr(value.in());

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    bm->f_out2(result.out());
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }

      {
	// out sequence of long parameter with increasing size
	cout << endl;
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Collocated CCM Test: void f_out3(out LongList ll1) "; 

	  ::CORBA_Stubs::LongList_var value = new ::CORBA_Stubs::LongList;
	  ::CORBA_Stubs::LongList_var result;
	  value->length(size);
	  for(long i=0; i<size; i++)
	    (*value)[i] = i;
	  bm->LongList_attr(value.in());  

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    bm->f_out3(result.out());
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }


      //----------------------------------------------------------
      // return value test cases 
      //----------------------------------------------------------

      {
	// long result
	cout << endl;
	cout << "Collocated CCM Test: long f_ret1() "; 

	CORBA::Long value = 7;
	CORBA::Long result;
	bm->long_attr(value);

	timer.start();
	for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	  result = bm->f_ret1();
	}
	timer.stop();
	cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,1);
      }

      {
	// string result with increasing size
	cout << endl;
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Collocated CCM Test: string f_ret2() "; 

	  string s;
	  for(int i=0; i<size; i++)
	    s += "X";
	  CORBA::String_var value = CORBA::string_dup(s.c_str());
	  CORBA::String_var result;
	  bm->string_attr(value.in());

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    result = bm->f_ret2();
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }

      {
	// sequence of long result with increasing size
	cout << endl;
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Collocated CCM Test: LongList f_ret3() "; 

	  ::CORBA_Stubs::LongList_var value = new ::CORBA_Stubs::LongList;
	  ::CORBA_Stubs::LongList_var result;
	  value->length(size);
	  for(long i=0; i<size; i++)
	    (*value)[i] = i;
	  bm->LongList_attr(value.in());  

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    result = bm->f_ret3();
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }

      cout << "--- Stop Test Case ------------------------------------" << endl;
    }
    catch(...) {
      cout << "TEST: there is something wrong!" << endl;
      error = -1;
    }
    if(error < 0) {
	return error;
    }

    // Destroy component instances
    myTest->remove();

    // Un-Deployment

    cout << ">>>> Stop Test Client: " << __FILE__ << endl;

    globalTimer.stop();
    cout << eval.getTimerResult(globalTimer,1,1);
}
