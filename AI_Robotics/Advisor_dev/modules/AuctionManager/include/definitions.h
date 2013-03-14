//Definitions Auctioneer

#ifndef _DEFINITIONS_H
#define _DEFINITIONS_H

// Command Preamble Type
typedef unsigned char cmd_len_t;


// Client/Server Commands
#define CMD_ERROR	   "ERROR"       // <--> ERROR <message>
#define CMD_INIT	   "INIT"        // ---> INIT <type> <name> <num-provides> [<provides-list>]
#define CMD_ACK		   "ACK"         // <--- ACK <id>
#define CMD_PING	   "PING"        // <--> PING
#define CMD_PONG	   "PONG"        // <--> PONG
#define CMD_WAIT	   "WAIT"        // <--> WAIT (maybe not used -- use ERROR instead?)
#define CMD_QUIT       "QUIT"        // <--> QUIT
#define CMD_MOVE       "MOVE"        // <--- MOVE <id> <x-vel> <y-vel> <a-vel>
#define CMD_MOVING     "MOVING"      // ---> MOVING
#define CMD_FOUND      "BROADCAST"   // ---> BROADCAST FOUND <color>
#define CMD_STATE      "STATE"       // <--- STATE <id> (currently replaced by ASKPOSE?)
#define CMD_ASKPOSE    "ASKPOSE"     // <--- ASKPOSE <id>
#define CMD_POSE       "POSE"        // ---> POSE <x-pos> <y-pos> <a-pos> <confidence>
#define CMD_ASKPLAYER  "ASKPLAYER"   // <--- ASKPLAYER <id>
#define CMD_PLAYER     "PLAYER"      // ---> PLAYER <port> <ip>
#define CMD_LOCK       "LOCK"        // <--- LOCK
#define CMD_UNLOCK     "UNLOCK"      // <--- UNLOCK
#define CMD_SNAP       "SNAP"        // <--- SNAP <id> (request an image)
#define CMD_IMAGE      "IMAGE"       // ---> IMAGE <image-data>
#define CMD_IDENT      "IDENT"       // IDENT <num-robots> [<robot_id> <name> <type> <num-provides> <provides>]
#define CMD_GOTO       "GOTO"        // GOTO Map-X Map-Y

#define CMD_AUCTION_START		"AUCTION_START" // AUCTION_START <auc_id><type of task> <x-coord> <y-coord> <sensor> 
#define CMD_AUCTION_WON  		"AUCTION_WON" // AUCTION_WON <auc_id> <robot_id>
#define CMD_AUCTION_BID  		"AUCTION_BID" // AUCTION_BID <auc_id> <bid>   Replys the cost of the bid
#define CMD_AUCTION_ASKBID     		"AUCTION_ASKBID"	// ASKBID <id> Gets all the bids skygrid has
#define CMD_AUCTION_COMPLETE   		"AUCTION_COMPLETE"	// COMPLETE <id> <Auc_id> Robot has succesfully complete a task  
#define CMD_AUCTION_FINISHED     	"AUCTION_FINISHED"  //Post to GUI That the entire area has be searched  
#define CMD_AUCTION_SEARCH   		"AUCTION_SEARCH" // gives the robots the ok to beginning search points. 
#define CMD_AUCTION_ASKSTATUS  		"AUCTION_ASKSTATUS"  //Return searching if the robot is still alive or dead otherwise
#define CMD_AUCTION_STATUS		"AUCTION_STATUS"  //Same as robot_finished 
#define CMD_AUCTION_RESUME		"AUCTION_RESUME"  //Resume all work
#define CMD_AUCTION_PAUSE		"AUCTION_PAUSE"   //Pause all work
#define CMD_AUCTION_STOP 		"AUCTION_STOP"    //Stop all work
#define CMD_AUCTION_PRINT               "AUCTION_PRINT"   //Print the list of points to be searched
#define CMD_AUCTION_SET			"AUCTION_SET"	// AUCTION_SET <auc_id><type of task> <x-coord> <y-coord> <sensor> //Robots keep this value
#define CMD_AUCTION_GO			"AUCTION_GO"    //AUCTION_GO <robot_id> <auc_id>

//Generic broadcast
#define CMD_EXECUTE_ENTITIES            "EXECUTE_ENTITIES"  //Broadcast to all entities including non robots
#define CMD_EXECUTE_ROBOTS		"EXECUTE_ROBOTS"    //Boradcast only to robots    
#define CMD_LOG_MESSAGE			"LOG_MESSAGE"  //Keep messages in the Log file 
//format CMD_EXECUTE_ROBOTS CMD_...(ANYCOMMAND) -1 ..... (ANY MESSAGE)  //-1 broadcast, or id to communicate with that robot

// Client/Server States
#define STATE_INIT           0
#define STATE_ACK            1
#define STATE_IDLE           2
#define STATE_CMD_PROC       3
#define STATE_AUCTION       4
#define STATE_BID      5
#define STATE_COMPLETE      6
#define STATE_WON      7
#define STATE_ROBOT_ID      8
#define STATE_QUIT           9
#define STATE_COLLECT_BID  12
#define STATE_ASK_BID      10
#define STATE_FINISH    11
#define STATE_STATUS 13

//BROADCAST
#define STATE_EXECUTE_ENTITIES		14
#define STATE_EXECUTE_ROBOTS		15

// Unique Client Names
#define UID_SURVEYOR_SRV10    "srv10"
#define UID_AIBO_GROWL        "growl"
#define UID_AIBO_BETSY        "betsy"
#define UID_GUI_PABLO         "pablo"


// Client Species Types
#define SID_AIBO         "aibo"
#define SID_SURVEYOR     "surveyor"
#define SID_SCRIBBLER    "scribbler"
#define SID_NXT          "nxt"
#define SID_GUI          "gui"
#define SID_COORDINATOR  "coordinator"


// Client Capabilities (Provides)
#define CAPS_POSITION2D  "position2d"
#define CAPS_CAMERA      "camera"
#define CAPS_METROCAM    "metrocam"
#define CAPS_BLOBFINDER  "blobfinder"
#define CAPS_RANGER      "ranger"
#define CAPS_SONAR       "sonar"

// Controller States
#define CTRL_HALT 0
#define CTRL_READY 1

//Distribution of Auction Mode
enum Auction_Mode{ RANDOM_ALLOCATION, SIMPLE_AUCTION, COMBINATORIAL_AUCTION_PROXIMITY, COMBINATORIAL_AUCTION_SORTED };

//Task type
enum TASK_TYPE{};


#define KEY_CTRL_STEP 0          // sets the keyboard control commands the robot to move in discrete steps 
#define KEY_CTRL_CONT 1          // sets the keyboard control commands the robot to move in continuous mode

#endif
