/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for all items which are available on
 * the left-panel items (PanelLeft is their parent).  The left-panel menu
 * system divides into two main sections, the navigation area at the top,
 * and the rest of the pane, which can have variable items defined within.
 * There are header items which appear larger, menu links, and text items.
 * Additional items can be defined, such as icons, but they are not
 * currently defined.  Macros can also be used which are refreshed
 * periodically from OPBM system variables (in Settings and Macros).
 *
 * Last Updated:  Jun 24, 2011
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

package opbm.panels;

import java.awt.Font;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Label;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import opbm.common.Macros;
import opbm.Opbm;

/**
 * Handles subordinate items in the navigation panel, including navigable items,
 * headers, links, spacers and text items.
 */
public class PanelLeftItem implements MouseListener {
	/** Constructor creates default size, <code>JLabel</code> object (and links
	 * object to parent), stores variables related to parent.
	 *
	 * @param opbm Parent object referenced for global method calls
	 * @param macroMaster Macro object referenced directly for expanding macros
	 * @param statusBar Status bar on main <code>JFrame</code> object, updated with tooltip text
	 * @param parent Parent <code>JPanel</code> object to which this PanelLeftItem's <code>JLabel</code> object will be added.
	 */
	PanelLeftItem(Opbm opbm, Macros macroMaster, Label statusBar, JPanel parent) {
		m_text			= "";
		m_foreColor		= Color.BLACK;
		m_backColor		= Color.WHITE;
		m_visible		= true;
		m_opaque		= false;
		m_font			= null;
		m_position		= null;
		m_size			= new Dimension(240, 20);
		m_opbm			= opbm;
		m_macroMaster	= macroMaster;
		m_parent		= parent;
		m_statusBar		= statusBar;
		m_label			= new JLabel();
		m_label.addMouseListener(this);
		m_label.setVisible(false);
		parent.add(m_label);
		m_command		= "";
		m_commandP1		= "";
		m_commandP2		= "";
		m_commandP3		= "";
		m_commandP4		= "";
		m_commandP5		= "";
		m_commandP6		= "";
		m_commandP7		= "";
		m_commandP8		= "";
		m_commandP9		= "";
		m_commandP10	= "";
		m_tooltip		= "";
	}

	/** Called when navigating away from a panel
	 *
	 */
	public void navigateAway() {
		m_label.setVisible(false);
	}

