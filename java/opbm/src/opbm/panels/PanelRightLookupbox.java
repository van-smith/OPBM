/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is used to create a lookupbox.  A lookupbox visually appears to be
 * a listbox, but it contains different internal abilities.  It is designed
 * primiarly to not be a key for field edits, but rather to be something that
 * is itself updated by the listbox that is
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

package opbm.panels;

import opbm.common.DroppableFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
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
import opbm.common.Utils;
import opbm.common.Xml;

public class PanelRightLookupbox implements ListSelectionListener, MouseListener
{
	public PanelRightLookupbox(Opbm				opbm,
							   JPanel			parentPanel,
							   PanelRight		parentPR,
							   PanelRightItem	parentPRI,
							   Commands			commandMaster,
							   Macros			macroMaster)
	{
		m_xmlLookupboxMaster	= new ArrayList<Xml>(0);
		m_visible				= false;
		m_opaque				= true;
		m_foreground			= Color.BLACK;
		m_background			= Color.WHITE;
		m_font					= null;
		m_lastIndex				= -1;
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
		m_lastMillisecond		= 0;
		m_lookupBox				= null;
		m_lookupboxScroller		= null;
		m_lookupboxXml			= null;
		m_lookupboxName			= "";
		m_lookupboxButtons		= "";
		m_listByP1				= "";
		m_listByP2				= "";
		m_listByP3				= "";
		m_listByP4				= "";
		m_listByP5				= "";
		m_listByP6				= "";
		m_listByP7				= "";
		m_listByP8				= "";
		m_listByP9				= "";
		m_listByP10				= "";

		m_dblClickCommand	= "";
		m_dblClickP1		= "";
		m_dblClickP2		= "";
		m_dblClickP3		= "";
		m_dblClickP4		= "";
		m_dblClickP5		= "";
		m_dblClickP6		= "";
		m_dblClickP7		= "";
		m_dblClickP8		= "";
		m_dblClickP9		= "";
		m_dblClickP10		= "";

		m_lookupboxAddCommand = "";
		m_lookupboxAddCommandP1 = "";
		m_lookupboxAddCommandP2 = "";
		m_lookupboxAddCommandP3 = "";
		m_lookupboxAddCommandP4 = "";
		m_lookupboxAddCommandP5 = "";
		m_lookupboxAddCommandP6 = "";
		m_lookupboxAddCommandP7 = "";
		m_lookupboxAddCommandP8 = "";
		m_lookupboxAddCommandP9 = "";
		m_lookupboxAddCommandP10 = "";

		m_lookupboxSubtractCommand = "";
		m_lookupboxSubtractCommandP1 = "";
		m_lookupboxSubtractCommandP2 = "";
		m_lookupboxSubtractCommandP3 = "";
		m_lookupboxSubtractCommandP4 = "";
		m_lookupboxSubtractCommandP5 = "";
		m_lookupboxSubtractCommandP6 = "";
		m_lookupboxSubtractCommandP7 = "";
		m_lookupboxSubtractCommandP8 = "";
		m_lookupboxSubtractCommandP9 = "";
		m_lookupboxSubtractCommandP10 = "";

		m_lookupboxZoomCommand = "";
		m_lookupboxZoomCommandP1 = "";
		m_lookupboxZoomCommandP2 = "";
		m_lookupboxZoomCommandP3 = "";
		m_lookupboxZoomCommandP4 = "";
		m_lookupboxZoomCommandP5 = "";
		m_lookupboxZoomCommandP6 = "";
		m_lookupboxZoomCommandP7 = "";
		m_lookupboxZoomCommandP8 = "";
		m_lookupboxZoomCommandP9 = "";
		m_lookupboxZoomCommandP10 = "";

		m_lookupboxUpCommand = "";
		m_lookupboxUpCommandP1 = "";
		m_lookupboxUpCommandP2 = "";
		m_lookupboxUpCommandP3 = "";
		m_lookupboxUpCommandP4 = "";
		m_lookupboxUpCommandP5 = "";
		m_lookupboxUpCommandP6 = "";
		m_lookupboxUpCommandP7 = "";
		m_lookupboxUpCommandP8 = "";
		m_lookupboxUpCommandP9 = "";
		m_lookupboxUpCommandP10 = "";

		m_lookupboxDownCommand = "";
		m_lookupboxDownCommandP1 = "";
		m_lookupboxDownCommandP2 = "";
		m_lookupboxDownCommandP3 = "";
		m_lookupboxDownCommandP4 = "";
		m_lookupboxDownCommandP5 = "";
		m_lookupboxDownCommandP6 = "";
		m_lookupboxDownCommandP7 = "";
		m_lookupboxDownCommandP8 = "";
		m_lookupboxDownCommandP9 = "";
		m_lookupboxDownCommandP10 = "";

		m_onSelectCommand		= "";
		m_onSelectP1			= "";
		m_onSelectP2			= "";
		m_onSelectP3			= "";
		m_onSelectP4			= "";
		m_onSelectP5			= "";
		m_onSelectP6			= "";
		m_onSelectP7			= "";
		m_onSelectP8			= "";
		m_onSelectP9			= "";
		m_onSelectP10			= "";
	}

