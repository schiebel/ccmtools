# $Id: Makefile.py,v 1.1 2005/01/11 08:59:22 teiniker Exp $

PROVIDE_H('coss/CosNaming.h')
REQUIRE_H('CORBA.h')

code = """

if test x$mico_basic_seen != xyes; then
    AC_MSG_ERROR([internal error: mico/basic not seen])
fi

if test -n "$MICO_LIB"; then
    MICOCOSS_LIB="-L${MICODIR}/lib -lmicocoss${MICOVERSION}"
fi

AC_SUBST(MICOCOSS_LIB)

"""

CONFIGURE_IN(
    order=AC_LIBRARIES,
    id='AC_PKG_MICO(coss)',
    lines=[code],
    propagate_only=1
)

EXTERNAL_LIBRARY2(
    inc='@MICO_INCLUDE@',
    lib='@MICOCOSS_LIB@'
    )