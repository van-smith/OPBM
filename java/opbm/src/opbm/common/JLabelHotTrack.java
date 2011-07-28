/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package opbm.common;

import opbm.common.Tupel;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import opbm.resultsviewer.ResultsViewer;

/**
 *
 * @author Rick
 */
public class JLabelHotTrack implements MouseListener
{
	public JLabelHotTrack()
	{
		super();
		m_rv					= null;
		m_selected				= false;
		m_mouseOver				= false;
		m_tupelToUpdate			= null;
		m_tupelIndex			= 0;
		m_rect					= new Rectangle();
		m_lblSelectedNeutral	= null;
		m_lblUnselectedNeutral	= null;
		m_lblSelectedOver		= null;
		m_lblUnselectedOver		= null;
		m_type					= TOGGLE_STATE;
		m_identifier			= "";
	}

	public JLabelHotTrack(ResultsViewer rv)
	{
		super();
		m_rv					= rv;
		m_selected				= false;
		m_mouseOver				= false;
		m_tupelToUpdate			= null;
		m_tupelIndex			= 0;
		m_rect					= new Rectangle();
		m_lblSelectedNeutral	= null;
		m_lblUnselectedNeutral	= null;
		m_lblSelectedOver		= null;
		m_lblUnselectedOver		= null;
		m_type					= TOGGLE_STATE;
		m_identifier			= "";
	}

	public void setIdentifier(String identifier)
	{
		m_identifier = identifier;
	}

	public String getIdentifier()
	{
		return(m_identifier);
	}

	public void setSelectedNeutral(JLabel label)
	{
		m_lblSelectedNeutral = label;
		m_lblSelectedNeutral.addMouseListener(this);
	}

	public void setSelectedOver(JLabel label)
	{
		m_lblSelectedOver = label;
		m_lblSelectedOver.addMouseListener(this);
	}

	public void setUnselectedNeutral(JLabel label)
	{
		m_lblUnselectedNeutral = label;
		m_lblUnselectedNeutral.addMouseListener(this);
	}

	public void setUnselectedOver(JLabel label)
	{
		m_lblUnselectedOver = label;
		m_lblUnselectedOver.addMouseListener(this);
	}

	public void setType(int type)
	{
		switch (type)
		{
			case TOGGLE_STATE:
				m_type = type;
				break;

			case CLICK_ACTION:
				m_type = type;
				break;
		}
	}

	public int getType()
	{
		return(m_type);
	}

	public JLabel getSelectedNeutral()
	{
		return(m_lblSelectedNeutral);
	}

	public JLabel getSelectedOver()
	{
		return(m_lblSelectedOver);
	}

	public JLabel getUnselectedNeutral()
	{
		return(m_lblUnselectedNeutral);
	}

	public JLabel getUnselectedOver()
	{
		return(m_lblUnselectedOver);
	}

	public void setSelected(boolean selected)
	{
		m_selected = selected;
		updateTupelIfSet();
	}

	public void updateTupelIfSet()
	{
		if (m_tupelToUpdate	!= null)
			m_tupelToUpdate.setSecond(m_tupelIndex, m_selected ? "Yes" : "No");
	}

	public void setTupelToUpdateByMouseActivity(Tupel		tupel,
												int			index)
	{
		m_tupelToUpdate		= tupel;
		m_tupelIndex		= index;
	}

	public void setBounds(int	startX,
						  int	startY,
						  int	endX,
						  int	endY)
	{
		m_rect.setBounds(startX, startY, endX, endY);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e)
	{
		m_selected = !m_selected;
		updateTupelIfSet();
		renderHotTrackChange();
		switch (m_type)
		{
			case TOGGLE_STATE:
				if (m_rv != null)
				{
					m_rv.recomputeScores();
					m_rv.renderScoreboard();
					m_rv.renderBottomAndGraph();
				}
				break;

			case CLICK_ACTION:
				if (m_rv != null)
				{
					m_rv.clickActionCallback(this);
				}
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		m_mouseOver = true;
		renderHotTrackChange();
	}

	public void mouseExited(MouseEvent e) {
		m_mouseOver = false;
		renderHotTrackChange();
	}

	public void renderHotTrackChange()
	{
		if (m_selected && m_lblSelectedNeutral != null && m_lblSelectedOver != null)
		{	// Selected
			if (m_mouseOver)
			{	// With the mouse over it
				hide(m_lblSelectedNeutral);
				show(m_lblSelectedOver);
				hide(m_lblUnselectedNeutral);
				hide(m_lblUnselectedOver);

			} else {
				// Without the mouse over it
				show(m_lblSelectedNeutral);
				hide(m_lblSelectedOver);
				hide(m_lblUnselectedNeutral);
				hide(m_lblUnselectedOver);

			}

		} else {
			// Not selected
			if (m_mouseOver)
			{	// With the mouse over it
				hide(m_lblSelectedNeutral);
				hide(m_lblSelectedOver);
				hide(m_lblUnselectedNeutral);
				show(m_lblUnselectedOver);

			} else {
				// Without the mouse over it
				hide(m_lblSelectedNeutral);
				hide(m_lblSelectedOver);
				show(m_lblUnselectedNeutral);
				hide(m_lblUnselectedOver);

			}

		}
	}

	public void hide(JLabel label)
	{
		if (label != null)
			label.setVisible(false);
	}

	public void show(JLabel label)
	{
		if (label != null)
		{
			label.setBounds(m_rect);
			label.setVisible(true);
		}
	}

	public static final int		TOGGLE_STATE = 0;
	public static final int		CLICK_ACTION = 1;

	// Labels to display when the mouse is over the item, or away from it
	public ResultsViewer	m_rv;
	public JLabel			m_lblSelectedNeutral;
	public JLabel			m_lblSelectedOver;
	public JLabel			m_lblUnselectedNeutral;
	public JLabel			m_lblUnselectedOver;
	public Rectangle		m_rect;
	public boolean			m_selected;
	public boolean			m_mouseOver;
	public int				m_type;
	public String			m_identifier;

	public Tupel			m_tupelToUpdate;
	public int				m_tupelIndex;
}