	/**
	 * Updates the LookupBox (typically during instantiation) to set the add,
	 * subtract and zoom buttons. Can be used dynamically to update the buttons
	 * as real-world conditions change.
	 */
	public void updateLookupBox()
	{
		boolean add, subtract, zoom, up, down;
		int buttonWidth = 0;
		int count = 0;
		int width, left, top;
		Insets inset;
		boolean destroyAdd		= true;
		boolean destroySubtract	= true;
		boolean destroyZoom		= true;
		boolean destroyUp		= true;
		boolean destroyDown		= true;
		Dimension d;
		Font f;

		if (m_opbm.isFontOverride())
			f	= new Font("Arial", Font.BOLD, 12);
		else
			f	= new Font("Calibri", Font.BOLD, 14);

		add			= m_lookupboxButtons.contains("+");
		subtract	= m_lookupboxButtons.contains("-");
		zoom		= m_lookupboxButtons.toLowerCase().contains("z");
		up			= m_lookupboxButtons.toLowerCase().contains("u");
		down		= m_lookupboxButtons.toLowerCase().contains("d");
		if (add || subtract || zoom || up || down)
		{
			// We must add new buttons and readjust
			if (add) {
				// The add button exists
				destroyAdd = false;
				if (m_lookupboxAdd == null) {
					m_lookupboxAdd = new JButton("+");
					m_lookupboxAdd.setFont(f);
					m_lookupboxAdd.addMouseListener(this);
					inset = m_lookupboxAdd.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_lookupboxAdd.setMargin(inset);
					m_parentPanel.add(m_lookupboxAdd);
				}
				buttonWidth += 35;
				++count;
			}

			if (subtract) {
				// The delte button exists
				destroySubtract = false;
				if (m_lookupboxSubtract == null) {
					m_lookupboxSubtract = new JButton("-");
					m_lookupboxSubtract.setFont(f);
					m_lookupboxSubtract.addMouseListener(this);
					inset = m_lookupboxSubtract.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_lookupboxSubtract.setMargin(inset);
					m_parentPanel.add(m_lookupboxSubtract);
				}
				buttonWidth += 35;
				++count;
			}

			if (zoom) {
				// The zoom button exists
				destroyZoom = false;
				if (m_lookupboxZoom == null) {
					m_lookupboxZoom = new JButton("Zoom");
					m_lookupboxZoom.setFont(f);
					m_lookupboxZoom.addMouseListener(this);
					inset = m_lookupboxZoom.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_lookupboxZoom.setMargin(inset);
					m_parentPanel.add(m_lookupboxZoom);
				}
				buttonWidth += 60;
				++count;
			}

			if (up) {
				// The up button exists
				destroyUp = false;
				if (m_lookupboxUp == null) {
					m_lookupboxUp = new JButton("Up");
					m_lookupboxUp.setFont(f);
					m_lookupboxUp.addMouseListener(this);
					inset = m_lookupboxUp.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_lookupboxUp.setMargin(inset);
					m_parentPanel.add(m_lookupboxUp);
				}
				buttonWidth += 45;
				++count;
			}

			if (down) {
				// The down button exists
				destroyDown = false;
				if (m_lookupboxDown == null) {
					m_lookupboxDown = new JButton("Dn");
					m_lookupboxDown.setFont(f);
					m_lookupboxDown.addMouseListener(this);
					inset = m_lookupboxDown.getInsets();
					inset.left = 3;
					inset.right = 3;
					m_lookupboxDown.setMargin(inset);
					m_parentPanel.add(m_lookupboxDown);
				}
				buttonWidth += 45;
				++count;
			}

			// Determine the relative center location of the defined buttons
			width		= m_width;
			buttonWidth += ((count - 1) * 5);
			left		= m_x + ((width - buttonWidth) / 2);
			top			= m_y + m_height - 25;
			if (add) {
				d = new Dimension(35,25);
				m_lookupboxAdd.setLocation(left, top);
				m_lookupboxAdd.setMinimumSize(d);
				m_lookupboxAdd.setMaximumSize(d);
				m_lookupboxAdd.setPreferredSize(d);
				m_lookupboxAdd.setSize(d);
				left += d.getWidth() + 5;
			}

			if (subtract) {
				d = new Dimension(35,25);
				m_lookupboxSubtract.setLocation(left, top);
				m_lookupboxSubtract.setMinimumSize(d);
				m_lookupboxSubtract.setMaximumSize(d);
				m_lookupboxSubtract.setPreferredSize(d);
				m_lookupboxSubtract.setSize(d);
				left += d.getWidth() + 5;
			}

			if (zoom) {
				d = new Dimension(60,25);
				m_lookupboxZoom.setLocation(left, top);
				m_lookupboxZoom.setMinimumSize(d);
				m_lookupboxZoom.setMaximumSize(d);
				m_lookupboxZoom.setPreferredSize(d);
				m_lookupboxZoom.setSize(d);
				left += d.getWidth() + 5;
			}

			if (up) {
				d = new Dimension(45,25);
				m_lookupboxUp.setLocation(left, top);
				m_lookupboxUp.setMinimumSize(d);
				m_lookupboxUp.setMaximumSize(d);
				m_lookupboxUp.setPreferredSize(d);
				m_lookupboxUp.setSize(d);
				left += d.getWidth() + 5;
			}

			if (down) {
				d = new Dimension(45,25);
				m_lookupboxDown.setLocation(left, top);
				m_lookupboxDown.setMinimumSize(d);
				m_lookupboxDown.setMaximumSize(d);
				m_lookupboxDown.setPreferredSize(d);
				m_lookupboxDown.setSize(d);
				left += d.getWidth() + 5;
			}

			// Adjust the lookupbox size
			m_height -= 28;
		}
		// If the add button exists, remove it
		if (m_lookupboxAdd != null) {
			if (destroyAdd) {
				m_parentPanel.remove(m_lookupboxAdd);
				m_lookupboxAdd = null;
			} else {
				m_lookupboxAdd.setVisible(true);
			}
		}

		// If the subtract button exists, remove it
		if (m_lookupboxSubtract != null) {
			if (destroySubtract) {
				m_parentPanel.remove(m_lookupboxSubtract);
				m_lookupboxSubtract = null;
			} else {
				m_lookupboxSubtract.setVisible(true);
			}
		}

		// If the zoom button exists, remove it
		if (m_lookupboxZoom != null) {
			if (destroyZoom) {
				m_parentPanel.remove(m_lookupboxZoom);
				m_lookupboxZoom = null;
			} else {
				m_lookupboxZoom.setVisible(true);
			}
		}

		// If the up button exists, remove it
		if (m_lookupboxUp != null) {
			if (destroyUp) {
				m_parentPanel.remove(m_lookupboxUp);
				m_lookupboxUp = null;
			} else {
				m_lookupboxUp.setVisible(true);
			}
		}

		// If the down button exists, remove it
		if (m_lookupboxDown != null) {
			if (destroyDown) {
				m_parentPanel.remove(m_lookupboxDown);
				m_lookupboxDown= null;
			} else {
				m_lookupboxDown.setVisible(true);
			}
		}
	}

