#!/usr/bin/env bash

echo "Checking tokens"
UUIDGEN=$(which uuidgen 2>/dev/null || echo "./uuidgen.sh")

if [ -s axonserver.token ] ; then
    echo "- Keeping old system-token"
else
    echo "- Generating system-token"
    ${UUIDGEN} > axonserver.token
fi
if [ -s axonserver-internal.token ] ; then
    echo "- Keeping old internal-token"
else
    echo "- Generating internal-token"
    ${UUIDGEN} > axonserver-internal.token
fi
if [ -s admin.password ] ; then
    echo "- Keeping old admin password"
else
    echo "- Generating admin password"
    ${UUIDGEN} > admin.password
fi

if [ -s axonserver.properties ] ; then
    echo "Keeping existing properties file"
else
    echo "Generating properties file"
    sed -e "s/__INTERNAL_TOKEN__/$(cat axonserver-internal.token)/" < axonserver.properties.tmpl > axonserver.properties
fi