#!/bin/bash

current_dir=$(pwd)
project_dir="${current_dir}/proj"
project3_dir="${current_dir}/proj/p3"

# Change the current directory to the directory of the script
cd $project3_dir
make

test_files_args=""
if [ "$#" -eq 1 ]; then
    test_files_args="$1"
    ./plan $test_files_args
fi

make clean
