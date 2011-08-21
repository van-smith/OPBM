/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for all panels (windows) that are created
 * dynamically at runtime based on real system parameters.
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

import opbm.common.Utils;
import java.awt.Font;
import java.awt.Color;
import java.awt.Label;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import opbm.common.Commands;
import opbm.common.Macros;
import opbm.Opbm;
import opbm.common.Xml;

/** Generates left- and right-side panels, implementing PanelLeft and PanelRight
 * classes, along with their child PanelLeftItem and PanelRightItem classes.
 *
 * Follows a particular, known syntax from the panels.xml file, along with
 * edits.xml for the PanelRight class generation.
 *
 * @author Rick C. Hodgin
 */
public class PanelFactory {
	/** Create a Left pr object for every <panel> tag in the m_root Xml
	 * tree, and populate its contents by the data contained within.
	 *
	 * Left pr objects are used for navigation and can only have four types
	 * of objects: 1) navigation, 2) spacer, 3) header, 4) menu.
	 *
	 * Each of these is parsed and converted automatically into its proper
	 * display form, whereby it is presented unto the user as a navigable list
	 * of menu items logically arranged.
	 *
	 * @param opbm Parent object referenced for global method calls
	 * @param macroMaster Macro object referenced for global method calls
	 * @param header <code>JLabel</code> object used for determining geometry
	 * @param statusBar <code>JLabel</code> object used for determining geometry
	 * @param panelReference <code>JPanel</code> object used for determining geometry
	 * @param parent <code>JFrame</code> object all new panels are added to
	 * @return true or false indicating success
	 */
	public static boolean createLeftPanelObjects(Opbm	opbm,
												 Macros	macroMaster,
												 JLabel	header,
												 Label	statusBar,
												 JPanel	panelReference,
												 JFrame	parent)
	{
		Xml xmlPanel, xmlNavigation, xmlCommand, xmlUnknown;
		Font navigation, heading, menu, text;
		String name, color, height, bgcolor, fgcolor;
		int i, j, lnTop, lnLeft;
		PanelLeft panel;
		PanelLeftItem pn;
		PanelLeftItem pi;
		List<Xml>		panelsXml	= new ArrayList<Xml>(0);
		List<Xml>		itemsXml	= new ArrayList<Xml>(0);		// Reusable, used for navigation, entries, etc.
		List<String>	tags		= new ArrayList<String>(0);
		tags.add("spacer");
		tags.add("heading");
		tags.add("menu");
		tags.add("text");

		// Setup the default fonts used by this process
		if (opbm.isFontOverride()) {
			// For non-Windows systems, we need to use a smaller font in case 'arial' isn't installed and it uses a larger default font
			navigation	= new Font("Arial", Font.BOLD, 16);
			heading		= new Font("Arial", Font.BOLD, 24);
			menu		= new Font("Arial", Font.BOLD, 16);
			text		= new Font("Arial", Font.PLAIN, 14);

		} else {
			// For newer Windows systems, the Calibri font is beautiful
			navigation	= new Font("Calibri", Font.BOLD, 18);
			heading		= new Font("Calibri", Font.BOLD, 30);
			menu		= new Font("Calibri", Font.BOLD, 18);
			text		= new Font("Calibri", Font.PLAIN, 16);
		}

		// Begin by loading every left pr entry in panels
		Xml.getNodeList(panelsXml, opbm.getPanelXml(), "opbm.panels.left", false);
		if (panelsXml.isEmpty()) {		// No panels were found
			return(false);
		}

		// Iterate through every pr, obtaining its directives within
		for (i = 0; i < panelsXml.size(); i++) {
			// Create a new PanelLeft object for this item
			xmlPanel = panelsXml.get(i);
			name	= xmlPanel.getAttribute("name");		// Each pr must have a "name" attribute, like [name="main"]
			bgcolor	= xmlPanel.getAttribute("bgcolor");		// Each pr may have a background color attribute, like [bgcolor="ffff00"]
			if (!name.isEmpty()) {
				panel = new PanelLeft(parent, name, opbm);	// Create the PanelLeft object
				opbm.addPanelLeft(panel);
				panel.setX(0);
				panel.setY(header.getHeight());
				panel.setWidth(panelReference.getWidth());
				panel.setHeight(panelReference.getHeight());
				if (!bgcolor.isEmpty())
					panel.setBackColor(bgcolor);
				panel.refresh();

				// Load navigation items
				lnTop = 10;								// Begin at 10 pixels down from top of pr
				lnLeft = 10;							// Begin at 10 pixels over
				itemsXml.clear();
				Xml.getNodeList(itemsXml, xmlPanel.getFirstChild(), "navigation", false);
				if (!itemsXml.isEmpty()) {
					// There is navigation data, load each in turn
					for (j = 0; j < itemsXml.size(); j++) {
						xmlNavigation = itemsXml.get(j);
						pn = new PanelLeftItem(opbm, macroMaster, statusBar, panel.getJPanel());
						xmlCommand	= xmlNavigation.getChildNode("command");
						pn.setText(xmlNavigation.getAttributeOrChild("text"));

						bgcolor = Utils.verifyColorFormat(opbm.expandMacros(Xml.getAttribute(xmlNavigation.getChildNode("text"), "bgcolor")));
						if (!bgcolor.isEmpty()) {
							pn.setBackColor(bgcolor);
							pn.setOpaque(true);
						} else {
							pn.setBackColor(Utils.encodeColorFormat(255,160,50));
							pn.setOpaque(false);
						}

						fgcolor = Utils.verifyColorFormat(opbm.expandMacros(Xml.getAttribute(xmlNavigation.getChildNode("text"), "fgcolor")));
						if (!fgcolor.isEmpty())
							pn.setForeColor(fgcolor);
						else
							pn.setForeColor(Utils.encodeColorFormat(190,118,37));

						pn.setFont(navigation);
						pn.setPosition(lnLeft, lnTop);
						pn.setSize(50, 20);
						lnLeft += 50;

						pn.setCommand(xmlCommand.getText());
						pn.setCommandP1(Xml.getAttribute(xmlCommand, "p1"));
						pn.setCommandP2(Xml.getAttribute(xmlCommand, "p2"));
						pn.setCommandP3(Xml.getAttribute(xmlCommand, "p3"));
						pn.setCommandP4(Xml.getAttribute(xmlCommand, "p4"));
						pn.setCommandP5(Xml.getAttribute(xmlCommand, "p5"));
						pn.setCommandP6(Xml.getAttribute(xmlCommand, "p6"));
						pn.setCommandP7(Xml.getAttribute(xmlCommand, "p7"));
						pn.setCommandP8(Xml.getAttribute(xmlCommand, "p8"));
						pn.setCommandP9(Xml.getAttribute(xmlCommand, "p9"));
						pn.setCommandP10(Xml.getAttribute(xmlCommand, "p10"));

						// Tooltips are evaluated at display for macro expansion, so no macro expansion here
						pn.setTooltip(xmlNavigation.getAttributeOrChild("tooltip"));
						panel.addNavigation(pn);
					}
					lnTop += 25;
				}

				// Load the menu entries
				itemsXml.clear();
				Xml.getNodeListOfNamedTags(itemsXml, xmlPanel, "", tags, true);
				if (!itemsXml.isEmpty()) {
					// There is/are some menu entry(ies)
					for (j = 0; j < itemsXml.size(); j++) {
						xmlUnknown = itemsXml.get(j);
//////////
// SPACER
						if (xmlUnknown.getName().equalsIgnoreCase("spacer")) {
							// Now it's known, adding a blank spacer, an optional <spacer height="50"/> attribute can be used
							height = Xml.getAttribute(xmlUnknown, "height");
							if (!height.isEmpty())
								lnTop += Integer.parseInt(height);		// They specified a height
							else
								lnTop += 25;							// Use default value of 25

//////////
// HEADING
						} else if (xmlUnknown.getName().equalsIgnoreCase("heading")) {
							// Now it's known, adding a heading item
							pi = new PanelLeftItem(opbm, macroMaster, statusBar, panel.getJPanel());
							pi.setText(xmlUnknown.getAttributeOrChild("text"));	// We don't parse macros because they are parsed in real-time by the PanelLeftItem()

							bgcolor = Utils.verifyColorFormat(Xml.getAttribute(xmlUnknown.getChildNode("text"), "bgcolor"));
							if (!bgcolor.isEmpty()) {
								pi.setBackColor(bgcolor);
								pi.setOpaque(true);
							} else {
								pi.setBackColor(Utils.encodeColorFormat(255,255,255));
								pi.setOpaque(false);
							}

							fgcolor = Utils.verifyColorFormat(Xml.getAttribute(xmlUnknown.getChildNode("text"), "fgcolor"));
							if (!fgcolor.isEmpty())
								pi.setForeColor(fgcolor);
							else
								pi.setForeColor(Utils.encodeColorFormat(255,160,50));

							pi.setFont(heading);
							pi.setPosition(15, lnTop);
							pi.setSize(panelReference.getWidth() - 20, 40);
							panel.addItem(pi);
							lnTop += 40;

//////////
// MENU
						} else if (xmlUnknown.getName().equalsIgnoreCase("menu")) {
							// Now it's known, adding a a menu item
							pi = new PanelLeftItem(opbm, macroMaster, statusBar, panel.getJPanel());
							xmlCommand	= xmlUnknown.getChildNode("command");
							pi.setText(xmlUnknown.getAttributeOrChild("text"));	// We don't parse macros because they are parsed in real-time by the PanelLeftItem()

							bgcolor = Utils.verifyColorFormat(Xml.getAttribute(xmlUnknown.getChildNode("text"), "bgcolor"));
							if (!bgcolor.isEmpty()) {
								pi.setBackColor(bgcolor);
								pi.setOpaque(true);
							} else {
								pi.setBackColor(Utils.encodeColorFormat(255,160,50));
								pi.setOpaque(false);
							}

							fgcolor = Utils.verifyColorFormat(Xml.getAttribute(xmlUnknown.getChildNode("text"), "fgcolor"));
							if (!fgcolor.isEmpty())
								pi.setForeColor(fgcolor);
							else
								pi.setForeColor(Utils.encodeColorFormat(0,0,255));

							pi.setFont(menu);
							pi.setCommand(xmlUnknown.getAttributeOrChild("command"));
							pi.setCommandP1(Xml.getAttribute(xmlCommand, "p1"));
							pi.setCommandP2(Xml.getAttribute(xmlCommand, "p2"));
							pi.setCommandP3(Xml.getAttribute(xmlCommand, "p3"));
							pi.setCommandP4(Xml.getAttribute(xmlCommand, "p4"));
							pi.setCommandP5(Xml.getAttribute(xmlCommand, "p5"));
							pi.setCommandP6(Xml.getAttribute(xmlCommand, "p6"));
							pi.setCommandP7(Xml.getAttribute(xmlCommand, "p7"));
							pi.setCommandP8(Xml.getAttribute(xmlCommand, "p8"));
							pi.setCommandP9(Xml.getAttribute(xmlCommand, "p9"));
							pi.setCommandP10(Xml.getAttribute(xmlCommand, "p10"));

							// Tooltips are evaluated at display for macro expansion, so no macro expansion here
							pi.setTooltip(xmlUnknown.getAttributeOrChild("tooltip"));
							pi.setPosition(35, lnTop);
							pi.setSize(panelReference.getWidth() - 40, 22);
							panel.addItem(pi);
							lnTop += 22;

//////////
// TEXT
						} else if (xmlUnknown.getName().equalsIgnoreCase("text")) {
							// Now it's known, adding a display-only item
							pi = new PanelLeftItem(opbm, macroMaster, statusBar, panel.getJPanel());
							pi.setText(xmlUnknown.getText());	// We don't parse macros because they are parsed in real-time by the PanelLeftItem()

							bgcolor = Utils.verifyColorFormat(Xml.getAttribute(xmlUnknown, "bgcolor"));
							if (!bgcolor.isEmpty()) {
								pi.setBackColor(bgcolor);
								pi.setOpaque(true);
							} else {
								pi.setBackColor(Utils.encodeColorFormat(255,160,50));
								pi.setOpaque(false);
							}

							fgcolor = Utils.verifyColorFormat(Xml.getAttribute(xmlUnknown, "fgcolor"));
							if (!fgcolor.isEmpty())
								pi.setForeColor(fgcolor);
							else
								pi.setForeColor(Utils.encodeColorFormat(128,128,92));

							pi.setFont(text);
							pi.setPosition(15, lnTop);
							pi.setSize(panelReference.getWidth() - 20, 20);
							panel.addItem(pi);
							lnTop += 18;

						}
					}
				}
			}
		}
		return(true);
	}

