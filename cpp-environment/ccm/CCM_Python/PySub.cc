#include <Python.h>
#include <CCM_Python/PySub.h>

namespace CCM_Python {

int PySub::initialized = 0;

PySub::PySub( ) : Py( ) {
    if ( ! initialized ) {
	PyEval_InitThreads();
	set_main_thread( PyThreadState_Get() );
	initialized += 1;
	Py_NewInterpreter( );
	current_thread = PyEval_SaveThread( );
	PyEval_RestoreThread(current_thread);
	PyThreadState_Swap(mainThread());
	PyEval_ReleaseLock( );
    } else {
	PyEval_AcquireLock();
	PyThreadState *cur = PyEval_SaveThread( );
	Py_NewInterpreter( );
	current_thread = PyEval_SaveThread();
	/*fprintf(stderr, "\t\t\tpysub:\t0x%x <=> 0x%x\n", cur, current_thread);*/
	PyEval_RestoreThread(cur);
	PyEval_ReleaseLock( );
    }
}

PySub::~PySub( ) { }

}
