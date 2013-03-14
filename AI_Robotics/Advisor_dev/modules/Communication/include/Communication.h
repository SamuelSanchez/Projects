#ifndef COMMUNICATION_H
#define COMMUNICATION_H

#include <boost/asio.hpp>
#include <boost/algorithm/string.hpp>
#include <boost/thread.hpp>  
#include <boost/date_time.hpp> 
#include <boost/system/error_code.hpp>
#include <stdlib.h>
#include <iostream>
#include <cstring>
#include <cmath>
#include <definitions.h>
#include "robotsProp.h"
#include "Message.h"
#include "metrobotics.h"

using namespace std;
using namespace metrobotics;

typedef unsigned char cmd_len_t;

class Communication
{

  public:
        Communication(Auction_Mode m): takeAuction(false), Mode(m), io_service_(), socket_(io_service_){
           deadLockTimer.start();
        }

	~Communication() 
	{
	   cout << " ~Communication " << endl;
           Disconnect();
	   //delete[] robots;
	   //delete message;
        }      

        //Handle connection
        bool Connect(string , unsigned short);
        void Disconnect();

	//Functions - Others
        void init_state();
        void Update();
	void userInput(char);	
	void setMessage(Message*);
	void stopRobotI(int);        	//Not in use
	void stopAllRobots();

        //Set Methods
	void set_ID(string, string);  //Sets the ID and type for the connection
	void setAucBuffer(string);
	void setAucBuffer(string, long);
	void setStartSearch(bool);	//Not in use

        //Get Methods
	int getNumberOfRobots();
	long getID(int);		//Not in use
	long getSessionID();		//Not in use
	int getState();
	bool getStartSearch();

	//Auction functions
	void begin_Search();
	void update_won();
	void do_state_action_status();	//Not in use
	void update_robots(vector<string>);   //only runs when the number of robots changed 
	void update_completion();

        //Accept Auction
        void setTargetPool( TargetPool * tp ) { targetPool = tp; }
        void startTakingAuctions(){ takeAuction = true; }
        void stopTakingAuctions(){ takeAuction = false; }
        void broadCastAllPoints(string, double);
        void broadCastWinners(long, int);
        void printMessageInCentralServer( string );
  private:
        bool takeAuction;
 
  	//Timer
	metrobotics::PosixTimer auctionTimer;
	static const double TIME_BROADCAST  = 1.0;
        TargetPool * targetPool;

        metrobotics::PosixTimer deadLockTimer;
        static const double DEALLOCK_TIME = 900;  //No experiment can take more than 15 mins with the current settings

	string ids;
        vector<robotsProp> robots;
	
        Message * message;
	int numberOfRobots;

	Auction_Mode Mode;

        //Auction members
        int Auc_ID;
	bool aucInProgress;
	bool startSearch;
        string AucBuffer;
        long mAuctionID;
	int finishedRobots;

        // Robot properties.
  	string mTypeID;
  	string mNameID;
        long mSessionID;
        int  mCurrentState;
        string mStringBuffer;

	boost::asio::io_service io_service_;
	boost::asio::ip::tcp::socket socket_;

	void askPose();
	void retrieve_pose();

        //Functions for managing communication
        bool read(stringstream&);
        bool write(const stringstream&);
        bool msg_waiting() const;
        void do_state_change(int);
	void do_state_action_init();
 	void do_state_action_ack();
   	void do_state_action_idle();
	void do_state_action_cmd_proc();

       //Functions for managing actions in MapCutter
       // TODO:


       //Functions for managing actions in Auction
	void do_state_action_auction();
	void do_state_action_collect_bid();//		Not in use
	void do_state_action_bid();
	void update_bid();
	void do_state_action_won();
	void update_robot_target_pool(Point, long);	//Not in use
	void do_state_action_complete();		//!
	void do_state_action_finished();
	void update_finished();

       //Functions
	void lockUnlock(string , long);			//Depricated
	void do_state_process_robots_number();
	void do_state_action_process_robotsID();
	string get_RobotID();
	void splitIDS();

	boost::asio::ip::tcp::socket& getSocket() 
	{
		return socket_;
	}
};

#endif  /* COMMUNICATION_H */
