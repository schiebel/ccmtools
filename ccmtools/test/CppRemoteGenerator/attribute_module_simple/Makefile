
APP_NAME = Test
CCMTOOLS_CMD = ccmtools 

all: remote

local: ${APP_NAME}.idl
	ccmtools-generate c++local -a -o xxx ${APP_NAME}.idl

remote: local
	${CCMTOOLS_CMD} idl2 -o xxx/CORBA_Stubs ${APP_NAME}.idl
	${CCMTOOLS_CMD} c++remote -o xxx ${APP_NAME}.idl
	${CCMTOOLS_CMD} c++remote-test -o xxx ${APP_NAME}.idl

check: 
	cp impl/Makefile.py xxx/
	cp test/_check_CCM_Remote_world_europe_austria_CCM_Session_Test_remote.cc xxx/test
	confix.py --packageroot=`pwd`/xxx --bootstrap --configure --make --targets=check

clean: 
	rm -rf xxx 
	rm -rf ${HOME}/tmp/xxx
