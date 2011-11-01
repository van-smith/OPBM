/*
 * OPBM - Office Productivity Benchmark
 *
 * This class allows the user to populate content in an input box, and then
 * receives their response via a choice of up to four buttons.
 *
 * Last Updated:  Sep 15, 2011
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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import opbm.Opbm;
import opbm.common.Tuple;
import opbm.graphics.AlphaImage;

public final class OpbmInput
					extends		TimerTask
					implements	MouseListener,
								WindowListener,
								KeyListener
{
	public OpbmInput(Opbm		opbm,
					 boolean	modal,
					 String		caption,
					 String		label,
					 String		initValue,
					 int		buttons,
					 String		id,
					 String		triggerCommand,
					 boolean	singleLine)
	{
		m_opbm				= opbm;
		m_modal				= modal;
		m_caption			= caption;
		m_label				= label;

		if (initValue == null)
			m_initValue	 = "";
		else
			m_initValue		= initValue;

		m_buttons			= buttons;
		m_nextButtonText	= "Next";
		m_cancelButtonText	= "Cancel";
		m_acceptButtonText	= "Accept";
		m_okayButtonText	= "Okay";

		if (id == null)
		{	// They didn't specify an ID, so use the generic one
			id = "input";
		}
		m_id				= id;
		m_singleLineInput	= singleLine;
		m_opbm.initializeDialogResponse(id, triggerCommand, null, this);
		createInputWindow();
	}

	public OpbmInput(Opbm		opbm,
					 boolean	modal,
					 String		caption,
					 String		label,
					 String		initValue,
					 int		buttons,
					 String		id,
					 String		nextButtonText,
					 String		cancelButtonText,
					 String		acceptButtonText,
					 String		okayButtonText,
					 String		triggerCommand,
					 boolean	singleLine)
	{
		m_opbm				= opbm;
		m_modal				= modal;
		m_caption			= caption;
		m_label				= label;

		if (initValue == null)
			m_initValue	 = "";
		else
			m_initValue		= initValue;

		m_buttons			= buttons;
		m_nextButtonText	= nextButtonText.isEmpty()		? "Next"	: nextButtonText;
		m_cancelButtonText	= cancelButtonText.isEmpty()	? "Cancel"	: cancelButtonText;
		m_acceptButtonText	= acceptButtonText.isEmpty()	? "Accept"	: acceptButtonText;
		m_okayButtonText	= okayButtonText.isEmpty()		? "Okay"	: okayButtonText;

		if (id == null)
		{	// They didn't specify an ID, so use the generic one
			id = "input";
		}
		m_id				= id;
		m_singleLineInput	= singleLine;
		m_opbm.initializeDialogResponse(id, triggerCommand, null, this);
		createInputWindow();
	}

	public static Opbm		m_si_opbm;
	public static String	m_si_label;
	public static String	m_si_caption;
	public static String	m_si_initValue;
	public static String	m_si_id;
	public static String	m_si_triggerCommand;
	public static boolean	m_si_singleLine;
	public static boolean	m_si_modal;
	/**
	 * Creates a simple input for inputting some value, with a post-user-action
	 * trigger command
	 */
	public static void simpleInput(Opbm			opbm,
								   String		caption,
								   String		label,
								   String		initValue,
								   String		id,
								   String		triggerCommand,
								   boolean		singleLine,
								   boolean		modal)
	{
		m_si_opbm				= opbm;
		m_si_label				= label;
		m_si_caption			= caption;
		m_si_initValue			= initValue;
		m_si_id					= id;
		m_si_triggerCommand		= triggerCommand;
		m_si_singleLine			= singleLine;
		m_si_modal				= modal;

		Thread t = new Thread("simpleInput_" + m_si_id)
		{
			@Override
			public void run()
			{	// In the thread we do the work
				Opbm	opbm			= m_si_opbm;
				String	label			= m_si_label;
				String	caption			= m_si_caption;
				String	initValue		= m_si_initValue;
				String	id				= m_si_id;
				String	triggerCommand	= m_si_triggerCommand;
				boolean	singleLine		= m_si_singleLine;
				boolean modal			= m_si_modal;

				OpbmInput oi = new OpbmInput(opbm, modal, caption, label, initValue, OpbmInput._BUTTONS23, id, triggerCommand, singleLine);
			}
		};
		t.start();
	}

	public void createInputWindow()
	{
		Dimension prefSize;
		Insets inset;
		Font fontLabel, fontInput, fontButtons;
		int buttonCenters, buttonBackoff, buttonWidth, buttonCount, thisButton, buttonTop;
		String caption;

		m_width		= 450;
		m_height	= m_singleLineInput ? /*single line*/180 : /*multi-line*/250;
		m_frame		= new DroppableFrame(m_opbm, false, false);
		caption		= m_caption.isEmpty() ? "OPBM Input" : m_caption;
		m_frame.setTitle(caption);

		// Compute the actual size we need for our window, so it's properly centered
		m_frame.pack();
		Insets fi		= m_frame.getInsets();
		m_actual_width	= m_width  + fi.left + fi.right;
		m_actual_height	= m_height + fi.top  + fi.bottom;
		m_frame.setSize(m_width  + fi.left + fi.right,
					    m_height + fi.top  + fi.bottom);

		prefSize = new Dimension(m_width  + fi.left + fi.right,
								 m_height + fi.top  + fi.bottom);
		m_frame.setMinimumSize(prefSize);
		m_frame.setPreferredSize(prefSize);

		prefSize = new Dimension(m_width  + fi.left + fi.right,
								 m_height + fi.top  + fi.bottom);
		m_frame.setMinimumSize(prefSize);
		m_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        m_frame.setSize(m_width, m_height);
		m_frame.setLocationRelativeTo(null);	// Center window
		m_frame.setLayout(null);				// We handle all redraws

		Container c = m_frame.getContentPane();
		c.setBackground(new Color(120, 120, 120));
		c.setForeground(Color.WHITE);

		m_pan = new JLayeredPane();
		m_pan.setLayout(null);
		m_pan.setBounds(0, 0, m_width, m_height);
		m_pan.setVisible(true);
		m_pan.setBorder(BorderFactory.createEmptyBorder());
		c.add(m_pan);
		AlphaImage img = new AlphaImage(Opbm.locateFile(m_singleLineInput ? "input_background_single_line.png" : "input_background.png"));

		// Set the background image
		m_lblBackground = new JLabel();
		m_lblBackground.setBounds(0, 0, m_width, m_height);
		m_lblBackground.setHorizontalAlignment(JLabel.LEFT);
		m_lblBackground.setVerticalAlignment(JLabel.TOP);
		m_lblBackground.setVisible(true);
		m_lblBackground.setIcon(new ImageIcon(img.getBufferedImage()));
		m_pan.add(m_lblBackground);
		m_pan.moveToFront(m_lblBackground);

		// Create the fonts
		fontLabel	= new Font("Calibri", Font.BOLD, 20);
		fontInput	= new Font("Calibri", Font.BOLD, 14);
		fontButtons	= fontLabel;

		// Create the visible label contents
		m_lblLabel = new JLabel();
		m_lblLabel.setBounds(15, 52, 423, 28);
		m_lblLabel.setHorizontalAlignment(JLabel.LEFT);
		m_lblLabel.setVerticalAlignment(JLabel.CENTER);
		m_lblLabel.setFont(fontLabel);
		m_lblLabel.setForeground(Color.BLUE);
		m_lblLabel.setText(m_label);
 		m_lblLabel.setVisible(true);
		m_pan.add(m_lblLabel);
		m_pan.moveToFront(m_lblLabel);

		// Create the input box
		if (m_singleLineInput)
		{	// It's a single-line input box
			m_txtInputSingleLine = new JTextField();
			m_txtInputSingleLine.setBounds(15, 85, 421, 25);
			m_txtInputSingleLine.setFont(fontInput);
			m_txtInputSingleLine.setText(m_initValue);
			m_txtInputSingleLine.setVisible(true);
			m_txtInputSingleLine.addKeyListener(this);
			m_pan.add(m_txtInputSingleLine);
			m_pan.moveToFront(m_txtInputSingleLine);

		} else {
			m_txtInput			= new JTextArea();
			m_txtInputScroll	= new JScrollPane(m_txtInput);
			m_txtInputScroll.setBounds(15, 83, 421, 100);
			m_txtInputScroll.setAutoscrolls(true);
			m_txtInputScroll.setVisible(true);
			m_txtInput.setFont(fontInput);
			m_txtInput.setLineWrap(true);
			m_txtInput.setWrapStyleWord(true);
			m_txtInput.setTabSize(2);
			m_txtInput.setText(m_initValue);
			m_txtInput.setVisible(true);
			m_txtInput.addKeyListener(this);
			m_pan.add(m_txtInputScroll);
			m_pan.moveToFront(m_txtInputScroll);
		}

		// Determine which buttons are specified
		buttonCount = 0;
		if ((m_buttons & _NEXT_BUTTON) != 0)
		{	// Create the okay button, to be positioned below
			m_btnNext = new JButton(m_nextButtonText);
			m_btnNext.setFont(fontButtons);
			m_btnNext.addMouseListener(this);
			inset = m_btnNext.getInsets();
			inset.left = 3;
			inset.right = 3;
			m_btnNext.setMargin(inset);
			m_pan.add(m_btnNext);
			m_pan.moveToFront(m_btnNext);
			++buttonCount;
		}
		if ((m_buttons & _CANCEL_BUTTON) != 0)
		{	// Create the cancel button, to be positioned below
			m_btnCancel = new JButton(m_cancelButtonText);
			m_btnCancel.setFont(fontButtons);
			m_btnCancel.addMouseListener(this);
			inset = m_btnCancel.getInsets();
			inset.left = 3;
			inset.right = 3;
			m_btnCancel.setMargin(inset);
			m_pan.add(m_btnCancel);
			m_pan.moveToFront(m_btnCancel);
			++buttonCount;
		}
		if ((m_buttons & _ACCEPT_BUTTON) != 0)
		{	// Create the yes button, to be positioned below
			m_btnAccept = new JButton(m_acceptButtonText);
			m_btnAccept.setFont(fontButtons);
			m_btnAccept.addMouseListener(this);
			inset = m_btnAccept.getInsets();
			inset.left = 3;
			inset.right = 3;
			m_btnAccept.setMargin(inset);
			m_pan.add(m_btnAccept);
			m_pan.moveToFront(m_btnAccept);
			++buttonCount;
		}
		if ((m_buttons & _OKAY_BUTTON) != 0)
		{	// Create the no button, to be positioned below
			m_btnOkay = new JButton(m_okayButtonText);
			m_btnOkay.setFont(fontButtons);
			m_btnOkay.addMouseListener(this);
			inset = m_btnOkay.getInsets();
			inset.left = 3;
			inset.right = 3;
			m_btnOkay.setMargin(inset);
			m_pan.add(m_btnOkay);
			m_pan.moveToFront(m_btnOkay);
			++buttonCount;
		}

		// Determine the coordinates for each button
		buttonCenters	= (m_width / (buttonCount + 1));
		buttonWidth		= (int)((double)buttonCenters * 0.80);
		buttonBackoff	= (m_width / (buttonCount + 2)) / 2;

		// Position the buttons
		thisButton	= 1;
		buttonTop	= m_singleLineInput ? 130 : 200;
		if (m_btnNext != null)
		{	// Position and make visible this button
			m_btnNext.setBounds( + (thisButton * buttonCenters) - buttonBackoff, buttonTop, buttonWidth, 40);
			m_btnNext.setVisible(true);
			++thisButton;
		}
		if (m_btnCancel != null)
		{	// Position and make visible this button
			m_btnCancel.setBounds((thisButton * buttonCenters) - buttonBackoff, buttonTop, buttonWidth, 40);
			m_btnCancel.setVisible(true);
			++thisButton;
		}
		if (m_btnAccept!= null)
		{	// Position and make visible this button
			m_btnAccept.setBounds((thisButton * buttonCenters) - buttonBackoff, buttonTop, buttonWidth, 40);
			m_btnAccept.setVisible(true);
			++thisButton;
		}
		if (m_btnOkay != null)
		{	// Position and make visible this button
			m_btnOkay.setBounds((thisButton * buttonCenters) - buttonBackoff, buttonTop, buttonWidth, 40);
			m_btnOkay.setVisible(true);
			++thisButton;
		}

		if (m_modal)
		{	// Make it a modal window
			m_frame.setModal(m_width, m_height, fi, m_pan, m_singleLineInput ? m_txtInputSingleLine : m_txtInput, true);

		} else {
			// Make it a non-modal window
			if (m_singleLineInput)
			{
				m_txtInputSingleLine.requestFocusInWindow();
				m_txtInputSingleLine.selectAll();

			} else {
				m_txtInput.requestFocusInWindow();
				m_txtInput.selectAll();
			}

			m_frame.setVisible(true);
			m_frame.forceWindowToHaveFocus();
		}
	}

	/**
	 * Sets a timeout period for when the window should self-close
	 * @param interval in seconds
	 */
	public void setTimeout(int interval)
	{
		m_timer		= new Timer();
		m_count		= 0;
		m_countMax	= interval;
		m_timer.schedule(this, 1000, 1000);
	}

	/**
	 * Reads the user input, waiting until the user has clicked a button before
	 * returning.
	 */
	public Tuple readInput()
	{
		Tuple tup;

		// Create a return tuple
		tup = new Tuple();

		// Loop until an action is encountered (user clicks a button, auto-closed, user-closed, etc.)
		m_actionEvents = 0;
		while (m_actionEvents == 0)
		{
			try
			{	// Wait for 1/10th second between checks
				Thread.sleep(100);

			} catch (InterruptedException ex) {
			}
		}

		// When we get here, an action has occurred, and we have our results
		tup.add("action",	m_dialogResponse);
		tup.add("value",	m_dialogValue);

		// Return the action and value
		return(tup);
	}

	/**
	 * Timer() callback
	 */
	@Override
	public void run()
	{
		++m_count;
		if (m_count >= m_countMax)
		{
			m_timer.cancel();
			m_dialogResponse	= "autoclosed";
			m_dialogValue		= null;
			m_opbm.setDialogResponse(m_id, "autoclosed", null, this);
			dispose();

		} else {
			// Update our on-screen countdown display
			if (m_lblCountdown == null)
			{
				m_lblCountdown = new JLabel();
				m_lblCountdown.setBounds(0, 0, 15, 10);
				m_lblCountdown.setFont(new Font("Calibri", Font.BOLD, 10));
				m_lblCountdown.setHorizontalAlignment(JLabel.CENTER);
				m_lblCountdown.setForeground(Color.WHITE);
				m_pan.add(m_lblCountdown);
				m_pan.moveToFront(m_lblCountdown);
			}
			m_lblCountdown.setText(Integer.toString(m_countMax - m_count));
		}
	}

	public void dispose()
	{
		++m_actionEvents;
		if (m_frame != null)
			m_frame.dispose();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getComponent() == m_btnNext)
		{	// Next button was clicked
			m_dialogResponse	= m_nextButtonText + "_button1";
			m_dialogValue		= m_singleLineInput ? m_txtInputSingleLine.getText() : m_txtInput.getText();
			m_opbm.setDialogResponse(m_id, m_dialogResponse, m_dialogValue, null, this);
			++m_actionEvents;
			m_frame.dispose();

		} else if (e.getComponent() == m_btnCancel) {
			// Cancel button was clicked
			m_dialogResponse	= m_cancelButtonText + "_button2";
			m_dialogValue		= m_singleLineInput ? m_txtInputSingleLine.getText() : m_txtInput.getText();
			m_opbm.setDialogResponse(m_id, m_dialogResponse, m_dialogValue, null, this);
			++m_actionEvents;
			m_frame.dispose();

		} else if (e.getComponent() == m_btnAccept) {
			// Accept button was clicked
			m_dialogResponse	= m_acceptButtonText + "_button3";
			m_dialogValue		= m_singleLineInput ? m_txtInputSingleLine.getText() : m_txtInput.getText();
			m_opbm.setDialogResponse(m_id, m_dialogResponse, m_dialogValue, null, this);
			++m_actionEvents;
			m_frame.dispose();

		} else if (e.getComponent() == m_btnOkay) {
			// Okay button was clicked
			m_dialogResponse	= m_okayButtonText + "_button4";
			m_dialogValue		= m_singleLineInput ? m_txtInputSingleLine.getText() : m_txtInput.getText();
			m_opbm.setDialogResponse(m_id, m_dialogResponse, m_dialogValue, null, this);
			++m_actionEvents;
			m_frame.dispose();

		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e)
	{	// User cancelled
		++m_actionEvents;
		m_opbm.setDialogResponse(m_id, m_cancelButtonText, m_singleLineInput ? m_txtInputSingleLine.getText() : m_txtInput.getText(), null, this);
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

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	public static final int	_NEXT_BUTTON	= 1;
	public static final int	_CANCEL_BUTTON	= 2;
	public static final int	_ACCEPT_BUTTON	= 4;
	public static final int	_OKAY_BUTTON	= 8;
	public static final int	_NEXT_CANCEL	= 3;
	public static final int	_ACCEPT_CANCEL	= 6;
	public static final int	_OKAY_CANCEL	= 10;
	public static final int	_BUTTON1		= 1;
	public static final int	_BUTTON2		= 2;
	public static final int	_BUTTON3		= 4;
	public static final int	_BUTTON4		= 8;
	public static final int _BUTTONS12		= _BUTTON1 + _BUTTON2;
	public static final int _BUTTONS14		= _BUTTON1 + _BUTTON4;
	public static final int _BUTTONS23		= _BUTTON2 + _BUTTON3;
	public static final int _BUTTONS34		= _BUTTON3 + _BUTTON4;
	public static final int _BUTTONS123		= _BUTTON1 + _BUTTON2 + _BUTTON3;
	public static final int _BUTTONS234		= _BUTTON2 + _BUTTON3 + _BUTTON4;
	public static final int _BUTTONS1234	= _BUTTON1 + _BUTTON2 + _BUTTON3 + _BUTTON4;

	private Opbm				m_opbm;
	private boolean				m_modal;
	private int					m_actual_width;
	private int					m_actual_height;
	private int					m_width;
	private int					m_height;
	private JLayeredPane		m_pan;
	public DroppableFrame		m_frame;

	private	int					m_actionEvents;					// Holds a counter for action events used in readInput()
	private String				m_dialogResponse;				// Holds the most recent dialog response, the button that was clicked, or event which closed the input
	private	String				m_dialogValue;					// The value of the input when the last action occurred

	public JLabel				m_lblBackground;
	public JLabel				m_lblLabel;
	public JScrollPane			m_txtInputScroll;
	public JTextArea			m_txtInput;
	public JTextField			m_txtInputSingleLine;

	private JButton				m_btnNext;
	private JButton				m_btnCancel;
	private JButton				m_btnAccept;
	private JButton				m_btnOkay;

	private String				m_nextButtonText;
	private String				m_cancelButtonText;
	private String				m_acceptButtonText;
	private String				m_okayButtonText;

	private String				m_caption;
	private String				m_label;
	private String				m_initValue;
	private int					m_buttons;
	private String				m_id;
	private boolean				m_singleLineInput;

	// Used for the setTimeout() functionality
	private Timer				m_timer;
	private int					m_count;
	private int					m_countMax;
	private JLabel				m_lblCountdown;
}
