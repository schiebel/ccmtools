
C++ :=
IDL :=
DESTROOT := ./build
PYTHONINC := /opt/local/include/python2.5

-include makedefs

os := $(shell uname | tr 'A-Z' 'a-z')
arch := $(shell uname -p)

VERSION := 0.5.8

ifeq "$(os)" "darwin"
SO := dylib
SOV := $(VERSION).dylib
endif
ifeq "$(os)" "linux"
SO := so
SOV := so.$(VERSION)
endif

##
## setup INSTDIR
##
ifneq "$(INSTDIR)" ""
instlib_path := $(INSTDIR)/lib/
else
instlib_path :=
endif


ifeq "$(IDL)" ""
IDL    := idl
endif

IDLSRC := $(shell find . -type f -name '*.idl' | grep -v /test/)
IDLHDR := $(IDLSRC:%.idl=%.h)

CPPHDR := $(shell find ./cpp-environment -type f -name '*.h' | egrep -v '/CCM_OCL/|/CCM_Remote/')
CPPSRC1 := $(shell find ./cpp-environment -type f -name '*.cc' | egrep -v '/CCM_OCL/|/CCM_Remote/')
CPPSRC2 := $(shell find ./cpp-environment -type f -name '*.cpp' | egrep -v '/CCM_OCL/|/CCM_Remote/')
CPPOBJ := $(CPPSRC1:%.cc=%.o) $(CPPSRC2:%.cpp=%.o)

ifeq "$(OPT)" ""
OPT := -O2
endif

ifeq "$(C++)" ""
C++ := g++
endif

_DESTROOT := $(abspath $(DESTROOT))
_LIBDIR := $(_DESTROOT)/lib
_INCDIR := $(_DESTROOT)/include
_JAVADIR := $(_DESTROOT)/share/java/ccmtools
_BINDIR := $(_DESTROOT)/bin

LOCALCPPLIB_PATH := $(_LIBDIR)/libccmtools_local.$(SOV)
LOCALCPPLNK_PATH := $(_LIBDIR)/libccmtools_local.$(SO)

define install-headers
  @echo $1 | perl -e 'use File::Basename; use File::Path; use File::Copy; $$from=<>; $$_=$$from; s|\./cpp-environment/wx/(?:[^/ ]+)/(?:[^/ ]+/)?|$(_INCDIR)/WX/Utils/|g; s|\./cpp-environment/ccm/|$(_INCDIR)/|g; s|/HomeFinder/HomeFinder.h|/HomeFinder.h|g; s|/CCM_Local/LocalComponents/|/LocalComponents/|g; @from=split(/\s+/,$$from); @to=split(/\s+/,$$_); while ( (scalar @from) > 0 && (scalar @to) > 0 ) { $$from = shift(@from); $$to = shift(@to); if ( ! -d dirname($$to) ) { mkpath(dirname($$to)); } copy($$from,$$to) or die "Copy failed: $$!"; }'
endef

%.h: %.idl
	cd $(dir $<) && $(IDL) $(notdir $<)

%.o: %.cc
	$(C++) -c $(OPT) -I$(_INCDIR) -I$(PYTHONINC) $< -o $@

%.o: %.cpp
	$(C++) -c $(OPT) -I$(_INCDIR) -I$(PYTHONINC) $< -o $@

all: setup install_headers $(LOCALCPPLNK_PATH) install_jars install_scripts

idl: $(IDLHDR) 

setup:
	mkdir -p $(_DESTROOT)
	mkdir -p $(_INCDIR)
	mkdir -p $(_LIBDIR)
	mkdir -p $(_JAVADIR)
	mkdir -p $(_BINDIR)

install_headers:
	$(call install-headers,$(CPPHDR))

$(LOCALCPPLIB_PATH): $(CPPOBJ)
ifeq "$(os)" "darwin"
	$(C++) -dynamiclib -install_name $(instlib_path)$(notdir $@) -o $@ $(filter %.o,$^) -L/opt/local/lib -lpython2.5
endif
ifeq "$(os)" "linux"
	$(C++) -shared -Wl,-soname,$(notdir $@) -o $@ $(filter %.o,$^)
endif

$(LOCALCPPLNK_PATH): $(LOCALCPPLIB_PATH)
	rm -f $@
	cd $(dir $<) && ln -fs $(notdir $<) $(notdir $@)

#                                                          && \
#	ln -fs $(notdir $<) libCCM_Python.$(SO) && \
#	ln -fs $(notdir $<) libWX_Utils_code.$(SO) && \
#	ln -fs $(notdir $<) libWX_Utils_error.$(SO) && \
#	ln -fs $(notdir $<) libWX_Utils_types.$(SO) && \
#	ln -fs $(notdir $<) libCCM_Local_HomeFinder.$(SO) && \
#	ln -fs $(notdir $<) libWX_Utils_types.$(SO)

install_jars:
	cp -p ccmtools/lib/antlr.jar $(_JAVADIR)
	cp -p ccmtools/lib/mdr01.jar $(_JAVADIR)
	cp -p ccmtools/lib/oclmetamodel.jar $(_JAVADIR)
	cp -p ccmtools/lib/ccmtools.jar $(_JAVADIR)
	cp -p java-environment/lib/Components.jar $(_JAVADIR)

install_scripts:
	cp -p ccmtools/bin/ccmtools $(_BINDIR)
	sed -i -e 's|^CCMTOOLS_LIB="/usr.*|CCMTOOLS_LIB="$(_JAVADIR)"|' $(_BINDIR)/ccmtools
	sed -i -e 's|^CCMTOOLS_HOME="/usr.*|CCMTOOLS_HOME="$(_DESTROOT)"|' $(_BINDIR)/ccmtools
	chmod 755 $(_BINDIR)/ccmtools

