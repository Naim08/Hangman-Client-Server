****Hangman Client***
Final Project of my server network client class. A few files are missing. 

Hangman Protocol
------- --------

Server responses to client requests are newline-terminated byte streams.

When a client first contacts the server, the server sends an
identifying header line, such as

  Hangman Server (10 March 2004). Type "HELP" for help.

The server responds to client requests as follows:

HELP    server sends a help message

NEW	server responds by sending the byte stream representation of an int:
  	  the number of letters in the word

GUESS x guesses the letter "x"
	server responds with a stream (one per character in the word)
 	  of (the byte string representation of) booleans, with the Ith 
          being true or false, according as whether or not the letter
          guessed matches the Ith character of the word

QUIT	quits the game (for this particular word)
	server responds with a byte stream, which is the word for the game

BYE	disconnects from the server
	server does not send a response


Hangman Client States
------- ------ ------

Client states:

STATE_UNCONNECTED	not connected to server (initial and final states)
STATE_CONNECTED		connected to server, not playing a game
STATE_PLAYING		playing a game
STATE_WINNER		just won a game
STATE_LOSER		just lost a game


Client requests to server:

NEW	state must be STATE_CONNECTED
	starts a new game
	state becomes STATE_PLAYING

GUESS x state must be STATE_PLAYING
	guesses the letter "x"
	if the word has been guessed, the state becomes STATE_WINNER
	if the client has used all its guesses, the state becomes STATE_LOSER
	otherwise, the state remains STATE_PLAYING

QUIT	state must be STATE_WINNER or STATE_LOSER
	client state becomes STATE_CONNECTED

BYE	state must be STATE_CONNECTED
	state becomes STATE_UNCONNECTED

NB: This design doesn't allow a player to quit early.
