/*
 * MCPainter.h
 */

#ifndef MCPAINTER_H_
#define MCPAINTER_H_

#include <GL/gl.h>
#include <GL/glut.h>
#include <Map.h>
#include "Graph.h"
#include "Point.h"
#include "Message.h"

class MCPainter {
public:
  MCPainter();
  void drawWalls(Map * map); 
  void drawRobotPositions(int, Message*, Position);
  void drawInterestPoints(vector<Point>);
  void drawVisitedPoints(Message*);
  void drawNodes(Graph * g);
  void drawEdges(Graph * g); 
};

#endif /* MCPAINTER_H_ */
