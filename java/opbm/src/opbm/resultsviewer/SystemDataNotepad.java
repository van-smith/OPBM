/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class of the OPBM.  It creates a GUI, loads
 * necessary files, beings processing based on context, etc.
 *
 * Last Updated:  July 18, 2011
 *
 * by Van Smith, Rick C. Hodgin
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @author Rick C. Hodgin
 * @version 1.0.1
 *
 */

package opbm.resultsviewer;

import opbm.dialogs.DroppableFrame;
import opbm.graphics.AlphaImage;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import opbm.Opbm;

/**
 *
 * @author rick
 */
public final class SystemDataNotepad implements MouseListener
{
	public SystemDataNotepad(Opbm			opbm,
							 ResultsViewer	rv)
	{
		m_opbm	= opbm;
		m_rv	= rv;
		createSystemDataNotepad();
	}

	public void createSystemDataNotepad()
	{
		Dimension prefSize;
		JLayeredPane pan;
		JLabel lblBackground;
		int actual_width, actual_height, width, height;

		width = 450;
		height = 200;
		m_frame = new DroppableFrame(m_opbm, false, false);
		m_frame.setTitle("OPBM - Export Data");

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
		m_frame.setLocationRelativeTo(null);  // Center window
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

		Font font = new Font("Calibri", Font.BOLD, 20);

		m_text = new JButton("Save as Text");
		m_text.setBounds(30, img.getHeight() - 60, (img.getWidth() / 2) - 50, 40);
		m_text.setFont(font);
		m_text.addMouseListener(this);
		m_text.setVisible(true);
		pan.add(m_text);
		pan.moveToFront(m_text);

		m_csv = new JButton("Save as CSV");
		m_csv.setBounds((img.getWidth() / 2) + 15, img.getHeight() - 60, (img.getWidth() / 2) - 50, 40);
		m_csv.setFont(font);
		m_csv.addMouseListener(this);
		m_csv.setVisible(true);
		pan.add(m_csv);
		pan.moveToFront(m_csv);

		m_frame.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getComponent() == m_text)
		{	// Write as a text file
			m_frame.dispose();

		} else if (e.getComponent() == m_csv) {
			// Write as a CSV file
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

	public Opbm				m_opbm;
	public DroppableFrame	m_frame;
	public ResultsViewer	m_rv;
	public JButton			m_text;
	public JButton			m_csv;
}
