import pika


def callback(ch, method, properties, body):
    out = body.decode("utf-8")
    print("-- Received " + out)


queueName = 'analysisBatcher'
hostName = 'localhost'  # LOCALHOST -> Actual Connection
batchID = '0000'  # needs to make an id

connection = pika.BlockingConnection(pika.ConnectionParameters(hostName))
channel = connection.channel()
channel.queue_declare(queue='analysisBatcher')
channel.basic_consume(callback,
                      queue=queueName,
                      no_ack=True)

print("-- Receiving messages from " + queueName)
channel.start_consuming()
