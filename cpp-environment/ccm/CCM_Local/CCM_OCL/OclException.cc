/*$Id: OclException.cc,v 1.1 2005/01/11 09:06:56 teiniker Exp $*/

/********************\
*                    *
*   OCL exceptions   *
*                    *
\********************/


#include "OclException.h"
#include <sstream>

using namespace std;


namespace CCM_OCL {


OclException::OclException( const char* message, const char* function, const char* file, int line ) throw()
: CCMException(LocalComponents::OCL_ERROR),
  message_(message), function_(function), file_(file), line_(line)
{
	try
	{
		ostringstream stream;
		stream << "OCL exception: " << message << endl
		       << "     function: " << function << endl
		       << "         file: " << file << endl
		       << "         line: " << line << endl;
		what_ = stream.str();
	}
	catch(...)
	{
		what_ = message_;
	}
}


OclException::OclException( const OclException& rhs ) throw()
: CCMException(LocalComponents::OCL_ERROR),
  message_(rhs.message_), function_(rhs.function_), file_(rhs.file_),
  what_(rhs.what_), line_(rhs.line_)
{
}


const OclException& OclException::operator=( const OclException& rhs ) throw()
{
    message_ = rhs.message_;
    function_ = rhs.function_;
    file_ = rhs.file_;
    what_ = rhs.what_;
    line_ = rhs.line_;
    return *this;
}


OclException::~OclException() throw()
{
}


/**
 * Returns the full error message.
 */
const char* OclException::what() const throw()
{
	return what_.c_str();
}


InvariantException::InvariantException( const char* message, const char* function,
                                        const char* file, int line, bool onExit ) throw()
: OclException(message, function, file, line), onExit_(onExit)
{
	try
	{
		ostringstream stream;
		stream << "OCL exception: " << message << endl
		       << "     function: " << function << (onExit ? "  on exit" : "  on entry") << endl
		       << "         file: " << file << endl
		       << "         line: " << line << endl;
		what_ = stream.str();
	}
	catch(...)
	{
		what_ = message_;
	}
}

InvariantException::InvariantException( const InvariantException& rhs ) throw()
: OclException(rhs), onExit_(rhs.onExit_)
{
}

const InvariantException& InvariantException::operator=( const InvariantException& rhs ) throw()
{
	OclException::operator=(rhs);
	onExit_ = rhs.onExit_;
	return *this;
}

InvariantException::~InvariantException() throw()
{
}


PreconditionException::PreconditionException( const char* message, const char* function,
                                              const char* file, int line ) throw()
: OclException(message, function, file, line)
{
}

PreconditionException::PreconditionException( const PreconditionException& rhs ) throw()
: OclException(rhs)
{
}

PreconditionException::~PreconditionException() throw()
{
}


PostconditionException::PostconditionException( const char* message, const char* function,
                                                const char* file, int line ) throw()
: OclException(message, function, file, line)
{
}

PostconditionException::PostconditionException( const PostconditionException& rhs ) throw()
: OclException(rhs)
{
}

PostconditionException::~PostconditionException() throw()
{
}


} // /namespace CCM_OCL


