/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is used to display contents within the listbox or lookupbox
 * classes.  Presently no special graphics or other abilities are defined,
 * but only a markup of the current item, and (today) only in the form of
 * concatenating up to 10 separate input fields within the reference XML
 * file for displaying data, such as "field1.field2.field3" being defined
 * within the XML file by references to the parent, such as:
 *
 * <parent field1="something">
 *     <options>
 *         <
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

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import opbm.common.Macros;
import opbm.common.Xml;

public class PanelRightListboxRenderer
		implements ListCellRenderer
{
	public PanelRightListboxRenderer(String listBy1,
									 String	listBy2,
									 String	listBy3,
									 String	listBy4,
									 String	listBy5,
									 String	listBy6,
									 String	listBy7,
									 String	listBy8,
									 String	listBy9,
									 String	listBy10)
	{
		m_listBy1			= listBy1;
		m_listBy2			= listBy2;
		m_listBy3			= listBy3;
		m_listBy4			= listBy4;
		m_listBy5			= listBy5;
		m_listBy6			= listBy6;
		m_listBy7			= listBy7;
		m_listBy8			= listBy8;
		m_listBy9			= listBy9;
		m_listBy10			= listBy10;
		m_defaultRenderer	= new DefaultListCellRenderer();
	}

	@Override
	public Component getListCellRendererComponent(JList		list,
												  Object	value,
												  int		index,
												  boolean	isSelected,
												  boolean	cellHasFocus)
	{
		String visual;
		Xml x;

		JLabel renderer = (JLabel)m_defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		x = (Xml)value;

		// The following fields derive the user-defined listing structure
		// specified in edits.xml's <listbox><listby> parameters, as in:
		// <listby p1="field1" p2="field2" p3="field3"/> (up to p10), any
		// parameter prefixed by a "#" pound sign refer to attributes, rather
		// than relative child tag's values.
		visual =  Macros.decodeCommonMacrosNoDollarSign(Xml.getAttributeOrChildExplicit(x, m_listBy1, " ", " ?", true));
		visual += Macros.decodeCommonMacrosNoDollarSign(Xml.getAttributeOrChildExplicit(x, m_listBy2, ".", ".?", true));
		visual += Macros.decodeCommonMacrosNoDollarSign(Xml.getAttributeOrChildExplicit(x, m_listBy3, ".", ".?", true));
		visual += Macros.decodeCommonMacrosNoDollarSign(Xml.getAttributeOrChildExplicit(x, m_listBy4, ".", ".?", true));
		visual += Macros.decodeCommonMacrosNoDollarSign(Xml.getAttributeOrChildExplicit(x, m_listBy5, ".", ".?", true));
		visual += Macros.decodeCommonMacrosNoDollarSign(Xml.getAttributeOrChildExplicit(x, m_listBy6, ".", ".?", true));
		visual += Macros.decodeCommonMacrosNoDollarSign(Xml.getAttributeOrChildExplicit(x, m_listBy7, ".", ".?", true));
		visual += Macros.decodeCommonMacrosNoDollarSign(Xml.getAttributeOrChildExplicit(x, m_listBy8, ".", ".?", true));
		visual += Macros.decodeCommonMacrosNoDollarSign(Xml.getAttributeOrChildExplicit(x, m_listBy9, ".", ".?", true));
		visual += Macros.decodeCommonMacrosNoDollarSign(Xml.getAttributeOrChildExplicit(x, m_listBy10, ".", ".?", true));
		renderer.setText(visual);
		return renderer;
	}

	protected	DefaultListCellRenderer		m_defaultRenderer;
	private		String						m_listBy1;
	private		String						m_listBy2;
	private		String						m_listBy3;
	private		String						m_listBy4;
	private		String						m_listBy5;
	private		String						m_listBy6;
	private		String						m_listBy7;
	private		String						m_listBy8;
	private		String						m_listBy9;
	private		String						m_listBy10;
}
