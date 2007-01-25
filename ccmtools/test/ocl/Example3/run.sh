#
# $Header: /cvsroot/ccmtools/ccmtools/test/ocl/Example3/run.sh,v 1.2 2004/02/28 18:22:03 rlechner Exp $
#
NAME=Example3
#
IDL=$NAME.idl
PKG=testOCL_$NAME
OUT=test_$NAME
VERSION=0.1
#
if ! ccmtools-c++-generate -a -d -c $VERSION -p $PKG -s $OUT $IDL; then exit 1; fi
if ! ccmtools-generate c++dbc -o $OUT $IDL; then exit 1; fi
rm -f mdr.*
cp src/*.h src/*.cc $OUT/src
cp src/test/*.* $OUT/test
if ! ccmtools-c++-configure -p $PKG -s $OUT; then exit 1; fi
if ! ccmtools-c++-make -p $PKG -s $OUT; then exit 1; fi
