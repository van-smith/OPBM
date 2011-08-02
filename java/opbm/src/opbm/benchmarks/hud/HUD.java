/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for benchmarking.  It executes scripts,
 * shows the heads-up display, displays the single-step debugger, etc.
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

package opbm.benchmarks.hud;

import opbm.common.DroppableFrame;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import opbm.Opbm;

public final class HUD extends DroppableFrame
{
	public HUD(Opbm		opbm,
			   boolean	isZoomWindow)
	{
		super(opbm, isZoomWindow);
		createHUD();
	}

	/**
	 * Creates a heads up display for execution on the benchmark runs
	 */
	public void createHUD()
	{
		int actualWidth, actualHeight;
		Dimension size;

		setTitle("OPBM HUD");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(320, 200);

		pack();
        Insets fi		= getInsets();
		actualWidth		= 320 + fi.left + fi.right;
		actualHeight	= 200 + fi.top  + fi.bottom;
        size = new Dimension(actualWidth, actualHeight);
        setMinimumSize(size);
        setMaximumSize(size);
        setPreferredSize(size);
        setSize(size);
		setAlwaysOnTop(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width - actualWidth - 5, dim.height - actualHeight - 5);
		setTransparency(m_opbm.getSettingsMaster().getHUDTranslucency());
		setLayout(null);	// We handle all redraws

		Container c = getContentPane();

		m_pan = new JLayeredPane();
		m_pan.setLayout(null);
		m_pan.setBounds(0, 0, 320, 200);
		m_pan.setVisible(true);
		m_pan.setBorder(BorderFactory.createEmptyBorder());
		c.add(m_pan);

		// Create the header image
		m_background = new JLabel();
		try {
			m_background.setIcon(new ImageIcon(ImageIO.read(new File(Opbm.locateFile("hud.png")))));

		} catch (IOException ex) {
			m_background.setText("missing hud.png");	// Nothing to do really, indicates an improper installation
		}
		m_background.setBounds(0, 0, 320, 200);
		m_background.setHorizontalAlignment(JLabel.LEFT);
		m_background.setOpaque(false);
		m_background.setVisible(true);
		m_pan.add(m_background);

		Font f0;
		if (m_opbm.isFontOverride())
			f0	= new Font("Arial", Font.PLAIN, 8);
		else
			f0	= new Font("Calibri", Font.PLAIN, 10);

		Font f1;
		if (m_opbm.isFontOverride())
			f1	= new Font("Arial", Font.PLAIN, 12);
		else
			f1	= new Font("Calibri", Font.PLAIN, 14);

		Font f2;
		if (m_opbm.isFontOverride())
			f2	= new Font("Arial", Font.PLAIN, 16);
		else
			f2	= new Font("Calibri", Font.PLAIN, 18);

		// Add the dialog areas, there are four, and the graphic:
		// 1) Name area (holds the name of the atom, molecule, scenario or suite being run)
		// 2) Counter (an area across the middle, above the progress bar)
		// 3) Status updates (2 lines, for status, timing and error entries)
		// 4) Debugger area (4 lines, which shows debug data)
		// 5) Icon showing the benchmark is in progress
		m_name = new JLabel();
		m_name.setBounds(9, 25, 237 - 9, 38 - 25);
		m_name.setHorizontalAlignment(JLabel.RIGHT);
		m_name.setVisible(true);
		m_name.setText("");
		m_name.setForeground(new Color(255,255,255));
		m_name.setFont(f0);
		m_pan.add(m_name);
		m_pan.moveToFront(m_name);

		m_status1	= new JLabel();
		m_status1.setBounds(4, 47, 312, 20);
		m_status1.setHorizontalAlignment(JLabel.LEFT);
		m_status1.setVisible(true);
		m_status1.setText("");
		m_status1.setFont(f1);
		m_pan.add(m_status1);
		m_pan.moveToFront(m_status1);

		m_status2	= new JLabel();
		m_status2.setBounds(4, 67, 312, 20);
		m_status2.setHorizontalAlignment(JLabel.LEFT);
		m_status2.setVisible(true);
		m_status2.setText("Status updates...");
		m_status2.setFont(f1);
		m_pan.add(m_status2);
		m_pan.moveToFront(m_status2);

		m_debug1	= new JLabel();
		m_debug1.setBounds(4, 115, 312, 20);
		m_debug1.setHorizontalAlignment(JLabel.LEFT);
		m_debug1.setVisible(true);
		m_debug1.setText("");
		m_debug1.setFont(f1);
		m_pan.add(m_debug1);
		m_pan.moveToFront(m_debug1);

		m_debug2	= new JLabel();
		m_debug2.setBounds(4, 135, 312, 20);
		m_debug2.setHorizontalAlignment(JLabel.LEFT);
		m_debug2.setVisible(true);
		m_debug2.setText("");
		m_debug2.setFont(f1);
		m_pan.add(m_debug2);
		m_pan.moveToFront(m_debug2);

		m_debug3	= new JLabel();
		m_debug3.setBounds(4, 155, 312, 20);
		m_debug3.setHorizontalAlignment(JLabel.LEFT);
		m_debug3.setVisible(true);
		m_debug3.setText("");
		m_debug3.setFont(f1);
		m_pan.add(m_debug3);
		m_pan.moveToFront(m_debug3);

		m_debug4	= new JLabel();
		m_debug4.setBounds(4, 175, 312, 20);
		m_debug4.setHorizontalAlignment(JLabel.LEFT);
		m_debug4.setVisible(true);
		m_debug4.setText("Debug data...");
		m_debug4.setFont(f1);
		m_pan.add(m_debug4);
		m_pan.moveToFront(m_debug4);

		m_counter	= new JLabel();
		m_counter.setBounds(2, 88, 316, 24);
		m_counter.setVerticalAlignment(JLabel.CENTER);
		m_counter.setHorizontalAlignment(JLabel.CENTER);
		m_counter.setVisible(true);
		m_counter.setText("");
		m_counter.setForeground(new Color(255,255,255));
		m_counter.setFont(f2);
		m_pan.add(m_counter);
		m_pan.moveToFront(m_counter);
	}

