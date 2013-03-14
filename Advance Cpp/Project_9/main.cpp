#include <iostream>
#include <sstream>
#include <fstream>
#include <map>
//#define DEBUG
#include "PermutedIndex.h"

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
	string str, word;
	PermutedIndex dictionary;	
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
			return 1;
		}
		
		int line = 1;
		//Read the whole file
		while( getline(file, str).good() ){ 			
			ss << str; //Get the entire line
			while( ss >>  word){
				dictionary.insert( word, line);
			}
			++line;
			//Reset the input stream and the string
			ss.str("");
			ss.clear();
			str.clear();
			word.clear();
		}
		//Print dictionary to file
		output << dictionary << endl;
	}catch(std::exception &e){
		cout << "Error : " << e.what() << endl;
	}
	
    //Close files
	file.close();
	output.close();
	
    return 0;
}
