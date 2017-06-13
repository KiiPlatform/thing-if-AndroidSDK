#!/bin/sh

if command -v ip &> /dev/null; then
    IP_ADDR=`ip addr show eth0 | sed -nEe 's/^\s+inet\W+([0-9.]+).*$/\1/p'`
elif command -v ifconfig &> /dev/null; then
    IP_ADDR=`ifconfig en0 | sed -nEe 's/^[[:space:]]+inet[[:space:]]+([0-9.]+).*$/\1/p'`
else
    echo "command not found"
    exit 1
fi

mkdir -p thingif/src/test/assets
echo "{\"IP\" : \"${IP_ADDR}\"}" > thingif/src/test/assets/setting.json
