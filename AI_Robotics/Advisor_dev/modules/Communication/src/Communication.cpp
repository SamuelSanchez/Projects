#include "Communication.h"


	bool Communication::Connect(string hostname, unsigned short port) 
	{
	  
	  using namespace boost::system;
	  using namespace boost::asio;
	  
	  // Make sure that the socket isn't already open.
	  if (getSocket().is_open()) {
	    cout<<"Disconnect!"<<endl;
	    Disconnect();
	  }
	  
	  error_code ec; // Used to check for errors.
	  
	  // Get the IP address of the host.
	  ip::address addr = ip::address::from_string(hostname, ec);
		  
	  // Connect to the host.
	  ip::tcp::endpoint endpt(addr, port);
	  socket_.connect(endpt, ec);
	  
          if(ec) {cout<<"failed on connect"; return false;}
           
	  
	  
	  return true;
	}
	

	void Communication::Disconnect()
	{
	  // Prepend function signature to error messages.
	  static const string signature = "Disconnect()";
	  
	  // Save us some typing -- I'm already feeling the effects of RSI.
	  using namespace boost::system;
	  using namespace boost::asio;
	  
	  // Make sure that the socket is already open.
	  if (!getSocket().is_open()){cout<<"Disconnect: !isOpen()"<<endl; return;}
	  
	  error_code ec; // Used to check for errors.
	  
	  // Shutdown the connection.
	  socket_.shutdown(ip::tcp::socket::shutdown_both, ec);
	  
	  
	  // Close the socket.
	  socket_.close(ec);	  
	}


        bool Communication::write(const stringstream& msgSt)
	{
	  // Prepend function signature to error messages.
	  static const string signature = "write()";
	  
	  // Make sure that the socket is already open.
	  if(!getSocket().is_open()){ cout<<"Write: !isOpen()"<<endl; return false;}

	  // Compute the maximum size of a message.
	  const static size_t num_bits = 8 * sizeof(cmd_len_t);

	  const static string::size_type max_size = (1 << num_bits) - 1;
	  
	  // Make sure that the message doesn't exceed the maximum size.
	  string msg = msgSt.str();

	  if (msg.size() > max_size) {
	    
	    return false;
	  }

	  // Build the message.
	  boost::asio::streambuf outputBuffer;
	  ostream os(&outputBuffer);
	  // First the preamble: size of the message.
	  cmd_len_t len = static_cast<cmd_len_t>(msg.size());

	  os.write(reinterpret_cast<const char *>(&len), static_cast<streamsize>(sizeof(cmd_len_t)));
	  // Now the message itself.
	  os << msg;
	  
	  // Send the message.
	  try {
	    size_t n = boost::asio::write(getSocket(), outputBuffer.data());
	    outputBuffer.consume(n);

	    //boost::posix_time::millisec t(1200);
    	    //boost::this_thread::sleep(t);
	  } catch (boost::system::system_error) {
	    
	    return false;
	  }
	  
	  return true;
	}

        bool Communication::read(stringstream& ss){

	  static const string signature = "message::read()";
	  // Make sure that the socket is already open.

	  if (!getSocket().is_open()){ cout<<"read: !isOpen()"<<endl;return false;}

	  // Read the message.
	  boost::asio::streambuf inputBuffer;
	  try {
	    cmd_len_t len;

	    boost::asio::read(getSocket(), boost::asio::buffer(&len, sizeof(cmd_len_t)));
            size_t n = boost::asio::read(getSocket(), inputBuffer.prepare(len));

	    inputBuffer.commit(n);

	  } catch (boost::system::system_error) {
	    
	    return false;
	  }
	
	   // Convert the message into a workable format.
	  istream is(&inputBuffer);
	  // Clear the contents of the argument.
	  ss.str("");
	  // Fill the argument with the message that we just read.
	  ss << is.rdbuf();
	  
	 // cout<<"*************"<<endl;
	 // cout<<"READ: "<<ss.str()<<endl;
	 // cout<<"*************"<<endl;
	    
	  return true;
 	}

        void Communication::Update()
	{
           //If this experiment takes more than 15 mins, then something is wrong. Terminate program.
           //With the current settings, no experiment should take longer than 15 mins.
           if( deadLockTimer.elapsed() > DEALLOCK_TIME ){
               std::exit(1);
           }

	   //cout << " My current state : " << mCurrentState << endl;
	   switch (mCurrentState) {
		  case STATE_INIT: {
		    do_state_action_init();
		  } break;
		  case STATE_ACK: {
		    do_state_action_ack();
		  } break; 
		  case STATE_IDLE: {
		    do_state_action_idle();
		  } break;
		  case STATE_CMD_PROC: {
		   do_state_action_cmd_proc();
		  } break;
		  case STATE_AUCTION: {
		   do_state_action_auction();				
		  } break;
		  case STATE_COLLECT_BID: {
		   do_state_action_collect_bid();
		  } break;
		  case STATE_BID:  {
		   do_state_action_bid(); 
		  } break;
		  case STATE_COMPLETE: {
		   do_state_action_complete();
		  } break;
		  case STATE_WON: {
		   do_state_action_won();
		  } break;
		  case STATE_ROBOT_ID: {
		   do_state_process_robots_number();
		  } break;
                  case STATE_STATUS: {
 		   do_state_action_status();
		  }break;
		  case STATE_FINISH: {
		   do_state_action_finished();
		  }break;
		  default: {
		  cout<<"unrecognized state :"<< mCurrentState <<endl;
		  } break;
	   }//switch
	}//UPDATE


	void Communication::init_state()
	{
    	  auctionTimer.start();
	  mCurrentState = STATE_INIT;
	  mSessionID    = -1;
	  //nextAuction = true;
          numberOfRobots = 0;
	  Auc_ID = finishedRobots = 0;
	  aucInProgress = startSearch = false;
	}
       
        bool Communication::msg_waiting() const
	{
	  	  
	  // Save us some typing -- I'm already feeling the effects of RSI.
	  using namespace boost::system;
	  using namespace boost::asio;
	  
	  error_code ec; // Used to check for errors.
	  
	  // Check the socket.
	  size_t payload = socket_.available(ec);
	  if (ec) {
	    if (true) cout<< " failed to peek at the socket" << endl;
	    return false;
	  } else if (payload >= sizeof(cmd_len_t)) {
	    return true;
	  } else {
	    return false;
	  }
        }

	void Communication::do_state_change(int state)
	{
	  // Don't make false changes.
	  if (mCurrentState != state) {
	    if (state == STATE_INIT) {
	      init_state();
	    } else {
	      mCurrentState = state;
              Update();
	    }
	    // Update the state timer.
//	    mStateTimer.start();	
	  }
         else{
		//cout << "ELse of do_state_change " << endl;
          }
	}


	void Communication::do_state_action_init()
	{
		  cout << "STATE = INIT"<<endl;	  
		  // Send the INIT command.
		  stringstream ss;
		  ss << CMD_INIT << " " << mTypeID << " " << mNameID << " 0 0";
		  if (write(ss)) {
			do_state_change(STATE_ACK);
		  } else {
			do_state_change(STATE_INIT);
		  }
	}

	void Communication::do_state_action_ack()
	{
		  // Don't block while waiting for the command.
		  //if (!msg_waiting()) return;
		  cout<<"STATE = Ack"<<endl;
		  // Prepare to read the command.
		  stringstream ss;
		  string cmd;
		  long session_id;
		  if (read(ss) && (ss >> cmd >> session_id) && (cmd.find(CMD_ACK) != string::npos)) {
		    if (session_id > 0) {

		      mSessionID = session_id;
		      cout << "Auctioneer ID is : " << mSessionID << endl;

		      //Let's process ids if there are robots available
		      //askPose();

		      //if(numberOfRobots>0){
		         do_state_change(STATE_ROBOT_ID);
			 //cout << " After processing robot's ID "  << endl;
		     //}
		    }
		    else{
			cout << "Session id < 0 "<<endl;
		    }
		 } 
                 else {
		     //cout<<"Ack else"<<endl;
		    do_state_change(STATE_INIT);
		 }
		  //cout << " Outside " << endl;
                  do_state_change(STATE_IDLE);
	}

	void Communication::do_state_action_idle()
	{
         	  //cout << "State = IDLE" << endl;
		  // Keep an eye out for new commands.
		  if ( msg_waiting() ) {
		     do_state_change(STATE_CMD_PROC);
		  } 
		  
                  //Broadcast every second, not every millisecond
		  if( !aucInProgress && auctionTimer.elapsed() >= TIME_BROADCAST ){
 		     auctionTimer.start();
	             askPose();
		  }
	}

        void Communication::do_state_action_cmd_proc()
	{
		  //cout << "State = Process command" << endl;
		  // Don't block while waiting for the command.
		  if (!msg_waiting()) return;
		  
		  // Prepare to read the command.
		  stringstream ss;
		  string cmd;
		  if (read(ss) && (ss >> cmd)) {
		    // Process the command.
		    
		    if (cmd.find(CMD_POSE) != string::npos) {
			//We don't need to do it here, we already do it in the method "do_state_process_robots_number()"
		      	//cout << " Updating Pose! " << endl;
		        //do_state_change(STATE_ROBOT_ID);		//THIS STATE HAS NOT BEEN DECLARED!!!
			mStringBuffer = ss.str();
			retrieve_pose();
		    }
		    else if(cmd.find(CMD_AUCTION_BID) != string::npos){
			   mStringBuffer = ss.str();
		           do_state_change(STATE_BID); 
		    }
		    else if(cmd.find(CMD_AUCTION_COMPLETE) != string::npos){
			   mStringBuffer = ss.str();
                           do_state_change(STATE_COMPLETE);
	  	    }
                    else if (cmd.find(CMD_AUCTION_FINISHED) != string::npos) {
		      mStringBuffer = ss.str();
		      do_state_change(STATE_FINISH);				//THIS STATE HAS NOT BEEN DECLARED!!!
		    }
		    else if(cmd.find(CMD_AUCTION_ASKBID) != string::npos){
			   mStringBuffer = ss.str();
		           do_state_change(STATE_ASK_BID); 
	            }
                    else if (cmd.find(CMD_AUCTION_STATUS) != string::npos){
			   mStringBuffer = ss.str();
			   do_state_change(STATE_STATUS);
		           
		    }else {	
			cout << " Command : " << cmd << endl;
		        do_state_change(STATE_IDLE); //To handle errors
		    }
		  } 
	}


        void Communication::do_state_action_auction()
	{			
	        cout << "State = Auction_Start" << endl;
		stringstream ss;
		ss << AucBuffer << " " << mSessionID;
                cout <<"AucAucBuffer = " << AucBuffer << endl;
	      	write(ss);

                //thread this sleep depending on the number of robots
                //sleep(3);
		message->setNextAuction(false);
		aucInProgress = true;

		cout << " I AM IN AUCTION - GOING TO STATE IDLE " << endl;

		do_state_change(STATE_IDLE);
		//do_state_change(STATE_COLLECT_BID);
	}

	void Communication::do_state_action_collect_bid()
	{
                cout << "State = Collect_Bid" << endl;
                stringstream ss;
		ss << CMD_AUCTION_ASKBID << " " << mAuctionID;
		//ss << s;

	      	write(ss);

                do_state_change(STATE_IDLE);
        }

	void Communication::do_state_action_bid()
	{
           string command;
           long Bidder;
           double bid_cost;
           long myID;
           stringstream iss( mStringBuffer );
           iss >> command >> Bidder >> Auc_ID >> bid_cost >> myID;
 
           Bid negotiation( Bidder, Auc_ID, bid_cost );
     
		if( Mode == SIMPLE_AUCTION ){
		        message->vAuctions.at(Auc_ID).add(negotiation);
		       // message->vAuctions.at(Auc_ID).print();

			cout << "Current Bids  at " << Auc_ID << " = " << message->vAuctions.at(Auc_ID).sizeOfBids()<<endl;

			//If we got all the bids
			if(message->vAuctions.at(Auc_ID).sizeOfBids()==numberOfRobots){
				cout << " IF !! " << endl;
				cout << " Size of bids : " << message->vAuctions.at(Auc_ID).sizeOfBids() << endl;
				cout << " Number of Robots : " << numberOfRobots << endl;

				update_bid();
			}
			else
			    do_state_change(STATE_IDLE);
		}
		else if( Mode == RANDOM_ALLOCATION ){
		    cout << " RANDOM ALLOCATION " << endl;
	            //This method will help us skip all the way to send the robot that 
		    //it won such a bid and to not wait for the other replies
		    stringstream oss;
		    oss << Bidder << " " << Auc_ID;
		    AucBuffer = oss.str();

		    //Set the winner for the Auction
		    message->vAuctions.at(Auc_ID).setAucWon(true);
		    
		    do_state_change(STATE_WON);
		}
                else if( Mode == COMBINATORIAL_AUCTION_PROXIMITY || Mode == COMBINATORIAL_AUCTION_SORTED ){
                    //std::cout << "DEBUG [ " << mStringBuffer << " ] " << std::endl;
                    if( takeAuction ){
                       targetPool->addBid( negotiation, Auc_ID );
                       //std::cout << "BID ACCEPTED : " << std::endl;
                       //negotiation.print();
                    }
                    //else{
                    //   std::cout << "**BID REJECTED : " << std::endl;
                       //negotiation.print();
                    //}
                    do_state_change(STATE_IDLE);
                }
	}

        void Communication::update_bid()
        {
                long winner = -1;
                stringstream oss;

		//winner = bidVector.checkWinner();
		cout << "All Bids recieved for Auction " << Auc_ID << endl;
		winner = message->vAuctions.at(Auc_ID).checkWinner();
                oss << winner << " " << Auc_ID;
                AucBuffer = oss.str();
                do_state_change(STATE_WON);
	} 

       void Communication::do_state_action_won()
	{
	    cout << "State = Auction_Won" << endl;	
	    stringstream iss(AucBuffer);
	    stringstream oss;
	    string robot_id;
	    Auction taskPoint;

	    //Add wining point to robot target POOL
	    iss >> robot_id >> Auc_ID;
	    taskPoint = message->vAuctions.at(Auc_ID);
	    taskPoint.setAucWinner(atol(robot_id.c_str()));		//set the winner of the auction in robot copy of auctions
	    message->vAuctions.at(Auc_ID).setAucWinner(atol(robot_id.c_str()));  //set the winner of the auction in auctioneer copy of auctions
	    message->vAuctions.at(Auc_ID).clearBids();

	    // add auctions that robots win to there list of places to search.	
	    for(int i =0; i < message->Bidders.size();i++)
	    {
		if(message->Bidders.at(i).getrobot_id() == atol(robot_id.c_str()))
		{
			message->Bidders.at(i).taskPool.push_back(taskPoint);
			cout<<"Winner : " << AucBuffer <<endl;
		}
	    }        

	    oss << CMD_AUCTION_WON << " " << AucBuffer;
	    write(oss);
	    message->setNextAuction(true);
	    aucInProgress = false;	
	    //cout << "Robot "<< robot_id << " has won the Auction";

	    cout << " ####################################### " << endl;
	    cout << " DEBUG : " << oss.str() << endl;
	    cout << " AUC_ID : " << Auc_ID << endl;
	    cout << " ####################################### " << endl;

            do_state_change(STATE_IDLE);
	}


	void Communication::update_won()
	{
		cout << " Communication::update_won() " << endl;
		int numOfAucWon = 0;
		for(int i =0; i < message->vAuctions.size(); i++)
		{
			if( message->vAuctions.at(i).getAucWon() == true)
			{
			   numOfAucWon++;	
			}		    
		}
		cout << "nAuctions ( " << numOfAucWon << " ) , vAuctions.size() ( " << message->vAuctions.size() << " ) " << endl;

		if(numOfAucWon == message->vAuctions.size())
		{
		   cout << " IS THIS TRUE: nAuctions : " << numOfAucWon << " == vAuctions.size() " << message->vAuctions.size() << endl;
		   startSearch = true;
		}
	}

	void Communication::begin_Search()
	{
		cout << " What state is this : " << mCurrentState << endl;
		stringstream oss;

		oss << CMD_AUCTION_SEARCH << " -1";
		write(oss);
	}

	//Close to do_state_action_process_robotsID()
	//But only to find out if the number of robots have changed to proceed
	//updating or not other classes....
        void Communication::do_state_process_robots_number()
	{
	     stringstream ss;
             vector<string> token;
	     string n;
	     string cmd;
             
	     ss << CMD_ASKPOSE << " -1";

	     //HERE IS THE BUG OF WHY NOT ALL THE POINTS ARE PAINTED
	     if (write(ss)){
		 if(read(ss) && (ss >> cmd) && (cmd.find(CMD_POSE) != string::npos) ){
		     n = ss.str();
                     boost::split(token, n, boost::is_any_of(" "), boost::token_compress_on);
		
		     if(!aucInProgress){    	
			   update_robots(token);	
		     }
		 }
		 else{	
			//cout << " We don't update until next action" << endl;
 		 }
	     }
	}

	void Communication::askPose(){
	     stringstream ss;
             vector<string> token;
	     string n;
	     string cmd;
             
	     ss << CMD_ASKPOSE << " -1";

	     write(ss);
	}

        void Communication::retrieve_pose()
	{
	     vector<string> token;
             boost::split(token, mStringBuffer, boost::is_any_of(" "), boost::token_compress_on);

	     if( !aucInProgress ){  //Don't update the number of robots when an auction starts	 
	        update_robots(token);	//so that we don't wait for the new robot input since, we
	     }				//didn't give him anything to bid on.
	
	     do_state_change(STATE_IDLE);
	}

	void Communication::update_robots(vector<string> tok)
	{
		int newNumofRobots = atoi(tok[1].c_str());
		if(newNumofRobots == 0)
		{
		  //cout << " No Bidders Present " << endl;
	         return;
		}
		   //cout << "New Bidder has entered Auction" <<endl;
		   //cout << "Number of Robots : " << newNumofRobots << endl;
		   
		   //NEED TO CLEAR THE VECTOR FIST!!!!
		   //populate temp robot array.
		   for(int holder = 2; holder < tok.size(); holder = holder +5 )  //skip the Confidence and theta value token
		   {
               		//holder = holder+2; //used to skip the Confidence and theta value token
			robots.clear(); // clear the vector to prevent overlapping data. 
			robotsProp aRobot;
			aRobot.robotFound = false;
			aRobot.id = atol(tok[holder].c_str());
			aRobot.x = atol(tok[holder+1].c_str());
			aRobot.y = atol(tok[holder+2].c_str());
			aRobot.theta = atol(tok[holder+3].c_str());

		        //cout << " robot has been parsed" << endl;
			for(int i =0; i < message->Bidders.size();i++)
			{ 
			  //cout << " Looking for robot " << endl;
   			  if(aRobot.id == message->Bidders.at(i).robot_id) //check to see if the ID are present in the vector of Bidders
			  {
			    //if found update info.
               		   //cout << " robot has been found" << endl;
			    message->Bidders.at(i).xPos =  atol(tok[holder+1].c_str()); //update X postion in both vectors
                      	    message->Bidders.at(i).yPos =  atol(tok[holder+2].c_str()); //update Y postion in both vectors
			    message->Bidders.at(i).theta = atol(tok[holder+3].c_str());
		            aRobot.robotFound = true;				//mark robot found it both vectors.
			    message->Bidders.at(i).bidderFound = true;
			    robots.push_back(aRobot);
			    break; //robot was found, dont continue searchin for it.		
                 	  }
 		        }

			robots.push_back(aRobot);
		        if(!aRobot.robotFound)
			{
			   //Should use the method of message->addBidder();
			   message->Bidders.push_back( Bidder(aRobot.id, aRobot.x, aRobot.y,true )); //add new robot to vector of bidders and mark it found.

                           targetPool->addAgent( aRobot.id );

			   cout << " New Bidder has been entered" << endl;	
			}
		   }

		   for(int j =0; j < message->Bidders.size();j++) //this loop checks to make sure no robots have died.
		   {
			if(message->Bidders.at(j).bidderFound)
			{
			  
			  message->Bidders.at(j).bidderFound =false; //reset bidderFound so i can be checked on next update.
			}
			else
			{
				cout <<"Robot "<< message->Bidders.at(j).robot_id<<" has died, new points available for Auction." <<endl;
				startSearch = false;
				message->removeBidder(j);
				
			}
		   }
		   
		   numberOfRobots = newNumofRobots;
	}

        void Communication::do_state_action_process_robotsID(){
		  stringstream ss;
		  ss << CMD_ASKPOSE << " -1";
                  cout << "State = Process_RobotID" << endl;
		  if (write(ss)){
		    ids = get_RobotID();
		    splitIDS();
		  }
		  else{
		    cout<<"ELSE IN PROCESS_ROBOTSID"<<endl;
		    do_state_change(STATE_ROBOT_ID);
		  }
        }//process_robotsID


	string Communication::get_RobotID()
	{
		  stringstream ss;
		  if (read(ss)){
		     cout << "retrieved robot ID"<<endl;
		    return ss.str();
		  }
		  else 
		     return "-1";
	}//get_RobotID


        void Communication::splitIDS()
	{
           int holder = 0;
	   robotsProp tempRobot;

           vector<string> token;
           boost::split(token, ids, boost::is_any_of(" "), boost::token_compress_on);
    
           //create dynamic array for robots
           numberOfRobots = atoi(token[1].c_str());

           //delete[] robots;
	   vector<robotsProp> robots;

           //populate robots with Robot DATA
           for(int i = 0; i < numberOfRobots; i++){
               holder = holder+2; //used to skip the Confidence value token 
               for(int j =0; j < 3;j++){
		  if(j==0){
		     tempRobot.id = atol(token[holder].c_str());
                     message->McThristy.robot_id = atol(token[holder].c_str());
                     message->addBidder(message->McThristy);
		  }else if(j==1){
		     tempRobot.x = atoi(token[holder].c_str());
                     
		  }else{
		     tempRobot.y = atoi(token[holder].c_str());
                  }
		   holder++;
		   robots.push_back(tempRobot);
               }
           } 

	   //For debugging porpuses
           cout<<"##########################################"<<endl;
           cout<<"Robot ID       X    Y"<<endl;
           cout<<"------------------------"<<endl;    
           int printpose =0;

           while(printpose < numberOfRobots)
	   {
               cout<<robots.at(printpose).id<<"    "<<robots.at(printpose).x<<"  "<<robots.at(printpose).y<<endl;
               printpose++;
           }
	}
        

 

	void Communication::update_robot_target_pool(Point interest, long id){}
	

	void Communication::do_state_action_complete()
	{
           string command; 
           long robot_ID;
	   long myID;
           int Auc_ID;
	   double time;
	   double delay;
           stringstream iss(mStringBuffer);
           iss >> command >> robot_ID >> myID >> Auc_ID >> time >> delay;
	
	   message->vAuctions.at(Auc_ID).setAucComplete(true);

	   cout << "DEBUG - COMPLETE : " << iss.str() << endl;	
           cout << "Robot " << robot_ID << " has completed auction " << Auc_ID << " in "<< time << " with delay of " << delay << endl;

           //GET WHEN A ROBOT REPLIES WITH A POINT
           if( Mode == COMBINATORIAL_AUCTION_PROXIMITY || Mode == COMBINATORIAL_AUCTION_SORTED ){
               targetPool->setTargetCompleted( Auc_ID );
           }
		
	   //check to see if all auctions have been completed. 
	   update_completion();
           do_state_change(STATE_IDLE);
	}

	void Communication::update_completion()
	{
                if( Mode == COMBINATORIAL_AUCTION_PROXIMITY || Mode == COMBINATORIAL_AUCTION_SORTED ){
                    return;
                }

		int numOfAucCompleted = 0;
		for(int i =0; i < message->vAuctions.size(); i++)
		{
			if( message->vAuctions.at(i).getAucCompleted() == true)
			{
			   numOfAucCompleted++;	
			}		    
		}
		
		if(numOfAucCompleted == message->vAuctions.size())
		{
		   message->setAllDone(true);
		}
	}

	void Communication::do_state_action_finished()
	{
	   string command; 
           long robot_ID;
	   long myID;
           int Auc_ID;
	   double time;
           stringstream iss(mStringBuffer);
           iss >> command >> robot_ID >> myID >> time;
	
	   cout << "DEBUG - FINISHED : " << iss.str() << endl;	
	   cout << "Robot " << robot_ID << " has finished all given task in " << time << " seconds" << endl;
	   update_finished();
	   do_state_change(STATE_IDLE);
	}

	void Communication::update_finished()
	{
             if( Mode == COMBINATORIAL_AUCTION_PROXIMITY || Mode == COMBINATORIAL_AUCTION_SORTED ){
                 std::cout << "COMBINATORIAL_COMMUNICATION" << std::endl;
                 return;
             }	    

	     finishedRobots++;
	     if(finishedRobots == numberOfRobots)
	    {
		message->setAllDone(true);
	    }
	}


	void Communication::do_state_action_status()
	{
	    cout << " Current Status is = "<< mStringBuffer << endl;

            do_state_change(STATE_IDLE);
	}


        void Communication::lockUnlock(string st, long id)
	{
	    cout<<"String: " << st<<endl;
	    cout<<"ID: "<< id<<endl;

	    stringstream lockUnlock;
 	    lockUnlock << st << id;
	    write(lockUnlock);
	} 


        void Communication::stopRobotI(int i)
	{
		 stringstream move;
 
 	 	 lockUnlock("LOCK ", robots.at(i).id);
	
	         move << CMD_MOVE << " " << robots.at(i).id << " " << 0 << " " << 0 << " " << 0;
                 cout<<move.str()<<endl;
 	         write(move);
		 move.str(string());
		 move.clear();

		 lockUnlock("UNLOCK ", robots.at(i).id);
        }


        void Communication::stopAllRobots()
	{
	    stringstream move;

            move << CMD_AUCTION_STOP << " -1";
            cout<<move.str()<<endl;
            write(move);
        }


	void Communication::userInput(char cmd)
	{
	   stringstream oss;

           cout << " *************************************** " << endl;
           cout << " USER INPUT : " << cmd << endl;
           cout << " *************************************** " << endl;

	   switch(cmd){
             case 'u':
                oss << CMD_AUCTION_ASKSTATUS << " -1";
                break;
	     case 'r':
		oss << CMD_AUCTION_RESUME << " -1";
	        break;
             case ' ':
		oss << CMD_AUCTION_PAUSE << " -1";
	        break;
             case 's':
		oss << CMD_AUCTION_STOP << " -1";
	        break;
	     case 'p':
                oss << CMD_AUCTION_PRINT << " -1";
                break;
	     case 'q':  //TEMP TEST
		oss << CMD_EXECUTE_ENTITIES << " " << CMD_AUCTION_PAUSE << " -1";
		break;
	     case 'a':  //TEMP TEST
		oss << CMD_EXECUTE_ROBOTS << " " << CMD_AUCTION_PRINT << " -1";
		break;
	     }
 
            write(oss);
            cout << "DEBUG : " << oss.str() << endl;

             //do_state_change(STATE_IDLE);
        }


        int Communication::getNumberOfRobots(){
	    return numberOfRobots;
	}

	
	void Communication::set_ID(string type, string name)
	{
	    mTypeID = type;
	    mNameID = name;
	}


        long Communication::getID(int i)
	{
            return robots.at(i).id;
        }

	
	void Communication::setAucBuffer(string point)
	{
            stringstream msg;
	    message->setNextAuction(false);
            msg << CMD_AUCTION_START << " -1 " << mSessionID << " " << point;    
	    AucBuffer = msg.str();
           
            do_state_change(STATE_AUCTION);
	}


        void Communication::setAucBuffer(string point, long id)
	{
            mAuctionID = id;
            stringstream msg;

            msg << CMD_AUCTION_START << " " << mAuctionID << " " << mSessionID << " " << point;    
	    AucBuffer = msg.str();
           
            do_state_change(STATE_AUCTION);
	}
   
        void Communication::broadCastAllPoints(string target, double time )
        {
           aucInProgress = true;
           stringstream msg;
           msg << CMD_EXECUTE_ROBOTS << " " << CMD_AUCTION_SET << " -1 "  << mSessionID << " " << target << " " << time;

           //Debug
           AucBuffer = msg.str();
           //cout <<"BUFFER [ " << AucBuffer << " ] " << endl;

           write( msg );
           do_state_change( STATE_IDLE );
        }

        void Communication::broadCastWinners( long agent, int bid )
        {
           aucInProgress = false;
           stringstream msg;
           msg << CMD_EXECUTE_ROBOTS << " " << CMD_AUCTION_GO << " " << agent << " " << mSessionID << " " << bid;
           
           //Debug
           AucBuffer = msg.str();
           //cout << "WINNING BUFFER [ " << AucBuffer << " ]" << endl;

           write( msg );
           do_state_change( STATE_IDLE );
        }

        void Communication::printMessageInCentralServer( string str )
        {
           stringstream msg;
           msg << CMD_LOG_MESSAGE << " " << str;
           write( msg );
           do_state_change( STATE_IDLE );
        }

        long Communication::getSessionID()
	{
	    return mSessionID;
        }


        int Communication::getState()
	{
	    return  mCurrentState;
	}


	void Communication::setStartSearch(bool value)
	{
	   startSearch = value;
	}


	bool Communication::getStartSearch()
	{
	   return startSearch;
	}

        
	void Communication::setMessage( Message * m )
	{
	   message = m;
	}
