#include <cstdint>
#include <sys/types.h>
#include <iostream>
#include <cstdlib>
#include <stdio.h>
#include <sstream>



typedef struct transactionPacket {
	uint16_t packetID; //randomly generated ID for checking successful transmassion
	uint8_t type; //0 if bicycle removed / 1 if bicycle returned
	char bicycleID[10]; //ID for bicycle used in transaction
	uint32_t userID; //ID of user involved in transaction
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
	pack->packetID = (unsigned) 4345;
	pack->type = 1;
	pack->userID = (unsigned) 343434;
	std::cout << pack->packetID << " " << pack->type << " " << pack->userID << std::endl;
	return 1;
}