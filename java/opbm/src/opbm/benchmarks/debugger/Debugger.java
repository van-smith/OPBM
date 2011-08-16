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

package opbm.benchmarks.debugger;

import opbm.common.Utils;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import opbm.benchmarks.BenchmarkParams;
import opbm.dialogs.DroppableFrame;
import opbm.common.Xml;

public final class Debugger extends DroppableFrame implements KeyListener, MouseListener, MouseMotionListener
{
	public Debugger(BenchmarkParams bp)
	{
		super(bp.m_opbm, false);

		m_bp					= bp;
		m_thisDebuggerStepName	= "(not yet defined)";
		m_mousePosition			= new Dimension(0, 0);
		m_playPosition			= new Rectangle(474, 569, 20, 20);
		m_play1Position			= new Rectangle(502, 569, 29, 20);
		m_stopPosition			= new Rectangle(538, 569, 22, 20);

		createDebugger();
	}

	/**
	 * Creates a debugger display for execution on the benchmark runs
	 */
	public void createDebugger()
	{
		int i, top, actualWidth, actualHeight, lnHeight;
		Dimension size;
		JLabel label;

		setTitle("OPBM Debugger");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(800, 600);

		pack();
        Insets fi = getInsets();
		actualWidth		= 800 + fi.left + fi.right;
		actualHeight	= 600 + fi.top  + fi.bottom;
        size = new Dimension(actualWidth, actualHeight);
        setMinimumSize(size);
        setMaximumSize(size);
        setPreferredSize(size);
        setSize(size);
		setAlwaysOnTop(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width - actualWidth - 5, 5);
		setLayout(null);					// We handle all redraws

		Container c = getContentPane();

		m_pan = new JLayeredPane();
		m_pan.setLayout(null);
		m_pan.setBounds(0, 0, 800, 600);
		m_pan.setVisible(true);
		m_pan.setBorder(BorderFactory.createEmptyBorder());
		m_pan.addKeyListener(this);
		m_pan.addMouseListener(this);
		m_pan.addMouseMotionListener(this);
		c.add(m_pan);

		// Create the header image
		m_background = new JLabel();
		try {
			m_background.setIcon(new ImageIcon(ImageIO.read(new File(m_opbm.locateFile("debug.png")))));

		} catch (IOException ex) {
			m_background.setText("missing debug.png");	// Nothing to do really, indicates an improper installation
		}
		m_background.setBounds(0, 0, 800, 600);
		m_background.setHorizontalAlignment(JLabel.LEFT);
		m_background.setOpaque(false);
		m_background.setVisible(true);
		m_pan.add(m_background);

		// Font for the command area
		Font f0;
		if (m_opbm.isFontOverride())
			f0	= new Font("Arial", Font.PLAIN, 8);
		else
			f0	= new Font("Calibri", Font.PLAIN, 10);

		// Font for the Variables and Parameters areas
		Font f1;
		if (m_opbm.isFontOverride())
			f1	= new Font("Arial", Font.PLAIN, 10);
		else
			f1	= new Font("Calibri", Font.PLAIN, 12);

		// Font for the sequence of operations area
		Font f2;
		if (m_opbm.isFontOverride())
			f2	= new Font("Arial", Font.PLAIN, 14);
		else
			f2	= new Font("Calibri", Font.PLAIN, 16);

		//FontMetrics f0m = getFontMetrics(f0);
		//FontMetrics f1m = getFontMetrics(f1);
		//FontMetrics f2m = getFontMetrics(f2);

		// Add the dialog areas, there are three
		// 1) Sequence of Operations area
		// 2) Variables area
		// 3) Parameters area
		// 4) Output area (each time single step or break, a line appears here)
		m_prefixers		= new ArrayList<JLabel>(0);
		m_sequences		= new ArrayList<JLabel>(0);
		m_variables		= new ArrayList<JLabel>(0);
		m_parameters	= new ArrayList<JLabel>(0);
		m_outputs		= new ArrayList<JLabel>(0);


//////////
// SEQUENCE OF OPERATIONS
		top			= 72;
		lnHeight	= 18;
		for (i = 0; top < 520; i++)
		{
			// Add the identifier
			label = new JLabel();
			label.setBounds(10, top, 85, Utils.truncateHeight(520, top, lnHeight));
			label.setHorizontalAlignment(JLabel.RIGHT);
			label.setVerticalAlignment(JLabel.TOP);
			label.setVisible(true);
			label.setText("");
			label.setBackground(new Color(255,245,235));
			label.setOpaque(true);
			label.setForeground(new Color(106,97,84));
			label.setFont(f2);
			m_pan.add(label);
			m_pan.moveToFront(label);
			m_prefixers.add(label);

			// Add the area for the sequence expanded
			label = new JLabel();
			label.setBounds(95, top, 473, Utils.truncateHeight(520, top, lnHeight));
			label.setHorizontalAlignment(JLabel.LEFT);
			label.setVerticalAlignment(JLabel.TOP);
			label.setVisible(true);
			label.setText("");
			label.setBackground(new Color(255,188,59));		// Bright orange
			label.setOpaque(false);							// Only the current operation is highlighted
			label.setForeground(new Color(106,97,84));
			label.setFont(f2);
			m_pan.add(label);
			m_pan.moveToFront(label);
			m_sequences.add(label);

			// Move down for the next one
			top += lnHeight;
		}

//////////
// VARIABLES
		top = 72;
		for (i = 0; top < 424; i++)
		{
			// Add the identifier
			label = new JLabel();
			label.setBounds(585, top, 210, Utils.truncateHeight(424, top, lnHeight));
			label.setHorizontalAlignment(JLabel.LEFT);
			label.setVerticalAlignment(JLabel.TOP);
			label.setVisible(true);
			label.setText("");
			label.setOpaque(false);							// Only the current operation is highlighted
			label.setForeground(new Color(106,97,84));
			label.setFont(f2);
			m_pan.add(label);
			m_pan.moveToFront(label);
			m_variables.add(label);

			// Move down for the next one
			top += lnHeight;
		}

//////////
// PARAMETERS
		top = 451;
		for (i = 0; top < 592; i++)
		{
			// Add the identifier
			label = new JLabel();
			label.setBounds(585, top, 210, Utils.truncateHeight(592, top, lnHeight));
			label.setHorizontalAlignment(JLabel.LEFT);
			label.setVerticalAlignment(JLabel.TOP);
			label.setVisible(true);
			label.setText("");
			label.setOpaque(false);							// Only the current operation is highlighted
			label.setForeground(new Color(106,97,84));
			label.setFont(f2);
			m_pan.add(label);
			m_pan.moveToFront(label);
			m_parameters.add(label);

			// Move down for the next one
			top += lnHeight;
		}

//////////
// OUTPUTS
		lnHeight = 14;
		top = 592 - lnHeight;
		for (i = 0; top > 548; i++)
		{
			// Add the identifier
			label = new JLabel();
			label.setBounds(10, top, 450, lnHeight);
			label.setHorizontalAlignment(JLabel.LEFT);
			label.setVerticalAlignment(JLabel.TOP);
			label.setVisible(true);
			label.setText("");
			label.setOpaque(false);							// Only the current operation is highlighted
			label.setForeground(new Color(106,97,84));
			label.setFont(f1);
			m_pan.add(label);
			m_pan.moveToFront(label);
			m_outputs.add(label);

			if (i == 0)
			{
				// First time through, add the location where the cursor will receive input
				m_textbox = new JTextField();
				m_textbox.setBounds(10, top, 5, lnHeight);
				m_textbox.setOpaque(false);
				m_textbox.setFont(f1);
				m_textbox.setVisible(true);
				m_textbox.setBorder(BorderFactory.createEmptyBorder());
				m_textbox.addKeyListener(this);
				m_pan.add(m_textbox);
				m_pan.moveToFront(m_textbox);
			}

			// Move down for the next one
			top -= lnHeight;
		}
	}

