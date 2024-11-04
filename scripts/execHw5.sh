#!/bin/bash

current_dir=$(pwd)
project_dir="${current_dir}/proj"
project5_dir="${current_dir}/proj/p5"

# Change the current directory to the directory of the script
cd $project5_dir

if [ "$#" -eq 2 ]; then
    ./plan $1 < $2
else
    ./plan $1
fi
