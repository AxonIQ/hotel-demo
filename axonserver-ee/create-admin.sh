#!/usr/bin/env bash

java -jar ./axonserver-cli.jar register-user -u admin -p $(cat ./admin.password) -r ADMIN@_admin -t $(cat ./axonserver.token)