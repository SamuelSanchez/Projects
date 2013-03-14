#include <iostream>
#include <cstdlib>
#include<cassert>
#include <time.h>
//#define DEBUG
#include "Matrix.h"

using namespace std;	
int main(){
	Matrix<int> a;
	Matrix<int> b(5,4);
	Matrix<int> c(2,5,4,5);
	
	cout << "a : [" << a.getRowCount() << "][" << a.getColumnCount() << "]" << endl;
	cout << "b : [" << b.getRowCount() << "][" << b.getColumnCount() << "]" << endl;
	cout << "c : [" << c.getRowCount() << "][" << c.getColumnCount() << "]" << endl;
	cout << endl;

	cout << "\tRow data" << endl;
	cout << "\t________" << endl;
	cout << a << endl;
	cout << b << endl;
	cout << c << endl;
	
	a.populateRandom();
	b.populateRandom(23);
	c.populateRandom();
		
	(*(*(b + 2) + 2)) = 100;
	c[3][4] = 99;
	(*( *(c + 4) + 5 )) = 999;
	(*( *(c + 3) + 5 )) = 10;

	/*
	for(int i = 0; i <= b.getHigh(); i++){
		for(int j = 0; j < b.getColumnCount(); j++){
			cout << b[i][j] << "\t";
		}
		cout << endl;
	}
	
	cout << endl;
	for(int i = 2; i <= 5; i++){
		for(int j = 4; j <= 5; j++){
			cout << c[i][j] << "\t";
		}
		cout << endl;
	}
	cout << endl;
	*/
	cout << "\tPopulated data" << endl;
	cout << "\t_______________" << endl;
	cout << a << endl;
	cout << b << endl;
	cout << c << endl;
	
	cout << "\tMultiplication" << endl;
	cout << "\t_______________" << endl;
	cout << "w = b * c " << endl;
	Matrix<int> w = b * c;
	cout << "w : [" << w.getRowCount() << "][" << w.getColumnCount() << "]" << endl;
	cout << w << endl;
	cout << endl;
	
	cout << "\tMultiplication" << endl;
	cout << "\t_______________" << endl;
	cout << "z = a * b " << endl;
	//Here it should throw an error saying that no data has been allocated
	Matrix<int> z = a * c;
	cout << "z : [" << z.getRowCount() << "][" << z.getColumnCount() << "]" << endl;
	cout << z << endl;
	
	return 0;
}