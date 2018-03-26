import socket

UDP_IP = "127.0.0.1"
UDP_PORT = 37777 # Change this for the particular server port
MESSAGE = "Hello, World!"

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)
print("message:", MESSAGE)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # UDP
sock.sendto(bytes(MESSAGE.encode("utf-8")), (UDP_IP, UDP_PORT))