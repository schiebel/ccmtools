
#include <CCM_Python/BasicTypes.h>
#include <stdio.h>

static int python_conversion_errno = 0;

void clear_python_conversion_error( ) {
    python_conversion_errno = 0;
}

int get_python_conversion_error( ) {
    return python_conversion_errno;
}

void set_python_conversion_error( ) {
    ++python_conversion_errno;
}

int convert_char_from_python( PyObject *o, void *arg ) {
    char *to = (char*) arg;

    if ( ! is_python_int(o) ) {
	PyErr_SetString( PyExc_TypeError, "not an integer" );
	return 0;
    }

    long value = PyInt_AsLong( o );

    if ( value == -1 && PyErr_Occurred( ) )
	return 0;

    if ( ! within_char_range( value ) ) {
	PyErr_SetString( PyExc_OverflowError, "not within char range" );
	return 0;
    }

    *to = (char) value;
    return 1;
}

int convert_short_from_python( PyObject *o, void *arg ) {
    short *to = (short*) arg;

    if ( ! is_python_int(o) ) {
	PyErr_SetString( PyExc_TypeError, "not an integer" );
	return 0;
    }

    long value = PyInt_AsLong( o );

    if ( value == -1 && PyErr_Occurred( ) )
	return 0;

    if ( ! within_short_range( value ) ) {
	PyErr_SetString( PyExc_OverflowError, "not within short range" );
	return 0;
    }

    *to = (short) value;
    return 1;
}

int convert_unsigned_short_from_python( PyObject *o, void *arg ) {
    unsigned short *to = (unsigned short*) arg;

    if ( ! is_python_int(o) ) {
	PyErr_SetString( PyExc_TypeError, "not an integer" );
	return 0;
    }

    long value = PyInt_AsLong( o );

    if ( value == -1 && PyErr_Occurred( ) )
	return 0;

    if ( ! within_unsigned_short_range( value ) ) {
	PyErr_SetString( PyExc_OverflowError, "not within unsigned short range" );
	return 0;
    }

    *to = (unsigned short) value;
    return 1;
}

int convert_int_from_python( PyObject *o, void *arg ) {
    int *to = (int*) arg;

    if ( ! is_python_int(o) ) {
	PyErr_SetString( PyExc_TypeError, "not an integer" );
	return 0;
    }

    long value = PyInt_AsLong( o );

    if ( value == -1 && PyErr_Occurred( ) )
	return 0;

    if ( ! within_int_range( value ) ) {
	PyErr_SetString( PyExc_OverflowError, "not within integer range" );
	return 0;
    }

    *to = (int) value;
    return 1;
}

int convert_long_from_python( PyObject *o, void *arg ) {

    long *to = (long*) arg;

    if ( ! is_python_long(o) ) {
	PyErr_SetString( PyExc_TypeError, "not a long" );
	return 0;
    }

    long value = PyLong_AsLong( o );

    if ( value == -1 && PyErr_Occurred( ) )
	return 0;

    *to = value;
    return 1;
}

int convert_unsigned_long_from_python( PyObject *o, void *arg ) {

    long *to = (long*) arg;

    if ( ! is_python_long(o) ) {
	PyErr_SetString( PyExc_TypeError, "not a long" );
	return 0;
    }

    unsigned long value = PyLong_AsUnsignedLong( o );

    if ( value == (unsigned long) -1 && PyErr_Occurred( ) )
	return 0;

    *to = value;
    return 1;
}

int convert_long_long_from_python( PyObject *o, void *arg ) {

    long long *to = (long long*) arg;

    if ( ! is_python_long_long(o) ) {
	PyErr_SetString( PyExc_TypeError, "not a long long" );
	return 0;
    }

    long long value = PyLong_AsLongLong( o );

    if ( value == -1 && PyErr_Occurred( ) )
	return 0;

    *to = value;
    return 1;
}

int convert_float_from_python( PyObject *o, void *arg ) {

    float *to = (float*) arg;

    if ( ! is_python_float(o) ) {
	PyErr_SetString( PyExc_TypeError, "not a float" );
	return 0;
    }

    double value = PyFloat_AsDouble( o );

    if ( value == -1 && PyErr_Occurred( ) )
	return 0;

    if ( ! within_float_range( value ) ) {
	PyErr_SetString( PyExc_OverflowError, "not within float range" );
	return 0;
    }

    *to = value;
    return 1;
}

int convert_double_from_python( PyObject *o, void *arg ) {

    double *to = (double*) arg;

    if ( ! is_python_float(o) ) {
	PyErr_SetString( PyExc_TypeError, "not a double" );
	return 0;
    }

    double value = PyFloat_AsDouble( o );

    if ( value == -1 && PyErr_Occurred( ) )
	return 0;

    *to = value;
    return 1;
}

