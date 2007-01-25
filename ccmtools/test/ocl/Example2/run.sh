#
# $Header: /cvsroot/ccmtools/ccmtools/test/ocl/Example2/run.sh,v 1.2 2004/02/26 13:05:14 rlechner Exp $
#
NAME=Example2
#
IDL=$NAME.idl
PKG=testOCL_$NAME
OUT=test_$NAME
VERSION=0.1
#
echo "ccmtools-generate c++dbc -o $OUT $IDL" > compile.sh
echo 'rm -f mdr.*' >> compile.sh
echo "ccmtools-c++-make -p $PKG -s $OUT" >> compile.sh
chmod 755 compile.sh
#
if ! ccmtools-c++-generate -a -d -c $VERSION -p $PKG -s $OUT $IDL; then exit 1; fi
if ! ccmtools-generate c++dbc -o $OUT $IDL; then exit 1; fi
rm -f mdr.*
if ! ccmtools-c++-configure -p $PKG -s $OUT; then exit 1; fi
if ! ccmtools-c++-make -p $PKG -s $OUT; then exit 1; fi
