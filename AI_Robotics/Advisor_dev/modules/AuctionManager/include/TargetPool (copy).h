#ifndef TARGETPOOL_H
#define TARGETPOOL_H

#include "Target.h"
#include <iostream>
#include <map>
#include <stdlib.h>
#include <algorithm>

class TargetPool {
   map<int,Target> targets;
   int targetsCompleted;
   map<long,bool> agents;    //Only if the agetnts are available can be reset for each point
   bool reauction_Targets;

   struct bidWinner
   { 
      bidWinner( int bid, long agent ): bid_id( bid ), agent_id( agent ) {}
      int bid_id;
      long agent_id; 
   };
   map<int,bidWinner> openBid;   //Position and pair Winners

   vector<Target> targetAgents;  //This is in order to sort targets - it is temporary
                                 //Map is a key sorted STL (standart template library) Container
   //Sorting Targets
   struct sortingTargets
   {
       bool operator() ( Target i, Target j )
       { return ( i.getCost() < j.getCost() ); }
   } sorting;

 public:
   TargetPool(): targetsCompleted( 0 ), reauction_Targets( false ) {}
   ~TargetPool() {}

   void updateBids()
   {
      //Let's clear the vector where we are going to store the points that have to be sorted [to be auction]
      targetAgents.clear();
      //Sorting reply bids in ascending order
      for( map<int,Target>::iterator itr = targets.begin(); itr != targets.end(); itr++ )
          if( (*itr).second.hasReply() ){
              (*itr).second.sortBids();
              //Once the  bids have been sorted [they have a reply], let's keep a copy
              targetAgents.push_back( itr->second );
          }
      //Now that all bids have been sorted for their respective replies, sort all bids in ascending order
      sort( targetAgents.begin(), targetAgents.end(), sorting ); 

      //std::cout << "SORTING ALL TARGETS" << std::endl;
      //for( vector<Target>::iterator itr = targetAgents.begin(); itr != targetAgents.end(); itr++ )
      //     std::cout << "Target [ " << (*itr).getID() << " ] - Cost [ " << (*itr).getCost() << " ]" << std::endl;
   }

   void updateWinners()
   {
      //std::cout << "UPDATE WINNERS" << std::endl;
      int counter = 1;
      int numberOfAgents = agents.size();
      openBid.clear();       //Reset Pair of Winners
      int numberOfWinners = 0;
     // std::cout << "Agents [ " << numberOfAgents << " ] - Targets [ " << targetAgents.size() << " ]" << endl;
      for( vector<Target>::iterator itr = targetAgents.begin(); itr != targetAgents.end(); itr++ ){
           //If there are more points than robots then stop iterating
           if( counter > numberOfAgents ){
               //std::cout << "NO MORE WINNERS - THERE ARE MORE POINTS THAN AGENTS" << std::endl;
               //std::cout << "AGENTS [ " << numberOfAgents << " ] - COUNTER [ " << counter << " ] - POINTS [ " << targets.size() << " ]" << std::endl;
               break;
           }
          // std::cout <<  "Target[ " << (*itr).getID() << " ]" << std::endl;
           if( !(*itr).isTargetCompleted() && (*itr).hasReply() ){
               int nAgents = (*itr).size(); //Get the number of replies for each Target
               //std::cout << "Find Winner - Replies for target [ " << nAgents << " ]" << std::endl;
               for( int agentSize = 0; agentSize < nAgents; agentSize++ ){
                   // std::cout << "Reply [ " << agentSize << " ]" << std::endl;
                    if( (*itr).updateWinner() && !(*agents.find((*itr).getWinner())).second ){//==false
                         (*agents.find( (*itr).getWinner() ) ).second = true;  //Setting to true means that agent cannot be reused
                         std::cout << "TARGET [ " << (*itr).getID() << " ] - WINNER [ " << (*itr).getWinner() << " ]" << std::endl;
                         bidWinner winner( (*itr).getID(), (*itr).getWinner() );
                         openBid.insert( pair<int,bidWinner> ( numberOfWinners, winner ) );
                         numberOfWinners++;
                         break;
                    }
               }
               counter++;
           }
      }
   }

