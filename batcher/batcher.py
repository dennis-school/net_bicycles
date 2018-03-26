import pika
import pymysql
import datetime


def fetchdata():

    database = pymysql.connect("localhost", "user",
                               "password", "OVBicycleDB")
    cursor = database.cursor()
    query = "SELECT * FROM TRANSACTION WHERE TAKEN_TIMESTAMP > %" % lastdate

    try:
        cursor.execute(query)
        querydata = cursor.fetchall()
        for row in querydata:
            print("%" % row[1])

    except:
        print("Failed ")


def createbatch():
    print("fill this")


def sendbatch(data):

    queuename = 'analysisBatcher'
    hostname = 'localhost'
    batchid = '0000'  # needs to make an id
    data = 'DataBatch'  # remove when data is passed to function

    connection = pika.BlockingConnection(pika.ConnectionParameters(hostname))
    channel = connection.channel()
    channel.queue_declare(queue=queuename)
    channel.basic_publish(exchange='',
                          routing_key='analysisBatcher',
                          body=data)  # needs to send data

    print("-- Batcher sent " + batchid + " to " + queuename)
    connection.close()


lastdate = datetime.datetime.now()