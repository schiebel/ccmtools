#ifndef __CCM_OCL__OCL_EXCEPTION__H__
#define __CCM_OCL__OCL_EXCEPTION__H__

/*$Id: OclException.h,v 1.1 2005/01/11 09:06:56 teiniker Exp $*/


/********************\
*                    *
*   OCL exceptions   *
*                    *
\********************/


#include <string>
#include <LocalComponents/CCM.h>


namespace CCM_OCL {

using std::string;
using LocalComponents::CCMException;


/**
 * Base class for all OCL exceptions.
 */
class OclException : public CCMException
{
protected:
	/// error message
	string message_;

	/// function
	string function_;

	/// source file
	string file_;

	/// full error message
	string what_;

	/// line number
	int line_;

public:
	virtual ~OclException() throw();
	OclException( const char* message, const char* function, const char* file, int line ) throw();
	OclException( const OclException& rhs ) throw();
	const OclException& operator=( const OclException& rhs ) throw();
	virtual const char *what() const throw();

	/**
	 * Returns the error message.
	 */
	const string& getMessage() const throw()
	{
		return message_;
	}

	/**
	 * Returns the function name.
	 */
	const string& getFunction() const throw()
	{
		return function_;
	}

	/**
	 * Returns the source file.
	 */
	const string& getFile() const throw()
	{
		return file_;
	}

	/**
	 * Returns the line number.
	 */
	int getLine() const throw()
	{
		return line_;
	}
};


/**
 * An invariant failed.
 */
class InvariantException : public OclException
{
protected:
	/// false=function entry  true=function exit
	bool onExit_;

public:
	virtual ~InvariantException() throw();
	InvariantException( const char* message, const char* function, const char* file, int line, bool onExit ) throw();
	InvariantException( const InvariantException& rhs ) throw();
	const InvariantException& operator=( const InvariantException& rhs ) throw();

	/**
	 * false=function entry  true=function exit
	 */
	bool isOnExit() const throw()
	{
		return onExit_;
	}
};


/**
 * A precondition failed.
 */
class PreconditionException : public OclException
{
public:
	virtual ~PreconditionException() throw();
	PreconditionException( const char* message, const char* function, const char* file, int line ) throw();
	PreconditionException( const PreconditionException& rhs ) throw();
};


/**
 * A postcondition failed.
 */
class PostconditionException : public OclException
{
public:
	virtual ~PostconditionException() throw();
	PostconditionException( const char* message, const char* function, const char* file, int line ) throw();
	PostconditionException( const PostconditionException& rhs ) throw();
};


} // /namespace CCM_OCL

#endif // __CCM_OCL__OCL_EXCEPTION__H__