   //TODO: DEBUG 
   void debugWinners()
   {
      std::cout << "DEBUG WINNERS" << std::endl;
      for( map<long,bool>::iterator itr = agents.begin(); itr != agents.end(); itr++ )
           std::cout << "WINNER [ " << (*itr).first << " ] - VALUE [ " << (*itr).second << " ]" << std::endl;

      for( map<int, bidWinner >::iterator itr = openBid.begin(); itr != openBid.end(); itr++ )
           std::cout << "POSITION [ " << (*itr).first << " ] - BID ID [ " << (*itr).second.bid_id << " ] - WINNER [ " << (*itr).second.agent_id << " ]" << std::endl;
   }   

   //Reset only for that points that are going to be auction each time.
   void reset()
   {
      //std::cout << "RESET - TARGET_POOL" << std::endl;
      reauction_Targets = false;
      for( map<int,Target>::iterator itr = targets.begin(); itr != targets.end(); itr++ )
           if( !(*itr).second.isTargetCompleted() )
               (*itr).second.reset();      

      for( map<long,bool>::iterator itr = agents.begin(); itr != agents.end(); itr++ )
           (*itr).second = false;
   }

   int getWinningBid( int pos ) const
   {
      return (*openBid.find( pos )).second.bid_id;
   }

   long getWinningAgent( int pos ) const
   {
      return (*openBid.find( pos )).second.agent_id;
   }

   void addTarget( Target target )
   {
      targets.insert( pair<int,Target> (target.getID(), target) );
   }

   void addBid( Bid bid, int targetId )
   {
      if( targets.count( targetId ) == 0 ){
         //std::cout<<"DEBUG : Target not found [ " << targetId << " ] " << std::endl;
         return;
      }
      //std::cout<<"DEBUG : Adding bid [ " << targetId << " ] " << std::endl;
      map<int,Target>::iterator itr;
      itr = targets.find( targetId );
      (*itr).second.addBid( bid );
   }

   void addAgent( long id )
   {
      //std::cout << "ADD AGENT [ " << id << " ]" << std::endl;
      agents.insert( pair<long,bool> ( id, false ) );
   }

   bool hasReply()
   {
      map<int,Target>::iterator itr;
      for( itr = targets.begin(); itr != targets.end(); itr++ ){
	 if( (*itr).second.hasReply() )
            return true;
      }
      return false;
   }
/*
   bool hasReply( int pos ) const //The id of the point is the position where it is located
   {
      if( targets.count( pos ) == 0 )
         return false;
      return (*targets.find( pos )).second.hasReply();
   }
*/ 
   bool hasAllReplies()
   {
      map<int,Target>::iterator itr;
      for( itr = targets.begin(); itr != targets.end(); itr++){
         if( (*itr).second.size() != agents.size() )
             return false;
      }
      return true;
   }

   int size() const
   {
      return targets.size();
   }
/*
   int agentSize() const
   {
      return agents.size();
   }
*/
   int targetsToBroadcast() const
   {
      return openBid.size();
   }

   bool isTargetCompleted( int targetID ) const
   { 
       //If the target doesn't exist then send that it is done
       if( targets.count( targetID ) == 0 )
          return true;
       (*targets.find( targetID )).second.isTargetCompleted();
   }

   bool isCompleted() const
   {
      return abs((int)targets.size() - targetsCompleted) == 0;
   }

   void setTargetCompleted( int targetID )
   {
      if( targets.count( targetID ) == 0 ){
         //std::cout << "Target completed not found [ " << targetID << " ] " << std::endl;
         return;
      }
      //std::cout << "Target Completed [ " << targetID << " ] " << std::endl;
      (*targets.find( targetID )).second.setTargetCompleted();
      reauction_Targets = true;
      targetsCompleted++;
   }

   bool reauctionTargets() const
   {
      return reauction_Targets;
   }

   string getTargetInfo( int pos ) const
   {
      if( targets.count( pos ) == 0 )
         return "0";
      return (*targets.find( pos )).second.getTargetInfo();
   }

   void print()
   {
      std::cout << "TARGETS : size [ " << targets.size() << " ] - completed [ " << targetsCompleted << " ] " << std::endl;
      map<int,Target>::iterator itr;
      for( itr = targets.begin(); itr != targets.end(); itr++ )
         if( !(*itr).second.isTargetCompleted() )
             (*itr).second.print();
   }
};
#endif  /* TARGETPOOL_H */