	/**
	 * Specifies the add button is allowed on the <code>_TYPE_LOOKUPBOX</code>
	 * control.
	 *
	 * @param command command to execute when the add button is clicked
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
	public void setAddButton(String command,
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
		// The m_lookupboxButtons string is used to identify which buttons are present, currently can be "+", "-" and "z" for "add, subtract and zoom"
		if (!command.isEmpty()) {
			m_lookupboxButtons			= m_lookupboxButtons.toLowerCase().replace("+", " ");
			m_lookupboxButtons			+= "+";
			m_lookupboxAddCommand		= command;
			m_lookupboxAddCommandP1		= p1;
			m_lookupboxAddCommandP2		= p2;
			m_lookupboxAddCommandP3		= p3;
			m_lookupboxAddCommandP4		= p4;
			m_lookupboxAddCommandP5		= p5;
			m_lookupboxAddCommandP6		= p6;
			m_lookupboxAddCommandP7		= p7;
			m_lookupboxAddCommandP8		= p8;
			m_lookupboxAddCommandP9		= p9;
			m_lookupboxAddCommandP10	= p10;
		}
	}

	/**
	 * Specifies the delete button is allowed on the <code>_TYPE_LOOKUPBOX</code>
	 * control.
	 *
	 * @param command command to execute when the delete button is clicked
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
	public void setSubtractButton(String command,
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
		// The m_lookupboxButtons string is used to identify which buttons are present, currently can be "+", "-" and "z" for "add, subtract and zoom"
		if (!command.isEmpty()) {
			m_lookupboxButtons					= m_lookupboxButtons.toLowerCase().replace("-", " ");
			m_lookupboxButtons					+= "-";
			m_lookupboxSubtractCommand			= command;
			m_lookupboxSubtractCommandP1		= p1;
			m_lookupboxSubtractCommandP2		= p2;
			m_lookupboxSubtractCommandP3		= p3;
			m_lookupboxSubtractCommandP4		= p4;
			m_lookupboxSubtractCommandP5		= p5;
			m_lookupboxSubtractCommandP6		= p6;
			m_lookupboxSubtractCommandP7		= p7;
			m_lookupboxSubtractCommandP8		= p8;
			m_lookupboxSubtractCommandP9		= p9;
			m_lookupboxSubtractCommandP10		= p10;
		}
	}

	/**
	 * Specifies the zoom button is allowed on the <code>_TYPE_LOOKUPBOX</code>
	 * control.
	 *
	 * @param command command to execute when the button is clicked
	 * @param autoPreview is there an auto-preview option?
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
	public void setZoomButton(String command,
							  boolean autoPreview,
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
		// The m_lookupboxButtons string is used to identify which buttons are present, currently can be "+", "-" and "z" for "add, subtract and zoom"
		if (!command.isEmpty())
		{
			m_lookupboxButtons				= m_lookupboxButtons.toLowerCase().replace("z", " ");
			m_lookupboxButtons				+= "z";
			m_lookupboxZoomCommand			= command;
			m_lookupboxZoomAutoPreview		= autoPreview;
			m_lookupboxZoomCommandP1		= p1;
			m_lookupboxZoomCommandP2		= p2;
			m_lookupboxZoomCommandP3		= p3;
			m_lookupboxZoomCommandP4		= p4;
			m_lookupboxZoomCommandP5		= p5;
			m_lookupboxZoomCommandP6		= p6;
			m_lookupboxZoomCommandP7		= p7;
			m_lookupboxZoomCommandP8		= p8;
			m_lookupboxZoomCommandP9		= p9;
			m_lookupboxZoomCommandP10		= p10;
		}
	}

	/**
	 * Specifies the up button is allowed on the <code>_TYPE_LOOKUPBOX</code>
	 * control.
	 *
	 * @param command command to execute when the button is clicked
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
	public void setUpButton(String command,
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
		// The m_lookupboxButtons string is used to identify which buttons are present, currently can be "+", "-" and "z" for "add, subtract and zoom"
		if (!command.isEmpty()) {
			m_lookupboxButtons			= m_lookupboxButtons.toLowerCase().replace("u", " ");
			m_lookupboxButtons			+= "u";
			m_lookupboxUpCommand		= command;
			m_lookupboxUpCommandP1		= p1;
			m_lookupboxUpCommandP2		= p2;
			m_lookupboxUpCommandP3		= p3;
			m_lookupboxUpCommandP4		= p4;
			m_lookupboxUpCommandP5		= p5;
			m_lookupboxUpCommandP6		= p6;
			m_lookupboxUpCommandP7		= p7;
			m_lookupboxUpCommandP8		= p8;
			m_lookupboxUpCommandP9		= p9;
			m_lookupboxUpCommandP10		= p10;
		}
	}

	/**
	 * Specifies the down button is allowed on the <code>_TYPE_LOOKUPBOX</code>
	 * control.
	 *
	 * @param command command to execute when the button is clicked
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
	public void setDownButton(String command,
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
		// The m_lookupboxButtons string is used to identify which buttons are present, currently can be "+", "-" and "z" for "add, subtract and zoom"
		if (!command.isEmpty()) {
			m_lookupboxButtons			= m_lookupboxButtons.toLowerCase().replace("d", " ");
			m_lookupboxButtons			+= "d";
			m_lookupboxDownCommand		= command;
			m_lookupboxDownCommandP1	= p1;
			m_lookupboxDownCommandP2	= p2;
			m_lookupboxDownCommandP3	= p3;
			m_lookupboxDownCommandP4	= p4;
			m_lookupboxDownCommandP5	= p5;
			m_lookupboxDownCommandP6	= p6;
			m_lookupboxDownCommandP7	= p7;
			m_lookupboxDownCommandP8	= p8;
			m_lookupboxDownCommandP9	= p9;
			m_lookupboxDownCommandP10	= p10;
		}
	}

	/**
	 * Specifies edit options for the <code>_TYPE_LOOKUPBOX</code> control.
	 *
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
	 * @param p11 tenth parameter
	 * @param p12 tenth parameter
	 * @param p13 tenth parameter
	 * @param p14 tenth parameter
	 * @param p15 tenth parameter
	 * @param p16 tenth parameter
	 * @param p17 tenth parameter
	 * @param p18 tenth parameter
	 * @param p19 tenth parameter
	 * @param p20 tenth parameter
	 */
	public void setRelativeEdits(String p1,
								 String p2,
								 String p3,
								 String p4,
								 String p5,
								 String p6,
								 String p7,
								 String p8,
								 String p9,
								 String p10,
								 String p11,
								 String p12,
								 String p13,
								 String p14,
								 String p15,
								 String p16,
								 String p17,
								 String p18,
								 String p19,
								 String p20)
	{
		m_lookupboxEdits1	= p1;
		m_lookupboxEdits2	= p2;
		m_lookupboxEdits3	= p3;
		m_lookupboxEdits4	= p4;
		m_lookupboxEdits5	= p5;
		m_lookupboxEdits6	= p6;
		m_lookupboxEdits7	= p7;
		m_lookupboxEdits8	= p8;
		m_lookupboxEdits9	= p9;
		m_lookupboxEdits10	= p10;
		m_lookupboxEdits11	= p11;
		m_lookupboxEdits12	= p12;
		m_lookupboxEdits13	= p13;
		m_lookupboxEdits14	= p14;
		m_lookupboxEdits15	= p15;
		m_lookupboxEdits16	= p16;
		m_lookupboxEdits17	= p17;
		m_lookupboxEdits18	= p18;
		m_lookupboxEdits19	= p19;
		m_lookupboxEdits20	= p20;
	}

	/**
	 * Specifies options options for the <code>_TYPE_LOOKUPBOX</code> control.
	 *
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
	 * @param p11 tenth parameter
	 * @param p12 tenth parameter
	 * @param p13 tenth parameter
	 * @param p14 tenth parameter
	 * @param p15 tenth parameter
	 * @param p16 tenth parameter
	 * @param p17 tenth parameter
	 * @param p18 tenth parameter
	 * @param p19 tenth parameter
	 * @param p20 tenth parameter
	 */
	public void setRelativeOptions(String p1,
								   String p2,
								   String p3,
								   String p4,
								   String p5,
								   String p6,
								   String p7,
								   String p8,
								   String p9,
								   String p10,
								   String p11,
								   String p12,
								   String p13,
								   String p14,
								   String p15,
								   String p16,
								   String p17,
								   String p18,
								   String p19,
								   String p20)
	{
		m_lookupboxOptions1		= p1;
		m_lookupboxOptions2		= p2;
		m_lookupboxOptions3		= p3;
		m_lookupboxOptions4		= p4;
		m_lookupboxOptions5		= p5;
		m_lookupboxOptions6		= p6;
		m_lookupboxOptions7		= p7;
		m_lookupboxOptions8		= p8;
		m_lookupboxOptions9		= p9;
		m_lookupboxOptions10	= p10;
		m_lookupboxOptions11	= p11;
		m_lookupboxOptions12	= p12;
		m_lookupboxOptions13	= p13;
		m_lookupboxOptions14	= p14;
		m_lookupboxOptions15	= p15;
		m_lookupboxOptions16	= p16;
		m_lookupboxOptions17	= p17;
		m_lookupboxOptions18	= p18;
		m_lookupboxOptions19	= p19;
		m_lookupboxOptions20	= p20;
	}

