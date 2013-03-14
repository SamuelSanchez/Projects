#ifndef COMBINATORIALAUCTIONSORTED_H
#define COMBINATORIALAUCTIONSORTED_H

#include "AuctionManager.h"

class CombinatorialAuctionSorted : public AuctionManager {
  public:
     CombinatorialAuctionSorted();

     ~CombinatorialAuctionSorted()
      {
          cout<<" ~CombinatorialAuctionSorted()"<<endl;
      }

//     void auctionPoints(Graph*);

     void print();

     void Update();

  private:
     //static const double AUCTION_TIME  = 9.0;
     double AUCTION_TIME;    //Time we will wait for replies - Let's make it proportional to the number of points
     double AUCTION_DURATION;//Time it takes for an auction to run - we'll send it to the robot
     bool firstAuction;
     bool inAuction;
     bool assigningTargets;
     int numberOfRobots;
     int difference;

     //Timer
     metrobotics::PosixTimer Auction_Timer;
     metrobotics::PosixTimer Auction_Duration;

     Auction DuPoint;      

     //Functions   
     void updateAuction();
     void startAuction();
};

#endif  /* COMBINATORIALAUCTIONPROXIMITY_H */
