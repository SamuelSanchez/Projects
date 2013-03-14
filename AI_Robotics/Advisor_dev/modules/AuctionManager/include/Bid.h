#ifndef BID_H
#define BID_H

#include <iostream>

class Bid{
  public:
     long   robot; 	//robot id
     int    id;    	//bid id
     double cost;	//bid cost

      Bid(){
	 robot = id = cost = -1;
      }

      Bid(long rId, int bId, double c){
	 robot = rId;
	 id = bId;
	 cost = c;
      }

      ~Bid(){}

      void print() const
      {
	   std::cout << "Robot = " << robot <<"\n"
                     << "Robot bid id = " << id <<"\n"
                     << "Robot cost = "<< cost <<std::endl;
      }
};
#endif
