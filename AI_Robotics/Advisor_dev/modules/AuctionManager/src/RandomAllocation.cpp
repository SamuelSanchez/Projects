#include "RandomAllocation.h"

    RandomAllocation::RandomAllocation():numberOfRobots(0),bidder(0){}


    //Try updating the number of robots
    inline void RandomAllocation::Update()
    {
        if( numberOfRobots == 0 ){
	   numberOfRobots = connection->getNumberOfRobots();
	}

//        if( !message->getAllDone() ){
//	    updateAuction();
//        }
//	  else{
//	    print();
//	  }

	updateAuction();

	if( message->getAllDone() ){
	     print();
             stringstream str;
             str << "AUCTIONEER_FINISHED SUCCESSFUL" << " AGENTS " << connection->getNumberOfRobots(); 
             connection->printMessageInCentralServer( str.str() );
             message->setProgramQuit( true );
	}
    }


    //Update robots information
    inline void RandomAllocation::updateAuction()
    {
	if(message->getNextAuction() &&  message->currAuc < (int)message->vAuctions.size() )
	{
	   cout << " I PASS TO THE NEXT AUCTION " << endl;
	   if(!message->vAuctions.at( message->currAuc).getAucWon() ) //will prevent auctions with winners from being reauctioned 
	   {
	      connection->setAucBuffer( message->vAuctions.at( message->currAuc ).send_points(), message->Bidders.at(bidder).getrobot_id() );
	      bidder++;

		cout << "nExt Auction = " <<message->getNextAuction() <<endl;	
	
	      //We are going around the vector of robots
	      if( bidder == (int)message->Bidders.size() ){
		 bidder = 0;
	      }
	   }
           message->currAuc++;
	}

	if(message->getNewPoints() ) 
	{
	   message->currAuc = 0;
	   message->setNewPoints(false);	
	}
      
        //Deploy when the points are auction and we haven't deployed yet
	//A NEW METHOD TO PREVENT FROM DOUBLE AUCTION POINTS SHOULD BE ADDED HERE
	//INSTEAD OF INCREASING THE VECTOR SIZE
	if(message->currAuc == (int)message->vAuctions.size())
        {
	   connection->update_won();
	   if(connection->getStartSearch())
	   {
	      cout << " LET'S BEGIN THE SEARCH !!! " << endl;	
	      connection->begin_Search(); //release robots to search the area.
	      message->printBidders();
	      message->currAuc++; 	//increment so that this code will run only once.
	   }
	}
    }


    void RandomAllocation::print()
    {
	//print out results of all auc.
	cout << " Robots ARE ALL DONE WITH TASK" << endl;
    }
 
