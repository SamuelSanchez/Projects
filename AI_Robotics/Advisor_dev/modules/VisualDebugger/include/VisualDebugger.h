#ifndef VISUAL_DEBUGGER_H
#define VISUAL_DEBUGGER_H

#include "MCPainter.h"
#include "Message.h"
#include "Communication.h"

class VisualDebugger {
  Map * myMap;
  Graph * g; 
  vector<Point> p; 
  Communication * comm;
  Message * msg;

 public: 
  VisualDebugger(Communication*, Message*,Map*, int); 

  void reshape(int, int); 
  void keyboard(unsigned char, int, int); 
  void keyboardSpecial(int, int, int); 
  void mouse(int, int, int, int); 
  void draw(void); 

  void addPoint(Point pt) { msg->addPoints(pt); p.push_back(pt); }

  Graph* getGraph() { return g; }

  // util functions
  int getWinX(int); 
  int getMapX(int); 
  int getWinY(int); 
  int getMapY(int);
};

#endif
