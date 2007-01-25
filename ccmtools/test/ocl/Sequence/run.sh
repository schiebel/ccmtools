#
# $Header: /cvsroot/ccmtools/ccmtools/test/ocl/Sequence/run.sh,v 1.2 2004/03/12 13:20:35 rlechner Exp $
#
NAME=MySequence
#
IDL=$NAME.idl
PKG=testOCL_$NAME
OUT=test_$NAME
VERSION=0.1
#
if ! ccmtools-c++-generate -a -d -c $VERSION -p $PKG -s $OUT $IDL; then exit 1; fi
if ! ccmtools-generate c++dbc -o $OUT $IDL; then exit 1; fi
rm -f mdr.*
cp _check_CCM_Local_CCM_Session_MySequence.cc $OUT/test
if ! ccmtools-c++-configure -p $PKG -s $OUT; then exit 1; fi
if ! ccmtools-c++-make -p $PKG -s $OUT; then exit 1; fi
echo "ccmtools-c++-make -p $PKG -s $OUT" > compile.sh
chmod 755 compile.sh
