/*
 * Georgi Butev | LSBU | NOS | Stavros Dimitriou
 * Client Thread.
 * Used to take user input from the keyboard.
 * The message is then sent to the remote peer's ServerThread.
 * Used to handle RSA encryption for messages 
 * and DES encryption for files.
 */

import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.math.BigInteger;

public class ClientThread extends Thread{
	// Initialise non-static variables.
	private Socket clientSocket;
	private PrintWriter sendFromClientSocket;
	private BufferedReader receiveFromClientSocket;
	private String nickname;
	private String password;
	private String message;
	private BufferedReader keyboard;
	private InputStreamReader bufferedUserInput;
	private BigInteger modulus;
	private BigInteger publicKey;
	private BigInteger privateKey;
	private BigInteger cipherTextMessage;
	private String fileEncryptionKey;
	private String encryptedFilePath;
	private Boolean success = false;
	// Constructor
	ClientThread(Socket clientSocket, PrintWriter sendFromClientSocket, BufferedReader receiveFromClientSocket, String nickname, String password){
		this.clientSocket = clientSocket;
		this.sendFromClientSocket = sendFromClientSocket;
		this.receiveFromClientSocket = receiveFromClientSocket;
		this.nickname = nickname;
		this.password = password;
	}

	// Free system resources.
	public void shutdown(){
		try{
			// Close down the socket, buffers, readers, and writers.
			bufferedUserInput.close();
			keyboard.close();
			receiveFromClientSocket.close();
			sendFromClientSocket.close();
			clientSocket.close();
			System.out.printf("Goodbye %s!", nickname);
		} catch(IOException ioException){
			System.out.println("Input / Output error during shutdown.");
		}
	}

	// Start the thread.
	public void run(){
		// Create object for reading system/keyboard input.
		bufferedUserInput = new InputStreamReader(System.in);
		// Create object for buffered reading of the user input.
		keyboard = new BufferedReader(bufferedUserInput);
		// Create object from class blueprint, pass no arguments.
		RSAlgorithm rsa = new RSAlgorithm();
		// Get N from RSA algorithm.
		modulus = rsa.getModulus();
		// Get d from RSA algorithm.
		publicKey = rsa.getPublicKey();
		// Get e from RSA algorithm.
		privateKey = rsa.getPrivateKey();
		do{
			try{
				System.out.println(nickname + ": ");
				// Read user message for remote peer.
				message = keyboard.readLine();
				// Suspend the application upong the exit keyword.
				if(message.matches("exit")){
					shutdown();
					System.exit(0);
				} else if(message.startsWith("secret")){ // Send encrypted message to remote peer.
					// Notify the remote peer to await secret message.
					sendFromClientSocket.println("secret" + nickname);
					// Disregard the secret keyword and get the plain message.
					String msg = message.substring(7);
					// Create object from class blueprint, pass N, e, and message arguments.
					Encode encode = new Encode(modulus, privateKey, msg);
					// Get the RSA encrypted message.
					cipherTextMessage = encode.getEncodedMessage();
					// Send N from RSA algorithm.
					sendFromClientSocket.println(modulus);
					// Send d from RSA algorithm.
					sendFromClientSocket.println(publicKey);
					// Send the encrypted message.
					sendFromClientSocket.println(cipherTextMessage);
				} else if(message.startsWith("file")){ // Send encrypted text file to remote peer.
					// Notify the remote peer to await secret file.
					sendFromClientSocket.println("file" + nickname);
					// Disregard the file keyword and get the file name.
					String msg = message.substring(5);
					/*
					do{
						System.out.println("Enter 8 characters long secret file encryption key: ");
						fileEncryptionKey = keyboard.readLine();
					} while((fileEncryptionKey.length() < 8) || (fileEncryptionKey.length() > 8));
					*/
					// Create object from class blueprint, pass message and password arguments.
					EncryptFile encryptFile = new EncryptFile(msg, password); //fileEncryptionKey
					// Get the abstract path of the encrypted file.
					encryptedFilePath = encryptFile.getEncryptedFile();
					System.out.println("The file successfully was encrypted.");
					// Create object from class blueprint, pass file path and socket arguments.
					SendFile sendFile = new SendFile(encryptedFilePath, clientSocket);
					//fileSizeBytes = sendFile.getFileSize();
					//sendFromClientSocket.println(fileSizeBytes);

					// Upon success or failure to send the encrypted, notify the user.
					success = sendFile.send();
					
					if(success){
						System.out.println("The file was successfully sent.");
					} else {
						System.out.println("The file was NOT sent.");
					}

					sendFromClientSocket.println("filesent");
				} else { // Send plain text message to remote peer.
					sendFromClientSocket.println(nickname + ": " + message);
				}
			} catch(IOException ioException){
				System.out.println("Input / Output error client.");
				System.exit(1);
			}
		} while(true);
	}
}