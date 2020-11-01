#!/usr/bin/env bash

mvn package
java -jar target/newlan-1.0-SNAPSHOT-jar-with-dependencies.jar $1 build_dir/out.asm
docker-compose up