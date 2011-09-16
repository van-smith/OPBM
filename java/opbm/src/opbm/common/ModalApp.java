/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is used at startup to lock exclusive access to the specified
 * filename.  By using this method, other instances of the app which try to
 * launch and access the same filename will be unable to do so, thereby
 * failing, and will call a function to send the current instance to the
 * foreground, to simulate the launch.
 *
 * Last Updated:  Sep 12, 2011
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

package opbm.common;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class ModalApp
{
	public static boolean isModalApp(String		fileName,
									 String		widnowTitle)
	{
		try {
			final File file = new File( fileName );
			final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
			final FileLock fileLock = randomAccessFile.getChannel().tryLock();
			if (fileLock != null)
			{
				Runtime.getRuntime().addShutdownHook(
				new Thread()
				{
					@Override
					public void run() {
						try {
							fileLock.release();
							randomAccessFile.close();
							file.delete();
						} catch (Exception e) {
						}
					}
				});
				// If we get here, we are the first to lock/access this file
				return true;
			}

		} catch (Exception e) {
		}
		// If we get here, unable to lock the file, not a modal app
		// See if we can send a message to the other window to bring it foreground
		return false;
	}
}
