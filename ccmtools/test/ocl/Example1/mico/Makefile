MICO_HOME = /usr/local/mico
APP_NAME = Math

CCM_COMPILER = $(CXX) -O2
CCM_LINKER = $(CXX)
CCM_GENERATOR = $(MICO_HOME)/bin/mico-ccm --standalone
IDL_COMPILER  = $(MICO_HOME)/bin/idl

MICO_INCLUDE = -I $(MICO_HOME)/include
CCM_LIBS = -L $(MICO_HOME)/lib  -lmico2.3.11 -lmicocoss2.3.11 -lmicoccm2.3.11 -ldl


all: server client

client: client.o $(APP_NAME).o
	$(CCM_LINKER) -o client client.o $(APP_NAME).o $(CCM_LIBS)

server: $(APP_NAME)_impl.o $(APP_NAME)_ccm.o $(APP_NAME).o
	$(CCM_LINKER) -o server $(APP_NAME)_impl.o $(APP_NAME)_ccm.o $(APP_NAME).o $(CCM_LIBS)

idl:  $(APP_NAME).idl	
	$(IDL_COMPILER) $(APP_NAME).idl
	$(IDL_COMPILER) --codegen-idl --idl3toidl2 --name $(APP_NAME)Eq $(APP_NAME).idl
	$(CCM_GENERATOR) $(APP_NAME).idl

.PHONY : clean
clean:
	-rm client server
	-rm *.o
	-rm $(APP_NAME)_ccm.cc $(APP_NAME)_ccm.h
	-rm $(APP_NAME).cc $(APP_NAME).h
	-rm $(APP_NAME)Eq.idl

client.o: client.cc $(APP_NAME).h
	$(CCM_COMPILER) $(MICO_INCLUDE) -c client.cc

$(APP_NAME).o: $(APP_NAME).cc
	$(CCM_COMPILER) $(MICO_INCLUDE) -c $(APP_NAME).cc

$(APP_NAME)_impl.o: $(APP_NAME)_impl.cc $(APP_NAME)_impl.h $(APP_NAME)_ccm.h $(APP_NAME).h
	$(CCM_COMPILER) $(MICO_INCLUDE) -c $(APP_NAME)_impl.cc

$(APP_NAME)_ccm.o: $(APP_NAME)_ccm.cc
	$(CCM_COMPILER) $(MICO_INCLUDE) -c $(APP_NAME)_ccm.cc

$(APP_NAME)_ccm.cc $(APP_NAME)_ccm.h: $(APP_NAME).idl
	$(CCM_GENERATOR) $(APP_NAME).idl

$(APP_NAME).cc $(APP_NAME).h: $(APP_NAME).idl
	$(IDL_COMPILER) $(APP_NAME).idl
	$(IDL_COMPILER) --codegen-idl --idl3toidl2 --name $(APP_NAME)Eq $(APP_NAME).idl




