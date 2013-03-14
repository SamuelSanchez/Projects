/*
 * MCPainter.cpp
 *
 *  Created on: Jun 2, 2010
 *      Author: appleapple
 */

#include "MCPainter.h"

MCPainter::MCPainter() {
  // TODO Auto-generated constructor stub

}

void MCPainter::drawWalls(Map * map){
  glBegin(GL_LINES);
  {
    vector<MapWall> walls = map->getWalls();
    vector<MapWall>::iterator iter; 
    for ( iter = walls.begin(); iter != walls.end(); iter++ ){
      glColor3f(0,0,0);
      glVertex2f(iter->getX0(), iter->getY0()); 
      glVertex2f(iter->getX1(), iter->getY1());
    }
  }
  glEnd();
}

void MCPainter::drawInterestPoints(vector<Point> p){
  if ( p.size() > 0 ) {

    vector<Point>::iterator iter; 
    for( iter = p.begin(); iter != p.end(); iter++ ) {
      glBegin(GL_POLYGON);
      {
	glColor3f(1,0.5,0.5); 
	//glColor3f(0.95,0.95,0.95);       // if bg white
	glVertex2f(iter->getX()-2, iter->getY()-2); 
	glVertex2f(iter->getX()-2, iter->getY()+2); 
	glVertex2f(iter->getX()+2, iter->getY()+2); 
	glVertex2f(iter->getX()+2, iter->getY()-2);  
      }
      glEnd();
    }

     
  }
}

void MCPainter::drawVisitedPoints(Message * msg)
{
  //if ( msg->vAuctions.size() > 0 ) {

     for(int i = 0; i < msg->vAuctions.size();i++ )
	{
	glBegin(GL_POLYGON);
	{
	  
	if(msg->vAuctions.at(i).getAucCompleted()){
	   glColor3f(0.5,1,0.5);
	}
	
	else{ 
	    glColor3f(1,0.5,0.5);
	}
		 
	//glColor3f(0.95,0.95,0.95);       // if bg white
	glVertex2f(msg->vAuctions.at(i).getAucLocX()-2, msg->vAuctions.at(i).getAucLocY()-2); 
	glVertex2f(msg->vAuctions.at(i).getAucLocX()-2, msg->vAuctions.at(i).getAucLocY()+2); 
	glVertex2f(msg->vAuctions.at(i).getAucLocX()+2, msg->vAuctions.at(i).getAucLocY()+2); 
	glVertex2f(msg->vAuctions.at(i).getAucLocX()+2, msg->vAuctions.at(i).getAucLocY()-2);


	}
	glEnd();
     }
   //}
}

void MCPainter::drawNodes(Graph * g){
  if ( g->numNodes() > 0 ) {
    glBegin(GL_LINES);
    {
      vector<Node> nodes = g->getNodes();
      vector<Node>::iterator iter; 
      for( iter = nodes.begin(); iter != nodes.end(); iter++ ) {
	glColor3f(1,0.5,0.5); 
	//glColor3f(0.95,0.95,0.95);       // if bg white
	glVertex2f(iter->getX()-1, iter->getY()-1); 
	glVertex2f(iter->getX()-1, iter->getY()+1); 
	glVertex2f(iter->getX()-1, iter->getY()+1); 
	glVertex2f(iter->getX()+1, iter->getY()+1); 
	glVertex2f(iter->getX()+1, iter->getY()+1); 
	glVertex2f(iter->getX()+1, iter->getY()-1); 
	glVertex2f(iter->getX()+1, iter->getY()-1); 
	glVertex2f(iter->getX()-1, iter->getY()-1); 
      }
    }
    glEnd();
  }
}


void MCPainter::drawEdges(Graph * g){
  if ( g->numEdges() > 0 ) {
    glBegin(GL_LINES);
    {
      vector<Edge> edges = g->getEdges();
      vector<Edge>::iterator iter; 
      for( iter = edges.begin(); iter != edges.end(); iter++ ) {
	//glColor3f(0.1,0.1,0.1);  
	glColor3f(0.9,0.9,0.9);       // if bg white
	Node n1 = g->getNode(iter->getFrom()); 
	Node n2 = g->getNode(iter->getTo());
	glVertex2f(n1.getX(), n1.getY()); 
	glVertex2f(n2.getX(), n2.getY()); 
      }
    }
    glEnd();
  }
}

/* current call for this function does not include a realPosition. That
   should be added if there is such an info coming from an overhead camera 
   or something of the sort 
*/
void MCPainter::drawRobotPositions(int nRobots, Message * msg, Position realPosition){
  //draw estimated position
  //int nRobots = msg->Bidders.size();

  int lineLen = 15;  

  
    try{
	
      for(int i =0; i<nRobots; i++){
	//draw poligon
	glBegin(GL_POLYGON);	
  	{
	   glColor3f(1, 0.75, 0.75);
           //Draw a robot at the time
           int x1 = msg->Bidders.at(i).getX() - lineLen * cos(msg->Bidders.at(i).getTheta() + .3);
           int y1 = msg->Bidders.at(i).getY() - lineLen * sin(msg->Bidders.at(i).getTheta() + .3);
           int x2 = msg->Bidders.at(i).getX() - lineLen * cos(msg->Bidders.at(i).getTheta() - .3);
           int y2 = msg->Bidders.at(i).getY() - lineLen * sin(msg->Bidders.at(i).getTheta() - .3);
           glVertex2f(msg->Bidders.at(i).getX(), msg->Bidders.at(i).getY());
           glVertex2f(x1, y1);
           glVertex2f(x2, y2);
           glVertex2f(msg->Bidders.at(i).getX(), msg->Bidders.at(i).getY()); 
	 
        }
	glEnd();
	
	//draw Real Position
        glBegin(GL_LINES);
        {
           Position p = realPosition;
           glColor3f(0, 0, 1);
           int lineLen = 100;
           int x = p.getX() + lineLen * cos(p.getTheta());
           int y = p.getY() + lineLen * sin(p.getTheta());
           glVertex2f(p.getX(), p.getY());
           glVertex2f(x, y);
        }
        glEnd();

      }


    }catch (std::exception &e)  {
      std::cerr << "Client Exception Painter: " << e.what() << "\n";
    }
  
   
}
