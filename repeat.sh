#!/bin/bash

# 방법 2: 간단한 Bash while 루프
while true; do
  timestamp=$(date +"%Y-%m-%d %H:%M:%S")
  echo "Current timestamp: $timestamp"
  python3 ./generate-csv.py -n 1000 -o /Users/jaehan1346/Tmp/batch-exam/data.csv
  sleep 10
done