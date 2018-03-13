#include <iostream>
#include <cstdlib>
#include "udp_socket.h"

int main( int argc, char **argv ) {
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