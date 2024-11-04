#!/bin/bash

current_dir=$(pwd)
project_dir="${current_dir}/proj"
project5_dir="${current_dir}/proj/p5"

# Change the current directory to the directory of the script
cd $project5_dir
make clean

cd $project_dir
tar -cvzf p5.tar.gz p5

mkdir -p $current_dir/dist
mv p5.tar.gz $current_dir/dist
