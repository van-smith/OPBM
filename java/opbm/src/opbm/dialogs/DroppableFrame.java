/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is the top-level class for all JFrame objects used in the system.
 * It implements drag-and-drop abilities on every window, which isn't always
 * used, but allows for immediate, easy expansion.  It also implements
 * transparency in a simplified way.
 *
 * Last Updated:  Sep 12, 2011
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

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.util.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.*;
import opbm.Opbm;
import opbm.panels.right.PanelRightLookupbox;

public class DroppableFrame extends JFrame
							implements	DropTargetListener,
										DragSourceListener,
										DragGestureListener,
										ComponentListener
{
	/** Constructor.  Initializes drag operation, hooks dropTarget, etc.
	 *
	 * @param opbm Parent object referenced for global method calls
	 * @param isZoomWindow must be set true if it's a zoom window (used for
	 * determining what to do when closing the window)
	 */
	public DroppableFrame(Opbm		opbm,
						  boolean	isZoomWindow,
						  boolean	isResizeable)
	{
		super("JFrame");
		m_dragSource	= DragSource.getDefaultDragSource();
		m_dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
		this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this));
		addComponentListener(this);

		m_opbm			= opbm;
		m_isZoomWindow	= isZoomWindow;
		m_isAppWindow	= !isZoomWindow;
		m_isResizeable	= isResizeable;
		m_lookupbox		= null;
		setResizable(isResizeable);
	}

	/**
	 * Uses the jawt.dll function JAWT_GetAWT() to obtain information about the
	 * canvas drawing info, which includes the host OS's HWND, among other
	 * things.
	 */
	public int getHWND()
	{
		return(Opbm.getComponentHWND(this));
	}

	/**
	 * Tells Windows how big the window can be, minimum and maximum, on
	 * resizing, and makes the output very smooth.
	 * @param minWidth
	 * @param minHeight
	 * @param maxWidth
	 * @param maxHeight
	 */
	public void setMinMaxResizeBoundaries(int	minWidth,
										  int	minHeight,
										  int	maxWidth,
										  int	maxHeight,
										  int	desktopWidth,
										  int	desktopHeight)
	{
		// Calls Win32 functions to intercept the WM_GETMINMAXINFO message, whereby it tells Windows how big the window can be
		Opbm.setMinMaxResizeBoundaries(getHWND(), minWidth, minHeight, maxWidth, maxHeight, desktopWidth, desktopHeight);
	}

	/**
	 * Tells Windows to keep this window as the TOPMOST window always, even
	 * when it loses focus.
	 */
	public void setPersistAlwaysOnTop()
	{
		Opbm.setPersistAlwaysOnTop(Opbm.getComponentHWND(this));
	}

	/** Updates the status bar with the specified label (used to indicate drop operation)
	 *
	 * @param statusBar label object to update
	 */
	public void setStatusBar(Label statusBar) {
		m_statusBar = statusBar;
	}

	@Override
	public void paint(Graphics g)
	{
		Dimension d = getSize();
		Dimension m = getMaximumSize();

		boolean resize = d.width > m.width || d.height > m.height;
		d.width = Math.min(m.width, d.width);
		d.height = Math.min(m.height, d.height);
		if (resize)
		{
			Point p = getLocation();
			setVisible(false);
			setSize(d);
			setLocation(p);
			setVisible(true);
		}
		super.paint(g);
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
	public void setTranslucency(float opaquePercent)
	{
		try
		{
			if (System.getProperty("java.version").substring(0,3).compareTo("1.6") <= 0)
			{	// Code for translucency works in 1.6, raises exception in 1.7
				Class awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
				Method mSetWindowOpacity;
				mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
				if (mSetWindowOpacity != null)
					mSetWindowOpacity.invoke(null, this, opaquePercent);

			} else {
				// Try alternate method per http://download.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html#uniform
				// Note:  Their sample code on the web page fails if the frame is decorated (has a border, minimize, restore, close buttons, a menu, etc.)
				// Note:  Their sample code is in error because the image they show for the translucency shows the border, as does their pixelated translucent window.
				setOpacity(opaquePercent);		// This suggested method from Oracle's sample code, causes IllegalComponentStateException.
				// A game developer found a workaround:
				// The following lines must be run in succession:
				//		dispose();
				//		setUndecorated(true);
				//		setTranslucency(0.5f);
				// Refer to HUD.java for an implementation of this
			}

		} catch (NoSuchMethodException ex) {
		} catch (SecurityException ex) {
		} catch (ClassNotFoundException ex) {
		} catch (IllegalAccessException ex) {
		} catch (IllegalArgumentException ex) {
		} catch (InvocationTargetException ex) {
		} catch (IllegalComponentStateException ex) {
		} catch (Throwable t) {
		} finally {
		}
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		Dimension d = getSize();
		Dimension m = getMaximumSize();

		boolean resize = d.width > m.width || d.height > m.height;
		d.width = Math.min(m.width, d.width);
		d.height = Math.min(m.height, d.height);
		if (resize)
		{	// There is no good way in Java to keep a window from flashing wildly when resized beyond its intended maximum size
			// I believe there is a win32 function to handle this though,
//			Point p = getLocation();
//			setVisible(false);
			setSize(d);
//			setLocation(p);
//			setVisible(true);
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
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
	protected boolean				m_isResizeable;
	protected PanelRightLookupbox	m_lookupbox;
}
