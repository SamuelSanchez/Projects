/*
 * Map.h
 *
 *  Created on: Dec 21, 2008
 *      Author: richardmarcley
 */

#ifndef MAP_H_
#define MAP_H_

#include <vector>
#include <math.h>
#include <stdio.h>
#include <string>
#include "MapWall.h"
#include "Position.h"
#include "GraphUtils.h"
using namespace std;

class Map {
public:
  Map();
  Map(int length, int height);

  void addWall(MapWall wall); 
  vector<MapWall> getWallById(string id);
  MapWall getWall(int index); 
  vector<MapWall> getWalls() { return walls; }

  int getLength() { return length; }
  int getHeight() { return height; }
  bool isAccessible(int x, int y);
  bool isAccessible(int x, int y, int t);
  bool isWithinBorders(int x, int y);
  
  int length;
  int height;
  
protected:
  vector<MapWall> walls; 
};

#endif /* MAP_H_ */
