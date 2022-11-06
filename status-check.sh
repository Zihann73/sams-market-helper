#!/bin/bash

if [ "$1" = "cn" ] && [ "$2" = "staging" ]; then
  url="https://stgapi.wooah.cn/api/Studio"
elif [ "$1" = "cn" ] && [ "$2" = "production" ]; then
  url="https://api.wooah.cn/api/Studio"
elif [ "$1" = "global" ] && [ "$2" = "staging" ]; then
  url="https://stgapi.d1.run/api/Studio"
elif [ "$1" = "global" ] && [ "$2" = "production" ]; then
  url="https://api.d1.run/api/Studio"
else echo "status=DOWN" >> "$GITHUB_ENV"; exit 1
fi

echo "$url"
i=0

flag=false
while [ "$i" -lt 3 ]
do
  date
  sleep 1
  CODE=`curl -I 'https://stgapi.d1.run/api/Studio' 2>/dev/null | head -n 1 | cut -d$' ' -f2`
  echo "$CODE"
  if [ "$CODE" = "200" ]; then
    flag=true
    break
  fi
  i=$((i + 1))
done
if [ $flag = "false" ]; then
  echo "status=DOWN" >> "$GITHUB_ENV"
else
  echo "status=UP" >> "$GITHUB_ENV"
fi