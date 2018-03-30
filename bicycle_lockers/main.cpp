#include <iostream>
#include <cstdlib>
#include <cstring>
#include <stdio.h>
#include <arpa/inet.h>
#include <sstream>
#include <string.h>
#include <sys/epoll.h>
#include "udp_socket.h"
#include "locker_packets.h"

#define WAIT_TRANSACTION 3
#define WAIT_SERVERCONNECT 0
#define WAIT_LIFESIGNAL 5 

const char *port = "37777";
int capacity;
char **bicycles;
int coordinatorPort, initialPort;
int backupCoordinator;
UDPSocket lockerSocket;


void serverConnect();
bool receivePacket(int packetID, int wait);

bool sendLifeCheckPacket() {
  bool alive;
  try {
    std::stringstream ss;
    struct sockaddr_in dest;
    dest.sin_addr.s_addr = inet_addr("127.0.0.1");
    dest.sin_family = AF_INET;
    dest.sin_port = htons(coordinatorPort);
    short packetID = rand() % 8999 + 1000;
    lifeCheckPacket lcp;
    lcp.packetID = htons(packetID);
    std::vector<unsigned char> data(sizeof(lcp));
    std::memcpy(data.data(), &lcp, sizeof(lcp));
    std::cout << "Sending life check to : " << lockerSocket.port( ) << std::endl;
    int numWrite = lockerSocket.write(data, dest);
    std::cout << "Wrote " << numWrite << " bytes" << std::endl;
    alive = receivePacket(packetID, WAIT_LIFESIGNAL);
  } catch ( std::exception& ex ) {
    std::cout << "Failed" << std::endl;
  }

  return alive;
}

bool handlePacket(receivedPacket rp, int packetID, sockaddr_in src) {

  switch(rp.flag) {
    case 1: //life response
      if(packetID == rp.packetID) {
        std::cout << "Coordinator still alive, staying connected." << std::endl;
        return true;
      } else {
        std::cout << "Coordinator not found." << std::endl;
        return false;
      }

    case 4: //connection accepted
      if(packetID == rp.packetID) {
        std::cout << "Connection to coordinator server accepted!" << std::endl;
        return true;
      } else {
        std::cout << "Connection to coordinator server failed, retrying..." << std::endl;
        return false;
      }

    case 5: //connection rejected
      if(packetID == rp.packetID) {
        std::cout << "Connection to coordinator server rejected." << std::endl;
        return true;
      } else {
        std::cout << "Connection to coordinator server failed, retrying..." << std::endl;
        return false;
      }

    case 6: //check/replace connection
      if(sendLifeCheckPacket()) {
        return true;
      } else {
        coordinatorPort = ntohs(src.sin_port); 
        serverConnect();
        return false;
      }


    case 7: //transaction accepted
      if(packetID == rp.packetID) {
        std::cout << "Transaction confirmed!" << std::endl;
        return true;
      } else {
        std::cout << "Failed to confirm transaction, please retry." << std::endl;
        return false;
      }

    case 8:
      coordinatorPort = initialPort;
      serverConnect();
      return true;

    default:
      std::cout << "Unrecognised message from coordinator server. Packet type: " << rp.flag << std::endl;
      break;
  }
}

bool receivePacket(int packetID, int wait) {
  try {
    std::cout << "Running: " << lockerSocket.port( ) << std::endl;
    struct sockaddr_in src;
    std::vector< unsigned char > data(32);
    // Note: Currently blocking
    int numRead = lockerSocket.read( data, src, wait);
    std::cout << "Read " << numRead << " bytes" << std::endl;
    std::string dataStr( data.begin( ), data.begin( ) + numRead );
    std::cout << "Message: " << dataStr << std::endl;
    receivedPacket *rp;
    rp = (receivedPacket*) dataStr.c_str();
    rp->flag = ntohs(rp->flag);
    rp->packetID = ntohs(rp->packetID);
    std::cout << rp->flag << "  " << rp->packetID << std::endl;
    return handlePacket(*rp, packetID, src);
    } catch ( std::exception& ex ) {
    std::cout << "Failed" << std::endl;
    return false;
  }

}

