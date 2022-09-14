#!/usr/bin/env bash

echo "Removing unused networks"
docker network prune -f

echo "Removing unused and unnamed volumes"
docker volume prune -f

echo "Removing named volumes"
docker volume ls | tail +2 | ( while read drive volume ; do docker volume rm -f ${volume} ; done )
