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
		ready_to_read, ready_to_write, in_error = \
		select.select([client_sock,], [client_sock,], [], 1)
		if ready_to_read >0:
			name = conn.recv(2048)
		client_sock.setblocking(False)
		print("connection:")
		print(str(client_sock))
		connectedSocketNames.append(name)
		connectedSockets.append(client_sock)

	except Exception:
    		print("no incoming connections")

	consoleInput = input("I")
	print("Input is: " + consoleInput)
	queuedBroadcasts.append(consoleInput)

	broadcastToSend = "IGNORE"
	counter=0
	if len(queuedBroadcasts) > 0:
		counter += 1
		broadcastToSend = queuedBroadcasts[0]
		queuedBroadcasts.remove(broadcastToSend)
		for curSocket in connectedSockets:
			try:
				ready_to_read, ready_to_write, in_error = select.select([curSocket], [curSocket], [curSocket], 1)
			except select.error: 
				conn.shutdown(2)# 0 = done receiving, 1 = done sending, 2 = both
				conn.close()
				connectedSockets.remove(curSocket)
				connectedSocketNames.remove(connectedSocketNames[counter])
				print ("connection error")
				continue
			if len(ready_to_read) > 0:
				if ( not (recv.find("BROADCAST")) and ( not (recv.find("SendToServer")))):
					recv = conn.recv(2048)
					millis = int(round(time.time() * 1000))
					newFile = open(str(millis) + ".csv", "w")
					newFile.write(recv)
					newFile.close()
				if recv.find("BROADCAST") != -1:
		        			# do stuff with received data
		        			queuedBroadcasts.append(recv)
		        			print ("recieved: " + recv)
				if recv.find("SendToServer") != -1:
		        			recv = conn.recv(2048)
		        			print ("received:" + recv)
			if len(ready_to_write) > 0:
	        			# connection for sending is valid, send the next item
	        			if broadcastToSend != "IGNORE":
	          				conn.send(broadcastToSend)