	/**
	 * Setter sets the associated <code>_TYPE_LOOKUPBOX</code> listing parameters
	 * Populated from PanelFactory, or dynamically as list filters change.
	 * The list is populated dynamically when called to update its list contents.
	 *
	 * @param p1 first list-by parameter
	 * @param p2 second list-by parameter
	 * @param p3 third list-by parameter
	 * @param p4 fourth list-by parameter
	 * @param p5 fifth list-by parameter
	 * @param p6 sixth list-by parameter
	 * @param p7 seventh list-by parameter
	 * @param p8 eighth list-by parameter
	 * @param p9 ninth list-by parameter
	 * @param p10 tenth list-by parameter
	 */
	public void setListBy(String p1,
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
		m_listByP1	= p1;
		m_listByP2	= p2;
		m_listByP3	= p3;
		m_listByP4	= p4;
		m_listByP5	= p5;
		m_listByP6	= p6;
		m_listByP7	= p7;
		m_listByP8	= p8;
		m_listByP9	= p9;
		m_listByP10	= p10;
	}

	/**
	 * Setter sets the associated <code>_TYPE_LOOKUPBOX</code> listing parameters
	 * Populated from PanelFactory, or dynamically as list filters change.
	 * The list is populated dynamically when called to update its list contents.
	 *
	 * @param command command to execute
	 * @param p1 first list-by parameter
	 * @param p2 second list-by parameter
	 * @param p3 third list-by parameter
	 * @param p4 fourth list-by parameter
	 * @param p5 fifth list-by parameter
	 * @param p6 sixth list-by parameter
	 * @param p7 seventh list-by parameter
	 * @param p8 eighth list-by parameter
	 * @param p9 ninth list-by parameter
	 * @param p10 tenth list-by parameter
	 */
	public void setDblClick(String command,
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
		m_dblClickCommand	= command;
		m_dblClickP1		= p1;
		m_dblClickP2		= p2;
		m_dblClickP3		= p3;
		m_dblClickP4		= p4;
		m_dblClickP5		= p5;
		m_dblClickP6		= p6;
		m_dblClickP7		= p7;
		m_dblClickP8		= p8;
		m_dblClickP9		= p9;
		m_dblClickP10		= p10;
	}

	/**
	 * Setter sets the associated <code>_TYPE_LOOKUPBOX</code> onSelect parameters
	 * Populated from PanelFactory, or dynamically as list filters change.
	 * The list is populated dynamically when called to update its list contents.
	 *
	 * @param command command to execute
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
	public void setOnSelect(String command,
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
		m_onSelectCommand	= command;
		m_onSelectP1		= p1;
		m_onSelectP2		= p2;
		m_onSelectP3		= p3;
		m_onSelectP4		= p4;
		m_onSelectP5		= p5;
		m_onSelectP6		= p6;
		m_onSelectP7		= p7;
		m_onSelectP8		= p8;
		m_onSelectP9		= p9;
		m_onSelectP10		= p10;
	}

	/**
	 * Specifies the xml filename to load for the <code>_TYPE_LOOKUPBOX</code> control.
	 *
	 * @param fileName xml file to load for listbox
	 */
	public void setFileName(String fileName)
	{
		m_lookupboxFileName = fileName;
	}

	/**
	 * Save the changes
	 */
	public void saveLookupBoxData()
	{
		m_lookupboxXml.saveNode(m_macroMaster.parseMacros("$scripts.xml$"));
	}

	/**
	 * Specifies where source data from which the <code>_TYPE_LOOKUPBOX</code>
	 * control is populated.
	 *
	 * @param location absolute location within the xml file to reach the list of data items
	 */
	public void setLocation(String location)
	{
		m_lookupboxLocation = location;
	}

	/**
	 * Specifies which entries within location will be pulled (allows a filter
	 * within a larger data set, to only pull out those named tags) for the
	 * <code>_TYPE_LOOKUPBOX</code> controls.
	 *
	 * @param forEach specifies relative tag within location for enumerated items
	 */
	public void setForEach(String forEach) {
		m_lookupboxForEach = forEach;
	}

	/**
	 * Updates the listbox's associated array after something has been added,
	 * deleted or cloned, and then repaints the control.
	 */
	public void updateLookupBoxArray()
	{
		int saveIndex;

		if (m_lookupBox != null && m_xmlLookupboxMaster != null)
		{
			saveIndex = m_lookupBox.getSelectedIndex();

			m_lookupBox.setListData(m_xmlLookupboxMaster.toArray());

			if (saveIndex != -1 && saveIndex < m_xmlLookupboxMaster.size())
				m_lookupBox.setSelectedIndex(saveIndex);
			else
				m_lookupBox.setSelectedIndex(m_xmlLookupboxMaster.size() - 1);

			m_parentPR.updateRelativeToFields();
			m_lookupBox.repaint();
		}
	}

	/**
	 * Repaints the listbox control.
	 */
	public void repaintLookupBox()
	{
		m_lookupBox.repaint();
	}

	/**
	 * Fills or refills the physical listbox, which allows it to load the file
	 * contents, create the node list, and populate and format the control.
	 */
	public void fillOrRefillLookupBoxArray()
	{
		int saveIndex;

		if (m_lookupBox != null)
			saveIndex = m_lookupBox.getSelectedIndex();
		else
			saveIndex = -1;

		// Grab the list of elements
		if (m_lookupboxSourceRelativeTo.isEmpty()) {
			// Lookup based on the absolute reference to the defined thing
			if (m_lookupboxFileName.isEmpty()) {
				// We're editing data directly from the scripts xml file that's already been loaded
				m_lookupboxXml = m_opbm.getScriptsXml();

			} else {
				// We must load the specified file
				m_lookupboxXml = m_opbm.loadXml(m_lookupboxFileName);
				if (m_lookupboxXml == null) {
					m_lookupboxXml = failedLoad();
					m_lookupboxSource = "root.error";
				}

			}

		} else {
			// It is relative to another control's setting, and loaded from that selected node
			m_lookupboxXml = m_parentPR.getListboxOrLookupboxFirstChildNodeByName(m_lookupboxSourceRelativeTo);

		}

		// Create the lookup box if it's not already created
		if (m_lookupBox == null)
		{
			m_lookupBox = new JList();
			m_lookupboxScroller = new JScrollPane(m_lookupBox);
			m_lookupBox.setSize(m_width, m_height);
			m_lookupBox.setFont(m_font);
			m_lookupBox.setLocation(m_x, m_y);
			m_lookupBox.setForeground(m_foreground);
			m_lookupBox.setBackground(m_background);
			m_lookupBox.setOpaque(m_opaque);
			PanelRightListboxRenderer prlr = new PanelRightListboxRenderer(m_listByP1, m_listByP2, m_listByP3, m_listByP4, m_listByP5, m_listByP6, m_listByP7, m_listByP8, m_listByP9, m_listByP10);
			if (prlr != null)
				m_lookupBox.setCellRenderer(prlr);
			m_lookupBox.setVisible(true);
			m_lookupboxScroller.setSize(m_width, m_height);
			m_lookupboxScroller.setLocation(m_x, m_y);
			m_lookupboxScroller.setVisible(true);
			m_lookupBox.addListSelectionListener(this);
			m_lookupBox.addMouseListener(this);
			m_parentPanel.add(m_lookupboxScroller);
		}

		if (m_xmlLookupboxMaster != null)
		{
			// Clear out any list that may already be there
			if (!m_xmlLookupboxMaster.isEmpty())
				m_xmlLookupboxMaster.clear();

			// Locate all the nodes within this lookupbox's xml node space
			if (m_lookupboxXml != null)
				Xml.getNodeList(m_xmlLookupboxMaster, m_lookupboxXml, m_lookupboxSource, false);

			if (!m_xmlLookupboxMaster.isEmpty())
				m_lookupBox.setListData(m_xmlLookupboxMaster.toArray());

			if (saveIndex != -1)
				m_lookupBox.setSelectedIndex(saveIndex);
			else
				m_lookupBox.setSelectedIndex(0);

			m_parentPR.updateRelativeToFields();
		}
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
		// This is a fixed-size field that's not resized,
		// so this control does nothing
	}

