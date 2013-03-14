#ifndef _COMMUNICATION_MANAGER_H
#define _COMMUNICATION_MANAGER_H

#include <iostream>
#include <stdint.h>
#include <stdlib.h>
#include <boost/thread.hpp>
#include <boost/asio.hpp>
#include "definitions.h"
#include "Task.h"
#include "Agent.h"

using namespace metrobotics;
using namespace std;
//using namespace boost::system;
//using namespace boost::asio;

class CommunicationManager
{
 public:
  CommunicationManager(string name, string type);
  ~CommunicationManager();
  
  // Data and function for outsiders.
  void SetIsAuctioning(const bool);
  void SetAgents(vector<Agent>& agents);
  void SetTasks(vector<Task>&  commTasks);
  void SetMessage(const string& message, long targetAgent = -1 );
  //int  GetAgentsCount() const { return commAgentsNumber; }

  // State management.
  int  GetState() const { return commCurrentState; }
  bool IsRegistered() const { return commSessionID >= 0; }
//  bool IsPerformingTask() const { return commPerformingTasks; }    //Should be inside Auction Class
  bool IsAttentionRequired() const { return commAttentionRequired; }

  // Connect to the Central Server.
  bool Connect(const std::string& hostname, unsigned short port);
  void Disconnect();
  bool isCommAlive() const;
		
  // Thread management.
  void Start();// {  commThread = boost::thread(&Communication::Update, this); }
  void Join();// { commThread.join(); }

/*FUNCTIONS CAN BE REPLACED FOR Message To Process Method
  void StopAgent(const long);
  void PauseAgent(const long);
  void ResumeAgent(const long);
  void UpdateAgentStatus(const long);
  void StopAgents();
  void PauseAgents();
  void ResumeAgents();
  void UpdateAgentsStatus();
*/


 private:
  // Boost ASIO (for sockets).
  boost::asio::io_service commIOService;
  boost::asio::ip::tcp::socket commSocket;

  // Boost THREAD (for Running independent).
  boost::thread commThread;

  // Properties to connect with the Central Server.
  std::string mTypeID;
  std::string mNameID;

  // State properties.
  int  commCurrentState;
  long commSessionID;
//  bool commPerformingTasks;  //Should be inside Auction Class
  bool commAttentionRequired;

  // Internal timers.
  static const double MAX_TIME_SILENCE = 60.0;
  static const double MAX_TIME_STATE   = 10.0;
  metrobotics::PosixTimer commSilenceTimer;
  metrobotics::PosixTimer commStateTimer;

  // Internal buffers for Communication.
  std::string communicationBuffer;

  // Data for outsiders.
  //int commAgentsNumber;
  vector<Agent>& commAgents;
  vector<Task>&  commTasks;
  bool commInAuction;

  // Heart beat of this Communication
  // Updates and maintains the internal state machine.
  void Update();

  // Internal functions for Communication.
  void init_state();
  bool msg_waiting() const;
  bool read(std::stringstream& ss);
  bool write(const std::stringstream& ss);
  void lock(const long);
  void unlock(const long);
  void update_robots_info();

  // State actions.
  void state_change(int state);
  void state_action_init();
  void state_action_ack();
  void state_action_idle();
  void state_action_ping_send();
  void state_action_pong_read();
  void state_action_pong_send();
  void state_action_cmd_proc();
  void state_action_robots_info_proc();//Do not update if Auction is in Progress
  void state_action_attention_required();

  // State for Auctions.
  void state_auction_bid();
  void state_auction_complete();
  void state_auction_finished();
  void state_auction_status();
};
