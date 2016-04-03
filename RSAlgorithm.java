/*
 * Georgi Butev | LSBU | NOS | Stavros Dimitriou
 * RSA Algorithm.
 * Generate prime numbers p and q.
 * Calculate their modulus and Phi.
 * Generate public and secret key exponent.
 */

import java.math.BigInteger;
import java.util.Random;
import java.util.Calendar;

public class RSAlgorithm{
	
	private static BigInteger p;
	private static BigInteger q;
	private static BigInteger modulus;
	private static Random random;
	//private static String phiChar = "\u03D5";
	private static BigInteger phi;
	private static BigInteger e; // Public exponent
	private static BigInteger d; // Secret exponent
	
	RSAlgorithm(){
		// Used to generate pseudo random numbers of 48 bit length.
		random = new Random();
		// Generate two prime numbers of 1024 bit length and random number seed.
		p = p.probablePrime(1024, random);
		q = q.probablePrime(1024, random);
		// Calculate the product of the two primes.
		modulus = p.multiply(q);
		// Phi is calculated using BigInteger specific arithmetic methods.
		phi = (p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)))); // Dividing p/p and q/q does not work!
		// Get current time instance and display the time before calculating e.
		Calendar start = Calendar.getInstance();
		System.out.println("Generating public exponent started at: " 
			+ start.get(Calendar.HOUR) + ":" +
			+ start.get(Calendar.MINUTE) + ":" +
			+ start.get(Calendar.SECOND));
		// Loop until Euler totient of 2048 bits and random seed
		// satisfies the RSA algorithm condition.
		do{
			e = e.probablePrime(2048, random);
		} while((BigInteger.valueOf(1).compareTo(e) < 0) 
			&& (e.compareTo(phi) < 0) 
			&& (e.gcd(phi).equals(BigInteger.valueOf(1))));
		// Get current time instance and display the time after calculating e.
		Calendar finish = Calendar.getInstance();
		System.out.println("Generating public exponent finished at: " 
			+ finish.get(Calendar.HOUR) + ":"
			+ finish.get(Calendar.MINUTE) + ":"
			+ finish.get(Calendar.SECOND));
		// The public key exponent is calculated using mod inverse.
		d = e.modInverse(phi);
		// Notify the user if d does not meet the RSA algorithm condition.
		if((BigInteger.valueOf(1).compareTo(d) < 0)	
			&& (d.compareTo(phi) < 0)){} else{
			System.out.println("Incorrect public exponent!");
		}
		// Print out all RSA algorithm variables.
		System.out.printf("p:\n%d\n", p);
		System.out.printf("q:\n%d\n", q);
		System.out.printf("Modulus:\n%d\n", modulus);
		System.out.printf("Phi:\n%d\n", phi);
		System.out.printf("Public exponent:\n%d\n", e);
		System.out.printf("Private exponent:\n%d\n", d);
		/*
		Cipher cipher = new Cipher(modulus, e);
		BigInteger messageEncoded = cipher.encode();
		System.out.println("Encoded Message: \n" + messageEncoded);
		BigInteger messageDecoded = cipher.decode(messageEncoded, d);
		System.out.println("Decoded Message: \n" + (new String(messageDecoded.toByteArray()))); // Needs toByteArray, else we get only digits.
		*/
	}

	public BigInteger getModulus(){
		// Used for both public and private key pair.
		return modulus;
	}

	public BigInteger getPublicKey(){
		// Used for public key pair.
		return d;
	}

	public BigInteger getPrivateKey(){
		// Used for private key pair.
		return e;
	}
}