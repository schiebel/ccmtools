# $Id: Makefile.py,v 1.1 2005/01/11 08:59:22 teiniker Exp $

PROVIDE_H('CORBA.h')

code = """

AC_PKG_MICO

mico_basic_seen=yes

if test -n "$MICOSHAREDDIR"; then
    AC_DEFINE([HAVE_MICO], [], [Can we use Mico?])
fi

"""

CONFIGURE_IN(
    order=AC_OPTIONS,
    id='AC_PKG_MICO(basic)',
    lines=[code],
    propagate_only=1)

EXTERNAL_LIBRARY2(
    inc='@MICO_INCLUDE@',
    lib='@MICO_LIB@'
    )

## EXTERNAL_LIBRARY(
##     enabled='no',
##     defroot='/usr/local',
##     incdir='include',
##     libdir='lib',
##     lib_bases=[#'micoccm2.3.10',
##                'micocoss2.3.10',
##                'mico2.3.10',
##                # mico needs dlopen() and friends
##                'dl'],
##     provide=['CORBA.h'],
##     featuremacros=['HAVE_MICO']
##     )
