#include "Auction.h"

   Auction::Auction(){}
   Auction::~Auction(){}

   void Auction::setAucNumber(int num){ AucNum = num;}
   void Auction::setAucWinner(long win){ AucWinner = win;}
   void Auction::setAucTask(string task){ AucTask = task;}
   void Auction::setAucLocation(int xT,int yT){ AucLocation.setX(xT); AucLocation.setY(yT);}
   void Auction::setAucSensor(string sensor){AucSensor = sensor;}
   void Auction::setAucComplete(bool complete){AucCompleted = complete;}
   void Auction::setAucWon (bool won){AucWon = won;}
   void Auction::setRunTime(double time){runTime = time;}
   vector<Bid> Auction::getBids() const {return bids;}


   int Auction::getAucNumber() {return AucNum;}
   long Auction::getAucWinner() {return AucWinner;}
   int Auction::getAucLocX(){return AucLocation.getX();}
   int Auction::getAucLocY(){return AucLocation.getY();}
   bool Auction::getAucWon(){return AucWon;}
   bool Auction::getAucCompleted(){return AucCompleted;}
   string Auction::getAucTask() {return AucTask;}
   Point Auction::getAucLocation() {return AucLocation;}
   string Auction::getAucSensor() {return AucSensor;}
  double Auction::getRunTime() {return runTime;}

   void Auction::add(Bid x)
   {
     bids.push_back(x);
   }

   void Auction::print()
   {
	cout << "AUCTION INFO" << endl;
        cout << "************" << endl;
        cout << "Auction # =  "<<AucNum<<endl;
        cout << "Auction Task = "<<AucTask<< " at "<<AucLocation.getX()<<","<<AucLocation.getY()<<" with "<<AucSensor<<endl;
        cout << "Auction Winner = "<<AucWinner<<endl;
        cout << "Auction Completed = "<<AucCompleted<<endl;
        
   }


   int Auction::sizeOfBids()
   {
	return bids.size();
    }

   void Auction::clearBids()
   {
     bids.clear();
   }

   long Auction::checkWinner()
   {
	int tempBid,winBid = 100000000 ,winPos;
        cout<<"Bid winner"<<endl;

	for(int i = 0; i < bids.size();i++){
	   tempBid = bids.at(i).cost;
	   
	   if(tempBid < winBid){ 
		winBid = tempBid;
		winPos = i;		
	   }
	}
	AucWon = true; // lets us know this auction has already taken place.

	return bids.at(winPos).robot;
  }


  string Auction::send_points()
  {
      int tempx, tempy;
      
     tempx = AucLocation.getX();
     tempy = AucLocation.getY();
     stringstream points;
    

     points <<AucNum << " " << AucTask<<" "<<tempx<<" "<<tempy<<" "<<AucSensor;

     return points.str();
  }
