import socket
import struct


def int_to_bytes(val, num_bytes):
    return [(val & (0xff << pos*8)) >> pos*8 for pos in range(num_bytes)]


UDP_IP = "127.0.0.1"
UDP_PORT = 37778 # Change this for the particular server port
MESSAGE = 42774

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)
print("message:", MESSAGE)

#data = str(MESSAGE)
#data = int(data)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # UDP
sock.sendto(struct.pack('>H', MESSAGE), (UDP_IP, UDP_PORT))