	/** Create a Right pr from a name object using <rawedit> tag data specified
	 * in edits.xml.
	 *
	 * Right pr objects are used for editing and have the multiple types
	 * of objects specified in the PanelRightItem class.  Refer to the static
	 * final int variables prefixed with _TYPE_ to identify types.
	 *
	 * @param opbm Parent object referenced for global method calls
	 * @param macroMaster Macro object referenced for global method calls
	 * @param header <code>JLabel</code> object used for determining geometry
	 * @param statusBar <code>JLabel</code> object used for determining geometry
	 * @param panelReference <code>JPanel</code> object used for determining geometry
	 * @param parent <code>JFrame</code> object all new panels are added to
	 * @return true or false indicating success
	 */
	public static PanelRight createRightPanelFromRawEdit(String		name,
														 Opbm		opbm,
														 Macros		macroMaster,
														 Commands	commandMaster,
														 JLabel		header,
														 Label		statusBar,
														 JPanel		panelReference,
														 JFrame		parent)
	{
		String pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, readOnly;
		Xml re, preCommand, postCommand;
		int i;
		List<Xml> rawEditXml = new ArrayList<Xml>(0);
		PanelRight panel;
		PanelRightItem pi;
		Font f;

		if (opbm.isFontOverride())
			f = new Font("Arial", Font.PLAIN, 14);
		else
			f = new Font("Calibri", Font.PLAIN, 16);

		// Begin by loading every left pr entry in panels
		Xml.getNodeList(rawEditXml, opbm.getEditXml(), "opbm.rawedits.rawedit", false);
		if (rawEditXml.isEmpty()) {	// No name entries were found
			return(null);
		}

		// Iterate through every pr, obtaining its directives within
		for (i = 0; i < rawEditXml.size(); i++)
		{
			// Create a new PanelRightobject for this item
			re = rawEditXml.get(i);
			if (re.getAttribute("name").equalsIgnoreCase(name))
			{
				// This is the matching entry
				panel = new PanelRight(parent, macroMaster, commandMaster, header, statusBar, name, opbm, re);
				panel.setBackColor(panelReference.getBackground());
				panel.setWidth(panelReference.getWidth());
				panel.setHeight(panelReference.getHeight());
				panel.setX(panelReference.getX());
				panel.setY(panelReference.getY());

				// Rawedit right panels have one JTextArea (editbox) area that
				// encompasses the entire rightpanel object, save a 5 pixel border
				pi = new PanelRightItem(opbm, macroMaster, commandMaster, statusBar, panel.getJPanel(), panel);
				pi.setIdentity(PanelRightItem._TYPE_EDITBOX);
				pi.setSize(panelReference.getWidth() - 10, panelReference.getHeight() - 10);
				pi.setPosition(5, 5);
				pi.setFont(f);

				preCommand = re.getChildNode("precommand");
				if (preCommand != null)
				{
					pc	= macroMaster.parseMacros(preCommand.getText());
					p1	= macroMaster.parseMacros(preCommand.getAttribute("p1"));
					p2	= macroMaster.parseMacros(preCommand.getAttribute("p2"));
					p3	= macroMaster.parseMacros(preCommand.getAttribute("p3"));
					p4	= macroMaster.parseMacros(preCommand.getAttribute("p4"));
					p5	= macroMaster.parseMacros(preCommand.getAttribute("p5"));
					p6	= macroMaster.parseMacros(preCommand.getAttribute("p6"));
					p7	= macroMaster.parseMacros(preCommand.getAttribute("p7"));
					p8	= macroMaster.parseMacros(preCommand.getAttribute("p8"));
					p9	= macroMaster.parseMacros(preCommand.getAttribute("p9"));
					p10	= macroMaster.parseMacros(preCommand.getAttribute("p10"));
					panel.setPreCommand(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
				}

				panel.setFilename	(macroMaster.parseMacros(re.getChildNode("filename").getText()));
				panel.setTooltip	(macroMaster.parseMacros(re.getChildNode("tooltip").getText()));

				postCommand = re.getChildNode("postcommand");
				if (postCommand != null)
				{
					pc	= macroMaster.parseMacros(postCommand.getText());
					p1	= macroMaster.parseMacros(postCommand.getAttribute("p1"));
					p2	= macroMaster.parseMacros(postCommand.getAttribute("p2"));
					p3	= macroMaster.parseMacros(postCommand.getAttribute("p3"));
					p4	= macroMaster.parseMacros(postCommand.getAttribute("p4"));
					p5	= macroMaster.parseMacros(postCommand.getAttribute("p5"));
					p6	= macroMaster.parseMacros(postCommand.getAttribute("p6"));
					p7	= macroMaster.parseMacros(postCommand.getAttribute("p7"));
					p8	= macroMaster.parseMacros(postCommand.getAttribute("p8"));
					p9	= macroMaster.parseMacros(postCommand.getAttribute("p9"));
					p10	= macroMaster.parseMacros(postCommand.getAttribute("p10"));
					panel.setPostCommand(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
				}

				readOnly = re.getAttribute("readonly");
				if (readOnly.equalsIgnoreCase("true") || readOnly.equalsIgnoreCase("yes"))
				{
					// It's a read-only rawEdit--which I suppose really makes it a rawView, doesn't it. :-) Oh, I crack myself up. :-)
					pi.setReadOnly(true);
				}

				panel.addItem(pi);

				// When we get here, the control is created (not yet displayed or populated, the caller must do that)
				return(panel);
			}
		}
		// If we get here, the rawedit wasn't found in edits.xml
		return(null);
	}

	/** Create a Right pr from an edit object using <edit> tag data specified
	 * in edits.xml.
	 *
	 * Right pr objects are used for editing and have the multiple types
	 * of objects specified in the PanelRightItem class.  Refer to the static
	 * final int variables prefixed with _TYPE_ to identify types.
	 *
	 * @param name name given to the panel from edits.xml
	 * @param opbm Parent object referenced for global method calls
	 * @param macroMaster Macro object referenced for global method calls
	 * @param commandMaster <code>Commands</code> object used for processing fieldNames
	 * @param header <code>JLabel</code> object used for determining geometry
	 * @param statusBar <code>JLabel</code> object used for determining geometry
	 * @param refPanel <code>JPanel</code> object used for determining geometry
	 * @param parent <code>JFrame</code> object all new panels are added to
	 * @param zoomField optional string indicate which fields should be included
	 * (if present, each entry must have "zoom='x'" where x contains the
	 * zoomField string)
	 * @return true or false indicating success
	 */
	public static PanelRight createRightPanelFromEdit(String	name,
													  Opbm		opbm,
													  Macros	macroMaster,
													  Commands	commandMaster,
													  JLabel	header,
													  Label		statusBar,
													  JPanel	refPanel,
													  JFrame	parent,
													  String	zoomField,
													  String	dataSource)
	{
		String s, pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, readOnly, swidth, sheight, zoom;
		Xml edit, child, preCommand, postCommand;
		boolean okToUse;
		Font textFont;
		int i, left, top, refWidth, refHeight, width;
		List<Xml> editsXml = new ArrayList<Xml>(0);
		PanelRight pr;
		PanelRightItem pri;

		refWidth	= refPanel.getWidth();
		refHeight	= refPanel.getHeight();
		if (opbm.isFontOverride())
			textFont	= new Font("Arial", Font.PLAIN, 16);
		else
			textFont	= new Font("Calibri", Font.PLAIN, 18);

		// Begin by loading every left pr entry in panels
		Xml.getNodeList(editsXml, opbm.getEditXml(), "opbm.edits.edit", false);
		if (editsXml.isEmpty()) {	// No name entries were found
			return(null);
		}

		// Iterate through every pr, obtaining its directives within
		for (i = 0; i < editsXml.size(); i++)
		{
			// Create a new PanelRightobject for this item
			edit = editsXml.get(i);
			if (edit.getAttribute("name").equalsIgnoreCase(name))
			{
				pr = new PanelRight(parent, macroMaster, commandMaster, header, statusBar, name, opbm, edit);
				pr.setBackColor(refPanel.getBackground());
				pr.setForeColor(refPanel.getForeground());
				pr.setWidth(refPanel.getWidth());
				pr.setHeight(refPanel.getHeight());
				pr.setX(refPanel.getX());
				pr.setY(refPanel.getY());

				pri		= null;
				left	= 5;
				top		= 5;

				// Iterate through every edit child, parsing each one as we go
				child = edit.getFirstChild();
				while (child != null)
				{
					// Grab any zoom attribute (if any)
					zoom = macroMaster.parseMacros(Xml.getAttributeOrChild(child, "zoom"));
					// if zoomField is populated, any field we use must have a "zoom='x'" tag
					// for this item to be included, which must contain the zoomField conent.
					// Note:  zoomFields can have multiple entries, such as zoom='x, y, z'
					//        where x, y and z are separate words allowed.
//////////
// NEWCOLUMN
					if (child.getName().equalsIgnoreCase("newcolumn")) {
						swidth	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "width"));
						if (swidth.isEmpty())
							width = 260;
						else
							width = Integer.valueOf(swidth);

						left += width;
						top = 5;


//////////
// LISTBOX
					} else if (child.getName().equalsIgnoreCase("listbox")) {
						if (zoomField.isEmpty() || (!zoomField.isEmpty() && zoom.contains(zoomField))) {
							// Okay to use
							pri = new PanelRightItem(opbm, macroMaster, commandMaster, statusBar, pr.getJPanel(), pr);
							pri.setIdentity(PanelRightItem._TYPE_LISTBOX);
							setAutoUpdate(child, pri, macroMaster);
							setName(child, pri, macroMaster);
							setSizeAndPosition(child, pr, pri, macroMaster, 250, refHeight - 5 - top, left, top, 5);
							setFont(child, pri, macroMaster, textFont);
							setColors(child, pri, macroMaster);
							if (setListboxAttributes(child, pri, macroMaster)) {
								pri.updateListBox();
								pri.setText(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "text")));
								pri.setTooltip(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "tooltip")));
								left	+= 260;
								top		= 5;

							} else {
								// Invalid entry
								statusBar.setText("Found invalid listbox entry in " + edit.getName() + "." + child.getName());
								pri = null;

							}
						}


//////////
// LOOKUPBOX
					} else if (child.getName().equalsIgnoreCase("lookupbox")) {
						if (zoomField.isEmpty() || (!zoomField.isEmpty() && zoom.contains(zoomField))) {
							// Okay to use
							pri = new PanelRightItem(opbm, macroMaster, commandMaster, statusBar, pr.getJPanel(), pr);
							pri.setIdentity(PanelRightItem._TYPE_LOOKUPBOX);
							setAutoUpdate(child, pri, macroMaster);
							setName(child, pri, macroMaster);
							setSizeAndPosition(child, pr, pri, macroMaster, 250, 157, left, top, 5);
							setFont(child, pri, macroMaster, textFont);
							setColors(child, pri, macroMaster);
							if (setLookupboxAttributes(child, pri, macroMaster)) {
								pri.updateLookupBox();
								pri.setText(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "text")));
								pri.setTooltip(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "tooltip")));
								top += pri.getHeight() + 15;

							} else {
								// Invalid entry
								statusBar.setText("Found invalid lookupbox entry in " + edit.getName() + "." + child.getName());
								pri = null;

							}
						}


