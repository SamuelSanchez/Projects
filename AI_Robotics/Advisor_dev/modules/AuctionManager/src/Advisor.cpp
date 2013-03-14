#include "Advisor.h" 

     //TODO: Communication.h/cpp must changed. States must do only their function and should be more clear.
     //      Connection to Central Server should happened here, not only setting up variables.
     Advisor::Advisor(Communication * comm, Message * msg, string type, string name, string ip, cmd_port port, Auction_Mode m)
              : connection(comm), message(msg), mode(m), notifyAuction(false)
     {
         try{
	     //Connecting to Skygrid
	     if (!connection->Connect(ip, port)) {
	         cerr << "Failed to establish a connection to the Central Server.\n"
	 	       << "Central Server hostname: " << ip << "\n"
		       << "Central Server port: " << port << endl;
	         exit(1);
	     }

	     connection->set_ID( type, name ); //("gui","Auctioneer");
	     connection->init_state();

             //Create the target pool that will be shared among Communication and Auctioneer
             targets = new TargetPool();

	     connection->setMessage( message );
             string modeStr;

             switch(mode){
              case RANDOM_ALLOCATION:
                taskAuctioneer = new RandomAllocation();
                modeStr = "RANDOM_ALLOCATION";
                break;
              case SIMPLE_AUCTION:
	        taskAuctioneer = new SimpleAuction();
                modeStr = "SIMPLE_AUCTION";
	        break;
              case COMBINATORIAL_AUCTION_PROXIMITY:
                taskAuctioneer = new CombinatorialAuctionProximity();
                modeStr = "COMBINATORIAL_AUCTION_PROXIMITY";
                targets->setMode( COMBINATORIAL_AUCTION_PROXIMITY );
                break;
              case COMBINATORIAL_AUCTION_SORTED:
                taskAuctioneer = new CombinatorialAuctionProximity();
                modeStr = "COMBINATORIAL_AUCTION_SORTED";
                targets->setMode( COMBINATORIAL_AUCTION_SORTED );
                break;
             }
             connection->setTargetPool( targets );
             taskAuctioneer->setTargetPool( targets );
	     taskAuctioneer->setMessage( message );         //I don't think I need message anymore for Combinatorial
	     taskAuctioneer->setCommunication( connection );

             init_message << "AUCTIONEER_STARTED" << " MODE " << modeStr; //targets.size()
         }
	 catch (std::exception &e)  {
             std::cerr << "\tException : " << e.what() << "\n";
             std::exit(1);
  	 }
     }


     void Advisor::setMode(Auction_Mode m)
     {
	/*if( Message->AllTaskCompleted() ){
	   delete taskAuctioneer;

           switch(mode){
	    case COMBINATORIAL_AUCTION:
              taskAuctioneer = new CombinatorialAuction();
              break;
            case SIMPLE_AUCTION:
	      taskAuctioneer = new SimpleAuction();
	      break;
            case RANDOM_ALLOCATION:
              taskAuctioneer = new RandomAllocation();
              break;
           }
	
	   taskAuctioneer->setMessage( message );
	   taskAuctioneer->setCommunication( connection );

        */
	 mode = m;
     }
/*    
     void Advisor::auctionPoints(Graph* g)
     {
        simple_Auction->auctionPoints(g);
     }
*/ 
     void Advisor::processCommands()
     {
        try{
            if( mode == COMBINATORIAL_AUCTION_PROXIMITY || mode == COMBINATORIAL_AUCTION_SORTED )
                message->transferPoints( targets );

	    while( !message->getProgramQuit() ) // || connection->getState()==STATE_QUIT)
	    {
	       updateAdvisor();

               //Give time to the robots to complete previous operations.
               usleep(100000);
	    }
        }catch (exception& e){
            std::cout << e.what() << "\n" << std::endl;
            connection->printMessageInCentralServer( "AUCTIONEER_FINISHED UNSUCCESSFUL" );
        }    
	std::exit(0); //Exit anyway after experiment fails or finishes successfuly. This is to test experiments.
     }

    void Advisor::updateAdvisor()
    {
        connection->Update();

	//Don't update anything if there are not robots available
	if(connection->getNumberOfRobots() < 1)
	   return;

        if( !notifyAuction ){
           init_message << " POINTS " << (int)message->vAuctions.size();
           connection->printMessageInCentralServer( init_message.str() );
           notifyAuction = true;
        }

	taskAuctioneer->Update();
    }
