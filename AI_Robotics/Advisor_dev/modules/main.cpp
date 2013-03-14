#include "VisualDebugger.h"
#include "Advisor.h"
#include "MCPainter.h"
#include <iostream>
#include <fstream>
#include <stdlib.h>
#include <time.h>
#include <unistd.h>
#include <boost/thread.hpp>

typedef unsigned short cmd_port;

using namespace std;

VisualDebugger * vD; 
Map * myMap; 
Advisor * advise;
vector<Point> PointsToAdd;
 
int mainWindow;
int height, width;

void init(void) {
  glClearColor(1.0, 1.0, 1.0, 0.0);
  glShadeModel(GL_FLAT);
}

void reshape(int w, int h){ vD->reshape(w,h); } 
void keyboard(unsigned char key, int x, int y){ vD->keyboard(key,x,y); } 
void keyboardSpecial(int key, int x, int y){ vD->keyboardSpecial(key,x,y); } 
void mouse(int button, int state, int x, int y){ vD->mouse(button, state, x, y); }
void draw(void){ 
  glutSetWindow(mainWindow); 
  vD->draw(); 
  glutPostRedisplay();
}

/*! \brief Displays the usage of the program. 

  Called when the arguments cannot be processed.
 */ 
void displayUsage(int argc, char** argv){
  cout << "USAGE: " << argv[0] << " [ options ]" << endl << endl;
  cout << "Where [ options ] can be: " << endl ;
  cout << "\t-f <file_path>" << endl;
  cout << "\t-m <auction_mode> (0: Random Auction | 1: Simple Auction | 2: Combinatorial Auction 1 | 3: Combinatorial Auction 2" << endl;
  cout << "\t-n <task_number>" << endl;
  cout << "\t-p <points_file_path>" << endl;
  cout << "\t-a <task_allocation> (0: Random Locations | 1: Fixed Locations | 2: Saved Points)" << endl;
  exit(1);
}

void generateRandomPoints(int size){
   int x, y, max_x, max_y;
   max_x = myMap->getLength();
   max_y = myMap->getHeight();

   for(int i=0; i<size ; i++){
   //Make sure that random points are accessible
      do{
         x = rand()% max_x + 0; //add 2 - Don't generate points too close to the wall
         y = rand()% max_y + 0;
      }while(!myMap->isAccessible(x,y,20) && !myMap->isWithinBorders(x,y));

      Point p(x,y);
      PointsToAdd.push_back(p);
   }

   //Save the randomPoints to a text
   ofstream pointsFile;
   pointsFile.open( "config_files/points.conf" );
   for( int i = 0; i<PointsToAdd.size(); i++ ){
      pointsFile << "point " << PointsToAdd.at(i).getX() << " " << PointsToAdd.at(i).getY() << "\n";
   }
   pointsFile.close();
}

void readPointsFromFile(ifstream& pFile){
  int x, y;
  string cmd;
 
  while( !pFile.eof() ) {
    cmd = ""; 
    pFile >> cmd ; 
    // if the line is commented skip it otherwise process.
    if (! (( cmd[0] == '/' && cmd[1] == '/' ) || ( cmd == "")) ){
      if ( cmd == "point" ){
	pFile >> x >> y ;
        cout << "Point to add [ " << x << " , " << y << " ]" << endl;
        Point p(x,y);
        PointsToAdd.push_back(p);
      }
    }
    else{
        cout << "Invalid Data [ " << cmd << " ]" << endl;
    }
  }
  cout << "Total Points : " << PointsToAdd.size() << endl;
}

