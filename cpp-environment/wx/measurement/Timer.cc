#include <sstream>
#include <iostream>
#include <sys/times.h>

#include "Timer.h" 

namespace WX {
namespace Utils {

using namespace std;

Timer::Timer()
{
  realTimeStart = realTimeStop = 0;
  userTimeStart = userTimeStop = 0;
  systemTimeStart = systemTimeStop =0;
}

Timer::~Timer()
{
}

void
Timer::start()
{
  tms timeStructure;
  realTimeStart = times(&timeStructure);
  userTimeStart = timeStructure.tms_utime;
  systemTimeStart = timeStructure.tms_stime;
}

void 
Timer::stop()
{
  tms timeStructure;
  realTimeStop = times(&timeStructure);
  userTimeStop = timeStructure.tms_utime;
  systemTimeStop = timeStructure.tms_stime;
}

double 
Timer::getRealTime() const
{
  return double(realTimeStop - realTimeStart) / sysconf(_SC_CLK_TCK);
}

double 
Timer::getUserTime() const
{
  return double(userTimeStop - userTimeStart) / sysconf(_SC_CLK_TCK);
}

double 
Timer::getSystemTime() const
{
  return double(systemTimeStop - systemTimeStart) / sysconf(_SC_CLK_TCK);
}

string 
Timer::toString() const
{
  ostringstream os;
  os << "real(" << getRealTime() << ")s ";
  os << "user(" << getUserTime() << ")s ";
  os << "sys(" << getSystemTime() << ")s ";
  return os.str();
}


ostream&
operator<<(std::ostream& o, const Timer& value)
{
  o << value.toString();
  return o;
}

} // /namespace
} // /namespace
