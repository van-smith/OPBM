/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class of the OPBM.  It creates a GUI, loads
 * necessary files, beings processing based on context, etc.
 *
 * Last Updated:  Aug 01, 2011
 *
 * by Van Smith, Rick C. Hodgin
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @author Rick C. Hodgin
 * @version 1.0.2
 *
 */


////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////// REMEMBER TO FIX THE LOOKUPBOX ENTER-KEY KEYLISTENER ISSUE ///////////
////////// PanelRightListbox & PanelRightLookupbox, August 19, 2011  ///////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

package opbm;

import opbm.common.Xml;
import opbm.common.Macros;
import opbm.common.Settings;
import opbm.common.Commands;
import opbm.resultsviewer.ResultsViewer;
import opbm.panels.PanelRightLookupbox;
import opbm.panels.PanelRight;
import opbm.panels.PanelRightListbox;
import opbm.panels.PanelLeft;
import opbm.panels.PanelFactory;
import opbm.panels.PanelRightItem;
import opbm.dialogs.DroppableFrame;
import opbm.benchmarks.Benchmarks;
import opbm.common.Tuple;
import opbm.common.Utils;
import java.io.*;
import java.awt.*;
import java.awt.Image.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import opbm.benchmarks.BenchmarkParams;
import opbm.common.ModalApp;
import opbm.dialogs.DeveloperWindow;
import opbm.dialogs.OpbmDialog;
import opbm.dialogs.SimpleWindow;
import org.xml.sax.SAXException;


/**
 * Primary Office Productivity Benchmark class deriving everything for the
 * OPBM application from an intelligent assembly of child classes.
 *
 * @author Rick C. Hodgin
 */