void generateFixedPoints(int size){
   int max_x = myMap->getLength();
   int max_y = myMap->getHeight();	

   //This is hardCoded
   if(size==2){
      int x = max_x/2;
      int y = max_y/4;

      Point p( x , y*3 );
      Point p2( x , y );

      PointsToAdd.push_back(p);
      PointsToAdd.push_back(p2);
   }
   else if(size==5){
      int x = max_x/4;
      int y = max_y/4;

      Point  p( x*2 , y*3 );
      Point p2( x , y*2 );
      Point p3( x*3 , y*2 );
      Point p4( x , y );
      Point p5( x*3 , y );

      PointsToAdd.push_back(p);
      PointsToAdd.push_back(p2);
      PointsToAdd.push_back(p3);
      PointsToAdd.push_back(p4);
      PointsToAdd.push_back(p5);
   }
   else if(size==8){
      int x = max_x/6;
      int y = max_y/4;

      Point  p( (x*2)-25 , y*3 );
      Point p2( (x*4)+25 , y*3 );
      Point p3( x , y*2 );
      Point p4( x*3 , y*2 );
      Point p5( x*5 , y*2 );
      Point p6( x , y );
      Point p7( x*3 , y );
      Point p8( x*5 , y );

      PointsToAdd.push_back(p);
      PointsToAdd.push_back(p2);
      PointsToAdd.push_back(p3);
      PointsToAdd.push_back(p4);
      PointsToAdd.push_back(p5);
      PointsToAdd.push_back(p6);
      PointsToAdd.push_back(p7);
      PointsToAdd.push_back(p8);
   }
   else if(size==9){
      int x = max_x/6;
      int y = max_y/4;

      Point  p( x, y*3 );
      Point p2( x*3 , y*3 );
      Point p3( x*5 , y*3 );
      Point p4( x , y*2 );
      Point p5( x*3 , y*2 );
      Point p6( x*5 , y*2 );
      Point p7( x , y );
      Point p8( x*3 , y );
      Point p9( x*5 , y );

      PointsToAdd.push_back(p);
      PointsToAdd.push_back(p2);
      PointsToAdd.push_back(p3);
      PointsToAdd.push_back(p4);
      PointsToAdd.push_back(p5);
      PointsToAdd.push_back(p6);
      PointsToAdd.push_back(p7);
      PointsToAdd.push_back(p8);
      PointsToAdd.push_back(p9);
   }
}

void createMap_defaultField() {
  myMap = new Map(500, 400);
  
  // outer walls
  myMap->addWall(MapWall("wall1", 0, 0, 0, 400)); 
  myMap->addWall(MapWall("wall2", 0, 0, 500, 0)); 
  myMap->addWall(MapWall("wall3", 500, 0, 500, 400)); 
  myMap->addWall(MapWall("wall4", 0, 400, 500, 400)); 
}


void readMapFile(ifstream& mFile) {
  string  cmd, label, tmp;
  int x1, y1, x2, y2; 
  bool first = true ;

  while( !mFile.eof() ) {
    cmd = ""; label = ""; x1 = 0 ; y1 = 0 ; x2 = 0 ; y2 = 0 ; 
    mFile >> cmd ; 

    // in case the file is empty create a default map
    if ( mFile.eof() ){
      if ( first ){
	cout << "Empty map file!..." << endl; 
	createMap_defaultField();
      }
    }
    // if the line is commented skip it otherwise process.
    else if (! ( cmd[0] == '/' && cmd[1] == '/' ) ){ 
      // if the first command includes size, set the window for the specified values
      // else create the default map
      if ( first ){
	first = false;
	if ( cmd == "size" ) {
	  mFile >> x1 >> y1 ; 
	  myMap = new Map(x1, y1);
	  width = x1;
	  height = y1;
	  continue;
	}
	else{
	  cout << "Map size not specified!..." << endl; 
	  exit(0);
	}
      }

      // process the command
      if ( cmd == "marker" ){ 
	getline(mFile, tmp); 
      }
      else if ( cmd == "wall" ){
	mFile >> label >> x1 >> y1 >> x2 >> y2 ; 
	myMap->addWall(MapWall(label, x1, y1, x2, y2)); 
      }
      else {
	if ( cmd == "size" )
	  cout << "size command has to be the first command in map config file. command ignored." << endl;
	else
	  cout << "Unknown map config command: " << cmd << endl;
	getline(mFile, tmp); 
      }
    }
    else {
      // ignore the rest of the line.
      getline(mFile, tmp); 
    }
  } 
}


