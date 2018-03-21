#ifndef UDP_SOCKET_H
#define UDP_SOCKET_H

#include <vector>
#include <sys/socket.h>
#include <sys/types.h>

// A non-server UDP socket on an arbitrary port. (Listens to loop-back address)
class UDPSocket {
public:
  UDPSocket( );
  ~UDPSocket( );
  int port( ) const;
  //void write( const std::vector< unsigned char >& data, struct sockaddr packetDst );
  
  // Make sure the 'data' buffer is big enough
  int read( std::vector< unsigned char >& data, struct sockaddr &dstPacketSrc );
private:
  int m_fd;
  int m_port;
};

#endif