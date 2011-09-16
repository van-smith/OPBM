/*
 * OPBM - Office Productivity Benchmark
 *
 * This class handles all HUD-based processing, for the heads-up-display.  It
 * allows for interaction between the user and the executing scripts.  And,
 * provides for early termination, debugging, etc.
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
import java.text.NumberFormat;
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
		super(opbm, isZoomWindow, false);
		m_bp		= bp;
		m_rebooting	= false;
		createHUD();
	}

	/**
	 * Creates a heads up display for execution on the benchmark runs
	 */
	public void createHUD()
	{
		boolean isTranslucent, isJava16orEarlier;
		int actualWidth, actualHeight;
		Dimension size;

		setTitle("OPBM HUD");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		if (m_bp.m_hudDebugInfo)
		{	// We are showing debug information
			m_width		= 320;
			m_height	= 200;

		} else {
			// Just the status information
			m_width		= 320;
			m_height	= 117;
		}
		m_translucency		= m_opbm.getSettingsMaster().getHUDTranslucency();
		isTranslucent		= m_translucency < 1.0;
		isJava16orEarlier	= System.getProperty("java.version").compareTo("1.7") < 0;

		pack();
		if (isJava16orEarlier || !isTranslucent)
		{	// For 1.6 and earlier, always handle it as their translucency windows work with the entire window
			Insets fi		= getInsets();
			actualWidth		= m_width + fi.left + fi.right;
			actualHeight	= m_height + fi.top  + fi.bottom;
		} else {
			// For 1.7 and later, and translucent windows, we don't have any insets affecting the height/width because of the way the new API handles translucency
			actualWidth		= m_width;
			actualHeight	= m_height;
		}
        size = new Dimension(actualWidth, actualHeight);
        setMinimumSize(size);
        setMaximumSize(size);
        setPreferredSize(size);
        setSize(size);
		setAlwaysOnTop(true);
		addMouseListener(this);

		if (!isJava16orEarlier)
		{
			dispose();				// Necessary for translucency in 1.7+
			setUndecorated(true);	// Necessary for translucency in 1.7+
		}
		if (isTranslucent)
			setTranslucency(m_translucency);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width - actualWidth - 5, dim.height - actualHeight - 5);
		setLayout(null);			// We handle all redraws

		Container c = getContentPane();

		m_pan = new JLayeredPane();
		m_pan.setLayout(null);
		m_pan.setBounds(0, 0, m_width, 200);
		m_pan.setVisible(true);
		m_pan.setBorder(BorderFactory.createEmptyBorder());
		c.add(m_pan);

		// Create the header image
		m_background = new JLabel();
		m_backgroundImg	= new AlphaImage(Opbm.locateFile("hud.png"));
		m_background.setIcon(new ImageIcon(m_backgroundImg.getBufferedImage()));
		m_background.setBounds(0, 0, m_width, 200);
		m_background.setHorizontalAlignment(JLabel.LEFT);
		m_background.setVerticalAlignment(JLabel.TOP);
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
		m_stopNeutral1				= new AlphaImage(Opbm.locateFile("hud_neutral1.png"));
		m_stopNeutral2				= new AlphaImage(Opbm.locateFile("hud_neutral2.png"));
		m_flashStopWarningImage1	= new AlphaImage(Opbm.locateFile("hud_flash1.png"));
		m_flashStopWarningImage2	= new AlphaImage(Opbm.locateFile("hud_flash2.png"));

		// Setup the stop control
		m_stop.setBounds(248, 6, 66, 36);
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
		// Bring it back to the front
		setPersistAlwaysOnTop();
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
		m_animatedButton.add(m_stopNeutral1);
		m_animatedButton.animateComponent(m_stop, 500);
	}

	public void setupAnimateStopTask()
	{
		killAnimatedTask();
		m_animatedButton = new AnimateImageTask();
		m_animatedButton.add(m_stopNeutral2);
		m_animatedButton.add(m_stopNeutral2);
		m_animatedButton.animateComponent(m_stop, 500);
	}

	public void setupAnimateFlashingStopWarningTask()
	{
		killAnimatedTask();
		m_animatedButton = new AnimateImageTask();
		m_animatedButton.add(m_flashStopWarningImage1);
		m_animatedButton.add(m_flashStopWarningImage2);
		m_animatedButton.setupCallback(this, "hud", null);
		m_animatedButton.animateComponent(m_stop, 333);
	}

	public void animateImageTaskCallback(Object obj)
	{
		String msg = "Attempting to stop, please wait...";

		if (!m_rebooting)
		{
			updateStatus(msg, msg);
			updateError(msg, msg);
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
			Thread t = new Thread("OPBM_Command_Line_Thread")
			{
				@Override
				public void run()
				{
					Opbm.stopProcesses();
				}
			};
			t.start();
			m_bp.m_debuggerOrHUDAction = BenchmarkParams._STOP_USER_CLICKED_STOP;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		setTranslucency(1.0f);
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
		setTranslucency(m_translucency);
		if (e.getComponent().equals(m_stop))
		{	// Left the stop button
			if (m_bp.m_debuggerOrHUDAction < BenchmarkParams._STOP)
			{	// Reset it back to its neutral animation
				setupAnimateNeutralTask();
			}
		}
	}

	private BenchmarkParams		m_bp;
	private int					m_width;
	private int					m_height;
	private float				m_translucency;
	private boolean				m_rebooting;
	private JLabel				m_background;
	private AlphaImage			m_backgroundImg;
	private JLabel				m_name;
	private JLabel				m_status1;
	private JLabel				m_status2;
	private JLabel				m_debug1;
	private JLabel				m_debug2;
	private JLabel				m_debug3;
	private JLabel				m_debug4;
	private JLabel				m_counter;
	private JLabel				m_stop;
	private AlphaImage			m_stopNeutral1;
	private AlphaImage			m_stopNeutral2;
	private JLayeredPane		m_pan;
	private AnimateImageTask	m_animatedButton;
	private AlphaImage			m_flashStopWarningImage1;
	private AlphaImage			m_flashStopWarningImage2;
}