void readConfigFile(ifstream& cfFile, string& csHost, cmd_port& csPort, string& rType, string& rName, int& sRange){
  string cmd, mfile, tmp;

  // parse configuration file + attempt to connect the player and central servers
  while( !cfFile.eof() ) {
    cmd = ""; 
    mfile = "map-demo-15jun11.conf";    
    cfFile >> cmd ; 
    
    // if the line is commented skip it otherwise process.
    if (! (( cmd[0] == '/' && cmd[1] == '/' ) || ( cmd == "")) ){ 
      if ( cmd == "central_server" ) {
	cfFile >> csHost >> csPort ;
      }      
      else if ( cmd == "advisor" ){ 
	cfFile >> rType >> rName; 
      }
      else if ( cmd == "map" ){
	cfFile >> mfile;
	ifstream mapFile( mfile.c_str(), ios::in );

	if ( !mapFile ) {
	  cout << "Unable to open map file " <<  mfile << endl; 
	  createMap_defaultField();
	}
	else {
	  readMapFile(mapFile);
	  mapFile.close();
	}
      }
      else if ( cmd == "ranger" ){
	cfFile >> sRange;
      }
      else {
	cout << "Unknown config command: " << cmd << endl;
	getline(cfFile, tmp); 
      }
    }
    else {
      // ignore the rest of the line.
      getline(cfFile, tmp); 
    }
  } 
}


