#ifndef AUCTION_H
#define AUCTION_H

#include "Bid.h"
#include <stdlib.h>
#include <iostream>
#include "Point.h"
#include <sstream>
#include <vector>

using namespace std;

class Auction{
   vector<Bid> bids; 
   int AucNum;
   int AucWinner;
   string AucTask;
   Point AucLocation;
   string AucSensor;
   bool AucCompleted,AucWon;
   int nRobots;			//Not in use
   double runTime;		//Not in use
        
  public:
   Auction();
   ~Auction();

   //Set Methods
   void setAucNumber(int);

   void setAucWinner(long);

   void setAucTask(string);

   void setAucLocation(int ,int);

   void setAucSensor(string);

   void setAucComplete(bool);

   void setAucWon (bool);

   void setRunTime(double);
   
   //Get Methods
   vector<Bid> getBids() const;

   int getAucNumber();

   long getAucWinner();

   int getAucLocX();

   int getAucLocY();

   bool getAucWon();

   bool getAucCompleted();

   string getAucTask();

   Point getAucLocation();

   string getAucSensor();

   double getRunTime();

   void add(Bid);

   void print();

   int sizeOfBids();
 
   void clearBids();

   long checkWinner();

   string send_points();
};

#endif
