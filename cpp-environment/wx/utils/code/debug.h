// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: debug.h,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#ifndef wx_utils_code_debug_h
#define wx_utils_code_debug_h

#include <sstream>
#include <string>

#include "DebugWriterMgr.h"

namespace WX {
namespace Utils {

/**

   \brief Enable/disable/select debugging.

   \ingroup utils_code

*/
class Debug {
public:
   Debug(bool read_env=true);
   Debug(const std::string& levels, bool read_env=true);
   ~Debug();

   /** Have all debugging messages printed out */
   void set_global(bool b) { global_=b; }

   /** Check whether we output all debugging messages */
   bool get_global() { return global_; }

   /** Have debugging messages with \c level printed out */
   void add_level(const std::string& level);

   void parse_levels(const std::string& levelstr);

   /** Check whether we output debugging messages with \c level */
   bool have_level(const std::string& level);
   int n_levels();

   /** singleton */
   static Debug& instance();

private:
   class DebugImpl* impl_;
   bool global_;
};

/**

   \def LDEBUGNL(level,msg)

   \ingroup utils_code

   \brief Output a message \a msg plus newline if \a level is on.

   \a level is stringified by the C preprocessor, so you don't enclose
   it into double quotes.

   \a msg is anything an ostream can handle. For example,

   \code 
   
   int n = some_number();
   LDEBUGNL(MyLevel, "Here I have "<<n<<" pieces");

   \endcode

*/

/**

   \def LDEBUG(level,msg)

   \brief Same as LDEBUGNL, but without newline

   \ingroup utils_code

*/

      
#ifdef WXDEBUG
#  define WRITE_MSG_TO_DEBUGWRITER(msg,facility,level)\
   {\
      std::ostringstream os;\
      os << msg << std::ends;\
      WX::Utils::DebugWriterMgr::instance().getDebugWriter().write(__FILE__,__LINE__,facility,\
                          level,os.str());\
   }

  // Trace level
#  define TRACE(msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(""))\
      WRITE_MSG_TO_DEBUGWRITER(msg, "",WX::Utils::DebugWriter::Trace)\
   }

#  define TRACENL(msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(""))\
         WRITE_MSG_TO_DEBUGWRITER(msg<<'\n',"",WX::Utils::DebugWriter::Trace)\
   }

#  define LTRACE(facility,msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(#facility))\
         WRITE_MSG_TO_DEBUGWRITER(msg,#facility,WX::Utils::DebugWriter::Trace)\
   }

#  define LTRACENL(facility,msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(#facility))\
         WRITE_MSG_TO_DEBUGWRITER(msg<<'\n',#facility,WX::Utils::DebugWriter::Trace)\
   }

  // Debug level
#  define DEBUG(msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(""))\
      WRITE_MSG_TO_DEBUGWRITER(msg, "",WX::Utils::DebugWriter::Debug)\
   }

#  define DEBUGNL(msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(""))\
         WRITE_MSG_TO_DEBUGWRITER(msg<<'\n',"",WX::Utils::DebugWriter::Debug)\
   }

#  define LDEBUG(facility,msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(#facility))\
         WRITE_MSG_TO_DEBUGWRITER(msg,#facility,WX::Utils::DebugWriter::Debug)\
   }

#  define LDEBUGNL(facility,msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(#facility))\
         WRITE_MSG_TO_DEBUGWRITER(msg<<'\n',#facility,WX::Utils::DebugWriter::Debug)\
   }

  // Notify level
#  define NOTIFY(msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(""))\
      WRITE_MSG_TO_DEBUGWRITER(msg, "",WX::Utils::DebugWriter::Notify)\
   }

#  define NOTIFYNL(msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(""))\
         WRITE_MSG_TO_DEBUGWRITER(msg<<'\n',"",WX::Utils::DebugWriter::Notify)\
   }

#  define LNOTIFY(facility,msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(#facility))\
         WRITE_MSG_TO_DEBUGWRITER(msg,#facility,WX::Utils::DebugWriter::Notify)\
   }

#  define LNOTIFYNL(facility,msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(#facility))\
         WRITE_MSG_TO_DEBUGWRITER(msg<<'\n',#facility,WX::Utils::DebugWriter::Notify)\
   }

  // Alert level
#  define ALERT(msg)\
   {\
      if (WX::Utils::EmergWriterMgr::instance().getDebugWriter().check(""))\
      WRITE_MSG_TO_DEBUGWRITER(msg, "",WX::Utils::DebugWriter::Alert)\
   }

#  define ALERTNL(msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(""))\
         WRITE_MSG_TO_DEBUGWRITER(msg<<'\n',"",WX::Utils::DebugWriter::Alert)\
   }

#  define LALERT(facility,msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(#facility))\
         WRITE_MSG_TO_DEBUGWRITER(msg,#facility,WX::Utils::DebugWriter::Alert)\
   }

#  define LALERTNL(facility,msg)\
   {\
      if (WX::Utils::DebugWriterMgr::instance().getDebugWriter().check(#facility))\
         WRITE_MSG_TO_DEBUGWRITER(msg<<'\n',#facility,WX::Utils::DebugWriter::Alert)\
   }

#else
#  define TRACE(msg)
#  define TRACENL(msg)
#  define LTRACE(facility,msg)
#  define LTRACENL(facility,msg)
#  define DEBUG(msg)
#  define DEBUGNL(msg)
#  define LDEBUG(facility,msg)
#  define LDEBUGNL(facility,msg)
#  define NOTIFY(msg)
#  define NOTIFYNL(msg)
#  define LNOTIFY(facility,msg)
#  define LNOTIFYNL(facility,msg)
#  define ALERT(msg)
#  define ALERTNL(msg)
#  define LALERT(facility,msg)
#  define LALERTNL(facility,msg)
#endif

} // /namespace
} // /namespace

#endif
