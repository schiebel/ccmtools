
C++ :=
IDL :=

-include makedefs

os := $(shell uname | tr 'A-Z' 'a-z')
arch := $(shell uname -p)

VERSION := 0.5.5

ifeq "$(os)" "darwin"
SO := dylib
SOV := $(VERSION).dylib
endif
ifeq "$(os)" "linux"
SO := so
SOV := so.$(VERSION)
endif

PYTHONVER := $(shell python -V 2>&1 | perl -pe "s|^.*?(\d+\.\d+)\.\d+\$$|\$$1|")
PYTHONLIBD := $(shell python -c "import sys; print sys.prefix" | perl -e "\$$_=<>; s|\n||g; if ( -e \"\$$_/lib/python$(PYTHONVER)/config/libpython$(PYTHONVER).$(SO)\" ) { print \"\$$_/lib/python$(PYTHONVER)/config\" } elsif ( -e \"\$$_/lib64/libpython.$(SO)\" ) { print \"\$$_/lib64/libpython.$(SO)\" } elsif ( -e \"\$$_/lib/libpython.$(SO)\" ) { print \"\$$_/lib/libpython.$(SO)\" }" )
PYTHONINCD := $(shell python -c "import sys; print sys.prefix" | perl -e "\$$_=<>; s|\n||g; if ( -e \"\$$_/Headers/Python.h\" ) { print \"\$$_/Headers\" } elsif ( -e \"\$$_/include/python$(PYTHONVER)/Python.h\" ) { print \"\$$_/include/python$(PYTHONVER)\" }" )

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

LIB := lib
ifeq "$(DESTROOT)" ""
DESTROOT := $(shell echo $$CASAPATH | perl -pe "s|(\S+).*|\$$1|")
ifneq "$(DESTROOT)" ""
_ARCH := $(shell echo $$CASAPATH | perl -pe "s|\S+\s+(\S+).*|\$$1|")
ifneq "$(_ARCH)" ""
_DESTROOT := $(DESTROOT)
_LIBDIR := $(_DESTROOT)/$(_ARCH)/lib
_INCDIR := $(_DESTROOT)/include
_JAVADIR := $(_DESTROOT)/share/java/ccmtools
_BINDIR := $(_DESTROOT)/$(_ARCH)/bin
instlib_path := $(DESTROOT)/$(_ARCH)/lib
instjava_path := $(_JAVADIR)
instroot_path := $(_DESTROOT)
endif
endif
endif

ifeq "$(_DESTROOT)" ""
DESTROOT := ./build
_DESTROOT := $(abspath $(DESTROOT))
_LIBDIR := $(_DESTROOT)/$(LIB)
_INCDIR := $(_DESTROOT)/include
_JAVADIR := $(_DESTROOT)/share/java/ccmtools
_BINDIR := $(_DESTROOT)/bin
endif

##
## setup INSTDIR
##
ifeq "$(instlib_path)" ""
ifeq "$(instjava_path)" ""
ifeq "$(instroot_path)" ""
ifneq "$(INSTDIR)" ""
instlib_path := $(INSTDIR)/$(LIB)/
instjava_path := $(INSTDIR)/share/java/ccmtools
instroot_path := $(INSTDIR)
else
instlib_path :=
instjava_path := $(_JAVADIR)
instroot_path := $(_DESTROOT)
endif
endif
endif
endif

LOCALCPPLIB_PATH := $(_LIBDIR)/libccmtools_local.$(SOV)
LOCALCPPLNK_PATH := $(_LIBDIR)/libccmtools_local.$(SO)

define install-headers
  @echo $1 | perl -e 'use File::Basename; use File::Path; use File::Copy; $$from=<>; $$_=$$from; s|\./cpp-environment/wx/(?:[^/ ]+)/(?:[^/ ]+/)?|$(_INCDIR)/WX/Utils/|g; s|\./cpp-environment/ccm/|$(_INCDIR)/|g; s|/HomeFinder/HomeFinder.h|/HomeFinder.h|g; s|/CCM_Local/LocalComponents/|/LocalComponents/|g; @from=split(/\s+/,$$from); @to=split(/\s+/,$$_); while ( (scalar @from) > 0 && (scalar @to) > 0 ) { $$from = shift(@from); $$to = shift(@to); print "$$to\n"; if ( ! -d dirname($$to) ) { mkpath(dirname($$to)); } copy($$from,$$to) or die "Copy failed: $$!"; }'
endef

%.h: %.idl
	cd $(dir $<) && $(IDL) $(notdir $<)

%.o: %.cc
	$(C++) -fPIC -c $(OPT) -I$(_INCDIR) -I$(PYTHONINCD) $< -o $@

%.o: %.cpp
	$(C++) -fPIC -c $(OPT) -I$(_INCDIR) -I$(PYTHONINCD) $< -o $@

all: setup install_headers $(LOCALCPPLNK_PATH) install_jars install_scripts install_templates

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
	$(C++) -dynamiclib -install_name $(instlib_path)$(notdir $@) -o $@ $(filter %.o,$^) -L$(PYTHONLIBD) -lpython$(PYTHONVER)
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
	sed -i -e 's|^CCMTOOLS_LIB="/usr.*|CCMTOOLS_LIB="$(instjava_path)"|' $(_BINDIR)/ccmtools
	sed -i -e 's|^CCMTOOLS_HOME="/usr.*|CCMTOOLS_HOME="$(instroot_path)"|' $(_BINDIR)/ccmtools
	chmod 755 $(_BINDIR)/ccmtools

install_templates:
	tar -C ccmtools/src --exclude .svn -cf - templates | tar -C $(_DESTROOT) -xf -
