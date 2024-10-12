#!/bin/bash

current_dir=$(pwd)
project_dir="${current_dir}/proj"
project3_dir="${current_dir}/proj/p3"

# Change the current directory to the directory of the script
cd $project3_dir
make clean

cd $project_dir
tar -cvzf p3.tar.gz p3

mkdir -p $current_dir/dist
mv p3.tar.gz $current_dir/dist
