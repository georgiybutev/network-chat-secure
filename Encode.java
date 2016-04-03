/*
 * Georgi Butev | LSBU | NOS | Stavros Dimitriou
 * Encrypt Message.
 * Used to encrypt String message with RSA scheme 
 * with 2048 bits long private key.
 */

import java.math.BigInteger;

public class Encode{
	// Initialise non-static variables.
	private BigInteger modulus;
	private BigInteger privateKey;
	private String message;
	private BigInteger messageBigInt;
	private BigInteger messageCipherText;
	// Constructor
	Encode(BigInteger modulus, BigInteger privateKey, String message){
		this.modulus = modulus;
		this.privateKey = privateKey;
		this.message = message;
	}

	public BigInteger getEncodedMessage(){
		// Get byte array from String message.
		messageBigInt = new BigInteger(message.getBytes());
		////while((messageBigInt.compareTo(modulus) != -1) || (messageBigInt.gcd(modulus).compareTo(BigInteger.valueOf(1)) != 0));
		
		// The encoding should satisfy the RSA algorithm condition.
		if((BigInteger.valueOf(1).compareTo(messageBigInt) < 0) 
			&& (messageBigInt.compareTo(modulus) < 0)){} else {
			System.out.println("Incorrect message conversation!");
		}

		// Encode the BigInteger message using the 
		// selected Euler totient and the generated modulus.
		messageCipherText = messageBigInt.modPow(privateKey, modulus);
		
		return messageCipherText;
	}
}