//////////
// PRECOMMAND
					} else if (child.getName().equalsIgnoreCase("precommand")) {
						pc	= macroMaster.parseMacros(child.getText());
						p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p1"));
						p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p2"));
						p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p3"));
						p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p4"));
						p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p5"));
						p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p6"));
						p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p7"));
						p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p8"));
						p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p9"));
						p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p10"));
						pr.setPreCommand(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);


//////////
// POSTCOMMAND
					} else if (child.getName().equalsIgnoreCase("postcommand")) {
						pc	= macroMaster.parseMacros(child.getText());
						p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p1"));
						p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p2"));
						p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p3"));
						p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p4"));
						p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p5"));
						p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p6"));
						p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p7"));
						p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p8"));
						p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p9"));
						p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "p10"));
						pr.setPostCommand(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);


///////////
// LABEL
					} else if (child.getName().equalsIgnoreCase("label")) {
						if (zoomField.isEmpty() || (!zoomField.isEmpty() && zoom.contains(zoomField))) {
							// Okay to use
							pri = new PanelRightItem(opbm, macroMaster, commandMaster, statusBar, pr.getJPanel(), pr);
							pri.setIdentity(PanelRightItem._TYPE_LABEL);
							setAutoUpdate(child, pri, macroMaster);
							setName(child, pri, macroMaster);
							setSizeAndPosition(child, pr, pri, macroMaster, refWidth - left - 5, 25, left, top, 5);
							setFont(child, pri, macroMaster, textFont);
							if (!setColors(child, pri, macroMaster))
								pri.setForeColor(pr.getForeColor());
							pri.setText(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "text")));
							pri.setField(Xml.getAttributeOrChild(child, "field"));
							pri.setTooltip(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "tooltip")));
							setCommand(child.getChildNode("command"), pri, macroMaster);
							top += pri.getHeight();
						}