	public void updateName(String line)
	{
		if (m_name != null)
		{
			updateDefaults();
			m_name.setText(line);
		}
	}

	public void updateStatus(String line)
	{
		if (m_status1 != null)
		{
			updateDefaults();
			m_status1.setText(m_status2.getText());
			m_status1.setForeground(m_status2.getForeground());
			m_status2.setText(line);
			m_status2.setForeground(new Color(0,0,0));
		}
	}

	public void updateTiming(String line)
	{
		if (m_status1 != null)
		{
			updateDefaults();
			m_status1.setText(m_status2.getText());
			m_status1.setForeground(m_status2.getForeground());
			m_status2.setText(line);
			m_status2.setForeground(new Color(0,0,192));
		}
	}

	public void updateError(String line)
	{
		if (m_debug1 != null)
		{
			updateDefaults();
			m_debug1.setText(m_debug2.getText());
			m_debug1.setForeground(m_debug2.getForeground());
			m_debug2.setText(m_debug3.getText());
			m_debug2.setForeground(m_debug3.getForeground());
			m_debug3.setText(m_debug4.getText());
			m_debug3.setForeground(m_debug4.getForeground());
			m_debug4.setText(line);
			m_debug4.setForeground(new Color(192,0,0));
		}
	}

	public void updateDebug(String line)
	{
		if (m_debug1 != null)
		{
			updateDefaults();
			m_debug1.setText(m_debug2.getText());
			m_debug1.setForeground(m_debug2.getForeground());
			m_debug2.setText(m_debug3.getText());
			m_debug2.setForeground(m_debug3.getForeground());
			m_debug3.setText(m_debug4.getText());
			m_debug3.setForeground(m_debug4.getForeground());
			m_debug4.setText(line);
			m_debug4.setForeground(new Color(0,0,0));
		}
	}

	public void updateCounter(String line)
	{
		if (m_counter != null)
		{
			updateDefaults();
			m_counter.setText(line);
		}
	}

	public void updateDefaults()
	{
		if (m_status2.getText().equalsIgnoreCase("Status updates..."))
			m_status2.setText("");

		if (m_debug4.getText().equalsIgnoreCase("Debug data..."))
			m_debug4.setText("");
	}

	private JLabel			m_background;
	private JLabel			m_name;
	private JLabel			m_status1;
	private JLabel			m_status2;
	private JLabel			m_debug1;
	private JLabel			m_debug2;
	private JLabel			m_debug3;
	private JLabel			m_debug4;
	private JLabel			m_counter;
	private JLayeredPane	m_pan;
}
