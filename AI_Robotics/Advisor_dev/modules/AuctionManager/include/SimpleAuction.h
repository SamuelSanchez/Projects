#ifndef SIMPLEAUCTION_H
#define SIMPLEAUCTION_H

#include "AuctionManager.h"

class SimpleAuction : public AuctionManager {
  public:
     SimpleAuction();

     ~SimpleAuction()
      {
          cout<<" ~SimpleAuction()"<<endl;
      }

     void auctionPoints(Graph*);

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
   
     //vector<Auction> vAuctions; 
     Auction DuPoint;      

     int numberOfRobots;
     

     //PosixTimer connectTimer;

     //Functions   
     void updateAuction();
};

#endif  /* SIMPLEAUCTION_H */
