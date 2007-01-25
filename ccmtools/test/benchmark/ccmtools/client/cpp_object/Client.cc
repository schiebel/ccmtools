/***
 * Local Object Test Client 
 *
 ***/

#include <cassert>
#include <iostream>

#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>

#include <WX/Utils/Timer.h>
#include <WX/Utils/TimerEvaluation.h>

#include <CCM_Local/CCM_Session_Test/Test_bm_impl.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace CCM_Session_Test;


int main(int argc, char *argv[])
{
    WX::Utils::Timer globalTimer;
    WX::Utils::TimerEvaluation eval;
    globalTimer.start();

    cout << ">>>> Start Test Client: " << __FILE__ << endl;
    int error = 0;

    try {
      cout << "--- Start Test Case -----------------------------------" << endl;

      // Test configuration
      WX::Utils::Timer timer;
      
      const long MAX_LOOP_COUNT = 100000000;

      const long SEQUENCE_SIZE_MAX = 1000;
      const long SEQUENCE_SIZE_STEP = 100;

      bm_impl bm(NULL);


      //----------------------------------------------------------
      // ping test case
      //----------------------------------------------------------

      {
	// ping
	cout << endl;
	cout << "Object Test: void f0() "; 

	timer.start();
	for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	  bm.f0();
	}
	timer.stop();
	cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,1);
      }



      //----------------------------------------------------------
      // in parameter test cases 
      //----------------------------------------------------------

      {
	cout << endl;
	// in long parameter
	cout << "Object Test: void f_in1(in long l1) "; 

	long value = 7;
	timer.start();
	for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	  bm.f_in1(value);
	}
	timer.stop();
	cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,1);
      }

      {
	cout << endl;
	// in string parameter with increasing size
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Object Test: void f_in2(in string s1) "; 

	  string value;
	  for(long i=0; i<size; i++)
	    value += "X";

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    bm.f_in2(value);
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }

      {
	cout << endl;
	// in sequence of long parameter with increasing size
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Object Test: void f_in3(in LongList ll1) "; 

	  LongList value;
	  for(long i=0; i<size; i++)
	    value.push_back(i);

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    bm.f_in3(value);
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }	


      //----------------------------------------------------------
      // inout parameter test cases 
      //----------------------------------------------------------

      {
	cout << endl;
	// long as result
	cout << "Object Test: void f_inout1(inout long) "; 

	long value = 7;
	long result;
	bm.long_attr(value);

	timer.start();
	for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	  bm.f_inout1(value);
	}
	timer.stop();
	cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,1);
      }

      {
	const long MAX_LOOP_COUNT = 10000000;
	cout << endl;
	// string as result with increasing size
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Object Test: void f_inout2(inout string s1) "; 

	  string value;
	  string result;
	  for(long i=0; i<size; i++)
	    value += "X";
	  bm.string_attr(value);

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    bm.f_inout2(value);
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }

      {
	const long MAX_LOOP_COUNT = 1000000;
	cout << endl;
	// sequence of long as result with increasing size
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Object Test: void f_inout3(inout LongList ll1) "; 

	  LongList value;
	  LongList result;
	  for(long i=0; i<size; i++)
	    value.push_back(i);
	  bm.LongList_attr(value);	  

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    bm.f_inout3(value);
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }	




      //----------------------------------------------------------
      // out parameters test cases 
      //----------------------------------------------------------

      {
	cout << endl;
	// long as result
	cout << "Object Test: void f_out1(out long) "; 

	long value = 7;
	long result;
	bm.long_attr(value);

	timer.start();
	for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	  bm.f_out1(result);
	}
	timer.stop();
	cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,1);
      }

      {
	const long MAX_LOOP_COUNT = 10000000;
	cout << endl;
	// string as result with increasing size
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Object Test: void f_out2(out string s1) "; 

	  string value;
	  string result;
	  for(long i=0; i<size; i++)
	    value += "X";
	  bm.string_attr(value);

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    bm.f_out2(result);
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }

      {
	const long MAX_LOOP_COUNT = 1000000;
	cout << endl;
	// sequence of long as result with increasing size
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Object Test: void f_out3(out LongList ll1) "; 

	  LongList value;
	  LongList result;
	  for(long i=0; i<size; i++)
	    value.push_back(i);
	  bm.LongList_attr(value);	  

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    bm.f_out3(result);
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }	


      
      //----------------------------------------------------------
      // return value test cases 
      //----------------------------------------------------------

      {
	cout << endl;
	// long as result
	cout << "Object Test: long f_ret1() "; 

	long value = 7;
	long result;
	bm.long_attr(value);

	timer.start();
	for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	  result = bm.f_ret1();
	}
	timer.stop();
	cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,1);
      }

      {
	const long MAX_LOOP_COUNT = 10000000;
	cout << endl;
	// string as result with increasing size
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Object Test: string f_ret2() "; 

	  string value;
	  string result;
	  for(long i=0; i<size; i++)
	    value += "X";
	  bm.string_attr(value);

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    result = bm.f_ret2();
	  }
	  timer.stop();
	  cout << eval.getTimerResult(timer,MAX_LOOP_COUNT,size);
	}
      }

      {
	const long MAX_LOOP_COUNT = 1000000;
	cout << endl;
	// sequence of long as result with increasing size
	for(long size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
	  cout << "Object Test: LongList f_ret3() "; 

	  LongList value;
	  LongList result;
	  for(long i=0; i<size; i++)
	    value.push_back(i);
	  bm.LongList_attr(value);	  

	  timer.start();
	  for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
	    result = bm.f_ret3();
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

    cout << ">>>> Stop Test Client: " << __FILE__ << endl;

    globalTimer.stop();
    cout << eval.getTimerResult(globalTimer,1,1);
}
