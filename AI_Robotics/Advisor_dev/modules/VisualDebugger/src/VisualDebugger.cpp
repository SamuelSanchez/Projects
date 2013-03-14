#include "VisualDebugger.h"

VisualDebugger::VisualDebugger(Communication * c, Message * mg, Map * m, int proximity): comm(c), msg(mg), myMap(m) {
  g = new Graph(myMap, true, proximity, false); 
}

//called when the window changes position and size
void VisualDebugger::reshape(int w, int h) {
  glViewport(0, 0, (GLsizei) w, (GLsizei) h);
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
  gluOrtho2D(-10, myMap->getLength() + 10, -10, myMap->getHeight() + 10);
}

void VisualDebugger::keyboard(unsigned char key, int x, int y)
{
  cout << "\tUSER KEY COMMAND: " << key << endl;
  // Resume or Initiate tasks
  if (key == 'u' || key == 'U'){
     comm->userInput('u');
  }
  // Resume or Initiate tasks
  if (key == 'r' || key == 'R'){
     comm->userInput('r');
  }
  // Pause All tasks
  if (key == ' '){
     comm->userInput(' ');
  }
  // Stop All task 
  if (key == 's' || key == 'S'){
     comm->userInput('s');
  }
  // Print the robot's tasks
  if (key == 'p' || key == 'P'){
     comm->userInput('p');
  }
  if (key == 'q' || key == 'Q'){
     comm->userInput('s');       //stop all robots.
     msg->resetAuctions();
     comm->setStartSearch(false);		//TEMP TESTING FOR BROADCAST FOR EVERYONE
     p.clear();
  }
  if (key == 'a' || key == 'A'){
     msg->currAuc = 0;
     msg->setNextAuction(!msg->getNextAuction());    //TEMP TESTING FOR BROADCAST FOR ROBOTS
  }
  if (key == 'x' || key == 'X'){
     comm->userInput('s');
     msg->setProgramQuit(true);
  }
  if(key == 'b' || key == 'B'){		//begin Auctions
  }

}

void VisualDebugger::keyboardSpecial(int key, int x, int y){}

int VisualDebugger::getWinX(int x) {
  int wx = ( (double) (glutGet(GLUT_WINDOW_WIDTH) - 40)/ (double) myMap->getLength() ) * x + 20; 
  return wx; 
}

int VisualDebugger::getWinY(int y) {
  int wy = ( myMap->getHeight() - y ) * ( (double)( glutGet(GLUT_WINDOW_HEIGHT)- 20 ) / (double) myMap->getHeight() ) + 10;
  return wy;
}

int VisualDebugger::getMapX(int x) {
  int mx = ( x - 20 ) * ( (double) myMap->getLength()/ (double) (glutGet(GLUT_WINDOW_WIDTH)-40)); 
  return mx; 
}

int VisualDebugger::getMapY(int y){
  int my = myMap->getHeight() - (( y - 10 ) * ((double) myMap->getHeight() /(double) (glutGet(GLUT_WINDOW_HEIGHT) - 20) ));
  return my;
}

void VisualDebugger::mouse(int button, int state, int x, int y) 
{  
    if (button == GLUT_LEFT_BUTTON) {
      if (state == GLUT_DOWN){
         Point p(getMapX(x), getMapY(y));

         if ( myMap->isAccessible(p.getX(),p.getY(), 10) && myMap->isWithinBorders(p.getX(),p.getY()) ){  
            this->addPoint(p);
            msg->addPoints(p);
         }
      }
    }
    //cout << "This function has been disable! " << endl; 
}

void VisualDebugger::draw(void) {
  MCPainter painter;
  glClear(GL_COLOR_BUFFER_BIT);
  glClearColor(1,1,1,0); // set current color to white

  painter.drawWalls(myMap);
  //painter.drawInterestPoints(p); 
  painter.drawVisitedPoints(msg);
  //painter.drawNodes(g); 
  //painter.drawEdges(g);
  painter.drawRobotPositions((int)msg->Bidders.size(), msg, Position(0, 0, 0));

  glutSwapBuffers();
}

