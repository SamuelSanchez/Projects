#ifndef AUCTIONMANAGER_H
#define AUCTIONMANAGER_H

#include "Auction.h"
#include "Message.h"
#include "Graph.h"
#include "Communication.h"
#include "metrobotics.h"

//When inherit the class use the virtual keyword, 
//to avoid the diamond problem in the future,
//in case that another class inherits two parents 
//derived from this class
class AuctionManager{
  public:
     //AuctionManager();

     //Using virtual Dtor to ensure that we will delete
     //the pointer to the derived calss before the base class.
     //To avoid memory leakage.
     virtual ~AuctionManager()
     {
	cout << " Base Class Dtor :: ~AuctionManager()" << endl;
     }

     virtual void Update() = 0; 

     virtual void print() = 0;

     void setMessage ( Message * src )
     {
	  message = src;
     }

     void setCommunication( Communication * comm )
     {
	  connection = comm;
     }

     //Not so Sure if this should be here
     void setTargetPool( TargetPool * tp)
     {
          targetsToAuction = tp;
     } 

  protected:
     //DataResource * data;
     Message * message;
     Communication * connection;
     TargetPool * targetsToAuction;

     //PosixTimer connectTimer;

     //Functions   
     virtual void updateAuction() = 0; ////make it inline 
};

#endif  /* AUCTIONMANAGER_H */