	/**
	 * If the specified file doesn't exist, creates an "error placeholder"
	 * to let the user know it wasn't loaded properly.
	 *
	 * @return the newly created error <code>Xml</code>
	 */
	public Xml failedLoad()
	{
		Xml root = new Xml("root", "");
		root.setFirstChild(new Xml("error", "Unable to load xml file"));
		return(root);
	}

	/** Called to physically remove the listbox, and any add, delete or clone
	 * buttons that are active.
	 */
	public void remove()
	{
		if (m_lookupBox != null)
			m_parentPanel.remove(m_lookupBox);

		if (m_lookupboxAdd != null)
			m_parentPanel.remove(m_lookupboxAdd);

		if (m_lookupboxSubtract != null)
			m_parentPanel.remove(m_lookupboxSubtract);

		if (m_lookupboxZoom != null)
			m_parentPanel.remove(m_lookupboxZoom);

		if (m_lookupboxUp!= null)
			m_parentPanel.remove(m_lookupboxUp);

		if (m_lookupboxDown != null)
			m_parentPanel.remove(m_lookupboxDown);
	}

	/**
	 * Physically selects the specified index.
	 *
	 * @param index item to update
	 */
	public void select(int index)
	{
		if (m_parentPRI.getAutoUpdate())
		{
			if (m_lookupBox != null) {
				m_lookupBox.setSelectedIndex(index);
				doOnSelect();
			}
		}
	}

	/**
	 * Specifies the relative path to the root node of the xml file to access
	 * the data element for the <code>_TYPE_LOOKUPBOX</code> control.
	 *
	 * @param source dot source, as in <code>opbm.scriptdata.flows</code>
	 * @param relativeTo relative source, as when the related item changes, this
	 * entry must be reloaded
	 */
	public void setSource(String	source,
						  String	relativeTo)
	{
		m_lookupboxSource			= source;
		m_lookupboxSourceRelativeTo	= relativeTo;
	}

	/**
	 * Assigns the name defined in edits.xml to the control
	 * @param name
	 */
	public void setName(String name)
	{
		m_lookupboxName = name;
	}

	/**
	 * Searches through <code>PanelRight</code>'s items to see if the specified
	 * <code>_TYPE_LOOKUPBOX</code> is identified by name, and if so, then returns
	 * its currently selected node.
	 * @return <code>Xml</code> for the selected item in the lookupbox
	 */
	public Xml getLookupboxFirstChildNode()
	{
		// This is it, see if there's an active Xml list and a selected item
		if (m_xmlLookupboxMaster != null)
		{
			if (m_lastIndex >= 0 && m_lastIndex < m_xmlLookupboxMaster.size())
				return(m_xmlLookupboxMaster.get(m_lastIndex).getFirstChild());
			else
				return(m_xmlLookupboxMaster.get(0).getFirstChild());
		}
		return(null);
	}

	/**
	 * Searches through <code>PanelRight</code>'s items to see if the specified
	 * <code>_TYPE_LOOKUPBOX</code> is identified by name, and if so, then returns
	 * its currently selected node.
	 * @return <code>Xml</code> for the selected item in the lookupbox
	 */
	public Xml getLookupboxNode()
	{
		// This is it, see if there's an active Xml list and a selected item
		if (m_xmlLookupboxMaster != null && m_lastIndex >= 0 && m_lastIndex < m_xmlLookupboxMaster.size())
			return(m_xmlLookupboxMaster.get(m_lastIndex));
		else
			return(null);
	}

