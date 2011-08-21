/*
 * OPBM - Office Productivity Benchmark
 *
 * This class handles all HUD-based processing, for the heads-up-display.  It
 * allows for interaction between the user and the executing scripts.  And,
 * provides for early termination, debugging, etc.
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

import java.awt.event.MouseEvent;
import opbm.dialogs.DroppableFrame;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import opbm.Opbm;
import opbm.benchmarks.BenchmarkParams;
import opbm.common.Utils;
import opbm.graphics.AlphaImage;
import opbm.graphics.AnimateImageTask;

public final class HUD extends DroppableFrame
					implements MouseListener
{
	public HUD(Opbm					opbm,
			   BenchmarkParams		bp,
			   boolean				isZoomWindow)
	{
		super(opbm, isZoomWindow);
		m_bp		= bp;
		m_rebooting	= false;
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

		m_status1 = new JLabel();
		m_status1.setBounds(4, 47, 312, 20);
		m_status1.setHorizontalAlignment(JLabel.LEFT);
		m_status1.setVisible(true);
		m_status1.setText("");
		m_status1.setFont(f1);
		m_pan.add(m_status1);
		m_pan.moveToFront(m_status1);

		m_status2 = new JLabel();
		m_status2.setBounds(4, 67, 312, 20);
		m_status2.setHorizontalAlignment(JLabel.LEFT);
		m_status2.setVisible(true);
		m_status2.setText("Status updates...");
		m_status2.setFont(f1);
		m_pan.add(m_status2);
		m_pan.moveToFront(m_status2);

		m_debug1 = new JLabel();
		m_debug1.setBounds(4, 115, 312, 20);
		m_debug1.setHorizontalAlignment(JLabel.LEFT);
		m_debug1.setVisible(true);
		m_debug1.setText("");
		m_debug1.setFont(f1);
		m_pan.add(m_debug1);
		m_pan.moveToFront(m_debug1);

		m_debug2 = new JLabel();
		m_debug2.setBounds(4, 135, 312, 20);
		m_debug2.setHorizontalAlignment(JLabel.LEFT);
		m_debug2.setVisible(true);
		m_debug2.setText("");
		m_debug2.setFont(f1);
		m_pan.add(m_debug2);
		m_pan.moveToFront(m_debug2);

		m_debug3 = new JLabel();
		m_debug3.setBounds(4, 155, 312, 20);
		m_debug3.setHorizontalAlignment(JLabel.LEFT);
		m_debug3.setVisible(true);
		m_debug3.setText("");
		m_debug3.setFont(f1);
		m_pan.add(m_debug3);
		m_pan.moveToFront(m_debug3);

		m_debug4 = new JLabel();
		m_debug4.setBounds(4, 175, 312, 20);
		m_debug4.setHorizontalAlignment(JLabel.LEFT);
		m_debug4.setVisible(true);
		m_debug4.setText("Debug data...");
		m_debug4.setFont(f1);
		m_pan.add(m_debug4);
		m_pan.moveToFront(m_debug4);

		m_counter = new JLabel();
		m_counter.setBounds(2, 88, 316, 24);
		m_counter.setVerticalAlignment(JLabel.CENTER);
		m_counter.setHorizontalAlignment(JLabel.CENTER);
		m_counter.setVisible(true);
		m_counter.setText("");
		m_counter.setForeground(new Color(255,255,255));
		m_counter.setFont(f2);
		m_pan.add(m_counter);
		m_pan.moveToFront(m_counter);

		// Create the stop control and load its images
		m_stop						= new JLabel();
		m_stopNeutral1				= new AlphaImage(Opbm.locateFile("hud_stop_neutral1.png"));
		m_stopNeutral2				= new AlphaImage(Opbm.locateFile("hud_stop_neutral2.png"));
		m_stopOver1					= new AlphaImage(Opbm.locateFile("hud_stop_over1.png"));
		m_stopOver2					= new AlphaImage(Opbm.locateFile("hud_stop_over2.png"));
		m_flashStopWarningImage1	= new AlphaImage(Opbm.locateFile("hud_flash1.png"));
		m_flashStopWarningImage2	= new AlphaImage(Opbm.locateFile("hud_flash2.png"));

		// Setup the stop control
		m_stop.setBounds(270, 6, 44, 35);
		m_stop.setVerticalAlignment(JLabel.CENTER);
		m_stop.setHorizontalAlignment(JLabel.CENTER);
		m_stop.setVisible(true);
		m_stop.setText("");
		setupAnimateNeutralTask();
		m_stop.addMouseListener(this);
		m_pan.add(m_stop);
		m_pan.moveToFront(m_stop);
	}

	public void updateName(String line)
	{
		updateName(line, line);
	}
	public void updateName(String line, String original)
	{
		if (m_name != null)
		{
			updateDefaults();
			m_name.setText(properlyFormat(line, original));
		}
	}

	public void updateStatus(String line)
	{
		updateStatus(line, line);
	}
	public void updateStatus(String line, String original)
	{
		if (m_status1 != null)
		{
			updateDefaults();
			m_status1.setText(m_status2.getText());
			m_status1.setForeground(m_status2.getForeground());
			m_status2.setText(properlyFormat(line, original));
			m_status2.setForeground(new Color(0,0,0));
		}
	}

	public void updateTiming(String line)
	{
		updateTiming(line, line);
	}
	public void updateTiming(String line, String original)
	{
		if (m_status1 != null)
		{
			updateDefaults();
			m_status1.setText(m_status2.getText());
			m_status1.setForeground(m_status2.getForeground());
			m_status2.setText(properlyFormat(line, original));
			m_status2.setForeground(new Color(0,0,192));
		}
	}

	public void updateError(String line)
	{
		updateError(line, line);
	}
	public void updateError(String line, String original)
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
			m_debug4.setText(properlyFormat(line, original));
			m_debug4.setForeground(new Color(192,0,0));
		}
	}

	public void updateDebug(String line)
	{
		updateDebug(line, line);
	}
	public void updateDebug(String line, String original)
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
			m_debug4.setText(properlyFormat(line, original));
			m_debug4.setForeground(new Color(0,0,0));
		}
	}

	public void updateCounter(String line)
	{
		updateCounter(line, line);
	}
	public void updateCounter(String line, String original)
	{
		if (m_counter != null)
		{
			updateDefaults();
			m_counter.setText(properlyFormat(line, original));
		}
	}

	public String properlyFormat(String line, String original)
	{
		String formattedLine;
		String finishLine = "status,timerfinish: total runtime,";
		String numbers;
		double value;
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);

		formattedLine = line;
		if (original.toLowerCase().startsWith("timing,"))
		{	// timing lines needs to be adjusted for display purposes
			m_bp.extractTimingLineElements(line);
			formattedLine = m_bp.m_timingName + ":  " + nf.format(m_bp.m_timingInSeconds) + "s, " + nf.format(m_bp.m_timingOfBaseline) + "%";

		} else if (original.toLowerCase().startsWith(finishLine)) {
			// Input line is: "status,TimerFinish: Total Runtime,28.3679337510449"
			try
			{	// We try to do the conversion, if it doesn't work, we'll just display the number with full precision, no biggie
				numbers	= Utils.extractOnlyNumbersWithCommasPeriodsAndSignsWholeString(original).replace(",", "");
				value	= Double.valueOf(numbers);
				formattedLine = "Script finished. Total time:  " + nf.format(value) + "s";
			} catch (Throwable t) {
			}
		}
		return(formattedLine);
	}

	public void updateDefaults()
	{
		if (m_status2.getText().equalsIgnoreCase("Status updates..."))
			m_status2.setText("");

		if (m_debug4.getText().equalsIgnoreCase("Debug data..."))
			m_debug4.setText("");
	}

	public void killAnimatedTask()
	{
		if (m_animatedButton != null)
		{
			m_animatedButton.pause();
			m_animatedButton = null;	// Set up for garbage collection
		}
	}

	public void setupAnimateNeutralTask()
	{
		killAnimatedTask();
		m_animatedButton = new AnimateImageTask();
		m_animatedButton.add(m_stopNeutral1);
		m_animatedButton.add(m_stopNeutral2);
		m_animatedButton.animateComponent(m_stop, 500);
	}

	public void setupAnimateStopTask()
	{
		killAnimatedTask();
		m_animatedButton = new AnimateImageTask();
		m_animatedButton.add(m_stopOver1);
		m_animatedButton.add(m_stopOver2);
		m_animatedButton.animateComponent(m_stop, 500);
	}

	public void setupAnimateFlashingStopWarningTask()
	{
		killAnimatedTask();
		m_animatedButton = new AnimateImageTask();
		m_animatedButton.add(m_flashStopWarningImage1);
		m_animatedButton.add(m_flashStopWarningImage2);
		m_animatedButton.setupCallback(this, "hud", null);
		m_animatedButton.animateComponent(m_stop, 250);
	}

	public void animateImageTaskCallback(Object obj)
	{
		String msg = "Attempting to stop, please wait...";

		if (!m_rebooting)
		{
			updateCounter(msg, msg);
			updateStatus(msg, msg);
			updateError(msg, msg);
			updateTiming(msg, msg);
			updateDebug(msg, msg);
		}
	}

	public void setRebooting()
	{
		m_rebooting = true;
		killAnimatedTask();
		setupAnimateFlashingStopWarningTask();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getComponent().equals(m_stop))
		{	// Pressed on the stop button
			setupAnimateFlashingStopWarningTask();
			Opbm.stopProcesses();
			m_bp.m_debuggerOrHUDAction = BenchmarkParams._STOP;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		if (e.getComponent().equals(m_stop))
		{	// Came on top of the stop button
			if (m_bp.m_debuggerOrHUDAction < BenchmarkParams._STOP)
			{
				killAnimatedTask();
				setupAnimateStopTask();
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		if (e.getComponent().equals(m_stop))
		{	// Left the stop button
			if (m_bp.m_debuggerOrHUDAction < BenchmarkParams._STOP)
			{	// Reset it back to its neutral animation
				setupAnimateNeutralTask();
			}
		}
	}

	private BenchmarkParams		m_bp;
	private boolean				m_rebooting;
	private JLabel				m_background;
	private JLabel				m_name;
	private JLabel				m_status1;
	private JLabel				m_status2;
	private JLabel				m_debug1;
	private JLabel				m_debug2;
	private JLabel				m_debug3;
	private JLabel				m_debug4;
	private JLabel				m_counter;
	private JLabel				m_stop;
	private AlphaImage			m_stopOver1;
	private AlphaImage			m_stopOver2;
	private AlphaImage			m_stopNeutral1;
	private AlphaImage			m_stopNeutral2;
	private JLayeredPane		m_pan;
	private AnimateImageTask	m_animatedButton;
	private AlphaImage			m_flashStopWarningImage1;
	private AlphaImage			m_flashStopWarningImage2;
}
