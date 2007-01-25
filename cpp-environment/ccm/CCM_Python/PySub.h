#ifndef _pysubinterp__H__
#define _pysubinterp__H__
#include <Python.h>
#include <CCM_Python/Py.h>

namespace CCM_Python {

class PySub : public Py {
    public:

        PySub( );
	~PySub( );

	PyThreadState *thread( ) { return current_thread; }

    private:
	static int initialized;
	PyThreadState *current_thread;
};

}

#endif

