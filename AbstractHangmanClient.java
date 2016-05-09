/******************************************************************************
 *
 * This class contains the logic of a hangman game.
 * Since this class is a derived class of SimpleSocketClient, the main action is in its handleSession() method. Said method will contact a Hangman server, and obtain a word from the server. It will then interact with the player, until one of two things has happened:
 *
 * The player has guessed the word. In this case, the player is congratulated.
 * The player has made too many bad guesses. In this case, the player is "punished".
 * Those methods that actually interact with the player are left as abstract methods. A concrete subclass of AbstractHangmanClient can then interact with the player as it sees fit. Hence, one might have a text-based interface, a graphical interface, perhaps even an audio interface.
 *
 *********************************************************************************/


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public abstract class AbstractHangmanClient extends SimpleSocketClient {
    protected static final int HANGMAN_DEFAULT_PORT = 9999; //default port for hangman
    protected static final String HANGMAN_DEFAULT_SERVER = "erdos.dsm.fordham.edu"; //Currently where the server is deployed
    protected int guessesRemaining; // guesses remaining
    protected String theWord; // current word
    protected StringBuffer wordSoFar; // the word as determined so far by the player's letter choices

    private SafeBufferedReader readBuffer; //safe buffer

    private char userGuess;
    private int wordLength; //word length
    private int wordGuessed;
    private int currentstate;

    /**
     * The constructor for the AbstractHangmanClient class.
     * @param debugging - True iff debugging output is enabled
     * @param hostName - The host on which the hangman server resides
     * @param portNumber - The port number on which the server is listening
     */
    public AbstractHangmanClient(boolean debugging, String hostName, int portNumber) {
        super(hostName, portNumber);
        this.debugOn = debugging;
        this.start();
    }
    /**
     * Obtain a guess from the user. This method is abstract, because it depends on the user interface.
     * @return an uppercase letter for a GUESS
     */
    public abstract char elicitGuess();

    /**
     * Display the current game state. This method is abstract, because it depends on the user interface.
     */
    public abstract void displayGame();

    /**
     * Congratulate the winner on her acumen. This method is abstract, because it depends on the user interface.
     */
    public abstract void congratulateWinner();

    /**
     * Player didn't guess the word; hang him. This method is abstract, because it depends on the user interface.
     */
    public abstract void punishLoser(); //odd function name?

    /**
     * Find out whether we want to play again. This method is abstract, because it depends on the user interface.
     * @return true or false, according to whether we want to play again or not.
     */
    public abstract boolean elicitPlayAgain();


    /**
     * This methods overrides the SimpleSocketClient.handleSession() method.
     * As mentioned in the introductory section, this method handles the main logic of a hangman game.
     * It uses a fairly simple finite state machine to do this.
     *
     * @throws Exception - in case anything goes wrong
     */
    public void handleSession() {
        DataInputStream dataInputStream = new DataInputStream(this.remoteInputStream);
        InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream);
        this.readBuffer = new SafeBufferedReader((Reader)inputStreamReader);
        this.currentstate = 1;
        try {
            this.readBuffer.readLine();
            this.readBuffer.readLine();
            //FSA
            label : do {
                switch (this.currentstate) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        this.startNewGame();
                        break;
                    }
                    case 2: {
                        this.userGuess = this.elicitGuess();
                        if (this.debugOn) {
                            System.out.println("Letter guessed: " + this.userGuess);
                        }
                        this.processGuess();
                        if (!this.debugOn) continue label;
                        System.out.println("Word so far: " + this.wordSoFar);
                        break;
                    }
                    case 3:
                    case 4: {
                        this.handleEndGame();
                        break;
                    }
                    default: {
                        System.err.println("Error: Unaccounted state = " + this.currentstate);
                        this.currentstate = 0;
                    }
                }
            } while (this.currentstate != 0);
            this.remoteOutputStream.writeBytes("BYE\n");
        }
        catch (Exception e) {
            System.err.println("HangmanClient HandleSession error: " + e);
        }
    }
    /**
     * Start a new game.
     * This involves contacting the server, finding out the length of the new word, initializing our guess for what the new word looks like, and displaying the latter.
     * @throws IOException - error if trying to start a newgame
     */
    public void startNewGame() {
        try {
            this.remoteOutputStream.writeBytes("NEW\n");
            String s = this.readBuffer.readLine();
            this.wordLength = Integer.parseInt(s.trim());
            if (this.debugOn) {
                System.out.println("StartNewGame: wordlength = " + this.wordLength);
            }
            this.wordSoFar = new StringBuffer(this.wordLength);
            for (int i = 0; i < this.wordLength; ++i) {
                this.wordSoFar.append('*');
            }
            this.guessesRemaining = 10;
            this.wordGuessed = this.wordLength;
            this.currentstate = 2;
            this.displayGame();
        }
        catch (IOException e) {
            System.err.println("HangmanClient StartNewGame error: " + e);
        }
    }
     /**
     * Handle the end of game (either a winner or a loser)
     *
     *
     */
    public void handleEndGame() {
        try {
            this.remoteOutputStream.writeBytes("QUIT\n");
            this.theWord = this.readBuffer.readLine();
            System.out.println("Game Ended!");
        }
        catch (IOException e) {
            System.err.println("HangmanClient HandleEndGame error: " + e);
        }
        if (this.currentstate == 3)
            this.congratulateWinner();
        else
            this.punishLoser();

        this.currentstate = this.elicitPlayAgain() ? 1 : 0;
    }	

    /**
     * Process a valid (A..Z) guess from the player.
     *  We send the guess to the server.
     *  The server tells us at what positions the guess matches the word.
     *  Our knowledge of the word is updated appropriately. If we have guessed the word, the player is congratulated.
     *  If there are no matches, the number of remaining guesses is decremented, and the player is punished if there are no remaining guesses.
     *  @throws IOException - if there was an error reading a guess
     *
     */

    public void processGuess() {
        try {
            this.remoteOutputStream.writeBytes("GUESS " + this.userGuess + '\n');
            boolean b = false;
            for (int i = 0; i < this.wordSoFar.length(); ++i) {
                String string = this.readBuffer.readLine();
                if (!Boolean.valueOf(string.trim()).booleanValue() || this.wordSoFar.charAt(i) != '*') continue;
                this.wordSoFar.setCharAt(i, this.userGuess);
                b = true;
                this.wordGuessed--;
            }
            if (!b) this.guessesRemaining--;
            if (this.wordGuessed == 0) this.currentstate = 3;
            else if (this.guessesRemaining == 0) this.currentstate = 4;
            else this.displayGame();
        } catch (IOException e) {
            System.err.println("HangmanClient ProcessGuess error: " + e);
        }
    }
}

