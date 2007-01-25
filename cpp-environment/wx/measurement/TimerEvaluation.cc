#include <sstream>
#include <iostream>
#include <sys/times.h>

#include "TimerEvaluation.h" 

namespace WX {
namespace Utils {

using namespace std;

TimerEvaluation::TimerEvaluation()
{

}

TimerEvaluation::~TimerEvaluation()
{

}

string
TimerEvaluation::getTimerResult(const Timer& timer, 
				const long loops, 
				const long size)
{
  /*
  ostringstream os;  
  os << "loops(" << loops << ") size(" << size << ") " 
     << timer << endl;  
  return os.str();
  */

  ostringstream os;
  os << "loops(" << loops << ") "
     << "size("  << size << ") "
     << "real("  << timer.getRealTime() << ")s "
     << "call("  << (timer.getRealTime()/loops)*1.0e6 << ")us"
     << endl;  
  return os.str();
}



} // /namespace
} // /namespace
