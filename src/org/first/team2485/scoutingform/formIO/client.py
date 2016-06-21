from bluetooth import *
import sys
import time

if sys.version < '3':
    input = raw_input

addr = None

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

print("looking for scouting server....")

service_matches = find_service( uuid = uuid, address = addr )

if len(service_matches) == 0:
    print("couldn't find the the Scouting Server")
    sys.exit(1)

port
name
host

for x in xrange(0, len(service_matches)):

    match = service_matches[x]
    port = match["port"]
    name = match["name"]
    host = match["host"]

    if name == "Scouting Server":
        break;

print("connecting to Port: \"%s\" Name: \"%s\" Host: \"%s\" " % (port, name, host))

sock=BluetoothSocket( RFCOMM )
sock.connect((host, port))

while True:

    consoleInput = input("I")
    print("Input is: " + consoleInput)

    if consoleInput.find("BROADCAST") != -1:
        sock.send(consoleInput)
    else 
        file = open("unsentData/scoutingData.csv", "r")
        data = file.read()
        data.close()
        print("Sending: " + data)
        sock.send(data)
        print("Sent")
        millis = int(round(time.time() * 1000))
        newFile = open(str(millis) + ".csv", "w")
        newFile.write(data)
        newFile.close()