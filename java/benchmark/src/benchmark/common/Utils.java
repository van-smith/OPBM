/*
 * OPBM's Java Benchmark -- Utility functions
 *
 * This class is a helper providing utility functions throughout.
 *
 * Last Updated:  Oct 4, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.2.0
 *
 */

package benchmark.common;

import java.util.UUID;

/**
 * Utility class handling static utility functions.
 */
public class Utils
{
	/**
	 * The UUID string representation is as described by this BNF:
	 *
	 *	 UUID                   = time_low-time_mid-time_high_and_version-variant_and_sequence-node
	 *	 time_low               = 4*<hexOctet>				// 8 bytes
	 *	 time_mid               = 2*<hexOctet>				// 4 bytes
	 *	 time_high_and_version  = 2*<hexOctet>				// 4 bytes
	 *	 variant_and_sequence   = 2*<hexOctet>				// 4 bytes
	 *	 node                   = 6*<hexOctet>				// 12 bytes
	 *	 hexOctet               = <hexDigit><hexDigit>		// 2 bytes
	 *	 hexDigit               = "0" through "f" or "F"	// 1 byte
	 * @return the aforementioned format
	 */
	public static String getUUID()
	{
		return(UUID.randomUUID().toString());
	}
}