int main( int argc, char** argv )
{
  try
  {
    srand(time(NULL));
    // usage: <exec> [-f <file_path>] [-m <auction_mode>] [-n <task_number>] [-a <task_allocation>]
    // To add more flags add it to the end of string followed
    // by a : or :: if the flag doesn't require an argument
    const char* optflags = "f:m:n:a:p:"; 
    const char* pointGeneratorFlag = "r:";
    int ch;
    bool pointFlag = false;

    //Default values given to minimize errors if they happen
    cmd_port server_port = 0;// = 6667;
    string server_hostname = "";// = "127.0.0.1";
    const char* filepath = "config_files/auction.conf";
    const char* pointFilePath = "config_files/points.conf";
    ifstream configFile;
    ifstream pointsFile;

    string type = ""; //"coordinator";
    string name = ""; //"advisor";
    int sensorRange = 0;
    int auction_mode = 1;
    int number_of_task = 5; // Points to be search
    int rfl = 0; // Locations are either random or fixed

    Auction_Mode mode = SIMPLE_AUCTION;
    try{
       if ( argc == 1 ) {
	 configFile.open(filepath);
    	 if (!configFile){
             cout << "unable to open file: " << filepath << endl; 
             return 1 ; 
    	 }
         pointsFile.open( pointFilePath );//DUMB OPEN
/*         if (!pointsFile){
            cout << "unable to open points file: " << pointsFile << endl; 
            return 1 ; 
         }
*/	 //We are not checking inputs here
         cout << "  Mode: 0 - Random Allocation\n"
              << "\t1 - Simple Auction\n"
	      << "\t2 - Combinatorial Auction 1\n"
              << "\t3 - Combinatorial Auction 2\n"
	      << "Enter the Mode: ";
         cin >> auction_mode;
         cout << "Number of Task (Options: 2,5,8,9): ";
         cin >> number_of_task;
         cout << "Is it random or fixed locations (0: random | 1: fixed | 2: savedPoints) : ";
         cin >> rfl;
       }
       else if( argc == 3 ){ //Generate Random points for experiments
          configFile.open(filepath);
    	  if (!configFile){
             cout << "unable to open file: " << filepath << endl; 
             return 1 ; 
    	   }
          pointsFile.open( pointFilePath );//DUMB OPEN
/*          if (!pointsFile){
             cout << "unable to open points file: " << pointsFile << endl; 
             return 1 ; 
          }
*/          if( 'r' == (int) getopt( argc, argv, pointGeneratorFlag ) ){
             number_of_task = atoi(optarg);
             pointFlag = true;
             //cout << "Random Points [ " << number_of_task << " ]" << endl;
          }
          else{
             cout << "Couldn't generate random points" << endl;
             displayUsage(argc, argv);
             return 1;
          }
       }
       else {
         while ( -1 != ( ch = getopt( argc, argv, optflags ))){
           switch ( ch ) {
	     case 'f': {
	       cout << "configuration file: " << optarg << endl;
	       configFile.open(optarg, ios::in); 
	     if( !configFile ) {
	       cout << "Can't open configuration file: " << optarg << ". Aborted" << endl; 
	       exit(1);
	     }
	     break;
           }
           case 'm': 
	     auction_mode = atoi(optarg);
	     break;
           case 'n':
	     number_of_task = atoi(optarg);
	     break;
           case 'a':
	     rfl = atoi(optarg);
	     break;
           case 'p':
	     pointsFile.open(optarg, ios::in);
             if (!pointsFile){
                cout << "unable to open points file: " << pointsFile << endl; 
                exit(1) ; 
             }
	     break;
           default:
	     displayUsage(argc, argv);
	     break;
           }
         }
       }
   }catch(std::exception &e){
      cout << "Input Error!" << endl;
      displayUsage(argc, argv);
      exit(1);
   }
    
    if( !pointFlag ){
       //Check all inputs
       if(auction_mode == 0)
          mode = RANDOM_ALLOCATION;  
       else if(auction_mode == 1)
          mode = SIMPLE_AUCTION;
       else if(auction_mode == 2)
          mode = COMBINATORIAL_AUCTION_PROXIMITY;
       else if(auction_mode == 3)
          mode = COMBINATORIAL_AUCTION_SORTED;
       else{
          cout << " Unknown Mode " << endl;
          mode = SIMPLE_AUCTION;
       }
       if(number_of_task != 2 && number_of_task != 5 && number_of_task != 8 && number_of_task !=9){
   	  cout << "Invalid value! Setting value to 5" << endl;
   	  number_of_task = 5;
       }
    }
    readConfigFile(configFile, server_hostname, server_port, type, name, sensorRange);
    configFile.close();

    if( rfl == 0 ){//Random Points
	generateRandomPoints(number_of_task);
    }
    else if(rfl == 1){//Fix Points
        generateFixedPoints(number_of_task);
    }
    else{//Read Points from File
        readPointsFromFile(pointsFile);
    }
    pointsFile.close();

    Communication * connection = new Communication(mode);
    Message * msg = new Message();
    advise = new Advisor(connection, msg, type, name, server_hostname, server_port, mode);
    vD = new VisualDebugger(connection, msg, myMap, sensorRange);

    //Add Points to the Visual Dibbuger
    for(int i=0; i < (int)PointsToAdd.size(); i++){
	vD->addPoint(PointsToAdd.at(i));
    }

    advise->run();

    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB);
    glutInit(&argc, argv);      
    
    //glutInitWindowSize(glutGet(GLUT_SCREEN_HEIGHT),glutGet(GLUT_SCREEN_HEIGHT));
    glutInitWindowSize(400, 400);
    glutInitWindowPosition(0,0);
    mainWindow = glutCreateWindow(argv[0]);
    init();
    glutReshapeFunc(reshape);
    glutMouseFunc(mouse);
    glutKeyboardFunc(keyboard);
    glutSpecialFunc(keyboardSpecial); 
    glutDisplayFunc(draw);
    glutMainLoop();  

    advise->join();

    delete msg;
    delete connection;
    delete advise;
    delete vD; 
    delete myMap; 

  }catch (runtime_error &e)  {
    std::cerr << "Client Exception 1: " << e.what() << "\n";
  }
  catch (std::exception &e)  {
      std::cerr << "Client Exception 2: " << e.what() << "\n";
  }
  
  return 0;
}
