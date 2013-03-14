#ifndef ADVISOR_H
#define ADVISOR_H

#include <exception>
#include "Graph.h"
#include "RandomAllocation.h"
#include "SimpleAuction.h"
#include "CombinatorialAuctionProximity.h"

typedef unsigned short cmd_port;

class Advisor{
  public:
     Advisor(Communication*, Message*, string, string, string, cmd_port, Auction_Mode);

     ~Advisor()
      {
          cout<<" ~Advisor()"<<endl;
	  delete taskAuctioneer;
          delete targets;
      }

     void setMode(Auction_Mode);

     void auctionPoints(Graph*);

     void run(){
         taskManagerThread = boost::thread(&Advisor::processCommands, this);
     }

     void join(){
	 taskManagerThread.join();
     }

  private:
     Communication * connection;
     Message * message;
     AuctionManager * taskAuctioneer;
     TargetPool * targets;

     static const double MAX_TIME_CONNECT   = 10.0;

     Auction_Mode mode;  
     stringstream init_message;                 
     bool notifyAuction;     

     //Functions   
     void updateAdvisor();

     void processCommands();

     boost::thread taskManagerThread;
};

 
#endif  /* ADVISOR_H */
