#!/bin/bash

if [ "$1" = "global" ]; then
  region="d1.run"
elif [ "$1" = "cnn" ]; then
  region="wooah.cn"
else
  echo "status=DOWN" >> "$GITHUB_ENV"
  echo "The given region $1 does not exist."
  exit 1
fi

if [ "$2" = "staging" ]; then
  stage="stgapi"
elif [ "$2" = "production" ]; then
  stage="api"
else
  echo "status=DOWN" >> "$GITHUB_ENV"
  echo "The given stage $2 does not exist."
  exit 1
fi

if [ "$3" = "api" ]; then
  service="/api/Studio"
elif [ "$3" = "document" ]; then
  service="/api/DocumentConverter/health"
else
  echo "status=DOWN" >> "$GITHUB_ENV"
  echo "The given service $3 does not exist."
  exit 1
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
