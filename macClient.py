import sys
import time
from threading import *
import select
import socket
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

address = waitForJavaInput()
print("Address= " + address)

print("Looking for scouting server...")

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

try:
	sock.connect((address, 28590))
	print("Sock: " + str(sock.getsockname()))
	print("SEND NAME")
	scoutName = waitForJavaInput()
	print("Scout name received")
	sock.send(scoutName.encode("utf-8"))
except TypeError as err:
    	print ("failed to connect ")
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
			        		recv = sock.recv(16777216)
			        		msgs = recv.split("^")
			        		for msg in msgs:
			        			if (not(msg is "")):
			        				print ("MESSAGE" + msg)