	public void update()
	{
		Xml parent, child, childRunner;
		int i;

		parent					= m_bp.m_debugParent;
		child					= m_bp.m_debugChild;
		m_thisDebuggerStepName	= parent.getAttributeOrChild("name") + "." + child.getAttributeOrChild("name");

		if (parent.getName().equalsIgnoreCase("atom"))
			updateAtomVariables(m_bp);

		// Show the contents
		m_prefixers.get(0).setText(parent.getName() + ":");
		m_sequences.get(0).setText("  " + parent.getAttributeOrChild("name"));
		m_prefixers.get(0).repaint();
		m_sequences.get(0).repaint();

		// Update the sequence listing
		childRunner = parent.getFirstChild();
		for (i = 1; i < m_variables.size() && childRunner != null; i++)
		{
			// Show the contents
			m_prefixers.get(i).setText(childRunner.getName() + ":");

			// Highlight the current line
			if (child == childRunner)
			{
				m_sequences.get(i).setOpaque(true);
				updateAtomParameters(child);
				m_sequences.get(i).setText("        " + childRunner.getAttributeOrChild("name"));

			} else {
				m_sequences.get(i).setOpaque(false);
				m_sequences.get(i).setText("        " + childRunner.getAttributeOrChild("name"));

			}
			m_prefixers.get(i).repaint();
			m_sequences.get(i).repaint();

			// Move to the next position
			childRunner = childRunner.getNext();
		}

		// Update the output window
		// Shift everything up one
		for (i = m_outputs.size() - 1; i > 0; i--)
		{
			m_outputs.get(i).setText(m_outputs.get(i-1).getText());
			m_outputs.get(i).repaint();
		}

		// Populate the last item
		if (m_bp.m_debugLastAction == BenchmarkParams._SINGLE_STEP)
			m_outputs.get(0).setText("Single step to " + m_thisDebuggerStepName);
		else
			m_outputs.get(0).setText("Break at " + m_thisDebuggerStepName);

		m_outputs.get(0).repaint();
	}

