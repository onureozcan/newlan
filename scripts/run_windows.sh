#!/usr/bin/env bash

mvn package
java -jar target/newlan-1.0-SNAPSHOT-jar-with-dependencies.jar $1 build_dir/out.asm -x64
cd build_dir
nasm -felf64 out.asm -o out.o
gcc -c ../native/entry.c -o entry.o
gcc out.o entry.o -o a.exe
./a.exe