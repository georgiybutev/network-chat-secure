/*
 * Georgi Butev | LSBU | NOS | Stavros Dimitriou
 * Server Thread.
 * There is no user input.
 * Used to receive text messages from the remote peer's ClientThread.
 * Used to handle RSA decryption for messages 
 * and DES decryption for files.
 */

import java.net.Socket;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.math.BigInteger;

public class ServerThread extends Thread{
	// Initialise non-static variables.
	private ServerSocket server;
	private Socket serverSocket;
	private PrintWriter sendFromServerSocket;
	private BufferedReader receiveFromServerSocket;
	private String message;
	private String plainTextMessage;
	private String nickname;
	private BigInteger modulus;
	private BigInteger publicKey;
	private BigInteger cipherTextMessage;
	private Boolean successReceiveFile = false;
	private Boolean successDecryptFile = false;
	private String password;
	private BufferedReader keyboard;
	private InputStreamReader bufferedUserInput;
	// Constructor
	ServerThread(ServerSocket server, Socket serverSocket, PrintWriter sendFromServerSocket, BufferedReader receiveFromServerSocket, String password) {
		this.server = server;
		this.serverSocket = serverSocket;
		this.sendFromServerSocket = sendFromServerSocket;
		this.receiveFromServerSocket = receiveFromServerSocket;
		this.password = password;
	}

	// Start the thread.
	public void run(){
		// Create object for reading system/keyboard input.
		bufferedUserInput = new InputStreamReader(System.in);
		// Create object for buffered reading of the user input.
		keyboard = new BufferedReader(bufferedUserInput);
		try{
			do{
				// Read the message from the remote peer.
				message = receiveFromServerSocket.readLine();
				// If the message is empty or matches filesent keyword, suspend the application.
				if(message == null || message.startsWith("filesent")){
					System.out.println("The connection was terminated by the remote peer.");
					System.exit(0);
				} else if(message.startsWith("secret")){ // Receive encrypted message from remote peer.
					// Disregard the secret keyword and get the plain message.
					nickname = message.substring(7);
					// Receive N from RSA algorithm.
					modulus = new BigInteger(receiveFromServerSocket.readLine()); // No parse function
					// Receive d from RSA algorithm.
					publicKey = new BigInteger(receiveFromServerSocket.readLine()); // Works only as constructor
					// Receive the encrypted message.
					cipherTextMessage = new BigInteger(receiveFromServerSocket.readLine()); // similar to new String(toByteArray)
					// Create object from class blueprint, pass N, d, and message arguments.
					Decode decode = new Decode(modulus, publicKey, cipherTextMessage);
					// Decrypt the received message.
					plainTextMessage = decode.getPlainTextMessage();
					System.out.println("\n" + nickname + ": " + plainTextMessage);
				} else if(message.startsWith("file")){ // Received encrypted text file from remote peer.
					// Disregard the file keyword and get the file path.
					nickname = message.substring(4);
					//fileSize = receiveFromServerSocket.readLine();
					//fileSizeBytes = fileSize.getBytes();

					// Create object from class blueprint, pass server socket arguments.
					ReceiveFile receiveFile = new ReceiveFile(serverSocket);
					// Upon success or failure to receive the file, notify the user.
					successReceiveFile = receiveFile.receive();

					/*do{
						System.out.println("Enter 8 characters long secret file decryption key: ");
						fileDecryptionKey = keyboard.readLine();
					} while((fileDecryptionKey.length() < 8) || (fileDecryptionKey.length() > 8));
					*/
					//fileDecryptionKey = "abcdefgh";
					// java.security.InvalidKeyException: Parameters missing
					// problem is CBC which requires IVs but ECB does not.
					// IvParameterSpec
					// java.security.InvalidAlgorithmParameterException: Wrong IV length: must be 8 bytes long

					// Create object from class blueprint, pass password arguments.
					DecryptFile decryptFile = new DecryptFile(password);
					// Upon success or failure to decrypt the file, notify the user.
					successDecryptFile = decryptFile.decrypt();

					if(successReceiveFile && successDecryptFile){
						System.out.println("The file was successfully received and decrypted.");
					} else {
						System.out.println("The file was NOT received or NOT decrypted.");
					}

				} else { // Receive plain text message from remote peer.
					System.out.println("\n" + message);
				}
			} while(true);
		} catch(IOException ioexception){
			System.out.println("Input / Output error server.");
			System.exit(1);
		}
	}
}