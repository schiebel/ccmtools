
APP_NAME = Test
APP_DIR = xxx
IDL3_DIR = ${APP_DIR}/idl3
IDL_INTERFACE = ${IDL3_DIR}/interface
IDL_COMPONENT = ${IDL3_DIR}/component
IDL_INCLUDE = -I${IDL_INTERFACE} -I${IDL_COMPONENT}

all: local

idl: Test.idl
	ccmtools-generate idl3 -o ${IDL3_DIR} ${APP_NAME}.idl
	ccmtools-generate idl3mirror -o ${IDL3_DIR} ${APP_NAME}.idl

local: idl
	ccmtools-generate c++local -o ${APP_DIR} ${IDL_INCLUDE} ${IDL_INTERFACE}/*.idl
	ccmtools-generate c++local -a -o ${APP_DIR} ${IDL_INCLUDE} ${IDL_COMPONENT}/*.idl
	ccmtools-generate c++local-test -o ${APP_DIR} ${IDL_INCLUDE} ${IDL_COMPONENT}/${APP_NAME}.idl

check: 
	cp impl/Makefile.py xxx
	cp impl/Test_console_impl.cc xxx/impl
	cp impl/Test_mirror_impl.cc xxx/impl
	confix.py --packageroot=`pwd`/xxx --bootstrap --configure --make --targets=check

clean: 
	rm -rf xxx 
	rm -rf ${HOME}/tmp/xxx
