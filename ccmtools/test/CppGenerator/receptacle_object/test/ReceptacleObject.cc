#include "ReceptacleObject.h"
#include <WX/Utils/debug.h>

using namespace std;
using namespace CCM_Local;

ReceptacleObject::ReceptacleObject()
{
  DEBUGNL("+ReceptacleObject::ReceptacleObject()");
}

ReceptacleObject::~ReceptacleObject()
{
  DEBUGNL("-ReceptacleObject::~ReceptacleObject()");
}

long 
ReceptacleObject::op_b1(const long p1, long& p2, long& p3)
{
  DEBUGNL(" ReceptacleObject::op_b1()");
  cout << " ReceptacleObject::op_b1()" << endl;
  p3=p2;
  p2=p1;
  return p3+p1;
}

string 
ReceptacleObject::op_b2(const std::string& p1, std::string& p2, std::string& p3)
{
  DEBUGNL(" ReceptacleObject::op_b2()");
  cout << " ReceptacleObject::op_b2()" << endl;
  p3=p2;
  p2=p1;
  return p3+p1;
}
