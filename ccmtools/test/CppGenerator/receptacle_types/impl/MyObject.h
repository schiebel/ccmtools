
#ifndef __MY_OBJECT__H__
#define __MY_OBJECT__H__

#include <iostream>
#include <CCM_Local/Console.h>

class MyObject 
  : virtual public CCM_Local::CCM_Console
{
public:
  MyObject();

  const std::string prompt() const
    throw(LocalComponents::CCMException);

  void prompt(const std::string value) 
    throw(LocalComponents::CCMException);

  long println(const std::string& msg);
  
private: 
  std::string prompt_;
};


#endif