int convert_string_from_python( PyObject *o, void *arg ) {

    std::string *to = (std::string*) arg;

    if ( ! is_python_string(o) ) {
        PyErr_SetString( PyExc_TypeError, "not a string" );
	return 0;
    }

    char *value = PyString_AsString(o);

    if ( ! value ) return 0;

    *to = value;
    return 1;
}

int convert_boolean_from_python( PyObject *o, void *arg ) {
    bool *to = (bool*) arg;

    if ( ! is_python_boolean(o) ) {
	PyErr_SetString( PyExc_TypeError, "not a boolean" );
	return 0;
    }

    *to = (PyObject_IsTrue(o) ? true : false);
    return 1;
}



PyObject* convert_char_to_python( char arg ) {
    return PyInt_FromLong( (long) arg );
}
PyObject* convert_short_to_python( short arg ) {
    return PyInt_FromLong( (long) arg );
}
PyObject* convert_unsigned_short_to_python( unsigned short arg ) {
    return PyInt_FromLong( (long) arg );
}
PyObject* convert_int_to_python( int arg ) {
    return PyInt_FromLong( (long) arg );
}
PyObject* convert_long_to_python( long arg ) {
    return PyLong_FromLong( arg );
}
PyObject* convert_unsigned_long_to_python( unsigned long arg ) {
    return PyLong_FromUnsignedLong( arg );
}
PyObject* convert_long_long_to_python( long arg ) {
    return PyLong_FromLongLong( arg );
}
PyObject* convert_float_to_python( float arg ) {
    return PyFloat_FromDouble( arg );
}
PyObject* convert_double_to_python( double arg ) {
    return PyFloat_FromDouble( arg );
}
PyObject* convert_string_to_python( const std::string &s ) {
    return PyString_FromString( s.c_str( ) );
}
PyObject* convert_boolean_to_python( bool b ) {
    return PyBool_FromLong( (long) (b ? 1 : 0) );
}

void default_int( int &i, const char * ) {
    i = 0;
}
void default_long( long &l, const char * ) {
    l = 0;
}
void default_long_long( long long &ll, const char * ) {
    ll = 0;
}
void default_float( float &f, const char * ) {
    f = 0.0;
}
void default_double( double &d, const char * ) {
    d = 0.0;
}
void default_string( std::string &str, const char * ) {
    str = "";
}

#define VEC_FROM_SINGLE(STR,TYPE,SRCTYPE,SRCTYPESTR)			\
int convert_ ## STR ## _from_python_single ( PyObject *obj, void *s ) {	\
    SRCTYPE value = 0;							\
    int result = convert_ ## SRCTYPESTR ## _from_python( obj, &value );	\
    if ( result ) {							\
	std::vector<TYPE> *vec = (std::vector<TYPE>*) s;		\
	(*vec).resize(1);						\
	(*vec)[0] = (TYPE) value;					\
    }									\
    return result;							\
}

//VEC_FROM_SINGLE(intvec,int,int,int)
//VEC_FROM_SINGLE(longvec,long,long,long)
//VEC_FROM_SINGLE(unsigned_longvec,unsigned long,unsigned long,unsigned_long)
//VEC_FROM_SINGLE(long_longvec,long long,long long,long_long)
//VEC_FROM_SINGLE(floatvec,float,float,float)
VEC_FROM_SINGLE(doublevec,double,double,double)
//VEC_FROM_SINGLE(booleanvec,bool,bool,boolean)
//VEC_FROM_SINGLE(stringvec,std::string,std::string,string)

// --------------------------------------------------------------------------------
//    DoubleVec polymorph
// --------------------------------------------------------------------------------
int is_double_compatible_single( const PyObject *element ) {
    if ( PyInt_Check(element) || PyLong_Check(element) || PyFloat_Check(element) ||
	 (PyComplex_Check(element) && PyComplex_ImagAsDouble((PyObject*)element) == (double) 0) )
	return 1;
    return 0;
}

int is_double_compatible_sequence( const PyObject *seq ) {

    if ( ! PyList_Check(seq) )
	return 0;

    for ( int i=0; i < PyList_Size((PyObject*)seq); ++i ) {
	PyObject *element = PyList_GetItem((PyObject*)seq,i);
	if ( ! (PyInt_Check(element) || PyLong_Check(element) || PyFloat_Check(element) ||
		(PyComplex_Check(element) && PyComplex_ImagAsDouble(element) == (double) 0)))
	    return 0;
    }
    return 1;
}

