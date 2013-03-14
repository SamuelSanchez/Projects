#ifndef POINT_H
#define POINT_H

#include <iostream>

class Point{
	int x; 
	int y; 
        bool visited;
public:
	Point(int xI = 0, int yI = 0): x(xI), y(yI),visited(false) {}
	void setX(int xT) { x = xT; }
	void setY(int yT) { y = yT; }
        void setVisted(bool value){visited = value;}
	int getX() { return x; }
	int getY() { return y; }
        int getVisted(){return visited;}
	void print() const { 
		std::cout << "(" << x << "," << y << ")" << std::endl;
	}
};

#endif /*POINT_H*/
