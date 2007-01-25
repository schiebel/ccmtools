#
# $Header: /cvsroot/ccmtools/ccmtools/test/ocl/euromicro/PrePostInv/run.sh,v 1.1 2004/02/26 16:41:32 teiniker Exp $
#
NAME=test
#
IDL=$NAME.idl
PKG=testOCL_$NAME
VERSION=0.1
#
if ! ccmtools-c++-generate -a -d -c $VERSION -p $PKG $IDL; then exit 1; fi
if ! ccmtools-generate c++dbc -o $PKG $IDL; then exit 1; fi
rm -f mdr.*
if ! cp src/*.cc $PKG/src; then exit 1; fi
if ! cp src/*.h $PKG/src; then exit 1; fi
if ! cp test/*.cc $PKG/test; then exit 1; fi
if ! ccmtools-c++-configure -p $PKG ; then exit 1; fi
if ! ccmtools-c++-make -p $PKG ; then exit 1; fi
echo "ccmtools-c++-make -p $PKG" > compile.sh
chmod 755 compile.sh
