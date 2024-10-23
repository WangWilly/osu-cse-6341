#!/bin/bash

current_dir=$(pwd)
project_dir="${current_dir}/proj"
project3_dir="${current_dir}/proj/p3"

# Change the current directory to the directory of the script
cd $project3_dir

if [ "$#" -eq 2 ]; then
    ./plan $1 < $2
else
    ./plan $1
fi
