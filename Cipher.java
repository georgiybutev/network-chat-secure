/*
 * Georgi Butev | LSBU | NOS | Stavros Dimitriou
 * Cipher
 * Used to demonstrate the RSA algorithm encode/decode procedure
 * as an additional plugin in conjuction with the RSA class.
 */

import java.math.BigInteger;

public class Cipher{
	// Initialise non-static variables.
	private BigInteger modulus;
	private BigInteger e;
	private String message = "Georgi";
	private BigInteger messageBigInt;
	private BigInteger messageCipherText;
	// Constructor
	Cipher(BigInteger modulus, BigInteger e){
		this.modulus = modulus;
		this.e = e;
	}

	public BigInteger encode(){
		// Get byte array from String message.
		messageBigInt = new BigInteger(message.getBytes());
		//while((messageBigInt.compareTo(modulus) != -1) || (messageBigInt.gcd(modulus).compareTo(BigInteger.valueOf(1)) != 0));
		
		// The encoding should satisfy the RSA algorithm condition.
		if((BigInteger.valueOf(1).compareTo(messageBigInt) < 0) 
			&& (messageBigInt.compareTo(modulus) < 0)){} else {
			System.out.println("Incorrect message conversation!");
		}

		// Encode the BigInteger message using the 
		// selected Euler totient and the generated modulus.
		messageCipherText = messageBigInt.modPow(e, modulus);
		
		return messageCipherText;
	}

	public BigInteger decode(BigInteger messageEncoded, BigInteger d){

		// Return the decoded BigInteger message using 
		// the public key and the generated modulus.
		BigInteger messagePlainText = messageEncoded.modPow(d, modulus);

		return messagePlainText;
	}
}