/*
 * Georgi Butev | LSBU | NOS | Stavros Dimitriou
 * Receive File.
 * Used to receive the encrypted file from the remote peer.
 */

import java.net.Socket;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.RandomAccessFile;

public class ReceiveFile{
	// Initialised non-static variables.
	private Boolean success = false;
	private Socket serverSocket;
	private BufferedInputStream bufferedInputStream;
	private DataInputStream dataInputStream;
	private RandomAccessFile randomAccessFile;
	private byte[] buffer;
	private int length;
	// Constructor
	ReceiveFile(Socket serverSocket){
		this.serverSocket = serverSocket;
	}

	public Boolean receive(){
		try{
			// Create object for efficient reading from the socket.
			bufferedInputStream = new BufferedInputStream(serverSocket.getInputStream());
			// Create object to read bytes in a portable way.
			dataInputStream = new DataInputStream(bufferedInputStream);
			// Create object for reading and writing bytes to the received file.
			randomAccessFile = new RandomAccessFile("received.des", "rw");

			// Procedure for receiving 1024 byte buffer using the socket.
			// Bytes are received until EOF (i.e. -1).
			buffer = new byte[1024];
			length = 0;
			do{
				randomAccessFile.write(buffer, 0, length);
				randomAccessFile.skipBytes(length);
			} while((length = dataInputStream.read(buffer)) != -1);

			// Close the output stream to free system resources.
			randomAccessFile.close();
		} catch(IOException ioException) {
			ioException.printStackTrace();
		}

		/*
		inputStream = serverSocket.getInputStream();
		try{
			fileOutputStream = new FileOutputStream("decoded.txt");
		} catch(FileNotFoundException fileNotFound){
			System.err.println(fileNotFound);
		}
		
		bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
		readFileSize = inputStream.read(fileSizeBytes, 0, fileSizeBytes.length);
		bytesPosition = readFileSize;

		do{
			readFileSize = inputStream.read(fileSizeBytes, bytesPosition, (fileSizeBytes.length - bytesPosition));
			if(readFileSize >= 0){
				bytesPosition += readFileSize;
			}
		} while(readFileSize > -1);

		bufferedOutputStream.write(fileSizeBytes, 0, bytesPosition);
		bufferedOutputStream.flush();
		*/
		return true;
	}
}