	public void updateAtomVariables(BenchmarkParams bp)
	{
		int i;

		if (!bp.m_atomVariables.isEmpty())
		{
			// Do every variable that will fit in the space
			for (i = 0; i < m_variables.size() && i < bp.m_atomVariables.size(); i++)
			{
				m_variables.get(i).setText((bp.m_atomVariables.get(i).getName() + " = " + bp.m_atomVariables.get(i).getValue()).trim());
				m_variables.get(i).repaint();
			}

		} else {
			m_variables.get(0).setText("No variables");
			m_variables.get(0).repaint();
			i = 1;

		}

		// Clear off the rest
		for ( ; i < m_variables.size(); i++)
		{
			m_variables.get(i).setText("");
			m_variables.get(i).repaint();
		}
	}

	public void updateAtomParameters(Xml child)
	{
		Xml options;
		int i;

		options = child.getChildNode("options");
		if (options != null)
		{
			// Do every variable that will fit in the space
			options = options.getFirstChild();
			if (options != null)
			{
				for (i = 0; i < m_parameters.size() && options != null; i++)
				{
					m_parameters.get(i).setText((options.getName() + " = " + m_bp.m_macroMaster.parseMacros(options.getText())).trim());
					m_parameters.get(i).setToolTipText(m_bp.m_macroMaster.parseMacros(options.getText()).trim());
					m_parameters.get(i).repaint();
					options = options.getNext();
				}

			} else {
				m_parameters.get(0).setText("No parameters");
				m_parameters.get(0).repaint();
				i = 1;

			}

		} else {
			m_parameters.get(0).setText("No parameters");
			m_parameters.get(0).repaint();
			i = 1;

		}

		// Clear off the rest
		for ( ; i < m_parameters.size(); i++)
		{
			m_parameters.get(i).setText("");
			m_parameters.get(i).repaint();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
//////////
// PLAY (GO)
		if (e.getKeyCode() == KeyEvent.VK_F5) {
			debuggerRun();

//////////
// PLAY1 (SINGLE STEP)
		} else if (e.getKeyCode() == KeyEvent.VK_F8) {
			debuggerSingleStep();

//////////
// STOP
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			debuggerStop();

		} else {
			// We ignore other keys
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
//////////
// PLAY (GO)
		if (m_playPosition.contains(m_mousePosition.width, m_mousePosition.height)) {
			debuggerRun();

//////////
// PLAY1 (SINGLE STEP)
		} else if (m_play1Position.contains(m_mousePosition.width, m_mousePosition.height)) {
			debuggerSingleStep();

//////////
// STOP
		} else if (m_stopPosition.contains(m_mousePosition.width, m_mousePosition.height)) {
			debuggerStop();

		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
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
	public void mouseDragged(MouseEvent e) {
		m_mousePosition.width	= e.getX();
		m_mousePosition.height	= e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		m_mousePosition.width	= e.getX();
		m_mousePosition.height	= e.getY();
	}

	public JTextField getCareTextbox()
	{
		return(m_textbox);
	}

	public void debuggerRun()
	{
		m_bp.m_debuggerOrHUDAction = BenchmarkParams._RUN;
		if (m_bp.m_hud != null)
			m_bp.m_hud.updateDebug("Resuming at " + m_thisDebuggerStepName);
	}

	public void debuggerSingleStep()
	{
		m_bp.m_debuggerOrHUDAction = BenchmarkParams._SINGLE_STEP;
		if (m_bp.m_hud != null)
			m_bp.m_hud.updateDebug("Single stepping through " + m_thisDebuggerStepName);
	}

	public void debuggerStop()
	{
		m_bp.m_debuggerOrHUDAction = BenchmarkParams._STOP_USER_CLICKED_STOP;
		if (m_bp.m_hud != null)
			m_bp.m_hud.updateDebug("Stopped at " + m_thisDebuggerStepName);
	}

	private BenchmarkParams	m_bp;

	private JLabel				m_background;
	private List<JLabel>		m_prefixers;
	private List<JLabel>		m_sequences;
	private List<JLabel>		m_variables;
	private List<JLabel>		m_parameters;
	private List<JLabel>		m_outputs;
	private JTextField			m_textbox;
	private JLayeredPane		m_pan;
	private Dimension			m_mousePosition;
	private String				m_thisDebuggerStepName;

	private Rectangle			m_playPosition;			// Play button (go)
	private Rectangle			m_play1Position;		// Play-1 button (single step)
	private Rectangle			m_stopPosition;			// Stop button

}
