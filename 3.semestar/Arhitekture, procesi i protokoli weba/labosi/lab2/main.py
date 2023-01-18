import flask
import time
import flask_sock

app = flask.Flask(__name__)
sock = flask_sock.Sock(app)

last_message_a = ""
last_message_a_read = True
last_message_b = ""
last_message_b_read = True


@app.route('/')
def index():
	return flask.render_template("index.html")

@app.route('/poll/<person>')
def polling(person):
	# validate person is a or b
	if person not in ["a", "b"]:
		return "nemoze", 413

	global last_message_a_read
	global last_message_b_read
	
	if person == "a" and not last_message_a_read:
		last_message_a_read = True
		return last_message_a, {'Content-Type':'text/plain'}
	elif person == "b" and not last_message_b_read:
		last_message_b_read = True
		return last_message_b, {'Content-Type':'text/plain'}

	return "", {'Content-Type':'text/plain'}



@app.route('/longpoll/<person>')
def longpolling(person):
	# validate person is a or b
	if person not in ["a", "b"]:
		return "nemoze", 413
	
	global last_message_a_read
	global last_message_b_read
  
	if person == "a":
		last_a = last_message_a
		while True:
			time.sleep(1)
			if last_a != last_message_a or not last_message_a_read:
				break
		last_message_a_read = True
		return last_message_a, {'Content-Type':'text/plain'}
	else:
		last_b = last_message_b
		while True:
			time.sleep(1)
			if last_b != last_message_b or not last_message_b_read:
				break
		last_message_b_read = True
		return last_message_b, {'Content-Type':'text/plain'}


@sock.route('/ws/a')
def ws_a(ws): # ovo poziva klijent b
	last_a = last_message_a
	global last_message_a_read
	global last_message_b_read
	while True:
		time.sleep(1)
		message = ws.receive(timeout=1)
		if message != None:
			global last_message_b
			last_message_b = message
			last_message_b_read = False
		
		if last_a != last_message_a or not last_message_a_read:
			last_a = last_message_a
			last_message_a_read = True
			ws.send(last_a)
		
@sock.route('/ws/b')
def ws_b(ws): # ovo poziva klijent a
	last_b= last_message_b
	global last_message_a_read
	global last_message_b_read
	while True:
		time.sleep(1)
		message = ws.receive(timeout=1)
		if message != None:
			global last_message_a
			last_message_a = message
			last_message_a_read = False
		
		if last_b != last_message_b or not last_message_b_read:
			last_b = last_message_b
			last_message_b_read = True
			ws.send(last_b)

@app.route('/send/<person>', methods=["POST"])
def send(person):
	# validate person is a or b
	if person not in ["a", "b"]:
		return "nemoze", 413

	global last_message_a
	global last_message_a_read
	global last_message_b
	global last_message_b_read
	if person == "a":
		last_message_a = flask.request.args["message"]
		last_message_a_read = False
	else:
		last_message_b = flask.request.args["message"]
		last_message_b_read = False
	return "ok"


@app.route('/poll.html')
def poll():
	return flask.render_template("poll.html")


@app.route('/longpoll.html')
def longpoll():
	return flask.render_template("longpoll.html")


@app.route('/ws.html')
def ws():
	return flask.render_template("ws.html")	

app.run(host='0.0.0.0', port=81, threaded=True)
