#include <iostream>
#include <cstdlib>
#include <stdio.h>
#include <sstream>
#include "udp_socket.h"

int capacity;
int *bicycles;
int port;
UDPSocket lockerSocket;

void sendPacket(int locker, int bicycleID) {
  bool received = false;
  while(!received) {
    try { 
      struct sockaddr src;
      std::stringstream ss;
      std::string dataString;
      int packetID = rand() % 8999 + 1000;
      ss << packetID << " " << locker << " " << bicycleID;
      dataString = ss.str();
      std::cout << "Sending: " << dataString;
      std::vector< unsigned char > data(dataString.begin(), dataString.end());
      std::cout << "  To: " << lockerSocket.port( ) << std::endl;
      int numWrite = lockerSocket.write( data, src );
      std::cout << "Wrote " << numWrite << " bytes" << std::endl;
    } catch ( std::exception& ex ) {
      std::cout << "Failed" << std::endl;
    }
    received = receivePacket(packetID);
  return;
}

bool receivePacket(int packetID) {
  try {
    std::cout << "Running: " << lockerSocket.port( ) << std::endl;
    struct sockaddr src;
    std::vector< unsigned char > data( 100 );
    // Note: Currently blocking
    int numRead = lockerSocket.read( data, src );
    std::cout << "Read " << numRead << " bytes" << std::endl;
    std::string dataStr( data.begin( ), data.begin( ) + numRead );
    std::cout << "Message: " << dataStr << std::endl;
    int receivedID;
    sscanf(dataStr.c_str(), "%d", &receivedID);
    bool result = (receivedID==packetID ? true : false);
    return result;
  } catch ( std::exception& ex ) {
    std::cout << "Failed" << std::endl;
    return false;
  }

}


void printBicycles() {
  for(int i=0; i<capacity; i++) {
    std::cout << "Locker " << i << ": " << bicycles[i] << std::endl;
  }
}

void removeBicycle(int locker) {
  int bicycleID;
  if(locker >= capacity || locker < 0 || bicycles[locker] < 1) {
    std::cout << "Invalid locker number, couldn't remove bicycle." << std::endl;
    return;
  }
  bicycleID = bicycles[locker];
  bicycles[locker] = 0;
  sendPacket(locker, bicycleID);
  std::cout << "Bicycle " << bicycleID << " removed from locker " << locker << std::endl;
}

void addBicycle(int locker, int bicycleID) {
  if(locker >= capacity || locker < 0 || bicycles[locker] != 0) {
    std::cout << "Invalid locker number, couldn't add bicycle." << std::endl;
    return;
  }
  bicycles[locker] = bicycleID;
  sendPacket(locker, bicycleID);
  std::cout << "Bicycle " << bicycleID << " added to locker " << locker << std::endl;
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
  
  std::cout << "---------------------------------------------" << std::endl;
  std::cout << "-------------Locker Initialised--------------" << std::endl;

  int locker;
  int bicycleID;
  int p;
  srand(time(NULL));

  while(1) {    
    std::cout << "---------------------------------------------" << std::endl;
    printBicycles();
    std::cout << "---------------------------------------------" << std::endl;
    std::cout << "Add Bicycle: 1 lockerNumber bicycleID" << std::endl;
    std::cout << "Remove Bicycle: 2 lockerNumber" << std::endl;
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
        addBicycle(locker, bicycleID);
        break;

      case 2:
        std::cin >> input;
        sscanf(input.c_str(), "%d", &locker);
        removeBicycle(locker);
        break;

      case 4:
        receivePacket();
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