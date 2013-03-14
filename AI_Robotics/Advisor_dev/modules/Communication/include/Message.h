#ifndef MESSAGE_H
#define MESSAGE_H

#include <vector>
#include "Auction.h"
#include "Bidder.h"
#include "TargetPool.h"

class Message{

  public:
        Message();
        ~Message();

        //This vector is easy to manage if it's declared public
        vector<Auction> vAuctions;
        vector<Bidder> Bidders;
	Bidder posHolder;
        bool nextAuction; //tells when to procede to the next auction.
        bool searchAuctions;
        bool allDone; //used to tell if all Auctions have been completed.
	bool newPoints;
        Bidder McThristy;
        bool programQuit;
	int currAuc;
      
	bool getNextAuction();

	void setNextAuction(bool);

	void addBidder(Bidder);

	bool getAllDone();

	void setAllDone(bool);

	bool getNewPoints();
	
	void setNewPoints(bool);
	
	void removeBidder(int);
        
        void printBidders();

        void setProgramQuit(bool);

	bool getProgramQuit();
	
	void addPoints(Point);

	void resetAuctions();

        void transferPoints( TargetPool* );

};
#endif
