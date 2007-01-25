## process this file with automake to produce Makefile.in

TESTS_ENVIRONMENT = top_builddir=$(top_builddir) top_srcdir=$(top_srcdir) \
                    JAVA=$(JAVA) RM=$(RM) MKDIR=$(MKDIR) PACKAGE=@PACKAGE@

dist_noinst_SCRIPTS = test-loader.sh

TESTS = \
  component.sh                 \
  enum.sh                      \
  home.sh                      \
  interface.sh                 \
  interface-inheritance.sh     \
  interface-short-names.sh     \
  operation-component.sh       \
  operation-interface.sh       \
  operations-primitivetypes.sh \
  provides-operation.sh        \
  provides.sh                  \
  struct.sh                    \
  supports-operation.sh        \
  supports.sh                  \
  uses-multiple-operation.sh   \
  uses-multiple.sh             \
  uses-operation.sh            \
  uses.sh

dist_check_SCRIPTS = $(TESTS)

clean-local:
	$(RM) -r -f sandbox

dist-hook:
	$(RM) -f -r `find $(distdir) -name CVS`
	$(RM) -f `find $(distdir) -name .cvsignore`

