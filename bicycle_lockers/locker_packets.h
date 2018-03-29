#ifndef LOCKER_PACKETS_H
#define LOCKER_PACKETS_H

#include <cstdint>
#include <sys/types.h>
#include <cstdlib>
#include <arpa/inet.h>
#include <netinet/in.h>

typedef struct transactionPacket {
	uint16_t flag = htons(2); //used by coordinator to denote transaction packet
	uint16_t packetID; //randomly generated ID for checking successful transmassion
	uint8_t type; //0 if bicycle removed / 1 if bicycle returned
	uint8_t bicycleID[10]; //ID for bicycle used in transaction
	uint32_t userID; //ID of user involved in transaction
} transactionPacket;

typedef struct connectionPacket {
	uint16_t flag = htons(3); //used by coordinator to denote connection packet
	uint16_t packetID; //randomly generated ID for checking successful transmassion
} connectionPacket;

#endif