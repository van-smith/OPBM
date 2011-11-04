/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is used to create a compilation.  A compilation visually appears
 * to be a listbox, and contains a list of atoms, molecules, scenarios or suites
 * to execute in any order as part of a compilation.  General format:
 *
 *		+---------------------+
 *		|      +------------+ |
 *		|+---+ |Listbox     | |
 *		|| + | |            | |         + adds the item with 1 iteration
 *		|+---+ |            | |         +N adds the item with N iterations
 *		|+---+ |            | |         - removes the item
 *		||+N | |            | |
 *		|+---+ |            | |         Clear clears all items
 *		|+---+ |            | |         Up moves the highlighted entry up
 *		|| - | |            | |         Down moves the highlighted entry down
 *		|+---+ |            | |
 *		|      +------------+ |
 *		|  +-----+ +--+ +--+  |
 *		|  |Clear| |Up| |Dn|  |
 *		|  +-----+ +--+ +--+  |
 *		+---------------------+
 *
 * Last Updated:  Nov 04, 2011
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

package opbm.panels.right;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import opbm.common.Commands;
import opbm.common.Macros;
import opbm.Opbm;
import opbm.common.Tuple;
import opbm.common.Utils;
import opbm.common.Xml;
import opbm.dialogs.OpbmDialog;
import opbm.dialogs.OpbmInput;

