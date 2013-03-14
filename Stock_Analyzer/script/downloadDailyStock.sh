#!/bin/sh

#Program dependents
Directory="/home/eduardo/Research/ML/Stock_Analyzer";
arg="${Directory}/data/company.stock";
jar="${Directory}/script/stockretrieval.jar";
Program_Name="mainApp";

#Retrieve program PID from the running processes
#Do not add .java in the name of the program
Program_PID=`ps -ef | grep java | grep $Program_Name`;

#Check if the program is running
if [ "$Program_PID" = "" ]; then
   echo "PID is Empty!";
   #Run the java program
   java -jar ${jar} ${arg} 
else
   echo "PID is $Program_PID";
fi