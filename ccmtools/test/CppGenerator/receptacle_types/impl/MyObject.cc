
#include<iostream>

#include "MyObject.h"

using namespace std;

MyObject::MyObject() 
{
    prompt_="-=> ";
}

const std::string 
MyObject::prompt() const
  throw(LocalComponents::CCMException) 
{
  return prompt_;
}

void 
MyObject::prompt(const std::string value) 
  throw(LocalComponents::CCMException) {
  prompt_=value;
}

long 
MyObject::println(const std::string& msg) {
  cout << prompt_ << msg << endl;
  return msg.length();
}
