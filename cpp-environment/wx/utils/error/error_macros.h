// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: error_macros.h,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#ifndef wx_utils_error_error_macros_h
#define wx_utils_error_error_macros_h

#include <sstream>

namespace WX {
namespace Utils {

/**

   \def COMPOSE_ERROR(ErrorType, var)

   \ingroup utils_error

   \brief Instantiate an error of type \c ErrorType, with name \c var

 */
#define COMPOSE_ERROR(ErrorType, var) \
ErrorType var; \
var.file(__FILE__).line(__LINE__);

/**

   \def COMPOSE_ERROR_ERROR(ErrorType, var, e)

   \ingroup utils_error

   \brief Instantiate an error of type \c ErrorType, with name \c var,
   which contains the nested error \c e

 */
#define COMPOSE_ERROR_ERROR(ErrorType, var, e) \
COMPOSE_ERROR(ErrorType, var);\
{\
::WX::Utils::ErrorTrace tr;\
tr.append(e);\
var.trace(tr);\
}

/**

   \def COMPOSE_ERROR_MSG(ErrorType, var, msg)

   \ingroup utils_error

   \brief Instantiate an error of type \c ErrorType, with name \c var,
   which contains the message \c msg

   Instantiate an error of type \c ErrorType, with name \c var, which
   contains the message \c msg. \c msg is an expression that can
   contain anything an \c ostream can take.

 */
#define COMPOSE_ERROR_MSG_MAKE_MSG(var, msg) \
{\
   std::ostringstream os;\
   os << msg;\
   var.message(os.str());\
}

#define COMPOSE_ERROR_MSG(ErrorType, var, msg) \
COMPOSE_ERROR(ErrorType, var) \
COMPOSE_ERROR_MSG_MAKE_MSG(var, msg)

/**

   \def COMPOSE_ERROR_TRACE_MSG(ErrorType, var, tr, msg)

   \ingroup utils_error

   \brief Instantiate an error of type \c ErrorType, with name \c var,
   which contains a nested list of errors, \c tr, and contains the
   message \c msg

   Instantiate an error of type \c ErrorType, with name \c var, which
   contains a nested list of errors, \c tr, and contains the message
   \c msg. \c msg is an expression that can contain anything an \c
   ostream can take.

 */
#define COMPOSE_ERROR_TRACE_MSG(ErrorType, var, tr, msg) \
COMPOSE_ERROR_MSG(ErrorType, var, msg) \
var.trace(tr);

/**

   \def COMPOSE_ERROR_ERROR_MSG(ErrorType, var, e, msg)

   \ingroup utils_error

   \brief Instantiate an error of type \c ErrorType, with name \c var,
   which contains a nested error, \c e, and contains the message \c
   msg

   Instantiate an error of type \c ErrorType, with name \c var, which
   contains a nested error, \c e, and contains the message \c msg. \c
   msg is an expression that can contain anything an \c ostream can
   take.

 */
#define COMPOSE_ERROR_ERROR_MSG(ErrorType, var, e, msg) \
COMPOSE_ERROR_MSG(ErrorType, var, msg) \
{\
::WX::Utils::ErrorTrace tr;\
tr.append(e);\
var.trace(tr);\
}

/**

   \def THROW_ERROR(ErrorType)

   \ingroup utils_error

   \brief Throws an error that is composed using \ref COMPOSE_ERROR

 */
#define THROW_ERROR(ErrorType) \
{ \
   COMPOSE_ERROR(ErrorType, var); \
   throw var; \
}

/**

   \def THROW_ERROR_MSG(ErrorType, msg)

   \ingroup utils_error

   \brief Throws an error that is composed using \ref COMPOSE_ERROR_MSG

 */
#define THROW_ERROR_MSG(ErrorType, msg) \
{ \
   COMPOSE_ERROR_MSG(ErrorType, var, msg); \
   throw var; \
}

/**

   \def THROW_ERROR_TRACE_MSG(ErrorType, tr, msg)

   \ingroup utils_error

   \brief Throws an error that is composed using \ref
   COMPOSE_ERROR_TRACE_MSG

 */
#define THROW_ERROR_TRACE_MSG(ErrorType, tr, msg) \
{ \
   COMPOSE_ERROR_TRACE_MSG(ErrorType, var, tr, msg); \
   throw var; \
}

/**

   \def THROW_ERROR_ERROR_MSG(ErrorType, e, msg)

   \ingroup utils_error

   \brief Throws an error that is composed using \ref
   COMPOSE_ERROR_ERROR_MSG

 */
#define THROW_ERROR_ERROR_MSG(ErrorType, e, msg) \
{ \
   COMPOSE_ERROR_ERROR_MSG(ErrorType, var, e, msg); \
   throw var; \
}

} // /namespace
} // /namespace

#endif