///////////
// LINK
					} else if (child.getName().equalsIgnoreCase("link")) {
						if (zoomField.isEmpty() || (!zoomField.isEmpty() && zoom.contains(zoomField))) {
							// Okay to use
							pri = new PanelRightItem(opbm, macroMaster, commandMaster, statusBar, pr.getJPanel(), pr);
							pri.setIdentity(PanelRightItem._TYPE_LINK);
							setAutoUpdate(child, pri, macroMaster);
							setName(child, pri, macroMaster);
							setSizeAndPosition(child, pr, pri, macroMaster, refWidth - left - 5, 25, left, top, 5);
							setFont(child, pri, macroMaster, textFont);
							if (!setColors(child, pri, macroMaster))
								pri.setForeColor(new Color(0,0,255));
							pri.setText(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "text")));
							pri.setField(Xml.getAttributeOrChild(child, "field"));
							pri.setTooltip(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "tooltip")));
							setCommand(child.getChildNode("command"), pri, macroMaster);
							top += pri.getHeight();
						}


///////////
// BUTTON
					} else if (child.getName().equalsIgnoreCase("button")) {
						if (zoomField.isEmpty() || (!zoomField.isEmpty() && zoom.contains(zoomField))) {
							// Okay to use
							pri = new PanelRightItem(opbm, macroMaster, commandMaster, statusBar, pr.getJPanel(), pr);
							pri.setIdentity(PanelRightItem._TYPE_BUTTON);
							setAutoUpdate(child, pri, macroMaster);
							setName(child, pri, macroMaster);
							setSizeAndPosition(child, pr, pri, macroMaster, 150, 30, left, top, 5);
							setFont(child, pri, macroMaster, textFont);
							setColors(child, pri, macroMaster);
							pri.setText(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "text")));
							pri.setField(Xml.getAttributeOrChild(child, "field"));
							pri.setTooltip(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "tooltip")));
							setCommand(child.getChildNode("command"), pri, macroMaster);
							top += pri.getHeight() + 5;
						}


///////////
// CHECKBOX
					} else if (child.getName().equalsIgnoreCase("checkbox")) {
						if (zoomField.isEmpty() || (!zoomField.isEmpty() && zoom.contains(zoomField))) {
							// Okay to use
							pri = new PanelRightItem(opbm, macroMaster, commandMaster, statusBar, pr.getJPanel(), pr);
							pri.setIdentity(PanelRightItem._TYPE_CHECKBOX);
							setAutoUpdate(child, pri, macroMaster);
							setName(child, pri, macroMaster);
							setSizeAndPosition(child, pr, pri, macroMaster, refWidth - left - 5, 25, left, top, 5);
							setFont(child, pri, macroMaster, textFont);
							setColors(child, pri, macroMaster);
							setDefault(child, pri, macroMaster);
							pri.setText(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "text")));
							pri.setField(Xml.getAttributeOrChild(child, "field"));
							pri.setTooltip(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "tooltip")));
							setCommand(child.getChildNode("command"), pri, macroMaster);
							top += pri.getHeight() + 5;
						}


///////////
// TEXTBOX
					} else if (child.getName().equalsIgnoreCase("textbox")) {
						if (zoomField.isEmpty() || (!zoomField.isEmpty() && zoom.contains(zoomField))) {
							// Okay to use
							pri = new PanelRightItem(opbm, macroMaster, commandMaster, statusBar, pr.getJPanel(), pr);
							pri.setIdentity(PanelRightItem._TYPE_TEXTBOX);
							setAutoUpdate(child, pri, macroMaster);
							setName(child, pri, macroMaster);
							setSizeAndPosition(child, pr, pri, macroMaster, refWidth - left - 5, 25, left, top, 5);
							setFont(child, pri, macroMaster, textFont);
							setColors(child, pri, macroMaster);
							setDefault(child, pri, macroMaster);
							pri.setText(Xml.getAttributeOrChild(child, "text"));
							pri.setField(Xml.getAttributeOrChild(child, "field"));
							pri.setTooltip(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "tooltip")));
							setCommand(child.getChildNode("command"), pri, macroMaster);
							top += pri.getHeight() + 5;
						}


///////////
// EDITBOX
					} else if (child.getName().equalsIgnoreCase("editbox")) {
						if (zoomField.isEmpty() || (!zoomField.isEmpty() && zoom.contains(zoomField)))
						{
							// Okay to use
							pri = new PanelRightItem(opbm, macroMaster, commandMaster, statusBar, pr.getJPanel(), pr);
							pri.setIdentity(PanelRightItem._TYPE_EDITBOX);
							setAutoUpdate(child, pri, macroMaster);
							setName(child, pri, macroMaster);
							setSizeAndPosition(child, pr, pri, macroMaster, refWidth - left - 5, 100, left, top, 5);
							setFont(child, pri, macroMaster, textFont);
							setColors(child, pri, macroMaster);
							setDefault(child, pri, macroMaster);
							pri.setText(Xml.getAttributeOrChild(child, "text"));
							pri.setField(Xml.getAttributeOrChild(child, "field"));
							pri.setTooltip(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "tooltip")));
							setCommand(child.getChildNode("command"), pri, macroMaster);
							top += pri.getHeight() + 5;
						}


//////////
// OPTIONS
					} else if (child.getName().equalsIgnoreCase("options")) {
						// Options doesn't actually do anything immediately.
						// But, as items are selected in a listbox or lookupbox, all options are
						// instructed to examine themselves to see if they need to be updated and
						// redrawn.  If so, then they are / do.
						if (zoomField.isEmpty() || (!zoomField.isEmpty() && zoom.contains(zoomField)))
						{
							// Okay to use these options on this edit
							pri = new PanelRightItem(opbm, macroMaster, commandMaster, statusBar, pr.getJPanel(), pr);
							pri.setIdentity(PanelRightItem._TYPE_OPTIONS);
							pri.setOptionsRelativeTo(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "relativeto")));
							pri.setPosition(left, top);
						}

