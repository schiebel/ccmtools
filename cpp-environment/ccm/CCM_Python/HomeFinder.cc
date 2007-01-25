//==============================================================================
// homefinder singleton
//==============================================================================

#include <CCM_Python/HomeFinder.h>
#include <map>
#include <string>

#define HOMEFINDER_STRING "homefinder"

namespace CCM_Python {

static PyObject *homefinder_exception = 0;
static PyObject *alternate_global_dict = 0;
static PyObject *global_module = 0;
static PyObject *global_dict = 0;

PyObject *get_global_dict( ) {

    if ( alternate_global_dict )
	return alternate_global_dict;
    if ( global_dict )
	return global_dict;

    global_module = PyImport_AddModule("__main__");
    Py_XINCREF(global_module);

    if ( global_module != NULL )
	global_dict = PyModule_GetDict(global_module);

    return global_dict;
}
void set_global_dict( PyObject *o ) {
    Py_XDECREF( alternate_global_dict );
    alternate_global_dict = o;
    Py_XINCREF( alternate_global_dict );
}

//
//  ------  homefinder object struct  ------
//
typedef struct {
  PyObject_HEAD
  static std::map<std::string,PyTypeObject*> *homemap;
  static PyObject *finder;
} Pyhomefinder_object;

std::map<std::string,PyTypeObject*> *Pyhomefinder_object::homemap = 0;
PyObject *Pyhomefinder_object::finder = 0;


//
//  ------  find_home_by_name  ------
//
static PyObject *
find_home_by_name( PyObject *self, PyObject *args ) {

    const char *name = 0;
    if ( ! PyArg_ParseTuple( args, "s", &name ) )
        return NULL;

    if (Pyhomefinder_object::homemap->find(name) == Pyhomefinder_object::homemap->end()) {
        PyErr_Format( homefinder_exception, "couldn't find '%s' home", name );
	return NULL;
    }

    PyTypeObject *type = (*Pyhomefinder_object::homemap)[name];
    PyObject *result = type->tp_new( type, NULL, NULL );

    if ( ! result ) {
        PyErr_SetString( homefinder_exception, "home allocation failed" );
	return NULL;
    }

    int ok = type->tp_init( result, NULL, NULL );
    if ( ok != 0 ) {
        Py_DECREF( result );
	result = NULL;
    }
    return result;
}

//
//  ------  find_home_by_home_type  ------
//
static PyObject *
find_home_by_home_type( PyObject *self, PyObject *args ) {
    PyErr_SetString( homefinder_exception, "sorry homefinder.find_home_by_home_type not yet implemented" );
    return NULL;
}

//
//  ------  find_home_by_component_type  ------
//
static PyObject *
find_home_by_component_type( PyObject *self, PyObject *args ) {
    PyErr_SetString( homefinder_exception, "sorry homefinder.find_home_by_component_type not yet implemented" );
    return NULL;
}

//
//  ------  string representation  ------
//
static PyObject *
Pyhomefinder_str( Pyhomefinder_object *self ) {
    return PyString_FromString( "homefinder\n    find_home_by_name( <string> )" );
}

//
//  ------  home destructor  ------
//
static void
Pyhomefinder_dealloc( Pyhomefinder_object *self ) {
    self->ob_type->tp_free( (PyObject*) self );
}


//
// ------  home constructor  ------
//
static PyObject *
Pyhomefinder_new( PyTypeObject *type, PyObject *args, PyObject *keywds ) {
    Pyhomefinder_object *self = (Pyhomefinder_object*) type->tp_alloc(type,0);
    return (PyObject*) self;
}

//
//  ------  home initialization  ------
//
static int
Pyhomefinder_init( Pyhomefinder_object *self, PyObject *args, PyObject *keywds ) {
    return 0;
}

//
//  ------  home method table ------
//
static PyMethodDef Pyhomefinder_methods[] = {
    { "find_home_by_name", find_home_by_name, METH_VARARGS,
      "find a home by name"
    },
    { "find_home_by_home_type", find_home_by_home_type, METH_VARARGS,
      "find a home by home type"
    },
    { "find_home_by_component_type", find_home_by_component_type, METH_VARARGS,
      "find a home by component type"
    },
    { NULL }
};


//
//  ------  home object table ------
//
static PyTypeObject Pyhomefinder_type = {
    PyObject_HEAD_INIT ( NULL )
    0,									/* ob_size */
    HOMEFINDER_STRING,							/* tp_name */
    sizeof ( Pyhomefinder_object ),					/* tp_basicsize */
    0,									/* tp_itemsize */

    /* methods */
    (destructor) Pyhomefinder_dealloc,				/* tp_dealloc */
    0,									/* tp_print */
    0,									/* tp_getattr */
    0,									/* tp_setattr */
    0,									/* tp_compare */
    0,									/* tp_repr */
    0,									/* tp_as_number */
    0,						   			/* tp_as_sequence */
    0,						   		  	/* tp_as_mapping */
    0,						   		 	/* tp_hash */
    0,						   	   		/* tp_call */
    (reprfunc) Pyhomefinder_str,		   	   		/* tp_str */
    0,						   	  		/* tp_getattro */
    0,									/* tp_setattro */
    0,									/* tp_as_buffer */

    Py_TPFLAGS_DEFAULT,				   			/* tp_flags */

    "homefinder is used to create CarRental components",	/* tp doc */

    0,		  							/* tp_traverse */
    0,									/* tp_clear */
    0,									/* tp_richcompare */
    0,									/* tp_weaklistoffset */
    0,									/* tp_iter */
    0,									/* tp_iternext */

    Pyhomefinder_methods,						/* tp_methods */
    NULL,								/* tp_members */

    0,									/* tp_getset */
    0,									/* tp_base */
    0,									/* tp_dict */
    0,									/* tp_descr_get */
    0,									/* tp_descr_set */
    0,									/* tp_dictoffset */
    (initproc) Pyhomefinder_init,					/* tp_init */
    0,									/* tp_alloc */
    Pyhomefinder_new,							/* tp_new */
};

//
//  ------  external homefinder initialization hook ------
//
int homefinder_init( PyObject *module, home_info info[] ) {

    PyObject *global_dict = (alternate_global_dict ? alternate_global_dict : PyModule_GetDict(PyImport_AddModule("__main__")));
    Py_XINCREF(global_dict);

    if ( ! module ) {
        PyErr_SetString( homefinder_exception, "null module pointer passed to homefinder_init()" );
	Py_XDECREF(global_dict);
	return 0;
    }

    if ( ! Pyhomefinder_object::homemap )
        Pyhomefinder_object::homemap = new std::map<std::string,PyTypeObject*>( );

    if ( ! Pyhomefinder_object::homemap ) {
        PyErr_SetString( homefinder_exception, "allocation failed in homefinder_init()" );
	Py_XDECREF(global_dict);
        return 0;
    }

    if ( PyType_Ready( &Pyhomefinder_type ) < 0 ) {
        PyErr_SetString( homefinder_exception, "type initialization failed in homefinder_init()" );
	Py_XDECREF(global_dict);
        return 0;
    }

    homefinder_exception = PyErr_NewException("homefinder.error", NULL, NULL);

    for ( int i=0; info && info[i].id; ++i ) {
        if ( info[i].type )
	  (*Pyhomefinder_object::homemap)[info[i].id] = info[i].type;
    }

    if ( ! Pyhomefinder_object::finder ) {
        Pyhomefinder_object::finder = Pyhomefinder_type.tp_new( &Pyhomefinder_type, NULL, NULL );
        if ( Pyhomefinder_object::finder == NULL ) {
            PyErr_SetString( homefinder_exception, "allocation failed in homefinder_init()" );
	    Py_XDECREF(global_dict);
	    return 0;
	}
	if ( Pyhomefinder_type.tp_init( Pyhomefinder_object::finder, NULL, NULL ) != 0 ) {
            PyErr_SetString( homefinder_exception, "object initialization failed in homefinder_init()" );
	    Py_XDECREF(global_dict);
	    return 0;
	}
    }

    Py_INCREF(Pyhomefinder_object::finder);
    PyModule_AddObject( module, HOMEFINDER_STRING, Pyhomefinder_object::finder );

    //
    //  -- should we add 'homefinder' to the global namespace?
    //     only if 'homefinder' isn't already defined...
    //
    if ( global_dict != NULL && PyDict_Check(global_dict) ) {
	if ( ! PyDict_GetItemString( global_dict, HOMEFINDER_STRING ) ) {
	    Py_INCREF(Pyhomefinder_object::finder);
	    PyDict_SetItemString( global_dict, HOMEFINDER_STRING, Pyhomefinder_object::finder );
	}
    }

    Py_XDECREF(global_dict);
    return 1;
}

} // namespace CCM_Python

