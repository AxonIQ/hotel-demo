#!/usr/bin/env bash

bytes=($(cat /dev/urandom | od -t x1 | head -1 | cut -c 9-))
echo -n "${bytes[0]}${bytes[1]}${bytes[2]}${bytes[3]}"
echo -n "-${bytes[4]}${bytes[5]}"
echo -n "-${bytes[6]}${bytes[7]}"
echo -n "-${bytes[8]}${bytes[9]}"
echo -n "-${bytes[10]}${bytes[11]}${bytes[12]}${bytes[13]}${bytes[14]}${bytes[15]}"