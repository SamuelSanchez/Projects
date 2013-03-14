#!/bin/sh
# This is a comment!
#echo "hello world!";

#GETTING ALL ARGUMENTS
#echo $@

#Sending Commands to the console
#use `terminal commands in here`

#VARIABLES ARE SAVED LIKE NAMES AND ARE CALLED WITH $NAME
#NAME="hello";
#echo $NAME

#Talk to festival
FESTIVAL=`echo "$@" | festival --tts`
