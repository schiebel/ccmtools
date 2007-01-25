#
# $Header: /cvsroot/ccmtools/ccmtools/test/ocl/Real/run.sh,v 1.2 2004/03/12 13:20:35 rlechner Exp $
#
NAME=MyReal
#
IDL=$NAME.idl
PKG=testOCL_$NAME
OUT=test_$NAME
VERSION=0.1
#
ccmtools-c++-generate -a -d -c $VERSION -p $PKG -s $OUT $IDL
if ! ccmtools-generate c++dbc -o $OUT $IDL; then exit 1; fi
rm -f mdr.*
cp _check_CCM_Local_CCM_Session_MyReal.cc $OUT/test
ccmtools-c++-configure -p $PKG -s $OUT
ccmtools-c++-make -p $PKG -s $OUT
echo "ccmtools-c++-make -p $PKG -s $OUT" > compile.sh
chmod 755 compile.sh
