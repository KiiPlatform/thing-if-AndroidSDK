#!/bin/sh

IP_ADDR=`command -v ip &> /dev/null && \
 ip addr show eth0 | sed -nEe 's/^\s+inet\W+([0-9.]+).*$/\1/p' || \
 ifconfig en0 | sed -nEe 's/^[[:space:]]+inet[[:space:]]+([0-9.]+).*$/\1/p'`

echo "{\"IP\" : \"${IP_ADDR}\"}" > thingif/src/test/assets/setting.json
