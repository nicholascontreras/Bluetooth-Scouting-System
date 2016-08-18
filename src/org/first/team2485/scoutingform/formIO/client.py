from bluetooth import *
import sys
import time
from threading import *
import select


t = None

def receiveFromServer():
	try:
        		ready_to_read, ready_to_write, in_error = select.select([sock], [], [sock])
        		print(ready_to_read)
        		print(ready_to_write)
        		print(in_error)
	except select.error:
        		print ("Error in Python 'Select'")
        		return
	if len(ready_to_read) > 0:
        		recv = sock.recv(2048)
        		print (recv)
	if not (ready_to_read[0] or ready_to_write[0] or in_error[0]):
		print ("no server message")	

	print("starting new timer")
	t = Timer(2.0, receiveFromServer)
	t.start()

if sys.version < '3':
    input = raw_input

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
	print("Connect")
	scoutName= input("GETNAME")
	print("Scout name received")
	sock.send(scoutName)
except TypeError as err:
    	print ("failed to connect - Port: \"%s\" Name: \"%s\" Host: \"%s\" " % (port, name, host))
    	print ("Error: ", err)
    	sys.exit()

t = Timer(10.0, receiveFromServer)
t.start()

while True:
	consoleInput = input("I")
	print("Input is: " + consoleInput)
	if consoleInput.find("BROADCAST") != -1:
		sock.send(consoleInput)
	elif consoleInput.find("SendToServer") !=-1:
        		sock.send(consoleInput)
	else:
        		file = open("unsentData/scoutingData.csv", "r")
        		data = file.read()
        		file.close()
        		print("Sending: " + data)
        		sock.send(data)
        		print("Sent")
        		millis = int(round(time.time() * 1000))
        		newFile = open(str(millis) + ".csv", "w")
        		newFile.write(data)
        		newFile.close()
        		print ("Scouting Data Received")






