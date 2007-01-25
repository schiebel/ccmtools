#! /bin/sh

# arguments :
# 1 - package name
# 2 - include path specification
# 3 - idl files to generate code from
# 4 - if zero length, will uninstall package

sandbox_dir=`pwd`/sandbox
build_dir=${sandbox_dir}/build
install_dir=${sandbox_dir}/install
data_dir=${sandbox_dir}/share/${PACKAGE}

${MKDIR} -p ${build_dir} ${install_dir} ${data_dir}

# set up the environment so we can change directories safely. this is nast, but
# necessary if we've got a bunch of relative paths in our environment. also link
# template directories so the generators can find them.

cwd=`pwd`

# get absolute paths for the top source and build dirs.

cd ${top_srcdir} ; abssrcdir=`pwd` ; cd ${cwd}
cd ${top_builddir} ; absbuilddir=`pwd` ; cd ${cwd}

for f in ${abssrcdir}/UI/scripts/ccmtools-* ; do ln -s $f ${sandbox_dir} ; done
for f in ${abssrcdir}/*Generator/*Templates ; do ln -s $f ${data_dir} ; done
for f in ${abssrcdir}/*Generator/*Environment ; do ln -s $f ${data_dir} ; done

mkdir ${sandbox_dir}/share/ccmtools/MDR
cp ${abssrcdir}/MDR/MOF/OCL.xml ${sandbox_dir}/share/ccmtools/MDR/

PCLASSPATH=${CLASSPATH}
PPATH=${PATH}
export CLASSPATH=${abssrcdir}/lib/mdr01.jar:${abssrcdir}/lib/oclmetamodel.jar:${abssrcdir}/lib/antlr.jar:${absbuilddir}
export CCMTOOLS_HOME=${sandbox_dir}
export PATH=${sandbox_dir}:${PATH}

ret=""

cd ${sandbox_dir}

# If there is no C++ environment, cancel test case execution 

test -e ${install_dir}/lib/libCCM_Local_LocalComponents.a && exit 1 

# generate component code.

rm -rf ${sandbox_dir}/${1}

test -z "${ret}" && ccmtools-c++-generate -d -a -p ${1} \
  -i ${install_dir} ${2} ${3} || ret=1

test -z "${ret}" && ccmtools-generate c++dbc -o ${1} ${3} || ret=1


# build and check. copy the contents of the package directory, if it exists, to
# the sandbox (this lets us distribute _app.cc files with the tests).

test -d ${abssrcdir}/test/CppDbcGenerator/${1}/impl && \
  ${CP} -rf ${abssrcdir}/test/CppDbcGenerator/${1}/impl ${1}

test -d ${abssrcdir}/test/CppDbcGenerator/${1}/test && \
  ${CP} -rf ${abssrcdir}/test/CppDbcGenerator/${1}/test ${1}

test -z "${ret}" && PYTHONPATH=${install_dir}:${PYTHONPATH} \
  ccmtools-c++-configure -p ${1} || ret=1

test -z "${ret}" && PYTHONPATH=${install_dir}:${PYTHONPATH} \
  ccmtools-c++-make -p ${1} || ret=1



test -z "${ret}" && ret=0

export CLASSPATH=${PCLASSPATH}
export PATH=${PPATH}
cd ${cwd}
exit ${ret}

