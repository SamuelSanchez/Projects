#ifndef TARGET_H
#define TARGET_H

#include "Bid.h"
#include <iostream>
#include <algorithm>
#include <vector>
#include <climits>
#define MAX_COST INT_MAX

class Target {
   static const int INVALID = -1;
   int targetId, x, y, winnerPositionInVector;
   string taskType, sensorType;
   long winnerRobot;
   vector<Bid> bids;
   bool targetCompleted;
   double minCost;

   struct sortingVector
   {
       bool operator() ( Bid i, Bid j )
       { return ( i.cost < j.cost ); }
   } sorting;
   
 public:
   Target( int ID = INVALID, int X = INVALID, int Y = INVALID, string task = "NONE", string sensor = "NONE" ): 
          targetId( ID ), x( X ), y( Y ), taskType( task ), sensorType( sensor ), winnerPositionInVector( INVALID ),
           winnerRobot( INVALID ), targetCompleted( false ), minCost( MAX_COST ){}

   ~Target(){}

   void sortBids()
   {
      //std::cout << "BEFORE SORT [ UPDATE ] " << std::endl;
      //print();
      //Let's sort the bids in an ascending order
      sort( bids.begin(), bids.end(), sorting );
 
      //Let's get the value of the min reply
      if( !bids.empty() ) 
         minCost = (*bids.begin()).cost;
      else
         minCost = MAX_COST;
      //std::cout << "AFTER SORT [ UPDATE ] " << std::endl;
      //print();
      //std::cout << "Min Cost [ " << minCost << " ]" << std::endl;
   }

   bool updateWinner()
   {
      //Increase the number of times I was asked to give a winner
      winnerPositionInVector++;

      //Get the winner & keep Track on how many times I was asked to get the winner
      if( winnerPositionInVector < bids.size() )
          winnerRobot = bids.at(  winnerPositionInVector ).robot;
      else
          winnerRobot = INVALID; //We over exceed the number of times that we could ask for winners
      //std::cout <<"UPDATE_WINNER [ " << winnerRobot << " ] " << std::endl;
      return winnerRobot != INVALID;      
   }

   void addBid( Bid bid )
   {
      //std::cout << "ADDING BID - TARGET ID [ " << targetId << " ]" << std::endl;
      bids.push_back( bid );
   }
   
   bool hasReply() const
   {
      return !bids.empty();
   }

   long getWinner() const
   {
      return winnerRobot;
   }

   int getID() const
   {
      return targetId;
   }

   double getCost() const
   {
      return minCost;
   }

   bool isTargetCompleted() const
   {
      return targetCompleted;
   }

   void setTargetCompleted()
   {
      targetCompleted = true;
   }

   string getTargetInfo() const
   {
     stringstream info;
     info << targetId << " " << taskType << " " << x << " " << y << " " << sensorType;
     return info.str();
   }

   int size() const
   {
      return bids.size();
   }

   void reset()
   {
      //std::cout << "DANGER! : WE ARE RESETING!" << std::endl;
      winnerPositionInVector = INVALID;
      winnerRobot = INVALID;
      bids.clear();     //Clear all bids
   }

   void print()
   {
	std::cout << "TARGET : " << std::endl;
        std::cout << "************"  << std::endl;
        std::cout << "ID         : " << targetId << std::endl;
        std::cout << "TASK       : " << taskType << " at " << x <<" - "<< y <<" with "<< sensorType << std::endl;
        std::cout << "WINNER     : " << winnerRobot << std::endl;
        std::cout << "COMPLETED  : " << targetCompleted << std::endl;

        vector<Bid>::iterator itr;
        for( itr = bids.begin(); itr < bids.end(); itr++ )
            std::cout<<"Bid : robot [ " << (*itr).robot << " ] - id [ " << (*itr).id << " ] - cost [ " << (*itr).cost << " ] " << std::endl;
   }
};
#endif  /* TARGETPOOL_H */