//////////
// VSPACER
					} else if (child.getName().equalsIgnoreCase("vspacer")) {
						if (zoomField.isEmpty() || (!zoomField.isEmpty() && zoom.contains(zoomField))) {
							// Okay to use
							sheight = macroMaster.parseMacros(Xml.getAttributeOrChild(child, "height"));
							if (sheight.isEmpty())
								top += 20;
							else
								top += Integer.valueOf(sheight);
						}


//////////
// HSPACER
					} else if (child.getName().equalsIgnoreCase("hspacer")) {
						if (zoomField.isEmpty() || (!zoomField.isEmpty() && zoom.contains(zoomField))) {
							// Okay to use
							swidth	= macroMaster.parseMacros(Xml.getAttributeOrChild(child, "width"));
							if (swidth.isEmpty())
								left += 20;
							else
								left += Integer.valueOf(swidth);
						}


					}
					// Add to the list of items on this pr
					if (pri != null)
					{
						pr.addItem(pri);
						// Reset the recently added item
						pri = null;
					}

					// Move to next child
					child = child.getNext();
				}

				// When we get here, the control is created (not yet displayed or populated, the caller must do that)
				return(pr);
			}
		}
		// If we get here, the rawedit wasn't found in edits.xml
		return(null);
	}

	/**
	 * Adds context options specified in the CustomInputsArrays fieldNames and
	 * fieldPrompts,
	 * @param pr PanelRight item we're adding to
	 * @param parent Parent panel to add context options
	 * @param opbm
	 * @param macroMaster
	 * @param commandMaster
	 * @param priMaster ArrayList of PanelRightItem entries to update for each added item
	 * @param xmlMaster ArrayList of Xml entries for each added item (null if does not already exist)
	 * @param fieldNames List of fields being input
	 * @param fieldPrompts List of prompts for each input item
	 * @param xml source of xml data to add any populated fieldNames items
	 * @param startX starting X coordinate for added items
	 * @param startY starting Y coordinate for added items
	 */
	public static void createRightPanelContextOptionsFromCustomInputsArrays(PanelRight				pr,
																			JPanel					parent,
																			Opbm					opbm,
																			Macros					macroMaster,
																			Commands				commandMaster,
																			List<PanelRightItem>	priMaster,
																			List<Xml>				xmlMaster,
																			List<String>			fieldNames,
																			List<String>			fieldPrompts,
																			Xml						xml,
																			int						startX,
																			int						startY)
	{
		int i, left, top, refWidth, refHeight;
		PanelRightItem pri;
		Font textFont;

		if (opbm.isFontOverride())
			textFont	= new Font("Arial", Font.PLAIN, 16);
		else
			textFont	= new Font("Calibri", Font.PLAIN, 18);

		left		= startX;
		top			= startY;
		refWidth	= pr.getWidth();
		refHeight	= pr.getHeight();
		for (i = 0; i < fieldNames.size(); i++)
		{
///////////
// LABEL
			// Add the prompt
			pri = new PanelRightItem(opbm, macroMaster, commandMaster, null, parent, pr);
			pri.setIdentity(PanelRightItem._TYPE_LABEL);
			setSizeAndPosition(null, pr, pri, macroMaster, refWidth - left - 5, 25, left, top, 5);
			pri.setText(macroMaster.parseMacros(fieldPrompts.get(i)));
			pri.setField(macroMaster.parseMacros(fieldNames.get(i)));
			pri.setFont(textFont);
			pr.addItem(pri);
			pri.navigateTo();
			top += pri.getHeight();
			priMaster.add(pri);
			xmlMaster.add(null);


///////////
// TEXTBOX
			// Add the input field
			pri = new PanelRightItem(opbm, macroMaster, commandMaster, null, parent, pr);
			pri.setIdentity(PanelRightItem._TYPE_TEXTBOX);
			setSizeAndPosition(null, pr, pri, macroMaster, refWidth - left - 5, 25, left, top, 5);
			pri.setField(macroMaster.parseMacros(fieldNames.get(i)));
			pri.setFont(textFont);
			pri.setTooltip(macroMaster.parseMacros("Please input the: " + fieldNames.get(i)));
			pr.addItem(pri);
			pri.loadData(xml);
			priMaster.add(pri);
			pri.navigateTo();
			top += pri.getHeight();
			priMaster.add(pri);
			xmlMaster.add(xml.getChildNode(fieldNames.get(i)));
		}
	}

	public static PanelRight createRightPanelFromXmlCustomInputsArray(List<Xml>		customInputs,
																	  Opbm			opbm,
																	  Macros		macroMaster,
																	  Commands		commandMaster,
																	  JPanel		refPanel,
																	  JFrame		frame)
	{
		int i, left, top, refWidth, refHeight;
		String sheight;
		Xml child;
		PanelRight		pr;
		PanelRightItem	pri;
		Font textFont;

		if (opbm.isFontOverride())
			textFont	= new Font("Arial", Font.PLAIN, 16);
		else
			textFont	= new Font("Calibri", Font.PLAIN, 18);

		pr = new PanelRight(frame, macroMaster, commandMaster, null, null, null, opbm, null);
		pr.setBackColor(refPanel.getBackground());
		pr.setForeColor(refPanel.getForeground());
		pr.setWidth(refPanel.getWidth());
		pr.setHeight(refPanel.getHeight());
		pr.setX(refPanel.getX());
		pr.setY(refPanel.getY());

		pri		= null;
		left	= 5;
		top		= 5;
		refWidth	= refPanel.getWidth();
		refHeight	= refPanel.getHeight();

		// Iterate through every edit child, parsing each one as we go
		for (i = 0; i < customInputs.size(); i++)
		{
			child = customInputs.get(i);

///////////
// LABEL
			if (child.getName().equalsIgnoreCase("label")) {
				pri = new PanelRightItem(opbm, macroMaster, commandMaster, null, pr.getJPanel(), pr);
				pri.setIdentity(PanelRightItem._TYPE_LABEL);
				setAutoUpdate(child, pri, macroMaster);
				setName(child, pri, macroMaster);
				setSizeAndPosition(child, pr, pri, macroMaster, refWidth - left - 5, 25, left, top, 5);
				setFont(child, pri, macroMaster, textFont);
				if (!setColors(child, pri, macroMaster))
					pri.setForeColor(pr.getForeColor());
				pri.setText(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "text")));
				pri.setField(Xml.getAttributeOrChild(child, "field"));
				pri.setTooltip(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "tooltip")));
				setCommand(child.getChildNode("command"), pri, macroMaster);
				top += pri.getHeight();


///////////
// TEXTBOX
			} else if (child.getName().equalsIgnoreCase("textbox")) {
				pri = new PanelRightItem(opbm, macroMaster, commandMaster, null, pr.getJPanel(), pr);
				pri.setIdentity(PanelRightItem._TYPE_TEXTBOX);
				setAutoUpdate(child, pri, macroMaster);
				setName(child, pri, macroMaster);
				setSizeAndPosition(child, pr, pri, macroMaster, refWidth - left - 5, 25, left, top, 5);
				setFont(child, pri, macroMaster, textFont);
				setColors(child, pri, macroMaster);
				setDefault(child, pri, macroMaster);
				pri.setText(Xml.getAttributeOrChild(child, "text"));
				pri.setField(Xml.getAttributeOrChild(child, "field"));
				pri.setTooltip(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "tooltip")));
				setCommand(child.getChildNode("command"), pri, macroMaster);
				top += pri.getHeight() + 5;


