#ifndef TRANSACTION_PACKET_H
#define TRANSACTION_PACKET_H

#include <cstdint>
#include <sys/types.h>
#include <cstdlib>

typedef struct transactionPacket {
	uint16_t flag = 2; //used by coordinator to denote transaction packet
	uint16_t packetID; //randomly generated ID for checking successful transmassion
	uint8_t type; //0 if bicycle removed / 1 if bicycle returned
	char bicycleID[10]; //ID for bicycle used in transaction
	uint32_t userID; //ID of user involved in transaction
} transactionPacket;

typedef struct connectionPacket {
	uint16_t flag = 6; //used by coordinator to denote connection packet
	uint16_t packetID; //randomly generated ID for checking successful transmassion
} connectionPacket;

#endif