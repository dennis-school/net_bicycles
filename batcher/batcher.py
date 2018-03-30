import pika
import pymysql
import time
import math
import struct
import threading

def flatten( a ):
    if isinstance(a, list) or isinstance(a,tuple):
        return [x for y in a for x in flatten(y)]
    else:
        return [ a ]

def obtain_batch( start_timestamp, end_timestamp ):
    connection = pymysql.connect('localhost', 'root',
                                 '', 'bicycle')
    try:
        with connection.cursor( ) as cursor:
            query = 'SELECT'\
                    '  bicycle_id,'\
                    '  taken_l.location_longitude AS taken_longitude,'\
                    '  taken_l.location_latitude AS taken_latitude,'\
                    '  returned_l.location_longitude AS returned_longitude,'\
                    '  returned_l.location_latitude AS returned_latitude '\
                    'FROM '\
                    '  transaction AS t'\
                    '  JOIN locker_set AS taken_l ON (t.taken_locker = taken_l.id)'\
                    '  JOIN locker_set AS returned_l ON (t.returned_locker = returned_l.id)'\
                    'WHERE'\
                    '  UNIX_TIMESTAMP(returned_timestamp) BETWEEN %s AND %s;'
            cursor.execute(query, (start_timestamp, end_timestamp))
            querydata = cursor.fetchall( )
            # Put it in a struct like:
            #
            # Message:
            #   UINT32 numElements
            #   Element[numElements] elements
            #
            # Element:
            #   CHAR[10] bicycleId
            #   DOUBLE TAKEN_LONGITUDE
            #   DOUBLE TAKEN_LATITUDE
            #   DOUBLE RETURNED_LONGITUDE
            #   DOUBLE RETURNED_LATITUDE
            binary = struct.pack( '>QI' + '10sdddd' * len(querydata), start_timestamp, len(querydata), *[ bytes(x,'utf8') if isinstance(x,str) else x for x in flatten( querydata ) ] )
            return binary
    finally:
        connection.close( )

def sendbatch(data):
    queuename = 'analysis_batcher'
    hostname = 'localhost'

    connection = pika.BlockingConnection(pika.ConnectionParameters(hostname))
    channel = connection.channel()
    channel.queue_declare(queue=queuename, durable=True)
    channel.basic_publish(exchange='',
                          routing_key=queuename,
                          body=data,
                          properties=pika.BasicProperties(
                              delivery_mode = 2, # persistent message
                          ) )

    print('-- Sent a batch')
    connection.close()

class BatcherInterval:
  def __init__(self,interval):
    self.start_time = math.floor( time.time( ) )
    self.interval = interval
    self.timer = threading.Timer(interval,self._timeout)
    self.timer.start( )

  def _timeout(self):
    end_time = self.start_time + self.interval
    data = obtain_batch( math.floor( self.start_time ), math.floor( end_time ) )

    sendbatch( data )

    # Some time will be lost for performing this timeout + CPU scheduling
    # Compensate for that such that it triggers at every interval
    delay = ( end_time + self.interval - time.time( ) )
    self.start_time = end_time

    if delay <= 0:
        self._timeout( )
    else:
        self.timer = threading.Timer(delay,self._timeout)
        self.timer.start( )

if __name__ == '__main__':
    # Run the batcher eveyr 5 seconds, where it pushes a batch of binary
    # data to RabbitMQ.
    interval = BatcherInterval( 5 )
