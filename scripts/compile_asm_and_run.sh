#!/usr/bin/env bash
cd build_dir
nasm -f elf32 out.asm -o out.o
gcc -c -m32 ../native/entry.c -o entry.o
gcc -m32 entry.o out.o
./a.out
