#!/bin/bash

current_dir=$(pwd)
project_dir="${current_dir}/proj"
project2_dir="${current_dir}/proj/p2"

# Change the current directory to the directory of the script
cd $project2_dir
make clean

cd $project_dir
tar -cvzf p2.tar.gz p2

mkdir -p $current_dir/dist
mv p2.tar.gz $current_dir/dist
