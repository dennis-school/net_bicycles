#!/usr/bin/env python
import pika
import struct
import pymysql
import time
import datetime

def parseBatch( data ):
    (timestamp,num_elements) = struct.unpack( '>QI', data[0:12] )
    flat = struct.unpack( '>10sdddd' * num_elements, data[12:] )
    elements = []
    for i in range(num_elements):
        elements.append( { 'bicycle_id':         flat[i*5+0].decode( 'utf-8' )
                         , 'taken_longitude':    flat[i*5+1]
                         , 'taken_latitude':     flat[i*5+2]
                         , 'returned_longitude': flat[i*5+3]
                         , 'returned_latitude':  flat[i*5+4] } )
    return (timestamp,elements)

def insertIntoDatabase( timestamp, value ):
    connection = pymysql.connect('localhost', 'root',
                                 '', 'bicycle')
    try:
        with connection.cursor( ) as cursor:
            query = 'INSERT INTO statistics (timestamp, value) VALUES(%s, %s);'
            cursor.execute(query, (datetime.datetime.fromtimestamp(timestamp).strftime('%Y-%m-%d %H:%M:%S'), value))
        connection.commit( )
    finally:
        connection.close( )

def callback(ch, method, properties, body):
    print(" [x] Received batch" )

    (timestamp,elements) = parseBatch( body )
    # Now it takes 6 more seconds to "process" the data
    time.sleep(6)
    insertIntoDatabase( timestamp, len( elements ) )
    print(" [x] Done")

    ch.basic_ack(delivery_tag = method.delivery_tag)


queuename = 'analysis_batcher'
hostname = 'localhost'

connection = pika.BlockingConnection(pika.ConnectionParameters(host=hostname))
channel = connection.channel()

channel.queue_declare(queue=queuename, durable=True)
print(' [*] Waiting for messages. To exit press CTRL+C')

channel.basic_qos(prefetch_count=1)
channel.basic_consume(callback,
                      queue=queuename)

channel.start_consuming()
