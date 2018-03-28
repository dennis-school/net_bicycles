#include <cstdint>
#include <sys/types.h>
#include <iostream>
#include <cstdlib>
#include <stdio.h>
#include <sstream>



typedef struct transactionPacket {
	uint_8 packetID; //randomly generated ID for checking successful transmassion
	uint_8 type; //0 if bicycle removed / 1 if bicycle returned
	char bicycleID[10]; //ID for bicycle used in transaction
	uint_16 userID; //ID of user involved in transaction
} transactionPacket;



std::string itoa(int i) {
	std::stringstream ss;
	ss << i;
	return ss.str();
}

int main() {
	char* test;
	//test = "43450123456789034343434";
	std::cout << test <<std::endl;
	struct transactionPacket *pack;
	pack = (transactionPacket*) test;
	pack->packetID = 4345;
	pack->type = 0;
	pack->userID = 34343434;
	std::cout << itoa(pack->packetID) << " " << itoa(pack->type) << " " << pack->bicycleID << " " << itoa(pack->userID) << std::endl;
	return 1;
}