## process this file with automake to produce Makefile.in

CLASSPATH_ENV = $(top_builddir):$(top_srcdir)/lib/antlr.jar
TESTS_ENVIRONMENT = top_builddir=$(top_builddir) top_srcdir=$(top_srcdir) \
                    JAVA=$(JAVA) CP=$(CP) RM=$(RM) MKDIR=$(MKDIR) \
                    PACKAGE=@PACKAGE@ CLASSPATH=$(CLASSPATH_ENV)

dist_noinst_SCRIPTS = test-loader.sh

# currently under investigation ... uncomment in local copy to test faster.

TESTS = \
        Example1.sh \
        Example2.sh \
        Example3.sh


dist_check_SCRIPTS = $(TESTS)

# these directories hold application logic and python modules for generated
# code.

EXTRA_DIST = 

clean-local:
	$(RM) -r -f sandbox

dist-hook:
	$(RM) -f -r `find $(distdir) -name CVS`
	$(RM) -f `find $(distdir) -name .cvsignore`

