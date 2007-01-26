#ifndef __basictypes__PYTHON__H__
#define __basictypes__PYTHON__H__

#include <Python.h>
#include <float.h>
#include <string>
#include <vector>

void clear_python_conversion_error( );
int get_python_conversion_error( );
void set_python_conversion_error( );

inline int is_python_int( const PyObject *o ) { return PyInt_Check(o); }
inline int is_python_long( const PyObject *o ) { return PyInt_Check(o) || PyLong_Check(o); }
inline int is_python_long_long( const PyObject *o ) { return is_python_long(o); }
inline int is_python_unsigned_long( const PyObject *o ) { return is_python_long(o); }
inline int is_python_float( const PyObject *o ) { return PyFloat_Check(o); }
inline int is_python_double( const PyObject *o ) { return PyFloat_Check(o); }
inline int is_python_string( const PyObject *o ) { return PyString_Check(o); }
inline int is_python_boolean( const PyObject *o ) { return PyBool_Check(o); }

inline int is_python_char( const PyObject *o ) { return is_python_int(o); }
inline int is_python_short( const PyObject *o ) { return is_python_int(o); }
inline int is_python_unsigned_short( const PyObject *o ) { return is_python_int(o); }

inline int is_intvec_singleton( const std::vector<int> &v ) { return v.size() == 1 ? 1 : 0; }
inline int is_longvec_singleton( const std::vector<long> &v ) { return v.size() == 1 ? 1 : 0; }
inline int is_longlongvec_singleton( const std::vector<long long> &v ) { return v.size() == 1 ? 1 : 0; }
inline int is_floatvec_singleton( const std::vector<float> &v ) { return v.size() == 1 ? 1 : 0; }
inline int is_doublevec_singleton( const std::vector<double> &v ) { return v.size() == 1 ? 1 : 0; }
inline int is_stringvec_singleton( const std::vector<std::string> &v ) { return v.size() == 1 ? 1 : 0; }
inline int is_booleanvec_singleton( const std::vector<bool> &v ) { return v.size() == 1 ? 1 : 0; }

inline int within_int_range( long value ) { return value <= INT_MAX && value >= INT_MIN; }
inline int within_long_range(  long value ) { return value <= LONG_MAX && value >= LONG_MIN; }
inline int within_unsigned_long_range( unsigned long value ) { return value <= (LONG_MAX * 2UL + 1UL) && value >= 0; }
inline int within_long_long_range(  long value ) { return within_long_range(value); }
inline int within_float_range( float value ) { return value <= FLT_MAX && value >= FLT_MIN; }
inline int within_double_range( double value ) { return value <= DBL_MAX && value >= DBL_MIN; }
inline int within_short_range( long value ) { return value <= SHRT_MAX && value >= SHRT_MIN; }
inline int within_unsigned_short_range( long value ) { return value <= (SHRT_MAX * 2 + 1) && value >= 0; }
inline int within_char_range( long value ) { return value <= CHAR_MAX && value >= CHAR_MIN; }

int convert_int_from_python( PyObject*, void * );
int convert_long_from_python( PyObject*, void * );
int convert_unsigned_long_from_python( PyObject*, void * );
int convert_long_long_from_python( PyObject*, void * );
int convert_float_from_python( PyObject*, void * );
int convert_double_from_python( PyObject*, void * );
int convert_string_from_python( PyObject*, void * );
int convert_boolean_from_python( PyObject*, void * );

int convert_short_from_python( PyObject*, void * );
int convert_unsigned_short_from_python( PyObject*, void * );
int convert_char_from_python( PyObject*, void * );

PyObject* convert_int_to_python( int );
PyObject* convert_long_to_python( long );
PyObject* convert_unsigned_long_to_python( unsigned long );
PyObject* convert_long_long_to_python( long );
PyObject* convert_float_to_python( float );
PyObject* convert_double_to_python( double );
PyObject* convert_string_to_python( const std::string & );
PyObject* convert_boolean_to_python( bool );

