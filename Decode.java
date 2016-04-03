/*
 * Georgi Butev | LSBU | NOS | Stavros Dimitriou
 * Decrypt Message.
 * Used to decrypt String message with RSA scheme 
 * with 2048 bits long public key.
 */

import java.math.BigInteger;

public class Decode{
	// Initialise non-static variables.
	private BigInteger modulus;
	private BigInteger publicKey;
	private BigInteger cipherTextMessage;
	private BigInteger messageBigInt;
	private String plainTextMessage;
	// Constructor
	Decode(BigInteger modulus, BigInteger publicKey, BigInteger cipherTextMessage){
		this.modulus = modulus;
		this.publicKey = publicKey;
		this.cipherTextMessage = cipherTextMessage;
	}

	public String getPlainTextMessage() {

		// Return the decoded BigInteger message using 
		// the public key and the generated modulus.
		messageBigInt = cipherTextMessage.modPow(publicKey, modulus);
		// The decoded BigInteger message in converted to String
		// using the constructor's byte array argument.
		plainTextMessage = new String(messageBigInt.toByteArray());
		
		return plainTextMessage;
	}
}