///////////
// EDITBOX
			} else if (child.getName().equalsIgnoreCase("editbox")) {
				pri = new PanelRightItem(opbm, macroMaster, commandMaster, null, pr.getJPanel(), pr);
				pri.setIdentity(PanelRightItem._TYPE_EDITBOX);
				setAutoUpdate(child, pri, macroMaster);
				setName(child, pri, macroMaster);
				setSizeAndPosition(child, pr, pri, macroMaster, refWidth - left - 5, 100, left, top, 5);
				setFont(child, pri, macroMaster, textFont);
				setColors(child, pri, macroMaster);
				setDefault(child, pri, macroMaster);
				pri.setText(Xml.getAttributeOrChild(child, "text"));
				pri.setField(Xml.getAttributeOrChild(child, "field"));
				pri.setTooltip(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "tooltip")));
				setCommand(child.getChildNode("command"), pri, macroMaster);
				top += pri.getHeight() + 5;


///////////
// BUTTON
			} else if (child.getName().equalsIgnoreCase("button")) {
				pri = new PanelRightItem(opbm, macroMaster, commandMaster, null, pr.getJPanel(), pr);
				pri.setIdentity(PanelRightItem._TYPE_BUTTON);
				setAutoUpdate(child, pri, macroMaster);
				setName(child, pri, macroMaster);
				setSizeAndPosition(child, pr, pri, macroMaster, 150, 30, left, top, 5);
				setFont(child, pri, macroMaster, textFont);
				setColors(child, pri, macroMaster);
				pri.setText(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "text")));
				pri.setField(Xml.getAttributeOrChild(child, "field"));
				pri.setTooltip(macroMaster.parseMacros(Xml.getAttributeOrChild(child, "tooltip")));
				setCommand(child.getChildNode("command"), pri, macroMaster);
				top += pri.getHeight() + 5;