PyObject* convert_short_to_python( short );
PyObject* convert_unsigned_short_to_python( unsigned short );
PyObject* convert_char_to_python( char );

#if 0
inline PyObject* convert_intvec_to_python_single( const std::vector<int> &v )
		{ return v.size() >= 1 ? convert_int_to_python(v[0]) : convert_int_to_python(0); }
inline PyObject* convert_longvec_to_python_single( const std::vector<long> &v )
		{ return v.size() >= 1 ? convert_long_to_python(v[0]) : convert_long_to_python(0); }
inline PyObject* convert_unsigned_longvec_to_python_single( const std::vector<unsigned long> &v )
		{ return v.size() >= 1 ? convert_unsigned_long_to_python(v[0]) : convert_unsigned_long_to_python(0); }
inline PyObject* convert_long_longvec_to_python_single( const std::vector<long long> &v )
		{ return v.size() >= 1 ? convert_long_long_to_python(v[0]) : convert_long_long_to_python(0); }
inline PyObject* convert_floatvec_to_python_single( const std::vector<float> &v )
		{ return v.size() >= 1 ? convert_float_to_python(v[0]) : convert_float_to_python(0); }
inline PyObject* convert_stringvec_to_python_single( const std::vector<std::string> &v )
		{ return v.size() >= 1 ? convert_string_to_python(v[0]) : convert_string_to_python(""); }
inline PyObject* convert_booleanvec_to_python_single( const std::vector<bool> &v )
		{ return v.size() >= 1 ? convert_boolean_to_python(v[0]) : convert_boolean_to_python(false); }
inline PyObject* convert_longvec_to_python_single( const std::vector<long> &v )
		{ return v.size() >= 1 ? convert_double_to_python((double)v[0]) : convert_double_to_python(0); }
#endif
inline PyObject* convert_doublevec_to_python_single( const std::vector<double> &v )
		{ return v.size() >= 1 ? convert_double_to_python(v[0]) : convert_double_to_python(0); }

inline PyObject* convert_intvec_to_python_single( const std::vector<int> &v )
		{ return v.size() >= 1 ? convert_int_to_python(v[0]) : convert_int_to_python(0); }

#if 0
int convert_intvec_from_python_single( PyObject *obj, void *s );
int convert_longvec_from_python_single( PyObject *obj, void *s );
int convert_unsigned_longvec_from_python_single( PyObject *obj, void *s );
int convert_long_longvec_from_python_single( PyObject *obj, void *s );
int convert_floatvec_from_python_single( PyObject *obj, void *s );
int convert_doublevec_from_python_single( PyObject *obj, void *s );
int convert_stringvec_from_python_single( PyObject *obj, void *s );
int convert_booleanvec_from_python_single( PyObject *obj, void *s );
int convert_doublevec_from_python_single( PyObject *obj, void *s );
#endif

void default_int( int &, const char *id = 0 );
void default_long( long &, const char *id = 0 );
void default_long_long( long long &, const char *id=0 );
void default_float( float &, const char *id = 0 );
void default_double( double &, const char *id = 0 );
void default_string( std::string &, const char *id=0 );

int is_double_compatible_single( const PyObject *o );
int is_double_compatible_sequence( const PyObject *o );
int convert_doublevec_from_double_compatible_single( PyObject *obj, void *s );
int convert_doublevec_from_double_compatible_sequence( PyObject *obj, void *s );

int is_int_compatible_single( const PyObject *o );
int is_int_compatible_sequence( const PyObject *o );
int convert_intvec_from_int_compatible_single( PyObject *obj, void *s );
int convert_intvec_from_int_compatible_sequence( PyObject *obj, void *s );

int is_string_compatible_single( const PyObject *o );
int is_string_compatible_sequence( const PyObject *o );
int convert_stringvec_from_string_compatible_single( PyObject *obj, void *s );
int convert_stringvec_from_string_compatible_sequence( PyObject *obj, void *s );

#endif
