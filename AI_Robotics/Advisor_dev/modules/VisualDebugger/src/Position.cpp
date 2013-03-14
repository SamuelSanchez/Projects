/*
 * Position.cpp
 *
 *  Created on: Dec 26, 2008
 *      Author: richardmarcley
 */

#include "Position.h"
#include <math.h>
#include <stdio.h>

Position::Position(double x, double y, double theta) {
	this->x = x;
	this->y = y;
	this->theta = theta;
}

double Position::getDistance(Position other) {
	double dx = x - other.getX();
	double dy = y - other.getY();
	double distance = sqrt(dx * dx + dy * dy);
	return distance;
}

double Position::getX() const {
	return x;
}

void Position::setX(double x) {
	this->x = x;
}

double Position::getY() const {
	return y;
}

void Position::setY(double y) {
	this->y = y;
}

double Position::getTheta() const {
	return theta;
}

void Position::setTheta(double theta) {
	this->theta = theta;
}

string* Position::toString() {
	char retVal[100];
	sprintf(retVal, "x=%f, y=%f, th=%f", x, y, theta);

	string* str = new string();
	str->assign(retVal);
	return str;
}

bool Position::operator==(Position p){
	return (x == p.getX() && y == p.getY() && theta == p.getTheta());
}

