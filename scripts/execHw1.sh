#!/bin/bash

current_dir=$(pwd)
project_dir="${current_dir}/proj"
project1_dir="${current_dir}/proj/p1"

# Change the current directory to the directory of the script
cd $project1_dir
make

test_files_args=""
if [ "$#" -eq 1 ]; then
    test_files_args="$1"
fi

./plan $test_files_args
