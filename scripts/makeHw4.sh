#!/bin/bash

current_dir=$(pwd)
project_dir="${current_dir}/proj"
project4_dir="${current_dir}/proj/p4"

# Change the current directory to the directory of the script
cd $project4_dir
make
