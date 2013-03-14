#ifndef RANDOMALLOCATION_H
#define RANDOMALLOCATION_H

#include "AuctionManager.h"

class RandomAllocation : public AuctionManager {
  public:
     RandomAllocation();

     ~RandomAllocation()
      {
          cout<<" ~RandomAllocation()"<<endl;
      }

     void print();

     void Update();
/*
     void setMessage ( Message * m){
	  message = m;
     }

     void setCommunication( Communication * comm ){
	  connection = comm;
     }
*/
  private:
//     static const double MAX_TIME_CONNECT   = 10.0;

//     Message * message;
//     Communication * connection;
     Auction DuPoint;
     int numberOfRobots;
     int bidder;

     //PosixTimer connectTimer;

     //Functions   
     void updateAuction();
};

#endif  /* RANDOMALLOCATION_H */
