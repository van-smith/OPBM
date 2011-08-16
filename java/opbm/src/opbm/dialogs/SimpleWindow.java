package opbm.dialogs;

import opbm.Opbm;
import opbm.common.DroppableFrame;

/**
 *
 * @author rick
 */
public class SimpleWindow extends DroppableFrame
{
	public SimpleWindow(Opbm		opbm,
						boolean	isZoomWindow)
	{
		// Call DroppableFrame constructor
		super(opbm, isZoomWindow);

		m_opbm		= opbm;
	}
}
