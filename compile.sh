#!/bin/sh
find src -name "*.java" > sources.txt
mkdir -p out
javac -d out @sources.txt && echo "Compiled successfully." || echo "Compile failed."
