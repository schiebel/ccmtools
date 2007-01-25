
COMPONENT_DIR = component
IDL3_DIR = idl3
IDL_INCLUDE = -I${IDL3_DIR}

all: remote nested dbc 
server: cppserver
client: cppclient

interface:
	ccmtools-generate c++local ${IDL_INCLUDE} -o ${COMPONENT_DIR} ${IDL3_DIR}/Benchmark.idl

local: interface
	ccmtools-generate c++local ${IDL_INCLUDE} -a -o ${COMPONENT_DIR} ${IDL3_DIR}/Test.idl

remote: local
	ccmtools-generate idl2 ${IDL_INCLUDE} -o ${COMPONENT_DIR}/CORBA_Stubs ${IDL3_DIR}/Benchmark.idl
	ccmtools-generate idl2 ${IDL_INCLUDE} -o ${COMPONENT_DIR}/CORBA_Stubs ${IDL3_DIR}/Test.idl
	ccmtools-generate c++remote ${IDL_INCLUDE} -o ${COMPONENT_DIR} ${IDL3_DIR}/Benchmark.idl
	ccmtools-generate c++remote ${IDL_INCLUDE} -o ${COMPONENT_DIR} ${IDL3_DIR}/Test.idl

nested: 
	ccmtools-generate c++local ${IDL_INCLUDE} -a -o ${COMPONENT_DIR} ${IDL3_DIR}/SuperTest.idl

dbc: 
	ccmtools-generate c++local ${IDL_INCLUDE} -o ${COMPONENT_DIR} ${IDL3_DIR}/DbcBenchmark.idl
	ccmtools-generate c++local ${IDL_INCLUDE} -a -o ${COMPONENT_DIR} ${IDL3_DIR}/DbcTest.idl
	ccmtools-generate c++dbc ${IDL_INCLUDE} -o ${COMPONENT_DIR} ${IDL3_DIR}/DbcTest.idl

install: 
	cp impl/Makefile.py ${COMPONENT_DIR} 
	cp impl/Test_bm_impl.cc ${COMPONENT_DIR}/impl
	cp impl/SuperTest_bm_impl.h ${COMPONENT_DIR}/impl
	cp impl/SuperTest_bm_impl.cc ${COMPONENT_DIR}/impl
	cp impl/DbcTest_bmInv1_impl.cc ${COMPONENT_DIR}/impl 
	cp impl/DbcTest_bmInv2_impl.cc ${COMPONENT_DIR}/impl 
	cp impl/DbcTest_bmInv3_impl.cc ${COMPONENT_DIR}/impl 
	cp impl/DbcTest_bmPost_impl.cc ${COMPONENT_DIR}/impl
	confix.py --profile=benchmark --packageroot=`pwd`/${COMPONENT_DIR} --bootstrap --configure --make --targets=install

cppserver: 
	confix.py --profile=benchmark --packageroot=`pwd`/server --bootstrap --configure --make

cppclient:
	confix.py --profile=benchmark --packageroot=`pwd`/client --bootstrap --configure --make

clean:
	confix.py --profile=benchmark --packageroot=`pwd`/${COMPONENT_DIR} --make --targets="uninstall clean"
	rm -rf ${COMPONENT_DIR}
	rm -rf ${HOME}/tmp/benchmark_build/${COMPONENT_DIR}
