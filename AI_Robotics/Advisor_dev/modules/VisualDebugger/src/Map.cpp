/*
 * Map.cpp
 *
 *  Created on: Dec 21, 2008
 *      Author: richardmarcley
 *    Modified: added walls 
 */

#include "Map.h"

#include <iostream>
using namespace std;

Map::Map(){}

Map::Map(int length, int height) {
  this->length = length;
  this->height = height;
}

void Map::addWall(MapWall wall){
  walls.push_back(wall); 
}

vector<MapWall> Map::getWallById(string id){
  vector<MapWall> foundWalls;

  for (int i =0; i< walls.size(); i++) {
    if (walls[i].getId() == id) {
      foundWalls.push_back(walls[i]);
    }
  }

  return foundWalls;
}

MapWall Map::getWall(int index){
  return walls[index];
}


bool Map::isWithinBorders(int x, int y){
  if ( x > 0 && x < length && y > 0 && y < height ) 
    return true;
  return false; 
}

/*! \brief this function returns if a point in the map is accessible. at the moment it only tests 
           if the point is in an open area or on a wall. 
 */
bool Map::isAccessible(int x, int y){
  vector<MapWall>::iterator iter; 
  for( iter = walls.begin(); iter != walls.end(); iter++ ){
    if ( isPointOnLine( x, y, iter->getX0(), iter->getY0(), iter->getX1(), iter->getY1() , 1) )
      return false; 
  }
  return true; 
}

bool Map::isAccessible(int x, int y, int t){
  vector<MapWall>::iterator iter; 
  for( iter = walls.begin(); iter != walls.end(); iter++ ){
    if ( isPointOnLine( x, y, iter->getX0(), iter->getY0(), iter->getX1(), iter->getY1() , t) )
      return false; 
  }
  return true; 
}


