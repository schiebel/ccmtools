
#include <CORBA.h>
#include <coss/CosNaming.h>
#include <mico/CCMContainer.h>
#include "Math.h"

using namespace std;

//******************************************************************************
// Implementation of the test client
//******************************************************************************

int main (int argc, char *argv[])
{
  // Initialize ORB and Naming-Service
  CORBA::ORB_var orb = CORBA::ORB_init (argc, argv);
  CORBA::Object_var obj = orb->resolve_initial_references ("NameService");
  CosNaming::NamingContextExt_var nc =
    CosNaming::NamingContextExt::_narrow (obj);
  assert (!CORBA::is_nil (nc));


  // Deployment *************************************************************

  // Find ComponentHomes in the Naming-Service
  obj = nc->resolve_str ("MathHome");
  assert (!CORBA::is_nil (obj));
  MathHome_var myMathHome = MathHome::_narrow (obj);


  // Deployment *************************************************************

  // Create component instances
  Math_var myMath = myMathHome->create();

  IntegerStack_var stack = myMath->provide_stack();

  myMath->configuration_complete();


  // Use components *********************************************************


  // Common variables for time measurement

  cout << "** Start Measurement **" << endl;
  clock_t start, stop;
  double cpu_time_used;
  const long LOOP1 = 1;
  const long LOOP2 = 10000;
  const long NUMBER_OF_MEASUREMENTS = 5;

  for(int m = 0; m < NUMBER_OF_MEASUREMENTS; m++) {
    cout << "  " << LOOP1 << " loops with " << LOOP2 << " push/pop calls" << endl;
    start = clock();
    for(int j = 0; j < LOOP1; j++) 
      {
	for(int i=0;i<LOOP2;++i) {
	  stack->push(i);
	}
	for(int i=0;i<LOOP2;++i) {
	  stack->pop();
	}
      }
    stop = clock();
    cpu_time_used = ((double)(stop-start)) / CLOCKS_PER_SEC * 1000.0;
    cout << "  time: " << cpu_time_used << "ms" << endl;
  }
  // Un-Deployment **********************************************************

  myMath->remove();
}






