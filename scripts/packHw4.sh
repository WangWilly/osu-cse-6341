#!/bin/bash

current_dir=$(pwd)
project_dir="${current_dir}/proj"
project4_dir="${current_dir}/proj/p4"

# Change the current directory to the directory of the script
cd $project4_dir
make clean

cd $project_dir
tar -cvzf p4.tar.gz p4

mkdir -p $current_dir/dist
mv p4.tar.gz $current_dir/dist
