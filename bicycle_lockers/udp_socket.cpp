#include "udp_socket.h"
#include <stdexcept>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <cstring>
#include <unistd.h>
#include <vector>
#include <ctime>
#include <netdb.h>

bool getLocalAddrinfo( struct addrinfo **ppDstAddrinfo, const char *portname ) {
  struct addrinfo hints;
  memset( &hints, 0, sizeof( struct addrinfo ) );
  hints.ai_family = AF_INET;
  hints.ai_socktype = SOCK_DGRAM;
  hints.ai_protocol = IPPROTO_UDP;
  hints.ai_flags = AI_PASSIVE;
  const char *hostname = 0;
  return getaddrinfo( hostname, portname, &hints, ppDstAddrinfo ) == 0;
}

int findPortNum( int fd ) {
  struct sockaddr_in sin;
  socklen_t addrLen = sizeof( struct sockaddr_in );
  if( getsockname( fd, (struct sockaddr *) &sin, &addrLen ) == 0 ) {
    return ntohs(sin.sin_port);
  } else {
    return -1;
  }
}

int setupSocket( const char *portname ) {
  struct addrinfo *pAddrinfo;
  if ( !getLocalAddrinfo( &pAddrinfo, portname ) ) {
    return -1;
  }
  int fd = socket( pAddrinfo->ai_family, pAddrinfo->ai_socktype, pAddrinfo->ai_protocol );
  if ( fd < 0 ) {
    freeaddrinfo( pAddrinfo );
	  return -1;
  }
  if ( bind( fd, pAddrinfo->ai_addr, pAddrinfo->ai_addrlen ) < 0 ) {
    freeaddrinfo( pAddrinfo );
	  return -1;
  }
  freeaddrinfo( pAddrinfo );
  return fd;
}

UDPSocket::UDPSocket(const char *portname )
    : m_fd( setupSocket( portname ) ) {
  
  if ( m_fd == -1 )
    throw std::runtime_error( "Failed to create UDP socket" );
  
  m_port = findPortNum( m_fd );
  
  if ( m_port == -1 )
    throw std::runtime_error( "Failed to create UDP socket" );
}

UDPSocket::UDPSocket()
    : m_fd( setupSocket( "0" ) ) {
  
  if ( m_fd == -1 )
    throw std::runtime_error( "Failed to create UDP socket" );
  
  m_port = findPortNum( m_fd );
  
  if ( m_port == -1 )
    throw std::runtime_error( "Failed to create UDP socket" );
}

UDPSocket::~UDPSocket( ) {
  
}

int UDPSocket::port( ) const {
  return m_port;
}

int UDPSocket::fd( ) const {
  return m_fd;
}

int UDPSocket::read( std::vector< unsigned char >& data, struct sockaddr_in &dstPacketSrc, int wait) {
  socklen_t addrlen = sizeof( struct sockaddr );
  time_t timer;
  time(&timer);
  int numRead = -1;
  if (wait > 0) {
    while ( (difftime(time(NULL), timer) < wait) && numRead==-1 ) {
      numRead = recvfrom( m_fd, &data[0], data.size( ), MSG_DONTWAIT, (struct sockaddr *) &dstPacketSrc, &addrlen );
    } 
  } else {
    numRead = recvfrom( m_fd, &data[0], data.size( ), 0, (struct sockaddr *) &dstPacketSrc, &addrlen );
  }
  return numRead;
}

int UDPSocket::write( std::vector< unsigned char >& data, struct sockaddr_in &dstPacketDest ) {
  socklen_t addrlen = sizeof( struct sockaddr_in );
  dstPacketDest.sin_addr.s_addr = inet_addr("127.0.0.1");
  dstPacketDest.sin_family = AF_INET;
  int numWrite = sendto( m_fd, &data[0], data.size(), 0, (struct sockaddr *) &dstPacketDest, addrlen);
  return numWrite;

}