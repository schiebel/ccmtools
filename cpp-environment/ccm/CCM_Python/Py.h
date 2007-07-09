#ifndef _py__H__
#define _py__H__
#include <Python.h>
#include <string>
#include <list>

namespace CCM_Python {

class Py {
    public:

	class error {
	    public:
		error( std::string msg ) : message_(msg) { }
		const std::string &message( ) const { return message_; }
	    private:
		std::string message_;
	};

	virtual ~Py( );
	Py( );

	virtual void lock( );
	virtual void unlock( );

	// delete the returned string (always returns non-null)
	char *findpath( );
	// delete the returned string (may return null)
	char *findfile( const char * );

	virtual int evalString(const char *);
	virtual int evalFile(const char *);

	static PyThreadState *mainThread( ) { return main_thread; }
	virtual PyThreadState *thread( ) { return main_thread; }

	static void init( int argc, char **argv, std::string load_path="", std::string init_file="",
			  bool setup_ipython=true, std::string ipython_namespace="__ipython_namespace__" );

    protected:
	unsigned int lock_count;
	PyThreadState *suspended_thread;
	void set_main_thread( PyThreadState *mt );

    private:

	static int argc;
	static char **argv;
	static unsigned int ref_count;
	static bool using_ipython;
	static std::string init_file;
	static std::string load_path;
	static std::string ipython_ns;
	static std::list<std::string> *path;
	bool did_lock;

	static PyThreadState *main_thread;

	void init( );
	void init_substitute( );
	void init_path( );

	// leave undefined (to prevent assignment)
	Py &operator=(const Py &);
	void *operator new( size_t );
};

}

#endif

