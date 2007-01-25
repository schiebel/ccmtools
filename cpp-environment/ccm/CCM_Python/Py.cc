#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <map>
#include <string>
#include <Python.h>
#include <CCM_Python/HomeFinder.h>
#include <CCM_Python/Py.h>

using std::map;
using std::string;

namespace CCM_Python {
// ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----
//    may set "-DPYTHONPATH=\"...\""         to indicate where the python executable is found
//            "-DPYTHONLIB=\"...:...:...\""  to indicate one or more library paths to search
//            "-DPYTHONVER=\"2.4\"
// ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----

int Py::argc = 0;
char **Py::argv = 0;
unsigned int Py::ref_count = 0;
bool Py::using_ipython = false;
PyThreadState *Py::main_thread = NULL;
std::string Py::init_file;
std::string Py::load_path;
std::string Py::ipython_ns;
std::list<std::string> *Py::path = 0;

#if defined(PYTHONVER)
#define PYTHON_VERSION  PYTHONVER
#else
#define PYTHON_VERSION	"2.4"
#endif

// ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----
//        %V  --  ${major}.${minor}
//        %v  --  ${major}${minor}
// ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----
#if defined(__APPLE_CC__)

/****** directories ordered in reverse order of priority ******/
static char *dirs_init[] = {
    "/opt/local/lib/python%V",
    "/usr/local/lib/python%V",
    "/opt/local/Library/Frameworks/Python.framework/Versions/%V/lib/python%V",
    "%H/.ipython",
    0,
};

static const char *subdirs[] = {
    "",
    "lib-dynload",
    "plat-darwin",
    "plat-mac",
    "plat-mac/lib-scriptpackages",
    "site-packages",
    0
};

static char *files[] = {
#if defined(PYTHONLIB)
    PYTHONLIB,
#endif
    "/opt/local/Library/Frameworks/Python.framework/Versions/%V/lib/python%v.zip",
    0
};

#elif defined(__linux__)

/****** directories ordered in reverse order of priority ******/
static char *dirs_init[] = {
    "/opt/local/lib/python%V",
    "/usr/local/lib/python%V",
    "/usr/lib/python%V",
    "/usr/bin",
    "%H/.ipython",
    0
};

static const char *subdirs[] = {
    "",
    "plat-linux2",
    "plat-linux",
    "lib-tk",
    "lib-dynload",
    "site-packages",
    "site-packages/Numeric",
    0
};

static const char *files[] = {
#if defined(PYTHONLIB)
    PYTHONLIB,
#endif
    "/usr/lib/python24.zip",
    "/usr/local/lib/python24.zip",
    "/opt/local/lib/python24.zip",
    0
};

#endif

static char **dirs = 0;
static const char file_separator = '/';

static char percent_map[] = { 'V', 'v', 'H', '\0' };
static char *percent_values[] = { "x.y", "xy", "/xxx", 0 };

static char *python_path[] = {
#if defined(PYTHONPATH)
    PYTHONPATH,
#endif
    "/opt/local/bin/python" PYTHON_VERSION,
    "/usr/local/bin/python" PYTHON_VERSION,
    "/opt/bin/python" PYTHON_VERSION,
    "/usr/bin/python" PYTHON_VERSION,
    "/opt/local/bin/python",
    "/usr/local/bin/python",
    "/opt/bin/python",
    "/usr/bin/python",
    0
};

void Py::init_substitute( ) {

    int major, minor;
    int dir_count = 0;

    char *user_path_count = strdup(load_path.c_str());
    char *user_path = strdup(user_path_count);

    for ( int i=0; dirs_init[i]; ++i ) ++dir_count;
    for ( char *ptr = strtok(user_path_count,":"); ptr; ptr = strtok(0,":")) ++dir_count;
    free(user_path_count);

    dirs = (char**) malloc((dir_count + 3) * (sizeof(char*)));

    int offset = 0;
    for ( char *ptr = strtok(user_path,":"); ptr; ptr = strtok(0,":"))
	dirs[offset++] = strdup(ptr);
    free( user_path );

    char *prefix = strdup(Py_GetPrefix());
    dirs[offset++] = prefix;
    int prefix_len = strlen(prefix);
    dirs[offset] = (char*) malloc(prefix_len + 15);
    memcpy( dirs[offset], prefix, prefix_len );
    dirs[offset][prefix_len++] = file_separator;
    dirs[offset][prefix_len++] = 'l';
    dirs[offset][prefix_len++] = 'i';
    dirs[offset][prefix_len++] = 'b';
    dirs[offset][prefix_len++] = file_separator;
    memcpy( &dirs[offset++][prefix_len], "python%V", 9 );

    for ( int i=0; dirs_init[i]; ++i )
	dirs[offset++] = strdup(dirs_init[i]);

    dirs[offset] = 0;

    sscanf( Py_GetVersion(), "%d.%d", &major, &minor );
    percent_values[0] = (char*) malloc(30);
    sprintf( percent_values[0], "%d.%d", major, minor );
    percent_values[1] = (char*) malloc(30);
    sprintf( percent_values[1], "%d%d", major, minor );
    const char *home = getenv("HOME");
    if ( home ) percent_values[2] = strdup(home);

}

void Py::init( ) {
    char **ptr;
    struct stat sb;
    for ( ptr = python_path; *ptr; ++ptr ) 
	if ( ! stat(*ptr, &sb) )
	    break;

    if ( *ptr ) Py_SetProgramName( *ptr );
    Py_Initialize( );
    init_substitute( );
}

/****** helper for Py::init_path( ) ******/
static void substitute( char *to, const char *from ) {

    while ( *from ) {
	if ( *from == '\\' ) {
	    if ( from[1] ) {
	        ++from;
		*to++ = *from;
	    }
	} else if ( *from == '%' ) {
	    int i = 0;
	    int found = 0;
	    for ( ; percent_map[i]; ++i ) {
		if ( from[1] == percent_map[i] ) {
		    const char *add = percent_values[i];
		    while ( *add ) *to++ = *add++;
		    ++from;
		    found = 1;
		    break;
		}
	    }
	    if ( ! found )
		*to++ = *from;
	} else {
	    *to++ = *from;
	}
	if ( *from ) ++from;
    }
    *to = '\0';
}


void Py::init_path( ) {

    if ( ! path ) {

	path = new std::list<std::string>( );

	map<std::string, int> mpath;
	int mpath_count = 0;

	char *buf = 0;
	char *cat = 0;
	int size = 0;

	struct stat sb;

	for ( char **dptr = dirs; *dptr; ++dptr, ++mpath_count ) {
	    for ( const char **sptr = subdirs; *sptr; ++sptr ) {

		const char *base = *dptr;
		const char *sub = *sptr;

		int base_len = base ? strlen(base) : 0;
		int sub_len = sub ? strlen(sub) : 0;

		if ( base_len + sub_len + 23 > size ) {
		    while ( base_len + sub_len + 23 > size ) size += 128;
		    buf = (char *) (buf ? realloc(buf, size) : malloc(size));
		    cat = (char *) (cat ? realloc(cat, size) : malloc(size));
		}

		if ( ! base && ! sub ) 
		    continue;					/* when does this condition occur? */
		if ( ! base || *sub == file_separator ) {

		    substitute( buf, sub );

		} else {

		    strcpy( cat, base );
		    if ( cat[base_len-1] != file_separator ) {
			cat[base_len] = file_separator;
			cat[base_len+1] = '\0';
		    }
		    strcat( cat, sub );
		    substitute( buf, cat );
		}

		if ( ! stat(buf, &sb) )
		    mpath.insert(std::map<std::string,int>::value_type(buf,mpath_count));
	    }
	}

	if ( ! mpath.empty() ) {

	    for ( int i=0; i < mpath_count; ++i ) {
		for ( std::map<std::string,int>::iterator iter = mpath.begin(); iter != mpath.end(); ++iter ) {
		    if ( (*iter).second == i ) {
			const char *add = (*iter).first.c_str();
			(*path).push_back(std::string(add));
		    }
		}
	    }

	    for ( const char **ptr = (const char**) files; *ptr; ++ptr ) {
		char *str = strdup(*ptr);
		for (char *p = strtok(str,":"); p; p = strtok(0,":")) {
		    if ( ! stat(p, &sb) ) {
			(*path).push_back(std::string(p));
		    }
		}
		free(str);
	    }

	}
    }
}

char *Py::findpath( ) {
    const char PATH_SEP = ':';

    if ( ! path ) init_path( );

    std::string resultStr("");

    for ( std::list<std::string>::iterator iter = (*path).begin(); iter != (*path).end(); ++iter ) {
	// append the path element
	resultStr.append(*iter);

	// delete any trailing file_sep characters
	resultStr.erase ( resultStr.find_last_not_of ( file_separator ) + 1 );
	resultStr += PATH_SEP;
    }

    // delete any trailing PATH_SEP
    resultStr.erase ( resultStr.find_last_not_of ( PATH_SEP ) + 1 );

    char *result = strdup( resultStr.c_str() );
    return result ? result : strdup("");
}

char *Py::findfile( const char *file ) {

    char *buffer = (char*) malloc(256);
    unsigned int buffer_len = 256;
    unsigned int file_len = strlen(file);
    struct stat sb;

    if ( ! path ) init_path();

    for ( std::list<std::string>::iterator iter = (*path).begin(); iter != (*path).end(); ++iter ) {
	const char *p = (*iter).c_str();
	unsigned int dir_len = strlen(p);
	const unsigned int len = dir_len + file_len + 2;
	if ( len > buffer_len ) {
	    while ( len > buffer_len ) buffer_len *= 2;
	    buffer = (char*) realloc(buffer, buffer_len);
	}
	memcpy( buffer, p, dir_len );
	buffer[dir_len] = file_separator;
	memcpy( &buffer[dir_len+1], file, file_len );
	buffer[dir_len+file_len+1] = '\0';
	if ( ! stat(buffer, &sb) )
	    return buffer;
    }

    free( buffer );
    return 0;
}

void Py::init( int c, char **v, std::string load_path_, std::string init_file_,
	       bool setup_ipython, std::string ipython_namespace ) {
    argc = c;
    argv = v;
    using_ipython = setup_ipython;
    init_file = init_file_;
    load_path = load_path_;
    ipython_ns = ipython_namespace;
}


Py::Py( ) : lock_count(0), did_lock(false), suspended_thread(0) {

    if ( ! argv )
	throw error( "unitialized: call Py::init( argc, argv ) from main()" );

    if ( ref_count == 0 ) {

	init( );

	char *pypath = findpath( );
	PySys_SetPath( pypath );
	PySys_SetArgv(argc, argv);
	free( pypath );

	if ( using_ipython ) {
	    PyObject *global_dict = PyModule_GetDict(PyImport_AddModule("__main__"));
	    PyObject *ns = PyDict_New();
	    PyDict_SetItemString(ns, "__name__", PyString_FromString("__main__"));
	    PyDict_SetItemString(global_dict, ipython_ns.c_str(), ns);
	    CCM_Python::set_global_dict(ns);
	    evalString("import IPython");
	}

	if ( ! init_file.empty()) {
	    evalFile(init_file.c_str());
	}
    }

    ref_count += 1;

}

Py::~Py( ) {
    if ( --ref_count == 0 ) {
	Py_Finalize();
    }
}

int Py::evalString( const char *str ) {
    lock();
    int result = PyRun_SimpleString(str);
    unlock();
    return result;
}

int Py::evalFile( const char *name ) {
    char *filename = findfile( name );
    if ( filename ) {
	FILE *fp = fopen(filename, "r");
	lock( );
	int result = PyRun_SimpleFile( fp, filename );
	unlock( );
    }
}

void Py::lock( ) {
    if ( main_thread && lock_count == 0 ) {
	PyEval_AcquireLock( );
	suspended_thread = PyThreadState_Swap(thread());
	/*fprintf(stderr, "\t\t\tlock:\t0x%x => 0x%x\n", suspended_thread, thread());*/
	did_lock = true;
    }
    ++lock_count;
}

void Py::unlock( ) {
    if ( lock_count ) {
	--lock_count;
	if ( did_lock && lock_count == 0 ) {
	    /*fprintf(stderr, "\t\t\tunlock:\t0x%x => 0x%x\n", thread(), suspended_thread);*/
	    PyThreadState_Swap(suspended_thread);
	    PyEval_ReleaseLock();
	    suspended_thread = 0;
	    did_lock = false;
	}
    }
}
void Py::set_main_thread( PyThreadState *mt ) {
    main_thread = mt;
}
  

}
