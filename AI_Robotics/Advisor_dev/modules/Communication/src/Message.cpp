#include "Message.h"

Message::Message():nextAuction(true), searchAuctions(false),McThristy(),allDone(false),newPoints(false), programQuit(false),currAuc(0){}

Message::~Message(){}


bool Message::getNextAuction()
{
   return nextAuction;
}


void Message::setNextAuction(bool value)
{
   nextAuction = value; 	
}


void Message::addBidder(Bidder blackfinn)
{
    //find a way to prevent duplicates from entering Bidder vector
    Bidders.push_back(blackfinn);
}

void Message::removeBidder(int num) 
{
      //DO NOT AUCTION AGAIN THE POINTS OF A DEAD ROBOT FOR THE EXPERIMENTS
      // THE EXPERIMENT WILL BE CONSIDER A FAILURE IF A ROBOT DIES AND THE
      // EXPERIMENT WILL BE RUN AGAIN.
      // FOR MY EXPERIMENT I WILL ALSO CONSIDER IT FOR FAILURE IF A ROBOT DIES
      // BUT FOR THE FUTURE WHEN RESTRUCTURING THIS WHOLE CLASS I WILL REACUTION
      // THE POINTS OF A ROBOT THAT DIES, BUT I WILL SENT A MESSAGE TO SKYGRID
      // THIS AUCTION BELONGS FROM A ROBOT THAT DIED
      /*
	int Auc_id =-1;
	for(int i = 0; i < Bidders.at(num).taskPool.size();i++)
	{
	   if(!Bidders.at(num).taskPool.at(i).getAucCompleted())
	   {
	       Auc_id = Bidders.at(num).taskPool.at(i).getAucNumber();  //get Auction numbers of dead robot.
	       vAuctions.at(Auc_id).setAucWon(false);  //set those auctions to be no winner so they will be auctioned again on update.
	       newPoints = true;
	       cout << " All auctions from dead robot have been reset" <<endl;
	   }
	   else
	   {
	      cout << "Dead robot have been removed" <<endl;
	   }
	}    
	*/	

	Bidders.erase(Bidders.begin()+num);  //remove dead robot from vector of bidders.
}

bool Message::getAllDone()
{
    return allDone;
}

void Message::setAllDone(bool value)
{
    allDone = value;
}

bool Message::getNewPoints()
{
    return newPoints;
}
	
void Message::setNewPoints(bool value)
{
    newPoints = value;
}

void Message::printBidders()
{
	for(int i = 0; i < (int)Bidders.size();i++)
	{
	   Bidders.at(i).print();			
	}
}

void Message::setProgramQuit(bool b)
{
     	programQuit = b;
}

bool Message::getProgramQuit()
{
	return programQuit;
}

void Message::addPoints(Point p)  //really should be in auction manager but we never made pointers from visual debugger to Auction Mananger
{
        //Prevent double saving the same auctions
	for(int i=0; i < (int)vAuctions.size(); i++){
 	   if( p.getX() == vAuctions.at(i).getAucLocX() && p.getY() == vAuctions.at(i).getAucLocY() ){
	      cout << " Auction with Position : " << p.getX() << " , " << p.getY() << " is already saved! " << endl;
	      return;
	   }
	}

	Auction newPoint;
	newPoint.setAucNumber(vAuctions.size());
	newPoint.setAucLocation(p.getX(),p.getY());
	newPoint.setAucTask("SWEEP");      //hard coded
        newPoint.setAucSensor("CAMERA");  //hard coded
        newPoint.setAucWinner(-1);
        newPoint.setAucComplete(false);
	newPoint.setAucWon(false);
	vAuctions.push_back(newPoint);
	cout << "Point : ID [ " << newPoint.getAucNumber() << " ] - ("<< p.getX()<< "," << p.getY()<<") was added to auction"<<endl;

        //We should set new points here to true when somone clicks in the VD
	//newPoints = true;
}

//Trasfer all points from Message to TargetPool
void Message::transferPoints( TargetPool * tp )
{
    std::cout << "TRANSFERING POINTS - SIZE [ " << vAuctions.size() << " ] " << std::endl;

    for(int i=0; i < (int) vAuctions.size(); i++){
        Target temp( vAuctions.at( i ).getAucNumber(), vAuctions.at( i ).getAucLocation().getX(), vAuctions.at( i ).getAucLocation().getY(), 
                     vAuctions.at( i ).getAucTask(), vAuctions.at( i ).getAucSensor() );
        tp->addTarget( temp );
        std::cout << "POINT [ " << vAuctions.at( i ).getAucNumber() << " ] - [ " << tp->getTargetInfo( i ) << " ] " << std::endl;
    }
}

void Message::resetAuctions()
{
	vAuctions.clear();   //remove Auctions
	currAuc = 0;
	nextAuction = false;
	allDone = false;
        
	for(int i = 0; i < (int)Bidders.size();i++)
	{
	    Bidders.at(i).taskPool.clear();   // clear the auctions stored by the robots. 
	}
}
