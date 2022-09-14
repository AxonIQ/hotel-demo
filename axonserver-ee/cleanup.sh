#!/usr/bin/env bash

echo "Removing generated files"
rm -f axonserver*.token axonserver.properties admin.password

echo "Removing ControlDB files"
rm -f data?/*.db

echo "Removing plugins"
rm -rf plugins?/{bundles,cache}

for d in log? events? ; do
    echo "Checking ${d}"
    for ctx in ${d}/* ; do
        if [ -d ${ctx} ] ; then
            echo "- Removing ${ctx}"
            rm -rf ${ctx}
        fi
    done
done