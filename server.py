from bluetooth import *
import time
import select
import socket


if sys.version < '3':
	print("BELOW VERSION 3")
	sys.exit(1)

# use this method instead of the normal input("")
def waitForJavaInput():
	result = ""
	while result == "":
		result = input("")
	return result

server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("", PORT_ANY))
server_sock.listen(5)
server_sock.setblocking(False)

server_sock2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_sock2.bind((socket.gethostname(), 28590))
server_sock2.listen(5)
server_sock2.setblocking(False)

port = server_sock.getsockname()
port2 = server_sock2.getsockname()

connectedSockets = []
connectedSocketNames = []
queuedBroadcasts = []

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
advertise_service( server_sock, "Scouting Server", service_id = uuid, service_classes = [ uuid, SERIAL_PORT_CLASS ], profiles = [ SERIAL_PORT_PROFILE ])

print("RFCOMM address: " + str(port))
print("TCP/IP address: " + str(port2))

while True:
	try:
		client_sock, client_info = server_sock.accept()
		print("accept(RFCOMM)")
		ready_to_read, ready_to_write, in_error = select.select([client_sock], [], [], 15)
		print("select(RFCOMM)")
		name = client_sock.recv(2048).decode("utf-8")
		print("read:(RFCOMM) " + name)
		client_sock.setblocking(False)
		print(str(client_sock))
		connectedSocketNames.append(name)
		connectedSockets.append(client_sock)
		print("NEW_SCOUT (RFCOMM):" + name)

	except Exception as err:
    		print("no incoming connections(RFCOMM)", err)

	try:
		client_sock2, client_info2 = server_sock2.accept()
		print("accept(TCP/IP)")
		ready_to_read2, ready_to_write2, in_error2 = select.select([client_sock2], [], [], 15)
		print("select(TCP/IP)")
		name2 = client_sock2.recv(2048).decode("utf-8")
		print("read(TCP/IP): " + name2)
		client_sock2.setblocking(False)
		print(str(client_sock2))
		connectedSocketNames.append(name2)
		connectedSockets.append(client_sock2)
		print("NEW_SCOUT (TCP/IP):" + name2)

	except Exception as err2:
    		print("no incoming connections(TCP/IP)", err2)

	consoleInput = waitForJavaInput()

	splitConsoleInput = consoleInput.split("^")

	for curConsoleInput in splitConsoleInput:
		queuedBroadcasts.append(consoleInput)

	counter = 0
	if len(queuedBroadcasts) > 0:
		broadcastToSend = queuedBroadcasts[0]
		queuedBroadcasts.remove(broadcastToSend)
		while (counter < len(connectedSockets) ):
			
			curSocket = connectedSockets[counter]
			try:
				ready_to_read, ready_to_write, in_error = select.select([curSocket], [curSocket], [curSocket], 5)
				print("Select")
				print(ready_to_read)
				print(ready_to_write)
				print(in_error)
			except select.error: 
				print ("LOST_SCOUT:" + connectedSocketNames[counter])
				curSocket.close()
				connectedSockets.remove(curSocket)
				connectedSocketNames.remove(connectedSocketNames[counter])
				continue
			if len(ready_to_read) > 0:
				recv = curSocket.recv(16777216).deocde("utf-8")
				msgs = recv.split("^")
				for msg in msgs:

					if (msg.find("SERVER") == 0):
						print ("MESSAGE" + msg)
					else:
						queuedBroadcasts.append(msg)
			if len(ready_to_write) > 0:
		        		if broadcastToSend != "READ_ONLY":
		        			try:
		        				if ( broadcastToSend.find(connectedSocketNames[counter] is 0)): 
		        					curSocket.send(broadcastToSend.encode("utf-8"))
		        				elif (broadcastToSend.find("BROADCAST") is 0):
		        					curSocket.send(broadcastToSend.encode("utf-8"))
		        			except Exception:
		        				print ("LOST_SCOUT:" + connectedSocketNames[counter])
		        				#curSocket.shutdown(2)# 0 = done receiving, 1 = done sending, 2 = both
		        				curSocket.close()
		        				connectedSockets.remove(curSocket)
		        				connectedSocketNames.remove(connectedSocketNames[counter])
		        				continue
			counter += 1