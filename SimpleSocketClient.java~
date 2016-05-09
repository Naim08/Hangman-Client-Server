package ClientServer;

import java.net.*;
import java.io.*;

/**
 * An abstract class that implements simple client to a remote service using
 * a Socket.
 * <P>
 * Based on an idea from
 * Courtois, <IT>Java Networking and Communications</IT>, Prentice-Hall, 1998.
 */

abstract public class SimpleSocketClient extends Thread
{
    public boolean debugOn = false;
  
    protected DataInputStream remoteInputStream;
    protected DataOutputStream remoteOutputStream;
  
    protected Socket remoteHostSocket;
    protected String hostName;
    protected int portNum;
  
    /**
     * Zero-parameter constructor does nothing.
     */

    public SimpleSocketClient() {}

    /**
     * Give a host name and a port to connect to
     *
     * @param hostName The name of the host to connect to
     * @param portNum  The port number to connect to
     */
    public SimpleSocketClient(String hostName, int portNum)
    {
	this.hostName = hostName;
	this.portNum = portNum;
    
	//note that somebody, someplace else decides whether to
	//kickstart our thread-- we don't do it here!
    }//SimpleSocketClient
  
  
    /**
     * We use this method as an "autopilot."  Given the initial setup
     * info, proceeds to execute a single session in a separate
     * thread. It opens a connection, handles the session, then closes
     * the connection when done 
     */
    public void run()
    {
	if (debugOn)
	    System.out.println("Running SimpleSocketClient...");
	if (openConnection() ) {
	    //if we opened the connection successfully
	    handleSession(); //handle the session...
	    closeConnection();//close the connection
	}
    }//run
  
    /**
     * Actually connect to the remote host.
     * When this fails, it's typically because we can't get a connection,
     * either because our local host is having Inet access problems,
     * or because the remote host isn't there
     * (or was incorrectly specified),
     * or because the remote host doesn't provide a service on
     * the given port.
     * @return True if connection was opened successfully.
     */
    public boolean openConnection()
    {
	boolean success = true; //start off assuming we'll be successful
    
	if (debugOn)
	    System.out.println("Opening Connection...");
    
	try {
	    //create and open a socket to the given host and port
	    remoteHostSocket = new Socket(hostName, portNum);
      
	    try {
		//get the output stream that we can use to send data
		remoteOutputStream = 
		    new DataOutputStream(remoteHostSocket.getOutputStream());
		//get the input stream that we can use to receive data
		remoteInputStream = 
		    new DataInputStream(remoteHostSocket.getInputStream());
	    }
	    catch (IOException ex) {
		success = false;
		if (debugOn) 
		    System.err.println("building streams failed: " + ex);
		//need to shut down the socket we've already opened!
		closeConnection();
	    }
	}
	catch (IOException sockEx) {
	    success = false;
	    if (debugOn)
		System.err.println("open socket failed: " + sockEx);
	}
    
	return success;
    } /* openConnection */
  
  
    /**
     * Handle a single session with the remote host.
     * This method must be overridden in any concrete class, since it
     * really defines how the client works!
     */
    abstract public void handleSession();
  
    /**
     * Close the open connection.
     */
    public void closeConnection()
    {
	if (debugOn) System.out.println("Closing Connection...");
    
	try {
	    //only attempt to close if we've got a valid socket
	    if (remoteHostSocket != null)  remoteHostSocket.close();
      
	    remoteOutputStream = null;
	    remoteInputStream = null;
	}
	catch (IOException closeEx){
	    if (debugOn)
		System.err.println("socket close threw: " + closeEx);
	};
    }
  
}/* class SimpleSocketClient */
