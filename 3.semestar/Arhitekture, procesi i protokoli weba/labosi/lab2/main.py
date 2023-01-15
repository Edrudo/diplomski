import flask
import time
import flask_sock

app = flask.Flask(__name__)
sock = flask_sock.Sock(app)

last_message_a = ""
last_message_b = ""

@app.route('/')
def index():
	return flask.render_template("index.html")

@app.route('/poll/<person>')
def polling(person):
	# validate person is a or b
	if person not in ["a", "b"]:
		return "nemoze", 413
	
	if person == "a":
		return last_message_a, {'Content-Type':'text/plain'}
	else:
		return last_message_b, {'Content-Type':'text/plain'}


@app.route('/longpoll/<person>')
def longpolling(person):
	# validate person is a or b
	if person not in ["a", "b"]:
		return "nemoze", 413
	
	print(last_message_a + " " + last_message_b)
  
	if person == "a":
		last_a = last_message_a
		while True:
			time.sleep(1)
			if last_a != last_message_a:
				break
		return last_message_a, {'Content-Type':'text/plain'}
	else:
		last_b = last_message_b
		while True:
			time.sleep(1)
			if last_b != last_message_b:
				break
		return last_message_b, {'Content-Type':'text/plain'}


@sock.route('/ws/a')
def ws_a(ws): # ovo poziva klijent b
	last_a = last_message_a
	while True:
		time.sleep(1)
		message = ws.receive(timeout=1)
		if message != None:
			global last_message_b
			last_message_b = message
		
		if last_a != last_message_a:
			last_a = last_message_a
			ws.send(last_a)
		
@sock.route('/ws/b')
def ws_b(ws): # ovo poziva klijent a
	last_b= last_message_b
	while True:
		time.sleep(1)
		message = ws.receive(timeout=1)
		if message != None:
			global last_message_a
			last_message_a = message
		
		if last_b != last_message_b:
			last_b = last_message_b
			ws.send(last_b)

@app.route('/send/<person>', methods=["POST"])
def send(person):
	# validate person is a or b
	if person not in ["a", "b"]:
		return "nemoze", 413

	global last_message_a
	global last_message_b
	if person == "a":
		last_message_a = flask.request.args["message"]
	else:
		last_message_b = flask.request.args["message"]
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
