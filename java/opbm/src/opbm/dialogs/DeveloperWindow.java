/*
 * OPBM - Office Productivity Benchmark
 *
 * This class draws and processes the main developer window for OPBM.
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

package opbm.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import opbm.Opbm;

public final class DeveloperWindow	extends DroppableFrame
								 implements WindowListener
{
	public DeveloperWindow(Opbm		opbm,
						   boolean	isZoomWindow)
	{
		super(opbm, isZoomWindow, true);	// Call DroppableFrame constructor
		int i;
		Dimension maxSize;

		// Initialize our parent and size our window
		m_opbm		= opbm;
		// This is the minimum size we should make the developer window because of the definitions present in edits.xml, and the left-side panel menus defined in panels.xml
		m_width		= 1100;
		m_height	= 645;
		// However, if it can be bigger, it is best to be around 1400 x 800
		// So we check the size of the host video display to see if it can be bigger, and if so, then we make it bigger
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		// Get size of each screen
		maxSize = new Dimension(m_width, m_height);
		for (i = 0; i < gs.length; i++)
		{
			DisplayMode dm = gs[i].getDisplayMode();
			if (dm.getWidth() > maxSize.getWidth() && dm.getHeight() > maxSize.getHeight())
			{	// Update the max
				maxSize.setSize(dm.getWidth(), dm.getHeight());
			}
		}
		// If we get here, and we're bigger than m_width and m_height, adjust m_width and m_height to 95% of the max size
		if (maxSize.getWidth() * 0.90f > m_width)
			m_width = Math.min((int)(maxSize.getWidth() * 0.90f), 1500);
		if (maxSize.getHeight() * 0.90f > m_height)
			m_height = Math.min((int)(maxSize.getHeight() * 0.90f), 750);

		// Set the title to our hard-coded form, which includes the application name plus version number (which may be the date and time of the last build)
		setTitle( Opbm.m_title );

		// Compute the actual size we need for our window, so it's properly centered
		pack();
        Insets fi		= getInsets();
		m_actual_width	= m_width  + fi.left + fi.right;
		m_actual_height	= m_height + fi.top  + fi.bottom;
        setSize(m_width  + fi.left + fi.right,
				m_height + fi.top  + fi.bottom);

        m_prefSize = new Dimension(m_width  + fi.left + fi.right,
								 m_height + fi.top  + fi.bottom);
        setMinimumSize(m_prefSize);
        setPreferredSize(m_prefSize);

        m_prefSize = new Dimension(m_width  + fi.left + fi.right,
								 m_height + fi.top  + fi.bottom);
        setMinimumSize(m_prefSize);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(m_width, m_height);
        setLocationRelativeTo(null);  // Center window
        setLayout(null);				// We handle all redraws
		Container c = getContentPane();
        c.setBackground(new Color(120, 120, 120));
		c.setForeground(Color.WHITE);

        createStatusBar();
		statusBar.setVisible(true);
		setStatusBar(statusBar);
		addKeyListener(opbm);
		addMouseWheelListener(opbm);
		addComponentListener(opbm);

        add(statusBar);
        setVisible(false);
		addWindowListener(this);

		// Create the header image
		lblHeader = new JLabel();
		try {
			lblHeader.setIcon(new ImageIcon(ImageIO.read(new File(Opbm.locateFile("header.png")))));

		} catch (IOException ex) {
			// Nothing to do really, indicates an improper installation
			m_lastError = ex.getMessage();
			lblHeader.setText(m_opbm.getAppTitle());
		}
		lblHeader.setBackground(Color.BLACK);
		lblHeader.setForeground(Color.WHITE);
		lblHeader.setOpaque(true);
		lblHeader.setBounds(0, 0, m_width, 50);
		lblHeader.setHorizontalAlignment(JLabel.LEFT);
		lblHeader.setVisible(true);
		add(lblHeader);

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
		if (opbm.isFontOverride())
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
		add(panLeft);

		// Create the right panel
		panRight = new JPanel();
		panRight.setOpaque(true);
		panRight.setBackground(new Color(209,200,172));
		panRight.setForeground(new Color(97,93,80));
		panRight.setBounds(251, lblHeader.getHeight(), m_width - panLeft.getWidth(), m_height - lblHeader.getHeight() - statusBar.getHeight());
		panRight.setLayout(null);
		panRight.setVisible(true);


//////////
// Developer Window
// Note: Click the Skin link (upper-left by Quit) for a simple, skinned interface
//
// OPBM
//   by Cana Labs,
//      Cossatot Analytics Laboratories, LLC.
//
// (c) 2011.
//
// Written by:
//   Van Smith
/////
		l1 = new JLabel("<html><font size=\"+2\"><b>Developer Window</b><br><table><tr><td width=\"10\">&nbsp;</td><td width=\"20\"><font size=\"-1\"><b>Note:</b></td><td width=\"5\"><font size=\"-1\">&nbsp;</td><td width=\"180\"><font size=\"-1\"><i>Click the 'Skin' link (upper-left, by Quit) for a simple, skinned interface</i></td></tr></table><br><br><font size=\"+2\"><b>OPBM</b></font><br><i><font size=\"-1\">&nbsp;&nbsp;&nbsp;by Cana Labs,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cossatot Analytics Laboratories, LLC.<br><br>&nbsp;&nbsp;&nbsp;<i>(c) 2011.<br><br></i></font><b>Written by:</b><font size=\"-1\"><i><br>&nbsp;&nbsp;&nbsp;Van Smith<br><br><br><br><br>&nbsp</i></font>");
		l1.setBounds(0, 0, panRight.getWidth(), panRight.getHeight());
		l1.setBackground(panRight.getBackground());
		l1.setForeground(panRight.getForeground());
		l1.setFont(f);
		l1.setHorizontalAlignment(JLabel.CENTER);
		l1.setVerticalAlignment(JLabel.CENTER);
		l1.setVisible(true);
		panRight.add(l1);

		add(panRight);
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
        Dimension d = new Dimension(getWidth(), 16);
        statusBar.setMinimumSize(d);
        statusBar.setMaximumSize(d);
        statusBar.setPreferredSize(d);
		statusBar.setSize(d);
		statusBar.setLocation(0, getHeight() - 16);
		statusBar.addKeyListener(m_opbm);
		statusBar.addMouseWheelListener(m_opbm);
    }

	public void componentResized(ComponentEvent evt)
	{
	// Called when the newFrame (window) is resized
		Dimension newSize = ((Component)evt.getSource()).getSize();
		Insets fi = getInsets();
		m_actual_width	= (int)newSize.getWidth();
		m_actual_height	= (int)newSize.getHeight();
		m_width			= m_actual_width  - fi.left - fi.right;
		m_height		= m_actual_height - fi.top  - fi.bottom;
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
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e)
	{	// They are closing the window, save anything that might be open and not yet saved, for OPBM is a WYSIWYG app
		m_opbm.getCommandMaster().processCommand(this, "rawedit_save",	null, null, null, null, null, null, null, null, null, null);
		m_opbm.getCommandMaster().processCommand(this, "edit_save",		null, null, null, null, null, null, null, null, null, null);
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	/**
	 * Holds the status bar display of tooltip texts, system messages, etc.
	 */
    public Label					statusBar;

	/**
	 * Holds the OPBM logo at the top in the header area.
	 */
	public JLabel					lblHeader;

	/**
	 * Holds the default left-panel object, indicating the reference size,
	 * position and color for all "cloned" child left-panel objects built by
	 * <code>PanelFactory.createLeftPanelObjects()</code>.
	 */
	public JPanel					panLeft;

	/**
	 * Holds the default right-panel object, indicating the reference size,
	 * position and color for all "cloned" child right-panel objects built by
	 * <code>PanelFactory.createRightPanelFromEdit()</code>.
	 */
	public JPanel					panRight;


	/**
	 * When an error occurs, holds the text caused by the exception
	 */
	public String					m_lastError;

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
	 * Preferred window size
	 */
	private Dimension				m_prefSize;
}
