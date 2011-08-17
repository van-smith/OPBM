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
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import opbm.Opbm;
import opbm.graphics.AlphaImage;

/**
 *
 * @author rick
 */
public final class OpbmInput
					implements MouseListener,
							   WindowListener
{
	public OpbmInput(Opbm		opbm,
					 String		caption,
					 String		label,
					 String		initValue,
					 int		buttons,
					 String		id,
					 String		triggerCommand)
	{
		m_opbm				= opbm;
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
		m_id			= id;
		m_opbm.initializeDialogResponse(id, triggerCommand);
		createInputWindow();
	}

	public OpbmInput(Opbm		opbm,
					 String		caption,
					 String		label,
					 String		initValue,
					 int		buttons,
					 String		id,
					 String		nextButtonText,
					 String		cancelButtonText,
					 String		acceptButtonText,
					 String		okayButtonText,
					 String		triggerCommand)
	{
		m_opbm			= opbm;
		m_caption		= caption;
		m_label			= label;

		if (initValue == null)
			m_initValue = "";
		else
			m_initValue	= initValue;

		m_buttons		= buttons;
		m_nextButtonText	= nextButtonText.isEmpty()		? "Next"	: nextButtonText;
		m_cancelButtonText	= cancelButtonText.isEmpty()	? "Cancel"	: cancelButtonText;
		m_acceptButtonText	= acceptButtonText.isEmpty()	? "Accept"	: acceptButtonText;
		m_okayButtonText	= okayButtonText.isEmpty()		? "Okay"	: okayButtonText;

		if (id == null)
		{	// They didn't specify an ID, so use the generic one
			id = "input";
		}
		m_id			= id;
		m_opbm.initializeDialogResponse(id, triggerCommand);
		createInputWindow();
	}

	public void createInputWindow()
	{
		Dimension prefSize;
		Insets inset;
		Font fontLabel, fontInput, fontButtons;
		int buttonCenters, buttonBackoff, buttonWidth, buttonCount, thisButton, buttonTop;

		m_width		= 450;
		m_height	= 250;
		m_frame		= new DroppableFrame(m_opbm, false);
		m_frame.setTitle(m_caption.isEmpty() ? "OPBM Input" : m_caption);

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
		AlphaImage img = new AlphaImage(Opbm.locateFile("input_background.png"));

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
		m_pan.add(m_txtInputScroll);
		m_pan.moveToFront(m_txtInputScroll);

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
		buttonTop	= 200;
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

		m_frame.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getComponent() == m_btnNext)
		{	// Next button was clicked
			m_opbm.setDialogResponse(m_id, m_nextButtonText, m_txtInput.getText());
			m_frame.dispose();

		} else if (e.getComponent() == m_btnCancel) {
			// Cancel button was clicked
			m_opbm.setDialogResponse(m_id, m_cancelButtonText, m_txtInput.getText());
			m_frame.dispose();

		} else if (e.getComponent() == m_btnAccept) {
			// Accept button was clicked
			m_opbm.setDialogResponse(m_id, m_acceptButtonText, m_txtInput.getText());
			m_frame.dispose();

		} else if (e.getComponent() == m_btnOkay) {
			// Okay button was clicked
			m_opbm.setDialogResponse(m_id, m_okayButtonText, m_txtInput.getText());
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
		m_opbm.setDialogResponse(m_id, m_cancelButtonText, m_txtInput.getText());
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

	public static final int	_NEXT_BUTTON	= 1;
	public static final int	_CANCEL_BUTTON	= 2;
	public static final int	_ACCEPT_BUTTON	= 4;
	public static final int	_OKAY_BUTTON	= 8;
	public static final int	_NEXT_CANCEL	= 3;
	public static final int	_ACCEPT_CANCEL	= 6;
	public static final int	_OKAY_CANCEL	= 10;

	private Opbm				m_opbm;
	private int					m_actual_width;
	private int					m_actual_height;
	private int					m_width;
	private int					m_height;
	private JLayeredPane		m_pan;
	public DroppableFrame		m_frame;

	public JLabel				m_lblBackground;
	public JLabel				m_lblLabel;
	public JScrollPane			m_txtInputScroll;
	public JTextArea			m_txtInput;

	public JButton				m_btnNext;
	public JButton				m_btnCancel;
	public JButton				m_btnAccept;
	public JButton				m_btnOkay;

	public String				m_nextButtonText;
	public String				m_cancelButtonText;
	public String				m_acceptButtonText;
	public String				m_okayButtonText;

	private String				m_caption;
	private String				m_label;
	private String				m_initValue;
	private int					m_buttons;
	private String				m_id;
}
