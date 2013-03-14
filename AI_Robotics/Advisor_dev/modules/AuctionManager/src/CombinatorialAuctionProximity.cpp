#include "CombinatorialAuctionProximity.h"

     CombinatorialAuctionProximity::CombinatorialAuctionProximity():AUCTION_DURATION(10), //Give a value 
        firstAuction(true), inAuction(false), assigningTargets(false), numberOfRobots(0),
        AUCTION_TIME( 10 ) //Give it a value
     {}

    inline void CombinatorialAuctionProximity::Update()
    {
         updateAuction();
         if( targetsToAuction->isCompleted() ){
	     print();
             stringstream str;
             str << "AUCTIONEER_FINISHED SUCCESSFUL" << " AGENTS " << connection->getNumberOfRobots(); 
             connection->printMessageInCentralServer( str.str() );
             message->setProgramQuit( true );
         }
    }

    inline void CombinatorialAuctionProximity::updateAuction()
    {
       if( !targetsToAuction->isCompleted() ){
          if( !inAuction && !assigningTargets)
             startAuction();
 
          //if( firstAuction && targetsToAuction->hasReply() ){
            // std::cout<<"OUR FIRST TIME TAKING AUCTIONS IS UP!!!"<< std::endl;
           //  firstAuction = false;
             //DONT KEEP THE TIME UNTIL WE DECIDE A WON MESSAGE
           //  AUCTION_DURATION = Auction_Duration.elapsed();
          //}

          if( ( Auction_Timer.elapsed() > AUCTION_TIME || targetsToAuction->hasAllReplies() ) && inAuction ){
             std::cout << "TIME [ " << Auction_Timer.elapsed() << " ] - GIVEN [ " << AUCTION_TIME << " ]" << std::endl;
             connection->stopTakingAuctions();
             //std::cout<<"TIME'S UP!! : MUST ASSIGNED AUCTIONS TO AGENTS THAT REPLIED!" << std::endl;
             //If not replies, then broadcast again            

             //reset Flags
             assigningTargets = true;
             inAuction = false;
     
             if( !targetsToAuction->hasReply() ){
                 assigningTargets = false;
                 targetsToAuction->reset(); //Don't think that it's necessary since there are not replies, nothing gets trigger
                 return;
	     }

             //targetsToAuction->debugWinners(); //Debug
             targetsToAuction->updateBids();
             targetsToAuction->updateWinners();
             //targetsToAuction->debugWinners(); //Debug

             for( int i = 0; i < targetsToAuction->targetsToBroadcast(); i++ ){
                  connection->broadCastWinners( targetsToAuction->getWinningAgent( i ), targetsToAuction->getWinningBid( i ) );
             }
             //After we have assigned all points, stop the timer - This is the time it takes to Auction and decide a winner
             AUCTION_DURATION = Auction_Duration.elapsed() + 1.0; //Let's give it an extra unit
          }

          if( !inAuction && assigningTargets ){
              if( targetsToAuction->reauctionTargets() ){
                  std::cout << "REAUCTION TARGETS" << std::endl;
                  assigningTargets = false;
                  targetsToAuction->reset();
                  //return;
              }
          }
       }
    }

    void CombinatorialAuctionProximity::startAuction()
    {
       std::cout << "STARTING TO TAKE AUCTIONS!!!" << std::endl;
       inAuction = true;
       connection->startTakingAuctions();
       int numberOfTargets = 0;
       
       for(int i = 0; i < targetsToAuction->size(); i++){
           //if( i== 0 && firstAuction )
           //   Auction_Duration.start();
           if( !targetsToAuction->isTargetCompleted( i ) ){
              //std::cout << "TARGET [ " << i << " ] - NOT DONE!" << std::endl;
              connection->broadCastAllPoints( targetsToAuction->getTargetInfo( i ), AUCTION_DURATION );
              numberOfTargets++;
              //We only need to keep the duration for broadcasting a point - last point
              //Since the timer in the robot side is reset everytime we give it a point
              Auction_Duration.start(); 
           }
           //else std::cout << "TARGET [ " << i << " ] - COMPLETED ALREADY!" << std::endl;
       }
       AUCTION_TIME =(double) numberOfTargets;
       Auction_Timer.start();
    }

    void CombinatorialAuctionProximity::print()
    {
	//print out results of all auc.
	cout << " Robots ARE ALL DONE WITH TASK" << endl;
    }
