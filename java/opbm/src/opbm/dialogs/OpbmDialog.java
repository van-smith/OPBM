/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class of the OPBM.  It creates a GUI, loads
 * necessary files, beings processing based on context, etc.
 *
 * Last Updated:  Sep 12, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.1.0
 *
 */

package opbm.dialogs;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
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
import opbm.Opbm;
import opbm.common.Utils;
import opbm.graphics.AlphaImage;

public final class OpbmDialog
					extends		TimerTask
					implements  MouseListener,
							    WindowListener
{
	public OpbmDialog(Opbm		opbm,
					  String	message,
					  String	caption,
					  int		buttons,
					  String	id,
					  String	triggerCommand)
	{
		m_opbm			= opbm;
		m_message		= message;
		m_caption		= caption;
		m_buttons		= buttons;
		if (id == null)
		{	// They didn't specify an ID, so use the generic one
			id = "dialog";
		}
		m_id			= id;
		m_opbm.initializeDialogResponse(id, triggerCommand, this, null);
		createDialogWindow("Okay", "Cancel", "Yes", "No");
	}

	public OpbmDialog(Opbm		opbm,
					  String	message,
					  String	caption,
					  int		buttons,
					  String	id,
					  String	triggerCommand,
					  String	button1Text,
					  String	button2Text,
					  String	button3Text,
					  String	button4Text)
	{
		m_opbm			= opbm;
		m_message		= message;
		m_caption		= caption;
		m_buttons		= buttons;
		if (id == null)
		{	// They didn't specify an ID, so use the generic one
			id = "dialog";
		}
		m_id			= id;
		m_opbm.initializeDialogResponse(id, triggerCommand, this, null);
		createDialogWindow(button1Text, button2Text, button3Text, button4Text);
	}

	/**
	 * Creates the interface, sets up the buttons, etc.
	 * @param okayButtonText text to display for the "okay button" position
	 * @param cancelButtonText text to display for the "cancel button" position
	 * @param yesButtonText text to display for the "yes button" position
	 * @param noButtonText text to display for the "no button" position
	 */
	public void createDialogWindow(String	okayButtonText,
								   String	cancelButtonText,
								   String	yesButtonText,
								   String	noButtonText)
	{
		Dimension prefSize;
		JLabel lblBackground;
		Insets inset;
		Font fontLabel, fontButtons;
		int actual_width, actual_height, width, height, buttonCenters, buttonBackoff, buttonWidth, buttonCount, thisButton, buttonTop;

		width = 450;
		height = 200;
		m_frame = new DroppableFrame(m_opbm, false, false);
		m_frame.setTitle(m_caption.isEmpty() ? "OPBM Dialog" : m_caption);

		// Compute the actual size we need for our window, so it's properly centered
		m_frame.pack();
		Insets fi		= m_frame.getInsets();
		actual_width	= width  + fi.left + fi.right;
		actual_height	= height + fi.top  + fi.bottom;
		m_frame.setSize(width  + fi.left + fi.right,
					  height + fi.top  + fi.bottom);

		prefSize = new Dimension(width  + fi.left + fi.right,
								 height + fi.top  + fi.bottom);
		m_frame.setMinimumSize(prefSize);
		m_frame.setPreferredSize(prefSize);

		prefSize = new Dimension(width  + fi.left + fi.right,
								 height + fi.top  + fi.bottom);
		m_frame.setMinimumSize(prefSize);
		m_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        m_frame.setSize(width, height);
		m_frame.setLocationRelativeTo(null);	// Center window
		m_frame.setLayout(null);				// We handle all redraws

		Container c = m_frame.getContentPane();
		c.setBackground(new Color(120, 120, 120));
		c.setForeground(Color.WHITE);

		m_pan = new JLayeredPane();
		m_pan.setLayout(null);
		m_pan.setBounds(0, 0, width, height);
		m_pan.setVisible(true);
		m_pan.setBorder(BorderFactory.createEmptyBorder());
		c.add(m_pan);
		AlphaImage img = new AlphaImage(Opbm.locateFile("inquiry_background.png"));

		// Set the background image
		lblBackground = new JLabel();
		lblBackground.setBounds(0, 0, width, height);
		lblBackground.setHorizontalAlignment(JLabel.LEFT);
		lblBackground.setVerticalAlignment(JLabel.TOP);
		lblBackground.setVisible(true);
		lblBackground.setIcon(new ImageIcon(img.getBufferedImage()));
		m_pan.add(lblBackground);
		m_pan.moveToFront(lblBackground);

		// Create the fonts
		fontLabel	= new Font("Calibri", Font.BOLD, 18);
		fontButtons	= new Font("Calibri", Font.BOLD, 20);

		// Create the visible label contents
		m_lblMessage = new JLabel();
		m_lblMessage.setBounds(15, 52, 419, 50);
		m_lblMessage.setHorizontalAlignment(JLabel.CENTER);
		m_lblMessage.setVerticalAlignment(JLabel.CENTER);
		m_lblMessage.setFont(fontLabel);
		// We force the text to center at a location we want using html tags
		m_lblMessage.setText(String.format("<html><div align='center' valign='center' WIDTH=%d><table><tr><td width='60'></td><td>%s</td></tr></table></div><html>", 419, m_message));
 		m_lblMessage.setVisible(true);
		m_pan.add(m_lblMessage);
		m_pan.moveToFront(m_lblMessage);

		// Determine which buttons are specified
		if (m_buttons != _ZERO_BUTTONS)
		{
			buttonCount = 0;
			if ((m_buttons & _OKAY_BUTTON) != 0)
			{	// Create the okay button, to be positioned below
				m_btnOkay = new JButton(okayButtonText);
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
			if ((m_buttons & _CANCEL_BUTTON) != 0)
			{	// Create the cancel button, to be positioned below
				m_btnCancel = new JButton(cancelButtonText);
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
			if ((m_buttons & _YES_BUTTON) != 0)
			{	// Create the yes button, to be positioned below
				m_btnYes = new JButton(yesButtonText);
				m_btnYes.setFont(fontButtons);
				m_btnYes.addMouseListener(this);
				inset = m_btnYes.getInsets();
				inset.left = 3;
				inset.right = 3;
				m_btnYes.setMargin(inset);
				m_pan.add(m_btnYes);
				m_pan.moveToFront(m_btnYes);
				++buttonCount;
			}
			if ((m_buttons & _NO_BUTTON) != 0)
			{	// Create the no button, to be positioned below
				m_btnNo = new JButton(noButtonText);
				m_btnNo.setFont(fontButtons);
				m_btnNo.addMouseListener(this);
				inset = m_btnNo.getInsets();
				inset.left = 3;
				inset.right = 3;
				m_btnNo.setMargin(inset);
				m_pan.add(m_btnNo);
				m_pan.moveToFront(m_btnNo);
				++buttonCount;
			}

			// Determine the coordinates for each button
			buttonCenters	= (width / (buttonCount + 1));
			buttonWidth		= (int)((double)buttonCenters * 0.80);
			buttonBackoff	= (width / (buttonCount + 2)) / 2;

			// Position the buttons
			thisButton	= 1;
			buttonTop	= 135;
			if (m_btnOkay != null)
			{	// Position and make visible this button
				m_btnOkay.setBounds( + (thisButton * buttonCenters) - buttonBackoff, buttonTop, buttonWidth, 40);
				m_btnOkay.setVisible(true);
				++thisButton;
			}
			if (m_btnCancel != null)
			{	// Position and make visible this button
				m_btnCancel.setBounds((thisButton * buttonCenters) - buttonBackoff, buttonTop, buttonWidth, 40);
				m_btnCancel.setVisible(true);
				++thisButton;
			}
			if (m_btnYes!= null)
			{	// Position and make visible this button
				m_btnYes.setBounds((thisButton * buttonCenters) - buttonBackoff, buttonTop, buttonWidth, 40);
				m_btnYes.setVisible(true);
				++thisButton;
			}
			if (m_btnNo != null)
			{	// Position and make visible this button
				m_btnNo.setBounds((thisButton * buttonCenters) - buttonBackoff, buttonTop, buttonWidth, 40);
				m_btnNo.setVisible(true);
				++thisButton;
			}
		}

		m_frame.setVisible(true);
		m_frame.forceWindowToHaveFocus();
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
	 * Timer() callback
	 */
	@Override
	public void run()
	{
		++m_count;
		if (m_count >= m_countMax)
		{
			m_timer.cancel();
			m_opbm.setDialogResponse(m_id, "autoclosed", this, null);
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
		if (m_frame != null)
			m_frame.dispose();
	}

	/**
	 * Creates a simple dialog for displaying something for an (optional)
	 * period of time before auto-closing
	 * @param message what to display
	 * @param caption what to put in the caption (title bar)
	 * @param timeoutSeconds how many seconds to pause before auto-closing
	 * (use 0 to disable)
	 */

	public static Opbm		m_sd_opbm;
	public static String	m_sd_uuid;
	public static String	m_sd_message;
	public static String	m_sd_caption;
	public static int		m_sd_timeoutSeconds;

	public static String simpleDialog(Opbm		opbm,
									  String	message,
									  String	caption,
									  int		timeoutSeconds)
	{
		m_sd_opbm			= opbm;
		m_sd_uuid			= Utils.getUUID();
		m_sd_message		= message;
		m_sd_caption		= caption;
		m_sd_timeoutSeconds	= timeoutSeconds;
		
		Thread t = new Thread("simpleDialog_" + m_sd_uuid)
		{
			@Override
			public void run()
			{	// In the thread we render the bottom section
				Opbm	opbm			= m_sd_opbm;
				String	uuid			= m_sd_uuid;
				String	message			= m_sd_message;
				String	caption			= m_sd_caption;
				int		timeoutSeconds	= m_sd_timeoutSeconds;

				OpbmDialog od = new OpbmDialog(opbm, message, caption, OpbmDialog._OKAY_BUTTON, uuid, "");
				if (timeoutSeconds != 0)
				{	// Pause the active thread until the timeout period is reached, or until the user clicks the button
					od.setTimeout(timeoutSeconds);
					Utils.monitorDialogWithTimeout(opbm, uuid, timeoutSeconds);
				}
			}
		};
		t.start();
		return(m_sd_uuid);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getComponent() == m_btnOkay)
		{	// Okay button was clicked
			m_opbm.setDialogResponse(m_id, "okay_button1", this, null);
			m_frame.dispose();

		} else if (e.getComponent() == m_btnCancel) {
			// Cancel button was clicked
			m_opbm.setDialogResponse(m_id, "cancel_button2", this, null);
			m_frame.dispose();

		} else if (e.getComponent() == m_btnYes) {
			// Yes button was clicked
			m_opbm.setDialogResponse(m_id, "yes_button3", this, null);
			m_frame.dispose();

		} else if (e.getComponent() == m_btnNo) {
			// No button was clicked
			m_opbm.setDialogResponse(m_id, "no_button4", this, null);
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
		String response = m_opbm.getDialogResponse(m_id);
		if (response == null || response.isEmpty())
			m_opbm.setDialogResponse(m_id, "cancel", this, null);
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

	public static final int	_ZERO_BUTTONS	= 0;
	public static final int	_OKAY_BUTTON	= 1;
	public static final int	_CANCEL_BUTTON	= 2;
	public static final int	_YES_BUTTON		= 4;
	public static final int	_NO_BUTTON		= 8;
	public static final int	_OKAY_CANCEL	= 3;
	public static final int	_YES_NO			= 12;
	public static final int	_YES_NO_CANCEL	= 14;
	public static final int	_YES_CANCEL		= 6;
	public static final int	_BUTTON1		= 1;
	public static final int	_BUTTON2		= 2;
	public static final int	_BUTTON3		= 4;
	public static final int	_BUTTON4		= 8;
	public static final int _BUTTONS12		= _BUTTON1 + _BUTTON2;
	public static final int _BUTTONS123		= _BUTTON1 + _BUTTON2 + _BUTTON3;
	public static final int _BUTTONS1234	= _BUTTON1 + _BUTTON2 + _BUTTON3 + _BUTTON4;

	private Opbm				m_opbm;
	private String				m_message;
	private String				m_caption;
	private int					m_buttons;
	private String				m_id;
	private DroppableFrame		m_frame;
	private JLayeredPane		m_pan;
	private Timer				m_timer;
	private JLabel				m_lblMessage;
	private JButton				m_btnOkay;
	private JButton				m_btnCancel;
	private JButton				m_btnYes;
	private JButton				m_btnNo;
	private int					m_count;
	private int					m_countMax;
	private JLabel				m_lblCountdown;
}
