
APP_NAME = Test
CCMTOOLS_CMD = ccmtools

all:local remote build

local: Test.idl
	${CCMTOOLS_CMD} c++local -a -o xxx Test.idl

remote: local
	${CCMTOOLS_CMD} idl2 -o xxx/CORBA_Stubs Test.idl
	${CCMTOOLS_CMD} c++remote -o xxx Test.idl
	${CCMTOOLS_CMD} c++remote-test -o xxx Test.idl

build: remote
	confix.py --packageroot=`pwd`/xxx --bootstrap --configure --make --targets=check
