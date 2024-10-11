#!/bin/bash

current_dir=$(pwd)
project_dir="${current_dir}/proj"
project2_dir="${current_dir}/proj/p2"

# Change the current directory to the directory of the script
cd $project2_dir
make

test_files_args=""
if [ "$#" -eq 1 ]; then
    test_files_args="$1"
fi

./plan $test_files_args
