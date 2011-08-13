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
import opbm.common.DroppableFrame;
import opbm.benchmarks.Benchmarks;
import opbm.common.Tupel;
import opbm.common.Utils;
import java.io.*;
import java.awt.*;
import java.awt.Image.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import opbm.common.ModalApp;
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
	public native static void sendWindowToForeground(String title);
	public native static String getHarnessCSVDirectory();			// Returns c:\\users\\user\\documents\\obbm\\results\\csv\\
	public native static String getHarnessXMLDirectory();			// Returns c:\\users\\user\\documents\\opbm\\results\\xml\
	public native static String getHarnessTempDirectory();			// Returns c:\\users\\user\\documents\\opbm\\temp\\
	public native static String getScriptCSVDirectory();			// Returns c:\\users\\user\\documents\\opbm\\scriptOutput\\
	public native static String getScriptTempDirectory();			// Returns c:\\users\\user\\documents\\opbm\\scriptOutput\\temp\\
	public native static String getSettingsDirectory();				// Returns c:\\users\\user\\documents\\opbm\\settings\\
	public native static void snapshotProcesses();					// Takes a snapshot of the currently running processes
	public native static void stopProcesses();						// Stops all processes that were not running when the snapshot was taken




	/** Constructor creates ArrayList for m_leftPanels and m_navHistory, master
	 * Macros and Commands class objects.
	 *
	 * @param args Allows one switch, -font to change the default fonts
	 */
    public Opbm(String[] args)
	{
/*
 * Used for debugging, or reference.  This data comes from the opbm64.dll or opbm32.dll functions:
		System.out.println(" Harness CSV Directory: " + getHarnessCSVDirectory());
		System.out.println(" Harness XML Directory: " + getHarnessXMLDirectory());
		System.out.println("Harness Temp Directory: " + getHarnessTempDirectory());
		System.out.println("  Script CSV Directory: " + getScriptCSVDirectory());
		System.out.println(" Script Temp Directory: " + getScriptTempDirectory());
		System.out.println("    Settings Directory: " + getSettingsDirectory());
*/

		// Make sure we're the only app running
		if (!isModalApp( "opbm.dat", m_title ))
		{	// Already another app instance running
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
		m_tupels					= new ArrayList<Tupel>(0);
		m_macroMaster				= new Macros(this);
		m_benchmarkMaster			= new Benchmarks();
		m_settingsMaster			= new Settings();
		m_commandMaster				= new Commands(this, m_macroMaster, m_settingsMaster);
		m_executingFromCommandLine	= false;

		// Make sure we have a results\ directory from wherever we are
		Utils.verifyDirectoryExists(new File("results\\.").getAbsolutePath());


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

				// Create a non-edt thread to allow the GUI to continue starting up and displaying while processing
				Thread t = new Thread("OPBM_Command_Line_Thread")
				{
					@Override
					public void run()
					{
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

						// If they specified any command line options, grab them
						m_executingFromCommandLine	= !args.isEmpty();
						runCount					= 0;
						for (i = 0; i < args.size(); i++)
						{
							line = args.get(i);
							if (line.toLowerCase().startsWith("-atom("))
							{	// It's an iterative atom count, at least it's supposed to be
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

							} else {
								// Ignore the unknown option
								System.out.println("Ignoring unknown option: \"" + line + "\"");
							}
						}
						if (runCount != 0)
						{
							// If we get here, we did not have an error, and we're ready to exit
							System.exit(0);
						}
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
	public void createAndShowResultsViewer(String resultsXmlFilename)
	{
		if (!resultsXmlFilename.isEmpty() && !m_executingFromCommandLine)
		{	// We only process real files
			m_rvFilename = resultsXmlFilename;

			// Launch the Results Viewer in another thread (keeps GUI running)
			Thread t = new Thread("results_viewer_loader")
			{
				@Override
				public void run()
				{
					m_rv = new ResultsViewer(m_opbm, 800, 556, true);

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
		Dimension prefSize;

		m_width = 1300;
		m_height = 700 + /* statusBar.getHeight() is not yet assigned */ 16;
        frame = new DroppableFrame(this, false);
		frame.setTitle( m_title );

		// Compute the actual size we need for our window, so it's properly centered
		frame.pack();
        Insets fi		= frame.getInsets();
		m_actual_width	= m_width  + fi.left + fi.right;
		m_actual_height	= m_height + fi.top  + fi.bottom;
        frame.setSize(m_width  + fi.left + fi.right,
					  m_height + fi.top  + fi.bottom);

        prefSize = new Dimension(m_width  + fi.left + fi.right,
								 m_height + fi.top  + fi.bottom);
        frame.setMinimumSize(prefSize);
        frame.setPreferredSize(prefSize);

        prefSize = new Dimension(m_width  + fi.left + fi.right,
								 m_height + fi.top  + fi.bottom);
        frame.setMinimumSize(prefSize);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(m_width, m_height);
        frame.setLocationRelativeTo(null);  // Center window
        frame.setLayout(null);				// We handle all redraws
		Container c = frame.getContentPane();
        c.setBackground(new Color(120, 120, 120));
		c.setForeground(Color.WHITE);

        createStatusBar();
		statusBar.setVisible(true);
		frame.setStatusBar(statusBar);
		frame.addKeyListener(this);
		frame.addMouseWheelListener(this);
		frame.addComponentListener(this);

        frame.add(statusBar);
        frame.setVisible(true);

		// Create the header image
		lblHeader = new JLabel();
		try {
			lblHeader.setIcon(new ImageIcon(ImageIO.read(new File(locateFile("header.png")))));

		} catch (IOException ex) {
			// Nothing to do really, indicates an improper installation
			m_lastError = ex.getMessage();
			lblHeader.setText("OPBM - Office Productivity Benchmark");
		}
		lblHeader.setBackground(Color.BLACK);
		lblHeader.setForeground(Color.WHITE);
		lblHeader.setOpaque(true);
		lblHeader.setBounds(0, 0, m_width, 50);
		lblHeader.setHorizontalAlignment(JLabel.LEFT);
		lblHeader.setVisible(true);
		frame.add(lblHeader);

		// Create the left panel (displayed only if panels.xml doesn'tup load properly)
		panLeft = new JPanel();
		panLeft.setOpaque(true);
		panLeft.setBackground(new Color(130,130,130));
		panLeft.setForeground(Color.WHITE);
		panLeft.setLayout(null);
		panLeft.setVisible(false);
		panLeft.setBounds(0, lblHeader.getHeight(), 250, m_height - lblHeader.getHeight() - statusBar.getHeight());

		// Add default objects for when the loading the panel
		Font f;
		if (isFontOverride())
			f	= new Font("Arial", Font.PLAIN, 16);
		else
			f	= new Font("Calibri", Font.PLAIN, 18);

		JLabel l1 = new JLabel("Error loading panels.xml");
		l1.setBounds(5, 5, 240, 25);
		l1.setForeground(new Color(255,255,128));
		l1.setFont(f);
		l1.setHorizontalAlignment(JLabel.LEFT);
		l1.setVisible(true);

		JLabel l2 = new JLabel("Please correct");
		l2.setBounds(5, 30, 240, 25);
		l2.setForeground(new Color(255,255,128));
		l2.setFont(f);
		l2.setHorizontalAlignment(JLabel.LEFT);
		l2.setVisible(true);

		panLeft.add(l1);
		panLeft.add(l2);
		frame.add(panLeft);

		// Create the right panel
		panRight = new JPanel();
		panRight.setOpaque(true);
		panRight.setBackground(new Color(209,200,172));
		panRight.setForeground(new Color(97,93,80));
		panRight.setBounds(251, lblHeader.getHeight(), m_width - panLeft.getWidth(), m_height - lblHeader.getHeight() - statusBar.getHeight());
		panRight.setLayout(null);
		panRight.setVisible(true);


//////////
// OPBM
//   by Cana Labs,
//      Cossatot Analytics Laboratories, LLC.
//
// (c) 2011.
//
// Written by:
//   Van Smith, Rick C. Hodgin
/////
		l1 = new JLabel("<html><font size=\"+2\"><b>OPBM</b></font><br><i><font size=\"-1\">&nbsp;&nbsp;&nbsp;by Cana Labs,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cossatot Analytics Laboratories, LLC.<br><br>&nbsp;&nbsp;&nbsp;<i>(c) 2011.<br><br></i></font><b>Written by:</b><font size=\"-1\"><i><br>&nbsp;&nbsp;&nbsp;Van Smith, Rick C. Hodgin<br><br><br><br><br>&nbsp</i></font>");
		l1.setBounds(0, 0, panRight.getWidth(), panRight.getHeight());
		l1.setBackground(panRight.getBackground());
		l1.setForeground(panRight.getForeground());
		l1.setFont(f);
		l1.setHorizontalAlignment(JLabel.CENTER);
		l1.setVerticalAlignment(JLabel.CENTER);
		l1.setVisible(true);
		panRight.add(l1);

		frame.add(panRight);

		// Load the XML panel content
		if (loadPanelsXml()) {
			// Once the Xml panel content is loaded, create all of the physical panels based on its instruction
			if (PanelFactory.createLeftPanelObjects(this, m_macroMaster, lblHeader, statusBar, panLeft, frame)) {
				// All default panels are created, render the top-level item
				if (navigateToLeftPanel("main")) {
					// If we get here, the main navigation panel is displayed and we're still good
					if (loadEditsXml()) {
						// We have our edits loaded, we're still good
						if (loadScriptsXml()) {
							// We have our scripts loaded, we're totally good
							// Normal system flow should reach this point
							statusBar.setText("Loaded panels.xml, edits.xml and scrips.xml okay.");

						} else {
							// Not found or not loaded properly, navigate to the raw editing options
							statusBar.setText("Error loading scripts.xml.  Please repair file manually. " + m_lastError);
							navigateToLeftPanel("XML File Maintenance");

						}

					} else {
						// Not found or not loaded properly, navigate to the raw editing options
						statusBar.setText("Error loading edits.xml.  Please repair file manually. " + m_lastError);
						navigateToLeftPanel("XML File Maintenance");

					}

				} else {
					// If we get here, the "main" panel wasn'tup found
					// Display our default panel, which indicates the error condition
					panLeft.setVisible(true);
				}

			} else {
				// If we get here, the "main" panel wasn'tup found
				// Display our default panel, which indicates the error condition
				panLeft.setVisible(true);
			}

		} else {
			// If we get here, the "main" panel wasn'tup found
			// Display our default panel, which indicates the error condition
			statusBar.setText("Error loading panels.xml.  Please exit application and repair file manually. " + m_lastError);
			panLeft.setVisible(true);
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
				panRight.setVisible(true);
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
				panRight.setVisible(true);
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
				panRight.setVisible(false);
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
				panRight.setVisible(false);
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
			m_lastError = ex.getMessage();

		} catch (SAXException ex) {
			m_lastError = ex.getMessage();

		} catch (IOException ex) {
			m_lastError = ex.getMessage();

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
		File f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12;
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
		PanelRight panel = PanelFactory.createRightPanelFromEdit(name, this, m_macroMaster, m_commandMaster, lblHeader, statusBar, panRight, frame, "", "");
		if (panel == null) {
			statusBar.setText("Error: Unable to edit " + name + ".");
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
		PanelRight panel = PanelFactory.createRightPanelFromRawEdit(fileName, this, m_macroMaster, m_commandMaster, lblHeader, statusBar, panRight, frame);
		if (panel == null) {
			statusBar.setText("Error: Unable to load " + fileName + " for editing.");
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

	/**
	 * Creates the statusBar, which appears at the bottom of the screen.
	 *
	 */
    private void createStatusBar()
	{
        statusBar = new Label();
        statusBar.setBackground(Color.LIGHT_GRAY);
        statusBar.setForeground(Color.BLACK);
        Dimension d = new Dimension(frame.getWidth(), 16);
        statusBar.setMinimumSize(d);
        statusBar.setMaximumSize(d);
        statusBar.setPreferredSize(d);
		statusBar.setSize(d);
		statusBar.setLocation(0, frame.getHeight() - 16);
		statusBar.addKeyListener(this);
		statusBar.addMouseWheelListener(this);
    }

	/** Called to resize everything when the user resizes the window.
	 *
	 */
	public void resizeEverything()
	{
		int i;

		// Reposition/size the statusBar
		statusBar.setLocation(0, m_height - statusBar.getHeight());
		statusBar.setSize(m_width, statusBar.getHeight());
		statusBar.repaint();

		// Reposition/size the scrollBarV
		lblHeader.setSize(m_width, lblHeader.getHeight());
		lblHeader.repaint();

		// Resize the right panel
		panRight.setSize(m_width - panRight.getX(),
						 m_height - panRight.getY() - statusBar.getHeight());

		// Resize the navigation panels
		for (i = 0; i < m_leftPanels.size(); i++) {
			m_leftPanels.get(i).afterWindowResize(panRight.getX() - 1,
												  panRight.getHeight());
		}

		// Resize the active edits (if any)
		if (m_rawEditActive != null) {
			m_rawEditActive.afterWindowResize(panRight.getWidth(),
											  panRight.getHeight());
		}

		if (m_editActive != null) {
			m_editActive.afterWindowResize(panRight.getWidth(),
										   panRight.getHeight());
		}
		frame.repaint();
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
		Dimension newSize = ((Component)evt.getSource()).getSize();
		Insets fi = frame.getInsets();
		m_actual_width	= (int)newSize.getWidth();
		m_actual_height	= (int)newSize.getHeight();
		m_width			= m_actual_width  - fi.left - fi.right;
		m_height		= m_actual_height - fi.top  - fi.bottom;
		resizeEverything();
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
									String				whereFrom,
									boolean				allowCustoms)
	{
		if (m_editActive != null)
			m_editActive.lookupboxAddCommand(source, whereTo, after, whereFrom, allowCustoms);
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

	public void saveCustomCommand(String tupelUuid)
	{
		JFrame fr;
		PanelRight pr;
		Tupel tup;

		tup = findTupel(tupelUuid);
		if (tup != null)
		{
			fr = (JFrame)tup.getSecond("frame");
			pr = (PanelRight)tup.getSecond("panelright");
			if (pr != null)
			{
				pr.saveCustomCommand(tup);
				// Close the window
				removeZoomWindow(fr);
			}
		}
	}

	public void cancelCustomCommand(String tupelUuid)
	{
		JFrame fr;
		Tupel tup;

		tup = findTupel(tupelUuid);
		if (tup != null)
		{
			fr = (JFrame)tup.getSecond("frame");
			removeZoomWindow(fr);
		}
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

		DroppableFrame fr = new DroppableFrame(this, true);
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

	public void hideMainPanel()
	{
		frame.setVisible(false);
	}

	public void showMainPanel()
	{
		frame.setVisible(true);
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

	public void addTupel(Tupel t)
	{
		m_tupels.add(t);
	}

	public Tupel findTupel(String uuid)
	{
		int i;

		for (i = 0; i < m_tupels.size(); i++)
		{
			if (m_tupels.get(i).getName().equalsIgnoreCase(uuid))
				return(m_tupels.get(i));
		}
		return(null);
	}

	public Tupel deleteTupel(String uuid)
	{
		int i;

		for (i = 0; i < m_tupels.size(); i++)
		{
			if (m_tupels.get(i).getName().equalsIgnoreCase(uuid))
				m_tupels.remove(i);
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

	public void initializeDialogResponse(String id)
	{
		int i;

		if (m_dialogTupel == null)
			m_dialogTupel = new Tupel(this);

		for (i = 0; i < m_dialogTupel.size(); i++)
		{
			if (m_dialogTupel.getFirst(i).equalsIgnoreCase(id))
			{	// Found it
				m_dialogTupel.setSecond(i, "Unanswered");
				return;
			}
		}
		// If we get here, it wasn't found, add it
		m_dialogTupel.add(id, "Unanswered");
	}

	public void setDialogResponse(String	id,
								  String	userAction)
	{
		int i;

		if (m_dialogTupel == null)
			initializeDialogResponse(id);

		for (i = 0; i < m_dialogTupel.size(); i++)
		{
			if (m_dialogTupel.getFirst(i).equalsIgnoreCase(id))
			{	// Found it
				m_dialogTupel.setSecond(i, userAction);
				return;
			}
		}
		// If we get here, it wasn't found, add it, and try again
		initializeDialogResponse(id);
		setDialogResponse(id, userAction);
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

	public Benchmarks getBenchmarkMaster()
	{
		return(m_benchmarkMaster);
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
		m_benchmarkMaster.benchmarkInitialize(m_bm_opbm,
											  m_bm_macroMaster,
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
		return(frame);
	}

	/** Main app entry point.
	 *
	 * @param args command line parameters
	 */
    public static void main(String[] args)
	{
		// Launch the system
        m_opbm = new Opbm(args);
    }

	/**
	 * Master instance created in main()
	 */
	private static Opbm				m_opbm;

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
	 * Master list of tupels used throughout the system, referenced by name
	 */
	private List<Tupel>				m_tupels;

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
	 * When an error occurs, holds the text caused by the exception
	 */
	private static String			m_lastError;

	/**
	 * Width of client area only (does not include size of window borders,
	 * header, etc.)
	 */
	private int						m_width;

	/**
	 * Height of client area only (does not include size of window borders,
	 * header, etc.)
	 */
	private int						m_height;

	/**
	 * Total width, including OS's handling of window borders.
	 */
	private int						m_actual_width;

	/**
	 * Total height, including OS's handling of window borders.
	 */
	private int						m_actual_height;

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
    private DroppableFrame			frame;

	/**
	 * Holds the status bar display of tooltip texts, system messages, etc.
	 */
    private Label					statusBar;

	/**
	 * Holds the OPBM logo at the top in the header area.
	 */
	private JLabel					lblHeader;

	/**
	 * Holds the default left-panel object, indicating the reference size,
	 * position and color for all "cloned" child left-panel objects built by
	 * <code>PanelFactory.createLeftPanelObjects()</code>.
	 */
	private JPanel					panLeft;

	/**
	 * Holds the default right-panel object, indicating the reference size,
	 * position and color for all "cloned" child right-panel objects built by
	 * <code>PanelFactory.createRightPanelFromEdit()</code>.
	 */
	private JPanel					panRight;

	private List<JFrame>			m_zoomFrames;
	private ResultsViewer			m_rv;
	private String					m_rvFilename;
	private boolean					m_executingFromCommandLine;

	private Tupel					m_dialogTupel;

	// Essential services
//import java.lang.Class;
//import java.awt.Robot;
//import java.sql.SQLException;
//import java.sql.ResultSet;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.swing.border.TitledBorder;
//import javax.swing.border.EtchedBorder;
//	private Robot					m_robot;				// Used to insert keystrokes and mouse events
//	private SqlUtils				m_sqlUtils;				// Used to store permanent data across sessions

	/**
	 * An internal debugger flag, to determine if certain breakpoints used during development should be stopped at or not
	 */
	private static boolean			m_breakpointsEnabled;
	private static String			m_title = "OPBM - Office Productivity Benchmark";
}
