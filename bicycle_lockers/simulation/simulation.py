import time
import subprocess
import random

class Locker:
  def __init__(self,port,coordPort,capacity):
    self._port = port
    # TODO: Perhaps pipe the stdout as well, after everything works
    self._process = subprocess.Popen( [ "../a.out", str(port), str(coordPort), str(capacity), *( "Empty" for i in range(capacity) ) ], stdin=subprocess.PIPE, stdout=subprocess.PIPE )
    self._slots = [ None for i in range( capacity ) ]

  def hasEmptySlot( self ):
    indices = [ x[0] for x in zip(range(len(self._slots)),self._slots) if x[1] == None ]
    return len(indices) > 0

  def hasBicycle( self ):
    indices = [ x[0] for x in zip(range(len(self._slots)),self._slots) if x[1] != None ]
    return len(indices) > 0

  def takeRandom( self ):
    ''' Takes a bicycle from one of it's slots, and returns the bicycle id '''
    # self._process.write( )
    indices = [ x[0] for x in zip(range(len(self._slots)),self._slots) if x[1] != None ]
    randIndex = random.randint(0,len(indices)-1)
    id = self._slots[indices[randIndex]]
    self._slots[indices[randIndex]] = None
    print( '[Locker %04d] Took bicycle %s from slot %d' % ( self._port, id, indices[ randIndex ] ) )
    self._process.stdin.write( bytes( '2 %d 1\n' % indices[ randIndex ], 'utf8' ) )
    self._process.stdin.flush( )
    return id

  def placeBicycle( self, id ):
    ''' Places a bicycle in a available slot '''
    indices = [ x[0] for x in zip(range(len(self._slots)),self._slots) if x[1] == None ]
    assert len(indices) > 0
    randIndex = random.randint(0,len(indices)-1)
    self._slots[ indices[ randIndex ] ] = id
    print( '[Locker %04d] Placed bicycle %s in slot %d' % ( self._port, id, indices[ randIndex ] ) )
    self._process.stdin.write( bytes( '1 %d %s 1\n' % ( indices[ randIndex ], id ), 'utf8' ) )
    self._process.stdin.flush( )

if __name__ == '__main__':
  with open('bicycles.txt') as f:
    bicycles = [ x.strip( ) for x in f.readlines( ) if len(x.strip()) == 10 ]

  with open('lockers.txt') as f:
    # lockers in format (port, capacity)
    lockerDescs = [x.strip( ).split( ' ' ) for x in f.readlines() if len(x.strip().split(' ')) == 2]
    lockerDescs = list((int(a),int(b)) for (a,b) in lockerDescs)
  
  lockers = [ Locker(l[0], 8100, l[1]) for l in lockerDescs ]
  takenBicycles = bicycles[:]

  time.sleep( 1 )

  while True:
    # Either place a random bike, or take one.
    # If there are many unplaced bikes, it is more likely for a bike to be placed
    if random.random( ) < len( takenBicycles ) / len( bicycles ):
      # Place a random taken bicycle in a random lockers
      slotLockers = [ l for l in lockers if l.hasEmptySlot( ) ]
      if len(takenBicycles) > 0 and len( slotLockers ) > 0:
        bicycleIndex = random.randint(0,len(takenBicycles)-1)
        lockerIndex = random.randint(0,len(slotLockers)-1)
        slotLockers[lockerIndex].placeBicycle( takenBicycles[bicycleIndex] )
        takenBicycles.remove( takenBicycles[bicycleIndex] )
    else:
      # Take a random bicycle
      filledLockers = [ l for l in lockers if l.hasBicycle( ) ]
      if len( filledLockers ) > 0:
        lockerIndex = random.randint(0,len(filledLockers)-1)
        takenBicycles.append( filledLockers[lockerIndex].takeRandom( ) )
    time.sleep( 0.1 )
