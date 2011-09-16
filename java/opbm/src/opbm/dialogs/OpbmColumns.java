/*
 * OPBM - Office Productivity Benchmark
 *
 * This class displays the specified data in the following format using a
 * standard column layout:
 *
 * Column layout format (see Utils.createSimpleTwoColumnDisplay()):
 *		<display width="640" height="480">
 *			<column header="Conflict" element="conflict" width="50%"/>
 *			<column header="Resolution" element="resolution" width="50%"/>
 *			<!-- repeats as necessary to define the layout for each data.item below
 *		</display>
 *
 * Input data format:
 *		<data>
 *			<item>
 *				<conflict fgcolor="whatever" bgcolor="whatever">text</conflict>
 *				<resolution fgcolor="whatever" bgcolor="whatever">text</resolution>
 *				<otherField1 fgcolor="whatever" bgcolor="whatever">text</otherField1>
 *				<otherFieldN fgcolor="whatever" bgcolor="whatever">text</otherFieldN>
 *			</item>
 *			<!-- repeats as necessary -->
 *		</data>
 *
 * Last Updated:  Sep 16, 2011
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

package opbm.dialogs;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JScrollBar;
import opbm.Opbm;
import opbm.common.Utils;
import opbm.common.Xml;
import opbm.graphics.AlphaImage;

public final class OpbmColumns
					extends		TimerTask
					implements  MouseListener,
							    WindowListener,
								AdjustmentListener,
								KeyListener
{
	public OpbmColumns(Opbm			opbm,
					   Xml			data,
					   Xml			definition)
	{
		m_opbm					= opbm;
		m_data					= data;
		m_definition			= definition;

		m_columns				= new ArrayList<Xml>(0);
		m_columnsDefinedYet		= false;

		m_items					= new ArrayList<Xml>(0);
		m_itemsDefinedYet		= false;

		m_rows					= new ArrayList<List<JLabel>>(0);
		m_topRow				= 0;

		initialize();
		render();
	}

	public void initialize()
	{
		int i, j, left, top, columnWidth, columnHeight;
		String element;
		Dimension prefSize;
		JLabel label;
		Insets inset;
		Font fontHeader, fontColumn;
		List<JLabel> columns;

		m_width		= getWidth() + 20/*for scrollbar*/;
		m_height	= getHeight();
		m_frame		= new DroppableFrame(m_opbm, false, false);
		m_frame.setTitle(getCaption());

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
		c.setBackground(Color.WHITE);
		c.setForeground(Color.BLACK);

		m_pan = new JLayeredPane();
		m_pan.setLayout(null);
		m_pan.setBounds(0, 0, m_width, m_height);
		m_pan.setBackground(Color.WHITE);
		m_pan.setOpaque(true);
		m_pan.setVisible(true);
		m_pan.setBorder(BorderFactory.createEmptyBorder());
		c.add(m_pan);

		// Set the header image
		AlphaImage img = new AlphaImage(Opbm.locateFile("header640.png"));
		m_lblHeader = new JLabel();
		m_lblHeader.setBounds(0, 0, m_width, img.getHeight());
		m_lblHeader.setHorizontalAlignment(JLabel.LEFT);
		m_lblHeader.setVerticalAlignment(JLabel.TOP);
		m_lblHeader.setBackground(Color.BLACK);
		m_lblHeader.setOpaque(true);
		m_lblHeader.setVisible(true);
		m_lblHeader.setIcon(new ImageIcon(img.getBufferedImage()));
		m_pan.add(m_lblHeader);
		m_pan.moveToFront(m_lblHeader);

		// Create the header font and column font
		fontHeader	= new Font("Calibri", Font.BOLD, 18);
		fontColumn	= new Font("Calibri", Font.PLAIN, 12);

		// Create the headers
		top		= m_lblHeader.getHeight() + _TOP_MARGIN;
		left	= 0;
		for (j = 0; j < columnCount(); j++)
		{
			columnWidth	= getColumnWidth(j);
			element		= getColumnElement(j);

			// Build the label for it
			label = new JLabel(getColumnHeader(j));
			label.setBounds(left, top, columnWidth, _HEADER_HEIGHT);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setVerticalAlignment(JLabel.CENTER);
			label.setForeground(getColumnBackcolor(j));
			label.setBackground(getColumnForecolor(j));
			label.setBorder(BorderFactory.createLineBorder(getColumnBackcolor(j)));
			label.setFont(fontHeader);
			label.setOpaque(true);
			label.setVisible(true);
			m_pan.add(label);
			m_pan.moveToFront(label);

			// Update our builder variables
			left += columnWidth - 1;
		}

		// Create the column entries for every data item
		for (i = 0; i < dataItemCount(); i++)
		{	// Build this entry
			columns	= new ArrayList<JLabel>(0);
			left	= 0;
			top		= m_lblHeader.getHeight() + _TOP_MARGIN + _HEADER_HEIGHT;
			for (j = 0; j < columnCount(); j++)
			{	// Build entries for each column
				columnWidth	= getColumnWidth(j);
				element		= getColumnElement(j);

				// Build the label for it
				label = new JLabel(" " + getDataItemText(i, element));
				label.setBounds(left, top, columnWidth, _ELEMENT_HEIGHT);
				label.setHorizontalAlignment(JLabel.LEFT);
				label.setVerticalAlignment(JLabel.CENTER);
				label.setForeground(getDataItemForecolor(i, element));
				label.setBackground(getDataItemBackcolor(i, element));
				label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				label.setFont(fontColumn);
				label.setOpaque(true);
				label.setVisible(false);
				m_pan.add(label);
				m_pan.moveToFront(label);

				// Update our builder variables
				columns.add(label);
				left	+= columnWidth - 1;
				top		+= _ELEMENT_HEIGHT - 1;
			}

			// Update our list of rows for later rendering
			m_rows.add(columns);
		}

		// Add a scrollbar on top
		m_scrollbar = new JScrollBar();
		m_scrollbar.setBounds(m_width - 20, m_lblHeader.getHeight(), 20, m_height - m_lblHeader.getHeight());
		m_scrollbar.setBlockIncrement(10);
		m_scrollbar.setUnitIncrement(1);
		m_scrollbar.setMinimum(0);
		m_scrollbar.setMaximum(Math.max(m_scrollbar.getVisibleAmount() + m_rows.size() - (m_height - (m_lblHeader.getHeight() + _TOP_MARGIN)) / _HEADER_HEIGHT, m_scrollbar.getVisibleAmount()));
		m_scrollbar.addAdjustmentListener(this);
		m_scrollbar.addKeyListener(this);
		m_pan.add(m_scrollbar);
		m_pan.moveToFront(m_scrollbar);

		m_frame.setVisible(true);
		m_frame.setAlwaysOnTop(true);
		m_frame.forceWindowToHaveFocus();
	}

	/**
	 * Called to render the entire listing, called each time the
	 */
	public void render()
	{
		int i, j, top;
		List<JLabel> columns;
		JLabel label;

		top = m_lblHeader.getHeight() + _TOP_MARGIN + _HEADER_HEIGHT;
		for (i = 0; i < m_rows.size(); i++)
		{	// Iterate through every row
			columns = m_rows.get(i);
			for (j = 0; j < columns.size(); j++)
			{	// Iterate through every column on that row
				label = columns.get(j);
				if (i >= m_topRow && top <= m_height)
				{	// There's room for this item
					if (label.getY() != top)
						label.setBounds(label.getX(), top, label.getWidth(), label.getHeight());

					if (!label.isVisible())
						label.setVisible(true);

				} else {
					// No room, make everything not visible
					if (label.isVisible())
						label.setVisible(false);
				}
			}
			if (i >= m_topRow)
				top += _ELEMENT_HEIGHT - 1;
		}
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

	/*
	 * The format of the definition that must be laid out for use of this class.
	 *
	 *		<display width="640" height="480" caption="whatever">
	 *			<column header="Conflict" element="conflict" width="50%"/>
	 *			<column header="Resolution" element="resolution" width="50%"/>
	 *			<!-- repeats as necessary to define the layout for each data.item below
	 *		</display>
	 */

	/**
	 * Returns the width specified in the definition
	 * @return
	 */
 	public int getWidth()
	{
		String width;

		width = m_definition.getAttribute("width");
		if (width.isEmpty())
		{	// No width, use the default
			return(640);
		} else {
			return(Integer.valueOf(width));
		}
	}

	/**
	 * Returns the height specified in the definition
	 * @return
	 */
	public int getHeight()
	{
		String height;

		height = m_definition.getAttribute("height");
		if (height.isEmpty())
		{	// No width, use the default
			return(480);
		} else {
			return(Integer.valueOf(height));
		}
	}

	/**
	 * Returns the caption specified in the definition
	 * @return
	 */
	public String getCaption()
	{
		String caption;

		caption = m_definition.getAttribute("caption");
		if (caption.isEmpty())
		{	// No caption, use the default
			return("OPBM Data Display");
		} else {
			return(caption);
		}
	}

	/**
	 * Returns the count for the number of columns specified in the definition
	 * @return
	 */
	public int columnCount()
	{
		Xml column;

		if (!m_columnsDefinedYet)
		{	// We haven't yet computed the value, so parse the definition to
			// physically count the column entries
			column	= m_definition.getFirstChild();
			while (column != null)
			{	// Iterate through every candidate, and count only those entries which are "column"
				if (column.getName().equalsIgnoreCase("column"))
				{	// We found one, but in order to be valid it must have
					// two attributes:  header, element
					// optional:  width, fgcolor, bgcolor
					if (column.getAttributeNode("header") != null && column.getAttributeNode("element") != null)
					{	// We're good
						m_columns.add(column);
					}
				}
				// Move to next sibling and test it
				column = column.getNext();
			}
			// When we get here, we have our count
			m_columnsDefinedYet	= true;
		}
		// Return the now-computed column size
		return(m_columns.size());
	}

	/**
	 * Return the column header name specified in the definition
	 * @param index column entry to return data for
	 * @return header name, or "Column N" if no header name is specified
	 */
	public String getColumnHeader(int index)
	{
		String header;

		if (index < m_columns.size())
		{	// Return the header from this entry
			header = m_columns.get(index).getAttribute("header");
			if (!header.isEmpty())
				return(header);
		}
		// There is no header defined, so just use column numbers
		return("Column " + Integer.toString(index));
	}

	/**
	 * Return the column element name specified in the definition
	 * @param index column entry to return data for
	 * @return element name, or null if nothing is specified in the definition
	 */
	public String getColumnElement(int index)
	{
		String element;

		if (index < m_columns.size())
		{	// Return the element from this entry
			element = m_columns.get(index).getAttribute("element");
			if (!element.isEmpty())
				return(element);
		}
		// There is no element defined, so indicate null
		return(null);
	}

	/**
	 * Return the column element name specified in the definition
	 * @param index column entry to return data for
	 * @return element name, or null if nothing is specified in the definition
	 */
	public int getColumnWidth(int index)
	{
		double value;
		String width;

		if (index < m_columns.size())
		{	// Return the width from this entry
			width = m_columns.get(index).getAttribute("width");
			if (!width.isEmpty())
			{
				if (width.contains("%"))
				{	// It's a percentage of the overall width
					width = width.replace("%", "");
					value = Double.valueOf(width) * (double)getWidth() / 100.0;
					return((int)value);

				}
				// Literal value
				value = (double)Integer.valueOf(width);
				if (value > 0)
					return((int)value);
			}
		}
		// There is no width defined, so use the default 15 pixels
		return(15);
	}

	/**
	 * Return the column foreground color specified in the definition
	 * @param index column entry to return data for
	 * @return element name, or null if nothing is specified in the definition
	 */
	public Color getColumnForecolor(int index)
	{
		String color;

		if (index < m_columns.size())
		{	// Return the color from this entry
			color = m_columns.get(index).getAttribute("fgcolor");
			if (!color.isEmpty())
			{	// It's not empty, extract it or return the default color
				// value if it's not valid
				return(Utils.extractColorFromRRGGBBformat(color, Color.BLACK));
			}
		}
		// There is no color defined, so use the default black foreground
		return(Color.BLACK);
	}

	/**
	 * Return the column foreground color specified in the definition
	 * @param index column entry to return data for
	 * @return element name, or null if nothing is specified in the definition
	 */
	public Color getColumnBackcolor(int index)
	{
		String color;

		if (index < m_columns.size())
		{	// Return the color from this entry
			color = m_columns.get(index).getAttribute("bgcolor");
			if (!color.isEmpty())
			{	// It's not empty, extract it or return the default color
				// value if it's not valid
				return(Utils.extractColorFromRRGGBBformat(color, Color.WHITE));
			}
		}
		// There is no color defined, so use the default white background
		return(Color.WHITE);
	}

	/**
	 * Obtains the number of data.item elements specified in the source data
	 * @return
	 */
	public int dataItemCount()
	{
		Xml item;

		if (!m_itemsDefinedYet && m_data.getName().equalsIgnoreCase("data"))
		{	// We haven't yet computed the value, so parse the definition to
			// physically count the column entries
			item = m_data.getFirstChild();
			while (item != null)
			{	// Iterate through every candidate, and count only those entries which are "column"
				if (item.getName().equalsIgnoreCase("item"))
				{	// We found one
					m_items.add(item);
				}
				// Move to next sibling and test it
				item = item.getNext();
			}
			// When we get here, we have our count
			m_itemsDefinedYet	= true;
		}
		// Return the now-computed column size
		return(m_items.size());
	}

	/**
	 * Returns the specified data item's element's text content (if it exists)
	 * @param index item number
	 * @param element data element to convey
	 * @return text from that element (if found), empty string otherwise
	 */
	public String getDataItemText(int		index,
								  String	element)
	{
		Xml itemData;

		if (index < m_items.size())
		{	// We're good, see if the element exists
			itemData = m_items.get(index).getChildNode(element);
			if (itemData != null)
			{	// We have the field
				return(itemData.getText());
			}
		}
		// Return an empty string
		return("");
	}

	/**
	 * Returns the specified data item's element's fgcolor content (if it exists)
	 * @param index item number
	 * @param element data element to convey
	 * @return fgcolor from that element (if found), empty string otherwise
	 */
	public Color getDataItemForecolor(int		index,
									  String	element)
	{
		String color;
		Xml itemData;

		if (index < m_items.size())
		{	// We're good, see if the element exists
			itemData = m_items.get(index).getChildNode(element);
			if (itemData != null)
			{	// We have the field
				color = itemData.getAttribute("fgcolor");
				if (!color.isEmpty())
					return(Utils.extractColorFromRRGGBBformat(color, getColumnForecolor(index)));
			}
		}
		// Return a default color
		return(getColumnForecolor(index));
	}

	/**
	 * Returns the specified data item's element's bgcolor content (if it exists)
	 * @param index item number
	 * @param element data element to convey
	 * @return bgcolor from that element (if found), empty string otherwise
	 */
	public Color getDataItemBackcolor(int		index,
									  String	element)
	{
		String color;
		Xml itemData;

		if (index < m_items.size())
		{	// We're good, see if the element exists
			itemData = m_items.get(index).getChildNode(element);
			if (itemData != null)
			{	// We have the field
				color = itemData.getAttribute("bgcolor");
				if (!color.isEmpty())
					return(Utils.extractColorFromRRGGBBformat(color, getColumnBackcolor(index)));
			}
		}
		// Return a default color
		return(getColumnBackcolor(index));
	}

	public boolean moveUp(int count)
	{
		if (m_topRow - count >= 0)
		{	// We can move up one
			m_topRow -= count;
			return(true);

		} else if (m_topRow > 0) {
			// We can move back to the beginning
			m_topRow = 0;
			return(true);

		} else {
			// Nope, we're at the top
			return(false);
		}
	}

	public boolean moveDown(int count)
	{
		if (m_topRow < m_rows.size() - count)
		{	// We can move down the specified number
			m_topRow += count;
			return(true);

		} else if (m_topRow < m_rows.size() - 1) {
			// We can move down at least a few, until we reach the end
			m_topRow = m_rows.size() - 1;
			return(true);

		} else {
			// Nope, we're at the top
			return(false);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
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
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
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

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		if (e.getValue() != m_topRow)
		{	// Something has changed
			m_topRow = e.getValue();
			render();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			moveUp(1);
			render();

		} else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
			moveUp(((m_height - m_lblHeader.getY()) / 18) - 2);
			render();

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			moveDown(1);
			render();

		} else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			moveDown(((m_height - m_lblHeader.getY()) / 18) - 2);
			render();

		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			dispose();

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	private Opbm				m_opbm;
	private Xml					m_data;
	private Xml					m_definition;
	private int					m_width;
	private int					m_height;
	private int					m_actual_width;
	private int					m_actual_height;
	private JLabel				m_lblHeader;
	private JScrollBar			m_scrollbar;

	private List<Xml>			m_columns;						// Holds an index of pointers into m_definition
	private boolean				m_columnsDefinedYet;			// When first loaded, m_columns is populated

	private List<Xml>			m_items;						// Holds an index of pointers into m_data
	private boolean				m_itemsDefinedYet;				// When first loaded, m_items is populated

	private DroppableFrame		m_frame;
	private JLayeredPane		m_pan;
	private JLabel				m_lblMessage;
	private JLabel				m_lblCountdown;

	private List<List<JLabel>>	m_rows;							// Holds the rows of label items to display
	private int					m_topRow;						// Holds the index for the top row

	// Timer related
	private Timer				m_timer;
	private int					m_count;
	private int					m_countMax;

	private final int			_HEADER_HEIGHT		= 24;
	private final int			_ELEMENT_HEIGHT		= 20;
	private final int			_TOP_MARGIN			= 0;
}
