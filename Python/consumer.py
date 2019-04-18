import pika
import requests
import json
import time
import sys

# defining the api-endpoint 
API_ENDPOINT_COLLECTION = "http://0.0.0.0:5027/prrqpy/diff_collection"

API_ENDPOINT_JOB = "http://0.0.0.0:5027/prrqpy/diff_job"


def handler1(ch, method, properties, body):
    print("-> Handled: [%s]" % (body))
    # Extract data from body and call the job and wait for response
    # API endpoint should be updating database
    # sending post request and saving response as response object
    # Extract data from body

    datastore = json.loads(body.decode('utf-8'))

    print("Posting data to Collection")

    r = requests.post(url=API_ENDPOINT_COLLECTION, data=json.dumps(datastore))

    time.sleep(2)

    ch.basic_ack(delivery_tag=method.delivery_tag)
    print("Dequing the queue now")


def handler2(ch, method, properties, body):
    print("-> Handled: [%s]" % (body))
    # Extract data from body and call the job and wait for response
    # API endpoint should be updating database
    # sending post request and saving response as response object
    # Extract data from body

    datastore = json.loads(body.decode('utf-8'))

    print("Posting data to job")
    r = requests.post(url=API_ENDPOINT_JOB, data=json.dumps(datastore))
    time.sleep(2)
    ch.basic_ack(delivery_tag=method.delivery_tag)
    print("Dequing the queue now")


def keepalive():
    """Keep the websocket connection alive (called each minute)."""
    print('KeepAlive...' + time.asctime(time.localtime(time.time())))
    try:
        connection.add_timeout(120, keepalive)
    except OSError as error:
        print('Killing the Rabbitmq connection...')
        sys.exit(0)


connection = pika.BlockingConnection(
    pika.ConnectionParameters(host='localhost'))

channel = connection.channel()

print('Consumer started. Handling queue messages.')

keepalive()
channel.basic_consume(handler1, queue='validation_queue', no_ack=False)
channel.basic_consume(handler2, queue='job_queue', no_ack=False)

try:
    channel.start_consuming()
except KeyboardInterrupt:
    channel.stop_consuming()

connection.close()


# def publish_to_queue(q_name, data):
#     '''Default empty exchange with routing key equal to the queue name
#     will route the message to that queue'''
#     current_app.logger.info("Connecting to RabbitMQ broker")

#     start = time.time()
#     connection = pika.BlockingConnection(
#         pika.ConnectionParameters(host='localhost'))
#     channel = connection.channel()

#     channel.queue_declare(queue=q_name)
#     # default empty exchange with routing key equal to the queue name
#     # will route the message to that queue
#     channel.basic_publish(exchange='',
#                           routing_key=q_name,
#                           body=json.dumps(data))
#     current_app.logger.info("Published to Queue! JSON: " +
#                             str(json.dumps(data)))

#     connection.close()
#     elapsed = time.time() - start
#     current_app.logger.info("Publishing Time: " + str(elapsed))
