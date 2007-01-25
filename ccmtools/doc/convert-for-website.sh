#!/bin/sh

USAGE="To use this script, change to either the tutorial or manual directories
in the CCM Tools CVS source tree. Then type '../convert-for-website.sh'.
You'll need to have your SSH set up properly to connect to ccmtools.sf.net."

cwd=`pwd`
doc=`basename $cwd`

if [ "$doc" != "manual" -a "$doc" != "tutorial" -a "$doc" != "guide" ] ; then
  echo "$USAGE"
  exit 1
fi

mkdir tmp
latex2html -dir tmp -html_version "4.0" -split 4 -local_icons $doc && \
  scp tmp/*.html tmp/*.css tmp/*.png \
    ccmtools.sf.net:/home/groups/c/cc/ccmtools/htdocs/$doc
rm -rf tmp