//////////
// VSPACER
			} else if (child.getName().equalsIgnoreCase("vspacer")) {
				sheight = macroMaster.parseMacros(Xml.getAttributeOrChild(child, "height"));
				if (sheight.isEmpty())
					top += 20;
				else
					top += Integer.valueOf(sheight);

			}

			if (pri != null) {
				pr.addItem(pri);
				pri = null;
			}
		}
		return(pr);
	}

	/**
	 * Builds the edit entries necessary to create an input of anything
	 * containing custom input fields, as indicated by the source of the edit,
	 * such as fields that are surrounded with {: and :} combinations.  They
	 * take the syntax:  {:field_name, Prompt to use on-screen for input:}
	 *
	 * @param opbm
	 * @param customInputs <code>ArrayList</code> to populate
	 * @param name edit name
	 * @param xmlData <code>Xml</code> record holding the data to search (the
	 * entry the user had highlighted when they clicked the add button)
	 * @param macroMaster master <code>Macros</code> class for this context
	 * @param saveObjectList unique identifier to the saved object <code>List</code>
	 */
	public static void buildCustomInputsFromEdit(Opbm		opbm,
												 List<Xml>	customInputs,
												 String		name,
												 Xml		xmlData,
												 Macros		macroMaster,
												 String		saveObjectList)
	{
		int i;
		Xml edit, child, label, textbox, button, command;
		String field, value;
		List<Xml>		editsXml	= new ArrayList<Xml>(0);
		List<String>	fieldNames	= new ArrayList<String>(0);
		List<String>	prompts		= new ArrayList<String>(0);

		// Begin by loading every left pr entry in panels
		Xml.getNodeList(editsXml, opbm.getEditXml(), "opbm.edits.edit", false);
		if (editsXml.isEmpty()) {	// No name entries were found
			return;
		}

		// Iterate through every pr, obtaining its directives within
		for (i = 0; i < editsXml.size(); i++)
		{
			// Create a new PanelRightobject for this item
			edit = editsXml.get(i);
			if (edit.getAttribute("name").equalsIgnoreCase(name))
			{
				child = edit.getFirstChild();
				while (child != null)
				{
///////////
// TEXTBOX
// EDITBOX
					if (child.getName().equalsIgnoreCase("textbox") || child.getName().equalsIgnoreCase("editbox"))
					{
						field = macroMaster.parseMacros(Xml.getAttributeOrChild(child, "field"));
						if (!field.isEmpty())
						{
							// There's an input here, parse its content to see if it has any custom fields
							value = Xml.getAttributeOrChildExplicit(xmlData, field, "", "", true);
							if (!value.isEmpty())
							{
								value = macroMaster.parseMacros(value);
								fieldNames.clear();
								prompts.clear();
								Utils.extractCustomInputFields(fieldNames, prompts, value);
								if (!fieldNames.isEmpty()) {
									// We will create edit entries for this/these one(s)
									for (i = 0; i < fieldNames.size(); i++)
									{
										// Label
										label = new Xml("label");
										label.setFirstAttribute(new Xml("text", prompts.get(i)));
										customInputs.add(label);

										// Textbox
										textbox = new Xml("textbox");
										textbox.setFirstAttribute(new Xml("field", fieldNames.get(i)));
										textbox.addAttribute(new Xml("indent", "1"));
										customInputs.add(textbox);
									}
								}
							}
						}
					}

					// Move to next child
					child = child.getNext();
				}
				// Finished
				if (!customInputs.isEmpty())
				{
					// Add the save and cancel buttons after a vertical spacer
					customInputs.add(new Xml("vspacer"));

					button = new Xml("button");
					button.setFirstAttribute(new Xml("text", "Save"));
					command = new Xml("command", "save_custom");
					command.setFirstAttribute(new Xml("p1", saveObjectList));
					button.addChild(command);
					customInputs.add(button);

					button = new Xml("button");
					button.setFirstAttribute(new Xml("text", "Cancel"));
					command = new Xml("command", "cancel_custom");
					command.setFirstAttribute(new Xml("p1", saveObjectList));
					button.addChild(command);
					customInputs.add(button);
				}
				break;
			}
		}
	}

	public static void setName(Xml				child,
							   PanelRightItem	pri,
							   Macros			macroMaster)
	{
		pri.setName(macroMaster.parseMacros(Xml.getAttribute(child, "name")));
	}

	public static void setAutoUpdate(Xml				child,
									 PanelRightItem		pri,
									 Macros				macroMaster)
	{
		String s;

		s = macroMaster.parseMacros(Xml.getAttribute(child, "autoupdate"));
		pri.setAutoUpdate(Utils.interpretBooleanAsYesNo(s, true).equalsIgnoreCase("yes"));
	}

	/**
	 * Sets listbox attributes.
	 *
	 * @param root xml to load the attributes/tags from
	 * @param pri <code>PanelRightItem</code> being updated
	 * @param macroMaster <code>Macros</code> class to parse any macros
	 * @return true or false if the minimum syntax was found (listby tag)
	 */
	public static boolean setListboxAttributes(Xml				root,
											   PanelRightItem	pri,
											   Macros			macroMaster)
	{
//		<buttons>+ - clone</buttons>
//		<filename>$scripts.xml$</filename>
//		<dblclick p1="atom">command</dblclick>
//		<enter p1="atom">command</enter>
//		<source>opbm.scriptdata.flows</source>
//		<foreach>flow</foreach>
//		<listby p1="engine" p2="#name"/>
		String command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10;
		Xml listBy, dblClick, enter;

		listBy = Xml.getChildNode(root, "listby");
		if (listBy == null) {
			// Syntax error
			return(false);
		}
		// We're good
		p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p1"));
		p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p2"));
		p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p3"));
		p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p4"));
		p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p5"));
		p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p6"));
		p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p7"));
		p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p8"));
		p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p9"));
		p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p10"));
		pri.setListBy(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

		dblClick = Xml.getChildNode(root, "dblclick");
		if (dblClick != null)
		{
			command	= macroMaster.parseMacros(dblClick.getText());
			p1		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p1"));
			p2		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p2"));
			p3		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p3"));
			p4		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p4"));
			p5		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p5"));
			p6		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p6"));
			p7		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p7"));
			p8		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p8"));
			p9		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p9"));
			p10		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p10"));
			pri.setDblClick(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		enter = Xml.getChildNode(root, "enter");
		if (enter != null)
		{
			command	= macroMaster.parseMacros(enter.getText());
			p1		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p1"));
			p2		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p2"));
			p3		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p3"));
			p4		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p4"));
			p5		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p5"));
			p6		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p6"));
			p7		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p7"));
			p8		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p8"));
			p9		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p9"));
			p10		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p10"));
			pri.setEnter(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		setListboxButtons(root, pri, macroMaster);
		setListboxOrLookupboxEvents(root, pri, macroMaster);

		pri.setSource(macroMaster.parseMacros(Xml.getAttributeOrChild(root, "source")), "");
		pri.setFileName(macroMaster.parseMacros(Xml.getAttributeOrChild(root, "filename")));
		pri.setLocation(macroMaster.parseMacros(Xml.getAttributeOrChild(root, "location")));
		pri.setForEach(macroMaster.parseMacros(Xml.getAttributeOrChild(root, "foreach")));
		return(true);
	}

	/** Sets fieldNames to execute on the add, delete and clone buttons.
	 *
	 * @param root xml to grab fieldNames
	 * @param pri <code>PanelRightItem</code> to update
	 * @param macroMaster <code>Macros</code> class to parse any macros
	 */
	public static void setListboxButtons(Xml			root,
										 PanelRightItem	pri,
										 Macros			macroMaster)
	{
		String pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10;
		Xml x;

		// Add
		x = Xml.getChildNode(root, "add");
		if (x != null) {
			pc	= macroMaster.parseMacros(x.getText());
			p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p1"));
			p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p2"));
			p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p3"));
			p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p4"));
			p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p5"));
			p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p6"));
			p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p7"));
			p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p8"));
			p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p9"));
			p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p10"));
			pri.setAddButton(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		// Delete
		x = Xml.getChildNode(root, "delete");
		if (x != null) {
			pc	= macroMaster.parseMacros(x.getText());
			p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p1"));
			p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p2"));
			p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p3"));
			p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p4"));
			p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p5"));
			p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p6"));
			p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p7"));
			p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p8"));
			p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p9"));
			p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p10"));
			pri.setDeleteButton(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		// Clone
		x = Xml.getChildNode(root, "clone");
		if (x != null) {
			pc	= macroMaster.parseMacros(x.getText());
			p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p1"));
			p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p2"));
			p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p3"));
			p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p4"));
			p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p5"));
			p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p6"));
			p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p7"));
			p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p8"));
			p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p9"));
			p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p10"));
			pri.setCloneButton(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		// Up
		x = Xml.getChildNode(root, "up");
		if (x != null) {
			pc	= macroMaster.parseMacros(x.getText());
			p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p1"));
			p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p2"));
			p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p3"));
			p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p4"));
			p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p5"));
			p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p6"));
			p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p7"));
			p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p8"));
			p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p9"));
			p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p10"));
			pri.setUpButton(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		// Down
		x = Xml.getChildNode(root, "down");
		if (x != null) {
			pc	= macroMaster.parseMacros(x.getText());
			p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p1"));
			p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p2"));
			p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p3"));
			p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p4"));
			p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p5"));
			p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p6"));
			p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p7"));
			p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p8"));
			p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p9"));
			p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p10"));
			pri.setDownButton(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}
	}

	/**
	 * Sets lookupbox attributes.
	 *
	 * @param root xml to load the attributes/tags from
	 * @param pri <code>PanelRightItem</code> being updated
	 * @param macroMaster <code>Macros</code> class to parse any macros
	 * @return true or false if the minimum syntax was found (listby tag)
	 */
	public static boolean setLookupboxAttributes(Xml				root,
												 PanelRightItem		pri,
												 Macros				macroMaster)
	{
		String command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10;
		Xml listBy, dblClick, enter;

		listBy = Xml.getChildNode(root, "listby");
		if (listBy == null) {
			// Syntax error
			return(false);
		}
		if (!setLookupboxSource(root, pri, macroMaster)) {
			// Syntax error
			return(false);
		}

		// We're good
		p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p1"));
		p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p2"));
		p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p3"));
		p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p4"));
		p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p5"));
		p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p6"));
		p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p7"));
		p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p8"));
		p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p9"));
		p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(listBy, "p10"));
		pri.setListBy(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

		dblClick = Xml.getChildNode(root, "dblclick");
		if (dblClick != null)
		{
			command	= macroMaster.parseMacros(dblClick.getText());
			p1		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p1"));
			p2		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p2"));
			p3		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p3"));
			p4		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p4"));
			p5		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p5"));
			p6		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p6"));
			p7		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p7"));
			p8		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p8"));
			p9		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p9"));
			p10		= macroMaster.parseMacros(Xml.getAttributeOrChild(dblClick, "p10"));
			pri.setDblClick(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		enter = Xml.getChildNode(root, "enter");
		if (enter != null)
		{
			command	= macroMaster.parseMacros(enter.getText());
			p1		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p1"));
			p2		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p2"));
			p3		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p3"));
			p4		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p4"));
			p5		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p5"));
			p6		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p6"));
			p7		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p7"));
			p8		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p8"));
			p9		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p9"));
			p10		= macroMaster.parseMacros(Xml.getAttributeOrChild(enter, "p10"));
			pri.setEnter(command, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		setLookupboxButtons(root, pri, macroMaster);
		setListboxOrLookupboxEvents(root, pri, macroMaster);

		pri.setFileName(macroMaster.parseMacros(Xml.getAttributeOrChild(root, "filename")));
		pri.setLocation(macroMaster.parseMacros(Xml.getAttributeOrChild(root, "location")));
		pri.setForEach(macroMaster.parseMacros(Xml.getAttributeOrChild(root, "foreach")));
		return(true);
	}

	public static void setListboxOrLookupboxEvents(Xml				root,
												   PanelRightItem	pri,
												   Macros			macroMaster)
	{
		String pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10;
		Xml source;

		source = Xml.getChildNode(root, "onselect");
		if (source != null)
		{
			pc	= macroMaster.parseMacros(source.getText());
			p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(source, "p1"));
			p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(source, "p2"));
			p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(source, "p3"));
			p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(source, "p4"));
			p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(source, "p5"));
			p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(source, "p6"));
			p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(source, "p7"));
			p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(source, "p8"));
			p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(source, "p9"));
			p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(source, "p10"));
			pri.setOnSelect(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}
	}

	public static boolean setLookupboxSource(Xml				root,
											 PanelRightItem		pri,
											 Macros				macroMaster)
	{
		Xml source;

		source = Xml.getChildNode(root, "source");
		if (source == null) {
			return(false);
		}
		pri.setSource(macroMaster.parseMacros(source.getText()), macroMaster.parseMacros(Xml.getAttributeOrChild(source, "relativeto")));
		return(true);
	}

	/**
	 * Sets fieldNames to execute on the add, subtract and zoom buttons.
	 *
	 * @param root xml to grab fieldNames
	 * @param pri <code>PanelRightItem</code> to update
	 * @param macroMaster <code>Macros</code> class to parse any macros
	 */
	public static void setLookupboxButtons(Xml				root,
										   PanelRightItem	pri,
										   Macros			macroMaster)
	{
		String pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10;
		boolean autoPreview;
		Xml x;

		// Add
		x = Xml.getChildNode(root, "add");
		if (x != null)
		{
			pc	= macroMaster.parseMacros(x.getText());
			p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p1"));
			p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p2"));
			p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p3"));
			p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p4"));
			p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p5"));
			p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p6"));
			p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p7"));
			p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p8"));
			p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p9"));
			p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p10"));
			pri.setAddButton(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		// Subtract
		x = Xml.getChildNode(root, "subtract");
		if (x != null)
		{
			pc	= macroMaster.parseMacros(x.getText());
			p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p1"));
			p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p2"));
			p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p3"));
			p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p4"));
			p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p5"));
			p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p6"));
			p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p7"));
			p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p8"));
			p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p9"));
			p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p10"));
			pri.setSubtractButton(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		// Zoom
		x = Xml.getChildNode(root, "zoom");
		if (x != null)
		{
			pc			= macroMaster.parseMacros(x.getText());
			autoPreview	= Utils.interpretBooleanAsYesNo(macroMaster.parseMacros(Xml.getAttributeOrChild(x, "autopreview")), false).equalsIgnoreCase("yes");
			p1			= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p1"));
			p2			= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p2"));
			p3			= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p3"));
			p4			= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p4"));
			p5			= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p5"));
			p6			= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p6"));
			p7			= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p7"));
			p8			= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p8"));
			p9			= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p9"));
			p10			= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p10"));
			pri.setZoomButton(pc, autoPreview, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		// Up
		x = Xml.getChildNode(root, "up");
		if (x != null)
		{
			pc	= macroMaster.parseMacros(x.getText());
			p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p1"));
			p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p2"));
			p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p3"));
			p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p4"));
			p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p5"));
			p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p6"));
			p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p7"));
			p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p8"));
			p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p9"));
			p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p10"));
			pri.setUpButton(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		// Down
		x = Xml.getChildNode(root, "down");
		if (x != null)
		{
			pc	= macroMaster.parseMacros(x.getText());
			p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p1"));
			p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p2"));
			p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p3"));
			p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p4"));
			p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p5"));
			p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p6"));
			p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p7"));
			p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p8"));
			p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p9"));
			p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p10"));
			pri.setDownButton(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}

		// RelativeEdits
		x = Xml.getChildNode(root, "relativeedits");
		if (x != null)
		{
			pri.setRelativeEdits(macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p1")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p2")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p3")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p4")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p5")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p6")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p7")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p8")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p9")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p10")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p11")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p12")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p13")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p14")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p15")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p16")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p17")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p18")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p19")),
								 macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p20"))  );
		}

		// RelativeOptions
		x = Xml.getChildNode(root, "relativeoptions");
		if (x != null)
		{
			pri.setRelativeOptions(macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p1")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p2")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p3")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p4")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p5")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p6")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p7")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p8")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p9")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p10")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p11")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p12")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p13")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p14")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p15")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p16")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p17")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p18")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p19")),
								   macroMaster.parseMacros(Xml.getAttributeOrChild(x, "p20"))  );
		}
	}

	/** Sets the default value for an input field (if specified).
	 *
	 * @param root xml to grab fieldNames
	 * @param pri <code>PanelRightItem</code> to update
	 * @param macroMaster <code>Macros</code> class to parse any macros
	 */
	public static void setDefault(Xml				root,
								  PanelRightItem	pri,
								  Macros			macroMaster)
	{
		pri.setDefault(macroMaster.parseMacros(Xml.getAttributeOrChild(root, "default")));
	}

	/** Sets optional attributes for size and position.
	 *
	 * @param root optional <code>Xml</code> to grab fieldNames
	 * @param pr <code>PanelRight</code> parent
	 * @param pri <code>PanelRightItem</code> to update
	 * @param macroMaster <code>Macros</code> class to parse any macros
	 * @param width width to use if parameter not found
	 * @param height height to use if parameter not found
	 * @param x x to use if parameter not found
	 * @param y y to use if parameter not found
	 * @param border width from edge for max height
	 */
	public static void setSizeAndPosition(Xml				root,
										  PanelRight		pr,
										  PanelRightItem	pri,
										  Macros			macroMaster,
										  int				width,
										  int				height,
										  int				x,
										  int				y,
										  int				border)
	{
		String swidth, sheight, sx, sy, sindents;
		int indent;
		boolean maxHeight = false;

		// Grab any attributes or tags which may indicate a specified size or position
		if (root != null)
		{
			swidth	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "width"));
			sheight	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "height"));
			sx		= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "x"));
			sy		= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "y"));

		} else {
			swidth	= "";
			sheight	= "";
			sx		= "";
			sy		= "";

		}

		if (sheight.equalsIgnoreCase("max"))
			maxHeight = true;

		// See if any were specified
		if (!swidth.isEmpty())
			width = Integer.valueOf(swidth);
		if (!maxHeight && !sheight.isEmpty())
			height = Integer.valueOf(sheight);
		if (!sx.isEmpty())
			x = Integer.valueOf(swidth);
		if (!sy.isEmpty())
			y = Integer.valueOf(sheight);

		if (maxHeight)
			height = pr.getHeight() - y - border;

		// See if there are any indents indicated
		if (root != null)
		{
			sindents = macroMaster.parseMacros(Xml.getAttributeOrChild(root, "indent"));
			if (!sindents.isEmpty()) {
				indent	= Integer.valueOf(sindents) * 20;
				x		+= indent;
				width	-= indent;
				if (width < 50)
					width = 50;
			}
		}

		// Set the values
		pri.setSize(width, height);
		pri.setPosition(x, y);
	}

	/** Sets the optional font
	 *
	 * @param root xml to grab fieldNames
	 * @param pri <code>PanelRightItem</code> to update
	 * @param macroMaster <code>Macros</code> class to parse any macros
	 * @param font font to use if parameter not found
	 */
	public static void setFont(Xml				root,
							   PanelRightItem	pri,
							   Macros			macroMaster,
							   Font				font)
	{
		String fontName, fontStyle, fontSize;
		int fStyle, fSize;
		Font f;

		// Grab any possible attributes
		fontName	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "fontName"));
		fontStyle	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "fontStyle"));
		fontSize	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "fontSize"));

		// If present, process them
		if (!fontName.isEmpty() || !fontStyle.isEmpty() || !fontSize.isEmpty()) {
			if (fontName.isEmpty())
				fontName = "Calibri";

			// Style
			if (fontStyle.isEmpty()) {
				fStyle = font.getStyle();

			} else {
				if (!fontStyle.toLowerCase().contains("plain")) {
					fStyle = 0;

					if (fontStyle.toLowerCase().contains("bold"))
						fStyle += Font.BOLD;

					if (fontStyle.toLowerCase().contains("italic"))
						fStyle += Font.ITALIC;

				} else {
					fStyle = Font.PLAIN;

				}
			}

			// Size
			if (fontSize.isEmpty())
				fSize = font.getSize();
			else
				fSize = Integer.valueOf(fontSize);

			// Create the font
			font = new Font(fontName, fStyle, fSize);

		}

		// Set the font
		pri.setFont(font);
	}

	/** Static function to set the colors on multiple items based on known
	 * color attributes or tags relative to the root Xml object.
	 *
	 * @param root <code>Xml</code> object containing color information at or
	 * below this level
	 * @param pri <code>PanelRightItem</code> to update
	 */
	public static boolean setColors(Xml root, PanelRightItem pri, Macros macroMaster) {
		String bgColor, fgColor;
		boolean changed = false;

		fgColor	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "fgcolor"));
		if (!fgColor.isEmpty()) {
			pri.setBackColor(Color.decode(Utils.verifyColorFormat(fgColor)));
			changed = true;
		}

		bgColor	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "bgcolor"));
		if (!bgColor.isEmpty()) {
			pri.setBackColor(Color.decode(Utils.verifyColorFormat(bgColor)));
			changed = true;
		}
		return(changed);
	}

	/**
	 * Static function to set the command for multiple items based on the known
	 * command and parameter syntax.
	 *
	 * @param root
	 * @param pri <code>PanelRightItem</code> to update
	 * @param macroMaster to parse any macros in the command
	 */
	public static void setCommand(Xml root, PanelRightItem pri, Macros macroMaster) {
		String pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10;

		if (root != null) {
			pc	= macroMaster.parseMacros(root.getText());
			p1	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "p1"));
			p2	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "p2"));
			p3	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "p3"));
			p4	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "p4"));
			p5	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "p5"));
			p6	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "p6"));
			p7	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "p7"));
			p8	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "p8"));
			p9	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "p9"));
			p10	= macroMaster.parseMacros(Xml.getAttributeOrChild(root, "p10"));
			pri.setCommand(pc, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
		}
	}
}
