#!/bin/bash

if [ "$1" = "global" ]; then
  region="d1.run"
  echo "$region"
elif [ "$1" = "cn" ]; then
  region="wooah.cn"
  echo "$region"
else
  echo "$1"
  echo "region env fail"
  exit 1
fi