public final class Opbm extends	ModalApp
					 implements	AdjustmentListener,
								KeyListener,
								MouseWheelListener,
								ComponentListener
{
//////////
//
// NATIVE functions in opbm64.dll:
//
//////
	static {
		if (System.getProperty("sun.arch.data.model").equals("32"))
		{	// 32-bit JVM
			System.loadLibrary("opbm32");
			System.out.println("Running 32-bit JVM");

		} else {
			// 64-bit JVM
			System.loadLibrary("opbm64");
			System.out.println("Running 64-bit JVM");
		}
	}
	public native static void	sendWindowToForeground(String title);
	// Note:  All of these get__Directory() functions ALWAYS return a path ending in a backslash
	public native static String	getHarnessCSVDirectory();					// Returns c:\\users\\user\\documents\\obbm\\results\\csv\\
	public native static String	getHarnessXMLDirectory();					// Returns c:\\users\\user\\documents\\opbm\\results\\xml\
	public native static String	getHarnessTempDirectory();					// Returns c:\\users\\user\\documents\\opbm\\temp\\
	public native static String	getScriptCSVDirectory();					// Returns c:\\users\\user\\documents\\opbm\\scriptOutput\\
	public native static String	getScriptTempDirectory();					// Returns c:\\users\\user\\documents\\opbm\\scriptOutput\\temp\\
	public native static String	getSettingsDirectory();						// Returns c:\\users\\user\\documents\\opbm\\settings\\
	public native static String	getRunningDirectory();						// Returns c:\\users\\user\\documents\\opbm\\running\\
	public native static String	getCSIDLDirectory(String name);				// Returns directory specified by the CSIDL option
	// End Note
	public native static void	snapshotProcesses();						// Takes a snapshot of the currently running processes
	public native static void	stopProcesses();							// Stops all processes that were not running when the snapshot was taken
	public native static String	GetRegistryKeyValue(String key);			// Requests the registry key value
	public native static String	SetRegistryKeyValueAsString(String key, String value);				// Writes the registry key and value as a REG_SZ
	public native static String	SetRegistryKeyValueAsDword(String key, int value);					// Writes the registry key and value as a REG_DWORD
	public native static String	SetRegistryKeyValueAsBinary(String key, String value, int length);	// Writes the registry key and value as a REG_BINARY
	public native static float	waitUntilSystemIdle(int percent, int durationMS, int timeoutMS);	// Waits up to timeoutMS for a period durationMS long of percent-or-lower total system activity




	/** Constructor creates ArrayList for m_leftPanels and m_navHistory, master
	 * Macros and Commands class objects.
	 *
	 * @param args Allows several switches:
	 *			-font			-- to change the default fonts
	 *			-atom:			-- Execute an atom
	 *			-atom(N):		-- Execute an atom N times
	 *			-trial			-- Execute a Trial Run of the entire benchmark suite
	 *			-official		-- Execute an Official Run of the entire benchmark suite
	 *			-skin			-- Load the simple, Skinned GUI
	 *			-simple			-- Load the Simple, skinned GUI
	 *			-developer		-- Load the Developer GUI
	 */
	@SuppressWarnings("LeakingThisInConstructor")
    public Opbm(String[] args)
	{
		m_opbm = this;
		System.out.println("JVM home: " + m_jvmHome);
		File f = new File(m_jvmHome);
		if (!f.exists())
		{	// Give a warning
			System.out.println("Warning: JVM home does not exist. Use -home:path override");
		}

/*
 * Used for debugging, or reference.  This data comes from the opbm64.dll or opbm32.dll functions:
		System.out.println(" Harness CSV Directory: " + getHarnessCSVDirectory());
		System.out.println(" Harness XML Directory: " + getHarnessXMLDirectory());
		System.out.println("Harness Temp Directory: " + getHarnessTempDirectory());
		System.out.println("  Script CSV Directory: " + getScriptCSVDirectory());
		System.out.println(" Script Temp Directory: " + getScriptTempDirectory());
		System.out.println("     Running Directory: " + getRunningDirectory());
		System.out.println("    Settings Directory: " + getSettingsDirectory());
		System.out.println("    System32 Directory: " + getCSIDLDirectory("SYSTEM"));
 */

		// Make sure we're the only app running
		if (!isModalApp( getHarnessTempDirectory() + "opbm.dat", m_title ))
		{	// Already another app instance running
			System.out.println("Another process is running, bringing it to foreground.");
			sendWindowToForeground(m_title);
			System.exit(-1);
		}

		// Set the necessary startup variables
		m_args						= args;
		m_leftPanels				= new ArrayList<PanelLeft>(0);
		m_navHistory				= new ArrayList<PanelLeft>(0);
		m_editPanels				= new ArrayList<PanelRight>(0);
		m_rawEditPanels				= new ArrayList<PanelRight>(0);
		m_zoomFrames				= new ArrayList<JFrame>(0);
		m_rvFrames					= new ArrayList<DroppableFrame>(0);
		m_tuples					= new ArrayList<Tuple>(0);
		m_macroMaster				= new Macros(this);
		m_benchmarkMaster			= new Benchmarks(this);
		m_settingsMaster			= new Settings(this);
		m_commandMaster				= new Commands(this, m_macroMaster, m_settingsMaster);
		m_executingFromCommandLine	= false;
		m_executingTrialRun			= false;
		m_executingOfficialRun		= false;
		m_executingBenchmarkRunName	= "";


		// If -font option is on command line, use slightly smaller fonts
		// REMEMBER I desire to change this later to use settings.xml file
		// to declare all fonts, then use -font Name to choose a font profile
		// from within the xml file, such as "-font Linux" for fonts that work
		// well with Linux.
		if (args.length != 0 && args[0].toLowerCase().contains("font"))
			m_fontOverride	= true;
		else
			m_fontOverride	= false;

        SwingUtilities.invokeLater(new Runnable()
		{
            @Override
            public void run()
			{
				// Show the GUI, which also loads the scripts.xml, edits.xml and panels.xml (essential files)
				createAndShowGUI();
				// This function exists outside the thead so it blocks the UI until everything is created

				// Create a non-edt thread to allow the GUI to continue starting up and displaying while processing
				Thread t = new Thread("OPBMStartupThread")
				{
					@Override
					public void run()
					{
						m_noExit = false;
						List<String>	args	= new ArrayList<String>(0);
						List<Xml>		list	= new ArrayList<Xml>(0);
						Xml target;
						String line, name, digits;
						int i, j, iterations, runCount;

						// Load the command line options, including those from files, into the execution sequence
						// Arguments get loaded into "List<String> args" rather than m_args[]
						// This allows command-line options that use @filename to be expanded into
						// their individual lines as additional command-line options.
						for (i = 0; i < m_args.length; i++)
						{
							if (m_args[i].startsWith("@"))
							{
								// Load this file's entries
								Opbm.readTerminatedLinesFromFile(m_args[i].substring(1), args);

							} else {
								// Add this option
								args.add(m_args[i]);

							}
						}

						// Look for necessary-to-know-in-advance flags
						for (i = 0; i < args.size(); i++)
						{
							line = args.get(i);
							if (line.toLowerCase().startsWith("-noexit"))
							{	// They don't want to exit when any automated runs are complete
								m_noExit = true;

							} else if (line.toLowerCase().startsWith("-skin") || line.toLowerCase().startsWith("-simple")) {
								// They want to launch the simple skinned window
								showSimpleWindow();

							} else if (line.toLowerCase().startsWith("-developer")) {
								// They want to launch the developer window
								showDeveloperWindow();

							} else if (line.toLowerCase().startsWith("-home:")) {
								// They are overriding the default java.home location for java.exe for the restarter
								m_jvmHome = line.substring(6).replace("\"", "");
								File f = new File(m_jvmHome);
								if (!f.exists())
								{	// The override location does not exist
									System.out.println("Warning: Java.home command-line override \"" + m_jvmHome + "\" does not exist.");
								}

							} else {
								// We don't do anything with other options, they'll be handled below
							}
						}

						// If they specified any command line options, grab them
						runCount = 0;
						for (i = 0; i < args.size(); i++)
						{
							line = args.get(i);
							if (line.toLowerCase().startsWith("-atom("))
							{	// It's an iterative atom count, at least it's supposed to be
								m_executingFromCommandLine = true;
								digits		= Utils.extractOnlyNumbers(line.substring(6));
								iterations	= Integer.valueOf(digits);
								list.clear();
								Xml.getNodeList(list, getScriptsXml(), "opbm.scriptdata.atoms.atom", false);
								if (!list.isEmpty())
								{
									for (j = 0; j < list.size(); j++)
									{
										target	= list.get(j);
										name	= target.getAttribute("name");
										if (name.replace(" ", "").equalsIgnoreCase(line.substring(6 + digits.length() + 2)))
										{
											// This is the benchmark they want to run
											++runCount;
											System.out.println("OPBM command line: Executing Atom \"" + name + "\" for " + digits + " iterations");
											benchmarkRunAtom(target, iterations, false, null, m_opbm, m_macroMaster, m_settingsMaster, "", "", "", "", "", "", "", "", "", "");
										}
									}

								} else {
									System.out.println("OPBM command line: Error loading scripts.xml");
									System.exit(-1);
								}

							} else if (line.toLowerCase().startsWith("-atom:")) {
								// It's an iterative atom count
								// Grab all of the atoms and iterate to find the name of the one we're after
								m_executingFromCommandLine = true;
								list.clear();
								Xml.getNodeList(list, getScriptsXml(), "opbm.scriptdata.atoms.atom", false);
								if (!list.isEmpty())
								{
									for (j = 0; j < list.size(); j++)
									{
										target	= list.get(j);
										name	= target.getAttribute("name");
										if (name.replace(" ", "").equalsIgnoreCase(line.substring(6)))
										{
											// This is the benchmark they want to run
											++runCount;
											System.out.println("OPBM command line: Executing Atom \"" + name + "\"");
											benchmarkRunAtom(target, 1, false, null, m_opbm, m_macroMaster, m_settingsMaster, "", "", "", "", "", "", "", "", "", "");
										}
									}

								} else {
									System.out.println("OPBM command line:  Error loading scripts.xml to obtain list of Atoms");
									System.exit(-1);
								}

							} else if (line.toLowerCase().startsWith("-trial")) {
								// They want to run a trial benchmark run
								m_executingFromCommandLine = true;
								++runCount;
								m_benchmarkMaster.benchmarkTrialRun(true);

							} else if (line.toLowerCase().startsWith("-official")) {
								// They want to run an official benchmark run
								m_executingFromCommandLine = true;
								++runCount;
								m_benchmarkMaster.benchmarkOfficialRun(true);

							} else if (line.toLowerCase().startsWith("-name:")) {
								// They are specifying a name for the run
								m_executingBenchmarkRunName = line.substring(6);
								System.out.println("Benchmark given '" + m_executingBenchmarkRunName + "' name.");

							} else if (line.toLowerCase().startsWith("-noexit")) {
								// handled above in pre-this-loop processing
							} else if (line.toLowerCase().startsWith("-home:")) {
								// handled above in pre-this-loop processing
							} else if (line.toLowerCase().startsWith("-skin") || line.toLowerCase().startsWith("-simple")) {
								// handled above in pre-this-loop processing
							} else if (line.toLowerCase().startsWith("-developer")) {
								// handled above in pre-this-loop processing

							} else {
								// Ignore the unknown option
								System.out.println("Ignoring unknown option: \"" + line + "\"");
							}
						}
						if (!m_noExit && runCount != 0)
						{	// If we get here, they don't want us to exit, or we did a run without any errors and we're ready to exit
							System.exit(0);
						}
						// Done doing command-line things
						m_executingFromCommandLine = false;
					}
				};
				t.start();
            }
        });
	}

	public static void readTerminatedLinesFromFile(String			fileName,
												   List<String>		args)
	{
		File f;
		FileInputStream fi;
		String line;

		try {
			f  = new File(fileName);
			if (!f.exists())
			{	// It doesn't exist in the current directory, see if it exists in tests\
				f = new File("tests\\" + fileName);
				if (!f.exists())
				{	// Nope, unable to find this file
					System.out.println("OPBM command line:  Cannot find \"" + fileName + "\", or \"tests\\" + fileName + "\"");
					return;
				}
			}
			fi = new FileInputStream(f);
			BufferedReader d = new BufferedReader(new InputStreamReader(fi));

			// Read each line in turn
			line = d.readLine();
			while (line != null && !line.isEmpty())
			{
				args.add(line);
				line = d.readLine();
			}
			d.close();

		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
	}

	/**
	 * Creates the Results Viewer
	 */
	public ResultsViewer createAndShowResultsViewer(String resultsXmlFilename)
	{
		int count;
		m_rv = null;

		m_rvsync = 0;
		if (!resultsXmlFilename.isEmpty() && !m_opbm.willTerminateAfterRun())
		{	// We only process real files
			m_rvFilename = resultsXmlFilename;

			// Launch the Results Viewer in another thread (keeps GUI running)
			++m_rvsync;		// Raise the condition of this sync point's use
			Thread t = new Thread("results_viewer_loader")
			{
				@Override
				public void run()
				{
					m_rv = new ResultsViewer(m_opbm, 800, 556, true);
					--m_rvsync;

					// Add the filter tags
					m_rv.addFilterTag("Internet",	"No");
					m_rv.addFilterTag("Disk",		"No");
					m_rv.addFilterTag("Video",		"No");
					m_rv.addFilterTag("Audio",		"No");
					m_rv.addFilterTag("Network",	"No");
					m_rv.addFilterTag("2D",			"No");
					m_rv.addFilterTag("3D",			"No");
					m_rv.addFilterTag("Read",		"No");
					m_rv.addFilterTag("Write",		"No");

					// Attempt to render the file they specified
					if (m_rv.load(m_rvFilename))
						m_rv.render();
				}
			};
			t.start();
		}
		count = 0;
		while (count < 50/* 50*200 = 10 seconds */ && m_rvsync != 0)
		{
			try
			{	// Wait for m_rv to be created and run() to to notify m_rvsync
			   Thread.sleep(200);

			} catch (InterruptedException ex) {
			}
		   ++count;
		}
		return(m_rv);
	}

	/**
	 * Creates the developer window to allow editing of scripts.xml and other
	 * xml files
	 */
	public void createDeveloperWindow()
	{
		m_frameDeveloper = new DeveloperWindow(this, false);
	}

	public void toggleDeveloperWindow()
	{
		if (m_frameDeveloper != null && m_frameDeveloper.isVisible())
		{	// Turn it off
			m_frameDeveloper.setVisible(false);

		} else {
			// Turn it on
			m_frameDeveloper.setVisible(true);

		}
	}

	public void showDeveloperWindow()
	{
		// Show the developer window
		if (m_frameDeveloper != null && !m_frameDeveloper.isVisible())
		{	// Turn it on
			m_frameDeveloper.setVisible(true);
		}

		// Hide the simple window
		if (m_frameSimple != null && m_frameSimple.isVisible())
		{	// Turn it off
			m_frameSimple.setVisible(false);
		}
	}

	public void hideDeveloperWindow()
	{
		// Show the developer window
		if (m_frameDeveloper != null && m_frameDeveloper.isVisible())
		{	// Turn it off
			m_frameDeveloper.setVisible(false);
		}
	}

	/**
	 * Creates the simple skinned interface, which allows for "Trial Run" and
	 * "Official Run", along with links to view previous entries
	 */
	public void createSimpleWindow()
	{
		m_frameSimple = new SimpleWindow(this, false);
	}

	public void toggleSimpleWindow()
	{
		if (m_frameSimple != null && m_frameSimple.isVisible())
		{	// Turn it off
			m_frameSimple.setVisible(false);

		} else {
			// Turn it on
			m_frameSimple.setVisible(true);

		}
	}

	public void showSimpleWindow()
	{
		// Hide the developer window
		if (m_frameDeveloper != null && m_frameDeveloper.isVisible())
		{	// Turn it off
			m_frameDeveloper.setVisible(false);
		}

		// Show the simple window
		if (m_frameSimple != null && !m_frameSimple.isVisible())
		{	// Turn it on
			m_frameSimple.setVisible(true);
		}
	}

	public void hideSimpleWindow()
	{
		// Hide the simple window
		if (m_frameSimple != null && m_frameSimple.isVisible())
		{	// Turn it off
			m_frameSimple.setVisible(false);
		}
	}

	public void showUserWindow()
	{
		// See which one should be visible
		if (m_frameSimple != null && m_settingsMaster.isSimpleSkin())
		{	// We're viewing the simple skin
			m_frameSimple.setVisible(true);

		}

		if (m_frameDeveloper != null && m_settingsMaster.isDeveloperSkin())
		{	// Viewing the developer window
			m_frameDeveloper.setVisible(true);
		}
	}

	/** Self-explanatory.  Builds the GUI for OPBM using a four-panel design:
	 * 1)  Header
	 * 2)  Left panel for navigation
	 * 3)  Right panel for display and editing of controls
	 * 4)  Status bar for displaying tooltips and general information
	 *
	 */
    public void createAndShowGUI()
	{
		createDeveloperWindow();		// For "developer" skin setting in settings.xml
		createSimpleWindow();			// For "simple" skin setting in settings.xml
		showUserWindow();				// Display whichever one should be displayed

		// Load the XML panel content
		if (loadPanelsXml()) {
			// Once the Xml panel content is loaded, create all of the physical panels based on its instruction
			System.out.println("Loaded panels.xml");
			if (PanelFactory.createLeftPanelObjects(this, m_macroMaster, m_frameDeveloper.lblHeader, m_frameDeveloper.statusBar, m_frameDeveloper.panLeft, m_frameDeveloper)) {
				// All default panels are created, render the top-level item
				System.out.println("Created menus and navigation panels");
				if (navigateToLeftPanel("main")) {
					// If we get here, the main navigation panel is displayed and we're still good
					System.out.println("Found main panel");
					if (loadEditsXml()) {
						// We have our edits loaded, we're still good
						System.out.println("Loaded edits.xml");
						if (loadScriptsXml()) {
							// We have our scripts loaded, we're totally good
							System.out.println("Loaded scripts.xml");
							System.out.println("OPBM System Initialization completed successfully");
							// Normal system flow should reach this point
							m_frameDeveloper.statusBar.setText("Loaded panels.xml, edits.xml and scrips.xml okay.");

						} else {
							// Not found or not loaded properly, navigate to the raw editing options
							System.out.println("Unable to load scripts.xml");
							m_frameDeveloper.statusBar.setText("Error loading scripts.xml.  Please repair file manually. " + m_frameDeveloper.m_lastError);
							navigateToLeftPanel("XML File Maintenance");

						}

					} else {
						// Not found or not loaded properly, navigate to the raw editing options
						System.out.println("Unable to load edits.xml");
						m_frameDeveloper.statusBar.setText("Error loading edits.xml.  Please repair file manually. " + m_frameDeveloper.m_lastError);
						navigateToLeftPanel("XML File Maintenance");

					}

				} else {
					// If we get here, the "main" panel wasn'tup found
					System.out.println("Could not find main panel in panels.xml");
					// Display our default panel, which indicates the error condition
					m_frameDeveloper.panLeft.setVisible(true);
				}

			} else {
				// If we get here, the "main" panel wasn't found
				// Display our default panel, which indicates the error condition
				System.out.println("Unable to create main menus and navigation panels");
				m_frameDeveloper.panLeft.setVisible(true);
			}

		} else {
			// If we get here, the "main" panel wasn'tup found
			System.out.println("Unable to load panels.xml");
			// Display our default panel, which indicates the error condition
			m_frameDeveloper.statusBar.setText("Error loading panels.xml.  Please exit application and repair file manually. " + m_frameDeveloper.m_lastError);
			m_frameDeveloper.panLeft.setVisible(true);
		}
    }

	/** Called to navigate to a panel by name.  Appends new navigation to an
	 * internal navigation history for traversing backwards using successive
	 * "Back" commands.
	 *
	 * @param name name of panel to navigate to
	 * @return true of false if navigation was successful
	 */
	public boolean navigateToLeftPanel(String name)
	{
		PanelLeft p;
		int i, j;

		for (i = 0; i < m_leftPanels.size(); i++) {
			p = m_leftPanels.get(i);
			if (p.getName().equalsIgnoreCase(name)) {
				// This is the one, navigate here
				// Make the new one visible before making the old one invisible (so there's no flicker)
				for (j = 0; j < m_leftPanels.size(); j++) {
					if (j != i) {
						// Make this one invisible
						m_leftPanels.get(j).navigateAway();
					}
				}
				m_navHistory.add(p);
				p.setVisible(true);
				p.navigateTo(m_macroMaster);
				return(true);
			}
		}
		return(false);
	}

	/**
	 * When the left panel (menu) is updated, by toggling a macro or clicking
	 * on some link that will change the display, we need to rebuild it.
	 */
	public void refreshLeftPanelsAfterMacroUpdate()
	{
		int i;

		for (i = 0; i < m_leftPanels.size(); i++)
		{	// Update every menu in turn, so when they are redisplayed, they are updated
			m_leftPanels.get(i).refreshAfterMacroUpdate();
		}
	}

	/** Navigate backward in the chain of navigated panels.
	 *
	 * @return true or false if navigation backward was possible
	 */
	public boolean navigateBack()
	{
		PanelLeft p;
		int i;

		if (m_navHistory.isEmpty() || m_navHistory.size() == 1) {
			// Nothing to navigate back to
			return(false);
		}
		// Remove the last one
		m_navHistory.remove(m_navHistory.size() - 1);
		p = m_navHistory.get(m_navHistory.size() - 1);

		// Make sure everything else is hidden
		for (i = 0; i < m_leftPanels.size(); i++) {
			if (!m_leftPanels.get(i).equals(p)) {
				m_leftPanels.get(i).navigateAway();
			}
		}
		// Make the last one visible
		p.navigateTo(m_macroMaster);
		return(true);
	}

	/** Adds the specified <code>RightPanel</code> edit object to the list of
	 * known <code>RightPanel</code>s that have been created.
	 *
	 * @param pr <code>RightPanel</code> object to add
	 */
	public void addEditToRightPanelList(PanelRight pr) {
		m_editPanels.add(pr);
	}

	/** Adds the specified <code>RightPanel</code> rawedit object to the list of
	 * known <code>RightPanel</code>s that have been created.
	 *
	 * @param pr <code>RightPanel</code> object to add
	 */
	public void addRawEditToRightPanelList(PanelRight pr)
	{
		m_rawEditPanels.add(pr);
	}

	/** Called to navigate away from a named edit rightpanel.  Used to sync
	 * menu navigation with rightpanel objects associated with the menu's
	 * action, such as "Save and Close" or "Close".
	 *
	 * @param name rightpanel name to close
	 * @param destroy true or false should this rightpanel be destroyed?
	 * @return true or false if object was found and removed
	 */
	public boolean navigateAwayEdit(String name, boolean destroy)
	{
		PanelRight pr;
		int i;

		for (i = 0; i < m_editPanels.size(); i++) {
			pr = m_editPanels.get(i);
			if (pr.getName().equalsIgnoreCase(name)) {
				// This is the match
				pr.doPostCommand();
				pr.navigateAway();
				if (destroy) {
					m_editPanels.remove(i);
					m_editActive = null;
				}
				m_frameDeveloper.panRight.setVisible(true);
				return(true);
			}
		}
		return(false);
	}

	/**
	 * Called to save the contents of an edit, which saves everything on the
	 * specified edit physically to disk.
	 */
	public void editSave()
	{
		if (m_editActive != null)
			m_editActive.save(false);
	}

	/**
	 * Closes the edit without saving.
	 */
	public void editClose()
	{
		if (m_editActive != null) {
			navigateAwayEdit(m_editActive.getName(), true);
		}
	}

	/** Called to navigate away from a named rawedit rightpanel.  Used to sync
	 * menu navigation with rightpanel objects associated with the menu's
	 * action, such as "Save and Close" or "Close".
	 *
	 * @param name rightpanel name to close
	 * @param destroy true or false should this rightpanel be destroyed?
	 * @return true or false if object was found and removed
	 */
	public boolean navigateAwayRawEdit(String name, boolean destroy)
	{
		PanelRight pr;
		int i;

		for (i = 0; i < m_rawEditPanels.size(); i++) {
			pr = m_rawEditPanels.get(i);
			if (pr.getName().equalsIgnoreCase(name)) {
				// This is the match, do its closing command, and then navigate away
				pr.doPostCommand();
				pr.navigateAway();
				if (destroy) {
					m_rawEditPanels.remove(i);
					m_rawEditActive = null;
				}
				m_frameDeveloper.panRight.setVisible(true);
				return(true);
			}
		}
		return(false);
	}

	/** Identifies the active edit object as the current one being displayed.
	 * Used to allow the macro $active_edit$ to return a valid name.
	 *
	 * @param name name of <code>RightPanel</code> edit to activate
	 * @return true or false if object was found and set active
	 */
	public boolean navigateToEdit(String name)
	{
		PanelRight pr;
		int i;

		for (i = 0; i < m_editPanels.size(); i++) {
			pr = m_editPanels.get(i);
			if (pr.getName().equalsIgnoreCase(name)) {
				m_frameDeveloper.panRight.setVisible(false);
				pr.setVisible(true);
				pr.navigateTo();
				m_editActive = pr;
				return(true);
			}
		}
		return(false);
	}

	/** Identifies the active rawedit object as the current one being displayed.
	 * Used to allow the macro $active_rawedit$ to return a valid name.
	 *
	 * @param name name of <code>RightPanel</code> rawedit to activate
	 * @return true or false if object was found and set active
	 */
	public boolean navigateToRawEdit(String name)
	{
		PanelRight pr;
		int i;

		for (i = 0; i < m_rawEditPanels.size(); i++) {
			pr = m_rawEditPanels.get(i);
			if (pr.getName().equalsIgnoreCase(name)) {
				m_frameDeveloper.panRight.setVisible(false);
				pr.setVisible(true);
				pr.navigateTo();
				m_rawEditActive = pr;
				return(true);
			}
		}
		return(false);
	}

	/**
	 * Called to save the contents of a rawedit physically to disk.
	 */
	public void rawEditSave()
	{
		if (m_rawEditActive != null)
			m_rawEditActive.save(true);
	}

	/**
	 * Saves the contents of the rawedit physically to disk, then navigates away
	 * from the panel, closing it.
	 */
	public void rawEditSaveAndClose()
	{
		if (m_rawEditActive != null) {
			m_rawEditActive.save(true);
			navigateAwayRawEdit(m_rawEditActive.getName(), true);
		}
	}

	/**
	 * Closes the rawedit without saving.
	 */
	public void rawEditClose()
	{
		if (m_rawEditActive != null) {
			navigateAwayRawEdit(m_rawEditActive.getName(), true);
		}
	}


	/** Navigate backward in the chain to top-most panel (first menu).
	 *
	 * @return true or false if navigation home was possible
	 */
	public boolean navigateHome()
	{
		int i;

		if (m_navHistory.isEmpty()) {
			// Nothing to navigate to
			return(false);
		}

		// Remove all but the home one
		for (i = m_navHistory.size() - 1; i > 0; i--)
			m_navHistory.remove(i);

		// Make the home one visible
		m_navHistory.get(0).navigateTo(m_macroMaster);
		return(true);
	}

	/** Called to obtain the name of the panel immediately previous to the
	 * current one.  Used for building contextual tooltip texts based on
	 * "wherever we are".
	 *
	 * @return Name of the immediately previous panel
	 */
	public String previousPanel()
	{
		if (m_navHistory.size() <= 1) {
			// Nothing to navigate back to before this one, so no valid name
			return("previous");
		}
		return(m_navHistory.get(m_navHistory.size() - 2).getName());
	}

	/** Called to obtain the name of the panel immediately at the start of
	 * the navigation list.  Used for building contextual tooltip texts based
	 * on "going to home from wherever we are".
	 *
	 * @return Name of home panel
	 */
	public String homePanel()
	{
		return(m_navHistory.get(0).getName());
	}

	/**
	 * Loads the panels.xml file as a W3C DOM object, converts it to a
	 * logically structured link-list of Xml class objects which are far
	 * easier to navigate generally.
	 *
	 * @return true of false if the panels.xml file was loaded
	 */
	private boolean loadPanelsXml()
	{
		m_panelXml = loadXml("panels.xml");
		return(m_panelXml != null);
	}

	/**
	 * Loads the edits.xml file as a W3C DOM object, converts it to a
	 * logically structured link-list of Xml class objects which are far
	 * easier to navigate generally.
	 *
	 * @return true of false if the edits.xml file was loaded
	 */
	private boolean loadEditsXml()
	{
		m_editXml = loadXml("edits.xml");
		return(m_editXml != null);
	}

	/**
	 * Loads the scripts.xml file as a W3C DOM object, converts it to a
	 * logically structured link-list of Xml class objects which are far
	 * easier to navigate generally.
	 *
	 * @return true of false if the scripts.xml file was loaded
	 */
	private boolean loadScriptsXml()
	{
		m_scriptXml = loadXml("scripts.xml");
		return(m_scriptXml != null);
	}

	/**
	 * Physically loads the specified XML file as a W3C DOM object, converts
	 * it to a logically structured link-list of Xml class objects which are far
	 * easier to navigate generally.
	 *
	 * @return valid or null if the file was loaded
	 */
	public static Xml loadXml(String fileName)
	{
		Xml root;
		File panelsXmlFile;
		String fileToProcess;
		DocumentBuilderFactory factory;
		DocumentBuilder bldPanelsXml;
		Document docPanelsXml;

		root			= null;
		// If the filename contains a \ character, we assume it's a fully qualified path, otherwise we search our path for it
		panelsXmlFile	= new File(fileName.contains("\\") ? fileName : locateFile(fileName));
		fileToProcess	= panelsXmlFile.getAbsolutePath();
		try {
			factory			= DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			bldPanelsXml	= factory.newDocumentBuilder();
			docPanelsXml	= bldPanelsXml.parse(fileToProcess);

			// Convert Java's w3c dom model to something more straight-forward and usable
			root			= Xml.processW3cNodesIntoXml(null, docPanelsXml.getChildNodes());

		} catch (ParserConfigurationException ex) {
			m_lastStaticError = ex.getMessage();

		} catch (SAXException ex) {
			m_lastStaticError = ex.getMessage();

		} catch (IOException ex) {
			m_lastStaticError = ex.getMessage();

		}
		return(root);	// success if root was defined
	}

	/**
	 * Attempts to locate the specified filename.  Files are located in one
	 * of a few places for this app.
	 *
	 * @param fileName file being searched for
	 * @return path of file if found, or original filename if not found
	 */
	public static String locateFile(String fileName)
	{
		File f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13;
		String path;

		f1	= new File("./"								+ fileName);
		f2	= new File("./resources/"					+ fileName);
		f3	= new File("./src/resources/"				+ fileName);
		f4	= new File(getSettingsDirectory()			+ fileName);
		f5	= new File("./resources/xmls/"				+ fileName);
		f6	= new File("./src/resources/xmls/"			+ fileName);
		f7	= new File("./resources/backgrounds/"		+ fileName);
		f8	= new File("./src/resources/backgrounds/"	+ fileName);
		f9	= new File("./resources/graphics/"			+ fileName);
		f10	= new File("./src/resources/graphics/"		+ fileName);
		f11	= new File("./resources/masks/"				+ fileName);
		f12	= new File("./src/resources/masks/"			+ fileName);
		f13	= new File("./src/resources/masks/simple/"	+ fileName);

		do {
			if (f1.exists()) {
				path = f1.getAbsolutePath();
				break;
			}
			if (f2.exists()) {
				path = f2.getAbsolutePath();
				break;
			}
			if (f3.exists()) {
				path = f3.getAbsolutePath();
				break;
			}
			if (f4.exists()) {
				path = f4.getAbsolutePath();
				break;
			}
			if (f5.exists()) {
				path = f5.getAbsolutePath();
				break;
			}
			if (f6.exists()) {
				path = f6.getAbsolutePath();
				break;
			}
			if (f7.exists()) {
				path = f7.getAbsolutePath();
				break;
			}
			if (f8.exists()) {
				path = f8.getAbsolutePath();
				break;
			}
			if (f9.exists()) {
				path = f9.getAbsolutePath();
				break;
			}
			if (f10.exists()) {
				path = f10.getAbsolutePath();
				break;
			}
			if (f11.exists()) {
				path = f11.getAbsolutePath();
				break;
			}
			if (f12.exists()) {
				path = f12.getAbsolutePath();
				break;
			}
			if (f13.exists()) {
				path = f13.getAbsolutePath();
				break;
			}
			// We didn't find the file.
			// Default to the regular filename (for error reporting)
			path = fileName;
			break;
		} while (false);
		// Regardless of whether it was found or not, return the result
		return(path);
	}

	/**
	 * Called to edit data in the scripts.xml file.  Lists all options at the
	 * specified level (by name), and presents an edit screen as defined by the
	 * loaded edits from edits.xml.
	 *
	 * @param name name of opbm.scriptdata.* to list and edit
	 * @return true of false if edit was found in edits.xml
	 */
	public boolean edit(String name)
	{
		PanelRight panel = PanelFactory.createRightPanelFromEdit(name, this, m_macroMaster, m_commandMaster, m_frameDeveloper.lblHeader, m_frameDeveloper.statusBar, m_frameDeveloper.panRight, m_frameDeveloper, "", "");
		if (panel == null) {
			m_frameDeveloper.statusBar.setText("Error: Unable to edit " + name + ".");
			return(false);

		} else {
			// It was found and created
			addEditToRightPanelList(panel);
			panel.doPreCommand();
			navigateToEdit(panel.getName());

		}
		return(true);
	}

	/**
	 * Called to raw edit the file specified.  Presents the user with a full
	 * page editbox of its contents.
	 *
	 * @param fileName name of file to edit
	 * @return true of false if file was found
	 */
	public boolean rawedit(String fileName)
	{
		PanelRight panel = PanelFactory.createRightPanelFromRawEdit(fileName, this, m_macroMaster, m_commandMaster, m_frameDeveloper.lblHeader, m_frameDeveloper.statusBar, m_frameDeveloper.panRight, m_frameDeveloper);
		if (panel == null) {
			m_frameDeveloper.statusBar.setText("Error: Unable to load " + fileName + " for editing.");
			return(false);

		} else {
			// It was found and created
			addRawEditToRightPanelList(panel);

			// Populate its contents
			panel.load(true);
			panel.doPreCommand();
			navigateToRawEdit(panel.getName());

		}
		return(true);
	}

	/** Called to resize everything when the user resizes the window.
	 *
	 */
	public void resizeEverything()
	{
		int i;

		m_frameDeveloper.resizeEverything();

		// Resize the navigation panels
		for (i = 0; i < m_leftPanels.size(); i++) {
			m_leftPanels.get(i).afterWindowResize(m_frameDeveloper.panRight.getX() - 1,
												  m_frameDeveloper.panRight.getHeight());
		}

		// Resize the active edits (if any)
		if (m_rawEditActive != null) {
			m_rawEditActive.afterWindowResize(m_frameDeveloper.panRight.getWidth(),
											  m_frameDeveloper.panRight.getHeight());
		}

		if (m_editActive != null) {
			m_editActive.afterWindowResize(m_frameDeveloper.panRight.getWidth(),
										   m_frameDeveloper.panRight.getHeight());
		}
		m_frameDeveloper.repaint();
	}

	/** Called when the scrollbar position moves.
	 *
	 * @param ae system adjustment event
	 */
	@Override
	public void adjustmentValueChanged(AdjustmentEvent ae) {
		// Called when scrollbar scrolls
	}

	/** Not used.  Required for use of ComponentListener.
	 *
	 */
	@Override
	public void componentHidden(ComponentEvent evt) {
	}
	/** Not used.  Required for use of ComponentListener.
	 *
	 */
	@Override
	public void componentShown(ComponentEvent evt) {
	}
	/** Not used.  Required for use of ComponentListener.
	 *
	 */
	@Override
	public void componentMoved(ComponentEvent evt) {
	}

	/** Called when the base newFrame is resized.  Calls
	 * <code>resizeEverything()</code>
	 *
	 */
	@Override
	public void componentResized(ComponentEvent evt)
	{
	// Called when the newFrame (window) is resized
		if (evt.getComponent().equals(m_frameDeveloper))
		{	// Resize everything on the developer window
			m_frameDeveloper.componentResized(evt);
			resizeEverything();
		}
	}

	/** Not used.  Required definition for KeyListener.
	 *
	 * @param e system key event
	 */
	@Override
	public void keyTyped(KeyEvent e) {
    }

	/** Not currently used.  Required definition for KeyListener.
	 *
	 * @param e system key event
	 */
	@Override
    public void keyPressed(KeyEvent e) {
    }

	/** Not used.  Required definition for KeyListener.
	 *
	 * @param e system key event
	 */
	@Override
    public void keyReleased(KeyEvent e) {
    }

	/** Called when the user wheels over the newFrame.
	 *
	 * @param e system mouse wheel event
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
	// For mouse-wheel events
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
//			int newPosition = scrollbarV.getValue() + e.getUnitsToScroll() * 16;
			if (e.getUnitsToScroll() < 0) {
				// Moving down (toward 0)
//				scrollbarV.setValue(Math.max(newPosition, scrollbarV.getMinimum()));
			}
			else {
				// Moving up (toward largest value)
//				scrollbarV.setValue(Math.min(newPosition, scrollbarV.getMaximum()));

			}
		}
	}

	public void listBoxAddCommand()
	{
		if (m_editActive != null)
			m_editActive.listBoxAddCommand();
	}

	public void listBoxDeleteCommand()
	{
		if (m_editActive != null)
			m_editActive.listBoxDeleteCommand();
	}

	public void listBoxCloneCommand()
	{
		if (m_editActive != null)
			m_editActive.listBoxCloneCommand();
	}

	public void listBoxCommand(String				command,
							   PanelRightListbox	source)
	{
		if (m_editActive != null)
			m_editActive.listboxCommand(command, source);
	}

	/**
	 * Called when the user clicks on the add button on a
	 * <code>_TYPE_LOOKUPBOX</code>
	 *
	 * @param whereFrom name of the lookupbox control being added from
	 * @param whereTo name of the listbox or lookupbox being added to
	 */
	public void lookupboxAddCommand(PanelRightLookupbox source,
									String				whereTo,
									String				after,
									String				whereFrom)
	{
		if (m_editActive != null)
			m_editActive.lookupboxAddCommand(source, whereTo, after, whereFrom);
	}

	public void lookupboxCommand(String					command,
								 PanelRightLookupbox	source)
	{
		if (m_editActive != null)
			m_editActive.lookupboxCommand(command, source);
	}

	public void lookupboxZoomCommand(PanelRightLookupbox	source,
									 String					editName,
									 String					zoomFields,
									 String					dataSource)
	{
		if (m_editActive != null)
			m_editActive.lookupboxZoomCommand(source, editName, zoomFields, dataSource);
	}

	public Xml getListboxOrLookupboxSelectedItem(PanelRightLookupbox source)
	{
		if (m_editActive != null)
			return(m_editActive.getLookupboxSelectedItemByObject(source));
		else
			return(null);
	}

	/**
	 * Search for the specified named lookupbox, and tell it to update itself.
	 * @param name
	 */
	public void lookupboxUpdateCommand(String name)
	{
		if (m_editActive != null)
		{
			m_editActive.lookupboxUpdateCommand(name);
		}
	}

	/**
	 * Creates a managed window relative to the <code>PanelRight</code> panel
	 * for size, color, etc.
	 *
	 * @param panelRef <code>PanelRight</code> with which to copy attributes from
	 * @param name window title
	 * @return new <code>DroppableFrame</code>
	 */
	public DroppableFrame createZoomWindow(PanelRight	panelRef,
										   String		name)
	{
		int actualWidth, actualHeight;
		Dimension size;

		DroppableFrame fr = new DroppableFrame(this, true, false);
		fr.setTitle("Zoom: " + name);
        fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		fr.setSize(panelRef.getWidth(), panelRef.getHeight());

		fr.pack();
        Insets fi = fr.getInsets();
		actualWidth		= panelRef.getWidth()  + fi.left + fi.right;
		actualHeight	= panelRef.getHeight() + fi.top  + fi.bottom;
        size = new Dimension(actualWidth, actualHeight);
        fr.setMinimumSize(size);
        fr.setMaximumSize(size);
        fr.setPreferredSize(size);
        fr.setSize(size);

        fr.setLocationRelativeTo(null);		// Center window on desktop
        fr.setLayout(null);					// We handle all redraws

		Container c = fr.getContentPane();
        c.setBackground(panelRef.getBackColor());
		c.setForeground(panelRef.getForeColor());

		return(fr);
	}

	public void addZoomWindow(JFrame fr)
	{
		if (!m_zoomFrames.contains(fr))
			m_zoomFrames.add(fr);
	}

	public void removeZoomWindow(JFrame fr)
	{
		if (fr != null)
		{
			fr.dispose();
		}
		m_zoomFrames.remove(fr);
	}

	public void closeAllZoomWindows()
	{
		int i;

		for (i = m_zoomFrames.size() - 1; i >= 0; i--)
		{
			m_zoomFrames.get(i).dispose();
		}
	}

	public void addTuple(Tuple t)
	{
		m_tuples.add(t);
	}

	public Tuple findTuple(String uuid)
	{
		int i;

		for (i = 0; i < m_tuples.size(); i++)
		{
			if (m_tuples.get(i).getUUID().equalsIgnoreCase(uuid))
				return(m_tuples.get(i));
		}
		return(null);
	}

	public Tuple deleteTuple(String uuid)
	{
		int i;

		for (i = 0; i < m_tuples.size(); i++)
		{
			if (m_tuples.get(i).getUUID().equalsIgnoreCase(uuid))
				m_tuples.remove(i);
		}
		return(null);
	}

	/** Called from various locations to process a command through the Macros
	 * <code>processCommand</code> feature.
	 *
	 * Note:  May not be needed in the near future as current plans include
	 * everything including a passed parameter to reach their own commandMaster
	 * variable.
	 *
	 * @param commandOriginal command to execute
	 * @param p1 first parameter of command
	 * @param p2 second parameter of command
	 * @param p3 third parameter of command
	 * @param p4 fourth parameter of command
	 * @param p5 fifth parameter of command
	 * @param p6 sixth parameter of command
	 * @param p7 seventh parameter of command
	 * @param p8 eighth parameter of command
	 * @param p9 ninth parameter of command
	 * @param p10 tenth parameter of command
	 */
	public void processCommand(String commandOriginal,
							   String p1,
							   String p2,
							   String p3,
							   String p4,
							   String p5,
							   String p6,
							   String p7,
							   String p8,
							   String p9,
							   String p10)
	{
		m_commandMaster.processCommand(this, commandOriginal, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
	}

	/**
	 * Initializes the response entry in the dialog tuple, so it can be accessed
	 * by the readDialog() code, or for other purposes
	 * @param id identifier to associate with this dialog input
	 * @param triggerCommand triggers the command specified once the
	 * OpbmInput dialog sets something
	 */
	public void initializeDialogResponse(String		id,
										 String		triggerCommand)
	{
		int i;

		if (m_dialogTuple == null)
			m_dialogTuple = new Tuple(this);

		for (i = 0; i < m_dialogTuple.size(); i++)
		{
			if (m_dialogTuple.getFirst(i).equalsIgnoreCase(id))
			{	// Found it
				m_dialogTuple.setSecond(i, "Unanswered");
				m_dialogTuple.setThird(i, "");
				m_dialogTuple.setTriggerCommand(i, triggerCommand);
				return;
			}
		}
		// If we get here, it wasn't found, add it
		i = m_dialogTuple.add(id, "Unanswered", "");
		m_dialogTuple.setTriggerCommand(i, triggerCommand);	// command to execute
		m_dialogTuple.setTriggerFilters(i, "3");			// when 3rd item is updated
	}

	/**
	 * When the dialog box closes, it sets the userAction (which button was
	 * pressed)
	 * @param id identifier associated with the dialog
	 * @param userAction user action (text on the button, generally speaking)
	 */
	public void setDialogResponse(String	id,
								  String	userAction)
	{
		int i;

		if (m_dialogTuple == null)
			initializeDialogResponse(id, "");

		for (i = 0; i < m_dialogTuple.size(); i++)
		{
			if (m_dialogTuple.getFirst(i).equalsIgnoreCase(id))
			{	// Found it
				m_dialogTuple.setSecond(i, userAction);
				return;
			}
		}
		// If we get here, it wasn't found, add it, and try again
		initializeDialogResponse(id, "");
		setDialogResponse(id, userAction);
	}

	/**
	 * When the input box closes, it sets the user action (which button was
	 * pressed) and the data that was in the input box when it was pressed
	 * @param id identifier associated with this input
	 * @param userAction user action (text on the button, generally speaking)
	 * @param data whatever the user had input in the input box when the button
	 * was pressed
	 */
	public void setDialogResponse(String	id,
								  String	userAction,
								  String	data)
	{
		int i;

		if (m_dialogTuple == null)
			initializeDialogResponse(id, "");

		for (i = 0; i < m_dialogTuple.size(); i++)
		{
			if (m_dialogTuple.getFirst(i).equalsIgnoreCase(id))
			{	// Found it
				m_dialogTuple.setSecond(i, userAction);
				m_dialogTuple.setThird(i, data);
				return;
			}
		}
		// If we get here, it wasn't found, add it, and try again
		initializeDialogResponse(id, "");
		setDialogResponse(id, userAction, data);
	}

	/**
	 * Returns the user's response (which button they pressed)
	 * @param id
	 * @return
	 */
	public String getDialogResponse(String id)
	{
		String result;
		int i;

		for (i = 0; i < m_dialogTuple.size(); i++)
		{
			if (m_dialogTuple.getFirst(i).equalsIgnoreCase(id))
			{	// Found it
				result = (String)m_dialogTuple.getSecond(i);
				return(result == null ? "" : result);
			}
		}
		// Not found
		return("--not found--");
	}

	/**
	 * Returns the user's response (which button they pressed)
	 * @param id
	 * @return
	 */
	public void clearDialogResponse(String id)
	{
		int i;

		for (i = 0; i < m_dialogTuple.size(); i++)
		{
			if (m_dialogTuple.getFirst(i).equalsIgnoreCase(id))
			{	// Found it
				m_dialogTuple.remove(i);
				return;
			}
		}
	}

	/**
	 * Returns the data item (input box) text that was recorded when the user
	 * pressed the button
	 * @param id
	 * @return
	 */
	public String getDialogResponseData(String id)
	{
		int i;

		for (i = 0; i < m_dialogTuple.size(); i++)
		{
			if (m_dialogTuple.getFirst(i).equalsIgnoreCase(id))
			{	// Found it
				return((String)m_dialogTuple.getThird(i));
			}
		}
		// Not found
		return("--not found--");
	}

	public void setTrialRun()
	{
		m_executingTrialRun		= true;
		m_executingOfficialRun	= false;
	}

	public void setOfficialRun()
	{
		m_executingTrialRun		= false;
		m_executingOfficialRun	= true;
	}

	public void setRunFinished()
	{
		m_executingTrialRun		= false;
		m_executingOfficialRun	= false;
	}

	public String getRunType()
	{
		if (m_executingOfficialRun)
			return("official");

		else if (m_executingTrialRun)
			return("trial");

		else
			return("manual");
	}

	public void setRunName(String name)
	{
		m_executingBenchmarkRunName = name;
	}

	public String getRunName()
	{
		return(m_executingBenchmarkRunName);
	}

	public boolean isExecutingTrialRun()
	{
		return(m_executingTrialRun);
	}

	public boolean isExecutionOfficialRun()
	{
		return(m_executingOfficialRun);
	}

	/**
	 * Open a web browser with the specified parameters
	 *
	 * @param p1 first parameter (typically the url)
	 * @param p2 second parameter (typically ignored)
	 * @param p3 third parameter
	 * @param p4 fourth parameter
	 * @param p5 fifth parameter
	 * @param p6 sixth parameter
	 * @param p7 seventh parameter
	 * @param p8 eighth parameter
	 * @param p9 ninth parameter
	 * @param p10 tenth parameter
	 */
	public void webBrowser(String p1,
						   String p2,
						   String p3,
						   String p4,
						   String p5,
						   String p6,
						   String p7,
						   String p8,
						   String p9,
						   String p10)
	{
		try {

			Utils.launchWebBrowser((p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10).trim());

		} catch (Exception ex1) {
			try {

				Utils.launchWebBrowser(p1);

			} catch (Exception ex2) {
			}
		}
	}

	public Settings getSettingsMaster()
	{
		return(m_settingsMaster);
	}

	public Macros getMacroMaster()
	{
		return(m_macroMaster);
	}

	public Benchmarks getBenchmarkMaster()
	{
		return(m_benchmarkMaster);
	}

	public Commands getCommandMaster()
	{
		return(m_commandMaster);
	}

	public void benchmarkLaunchTrialRun(boolean automated)
	{
		m_benchmarkMaster.benchmarkLaunchTrialRun(automated);
	}

	public void benchmarkLaunchOfficialRun(boolean automated)
	{
		m_benchmarkMaster.benchmarkLaunchOfficialRun(automated);
	}

	public void benchmarkRunAtom(Xml			atom,
								 int			iterations,
								 boolean		openInNewThread,
								 PanelRightItem	pri,
								 Opbm			opbm,
								 Macros			macroMaster,
								 Settings		settingsMaster,
								 String			p1,
								 String			p2,
								 String			p3,
								 String			p4,
								 String			p5,
								 String			p6,
								 String			p7,
								 String			p8,
								 String			p9,
								 String			p10)
	{
		m_bm_atom			= atom;
		m_bm_iterations		= iterations;
		m_bm_pri			= pri;
		m_bm_opbm			= opbm;
		m_bm_macroMaster	= macroMaster;
		m_bm_settingsMaster	= settingsMaster;
		m_bm_p1				= p1;
		m_bm_p2				= p2;
		m_bm_p3				= p3;
		m_bm_p4				= p4;
		m_bm_p5				= p5;
		m_bm_p6				= p6;
		m_bm_p7				= p7;
		m_bm_p8				= p8;
		m_bm_p9				= p9;
		m_bm_p10			= p10;

		if (openInNewThread)
		{
			// Since the benchmark uses an overlay heads-up-display,
			// it needs to be off the EDT thread, so we give it its own thread.
			Thread t = new Thread("OPBM_Benchmark_Thread")
			{
				@Override
				public void run()
				{
					runAtom();
				}
			};
			t.start();

		} else {
			runAtom();
		}
	}

	public void runAtom()
	{
		m_benchmarkMaster.benchmarkInitialize(m_bm_macroMaster,
											  m_bm_settingsMaster);

		if (m_bm_atom == null && m_bm_pri != null)
		{
			m_bm_atom = m_benchmarkMaster.loadAtomFromPanelRightItem(m_bm_pri,
																	 m_bm_p1, m_bm_p2, m_bm_p3, m_bm_p4, m_bm_p5,
																	 m_bm_p6, m_bm_p7, m_bm_p8, m_bm_p9, m_bm_p10);
		}

		m_benchmarkMaster.benchmarkRunAtom(m_bm_atom, m_bm_iterations);
		m_benchmarkMaster.benchmarkShutdown();
	}

	Xml				m_bm_atom;
	int				m_bm_iterations;
	PanelRightItem	m_bm_pri;
	Opbm			m_bm_opbm;
	Macros			m_bm_macroMaster;
	Settings		m_bm_settingsMaster;
	String			m_bm_p1;
	String			m_bm_p2;
	String			m_bm_p3;
	String			m_bm_p4;
	String			m_bm_p5;
	String			m_bm_p6;
	String			m_bm_p7;
	String			m_bm_p8;
	String			m_bm_p9;
	String			m_bm_p10;

	/** Calls <code>Macros.parseMacros()</code>
	 *
	 * Note:  May no longer be needed as everything now includes a passed
	 * parameter to reach their own Macros class (macroMaster variable).
	 *
	 * @param candidate string which may contain macros to expand
	 * @return string with any macros expanded or replaced
	 */
	public String expandMacros(String candidate)
	{
		return(m_macroMaster.parseMacros(candidate));
	}

	/** Adds a panel to the m_leftPanels ArrayList.
	 *
	 */
	public void addPanelLeft(PanelLeft p)
	{
		m_leftPanels.add(p);
	}

	/** Returns the root of the loaded panels.xml file in its logically
	 * structured link-list Xml class hierarchy.
	 *
	 * @return Xml root for panels.xml
	 */
	public Xml getPanelXml()
	{
		return(m_panelXml);
	}

	/** Returns the root of the loaded edits.xml file in its logically
	 * structured link-list Xml class hierarchy.
	 *
	 * @return Xml root for edits.xml
	 */
	public Xml getEditXml()
	{
		return(m_editXml);
	}

	/** Returns the root of the loaded scripts.xml file in its logically
	 * structured link-list Xml class hierarchy.
	 *
	 * @return Xml root for scripts.xml
	 */
	public Xml getScriptsXml()
	{
		return(m_scriptXml);
	}

	public PanelRight getActiveEdit()
	{
		return(m_editActive);
	}

	public PanelRight getActiveRawEdit()
	{
		return(m_rawEditActive);
	}

	public void updateEditListboxesAndLookupboxes()
	{
		if (m_editActive != null) {
			m_editActive.updateEditListboxesAndLookupboxes();
		}
	}

	/**
	 * Indicates whether or not the -font command line switch was used.
	 *
	 * @return true or false, should font be overridden?
	 */
	public boolean isFontOverride()
	{
		return(m_fontOverride);
	}

	/**
	 * Examines the registry keys to see if User Account Control is enabled,
	 * which will prevent unsigned executables from running.
	 * @return
	 */
	public static boolean isUACEnabled()
	{
		String value;

		value = Opbm.GetRegistryKeyValue("HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System\\EnableLUA");
		try {
			if (Integer.valueOf(value) != 0)
				return(true);	// UAC is enabled
		} catch (Throwable t) {
			// Probably a number exception, but should not happen as this registry key is a DWORD
		}
		// If we get here, not enabled or an error (which we then assume not enabled)
		return(false);
	}

	/**
	 * Debugging tool, enable or disable debugging
	 * @param enabled
	 */
	public static void setBreakpointsEnabled(boolean enabled)
	{
		m_breakpointsEnabled = enabled;
	}

	/**
	 * Debugging too, queries current setting of breakpoints
	 * @return
	 */
	public static boolean areBreakpointsEnabled()
	{
		return(m_breakpointsEnabled);
	}

	/**
	 * Returns the JFrame of the main GUI
	 * @return
	 */
	public DroppableFrame getGUIFrame()
	{
		return(m_frameDeveloper);
	}

	/**
	 * Adds the frame to a list so that it can be closed when starting a new
	 * benchmark run
	 * @param frame
	 */
	public void addResultsViewerToQueue(DroppableFrame frame)
	{
		int i;

		for (i = 0; i < m_rvFrames.size(); i++)
		{
			if (m_rvFrames.get(i).equals(frame))
			{	// It's already in our list
				return;
			}
		}
		// If we get here, it's not in our list, add it
		m_rvFrames.add(frame);
	}

	/**
	 * Removes the entry from the queue
	 * @param frame
	 */
	public void removeResultsViewerFromQueue(DroppableFrame frame)
	{
		int i;

		for (i = 0; i < m_rvFrames.size(); i++)
		{
			if (m_rvFrames.get(i).equals(frame))
			{	// It's here, delete it
				m_rvFrames.remove(i);
			}
		}
	}

	/**
	 * Called when a benchmark run starts to close all the results viewer windows
	 */
	public void closeAllResultsViewerWindowsInQueue()
	{
		int i;

		for (i = m_rvFrames.size() - 1; i >= 0; i--)
		{	// Close and remove them from this list (so they won't show up again and will be garbage collected)
			m_rvFrames.get(i).dispose();
			m_rvFrames.remove(i);
		}
	}

	/**
	 * Called when a benchmark run starts to hide all the results viewer windows
	 */
	public void hideAllResultsViewerWindowsInQueue()
	{
		int i;

		for (i = 0; i < m_rvFrames.size(); i++)
			m_rvFrames.get(i).setVisible(false);
	}

	/**
	 * Called when a benchmark ends to restore all the results viewer windows
	 */
	public void showAllResultsViewerWindowsInQueue()
	{
		int i;

		for (i = 0; i < m_rvFrames.size(); i++)
			m_rvFrames.get(i).setVisible(true);
	}

	/**
	 * Asks the user for a directory, and loads all results*.xml files, adding
	 * up every timing point contained within, and producing an average and
	 * output file called results_averages.xml in the same directory.
	 */
	public void computeResultsXmlAverages()
	{
		int i, j, totalFiles, totalEntries;
		String fileName, directoryName, entry, atomName, timingName, timingSeconds, outputLine;
		JFileChooser chooser;
		String[] files;
		File candidate;
		Xml results, timing;
		List<Xml> resultsData;
		List<Xml> resultItems;
		Tuple compiledResults;
		Tuple detailedResults;
		BenchmarkParams bp;
		OpbmDialog od;
		FilenameFilter fileFilter;
		javax.swing.filechooser.FileFilter directoryFilter;
		List<String> output;


		// Include only those files which are results*.xml
		fileFilter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return(name.toLowerCase().startsWith("results") && name.toLowerCase().endsWith(".xml"));
			}
		};

		// Include only those files which are results*.xml
		directoryFilter = new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File file)
			{
				return(file.isDirectory());
			}
			@Override
			public String getDescription() {
				return("Directory");
			}
		};


		// Ask the user for the directory
		chooser = new JFileChooser();
		chooser.setFileFilter(directoryFilter);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if (chooser.showOpenDialog(m_frameDeveloper) == JFileChooser.APPROVE_OPTION)
		{
			directoryName	= Utils.verifyPathEndsInBackslash(chooser.getSelectedFile().getAbsolutePath());
			candidate		= new File(directoryName);
			System.out.println("Loading results*.xml files from " + directoryName + " for average.");

			// Get a list of all matching files in the list
			files = candidate.list(fileFilter);
			if (files != null)
			{	// Process each file in the list
				bp = new BenchmarkParams();

				// Create the master list to be used for all data gathering
				// resultsData (allocated below, once for each file)
				// holds a full list of pointers to each score line

				compiledResults = new Tuple(this);
				// The tuple array contains one entry per file, with these data items in these element locations:
				//		first	= fileName
				//		second	= resultsData ArrayList of raw Xml entries for the file
				//		third	= Tuple containing:
				//					first	= name of timing item
				//					second	= timing
				//					third	= results entry for this item

				// Iterate through every file
				totalFiles		= 0;
				totalEntries	= 0;
				for (i = 0; i < files.length; i++)
				{	// Get filename of file or directory
					fileName	= directoryName + files[i];
					candidate	= new File(fileName);
					if (candidate.exists() && candidate.isFile() && candidate.canRead())
					{	// Try to load as an Xml
						results = loadXml(fileName);
						if (results != null)
						{	// Process its data elements
							++totalFiles;

							// Grab the list of nodes
							resultsData = new ArrayList<Xml>(0);			// Holds the list of opbm.rawdata.run.results nodes in the file
							Xml.getNodeList(resultsData, results, "opbm.rawdata.run.results", false);
							if (!resultsData.isEmpty())
							{	// There is at least one entry in this file

								// Create the entries for this listing
								resultItems		= new ArrayList<Xml>(0);	// Holds the scoring data lines from each result*.xml file that's loaded
								detailedResults	= new Tuple(this);			// Holds the detailed items for each Xml entry broken out
								compiledResults.add(fileName, resultItems, detailedResults);
								for (j = 0; j < resultsData.size(); j++)
								{	// Grab all of the named elements that are not total lines
									results = resultsData.get(j);
									timing = results.getChildNode("timing");
									while (timing != null)
									{	// Process every entry that's not a timing line
										entry = timing.getText();
										if (!entry.toLowerCase().startsWith("total,"))
										{	// Save this entry
											++totalEntries;
											resultItems.add(timing);
											bp.extractTimingLineElements(timing.getText());
											detailedResults.add(bp.m_timingName, Double.toString(bp.m_timingInSeconds), results);
										}

										// Move to next sibling
										timing = timing.getNext();
									}
									// When we get here, this entry's timing elements have been exhausted
									// Proceed to the next results entry
								}
								// When we get here, all results for this file have been consumed
								// We don't clear up the results Xml that was loaded, because
								// it has elements referenced above

							} else {
								System.out.println("No opbm.rawdata.run.results elements found in " + fileName);
							}
							// When we get here, we're ready to try the next file

						} else {
							System.out.println("Unable to load " + fileName);
						}

					} else {
						System.out.println("Ignoring " + fileName + ", not found.");
					}
					// When we get here, we're ready to try the next file
				}
				// When we get here, we've tried all files
				if (totalFiles == 0)
				{	// Nothing to do
					od = new OpbmDialog(m_opbm, "Directory had no files with opbm.rawdata.run.results elements.", "Error", OpbmDialog._OKAY_BUTTON, "", "");
					return;
				}
				// If we get here, there is some data to process
				output = new ArrayList<String>(0);
				// Generate an output of everything we have
				for (i = 0; i < compiledResults.size(); i++)
				{	// Generate output from each entry
					fileName		= compiledResults.getFirst(i);
					resultItems		= (List<Xml>)compiledResults.getSecond(i);
					detailedResults	= (Tuple)compiledResults.getThird(i);
					for (j = 0; j < detailedResults.size(); j++)
					{	// Build the line for this entry
						timingName		= (String)detailedResults.getFirst(j);
						timingSeconds	= (String)detailedResults.getSecond(j);
						atomName		= ((Xml)detailedResults.getThird(j)).getAttribute("name");
						outputLine		= atomName + "," + timingName + "," + timingSeconds;
						output.add(outputLine);
					}
					// When we get here, all entries for this file are generated
					// Proceed to the next file
				}
				// When we get here, the output list is populated
				fileName = directoryName + "output.csv";
				Utils.writeTerminatedLinesToFile(fileName, output);
				od = new OpbmDialog(m_opbm, "Compiled results to " + fileName, "Success", OpbmDialog._OKAY_BUTTON, "", "");
				return;
			}
			// Directory contains no results*.xml files
			od = new OpbmDialog(m_opbm, "Directory contains no results*.xml files", "Error", OpbmDialog._OKAY_BUTTON, "", "");
			return;
		}
	}

	/**
	 * When a command line sequence is run, this variable is set high.
	 * @return yes or no if we're running a sequence from the command line
	 */
	public boolean isExecutingFromCommandLine()
	{
		return(m_executingFromCommandLine);
	}

	/**
	 * Based on a combination of factors.  If running a manual benchmark,
	 * then will not be terminating afterward.  If running from the command
	 * line then they will be terminating unless they've specified the -noexit
	 * command line parameter.
	 * @return
	 */
	public boolean willTerminateAfterRun()
	{
		if (m_executingFromCommandLine)
			return(!m_noExit);
		else
			return(false);
	}

	/**
	 * Returns the encoded final static version string, which is the build
	 * date and time.
	 */
	public String getVersion()
	{
		return(m_version);
	}

	/**
	 * Returns the encoded final static title string, which includes the build
	 * date and time.
	 */
	public String getAppTitle()
	{
		return(m_title);
	}

	/** Main app entry point.
	 *
	 * @param args command line parameters
	 */
    public static void main(String[] args)
	{
		// Launch the system
        Opbm o = new Opbm(args);
    }

	/**
	 * Master instance created in main()
	 */
	private Opbm					m_opbm;

	/**
	 * Holds the command line arguments for processing after the invokeLater() runnable
	 */
	private String[]				m_args;

	/**
	 * Root node of the edits.xml data that is loaded at startup
	 */
	private	Xml						m_editXml;

	/**
	 * Root node of the panels.xml data that is loaded at startup
	 */
	private	Xml						m_panelXml;

	/**
	 * Root node of the scripts.xml data that is loaded at startup
	 */
	private	Xml						m_scriptXml;

	/**
	 * Raw pool of all loaded left-side panels, whether they are displayed or not
	 */
	private List<PanelLeft>			m_leftPanels;

	/**
	 * Chain of panels as they're navigated to/through in real-time
	 */
	private List<PanelLeft>			m_navHistory;

	/**
	 * Raw pool of edit rightpanels as created
	 */
	private List<PanelRight>		m_editPanels;

	/**
	 * Raw pool of rawedit rightpanels as created
	 */
	private List<PanelRight>		m_rawEditPanels;

	/**
	 * Master list of tuples used throughout the system, referenced by name
	 */
	private List<Tuple>				m_tuples;

	/**
	 * Holds current active edit <code>PanelRight</code> object being displayed.
	 * Used to make $active_edit$ macro work.
	 */
	private PanelRight				m_editActive;

	/**
	 * Holds current active_rawedit <code>PanelRight</code> object being displayed.
	 * Used to make $active_rawedit$ macro work.
	 */
	private PanelRight				m_rawEditActive;

	/**
	 * Handles all macros used in this system, updated with live data while
	 * running.  Note:  Does not need to be a non-static class, but was setup
	 * that way for possible future extensibility.
	 */
	private Macros					m_macroMaster;

	/**
	 * Handles all benchmark runs
	 */
	private Benchmarks				m_benchmarkMaster;

	/**
	 * Handles all settings processing.
	 */
	private Settings				m_settingsMaster;

	/**
	 * Handles all command processing.  Note:  Does not need to be a non-static
	 * class, but was setup that way for possible future extensibility.
	 */
	private Commands				m_commandMaster;

	/**
	 * Command line switch "-font" can be used to indiate not running in
	 * Windows.  Used to switch default font from Calibri to Arial.
	 */
	private boolean					m_fontOverride;

	/**
	 * Reference to the main newFrame used for visualization in this application.
	 * Refer to the <code>DroppableFrame</code> class.  This newFrame supports
	 * four primary components:
	 *
	 *		1)  Header
	 *		2)	Left-panel
	 *		3)	Right-panel
	 *		4)  Status bar
	 *
	 * These elements are always present, though they change through the real-
	 * time use of the system by users.
	 */
    private DeveloperWindow			m_frameDeveloper;
	private SimpleWindow			m_frameSimple;

	private List<JFrame>			m_zoomFrames;
	private List<DroppableFrame>	m_rvFrames;		// Holds list of open windows, closed automatically at benchmark run
	private ResultsViewer			m_rv;
	private String					m_rvFilename;
	private boolean					m_executingFromCommandLine;
	private boolean					m_executingTrialRun;
	private boolean					m_executingOfficialRun;
	private String					m_executingBenchmarkRunName;
	private boolean					m_noExit;						// Was the -noexit command line option specified?
	private Tuple					m_dialogTuple;

	/**
	 * An internal debugger flag, to determine if certain breakpoints used during development should be stopped at or not
	 */
	public static boolean			m_breakpointsEnabled;
	public static String			m_lastStaticError;
// REMEMBER for non-Windows based java runtimes, this logic will need to be changed
// REMEMBER there's also a registry key for this item which may need to be used instead (for custom installations of Java), though for the standard installations from Oracle, this should always work:
	public static String			m_jvmHome				= Utils.getPathToJavaDotExe();

	// Synchronization items used for various wait-until-all-parts-are-completed operations
	public volatile static int		m_rvsync = 0;		// Used by createAndShowResultsViewer

	// Used for the build-date and time
	public final static String		m_version				= "Built 2011.08.22 05:19am";
	public final static String		m_title					= "OPBM - Office Productivity Benchmark - " + m_version;
}
