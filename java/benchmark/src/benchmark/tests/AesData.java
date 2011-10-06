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

public class AesData
{
	/**
	 * Constructor. Builds N-element strings for encryption / decryption
	 */
	public static void initialize()
	{
		m_aesOriginal	= new byte[_NUMBER_OF_AES_STRINGS_TO_ENCODE_AND_DECODE][];
		m_aesEncrypted	= new byte[_NUMBER_OF_AES_STRINGS_TO_ENCODE_AND_DECODE][];
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


	public	static boolean				m_isValid;
	public	static KeyGenerator			m_kgen;
	public	static SecretKey			m_skey;
	public	static byte[]				m_key;
	public	static SecretKeySpec		m_skeySpec;
	public	static Cipher				m_cipher;
	public	static IvParameterSpec		m_ivspec;

	public	static byte[][]				m_aesOriginal;
	public	static byte[][]				m_aesEncrypted;

	public	static final int			_NUMBER_OF_AES_STRINGS_TO_ENCODE_AND_DECODE = 10000;
	public	static final int			_AES_STRING_LENGTH							= 2048;		// 2KB each
}
