#!/bin/bash

i=0
flag=false
while [ "$i" -lt 3 ]
do
  sleep 1
  CODE=`curl -I 'https://api.d1.run/api/Studio' 2>/dev/null | head -n 1 | cut -d$' ' -f2`
  echo $CODE
  if [ $CODE == "200" ]; then
    flag=true
    break
  fi
  i=`expr $i + 1`
done
if [ $flag == "false" ]; then
  echo "status=DOWN" >> $GITHUB_ENV; else
  echo "status=UP" >> $GITHUB_ENV
fi