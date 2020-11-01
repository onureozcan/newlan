FROM ubuntu:18.04

RUN apt-get update
RUN apt-get install -y nasm
RUN apt-get install -y gcc-6
RUN apt-get -y install libc6-dev-i386
RUN apt-get -y install gcc-6-multilib