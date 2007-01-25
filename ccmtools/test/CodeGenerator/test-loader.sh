#! /bin/sh

$JAVA Main ${top_srcdir}/test/idl/${IDL}.idl 2>&1 > `basename ${IDL}`.output