bool sendPacket(char type, char* bicycleID, int userID) {
  bool received = false;
    try {
    std::stringstream ss;
    struct sockaddr_in dest;
    dest.sin_addr.s_addr = inet_addr("127.0.0.1");
    dest.sin_family = AF_INET;
    dest.sin_port = htons(coordinatorPort);
    short packetID = rand() % 8999 + 1000;
    transactionPacket tp;
    tp.packetID = htons(packetID);
    tp.type = (unsigned) type;
    strcpy((char*)tp.bicycleID, bicycleID);
    tp.userID = htonl(userID);
    std::cout << ntohs(tp.packetID) << " " << tp.type << " " << tp.bicycleID << " " << ntohl(tp.userID) << std::endl;
    std::cout << tp.packetID << " " << tp.type << " " << tp.bicycleID << " " << tp.userID << std::endl;
    
    std::vector<unsigned char> data(sizeof(tp));
    std::memcpy(data.data(), &tp, sizeof(tp));
    ss << packetID << " " << " " << bicycleID;
    std::cout << "Sending To: " << lockerSocket.port( ) << std::endl;

    int numWrite = lockerSocket.write(data, dest);
    std::cout << "Wrote " << numWrite << " bytes" << std::endl;
    received = receivePacket(packetID, WAIT_TRANSACTION);
    } catch ( std::exception& ex ) {
      std::cout << "Failed" << std::endl;
    }
  return received;
}

void serverConnect() {
  bool received = false;
  while (!received) {
    try {
      std::cout << "Trying connection to server..." << std::endl;
      struct sockaddr_in dest;
      dest.sin_port = htons(coordinatorPort);
      dest.sin_addr.s_addr = inet_addr("127.0.0.1");
      dest.sin_family = AF_INET;
      short packetID = rand() % 8999 + 1000;
      std::cout << packetID << std::endl;
      connectionPacket cp;
      cp.packetID = htons(packetID);
      std::cout << cp.flag << " " << cp.packetID << std::endl;
      std::vector<unsigned char> data(sizeof(cp));
      std::memcpy(data.data(), &cp, sizeof(cp));
      std::cout << "Sending To: " << lockerSocket.port( ) << std::endl;
      int numWrite = lockerSocket.write(data, dest);
      std::cout << "Wrote " << numWrite << " bytes" << std::endl;
      received = receivePacket(packetID, WAIT_SERVERCONNECT);
      } catch ( std::exception& ex ) {
        std::cout << "Failed" << std::endl;
      }
  }
}


void printBicycles() {
  for(int i=0; i<capacity; i++) {
    std::cout << "Locker " << i << ": " << bicycles[i] << std::endl;
  }
}

void removeBicycle(int locker, int userID) {
  char *bicycleID;
  std::cout << "Locker: " << locker << " Capactiy: " << capacity << std::endl;
  if(locker >= capacity || locker < 0 || strcmp(bicycles[locker], "Empty") == 0) {
    std::cout << "Invalid locker number, couldn't remove bicycle." << std::endl;
    return;
  }
  if (sendPacket('0', bicycleID, userID)) {
    std::cout << "Bicycle " << bicycleID << " removed from locker " << locker << " by user: " << userID << std::endl;
    bicycleID = bicycles[locker];
    std::string empty = "Empty";
    bicycles[locker] = (char*) empty.c_str();
  } else {
    std::cout << "No response from coordinator server. Transaction couldn't be processed." << std::endl;
  }
  return;
}

void addBicycle(int locker, std::string bicycleID, int userID) {
  if(locker >= capacity || locker < 0 || strcmp(bicycles[locker], "Empty") != 0 ) {
    std::cout << "Invalid locker number, couldn't add bicycle." << std::endl;
    return;
  }
  char *bicycleIDcstr = (char*) bicycleID.c_str();
  if(sendPacket('1', bicycleIDcstr, userID)) {
    bicycles[locker] = bicycleIDcstr;
    std::cout << "Bicycle " << bicycleID << " added to locker " << locker << " by user: " << userID << std::endl;
  } else {
    std::cout << "No response from coordinator server. Transaction couldn't be processed." << std::endl;
  }
  return;
}

