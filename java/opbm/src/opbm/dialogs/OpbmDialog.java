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
import opbm.Opbm;
import opbm.graphics.AlphaImage;

/**
 *
 * @author rick
 */
public final class OpbmDialog
					implements MouseListener,
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
		m_opbm.initializeDialogResponse(id, triggerCommand);
		createDialogWindow();
	}

	public void createDialogWindow()
	{
		Dimension prefSize;
		JLayeredPane pan;
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

		pan = new JLayeredPane();
		pan.setLayout(null);
		pan.setBounds(0, 0, width, height);
		pan.setVisible(true);
		pan.setBorder(BorderFactory.createEmptyBorder());
		c.add(pan);
		AlphaImage img = new AlphaImage(Opbm.locateFile("inquiry_background.png"));

		// Set the background image
		lblBackground = new JLabel();
		lblBackground.setBounds(0, 0, width, height);
		lblBackground.setHorizontalAlignment(JLabel.LEFT);
		lblBackground.setVerticalAlignment(JLabel.TOP);
		lblBackground.setVisible(true);
		lblBackground.setIcon(new ImageIcon(img.getBufferedImage()));
		pan.add(lblBackground);
		pan.moveToFront(lblBackground);

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
		pan.add(m_lblMessage);
		pan.moveToFront(m_lblMessage);

		// Determine which buttons are specified
		buttonCount = 0;
		if ((m_buttons & _OKAY_BUTTON) != 0)
		{	// Create the okay button, to be positioned below
			m_btnOkay = new JButton("Okay");
			m_btnOkay.setFont(fontButtons);
			m_btnOkay.addMouseListener(this);
			inset = m_btnOkay.getInsets();
			inset.left = 3;
			inset.right = 3;
			m_btnOkay.setMargin(inset);
			pan.add(m_btnOkay);
			pan.moveToFront(m_btnOkay);
			++buttonCount;
		}
		if ((m_buttons & _CANCEL_BUTTON) != 0)
		{	// Create the cancel button, to be positioned below
			m_btnCancel = new JButton("Cancel");
			m_btnCancel.setFont(fontButtons);
			m_btnCancel.addMouseListener(this);
			inset = m_btnCancel.getInsets();
			inset.left = 3;
			inset.right = 3;
			m_btnCancel.setMargin(inset);
			pan.add(m_btnCancel);
			pan.moveToFront(m_btnCancel);
			++buttonCount;
		}
		if ((m_buttons & _YES_BUTTON) != 0)
		{	// Create the yes button, to be positioned below
			m_btnYes = new JButton("Yes");
			m_btnYes.setFont(fontButtons);
			m_btnYes.addMouseListener(this);
			inset = m_btnYes.getInsets();
			inset.left = 3;
			inset.right = 3;
			m_btnYes.setMargin(inset);
			pan.add(m_btnYes);
			pan.moveToFront(m_btnYes);
			++buttonCount;
		}
		if ((m_buttons & _NO_BUTTON) != 0)
		{	// Create the no button, to be positioned below
			m_btnNo = new JButton("No");
			m_btnNo.setFont(fontButtons);
			m_btnNo.addMouseListener(this);
			inset = m_btnNo.getInsets();
			inset.left = 3;
			inset.right = 3;
			m_btnNo.setMargin(inset);
			pan.add(m_btnNo);
			pan.moveToFront(m_btnNo);
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

		m_frame.setVisible(true);
		m_frame.forceWindowToHaveFocus();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getComponent() == m_btnOkay)
		{	// Okay button was clicked
			m_opbm.setDialogResponse(m_id, "okay");
			m_frame.dispose();

		} else if (e.getComponent() == m_btnCancel) {
			// Cancel button was clicked
			m_opbm.setDialogResponse(m_id, "cancel");
			m_frame.dispose();

		} else if (e.getComponent() == m_btnYes) {
			// Yes button was clicked
			m_opbm.setDialogResponse(m_id, "yes");
			m_frame.dispose();

		} else if (e.getComponent() == m_btnNo) {
			// No button was clicked
			m_opbm.setDialogResponse(m_id, "no");
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
		m_opbm.setDialogResponse(m_id, "cancel");
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

	public static final int	_OKAY_BUTTON	= 1;
	public static final int	_CANCEL_BUTTON	= 2;
	public static final int	_YES_BUTTON		= 4;
	public static final int	_NO_BUTTON		= 8;
	public static final int	_OKAY_CANCEL	= 3;
	public static final int	_YES_NO			= 12;
	public static final int	_YES_NO_CANCEL	= 14;
	public static final int	_YES_CANCEL		= 6;

	private Opbm				m_opbm;
	private String				m_message;
	private String				m_caption;
	private int					m_buttons;
	private String				m_id;
	public DroppableFrame		m_frame;
	public JLabel				m_lblMessage;
	public JButton				m_btnOkay;
	public JButton				m_btnCancel;
	public JButton				m_btnYes;
	public JButton				m_btnNo;
}
