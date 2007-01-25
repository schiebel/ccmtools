APP_NAME = Test
CCMTOOLS_CMD = ccmtools

all: remote 

local: ${APP_NAME}.idl
	${CCMTOOLS_CMD} c++local -a -o xxx ${APP_NAME}.idl

remote: local
	${CCMTOOLS_CMD} idl2 -o xxx/CORBA_Stubs ${APP_NAME}.idl
	${CCMTOOLS_CMD} c++remote -o xxx ${APP_NAME}.idl
	${CCMTOOLS_CMD} c++remote-test -o xxx ${APP_NAME}.idl

check: 
	cp impl/Makefile.py xxx/
	cp impl/Test_impl.cc xxx/impl
	cp impl/Test_in_port_impl.cc xxx/impl
	cp test/_check_CCM_Remote_CCM_Session_Test_remote.cc xxx/test
	confix.py --packageroot=`pwd`/xxx --bootstrap --configure --make --targets=check

clean: 
	rm -rf xxx 
	rm -rf ${HOME}/tmp/xxx