void handleStdin() {

  int locker;
  std::string bicycleID, input;
  int userID;
  int p;

  std::cin >> input;
  sscanf(input.c_str(), "%d", &p);

  switch (p) {
    case 3:
      std::cout << "Terminating Locker Set" << std::endl;
      std::cout << "---------------------------------------------" << std::endl;
      for (int i=0; i<capacity; i++) {
        std::cout << "Freeing " << i << std::endl;
        free(bicycles[i]);
      }
      free(bicycles);
      exit(EXIT_SUCCESS);

    case 1:
      std::cin >> input;
      sscanf(input.c_str(), "%d", &locker);
      std::cin >> bicycleID;
      std::cin >> input;
      sscanf(input.c_str(), "%d", &userID); 
      addBicycle(locker, bicycleID, userID);
      break;

    case 2:
      std::cin >> input;
      sscanf(input.c_str(), "%d", &locker);
      std::cin >> input;
      sscanf(input.c_str(), "%d", &userID);
      removeBicycle(locker, userID);
      break;

    default:
      std::cout << "Error: Incorrect Format." << std::endl;
      break;
  }
}


int main( int argc, char **argv ) {

  std::cout << "---------------------------------------------" << std::endl;
  std::cout << "------------OV Bicycle Locker Set------------" << std::endl;
  std::cout << "---------------------------------------------" << std::endl;
  std::cout << "Initialising Locker Set Node..." << std::endl;

  if(argc < 3) { 
    std::cout << "Initialisation Error: Too few arguments" << std::endl; 
    return 0;
  }

  int epollfd = epoll_create(2);
  if(epollfd < 0) {
    std::cout << "Error in epoll_create()." << std::endl; 
    return 0;
  }

  std::string input;
  port = argv[1];

  input = argv[2];
  sscanf(input.c_str(), "%d", &coordinatorPort);
  initialPort = coordinatorPort;

  lockerSocket = UDPSocket(port);
  struct epoll_event evSock, evIn, events[2];
  evSock.events = EPOLLIN;
  evSock.data.fd = lockerSocket.fd();
  if (epoll_ctl(epollfd, EPOLL_CTL_ADD, lockerSocket.fd(), &evSock) != 0) {
    std::cout << "Error in epoll_ctl(), while adding socket." << std::endl; 
    return 0;
  }

  evIn.events = EPOLLIN;
  evIn.data.fd = 0;
  if (epoll_ctl(epollfd, EPOLL_CTL_ADD, 0, &evIn) != 0) {
    std::cout << "Error in epoll_ctl(), while adding stdin." << std::endl; 
    return 0;
  } 

  input = argv[3];
  sscanf(input.c_str(), "%d", &capacity);

  bicycles = (char**) malloc(capacity*sizeof(char*));
  
  for (int i=0; i<capacity; i++) {
    bicycles[i] = (char*) malloc(10*sizeof(char));
  }

  srand(time(NULL));

  if(argc < capacity+4) { 
    std::cout << "Initialisation Error: Too few arguments" << std::endl; 
    return 0;
  }

  if(argc > capacity+4) { std::cout << "Initialisation Warning: Too many arguments, only first " << capacity+2 << " arguments used." << std::endl; }

  std::cout << "Locker initialised with " << capacity << " lockers." << std::endl;
  std::cout << "Setting up lockers with bicycle ids:" << std::endl;
  
  for(int i=0; i<capacity; i++) {
    bicycles[i] = argv[i+4];
    std::cout << "Bicycle with id: " << bicycles[i] << " added to locker " << i << std::endl;
  }

  std::cout << "Initialising connection to specified coordinator server..." << std::endl;
  serverConnect();
  std::cout << "Connection success!" << std::endl;
  
  std::cout << "---------------------------------------------" << std::endl;
  std::cout << "-------------Locker Initialised--------------" << std::endl;

  

  while(1) {    
    std::cout << "---------------------------------------------" << std::endl;
    printBicycles();
    std::cout << "---------------------------------------------" << std::endl;
    std::cout << "Add Bicycle: 1 lockerNumber bicycleID userID" << std::endl;
    std::cout << "Remove Bicycle: 2 lockerNumber userID" << std::endl;
    std::cout << "Terminate Locker Set: 3" << std::endl;
    std::cout << "---------------------------------------------" << std::endl;

    std::cout << "Epoll waiting..." << std::endl;
    int evnts = epoll_wait(epollfd, events, 2, -1);
    std::cout << "Ready on fd: " << events[0].data.fd << std::endl;

    for (int i=0; i<evnts; i++) {
      if (events[i].data.fd == 0) {
        handleStdin();
      } else {
        receivePacket(0, 1);
      }
    }

  }

}