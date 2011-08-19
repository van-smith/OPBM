package opbm.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import opbm.Opbm;

/**
 *
 * @author rick
 */
public class DeveloperWindow extends DroppableFrame
{
	public DeveloperWindow(Opbm		opbm,
						   boolean	isZoomWindow)
	{
		super(opbm, isZoomWindow);	// Call DroppableFrame constructor

		// Initialize our parent and size our window
		m_opbm		= opbm;
		m_width		= 1100;
		m_height	= 645;
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

		// Create the header image
		lblHeader = new JLabel();
		try {
			lblHeader.setIcon(new ImageIcon(ImageIO.read(new File(Opbm.locateFile("header.png")))));

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
