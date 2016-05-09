/******************************************
 * Project 5: Hangman Client
 *
 * Client implemention of Hangman game. Keeps track of guesses, words guess, and guesses remaining.
 *
 * A class that implements the abstract methods of Abstract HangmanClient.
 * It provides a text-based interface, as well as a main() method, giving an executable program.
 *
 *
 * @Author: Md Naim Miah
 * @Date: 9 May 2016
 ********************************************/

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class TextHangmanClient extends AbstractHangmanClient {

    private SafeBufferedReader readKeyboard = new SafeBufferedReader((Reader)new InputStreamReader(System.in));
    /**
     * The constructor for the TextHangmanClient class. Used to call constructor from AbstractHangmanClient.
     * @param debugging - True iff debugging output is enabled
     * @param serverName - The host on which the hangman server resides
     * @param portNumber - The port number on which the server is listening
     *
     */
    public TextHangmanClient(boolean debugging, String serverName, int portNumber) {
        super(debugging, serverName, portNumber); // AbstractHangmanClient constructor
    }

    /**
     * Obtain a guess from the user.
     * @return an uppercase letter for a GUESS
     */
    @Override
    public char elicitGuess() {
        char c = '0';
        try {
            String s;
            do {
                System.out.print("Letter? ");
                System.out.flush();
            } while (!Character.isLetter(c = (s = this.readKeyboard.readLine()).charAt(0)));
        }
        catch (IOException e) {
            System.err.println("TextHangmanClient ElicitGuess Error: " + e);
        }
        return Character.toUpperCase(c);
    }
    /**
     * Find out whether we want to play again.
     * @return true or false, according to whether we want to play again or not.
     */

    @Override
    public boolean elicitPlayAgain() {
        char c = '0';
        try {
            do {
                System.out.print("Another game (Y/N)? ");
                System.out.flush();
                String s = this.readKeyboard.readLine();
                c = s.charAt(0);
            } while ((c = Character.toUpperCase(c)) != 'Y' && c != 'N');
            if (c == 'N') 
                System.out.println("Game Ended!");
        }
        catch (IOException e) {
            System.err.println("TextHangmanClient ElicitPlayAgain Error: " + e);
        }
        return c == 'Y';
    }
    /**
     * Display the current game state.
     */
    @Override
    public void displayGame() {
        System.out.println("Word: " + this.wordSoFar + "\nGuesses remaining:" + this.guessesRemaining);
    }
    /**
     * Congratulate the winner on her acumen.
     */
    @Override
    public void congratulateWinner() {
        System.out.println("Word was: " + this.theWord + "\nCongratulations! You got the word!");
    }
    /**
     *Player failed to guess the word.
     */
    @Override
    public void punishLoser() {
        System.out.println("Sorry! Too many guesses! \nWord was: " + this.theWord + "\nHangman ... take a few \"practice swings\" (heh, heh)");
    }
    /**
     * Print usage message
     */
    public static void usage() {
        System.err.println("Usage: HangmanServer [-d] [-h] [server]");
        System.err.println("  -d: print debugging info");
        System.err.println("  -h: print this help msg");
    }
    /**
     * The usual main() function, which gets things rolling.
     * After parsing the command line, it invokes the constructor for this class. That's it! Optional command line parameters:
     *
     * <li>Flag -d: enable debug output</li>
     * <li>Flag -h: print help message</li>
     * <li>Name of alternate Hangman server</li>
     * <li>Any other flags (e.g., -x) will cause the help message to be printed, along with an error exit.</li>
     *
     */
    public static void main(String[] argv) {
        String hostName = "erdos.dsm.fordham.edu";
        boolean debugging = false;
        int portNumber = 9999;
        int n = 0;
        if (argv.length > 0 && argv[0].charAt(0) == '-') {
            n++;
            switch (argv[0].charAt(1)) {
                case 'h': {
                    TextHangmanClient.usage();
                    System.exit(0);
                    break;
                }
                case 'd': {
                    debugging = true;
                    break;
                }
                default: {
                    TextHangmanClient.usage();
                    System.exit(1);
                }
            }
        }
        if (argv.length > n) {
            hostName = argv[n];
        }
        new TextHangmanClient(debugging, hostName, portNumber);
    }
}

