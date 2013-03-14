#include <iostream>
#include <cstdlib>
#include<cassert>
#include <time.h>
#include <climits>
//#define DEBUG
#include "VNT.h"

using namespace std;	
int main(){
	VNT a;
	VNT b(5,4);
	cout << b << endl;
	b.add(1);
	b.add(23);
	b.add(4);
	cout << b << endl;
	
	return 0;
}