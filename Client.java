/*
 * Georgi Butev | LSBU | NOS | Stavros Dimitriou
 * Chat Client/Server
 * Used to get connection details and
 * start the client and the server thread.
 */

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.lang.IllegalArgumentException;
import java.util.Locale;
import java.util.Arrays;

public class Client{

	// Initialise static variables.
	private static int localPortNumber;
	// Get local port for server socket bind.
	private static void getConnectionDetails(String[] args){
		try{
			localPortNumber = Integer.parseInt(args[0]);
		} catch(ArrayIndexOutOfBoundsException arrayException){
			System.out.println("Please use the following format: \n"
				+ "java Client 4444\n");
			System.exit(1);
		} catch(NumberFormatException numberException){
			System.out.println("Please, choose a port number in the range of 1024 and 65534.");
			System.exit(1);
		}
	}

	public static void main(String[] args){
		getConnectionDetails(args);
		// Create object from class blueprint, pass port number as argument.
		ServerAccept sa = new ServerAccept(localPortNumber);
		// Initialise the server thread.
		sa.start();
		// Create object from class blueprint.
		ClientConnect cc = new ClientConnect();
		// Initialise the client thread.
		cc.start();
	}
}

class ServerAccept extends Thread{
	// Initialise non-static variables.
	private ServerSocket server;
	private Socket serverSocket;
	private PrintWriter sendFromServerSocket;
	private InputStreamReader bufferedServerSocket;
	private BufferedReader receiveFromServerSocket;
	private int localPortNumber;
	// Constructor
	ServerAccept(int localPortNumber){
		this.localPortNumber = localPortNumber;
	}

	// Start the thread.
	public void run(){
		this.localPortNumber = localPortNumber;
		try{
			// Bind the server to the user supplied port number.
			server = new ServerSocket(localPortNumber);
			// Accept incoming client socket requests.
			serverSocket = server.accept();
			// Create object for sending data using the server socket, auto flush.
			sendFromServerSocket = new PrintWriter(serverSocket.getOutputStream(), true);
			// Create object for receiving date using the server socket.
			bufferedServerSocket = new InputStreamReader(serverSocket.getInputStream());
		} catch (IOException ioException){
			System.err.println("Input / Output error: ");
			System.exit(1);
		}
		// Create object for buffered reading of incoming data.
		receiveFromServerSocket = new BufferedReader(bufferedServerSocket);

		//System.out.println("The server address is: " + serverSocket.getLocalAddress().toString());
		//System.out.println("The server port is: " + serverSocket.getLocalPort());

		// Create object from class blueprint, pass server socket related arguments.
		ServerThread st = new ServerThread(server, serverSocket, sendFromServerSocket, receiveFromServerSocket, "abcdefgh");
		// Initialise the server thread.
		st.start();

	}
}

class ClientConnect extends Thread {
	// Initialise non-static variables.
	private Socket clientSocket;
	private PrintWriter sendFromClientSocket;
	private BufferedReader receiveFromClientSocket;
	private BufferedReader keyboard;
	private InputStreamReader bufferedClientSocket;
	private InputStreamReader bufferedUserInput;
	private String remoteAddress;
	private int remotePortNumber;
	private String nickname;
	private String password;
	private Boolean correctPortNumber = false;
	// Constructor
	ClientConnect(){

	}
	
	// Start the thread.
	public void run(){

		configureConnection(); 

		try{
			try{
				// Create socket object, pass peer address and port as arguments.
				clientSocket = new Socket(remoteAddress, remotePortNumber);
			} catch (ProtocolException protocolException){
				System.err.println("TCP/IP error.");
				System.exit(1);
			} catch (BindException bindException){
				System.err.println("The port is already in use.");
				System.exit(1);
			} catch (ConnectException connectException){
				System.err.println("The remote peer is down.");
				System.exit(1);
			} catch (NoRouteToHostException noRouteToHost){
				System.err.println("The firewall is preventing you from accessing the remote peer.");
				System.exit(1);
			} catch (PortUnreachableException portUnreachable){
				System.err.println("The remote peer's port is unreachable.");
				System.exit(1);
			} catch (UnknownHostException unknownHost){
				System.err.println("The IP or host name format is incorrect.");
				System.exit(1);
			}
			// Create object for sending data using the client socket, auto flush.
			sendFromClientSocket = new PrintWriter(clientSocket.getOutputStream(), true);
			// Create object for receiving date using the client socket.
			bufferedClientSocket = new InputStreamReader(clientSocket.getInputStream());
		} catch (IOException ioException){
			System.err.println("Input / Output error.");
			System.exit(1);
		}

		// Create object for buffered reading of incoming data.
		receiveFromClientSocket = new BufferedReader(bufferedClientSocket);

		//System.out.println("The client address is: " + clientSocket.getInetAddress().toString());
		//System.out.println("The client port is: " + clientSocket.getPort());

		// Create object from class blueprint, pass client socket related arguments.
		ClientThread ct = new ClientThread(clientSocket, sendFromClientSocket, receiveFromClientSocket, nickname, password);
		// Initialise the server thread.
		ct.start();

	}

	// Set address, port, nickname, and password fields.
	private void configureConnection(){
		// Create object for reading system/keyboard input.
		bufferedUserInput = new InputStreamReader(System.in);
		// Create object for buffered reading of the user input.
		keyboard = new BufferedReader(bufferedUserInput);
		try{
			do{
				System.out.println("Enter remote peer's IP address or Host name: ");
				// Set the remote address if the user input is not empty.
				remoteAddress = keyboard.readLine();
			} while(remoteAddress.isEmpty());
			do{
				System.out.println("Enter remote peer's port number: ");
				// Parse and set the remote port if the number is between 1024 and 65543.
				remotePortNumber = Integer.parseInt(keyboard.readLine());
				if(remotePortNumber > 1024 && remotePortNumber < 65534){
					correctPortNumber = true;
				}
			} while(!correctPortNumber);
			do{
				System.out.println("Choose a nickname: ");
				// Set the nickname if the user input is not empty.
				nickname = keyboard.readLine();
			} while(nickname.isEmpty());
			do{
				System.out.println("Choose an 8 character password: ");
				// Set the DES encrypt/decrypt password. Make sure it is 8 characters long.
				password = keyboard.readLine();
			} while(password.isEmpty() || (password.length() < 8) || (password.length() > 8));

		} catch (IOException ioException){
			System.err.println("Input / Output error: ");
			System.exit(1);
		} catch(IllegalArgumentException illegalArgumentException){
			System.err.println("Client / Server arguments error: ");
			System.exit(1);
		}
	}
}