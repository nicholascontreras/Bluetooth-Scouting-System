from bluetooth import *
import sys

if sys.version < '3':
    input = raw_input

addr = None

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

print("looking for scouting server...")

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

print("connected.  type stuff")
while True:
    file = open("unsentData/scoutingData.csv", "r")
    data = file.read()
    data.close()
    if len(data) == 0: 
        print("No data")
    print("Sending: " + data)
    sock.send(data)
    print("Sent")
    newFile = open(gmtime(0) + ".csv", "w")
    newFile.write(data)
    newFile.close()
    break;

print("Closing socket")
sock.close()
sys.exit(0)