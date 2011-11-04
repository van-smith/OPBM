/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is used to display contents within the compilation listbox.
 * It prefixes each entry with "Atom ", "Molecule ", "Scenario " or "Suite ".
 *
 * Last Updated:  Nov 03, 2011
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

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import opbm.common.Utils;
import opbm.common.Xml;

public class PanelRightCompilationRenderer
								implements ListCellRenderer
{
	public PanelRightCompilationRenderer()
	{
		m_defaultRenderer	= new DefaultListCellRenderer();
	}

	@Override
	public Component getListCellRendererComponent(JList		list,
												  Object	value,
												  int		index,
												  boolean	isSelected,
												  boolean	cellHasFocus)
	{
		String visual, tag, name, iteration;
		Xml x;

		JLabel renderer = (JLabel)m_defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		x = (Xml)value;

		// Grab the components for this entry
		tag			= x.getName();
		name		= x.getAttribute("name");
		iteration	= x.getAttribute("iterations");

		if (Utils.getValueOf(iteration, 1, 1, Integer.MAX_VALUE) == 1)
		{	// A single iteration
			// Renders as:
			//		"atom:name"
			//		"molecule:name"
			//		"scenario:name"
			//		"suite:name"
			visual = tag + ":" + Utils.toProper(name).replace(" ", "");

		} else {
			// MOre than one iteration
			// Renders as:
			//		"atom(N):name"
			//		"molecule(N):name"
			//		"scenario(N):name"
			//		"suite(N):name"
			visual = tag + "(" + iteration + "):" + Utils.toProper(name).replace(" ", "");
		}
		renderer.setText(visual);
		return renderer;
	}

	protected	DefaultListCellRenderer		m_defaultRenderer;
}
