//
// $Id: CerrDebugWriter.h,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#ifndef wx_utils_CerrDebugWriter_h
#define wx_utils_CerrDebugWriter_h

#include "DebugWriter.h"

namespace WX {
namespace Utils {


class CerrDebugWriter : public DebugWriter {
public:
  int write(const char* file, int line,
            const std::string& facility,
            const int level,
            const std::string& msg);
  bool check(const std::string& facility);
  static CerrDebugWriter& instance();   // ensures singleton
private:
  static CerrDebugWriter* inst_;        // the instance of CerrDebugWriter
private:
  CerrDebugWriter() {};
};

} // /namespace
} // /namespace


#endif // end of wx_utils_CerrDebugWriter_h
