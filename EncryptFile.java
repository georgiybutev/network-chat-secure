/*
 * Georgi Butev | LSBU | NOS | Stavros Dimitriou
 * Encrypt File.
 * Used to encrypt a file with DES, CBC, and PKCS5Padding scheme.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;
import java.math.BigInteger;
import java.io.IOException;

public class EncryptFile{
	// Initialised non-static variables.
	private String filePath;
	private String temp = "sent.des";
	private File inputFile;
	private File outputFile;
	private FileInputStream fileInputStream;
	private FileOutputStream fileOutputStream;
	private CipherInputStream cipherInputStream;
	private SecretKeySpec secretKeySpec;
	private IvParameterSpec iv;
	private String fileEncryptionKey;
	private byte[] cipherKey;
	private byte[] outputBytes;
	private int lengthBytes;
	private Cipher crypt;
	// Constructor
	EncryptFile(String msg, String fileEncryptionKey){
		this.filePath = msg;
		this.fileEncryptionKey = fileEncryptionKey;
	}

	public String getEncryptedFile(){
		try {
			// Create object for file manipulation pointing to the file to be sent.
			inputFile = new File(filePath);
			// Create object for file manipulation pointing to the encrypted file.
			outputFile = new File(temp);
			//cipherKey = privateKey.toByteArray(); // Too much for DES which requires 8 byte key.

			// The cipher key is based on the 8 character long password
			// which is converted into byte array.
			cipherKey = fileEncryptionKey.getBytes();
			// The inialising vectors are necessary for CBC.
			iv = new IvParameterSpec(cipherKey);
			// Create raw secret keys for DES or Triple DES.
			secretKeySpec = new SecretKeySpec(cipherKey, "DES");

			try{
				// Select the encoding algorithm.
				crypt = Cipher.getInstance("DES/CBC/PKCS5Padding");
				// Switch to encrypt mode using the secret key and initialising vectors.
				crypt.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
			} catch (java.security.NoSuchAlgorithmException noSuchAlgorithm){
				System.out.println("The encryption algorithm failed.");
			}

			try{
				// Create object for reading the contents of a file.
				fileInputStream = new FileInputStream(inputFile);
			} catch(IOException ioException){
				System.out.println("The specified file does not exist.");
			}

			// Create object for reading the file using the initialised cipher.
			cipherInputStream = new CipherInputStream(fileInputStream, crypt);
			// Create object for writing to the encrypted file.
			fileOutputStream = new FileOutputStream(outputFile);

			// Procedure for writing to encrypted file using 8 byte buffer.
			// The loop reads the plain text file with the initialised cipher
			// and write to the encrypted file until EOF (i.e. -1).
			outputBytes = new byte[8];
			lengthBytes = cipherInputStream.read(outputBytes);
			do {
				fileOutputStream.write(outputBytes, 0, lengthBytes);
				lengthBytes = cipherInputStream.read(outputBytes);
			} while (lengthBytes != -1);

			freeSystemResources();

		} catch (Exception exception) {
			System.err.println(exception);
		}
		return outputFile.toString();
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