#! /bin/sh

cwd=`pwd`

cd ${top_srcdir} ; abssrcdir=`pwd` ; cd ${cwd}
cd ${top_builddir} ; absbuilddir=`pwd` ; cd ${cwd}

PATH=${abssrcdir}/UI/scripts:$PATH
CLASSPATH=${absbuilddir}:${abssrcdir}/lib/antlr.jar:$CLASSPATH

sandbox_dir=${cwd}/sandbox
data_dir=${sandbox_dir}/share/${PACKAGE}

${MKDIR} -p ${data_dir}
for f in ${abssrcdir}/*Generator/*Templates ; do ln -s ${f} ${data_dir} ; done

export CCMTOOLS_HOME=${sandbox_dir}

cd ${sandbox_dir}

ret=""

test -z "${ret}" && ccmtools-generate idl3 -o idl3.${IDL} \
  ${abssrcdir}/test/idl/${IDL}.idl || ret=1

test -z "${ret}" && ccmtools-generate idl3mirror -o idl3mirror.${IDL} \
  ${abssrcdir}/test/idl/${IDL}.idl || ret=1

test -z "${ret}" && ccmtools-generate idl2 -o idl2.${IDL} \
  -Iidl3.${IDL} idl3.${IDL}/* || ret=1

test -z "${ret}" && ccmtools-generate idl2 -o idl2mirror.${IDL} \
  -Iidl3mirror.${IDL} idl3mirror.${IDL}/* || ret=1

test -z "${ret}" && ret=0

${RM} -f -r share
cd ${cwd}
exit ${ret}

