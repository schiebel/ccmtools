
APP_NAME = Example1

all: build

local: ${APP_NAME}.idl
	ccmtools-generate c++local -a -o xxx ${APP_NAME}.idl
	ccmtools-generate c++local-test -o xxx ${APP_NAME}.idl

dbc: local
	ccmtools-generate c++dbc -o xxx ${APP_NAME}.idl

build: local dbc
	cp impl/Math_stack_impl.cc xxx/impl/
	cp test/_check_CCM_Local_CCM_Session_Math.cc  xxx/test
	confix.py --packageroot=`pwd`/xxx --bootstrap --configure --make --targets=check

clean: 
	rm -rf xxx 
	rm -rf ${HOME}/tmp/xxx
	rm constraints.txt
	rm mdr.btd
	rm mdr.btx