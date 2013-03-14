#ifndef BIDDER_H
#define BIDDER_H

#include <vector>

class Bidder{
  public:
      Bidder():robot_id(-1),xPos(0),yPos(0),theta(0){}

      Bidder(long id, double cx, double cy,bool value):robot_id(id),xPos(cx),yPos(cy),bidderFound(value){}

      ~Bidder(){}

      vector<Auction> taskPool;
      
      long robot_id;
      int xPos; 
      int yPos;
      double theta;
      bool bidderFound;
	
      long getrobot_id()
      {
        return robot_id;	
      }

      int getX(){
        return xPos;
      }

      int getY(){
	return yPos;
      }
   
      double getTheta(){
	return theta;
      }


      void print()
      {
	  cout << "########Bidder INFO for "<<robot_id<<" ######"<<endl;
	  cout << "Current Position = (" << xPos<< ","<<yPos<<")"<<endl; 
	  cout << "Number of point to visit = " << taskPool.size()<<endl;
          for(int i = 0; i < (int)taskPool.size();i++)
	  {
		cout << "Point "<< i << " = " <<endl; 
		taskPool.at(i).print();
	  }  
      }
};
#endif   
