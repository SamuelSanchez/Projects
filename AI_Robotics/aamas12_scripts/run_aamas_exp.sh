#!/bin/bash

echo "Enter 0 for all-in-one OR" 
echo "      1 for distributed init robot configuration: " 
read choice

echo "Enter the number of robots [ 3, 4, 5 ]: "
read num_robots

#echo "Enter the Method:"
#echo "	0 - Random Allocation"
#echo "	1 - Simple Auction"
#echo "	2 - Combinatorial Proximity Auction"
#read method_number

if [ $choice -eq 0 ] 
then 
    echo "Running all in one mode"
    if [ $num_robots -eq 3 ]
    then
	echo "Number of Robots: 3"
	STAGE_FILE="../virtual_arena/lab/surveyor-3-allinone.cfg"
	num_pts=(2 3 8)
    elif [ $num_robots -eq 4 ]
    then
	echo "Number of Robots: 4"
	STAGE_FILE="../virtual_arena/lab/surveyor-4-allinone.cfg"
	num_pts=(2 4 9)
    elif [ $num_robots -eq 5 ]
    then
	echo "Number of Robots: 5"
	STAGE_FILE="../virtual_arena/lab/surveyor-5-allinone.cfg"
	num_pts=(2 5 9)
    fi
elif [ $choice -eq 1 ] 
then  
    echo "Running in distributed mode"
    if [ $num_robots -eq 3 ]
    then
	echo "Number of Robots: 3"
	STAGE_FILE="../virtual_arena/lab/surveyor-3-distributed.cfg"
	num_pts=(2 3 8)
        #num_pts=(1)
    elif [ $num_robots -eq 4 ]
    then
	echo "Number of Robots: 4"
	STAGE_FILE="../virtual_arena/lab/surveyor-4-distributed.cfg"
	num_pts=(2 4 9)
        #num_pts=(2 4 9)
    elif [ $num_robots -eq 5 ]
    then
	echo "Number of Robots: 5"
	STAGE_FILE="../virtual_arena/lab/surveyor-5-distributed.cfg"
	num_pts=(2 5 9)
    fi
fi
	
for RUN_NO in `seq 1 2`
do
    for NUM_PTS in ${num_pts[*]}
    do 
	for METHOD in 0 1 2 3 # 0: random allocation, 1: simple auction 
	do
	    if [ $METHOD -eq 0 ]
	    then
		method_name="Random Allocation"
	    elif [ $METHOD -eq 1 ]
	    then
		method_name="Auction"
            elif [ $METHOD -eq 2 ]
            then
                method_name="Combinatorial Auction"
	    elif [ $METHOD -eq 3 ]
	    then
		method_name="Combinatorial Auction 2"
            fi

	    echo "NUM_PTS:" $NUM_PTS ", METHOD:" $method_name ", RUN_NO:" $RUN_NO 

            # start player/stage
	    echo "Starting Stage..."
	    xterm -e player $STAGE_FILE &
	    sleep 3
	    
            # start robots 
	    echo "Starting RobotController 1..."
	    xterm -e ../RobotController/controller -d -f ../aamas12_scripts/robot-stage1.conf &
	    sleep 3
	    echo "Starting RobotController 2..."
	    xterm -e ../RobotController/controller -d -f ../aamas12_scripts/robot-stage2.conf &
	    sleep 3
	    echo "Starting RobotController 3..."
	    xterm -e ../RobotController/controller -d -f ../aamas12_scripts/robot-stage3.conf &
	    sleep 3

	    if [ $num_robots -ge 4 ]
	    then
		echo "Starting RobotController 4..."
		xterm -e ../RobotController/controller -d -f ../aamas12_scripts/robot-stage4.conf &
		sleep 3
	    fi
	    if [ $num_robots -eq 5 ]
	    then
		echo "Starting RobotController 5..."
		xterm -e ../RobotController/controller -d -f ../aamas12_scripts/robot-stage5.conf &
		sleep 3
	    fi

            # start taskmanager
	    echo "Starting Task Manager"
	    taskmanager="../Advisor_dev/TaskManager"
            filepath="../Advisor_dev/config_files/auction.conf" 
            pointPath="../Advisor_dev/config_files/points13.conf"
            #pointPath="../Advisor_dev/config_files/points11_2.conf"
            #pointPath="../Advisor_dev/config_files/points11.conf"
            #pointPath="../Advisor_dev/config_files/points10_3.conf"
            #pointPath="../Advisor_dev/config_files/points10_2.conf"
	    #taskmanager="../IntelligenceEngine/AuctionManager/taskmanager"
	    #filepath="../aamas12_scripts/advisor.conf"
	    #xterm -e $taskmanager -f $filepath -m $METHOD -n $NUM_PTS -a 2 -p $pointPath &
            xterm -e $taskmanager -f $filepath -m $METHOD -n $NUM_PTS -a 1 -p $pointPath &
	    sleep 2
	    init_pid=`pidof taskmanager`
	    sleep 1
            exp_array=(`pidof xterm`)
            exp_number=${#exp_array[@]}

            temp_array=(`pidof xterm`)
            temp_number=${#temp_array[@]}
	    while [ $exp_number -eq $temp_number ]
	    do
                temp_array=(`pidof xterm`)
                temp_number=${#temp_array[@]}
		sleep 1
	    done
            exp_array=(`pidof xterm`)
            for each_process in ${exp_array[*]} 
            do
 	        kill -9 $each_process
	    done        

	    #kill -9 $(pidof player)
	    sleep 5
	done
    done
done


