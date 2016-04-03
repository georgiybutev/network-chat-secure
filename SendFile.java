/*
 * Georgi Butev | LSBU | NOS | Stavros Dimitriou
 * Send File.
 * Used to send the encrypted file to the remote peer.
 */

import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class SendFile {
	// Initialised non-static variables.
	private String encryptedFilePath;
	private Socket clientSocket;
	private File file;
	private BufferedOutputStream bufferedOutputStream;
	private DataOutputStream dataOutputStream;
	private FileInputStream fileInputStream;
	private byte[] buffer;
	private int length;
	// Constructor
	SendFile(String encryptedFilePath, Socket clientSocket){
		this.encryptedFilePath = encryptedFilePath;
		this.clientSocket = clientSocket;
	}

	public Boolean send(){
		
		try{
			// Create object for file manipulation pointing to the encrypted file.
			file = new File(encryptedFilePath);
			// Create object for efficient writing to the socket.
			bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
			// Create object to write bytes in a portable way.
			dataOutputStream = new DataOutputStream(bufferedOutputStream);
			// Create object for reading from the encrypted file.
			fileInputStream = new FileInputStream(file);
			
			// Procedure for sending 1024 byte buffer using the socket.
			// Bytes are sent until EOF (i.e. -1).
			buffer = new byte[1024];
			length = 0;

			do{
				dataOutputStream.write(buffer, 0, length);
				dataOutputStream.flush();
			} while((length = fileInputStream.read(buffer)) != -1);
			
			// Close the input and output stream to free system resources.
			fileInputStream.close();
			dataOutputStream.close();
		} catch(IOException ioException){
			System.out.println("Error while sending the file.");
		}
		/*
		do {
			file = new File(encryptedFilePath);
			encryptedFileSize = new byte[(int)file.length()];
			try{
				fileInputStream = new FileInputStream(file);	
			} catch(FileNotFoundException fileNotFound){
				System.err.println("The file does not exist.");
			}
			
			try{
				bufferedInputStream = new BufferedInputStream(fileInputStream);
				bufferedInputStream.read(encryptedFileSize, 0, encryptedFileSize.length);
				outputStream = clientSocket.getOutputStream();
				System.out.println("Start sending...");
				outputStream.write(encryptedFileSize, 0, encryptedFileSize.length);
				outputStream.flush();
			} catch(IOException ioExceptionI){
				System.err.println("Input / Output error.");
			}
			
			try{
				remainingBytes = fileInputStream.available();
			} catch(IOException ioExceptionII) {
				System.err.println("Remaining bytes check failed.");
			}

		} while(remainingBytes == 0);*/

		return true;
	}

	/*public byte[] getFileSize(){
		file = new File(encryptedFilePath);
		encryptedFileSize = new byte[(int)file.length()];
		return encryptedFileSize;
	}*/
}