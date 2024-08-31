#!/bin/bash

current_dir=$(pwd)
project_dir="${current_dir}/proj"
project1_dir="${current_dir}/proj/p1"

# Change the current directory to the directory of the script
cd $project1_dir
make clean

cd $project_dir
tar -cvzf p1.tar.gz p1

mkdir -p $current_dir/dist
mv p1.tar.gz $current_dir/dist
