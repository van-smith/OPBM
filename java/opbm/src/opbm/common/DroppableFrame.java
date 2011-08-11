/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for all JFrame objects used in the system.
 * It implements drag-and-drop abilities on every window, which isn't always
 * used, but allows for immediate, easy expansion.  It also implements
 * transparency in a simplified way.
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

package opbm.common;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.InputEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.*;
import opbm.Opbm;
import opbm.panels.PanelRightLookupbox;

public class DroppableFrame extends JFrame
							implements	DropTargetListener,
										DragSourceListener,
										DragGestureListener
{
	/** Constructor.  Initializes drag operation, hooks dropTarget, etc.
	 *
	 * @param opbm Parent object referenced for global method calls
	 * @param isZoomWindow must be set true if it's a zoom window (used for
	 * determining what to do when closing the window)
	 */
	public DroppableFrame(Opbm		opbm,
						  boolean	isZoomWindow)
	{
		m_dragSource	= DragSource.getDefaultDragSource();
		m_dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
		this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this));

		m_opbm			= opbm;
		m_isZoomWindow	= isZoomWindow;
		m_isAppWindow	= !isZoomWindow;
		m_lookupbox		= null;
	}

	/** Updates the status bar with the specified label (used to indicate drop operation)
	 *
	 * @param statusBar label object to update
	 */
	public void setStatusBar(Label statusBar) {
		m_statusBar = statusBar;
	}

	/**
	 * Specifies the lookupbox that should be notified that the window was closed
	 * @param lookupbox <code>PanelRightLookupbox</code> object to call when disposing the window
	 */
	public void setCloseNotification(PanelRightLookupbox lookupbox)
	{
		m_lookupbox	= lookupbox;
	}

	/** Not used.  Required for override of DragSourceListener.
	 *
	 * @param DragSourceDropEvent system drag source drop event
	 */
	@Override
	public void dragDropEnd(DragSourceDropEvent DragSourceDropEvent){}

	/** Not used.  Required for override of DragSourceListener.
	 *
	 * @param DragSourceDragEvent system drag source drop event
	 */
	@Override
	public void dragEnter(DragSourceDragEvent DragSourceDragEvent){}

	/** Not used.  Required for override of DragSourceListener.
	 *
	 * @param DragSourceEvent system drag source drop event
	 */
	@Override
	public void dragExit(DragSourceEvent DragSourceEvent){}

	/** Not used.  Required for override of DragSourceListener.
	 *
	 * @param DragSourceDragEvent system drag source drop event
	 */
	@Override
	public void dragOver(DragSourceDragEvent DragSourceDragEvent){}

	/** Not used.  Required for override of DragSourceListener.
	 *
	 * @param DragSourceDragEvent system drag source drop event
	 */
	@Override
	public void dropActionChanged(DragSourceDragEvent DragSourceDragEvent){}

	/** Accepts the drag so the cursor changes visually.
	 *
	 * @param dropTargetDragEvent system drag source drop event used to call
	 * <code>acceptDrag()</code> method
	 */
	@Override
	public void dragEnter (DropTargetDragEvent dropTargetDragEvent)
	{
		dropTargetDragEvent.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
	}

	/** Not used.  Required for override of DropTargetListener.
	 *
	 * @param dropTargetEvent system event
	 */
	@Override
	public void dragExit (DropTargetEvent dropTargetEvent) {}

	/** Not used.  Required for override of DropTargetListener.
	 *
	 * @param dropTargetDragEvent system event
	 */
	@Override
	public void dragOver (DropTargetDragEvent dropTargetDragEvent) {}

	/** Not used.  Required for override of DropTargetListener.
	 *
	 * @param dropTargetDragEvent system event
	 */
	@Override
	public void dropActionChanged (DropTargetDragEvent dropTargetDragEvent){}

	/** Parses the specified files to see if any of them are ones we accept.
	 *
	 * @param dropTargetDropEvent system event
	 */
	@Override
	public synchronized void drop(DropTargetDropEvent dropTargetDropEvent)
	{
		try
		{
			Transferable tr = dropTargetDropEvent.getTransferable();
			if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				dropTargetDropEvent.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
				java.util.List fileList = (java.util.List)tr.getTransferData(DataFlavor.javaFileListFlavor);
				Iterator iterator = fileList.iterator();
				while (iterator.hasNext())
				{
					File file = (File)iterator.next();
					if (file.getName().toLowerCase().endsWith(".xml")) {
						if (file.getName().toLowerCase().contains("results"))
						{	// It's a results.xml file, launch the results viewer
							m_opbm.createAndShowResultsViewer(file.getAbsolutePath());
						}
					}
					else {
						m_statusBar.setText("Ignored dropped file: " + file.getAbsolutePath());
					}
				}
				dropTargetDropEvent.getDropTargetContext().dropComplete(true);

			} else {
				dropTargetDropEvent.rejectDrop();

			}

		} catch (IOException io) {
			dropTargetDropEvent.rejectDrop();

		} catch (UnsupportedFlavorException ufe) {
			dropTargetDropEvent.rejectDrop();

		}
	}

	/** Not used.  Required for override of DragGestureListener.
	 *
	 * @param dragGestureEvent system event
	 */
	@Override
	public void dragGestureRecognized(DragGestureEvent dragGestureEvent)
	{
	}

	/**
	 * Allows the frame to become completely transparent to fully opaque.
	 * @param opaquePercent of opaqueness
	 */
	public void setTransparency(float opaquePercent)
	{
		try {
			Class awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
			Method mSetWindowOpacity;
			mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
			if (mSetWindowOpacity != null)
				mSetWindowOpacity.invoke(null, this, Float.valueOf(opaquePercent));

		} catch (NoSuchMethodException ex) {
		} catch (SecurityException ex) {
		} catch (ClassNotFoundException ex) {
		} catch (IllegalAccessException ex) {
		} catch (IllegalArgumentException ex) {
		} catch (InvocationTargetException ex) {
		}
	}

	@Override
	public void dispose()
	{
		if (m_lookupbox != null)
			m_lookupbox.notifyOnClose();

		if (m_isZoomWindow) { // Just closing this one zoom window
			m_isZoomWindow = false;
			m_opbm.removeZoomWindow(this);

		} else if (m_isAppWindow) { // Closing down the whole app
			m_isAppWindow = false;
			m_opbm.closeAllZoomWindows();
		}

		super.dispose();
	}

	public void forceWindowToHaveFocus()
	{
		Rectangle bounds;
		Insets insets;
		Robot robot = null;

		// Bring the window visible and to the forefront
		setVisible(true);
		toFront();

		// Get the window's coordinates
		bounds = getBounds();
		insets = getInsets();

		// Send a mouse click to the window's title bar
		try {
			robot = new Robot();
			robot.mouseMove(bounds.x + bounds.width / 2, bounds.y + insets.top / 2);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);

		} catch (AWTException ex) {
		}
	}

	protected DragSource			m_dragSource;
	protected Label					m_statusBar;
	protected Opbm					m_opbm;
	protected boolean				m_isAppWindow;
	protected boolean				m_isZoomWindow;
	protected PanelRightLookupbox	m_lookupbox;
}