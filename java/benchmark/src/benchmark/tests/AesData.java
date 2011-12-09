/*
 * OPBM's Java Benchmark -- AES data and encryption/decryption init functions
 *
 * This class holds the logic related to setting up everything needed by the
 * AES encrypt/decrypt tests.
 *
 * -----
 * Last Updated:  Oct 6, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.0
 *
 */

 package benchmark.tests;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import benchmark.common.RandomData;


public final class AesData
{
	private static final int		_BASELINE_STRING_LENGTH		= 256;	// One for every ANSI+128 character
    
	/**
	 * Constructor. Builds N-element strings for encryption / decryption
	 */
	public static void initialize()
	{
		m_aesOriginal	= new byte[_NUMBER_OF_AES_STRINGS_TO_ENCODE_AND_DECODE][];
		m_aesEncrypted	= new byte[_NUMBER_OF_AES_STRINGS_TO_ENCODE_AND_DECODE][];

		// Generate a baseline random sequence of 256 alphanumeric characters
		byte m_baseline[] = new byte[_BASELINE_STRING_LENGTH];
		for (int i = 0; i < _BASELINE_STRING_LENGTH; i++)
        {
			m_baseline[ i ] = (byte)( RandomData.m_rdStringBuildBaseline.nextFloat() * 
                              (float)( _BASELINE_STRING_LENGTH - 1 ) );
        }

		// Populate the strings needed for AES encoding from the random baseline
		for( int i = 0; i < m_aesOriginal.length; i++ )
		{	// Populate it with random characters from baseline
			m_aesOriginal[ i ] = new byte[ _AES_STRING_LENGTH ];
			for ( int j = 0; j < _AES_STRING_LENGTH; j++ )
			{	// Grab a character from our pseudo-randomly created list of characters above
				m_aesOriginal[ i ][ j ] = m_baseline[ 
                        (int)( RandomData.m_rdStringCharInBaseline.nextFloat() * 
                        (float)( _BASELINE_STRING_LENGTH - 1 ) ) ];
			}
		}
		// When we get here, our list is populated with random-length text from _MIN_AES_STRING_LENGTH to _MAX_AES_STRING_LENGTH characters in length

    }
        
	/**
	 * Initialization algorithm to setup the AES keys, specs and cipher engine.
	 */
	public static void initializeCipherEncryptionEngine()
	{
        // generate a key
		try {
			m_kgen = KeyGenerator.getInstance("AES");

		} catch (NoSuchAlgorithmException ex) {
			m_isValid = false;
			return;
		}
		// To use 256 bit keys, you need the "unlimited strength" encryption policy files from Sun.
        m_kgen.init(128);
        m_key = m_kgen.generateKey().getEncoded();
        m_skeySpec = new SecretKeySpec(m_key, "AES");

        // build the initialization vector.  Can be bytes of anything
        byte[] iv = { 1, 2, 1, 9, 2, 0, 1, 1, 1, 2, 1, 9, 2, 0, 1, 1 };
        m_ivspec = new IvParameterSpec(iv);
		try {
			m_cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		} catch (NoSuchAlgorithmException ex) {
			m_isValid = false;
			return;
		} catch (NoSuchPaddingException ex) {
			m_isValid = false;
			return;
		}

		// If we get here, we're good
		m_isValid = true;
	}

	public static void setEncryptMode()
	{
		// Set up the cipher engine to be in encrypt mode
		try {
			m_cipher.init(Cipher.ENCRYPT_MODE, m_skeySpec, m_ivspec);

		} catch (InvalidAlgorithmParameterException ex) {
			System.out.println("AES Failure:  Unable to setup the cipher encryption engine.");
			m_isValid = false;
		} catch (InvalidKeyException ex) {
			System.out.println("AES Failure:  Unable to setup the cipher encryption engine.");
			m_isValid = false;
		}
	}

	public static void setDecryptMode()
	{
		// Set up the cipher engine to be in decrypt mode
		try {
			m_cipher.init(Cipher.DECRYPT_MODE, m_skeySpec, m_ivspec);

		} catch (InvalidAlgorithmParameterException ex) {
			System.out.println("AES Failure:  Unable to setup the cipher decryption engine.");
			m_isValid = false;
		} catch (InvalidKeyException ex) {
			System.out.println("AES Failure:  Unable to setup the cipher decryption engine.");
			m_isValid = false;
		}
	}


	public	static	boolean					m_isValid;
	public	static	KeyGenerator			m_kgen;
	public	static	SecretKey				m_skey;
	public	static	byte[]					m_key;
	public	static	SecretKeySpec			m_skeySpec;
	public	static	Cipher					m_cipher;
	public	static	IvParameterSpec			m_ivspec;

	public	static byte[][]					m_aesOriginal;
	public	static byte[][]					m_aesEncrypted;

	// Constants to determine how big m_aesOriginal and m_aesEncrypted should be, as in byte[10000][2048]
	public	static final int				_NUMBER_OF_AES_STRINGS_TO_ENCODE_AND_DECODE = 10000;
	public	static final int				_AES_STRING_LENGTH							= 2048;		// 2KB each
}
