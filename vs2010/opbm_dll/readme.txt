Information about the projects/directories used by this solution:

cpu			- main class used by WaitUntilIdle(), does the physical work of examining system usage
opbm		- project for opbm.dll, used by AutoIt scripts and the LoadPlugin() function
opbm64		- project for opbm64.dll and opbm32.dll (depending on target build), used by Java's JNI
              engine for Opbm's Java-based interface to Windows functions Win32/WOW64
restarter	- project used by opbm.jar, handles restarting of JVM after reboot

Note:	It is likely that the installer will need to have a 32-bit version of this DLL as well,
		and load it appropriately based on the target machine.
				 
Note:	So far we are targeting only 64-bit Windows 7, which will run/load 32-bit software as well.

- Rick C. Hodgin
- August 17, 2011
