#include <iostream>
#include <cstdlib>
#include <cstring>
#include <stdio.h>
#include <arpa/inet.h>
#include <sstream>
#include <string.h>
#include "udp_socket.h"
#include "locker_packets.h"

int capacity;
int *bicycles;
int port;
int coordinatorPort = 37777;
UDPSocket lockerSocket;

bool receivePacket(std::string type, int packetID) {
  try {
    std::cout << "Running: " << lockerSocket.port( ) << std::endl;
    struct sockaddr src;
    std::vector< unsigned char > data(32);
    // Note: Currently blocking
    int numRead = lockerSocket.read( data, src );
    std::cout << "Read " << numRead << " bytes" << std::endl;
    std::string dataStr( data.begin( ), data.begin( ) + numRead );
    std::cout << "Message: " << dataStr << std::endl;
    int receivedID;
    receivedID = (data[0] << 8) | data[1];
    std::cout << "Received: " << receivedID << " ID: " << packetID << std::endl;
    if(ntohs(receivedID)==packetID) {
      std::cout << type << " confirmed!" << std::endl;
      return true;
    } else {
      std::cout << type << " failed, retrying." << std::endl;
      return false;
    }
  } catch ( std::exception& ex ) {
    std::cout << "Failed" << std::endl;
    return false;
  }

}

void sendPacket(char type, int bicycleID, int userID) {
  bool received = false;
  while(!received) {
    try {
      std::stringstream ss;
      struct sockaddr_in dest;
      dest.sin_port = htons(coordinatorPort);
      short packetID = rand() % 8999 + 1000;
      transactionPacket tp;
      tp.packetID = htons(packetID);
      tp.type = (unsigned) type;
      ss << bicycleID;
      strcpy((char*)tp.bicycleID, ss.str().c_str());
      tp.userID = htonl(userID);
      std::cout << ntohs(tp.packetID) << " " << tp.type << " " << tp.bicycleID << " " << ntohl(tp.userID) << std::endl;
      std::cout << tp.packetID << " " << tp.type << " " << tp.bicycleID << " " << tp.userID << std::endl;
      
      std::vector<unsigned char> data(sizeof(tp));
      std::memcpy(data.data(), &tp, sizeof(tp));
      ss << packetID << " " << " " << bicycleID;
      std::cout << "Sending To: " << lockerSocket.port( ) << std::endl;
      int numWrite = lockerSocket.write(data, dest);
      std::cout << "Wrote " << numWrite << " bytes" << std::endl;
      std::string type = "Transaction";
      received = receivePacket(type, packetID);
    } catch ( std::exception& ex ) {
      std::cout << "Failed" << std::endl;
    }
  }
  return;
}

void serverConnect() {
  bool received = false;
  while (!received) {
    try {
      std::cout << "Trying connection to server..." << std::endl;
      struct sockaddr_in dest;
      dest.sin_port = htons(coordinatorPort);
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
      std::string type = "Connection";
      received = receivePacket(type, packetID);
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
  int bicycleID;
  if(locker >= capacity || locker < 0 || bicycles[locker] < 1) {
    std::cout << "Invalid locker number, couldn't remove bicycle." << std::endl;
    return;
  }
  bicycleID = bicycles[locker];
  bicycles[locker] = 0;
  sendPacket('0', bicycleID, userID);
  std::cout << "Bicycle " << bicycleID << " removed from locker " << locker << "by user: " << userID << std::endl;
}

void addBicycle(int locker, int bicycleID, int userID) {
  if(locker >= capacity || locker < 0 || bicycles[locker] != 0) {
    std::cout << "Invalid locker number, couldn't add bicycle." << std::endl;
    return;
  }
  bicycles[locker] = bicycleID;
  sendPacket('1', bicycleID, userID);
  std::cout << "Bicycle " << bicycleID << " added to locker " << locker << "by user: " << userID << std::endl;
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

  char *portno = argv[1];
  lockerSocket = UDPSocket(portno);
  std::string input;
  input = argv[2];
  sscanf(input.c_str(), "%d", &capacity);
  bicycles = (int*) malloc(capacity*sizeof(int));
  srand(time(NULL));

  if(argc < capacity+3) { 
    std::cout << "Initialisation Error: Too few arguments" << std::endl; 
    return 0;
  }

  if(argc > capacity+3) { std::cout << "Initialisation Warning: Too many arguments, only first " << capacity+2 << " arguments used." << std::endl; }

  std::cout << "Locker initialised with " << capacity << " lockers." << std::endl;
  std::cout << "Setting up lockers with bicycle ids:" << std::endl;
  
  for(int i=0; i<capacity; i++) {
    input = argv[i+3];
    sscanf(input.c_str(), "%d", &bicycles[i]);
    std::cout << "Bicycle with id: " << bicycles[i] << " added to locker " << i << std::endl;
  }

  std::cout << "Initiasalising connection to specified coordinator server..." << std::endl;
  serverConnect();
  std::cout << "Connection success!" << std::endl;
  
  std::cout << "---------------------------------------------" << std::endl;
  std::cout << "-------------Locker Initialised--------------" << std::endl;

  int locker;
  int bicycleID;
  int userID;
  int p;

  while(1) {    
    std::cout << "---------------------------------------------" << std::endl;
    printBicycles();
    std::cout << "---------------------------------------------" << std::endl;
    std::cout << "Add Bicycle: 1 lockerNumber bicycleID userID" << std::endl;
    std::cout << "Remove Bicycle: 2 lockerNumber userID" << std::endl;
    std::cout << "Terminate Locker Set: 3" << std::endl;
    std::cout << "---------------------------------------------" << std::endl;
    std::cin >> input;
    sscanf(input.c_str(), "%d", &p);

    switch (p) {
      case 3:
        std::cout << "Terminating Locker Set" << std::endl;
        std::cout << "---------------------------------------------" << std::endl;
        return 1;

      case 1:
        std::cin >> input;
        sscanf(input.c_str(), "%d", &locker);
        std::cin >> input;
        sscanf(input.c_str(), "%d", &bicycleID);  
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


  try {
    UDPSocket socket;
    std::cout << "Running: " << socket.port( ) << std::endl;
    struct sockaddr src;
    std::vector< unsigned char > data( 100 );
    // Note: Currently blocking
    int numRead = socket.read( data, src );
    std::cout << "Read " << numRead << " bytes" << std::endl;
    std::string dataStr( data.begin( ), data.begin( ) + numRead );
    std::cout << "Message: " << dataStr << std::endl;
  } catch ( std::exception& ex ) {
    std::cout << "Failed" << std::endl;
  }
}