public class PanelRightCompilation
						implements	ListSelectionListener,
									MouseListener,
									KeyListener
{
	public PanelRightCompilation(Opbm				opbm,
								 JPanel				parentPanel,
								 PanelRight			parentPR,
								 PanelRightItem		parentPRI,
								 Commands			commandMaster,
								 Macros				macroMaster)
	{
		m_xmlCompilationMaster	= null;
		m_visible				= false;
		m_opaque				= true;
		m_foreground			= Color.BLACK;
		m_background			= Color.WHITE;
		m_font					= null;
		m_width					= 0;
		m_height				= 0;
		m_x						= 0;
		m_y						= 0;
		m_opbm					= opbm;
		m_parentPanel			= parentPanel;
		m_parentPR				= parentPR;
		m_parentPRI				= parentPRI;
		m_commandMaster			= commandMaster;
		m_macroMaster			= macroMaster;
		m_compilationBox		= null;
		m_compilationScroller	= null;
		m_compilationName		= "";

		m_compilationInCommand	= "";
		m_compilationInCommandP1	= "";
		m_compilationInCommandP2	= "";
		m_compilationInCommandP3	= "";
		m_compilationInCommandP4	= "";
		m_compilationInCommandP5	= "";
		m_compilationInCommandP6	= "";
		m_compilationInCommandP7	= "";
		m_compilationInCommandP8	= "";
		m_compilationInCommandP9	= "";
		m_compilationInCommandP10	= "";
	}

	/**
	 * Updates the Compilation (typically during instantiation) to set the
	 * buttons.
	 */
	public void updateCompilation()
	{
		boolean in, subtract, clear, up, down, iteration;
		int buttonWidth = 0;
		int count = 0;
		int width, left, top;
		Insets inset;
		boolean destroyIn			= true;
		boolean destroySubtract		= true;
		boolean destroyClear		= true;
		boolean destroyUp			= true;
		boolean destroyDown			= true;
		boolean destroyIteration	= true;
		Dimension d;
		Font f;

		if (m_opbm.isFontOverride())
			f	= new Font("Arial", Font.BOLD, 12);
		else
			f	= new Font("Calibri", Font.BOLD, 14);

		// A future revision could allow these to be turned off
		in			= true;
		subtract	= true;
		clear		= true;
		up			= true;
		down		= true;
		iteration	= true;

		if (in || subtract)
		{	// These buttons are displayed along the left-side
			if (in)
			{	// The in button exists
				destroyIn = false;
				if (m_compilationIn == null) {
					m_compilationIn = new JButton(">");
					m_compilationIn.setFont(f);
					m_compilationIn.addMouseListener(this);
					m_compilationIn.setToolTipText("Append the highlighted Atom, Molecule, Scenario or Suite into the compilation list after its highlighted item");
					inset = m_compilationIn.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_compilationIn.setMargin(inset);
					m_parentPanel.add(m_compilationIn);
				}
			}

			if (subtract)
			{	// The delete button exists
				destroySubtract = false;
				if (m_compilationSubtract == null) {
					m_compilationSubtract = new JButton("<");
					m_compilationSubtract.setFont(f);
					m_compilationSubtract.addMouseListener(this);
					m_compilationSubtract.setToolTipText("Remove the highlighted item from the compilation");
					inset = m_compilationSubtract.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_compilationSubtract.setMargin(inset);
					m_parentPanel.add(m_compilationSubtract);
				}
			}
		}

		if (clear || up || down || iteration)
		{	// These buttons are displayed along the bottom
			if (clear) {
				// The clone button exists
				destroyClear = false;
				if (m_compilationClear == null) {
					m_compilationClear = new JButton("Clear");
					m_compilationClear.setFont(f);
					m_compilationClear.addMouseListener(this);
					m_compilationClear.setToolTipText("Clear the compilation, reset it to empty");
					inset = m_compilationClear.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_compilationClear.setMargin(inset);
					m_parentPanel.add(m_compilationClear);
				}
				buttonWidth += 55;
				++count;
			}

			if (up) {
				// The up button exists
				destroyUp = false;
				if (m_compilationUp == null) {
					m_compilationUp = new JButton("Up");
					m_compilationUp.setFont(f);
					m_compilationUp.addMouseListener(this);
					m_compilationUp.setToolTipText("Move the highlighted item up one position in the compilation");
					inset = m_compilationUp.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_compilationUp.setMargin(inset);
					m_parentPanel.add(m_compilationUp);
				}
				buttonWidth += 40;
				++count;
			}

			if (down) {
				// The down button exists
				destroyDown = false;
				if (m_compilationDown == null) {
					m_compilationDown = new JButton("Dn");
					m_compilationDown.setFont(f);
					m_compilationDown.addMouseListener(this);
					m_compilationDown.setToolTipText("Move the highlighted item down one position in the compilation");
					inset = m_compilationDown.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_compilationDown.setMargin(inset);
					m_parentPanel.add(m_compilationDown);
				}
				buttonWidth += 40;
				++count;
			}

			if (iteration) {
				// The iteration button exists
				destroyIteration = false;
				if (m_compilationIteration == null) {
					m_compilationIteration = new JButton("(N)");
					m_compilationIteration.setFont(f);
					m_compilationIteration.addMouseListener(this);
					m_compilationIteration.setToolTipText("Change the iteration count of the highlighted item");
					inset = m_compilationIteration.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_compilationIteration.setMargin(inset);
					m_parentPanel.add(m_compilationIteration);
				}
				buttonWidth += 40;
				++count;
			}

			// The in and subtract buttons appear along the left side
			if (in || subtract)
			{
				left	= m_x + 5;
				top		= m_y + (m_height / 2) - 60;
				if (in) {
					d = new Dimension(30,25);
					m_compilationIn.setLocation(left, top);
					m_compilationIn.setMinimumSize(d);
					m_compilationIn.setMaximumSize(d);
					m_compilationIn.setPreferredSize(d);
					m_compilationIn.setSize(d);
					top += 40;
				}

				if (subtract) {
					d = new Dimension(30,25);
					m_compilationSubtract.setLocation(left, top);
					m_compilationSubtract.setMinimumSize(d);
					m_compilationSubtract.setMaximumSize(d);
					m_compilationSubtract.setPreferredSize(d);
					m_compilationSubtract.setSize(d);
				}
			}

			// Determine the relative center location of the defined buttons
			width		= m_width;
			buttonWidth += ((count - 1) * 5);
			left		= m_x + 50 + ((width - 50 - buttonWidth) / 2);
			top			= m_y + m_height - 25;
			if (clear) {
				d = new Dimension(55,25);
				m_compilationClear.setLocation(left, top);
				m_compilationClear.setMinimumSize(d);
				m_compilationClear.setMaximumSize(d);
				m_compilationClear.setPreferredSize(d);
				m_compilationClear.setSize(d);
				left += d.getWidth() + 5;
			}

			if (up) {
				d = new Dimension(40,25);
				m_compilationUp.setLocation(left, top);
				m_compilationUp.setMinimumSize(d);
				m_compilationUp.setMaximumSize(d);
				m_compilationUp.setPreferredSize(d);
				m_compilationUp.setSize(d);
				left += d.getWidth() + 5;
			}

			if (down) {
				d = new Dimension(40,25);
				m_compilationDown.setLocation(left, top);
				m_compilationDown.setMinimumSize(d);
				m_compilationDown.setMaximumSize(d);
				m_compilationDown.setPreferredSize(d);
				m_compilationDown.setSize(d);
				left += d.getWidth() + 5;
			}

			if (iteration) {
				d = new Dimension(50,25);
				m_compilationIteration.setLocation(left, top);
				m_compilationIteration.setMinimumSize(d);
				m_compilationIteration.setMaximumSize(d);
				m_compilationIteration.setPreferredSize(d);
				m_compilationIteration.setSize(d);
				left += d.getWidth() + 5;
			}

			// Adjust the listbox size
			m_height -= 28;
		}
		// If the in button exists, remove it
		if (m_compilationIn != null) {
			if (destroyIn) {
				m_parentPanel.remove(m_compilationIn);
				m_compilationIn = null;
			} else {
				m_compilationIn.setVisible(true);
			}
		}

		// If the subtract button exists, remove it
		if (m_compilationSubtract != null) {
			if (destroySubtract) {
				m_parentPanel.remove(m_compilationSubtract);
				m_compilationSubtract = null;
			} else {
				m_compilationSubtract.setVisible(true);
			}
		}

		// If the clear button exists, remove it
		if (m_compilationClear != null) {
			if (destroyClear) {
				m_parentPanel.remove(m_compilationClear);
				m_compilationClear = null;
			} else {
				m_compilationClear.setVisible(true);
			}
		}

		// If the up button exists, remove it
		if (m_compilationUp != null) {
			if (destroyUp) {
				m_parentPanel.remove(m_compilationUp);
				m_compilationUp = null;
			} else {
				m_compilationUp.setVisible(true);
			}
		}

		// If the down button exists, remove it
		if (m_compilationDown != null) {
			if (destroyDown) {
				m_parentPanel.remove(m_compilationDown);
				m_compilationDown = null;
			} else {
				m_compilationDown.setVisible(true);
			}
		}

		// If the iteration button exists, remove it
		if (m_compilationIteration != null) {
			if (destroyIteration) {
				m_parentPanel.remove(m_compilationIteration);
				m_compilationIteration = null;
			} else {
				m_compilationIteration.setVisible(true);
			}
		}
	}

	/**
	 * Specifies the in button command
	 *
	 * @param command command to execute when the in button is clicked
	 * @param p1 first parameter
	 * @param p2 second parameter
	 * @param p3 third parameter
	 * @param p4 fourth parameter
	 * @param p5 fifth parameter
	 * @param p6 sixth parameter
	 * @param p7 seventh parameter
	 * @param p8 eighth parameter
	 * @param p9 ninth parameter
	 * @param p10 tenth parameter
	 */
	public void setInButton(String command,
							String p1,
							String p2,
							String p3,
							String p4,
							String p5,
							String p6,
							String p7,
							String p8,
							String p9,
							String p10)
	{
		if (!command.isEmpty()) {
			m_compilationInCommand		= command;
			m_compilationInCommandP1	= p1;
			m_compilationInCommandP2	= p2;
			m_compilationInCommandP3	= p3;
			m_compilationInCommandP4	= p4;
			m_compilationInCommandP5	= p5;
			m_compilationInCommandP6	= p6;
			m_compilationInCommandP7	= p7;
			m_compilationInCommandP8	= p8;
			m_compilationInCommandP9	= p9;
			m_compilationInCommandP10	= p10;
		}
	}

	/**
	 * Called to add the named highlighted's item to the compilation after its
	 * highlighted item
	 * @param name
	 */
	public void compilationInCommand(String dataSourceName)
	{
		Xml entry;
		int iterations;
		String tag, name;
		OpbmDialog od;

		entry = m_parentPR.getListboxOrLookupboxNodeByName(dataSourceName);
		if (entry != null)
		{	// Add it if it's an atom, molecule, etc.
			tag		= entry.getName();
			name	= entry.getAttribute("name");
			if (!name.isEmpty())
			{
				if (tag.equalsIgnoreCase("atom"))
				{	// Add an atom
				} else if (tag.equalsIgnoreCase("molecule")) {
					// Add a molecule
				} else if (tag.equalsIgnoreCase("scenario")) {
					// Add a scenario
				} else if (tag.equalsIgnoreCase("suite")) {
					// Add a suite
				} else {
					// An invalid type was found, that we don't know what to do with
					od = new OpbmDialog(m_opbm, true, "Unknown type specified encountered: " + tag, "Failure", OpbmDialog._OKAY_BUTTON, "CompilationInCommand", "");
					return;
				}

				// Ask the user how many iterations
				OpbmInput oi = new OpbmInput(m_opbm, true, "Iteration Count for " + Utils.toProper(tag) + ": " + Utils.toProper(name), "Please specify the iteration count (1 to N):", "1", OpbmInput._ACCEPT_CANCEL, "iteration_count_compilation", "", true);
				Tuple input = oi.readInput();
				if (!((String)input.getSecond("action")).toLowerCase().contains("accept"))
				{	// They did not click the accept button, so they are canceling
					return;
				}
				// If we get here, we have a value
				iterations	= Utils.getValueOf((String)input.getSecond("value"), 0, 0, Integer.MAX_VALUE);
				if (iterations == 0)
				{	// They did not specify a valid value
					return;
				}

				// We're good, Insert it where they are
				m_opbm.insertToCompilationXml(tag, name, iterations);
				fillOrRefillLookupBoxArray();
			}
		}
	}

	/**
	 * Repaints the listbox control.
	 */
	public void repaintCompilation()
	{
		m_compilationBox.repaint();
	}

	/**
	 * Fills or refills the physical listbox, which allows it to load the file
	 * contents, create the node list, and populate and format the control.
	 */
	public void fillOrRefillLookupBoxArray()
	{
		int selected;

		if (m_compilationBox == null)
		{
			m_compilationBox = new JList();
			m_compilationBox.addKeyListener(this);
			m_compilationBox.addListSelectionListener(this);
			m_compilationBox.addMouseListener(this);
			m_compilationBox.setSize(m_width - 50, m_height);
			m_compilationBox.setFont(m_font);
			m_compilationBox.setLocation(m_x + 50, m_y);
			m_compilationBox.setForeground(m_foreground);
			m_compilationBox.setBackground(m_background);
			m_compilationBox.setOpaque(m_opaque);
			PanelRightCompilationRenderer prcr = new PanelRightCompilationRenderer();
			if (prcr != null)
				m_compilationBox.setCellRenderer(prcr);
			m_compilationScroller = new JScrollPane(m_compilationBox);
			m_compilationScroller.addKeyListener(this);
			m_compilationScroller.setSize(m_width - 50, m_height);
			m_compilationScroller.setLocation(m_x + 50, m_y);
			m_compilationScroller.setVisible(true);
			m_compilationBox.setVisible(true);
			m_parentPanel.add(m_compilationScroller);
		}

		m_compilationBox.setListData(m_opbm.getCompilationXml().toArray());
		m_compilationBox.setSelectedIndex(m_opbm.getCompilationHighlightEntry());
	}

	/**
	 * After the user resizes the main window, this function is called to
	 * resize the listbox and its associated buttons (if any).
	 *
	 * @param newWidth
	 * @param newHeight
	 */
	public void afterWindowResize(int newWidth, int newHeight)
	{
		// Compilations are fixed-size fields that's not resized,
		// so this control does nothing
	}

	/** Called to physically remove the listbox, and any add, delete or clone
	 * buttons that are active.
	 */
	public void remove()
	{
		if (m_compilationBox != null)
			m_parentPanel.remove(m_compilationBox);

		if (m_compilationIn != null)
			m_parentPanel.remove(m_compilationIn);

		if (m_compilationSubtract != null)
			m_parentPanel.remove(m_compilationSubtract);

		if (m_compilationClear != null)
			m_parentPanel.remove(m_compilationClear);

		if (m_compilationUp!= null)
			m_parentPanel.remove(m_compilationUp);

		if (m_compilationDown != null)
			m_parentPanel.remove(m_compilationDown);
	}

	/**
	 * Physically selects the specified index.
	 *
	 * @param index item to update
	 */
	public void select(int index)
	{
		if (m_compilationBox != null) {
			m_compilationBox.setSelectedIndex(index);
		}
	}

	/**
	 * Assigns the name defined in edits.xml to the control
	 * @param name
	 */
	public void setName(String name)
	{
		m_compilationName = name;
	}

	public String getName()
	{
		return(m_parentPRI.getName());
	}

	/**
	 * Sets the visible parameter.
	 *
	 * @param visible true or false should this control be displayed
	 */
	public void setVisible(boolean visible) {
		m_visible = visible;
	}

	/**
	 * Specifies the size of the listbox
	 *
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		m_width		= width;
		m_height	= height;
	}

	/**
	 * Specifies the position of the listbox
	 *
	 * @param x
	 * @param y
	 */
	public void setLocation(int x, int y) {
		m_x		= x;
		m_y		= y;
	}

	/**
	 * Specifies the foreground color
	 *
	 * @param color
	 */
	public void setForeground(Color color) {
		m_foreground = color;
	}

	/**
	 * Specifies the background color
	 *
	 * @param color
	 */
	public void setBackground(Color color) {
		m_background = color;
	}

	/**
	 * Specifies whether or not the control is opaque
	 *
	 * @param opaque
	 */
	public void setOpaque(boolean opaque) {
		m_opaque = opaque;
	}

	/**
	 * Specifies the font to use for the control
	 * @param font <code>Font</code> to specify
	 */
	public void setFont(Font font) {
		m_font = font;
	}

	/**
	 * Returns the control width
	 * @return width
	 */
	public int getWidth() {
		return(m_width);
	}

	/**
	 * Returns the control height
	 * @return height
	 */
	public int getHeight() {
		return(m_height);
	}

	/**
	 * Returns the control's x position
	 * @return x
	 */
	public int getX() {
		return(m_x);
	}

	/**
	 * Returns the controls' y position
	 * @return y y coordinate
	 */
	public int getY() {
		return(m_y);
	}

	/**
	 * Returns the control's foreground color
	 * @return <code>Color</code>
	 */
	public Color getForeground() {
		return(m_foreground);
	}

	/**
	 * Returns the control's background color
	 * @return <code>Color</code>
	 */
	public Color getBackground() {
		return(m_background);
	}

	/**
	 * Returns the controls' font
	 * @return <code>Font</code>
	 */
	public Font getFont() {
		return(m_font);
	}

	/**
	 * Returns the location
	 * @return <code>Point</code>
	 */
	public Point getLocation() {
		Point p = new Point(m_x, m_y);
		return(p);
	}

	/**
	 * Returns the size
	 * @return <code>Dimension</code>
	 */
	public Dimension getSize() {
		Dimension d = new Dimension(m_width, m_height);
		return(d);
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

	/**
	 * Not used but required for override
	 *
	 * @param e mouse event
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * When the mouse button is pressed down on an add, delete or clone button.
	 *
	 * @param e mouse event
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		int selected;

		selected = m_compilationBox.getSelectedIndex();
		try {
//////////
// LISTBOX
			if (e.getSource().equals(m_compilationBox))
			{
				// No command

//////////
// IN
			} else if (e.getSource().equals(m_compilationIn)) {
				m_commandMaster.processCommand(this,
											   m_compilationInCommand,
											   m_parentPRI.getName(),
											   m_compilationInCommandP1,
											   m_compilationInCommandP2,
											   m_compilationInCommandP3,
											   m_compilationInCommandP4,
											   m_compilationInCommandP5,
											   m_compilationInCommandP6,
											   m_compilationInCommandP7,
											   m_compilationInCommandP8,
											   m_compilationInCommandP9);

//////////
// DELETE
			} else if (e.getSource().equals(m_compilationSubtract)) {
				// They clicked on the subtract button to remove the highlighted entry
				m_opbm.deleteCompilationHighlightedEntries(m_compilationBox);
				m_opbm.setCompilationHighlightEntry(selected);
				fillOrRefillLookupBoxArray();

//////////
// CLEAR
			} else if (e.getSource().equals(m_compilationClear)) {
				// They want to reset the entire contents
				m_opbm.clearCompilation();
				m_opbm.setCompilationHighlightEntry(0);
				fillOrRefillLookupBoxArray();

//////////
// UP
			} else if (e.getSource().equals(m_compilationUp)) {
				// They want to move the highlighted entry down one
				m_opbm.moveCompilationHighlightedEntryUpOne();
				fillOrRefillLookupBoxArray();

//////////
// DOWN
			} else if (e.getSource().equals(m_compilationDown)) {
				// They want to move the highlighted entry down one
				m_opbm.moveCompilationHighlightedEntryDownOne();
				fillOrRefillLookupBoxArray();

//////////
// ITERATION
			} else if (e.getSource().equals(m_compilationIteration)) {
				// They want to change iterations
				m_opbm.compilationUpdateIterations();
				m_opbm.setCompilationHighlightEntry(selected);
				fillOrRefillLookupBoxArray();

			}

		} catch (UnsupportedOperationException ex) {
		} catch (ClassCastException ex) {
		} catch (NullPointerException ex) {
		} catch (IllegalArgumentException ex) {
		} catch (IndexOutOfBoundsException ex) {
		}
	}

	/**
	 * When the mouse is released on the add, clone or delete button, it sets
	 * focus back on the listbox control.
	 *
	 * @param e
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
//		m_lookupBox.requestFocusInWindow();
	}

	/**
	 * Not used but required for override
	 *
	 * @param e mouse event
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Not used but required for override
	 *
	 * @param e mouse event
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		m_opbm.setCompilationHighlightEntry(m_compilationBox.getSelectedIndex());
	}

	private Opbm			m_opbm;
	private boolean			m_visible;
	private boolean			m_opaque;
	private Color			m_foreground;
	private Color			m_background;
	private Font			m_font;
	private JPanel			m_parentPanel;
	private PanelRight		m_parentPR;
	private PanelRightItem	m_parentPRI;
	private Commands		m_commandMaster;
	private Macros			m_macroMaster;
	private JList			m_compilationBox;
	private JScrollPane		m_compilationScroller;
	private List<Xml>		m_xmlCompilationMaster;

	private int				m_width;
	private int				m_height;
	private int				m_x;
	private int				m_y;

	private JButton			m_compilationIn;				// In button for compilation listboxes
	private JButton			m_compilationSubtract;			// Subtract button for compilation listboxes
	private JButton			m_compilationClear;				// Clear button for compilation listboxes
	private JButton			m_compilationUp;				// Up button for compilation listboxes
	private JButton			m_compilationDown;				// Down button for compilation listboxes
	private JButton			m_compilationIteration;			// Iteration button for compilation listboxes
	private String			m_compilationName;				// Name given to this control in edits.xml

	private String			m_compilationInCommand;
	private String			m_compilationInCommandP1;
	private String			m_compilationInCommandP2;
	private String			m_compilationInCommandP3;
	private String			m_compilationInCommandP4;
	private String			m_compilationInCommandP5;
	private String			m_compilationInCommandP6;
	private String			m_compilationInCommandP7;
	private String			m_compilationInCommandP8;
	private String			m_compilationInCommandP9;
	private String			m_compilationInCommandP10;
}
