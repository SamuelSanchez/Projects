#include "SimpleAuction.h"

     SimpleAuction::SimpleAuction():numberOfRobots(0){}


    //Try updating the number of robots
    inline void SimpleAuction::Update()
    {
           updateAuction();
           //UPDATE ANYTHING ELSE IN HERE

	   if(message->getAllDone())
	   {
	     print();
             stringstream str;
             str << "AUCTIONEER_FINISHED SUCCESSFUL" << " AGENTS " << connection->getNumberOfRobots(); 
             connection->printMessageInCentralServer( str.str() );
             message->setProgramQuit( true );
	   }
    }


    //Update robots information
    //TODO : Find the bug - Here is a bug!
    inline void SimpleAuction::updateAuction()
    {
	//cout << " After updating the connection " << endl;
	if(message->getNextAuction() &&  message->currAuc < (int)message->vAuctions.size() )
	{
	      if(!message->vAuctions.at(message->currAuc).getAucWon())  //will prevent auctions with winners from being reauctioned 
	      {
		connection->setAucBuffer(message->vAuctions.at(message->currAuc).send_points());
      	      	cout << "nExt Auction = " <<message->getNextAuction() <<endl;
	      }
	     
              message->currAuc++;
	}

	if(message->getNewPoints() && message->currAuc >= (int)message->vAuctions.size()) //start the auction over but only the auctions with no winner. 
	{
	      message->currAuc = 0;
	      message->setNewPoints(false);	
	}
      
        //Deploy when the points are auction and we haven't deployed yet
	if(message->currAuc == (int)message->vAuctions.size())
        {
		connection->update_won();
		if(connection->getStartSearch())
		{	
			connection->begin_Search(); //release robots to search the area.
			cout << "####BEGIN SEARCH####" << endl;
			message->printBidders();
			message->currAuc++; 	//increment so that this code will run only once.
		}
	}
        
    }

/*
    void SimpleAuction::auctionPoints(Graph* g)
    {
       //retrieve points to Auction
       for(int i = 0;i < g->numNodes();i++)
	{
           DuPoint.setAucNumber(i);
           DuPoint.setAucLocation(g->getNode(i).getX(),g->getNode(i).getY());
	   DuPoint.setAucTask("SWEEP");      //hard coded
           DuPoint.setAucSensor("CAMERA");  //hard coded
           DuPoint.setAucWinner(-1);
           DuPoint.setAucComplete(false);
	   DuPoint.setAucWon(false);
	   cout << "Auction at point "<< i  <<" = ("<< g->getNode(i).getX() <<" , " << g->getNode(i).getY() <<")" << endl;
    	   message->vAuctions.push_back(DuPoint);

       }
       cout << "vAuction size =" <<message->vAuctions.size() << endl;
    }
*/
    


    void SimpleAuction::print()
    {
	//print out results of all auc.
	cout << " Robots ARE ALL DONE WITH TASK" << endl;
    }
 
