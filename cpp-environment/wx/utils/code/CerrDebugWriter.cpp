//
// $Id: CerrDebugWriter.cpp,v 1.1 2005/01/11 08:59:23 teiniker Exp $
//

#include "debug.h"
#include "CerrDebugWriter.h"

#include <iostream>

namespace WX {
namespace Utils {

using namespace std;

CerrDebugWriter*  CerrDebugWriter::inst_ = 0;

CerrDebugWriter& CerrDebugWriter::instance() {
  if (inst_ == 0) {
    inst_ = new CerrDebugWriter;
  }
  return *inst_;
}

int CerrDebugWriter::write(const char* file, int line,const string& facility,
                           const int level,const string& msg) {
  cerr<<"["<<file<<":"<<line<<"] "<<msg;
  return 0;
}

bool CerrDebugWriter::check(const string& facility) {
  if (facility.length() == 0) {
    return Debug::instance().get_global();
  } else {
    return (Debug::instance().get_global() ||
            Debug::instance().have_level(facility));
  }
}



} // /namespace
} // /namespace