	/** Called when navigating to a panel.  Populates labels' text property with
	 * updated (expanded macros) when called.  Sets color, size, position,
	 * opaqueness and visibility.
	 *
	 */
	public void navigateTo() {
		if (m_command.isEmpty()) {
			m_label.setText("<html>" + m_macroMaster.parseMacros(m_text));
			m_label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		} else {
			m_label.setText("<html><u>" + m_macroMaster.parseMacros(m_text) + "</u>");
			m_label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

		if (m_font != null)
			m_label.setFont(m_font);

		m_label.setForeground(m_foreColor);
		if (m_opaque) {
			m_label.setBackground(m_backColor);
			m_label.setOpaque(true);
		} else {
			m_label.setOpaque(false);
		}
		m_label.setBounds(m_position.width, m_position.height, m_size.width, m_size.height);
		m_label.setVisible(m_visible);
	}

	/** Processes the associated command (if any) for the object when user presses
	 * any mouse button (right-, left- or other-button-click)
	 *
	 * @param e System mouse event
	 */
	@Override
    public void mousePressed(MouseEvent e) {
		if (!m_command.isEmpty()) {
			// There's a command here, and it's being clicked
			m_opbm.processCommand(m_command,
								  m_commandP1,
								  m_commandP2,
								  m_commandP3,
								  m_commandP4,
								  m_commandP5,
								  m_commandP6,
								  m_commandP7,
								  m_commandP8,
								  m_commandP9,
								  m_commandP10);
		}
    }

	/** Not used but included because required for listener declaration.
	 *
	 * @param e System mouse event
	 */
	@Override
    public void mouseReleased(MouseEvent e) {
        //e.getClickCount()
    }

	/** Dynamically updates tooltip text for object being moused over.
	 *
	 * @param e System mouse event
	 */
	@Override
    public void mouseEntered(MouseEvent e) {
		String tooltip = m_macroMaster.parseMacros(m_tooltip);
		if (!m_statusBar.getText().equals(tooltip)) {
			m_statusBar.setText(tooltip);
			m_statusBar.repaint();
		}
    }

	/** Not used but included because required for listener declaration.
	 *
	 * @param e System mouse event
	 */
	@Override
    public void mouseExited(MouseEvent e) {
    }

	/** Not used but included because required for listener declaration.
	 *
	 * @param e System mouse event
	 */
	@Override
    public void mouseClicked(MouseEvent e) {
    }

	/** Setter sets text to display on <code>JLabel</code>
	 *
	 * @param text Any text to display
	 */
	public void setText(String text)
	{
		m_text = text;
	}

	/** Setter sets foreground color of <code>JLabel</code>
	 *
	 * @param color from Color() class
	 */
	public void setForeColor(Color color)
	{
		m_foreColor = color;
	}

	/** Setter sets background color of <code>JLabel</code>.  Only used if opaque set to true.
	 *
	 * @param color from Color() class
	 */
	public void setBackColor(Color color)
	{
		m_backColor = color;
	}

	/** Setter sets visible property of <code>JLabel</code>.
	 *
	 * @param visible true or false is visible
	 */
	public void setVisible(boolean visible)
	{
		m_visible = visible;
	}

	/** Setter sets opaque of <code>JLabel</code>.
	 *
	 * @param opaque true or false is opaque
	 */
	public void setOpaque(boolean opaque)
	{
		m_opaque = opaque;
	}

	/** Setter sets the associated command for this <code>JLabel</code> when user clicks their mouse.
	 * Populated from PanelFactory, or dynamically as system parameters change.
	 *
	 * @param command command to execute
	 */
	public void setCommand(String command)
	{
		m_command = command;
	}

	/** Setter sets the first parameter of the associated command.
	 *
	 * @param commandP1 command's first parameter
	 */
	public void setCommandP1(String commandP1)
	{
		m_commandP1 = commandP1;
	}

	/** Setter sets the second parameter of the associated command.
	 *
	 * @param commandP2 command's second parameter
	 */
	public void setCommandP2(String commandP2)
	{
		m_commandP2 = commandP2;
	}

	/** Setter sets the third parameter of the associated command.
	 *
	 * @param commandP3 command's third parameter
	 */
	public void setCommandP3(String commandP3)
	{
		m_commandP3 = commandP3;
	}

	/** Setter sets the fourth parameter of the associated command.
	 *
	 * @param commandP4 command's fourth parameter
	 */
	public void setCommandP4(String commandP4)
	{
		m_commandP4 = commandP4;
	}

	/** Setter sets the fifth parameter of the associated command.
	 *
	 * @param commandP5 command's fifth parameter
	 */
	public void setCommandP5(String commandP5)
	{
		m_commandP5 = commandP5;
	}

	/** Setter sets the sixth parameter of the associated command.
	 *
	 * @param commandP6 command's sixth parameter
	 */
	public void setCommandP6(String commandP6)
	{
		m_commandP6 = commandP6;
	}

	/** Setter sets the seventh parameter of the associated command.
	 *
	 * @param commandP7 command's seventh parameter
	 */
	public void setCommandP7(String commandP7)
	{
		m_commandP7 = commandP7;
	}

	/** Setter sets the eighth parameter of the associated command.
	 *
	 * @param commandP8 command's eighth parameter
	 */
	public void setCommandP8(String commandP8)
	{
		m_commandP8 = commandP8;
	}

	/** Setter sets the ninth parameter of the associated command.
	 *
	 * @param commandP9 command's ninth parameter
	 */
	public void setCommandP9(String commandP9)
	{
		m_commandP9 = commandP9;
	}

	/** Setter sets the tenth parameter of the associated command.
	 *
	 * @param commandP10 command's tenth parameter
	 */
	public void setCommandP10(String commandP10)
	{
		m_commandP10 = commandP10;
	}

	/** Setter sets the tooltip text displayed when user is over this <code>JLabel</code>
	 *
	 * @param tooltip text to display
	 */
	public void setTooltip(String tooltip)
	{
		m_tooltip = tooltip;
	}

	/** Setter sets the font to display
	 *
	 * @param font Font() class
	 */
	public void setFont(Font font)
	{
		m_font = font;
	}

	/** Setter positions the <code>JLabel</code> component within the parent <code>JPanel</code>.
	 * Stored as a <code>Dimension</code>.
	 *
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	public void setPosition(int x, int y)
	{
		m_position = new Dimension(x, y);
	}

	/** Setter sets the width and height of the <code>JLabel</code>.  Stored as a <code>Dimension</code>.
	 *
	 * @param width Width of the <code>JLabel</code>
	 * @param height Height of the <code>JLabel</code>
	 */
	public void setSize(int width, int height)
	{
		m_size = new Dimension(width, height);
	}

	/** Getter returns the text originally defined (not what visibly appears on
	 * the label, as it will have macros expanded and possibly be marked up with
	 * HTML codes.
	 *
	 * @return original un-expanded form of the string
	 */
	public String getText()
	{
		return(m_text);
	}

	/** Getter returns the foreground color.
	 *
	 * @return Color() class object
	 */
	public Color getForeColor()
	{
		return(m_foreColor);
	}

	/** Getter returns the background color.
	 *
	 * @return Color() class object.
	 */
	public Color getBackColor()
	{
		return(m_backColor);
	}

	/** Getter returns the opaque setting.
	 *
	 * @return true or false is opaque
	 */
	public boolean getOpaque()
	{
		return(m_opaque);
	}

	/** Getter returns the visible setting.
	 *
	 * @return true or false is visible
	 */
	public boolean getVisible()
	{
		return(m_visible);
	}

	/** Getter returns the original command associated with this PanelLeftItem.
	 *
	 * @return command
	 */
	public String getCommand()
	{
		return(m_command);
	}

	/** Getter returns the first parameter associated with the command.
	 *
	 * @return first parameter
	 */
	public String getCommandP1()
	{
		return(m_commandP1);
	}

	/** Getter returns the second parameter associated with the command.
	 *
	 * @return second parameter
	 */
	public String getCommandP2()
	{
		return(m_commandP2);
	}

	/** Getter returns the third parameter associated with the command.
	 *
	 * @return third parameter
	 */
	public String getCommandP3()
	{
		return(m_commandP3);
	}

	/** Getter returns the fourth parameter associated with the command.
	 *
	 * @return fourth parameter
	 */
	public String getCommandP4()
	{
		return(m_commandP4);
	}

	/** Getter returns the fifth parameter associated with the command.
	 *
	 * @return fifth parameter
	 */
	public String getCommandP5()
	{
		return(m_commandP5);
	}

	/** Getter returns the sixth parameter associated with the command.
	 *
	 * @return sixth parameter
	 */
	public String getCommandP6()
	{
		return(m_commandP6);
	}

	/** Getter returns the seventh parameter associated with the command.
	 *
	 * @return seventh parameter
	 */
	public String getCommandP7()
	{
		return(m_commandP7);
	}

	/** Getter returns the eighth parameter associated with the command.
	 *
	 * @return eighth parameter
	 */
	public String getCommandP8()
	{
		return(m_commandP8);
	}

	/** Getter returns the ninth parameter associated with the command.
	 *
	 * @return ninth parameter
	 */
	public String getCommandP9()
	{
		return(m_commandP9);
	}

	/** Getter returns the tenth parameter associated with the command.
	 *
	 * @return tenth parameter
	 */
	public String getCommandP10()
	{
		return(m_commandP10);
	}

	/** Getter returns the un-expanded tooltip text.
	 *
	 * @return tooltip text
	 */
	public String getTooltip()
	{
		return(m_tooltip);
	}

	/** Getter returns the font used for the <code>JLabel</code>
	 *
	 * @return Font() class object
	 */
	public Font getFont()
	{
		return(m_font);
	}

	/** Getter returns the current position of the object
	 *
	 * @return as Dimension() class object
	 */
	public Dimension getPosition()
	{
		return(m_position);
	}

	/** Getter returns the current size of the object
	 *
	 * @return as Dimension() class object
	 */
	public Dimension getSize()
	{
		return(m_size);
	}

	private Opbm		m_opbm;
	private Macros		m_macroMaster;
	private String		m_text;
	private Color		m_foreColor;
	private Color		m_backColor;
	private boolean		m_visible;
	private boolean		m_opaque;
	private Font		m_font;
	private Dimension	m_position;
	private Dimension	m_size;

	private JLabel		m_label;
	private Label		m_statusBar;
	private JPanel		m_parent;

	private String		m_command;
	private String		m_commandP1;			// Parameter #1
	private String		m_commandP2;			// Parameter #2
	private String		m_commandP3;			// Parameter #3
	private String		m_commandP4;			// Parameter #4
	private String		m_commandP5;			// Parameter #5
	private String		m_commandP6;			// Parameter #6
	private String		m_commandP7;			// Parameter #7
	private String		m_commandP8;			// Parameter #8
	private String		m_commandP9;			// Parameter #9
	private String		m_commandP10;			// Parameter #10
	private String		m_tooltip;
}
