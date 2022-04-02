# Next steps

## Goals

Have an measurement history for maven test bench

## Steps

maven test bench side 
1) Create a s3 bucket (done: bucketname: quickperf-maventestbench-measurements)
2) Push each measure file to s3 bucket thanks to a custom exporter (change AllocationExporter to push to s3)
   1) Think to push a timestamped file

maven plugin apache site
1) Get all s3 files 
   1) first all 
   2) second just take the last 10 days
2) Parse measurement file
   1) ex: 3.31 Giga bytes (3 552 270 680 bytes) --> parse to get only 3552270680
3) Generate graph from measurements