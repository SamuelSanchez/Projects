#include <iostream>
#include <sstream>
#include <fstream>
//#define DEBUG
#include "SortedLinkedList.h"

using namespace std;

void displayUsage(){
	cout << "-- Run program as follows --" << endl;
	cout << "Usage :" << endl;
	cout << "\t<program_name> <input_file> <output_file>" << endl;
}

int main(int argc, char *argv[])
{
	ifstream file;
	ofstream output;
	stringstream ss;
	string str;	
	try{
		//Make sure that there's a file to read		
		if( argc < 3 ){
			displayUsage();
			return 1;
		}
		
		//Open file
		file.open(argv[1]);
		output.open(argv[2]);
		if( !file ){
			cout << "Unable to open file [ " << argv[1] << " ]" << endl;
			//cout << "Unable to open file!" << endl;
			return 1;
		}
		
		int c, e, odd = 0;		
		SortedLinkedList first, second;
		//Read the whole file
		while( getline(file, str).good() ){ 
			c = 0, e = 0;			
			ss << str;
			while( ss >> c >> e ){
				if( odd % 2 == 0 ){
					first.push( c, e );
				}else{
					second.push( c, e);
				}
			}			
			
			//Write to file
			if( ++odd % 2 == 0 ){
				SortedLinkedList temp;
				output << "original 1: " << first << endl;
				output << "original 2: " << second << endl;
				first.validate();
				second.validate();
				output << "canonical 1: " << first << endl;
				output << "canonical 2: " << second << endl;
				//Add both lists
				temp = first + second;
				output << "sum: " << temp << endl;
				//Substract both lists
				temp = first - second;
				output << "difference: " << temp << endl;
				//Multiply both list
				temp = first * second;
				output << "product: " << temp << endl;
				output << endl;
				//Flush everything into the file
				output.flush();
				first.clear();
				second.clear();
			}
			
			//Reset the input stream and the string
			ss.str("");
			ss.clear();
			str.clear();
		}
	}catch(std::exception &e){
		cout << "Error : " << e.what() << endl;
	}
	
    //Close files
	file.close();
	output.close();
	
    return 0;
}
