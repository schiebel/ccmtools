
all: remote 

remote: 
	idl ../ccmtools/idl3/Benchmark.idl
	mv Benchmark.cc corba_object
	mv Benchmark.h corba_object

build: 
	confix.py --profile=benchmark --packageroot=`pwd` --bootstrap --configure --make 

check: 
	confix.py --profile=benchmark --packageroot=`pwd` --bootstrap --configure --make --targets=check

clean:
	confix.py --profile=benchmark --packageroot=`pwd` --make --targets="uninstall clean"
	rm -rf ${HOME}/tmp/benchmark_build/corba_object