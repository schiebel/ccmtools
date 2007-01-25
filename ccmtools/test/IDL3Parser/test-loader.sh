#! /bin/sh

OUTPUT=test-`echo ${IDL} | sed 's,/,-,g'`.output
${JAVA} Main ${top_srcdir}/test/idl/${IDL}.idl 2>&1 > ${OUTPUT} \
    && cat ${OUTPUT} \
    | sed '1,/^Symbol table:$/d' \
    | grep -q "^\[s\] ${EXPECTED}" \
    && rm ${OUTPUT}
