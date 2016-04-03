/*
 * Georgi Butev | LSBU | NOS | Stavros Dimitriou
 * Decypt File.
 * Used to decrypt a file with DES, CBC, and PKCS5Padding scheme.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;
import java.io.IOException;

public class DecryptFile{
	// Initialise non-static variables.
	private String fileDecryptionKey;
	private File receivedFile;
	private File decryptedFile;
	private FileInputStream fileInputStream;
	private FileOutputStream fileOutputStream;
	private CipherInputStream cipherInputStream;
	private byte[] cipherKey;
	private SecretKeySpec secretKeySpec;
	private IvParameterSpec iv;
	private Cipher decrypt;
	private byte[] outputBytes;
	private int lengthBytes;
	// Constructor
	DecryptFile(String fileDecryptionKey){
		this.fileDecryptionKey = fileDecryptionKey;
	}

	public Boolean decrypt(){
		try{
			// The cipher key is based on the 8 character long password
			// which is converted into byte array.
			cipherKey = fileDecryptionKey.getBytes();
			// The inialising vectors are necessary for CBC.
			iv = new IvParameterSpec(cipherKey);
			// Create raw secret keys for DES or Triple DES.
			secretKeySpec = new SecretKeySpec(cipherKey, "DES");

			try{
				// Select the decoding algorithm.
				decrypt = Cipher.getInstance("DES/CBC/PKCS5Padding");
				// Switch to decypt mode using the secret key and initialising vectors.
				decrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
			} catch (java.security.NoSuchAlgorithmException noSuchAlgorithm){
				System.out.println("The encryption algorithm failed.");
			}

			// Create object for file manipulation pointing to the received file.
			receivedFile = new File("received.des");

			try{
				// Create object for reading the contents of a file.
				fileInputStream = new FileInputStream(receivedFile);
			} catch(IOException ioException){
				System.out.println("The specified file does not exist.");
			}

			// Create object for reading the file using the initialised cipher.
			cipherInputStream = new CipherInputStream(fileInputStream, decrypt);
			// Create object for file manipulation pointing to the plain text file.
			decryptedFile = new File("decrypted.txt");
			// Create object for writing to the plain text file.
			fileOutputStream = new FileOutputStream(decryptedFile);

			// Procedure for writing to the plain text file using 8 byte buffer.
			// The loop reads the encrypted file with the initialised cipher
			// and write to the plain text file until EOF (i.e. -1).
			outputBytes = new byte[8];
			lengthBytes = cipherInputStream.read(outputBytes);
			do{
				fileOutputStream.write(outputBytes, 0, lengthBytes);
				lengthBytes = cipherInputStream.read(outputBytes);
			} while(lengthBytes != -1);

			freeSystemResources();
		} catch (Exception exception) {
			System.err.println(exception);
		}

		return true;
	}

	private void freeSystemResources(){
		try{
			// Flush and close the stream readers.
			fileOutputStream.flush();
			fileOutputStream.close();
			cipherInputStream.close();
			fileInputStream.close();
		} catch (IOException ioException){
			System.err.println("Could not close stream readers.");
		}
	}
}