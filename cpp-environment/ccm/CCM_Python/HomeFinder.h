//==============================================================================
// homefinder singleton
//==============================================================================
#ifndef __homefinder__PYTHON__H__
#define __homefinder__PYTHON__H__

#include <Python.h>

namespace CCM_Python {


typedef struct {
    const char *id;
    PyTypeObject *type;
} home_info;

// returns borrowed reference
PyObject *get_global_dict( );
// specify an alternate global dictionary instead of __main__
void set_global_dict( PyObject * );

// internal implementation
int homefinder_init_internal_( PyObject *module, home_info[], PyObject *global_dict );

// returns 0 upon failure
 int homefinder_init( PyObject *module, home_info info[] );

} // namespace CCM_Python

#endif

