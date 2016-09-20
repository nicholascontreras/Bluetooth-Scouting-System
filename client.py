from bluetooth import *
import sys
import time
from threading import *
import select
import os
import sys

if sys.version_info[0] < 3:
	input = raw_input

def waitForJavaInput():
	result = ""
	while result == "":
		result = input("")
	return result

print("Python Version: " + str(sys.version_info[0]))

temp = waitForJavaInput()
print("Name = " + temp)

addr = None
uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
print("Looking for scouting server...")

service_matches = find_service(uuid = uuid, address = addr)

if len(service_matches) == 0:
	print("Could not find Server")
	sys.exit(1)

port = 0
name = 0
host = 0

for x in range(0, len(service_matches)):
	match = service_matches[x]
	port = match["port"]
	name = match["name"]
	host = match["host"]

	if name == "Scouting Server":
        		break;

print("Connecting to Port: \"%s\" Name: \"%s\" Host: \"%s\" " % (port, name, host))

sock=BluetoothSocket( RFCOMM )
try:
	sock.connect((host, port))
	print("SEND NAME")
	scoutName = waitForJavaInput()
	print("Scout name received")
	sock.send(scoutName)
except TypeError as err:
    	print ("failed to connect - Port: \"%s\" Name: \"%s\" Host: \"%s\" " % (port, name, host))
    	print ("Error: ", err)
    	sys.exit()

while True:
	consoleInput = waitForJavaInput()
	print("Input is: " + consoleInput)
	msgs = consoleInput.split("^")
	for msg in msgs:
        		if (not(msg is "")):
		        	if (msg == "SEND_SCOUTING_DATA"):
		        		file = open(os.path.dirname(os.path.realpath(__file__)) + "/unsentData/scoutingData.csv", "r")
		        		data = file.read()
		        		file.close()
		        		print("Sending: " + data)
		        		data = data + "^"
		        		sock.send(data)
		        		print("Passed to socket...")
		        		millis = int(round(time.time() * 1000))
		        		newFile = open(os.path.dirname(os.path.realpath(__file__)) + "/" + str(millis) + ".csv", "w")
		        		newFile.write(data)
		        		newFile.close()
		        		print ("Scouting Data Sent")
		        	elif (msg != "READ_ONLY"):
		        		msg = msg + "^"
		        		sock.send(msg)
		        	else:
		        		try:
			        		ready_to_read, ready_to_write, in_error = select.select([sock], [], [sock], 5)
			        		print(ready_to_read)
			        		print(ready_to_write)
			        		print(in_error)
			        	except select.error:
			        		print ("Error in Python 'Select'")
			        	if len(ready_to_read) > 0:
			        		recv = sock.recv(2048)
			        		msgs = recv.split("^")
			        		for msg in msgs:
			        			if (not(msg is "")):
			        				print ("MESSAGE" + msg)