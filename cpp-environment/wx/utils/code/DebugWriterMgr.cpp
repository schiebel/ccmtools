//
// $Id: DebugWriterMgr.cpp,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//

#include "DebugWriterMgr.h"

namespace WX {
namespace Utils {

DebugWriterMgr* DebugWriterMgr::inst_ =0;

DebugWriterMgr::DebugWriterMgr() {
  defaultWriter_      = &CerrDebugWriter::instance();
  debugWriter_        = defaultWriter_;
  explicitWriter_     = 0;
}

DebugWriterMgr& DebugWriterMgr::instance() {
  if (inst_ == 0) {
    inst_ = new DebugWriterMgr;
  }
  return *inst_;
}

void DebugWriterMgr::setDebugWriter(DebugWriter* debWriter) {
  explicitWriter_ = debugWriter_ = debWriter;  
}

DebugWriter& DebugWriterMgr::getDebugWriter() {
  return *debugWriter_;
}

void DebugWriterMgr::activate() {
  if (explicitWriter_ != 0) {
    debugWriter_ = explicitWriter_;
  }
}

void DebugWriterMgr::deactivate() {
  debugWriter_ = defaultWriter_;
}


} // /namespace
} // /namespace
