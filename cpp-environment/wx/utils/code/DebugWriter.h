//
// $Id: DebugWriter.h,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#ifndef wx_utils_DebugWriter_h
#define wx_utils_DebugWriter_h

#include <string>

namespace WX {
namespace Utils {


class DebugWriter {
public:

  // Debug levels
  enum {
    Emerg,
    Alert,
    Error,
    Notify,
    Debug,
    Trace
  };

  virtual int write(const char* file, int line,
                    const std::string& facility,
                    const int level,
                    const std::string& msg)=0;
  virtual bool check(const std::string& facility)=0;
  virtual ~DebugWriter() {};
};

} // /namespace
} // /namespace


#endif // end of wx_utils_DebugWriter_h
