from bluetooth import *
import time
import select


if sys.version < '3':
	input = raw_input

server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("", PORT_ANY))
server_sock.listen(1)
server_sock.setblocking(False)

port = server_sock.getsockname()[1]
connectedSockets = []
connectedSocketNames = []
queuedBroadcasts = []

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
advertise_service( server_sock, "Scouting Server", service_id = uuid, service_classes = [ uuid, SERIAL_PORT_CLASS ],profiles = [ SERIAL_PORT_PROFILE ])

print("Waiting for connections on RFCOMM channel: " + str(port))

while True:
	try:
		client_sock, client_info = server_sock.accept()
		print("accept")
		ready_to_read, ready_to_write, in_error = \
		select.select([client_sock], [], [], 15)
		print("select")
		name = client_sock.recv(2048)
		print("read: " + name)
		client_sock.setblocking(False)
		print(str(client_sock))
		connectedSocketNames.append(name)
		connectedSockets.append(client_sock)
		print("Socket added")

	except Exception as err:
    		print("no incoming connections", err)

	consoleInput = input("I")
	while (consoleInput.find("^") != -1):
		consoleInput = consoleInput[:consoleInput.find("^")] + consoleInput[(consoleInput.find("^") + 1):]

	while (consoleInput.find("*") != -1):
		consoleInput = consoleInput[:consoleInput.find("*")] + consoleInput[(consoleInput.find("*") + 1):]
	
	print("Input is: " + consoleInput)

	if (not(consoleInput  is "")):
		consoleInput = consoleInput + "*Server^"

	queuedBroadcasts.append(consoleInput)

	broadcastToSend = "IGNORE"
	counter=0
	if len(queuedBroadcasts) > 0:
		broadcastToSend = queuedBroadcasts[0]
		queuedBroadcasts.remove(broadcastToSend)
		while (counter < len(connectedSockets) ):
			
			curSocket = connectedSockets[counter]
			try:
				ready_to_read, ready_to_write, in_error = select.select([curSocket], [curSocket], [curSocket], 10)
				print("Select")
				print(ready_to_read)
				print(ready_to_write)
				print(in_error)
			except select.error: 
				curSocket.close()
				connectedSockets.remove(curSocket)
				connectedSocketNames.remove(connectedSocketNames[counter])
				print ("connection error")
				continue
			if len(ready_to_read) > 0:
				recv = curSocket.recv(2048)
				msgs = recv.decode().split("^")
				for msg in msgs:
					if (msg is ""):
						continue
					if (  msg.find("BROADCAST") is -1 and msg.find("SendToServer") is -1):
						print ("Making file...")
						millis = int(round(time.time() * 1000))
						newFile = open("C:/Users/Troy/Desktop/" + str(millis) + ".csv", "w")
						newFile.write(msg)
						newFile.close()
						print ("Made File")
					if msg.find("BROADCAST") is 0:
			        			# do stuff with received data
			        			queuedBroadcasts.append(msg)
			        			print ("recieved: " + msg)
					if msg.find("SendToServer") is 0:
			        			print ("received:" + msg)
			if len(ready_to_write) > 0:
	        	# connection for sending is valid, send the next item
	        		if broadcastToSend != "IGNORE":
	        				try:
	        					if ( broadcastToSend.find(connectedSocketNames[counter] is 0)): 
	        						curSocket.send(broadcastToSend)
	        					elif (broadcastToSend.find("BROADCAST") is 0):
	          						curSocket.send(broadcastToSend)
	          				except Exception:
	          					print ("Connection disconnected for " + connectedSocketNames[counter])
	          					#curSocket.shutdown(2)# 0 = done receiving, 1 = done sending, 2 = both
	          					curSocket.close()
	          					connectedSockets.remove(curSocket)
	          					connectedSocketNames.remove(connectedSocketNames[counter])
	          					print ("Connection successfully disconnected")
	          					continue
			counter += 1