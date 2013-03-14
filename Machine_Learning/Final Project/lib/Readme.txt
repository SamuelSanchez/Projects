Note :
	- Apache Ant must be installed in order to run this proram.

To Compile:
	- Type ant
	- Type antJ to create executable Jar
	
To Run:
	- Type ant run or to run from jar type runJ
	
	- It is using Naive Bayes ML algorithm	

	1) The program will start running and it will cotinusly ask the user demonstrations.
		
	2) Once that it has enough demostrations, it will try to predict an action for it. Every action comes with a probability.

	3) If the probability is more than the threshold, it will perform such action, otherwise it will ask the user for a demostration.

	4) The parameters of the program can be modified in the class called Global.java