int convert_doublevec_from_double_compatible_single( PyObject *element, void *s ) {

    std::vector<double> *vec = (std::vector<double>*) s;
    (*vec).resize(1);

    if (PyInt_Check(element))
	(*vec)[0] = (double) PyInt_AsLong(element);
    else if (PyLong_Check(element)) 
	(*vec)[0] = (double) PyLong_AsDouble(element);
    else if (PyFloat_Check(element))
	(*vec)[0] = (double) PyFloat_AsDouble(element);
    else if (PyComplex_Check(element))
	(*vec)[0] = (double) PyComplex_RealAsDouble(element);
    else
	(*vec)[0] = (double) 0;

    return 1;
}

int convert_doublevec_from_double_compatible_sequence( PyObject *seq, void *s ) {

    if ( ! PyList_Check(seq) )
	return 0;

    std::vector<double> *vec = (std::vector<double>*) s;
    (*vec).resize(PyList_Size(seq));
    for ( int i=0; i < PyList_Size(seq); ++i ) {
	PyObject *element = PyList_GetItem(seq,i);
	if (PyInt_Check(element))
	    (*vec)[i] = (double) PyInt_AsLong(element);
	else if (PyLong_Check(element)) 
	    (*vec)[i] = (double) PyLong_AsDouble(element);
	else if (PyFloat_Check(element))
	    (*vec)[i] = (double) PyFloat_AsDouble(element);
	else if (PyComplex_Check(element))
	    (*vec)[i] = (double) PyComplex_RealAsDouble(element);
	else
	    (*vec)[i] = (double) 0;
    }

    return 1;
}

// --------------------------------------------------------------------------------
//    IntVec polymorph
// --------------------------------------------------------------------------------
int is_int_compatible_single( const PyObject *element ) {
    if ( PyInt_Check(element) || PyLong_Check(element) )
	return 1;
    return 0;
}

int is_int_compatible_sequence( const PyObject *seq ) {

    if ( ! PyList_Check(seq) )
	return 0;

    for ( int i=0; i < PyList_Size((PyObject*)seq); ++i ) {
	PyObject *element = PyList_GetItem((PyObject*)seq,i);
	if ( ! (PyInt_Check(element) || PyLong_Check(element)) )
	    return 0;
    }
    return 1;
}

int convert_intvec_from_int_compatible_single( PyObject *element, void *s ) {

    std::vector<int> *vec = (std::vector<int>*) s;
    (*vec).resize(1);

    if (PyInt_Check(element))
	(*vec)[0] = (int) PyInt_AsLong(element);
    else if (PyLong_Check(element)) 
	(*vec)[0] = (int) PyLong_AsLong(element);
    else
	(*vec)[0] = (int) 0;

    return 1;
}

int convert_intvec_from_int_compatible_sequence( PyObject *seq, void *s ) {

    if ( ! PyList_Check(seq) )
	return 0;

    std::vector<int> *vec = (std::vector<int>*) s;
    (*vec).resize(PyList_Size(seq));
    for ( int i=0; i < PyList_Size(seq); ++i ) {
	PyObject *element = PyList_GetItem(seq,i);
	if (PyInt_Check(element))
	    (*vec)[i] = (int) PyInt_AsLong(element);
	else if (PyLong_Check(element)) 
	    (*vec)[i] = (int) PyLong_AsLong(element);
	else
	    (*vec)[i] = (int) 0;
    }

    return 1;
}

// --------------------------------------------------------------------------------
//    StringVec polymorph
// --------------------------------------------------------------------------------
int is_string_compatible_single( const PyObject *element ) {
    if ( PyString_Check(element) )
	return 1;
    return 0;
}

int is_string_compatible_sequence( const PyObject *seq ) {

    if ( ! PyList_Check(seq) )
	return 0;

    for ( int i=0; i < PyList_Size((PyObject*)seq); ++i ) {
	PyObject *element = PyList_GetItem((PyObject*)seq,i);
	if ( ! (PyString_Check(element)) )
	    return 0;
    }
    return 1;
}

int convert_stringvec_from_string_compatible_single( PyObject *element, void *s ) {

    std::vector<std::string> *vec = (std::vector<int>*) s;
    (*vec).resize(1);

    if (PyString_Check(element))
	(*vec)[0] = PyString_AsString(element);
    else
	(*vec)[0] = "";

    return 1;
}

int convert_stringvec_from_string_compatible_sequence( PyObject *seq, void *s ) {

    if ( ! PyList_Check(seq) )
	return 0;

    std::vector<int> *vec = (std::vector<int>*) s;
    (*vec).resize(PyList_Size(seq));
    for ( int i=0; i < PyList_Size(seq); ++i ) {
	PyObject *element = PyList_GetItem(seq,i);
	if (PyString_Check(element))
	    (*vec)[i] = PyString_AsString(element);
	else
	    (*vec)[i] = (int) 0;
    }

    return 1;
}
