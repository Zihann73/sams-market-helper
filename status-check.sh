#!/bin/bash

#if [ "$1" = "cn" ] && [ "$2" = "staging" ] && [ "$3" = "api" ]; then
#  url="https://stgapi.wooah.cn/api/Studio"
#elif [ "$1" = "cn" ] && [ "$2" = "production" ] && [ "$3" = "api" ]; then
#  url="https://api.wooah.cn/api/Studio"
#elif [ "$1" = "global" ] && [ "$2" = "staging" ] && [ "$3" = "api" ]; then
#  url="https://stgapi.d1.run/api/Studio"
#elif [ "$1" = "global" ] && [ "$2" = "production" ] && [ "$3" = "api" ]; then
#  url="https://api.d1.run/api/Studio"
#elif [ "$1" = "cn" ] && [ "$2" = "production" ] && [ "$3" = "document" ]; then
#  url="https://api.wooah.cn/api/DocumentConverter/health"
#elif [ "$1" = "global" ] && [ "$2" = "production" ] && [ "$3" = "document" ]; then
#  url="https://api.d1.run/api/DocumentConverter/health"
#else echo "status=DOWN" >> "$GITHUB_ENV"; exit 1
#fi

if [ "$1" = "global" ]; then
  region="d1.run"
elif [ "$1" = "cn" ]; then
  region="wooah.cn"
else echo "status=DOWN" >> "$GITHUB_ENV"; exit 1
fi

if [ "$2" = "staging" ]; then
  stage="stgapi"
elif [ "$2" = "production" ]; then
  stage="api"
else echo "status=DOWN" >> "$GITHUB_ENV"; exit 1
fi

if [ "$3" = "api" ]; then
  service="/api/Studio"
elif [ "$3" = "document" ]; then
  service="/api/DocumentConverter/health"
else echo "status=DOWN" >> "$GITHUB_ENV"; exit 1
fi

url="https://${stage}.${region}${service}"
echo "$url"
i=0

flag=false
while [ "$i" -lt 30 ]
do
  date
  sleep 2
  CODE=`curl -o /dev/null -s -w "%{http_code}\n" $url`
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