	/**
	 *
	 * @param name edit searched in m_lookupboxEdits1..N
	 * @return empty string if not found, edit if found
	 */
	public String getEditForXml(String name)
	{
		String edit = "";

		name = name + "=";
		if (m_lookupboxEdits1.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits1.substring(m_lookupboxEdits1.indexOf("="));
		else if (m_lookupboxEdits2.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits2.substring(m_lookupboxEdits2.indexOf("="));
		else if (m_lookupboxEdits3.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits3.substring(m_lookupboxEdits3.indexOf("="));
		else if (m_lookupboxEdits4.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits4.substring(m_lookupboxEdits4.indexOf("="));
		else if (m_lookupboxEdits5.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits5.substring(m_lookupboxEdits5.indexOf("="));
		else if (m_lookupboxEdits6.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits6.substring(m_lookupboxEdits6.indexOf("="));
		else if (m_lookupboxEdits7.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits7.substring(m_lookupboxEdits7.indexOf("="));
		else if (m_lookupboxEdits8.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits8.substring(m_lookupboxEdits8.indexOf("="));
		else if (m_lookupboxEdits9.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits9.substring(m_lookupboxEdits9.indexOf("="));
		else if (m_lookupboxEdits10.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits10.substring(m_lookupboxEdits10.indexOf("="));
		else if (m_lookupboxEdits11.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits11.substring(m_lookupboxEdits11.indexOf("="));
		else if (m_lookupboxEdits12.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits12.substring(m_lookupboxEdits12.indexOf("="));
		else if (m_lookupboxEdits13.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits13.substring(m_lookupboxEdits13.indexOf("="));
		else if (m_lookupboxEdits14.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits14.substring(m_lookupboxEdits14.indexOf("="));
		else if (m_lookupboxEdits15.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits15.substring(m_lookupboxEdits15.indexOf("="));
		else if (m_lookupboxEdits16.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits16.substring(m_lookupboxEdits16.indexOf("="));
		else if (m_lookupboxEdits17.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits17.substring(m_lookupboxEdits17.indexOf("="));
		else if (m_lookupboxEdits18.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits18.substring(m_lookupboxEdits18.indexOf("="));
		else if (m_lookupboxEdits19.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits19.substring(m_lookupboxEdits19.indexOf("="));
		else if (m_lookupboxEdits20.toLowerCase().startsWith(name))
			edit = m_lookupboxEdits20.substring(m_lookupboxEdits20.indexOf("="));

		return(edit.substring(1));
	}

	/**
	 *
	 * @param name edit searched in m_lookupboxEdits1..N
	 * @return empty string if not found, edit if found
	 */
	public String getOptionsForXml(String name)
	{
		String edit = "";

		name = name + "=";
		if (m_lookupboxOptions1.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions1.substring(m_lookupboxOptions1.indexOf("="));
		else if (m_lookupboxOptions2.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions2.substring(m_lookupboxOptions2.indexOf("="));
		else if (m_lookupboxOptions3.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions3.substring(m_lookupboxOptions3.indexOf("="));
		else if (m_lookupboxOptions4.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions4.substring(m_lookupboxOptions4.indexOf("="));
		else if (m_lookupboxOptions5.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions5.substring(m_lookupboxOptions5.indexOf("="));
		else if (m_lookupboxOptions6.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions6.substring(m_lookupboxOptions6.indexOf("="));
		else if (m_lookupboxOptions7.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions7.substring(m_lookupboxOptions7.indexOf("="));
		else if (m_lookupboxOptions8.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions8.substring(m_lookupboxOptions8.indexOf("="));
		else if (m_lookupboxOptions9.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions9.substring(m_lookupboxOptions9.indexOf("="));
		else if (m_lookupboxOptions10.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions10.substring(m_lookupboxOptions10.indexOf("="));
		else if (m_lookupboxOptions11.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions11.substring(m_lookupboxOptions11.indexOf("="));
		else if (m_lookupboxOptions12.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions12.substring(m_lookupboxOptions12.indexOf("="));
		else if (m_lookupboxOptions13.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions13.substring(m_lookupboxOptions13.indexOf("="));
		else if (m_lookupboxOptions14.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions14.substring(m_lookupboxOptions14.indexOf("="));
		else if (m_lookupboxOptions15.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions15.substring(m_lookupboxOptions15.indexOf("="));
		else if (m_lookupboxOptions16.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions16.substring(m_lookupboxOptions16.indexOf("="));
		else if (m_lookupboxOptions17.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions17.substring(m_lookupboxOptions17.indexOf("="));
		else if (m_lookupboxOptions18.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions18.substring(m_lookupboxOptions18.indexOf("="));
		else if (m_lookupboxOptions19.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions19.substring(m_lookupboxOptions19.indexOf("="));
		else if (m_lookupboxOptions20.toLowerCase().startsWith(name))
			edit = m_lookupboxOptions20.substring(m_lookupboxOptions20.indexOf("="));

		return(edit.substring(1));
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

	/**
	 * Used when the listbox position changes
	 *
	 * @param e
	 */
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		Xml xml;
		String p1;

		if (!m_isMovingUpOrDown)
		{ // We only process changed values when we're not in the middle of a migrate operation
			// See if this move affects any relativeTo fields that are relative to this
			m_parentPR.updateRelativeToFields();

			if (m_lookupboxZoomAutoPreview && m_lookupboxZoomAutoPreviewPR != null)
			{ // We need to update the zoom window
				p1 = m_lookupboxZoomCommandP1;
				if (p1.equalsIgnoreCase("relativeto"))
				{
					// The name isn't a hard-coded name, because it is one of a list.
					// We have to find out which name belongs here based on whatever
					// one of the list is currently selected.
					xml = m_opbm.getListboxOrLookupboxSelectedItem(this);
					if (xml == null)
						return;		// Cannot execut the command because nothing is selected, or there is no data

					p1 = m_macroMaster.parseMacros(getEditForXml(xml.getName()));

				} else {
					p1 = m_macroMaster.parseMacros(p1);

				}
				buildZoomWindow(p1, m_lookupboxZoomCommandP2, "");
			}
			doOnSelect();
			m_lastIndex = m_lookupBox.getSelectedIndex();
		}
	}

	/**
	 * Execute the selected command when an option is selected
	 */
	public void doOnSelect()
	{
		if (!m_onSelectCommand.isEmpty())
			m_commandMaster.processCommand(this, m_onSelectCommand, m_onSelectP1, m_onSelectP2, m_onSelectP3, m_onSelectP4, m_onSelectP5, m_onSelectP6, m_onSelectP7, m_onSelectP8, m_onSelectP9, m_onSelectP10);
	}

	/**
	 * Called when the user clicks on the add button on a
	 * <code>_TYPE_LOOKUPBOX</code>
	 *
	 * @param whereTo name of the listbox or lookupbox being added to
	 * @param after
	 * @param whereFrom name of the lookupbox control being added from
	 */
	public void lookupboxAddCommand(PanelRightLookupbox		source,
									String					whereTo,
									String					after,
									String					whereFrom,
									boolean					allowCustoms)
	{
		if (source == this)
			m_parentPR.lookupboxAddClicked(m_xmlLookupboxMaster.get(m_lastIndex),
										   m_lookupboxSourceRelativeTo,
										   whereTo,
										   after,
										   whereFrom,
										   allowCustoms);
	}

	/**
	 * Called when user clicks the subtract button on a
	 * <code>_TYPE_LOOKUPBOX</code>
	 */
	public void lookupboxCommand(String					command,
								 PanelRightLookupbox	source)
	{
		if (source == this)
		{
			if (command.equalsIgnoreCase("subtract"))
				m_parentPR.lookupboxSubtractClicked(m_xmlLookupboxMaster.get(m_lastIndex));
			else if (command.equalsIgnoreCase("up"))
				moveSelectedUp();
			else if (command.equalsIgnoreCase("down"))
				moveSelectedDown();
		}
	}

	/**
	 * Moves the selected <code>Xml</code> up one position in the list.
	 */
	public void moveSelectedUp()
	{
		Xml xml;
		int saveIndex;
		boolean didMove = false;

		xml = getLookupboxNode();
		if (xml != null)
		{
			m_isMovingUpOrDown = true;
			saveIndex = m_lookupBox.getSelectedIndex();
			m_lastIndex = -1;
			didMove = xml.moveNodeUp();
			m_lookupBox.setSelectedIndex(didMove ? --saveIndex : saveIndex);
			m_lastIndex = saveIndex;
			if (didMove)
				fillOrRefillLookupBoxArray();

			m_isMovingUpOrDown = false;
		}
	}

	/**
	 * Moves the selected <code>Xml</code> down one position in the list.
	 */
	public void moveSelectedDown()
	{
		Xml xml;
		int saveIndex;
		boolean didMove = false;

		xml = getLookupboxNode();
		if (xml != null)
		{
			m_isMovingUpOrDown = true;
			saveIndex = m_lookupBox.getSelectedIndex();
			m_lastIndex = -1;
			didMove = xml.moveNodeDown();
			m_lookupBox.setSelectedIndex(didMove ? ++saveIndex : saveIndex);
			m_lastIndex = saveIndex;
			if (didMove)
				fillOrRefillLookupBoxArray();

			m_isMovingUpOrDown = false;
		}
	}

	/**
	 * Called when user clicks the zoom button on a <code>_TYPE_LOOKUPBOX</code>
	 */
	public void lookupboxZoomCommand(PanelRightLookupbox	source,
									 String					editName,
									 String					zoomFields,
									 String					dataSource)
	{
		if (source == this)
			buildZoomWindow(editName, zoomFields, dataSource);
	}

	public void buildZoomWindow(String	editName,
								String	zoomFields,
								String	dataSource)
	{
		Xml node;
		DroppableFrame fr;
		PanelRight pr = null;

		node = getLookupboxNode();

		// See if there's already a window created / displayed
		if (m_lookupboxZoomAutoPreviewFR == null)
		{
			// Create and populate the window
			fr = m_opbm.createZoomWindow(m_parentPR, Xml.getAttributeOrChild(node, "name"));
			if (m_lookupboxZoomAutoPreview)
				m_lookupboxZoomAutoPreviewFR = fr;

		} else {
			fr = m_lookupboxZoomAutoPreviewFR;
			pr = m_lookupboxZoomAutoPreviewPR;

		}

		if (pr != null)
		{
			fr.remove(pr.getJPanel());
			pr = null;
		}

		pr = PanelFactory.createRightPanelFromEdit(editName, m_opbm, m_macroMaster, m_commandMaster, null, null, m_parentPR.getJPanel(), fr, zoomFields, dataSource);

		// Position and display the content pane
		pr.setX(0);
		pr.setY(0);
		pr.setVisible(true);
		pr.navigateTo();
		m_lookupboxZoomAutoPreviewPR = pr;

		// Move to the correct location
		pr.positionListboxTo(node);

		// Show the window
		fr.setVisible(true);
		fr.setCloseNotification(this);

		// Add to our list of controlled windows (so they can be termined when the main frame exits as well)
		m_opbm.addZoomWindow(fr);
	}

	/**
	 * They've closed the zoom window, so we no longer autoupdate it
	 */
	public void notifyOnClose()
	{
		m_lookupboxZoomAutoPreviewPR = null;
		m_lookupboxZoomAutoPreviewFR = null;
	}

	public Xml getLookupboxSelectedItem()
	{
		return(getLookupboxNode());
	}

	/**
	 * Called when the user clicks on a separate control that relates to this
	 * control in some way
	 */
	public void lookupboxUpdateCommand()
	{
		fillOrRefillLookupBoxArray();
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
		long now;

		try {
//////////
// LISTBOX
			if (e.getSource().equals(m_lookupBox))
			{
				// We don't do anything when the user clicks down on here, except
				// see how long it's been since the last down click.  If it's less
				// than .4 second, we execute the dblClick command
				now = Utils.getHighResolutionTimer();
				if (now - m_lastMillisecond <= 400)
				{	// It's less than .4 second, so we issue the double click
					m_commandMaster.processCommand(m_parentPRI,
												   m_dblClickCommand,
												   m_dblClickP1,
												   m_dblClickP2,
												   m_dblClickP3,
												   m_dblClickP4,
												   m_dblClickP5,
												   m_dblClickP6,
												   m_dblClickP7,
												   m_dblClickP8,
												   m_dblClickP9,
												   m_dblClickP10);
				}
				m_lastMillisecond = now;

//////////
// ADD
			} else if (e.getSource().equals(m_lookupboxAdd)) {
				// Before the add, we have to save any current changes
				m_commandMaster.processCommand(this,
											   m_lookupboxAddCommand,
											   m_lookupboxAddCommandP1,
											   m_lookupboxAddCommandP2,
											   m_lookupboxAddCommandP3,
											   m_lookupboxAddCommandP4,
											   m_lookupboxAddCommandP5,
											   m_lookupboxAddCommandP6,
											   m_lookupboxAddCommandP7,
											   m_lookupboxAddCommandP8,
											   m_lookupboxAddCommandP9,
											   m_lookupboxAddCommandP10);

//////////
// DELETE
			} else if (e.getSource().equals(m_lookupboxSubtract)) {
				// They clicked on the delete button on the specified entry
				m_commandMaster.processCommand(this,
											   m_lookupboxSubtractCommand,
											   m_lookupboxSubtractCommandP1,
											   m_lookupboxSubtractCommandP2,
											   m_lookupboxSubtractCommandP3,
											   m_lookupboxSubtractCommandP4,
											   m_lookupboxSubtractCommandP5,
											   m_lookupboxSubtractCommandP6,
											   m_lookupboxSubtractCommandP7,
											   m_lookupboxSubtractCommandP8,
											   m_lookupboxSubtractCommandP9,
											   m_lookupboxSubtractCommandP10);


//////////
// ZOOM
			} else if (e.getSource().equals(m_lookupboxZoom)) {
				// Before the clone, we have to save any current changes
				m_commandMaster.processCommand(this,
											   m_lookupboxZoomCommand,
											   m_lookupboxZoomCommandP1,
											   m_lookupboxZoomCommandP2,
											   m_lookupboxZoomCommandP3,
											   m_lookupboxZoomCommandP4,
											   m_lookupboxZoomCommandP5,
											   m_lookupboxZoomCommandP6,
											   m_lookupboxZoomCommandP7,
											   m_lookupboxZoomCommandP8,
											   m_lookupboxZoomCommandP9,
											   m_lookupboxZoomCommandP10);


//////////
// UP
			} else if (e.getSource().equals(m_lookupboxUp)) {
				// Before the clone, we have to save any current changes
				m_commandMaster.processCommand(this,
											   m_lookupboxUpCommand,
											   m_lookupboxUpCommandP1,
											   m_lookupboxUpCommandP2,
											   m_lookupboxUpCommandP3,
											   m_lookupboxUpCommandP4,
											   m_lookupboxUpCommandP5,
											   m_lookupboxUpCommandP6,
											   m_lookupboxUpCommandP7,
											   m_lookupboxUpCommandP8,
											   m_lookupboxUpCommandP9,
											   m_lookupboxUpCommandP10);


//////////
// DOWN
			} else if (e.getSource().equals(m_lookupboxDown)) {
				// Before the clone, we have to save any current changes
				m_commandMaster.processCommand(this,
											   m_lookupboxDownCommand,
											   m_lookupboxDownCommandP1,
											   m_lookupboxDownCommandP2,
											   m_lookupboxDownCommandP3,
											   m_lookupboxDownCommandP4,
											   m_lookupboxDownCommandP5,
											   m_lookupboxDownCommandP6,
											   m_lookupboxDownCommandP7,
											   m_lookupboxDownCommandP8,
											   m_lookupboxDownCommandP9,
											   m_lookupboxDownCommandP10);

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
	public void mouseReleased(MouseEvent e) {
		m_lookupBox.requestFocusInWindow();
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

	private Opbm			m_opbm;
	private Xml				m_lookupboxXml;
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
	private JList			m_lookupBox;
	private JScrollPane		m_lookupboxScroller;
	private List<Xml>		m_xmlLookupboxMaster;

	private int				m_lastIndex;
	private int				m_width;
	private int				m_height;
	private int				m_x;
	private int				m_y;
	private boolean			m_isMovingUpOrDown;		// Is moving up or down overrides save operations
	private long			m_lastMillisecond;		// The last millisecond when the mouse button was clicked down within the listbox (used to catch double-clicks)

	private String			m_lookupboxButtons;				// Contains any combination of "+" "-" and "zoom" as in "+-zoom" or "+zoom-" or "zoom-+", etc.
	private JButton			m_lookupboxAdd;					// Add button for lookupboxes
	private JButton			m_lookupboxSubtract;			// Subtract button for lookupboxes
	private JButton			m_lookupboxZoom;				// Zoom button for lookupboxes
	private JButton			m_lookupboxUp;					// Up button for lookupboxes
	private JButton			m_lookupboxDown;				// Down button for lookupboxes
	private String			m_lookupboxSource;				// Location to access nodes within the xml file
	private String			m_lookupboxName;				// Name given to this control in editx.xml
	private String			m_lookupboxSourceRelativeTo;	// Relative location to access nodes from, refers to a separate listbox control defined on the edit
	private String			m_lookupboxFileName;			// Source filename of this control's xml file (file being edited)
	private String			m_lookupboxLocation;			// The location within the specified fileName (XML file) where the data is found
	private String			m_lookupboxForEach;				// For each of these elements within the lookupboxLocation specified, display / edit relative content

	private String			m_listByP1;						// Parameter #1
	private String			m_listByP2;						// Parameter #2
	private String			m_listByP3;						// Parameter #3
	private String			m_listByP4;						// Parameter #4
	private String			m_listByP5;						// Parameter #5
	private String			m_listByP6;						// Parameter #6
	private String			m_listByP7;						// Parameter #7
	private String			m_listByP8;						// Parameter #8
	private String			m_listByP9;						// Parameter #9
	private String			m_listByP10;					// Parameter #10

	private String			m_dblClickCommand;
	private String			m_dblClickP1;
	private String			m_dblClickP2;
	private String			m_dblClickP3;
	private String			m_dblClickP4;
	private String			m_dblClickP5;
	private String			m_dblClickP6;
	private String			m_dblClickP7;
	private String			m_dblClickP8;
	private String			m_dblClickP9;
	private String			m_dblClickP10;

	private String			m_lookupboxAddCommand;
	private String			m_lookupboxAddCommandP1;
	private String			m_lookupboxAddCommandP2;
	private String			m_lookupboxAddCommandP3;
	private String			m_lookupboxAddCommandP4;
	private String			m_lookupboxAddCommandP5;
	private String			m_lookupboxAddCommandP6;
	private String			m_lookupboxAddCommandP7;
	private String			m_lookupboxAddCommandP8;
	private String			m_lookupboxAddCommandP9;
	private String			m_lookupboxAddCommandP10;

	private String			m_lookupboxSubtractCommand;
	private String			m_lookupboxSubtractCommandP1;
	private String			m_lookupboxSubtractCommandP2;
	private String			m_lookupboxSubtractCommandP3;
	private String			m_lookupboxSubtractCommandP4;
	private String			m_lookupboxSubtractCommandP5;
	private String			m_lookupboxSubtractCommandP6;
	private String			m_lookupboxSubtractCommandP7;
	private String			m_lookupboxSubtractCommandP8;
	private String			m_lookupboxSubtractCommandP9;
	private String			m_lookupboxSubtractCommandP10;

	private DroppableFrame	m_lookupboxZoomAutoPreviewFR;
	private PanelRight		m_lookupboxZoomAutoPreviewPR;
	private String			m_lookupboxZoomCommand;
	private boolean			m_lookupboxZoomAutoPreview;
	private String			m_lookupboxZoomCommandP1;
	private String			m_lookupboxZoomCommandP2;
	private String			m_lookupboxZoomCommandP3;
	private String			m_lookupboxZoomCommandP4;
	private String			m_lookupboxZoomCommandP5;
	private String			m_lookupboxZoomCommandP6;
	private String			m_lookupboxZoomCommandP7;
	private String			m_lookupboxZoomCommandP8;
	private String			m_lookupboxZoomCommandP9;
	private String			m_lookupboxZoomCommandP10;

	private String			m_lookupboxUpCommand;
	private String			m_lookupboxUpCommandP1;
	private String			m_lookupboxUpCommandP2;
	private String			m_lookupboxUpCommandP3;
	private String			m_lookupboxUpCommandP4;
	private String			m_lookupboxUpCommandP5;
	private String			m_lookupboxUpCommandP6;
	private String			m_lookupboxUpCommandP7;
	private String			m_lookupboxUpCommandP8;
	private String			m_lookupboxUpCommandP9;
	private String			m_lookupboxUpCommandP10;

	private String			m_lookupboxDownCommand;
	private String			m_lookupboxDownCommandP1;
	private String			m_lookupboxDownCommandP2;
	private String			m_lookupboxDownCommandP3;
	private String			m_lookupboxDownCommandP4;
	private String			m_lookupboxDownCommandP5;
	private String			m_lookupboxDownCommandP6;
	private String			m_lookupboxDownCommandP7;
	private String			m_lookupboxDownCommandP8;
	private String			m_lookupboxDownCommandP9;
	private String			m_lookupboxDownCommandP10;

	private String			m_lookupboxEdits1;
	private String			m_lookupboxEdits2;
	private String			m_lookupboxEdits3;
	private String			m_lookupboxEdits4;
	private String			m_lookupboxEdits5;
	private String			m_lookupboxEdits6;
	private String			m_lookupboxEdits7;
	private String			m_lookupboxEdits8;
	private String			m_lookupboxEdits9;
	private String			m_lookupboxEdits10;
	private String			m_lookupboxEdits11;
	private String			m_lookupboxEdits12;
	private String			m_lookupboxEdits13;
	private String			m_lookupboxEdits14;
	private String			m_lookupboxEdits15;
	private String			m_lookupboxEdits16;
	private String			m_lookupboxEdits17;
	private String			m_lookupboxEdits18;
	private String			m_lookupboxEdits19;
	private String			m_lookupboxEdits20;

	private String			m_lookupboxOptions1;
	private String			m_lookupboxOptions2;
	private String			m_lookupboxOptions3;
	private String			m_lookupboxOptions4;
	private String			m_lookupboxOptions5;
	private String			m_lookupboxOptions6;
	private String			m_lookupboxOptions7;
	private String			m_lookupboxOptions8;
	private String			m_lookupboxOptions9;
	private String			m_lookupboxOptions10;
	private String			m_lookupboxOptions11;
	private String			m_lookupboxOptions12;
	private String			m_lookupboxOptions13;
	private String			m_lookupboxOptions14;
	private String			m_lookupboxOptions15;
	private String			m_lookupboxOptions16;
	private String			m_lookupboxOptions17;
	private String			m_lookupboxOptions18;
	private String			m_lookupboxOptions19;
	private String			m_lookupboxOptions20;

	private String			m_onSelectCommand;
	private String			m_onSelectP1;
	private String			m_onSelectP2;
	private String			m_onSelectP3;
	private String			m_onSelectP4;
	private String			m_onSelectP5;
	private String			m_onSelectP6;
	private String			m_onSelectP7;
	private String			m_onSelectP8;
	private String			m_onSelectP9;
	private String			m_onSelectP10;
}
