package opbm.dialogs;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import opbm.Opbm;
import opbm.graphics.JLabelHotTrack;
import opbm.graphics.AlphaImage;

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
		m_width		= 473;	// width of "simple_background.png"
		m_height	= 313;
		setTitle("OPBM");

		// Compute the actual size we need for our window, so it's properly centered
		pack();
		Insets fi		= getInsets();
		m_actual_width	= m_width  + fi.left + fi.right;
		m_actual_height	= m_height + fi.top  + fi.bottom;
		setSize(m_width  + fi.left + fi.right,
				m_height + fi.top  + fi.bottom);

		m_prefSize = new Dimension(m_width  + fi.left + fi.right,
								 m_height + fi.top  + fi.bottom);
		setMinimumSize(m_prefSize);
		setPreferredSize(m_prefSize);

		m_prefSize = new Dimension(m_width  + fi.left + fi.right,
								 m_height + fi.top  + fi.bottom);
		setMinimumSize(m_prefSize);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(m_width, m_height);
		setLocationRelativeTo(null);	// Center window
		setLayout(null);				// We handle all redraws
		addKeyListener(opbm);
		addMouseWheelListener(opbm);
		addComponentListener(opbm);

		Container c = getContentPane();
		c.setBackground(new Color(120, 120, 120));
		c.setForeground(Color.WHITE);

		m_pan = new JLayeredPane();
		m_pan.setLayout(null);
		m_pan.setBounds(0, 0, m_width, m_height);
		m_pan.setVisible(true);
		m_pan.setBorder(BorderFactory.createEmptyBorder());
		m_pan.addKeyListener(opbm);
		c.add(m_pan);

		// Set the background image
		m_lblBackground = new JLabel();
		m_lblBackground.setBounds(0, 0, m_width, m_height);
		m_lblBackground.setHorizontalAlignment(JLabel.LEFT);
		m_lblBackground.setVerticalAlignment(JLabel.TOP);
		m_imgBackground = new AlphaImage(Opbm.locateFile("simple_background.png"));		// Stopwatch on dark slate (colorized 35, 30, -20)
		m_pan.add(m_lblBackground);
		m_pan.moveToFront(m_lblBackground);

		extractQuit();
		extractTrialRun();
		extractOfficialRun();
		extractViewPreviousResults();
		extractDeveloperInterface();

		m_lblBackground.setIcon(new ImageIcon(m_imgBackground.getBufferedImage()));
		m_lblBackground.setVisible(true);
		setVisible(false);
	}

	private void extractQuit()
	{
		JLabel neutral, over;

		// Grab our source images
		m_imgQuitNeutral	= m_imgBackground.extractImage(4, 55, 65, 84);
		m_imgQuitOver		= m_imgBackground.extractImage(4, 55, 65, 84);
		m_imgQuitMask1		= new AlphaImage(Opbm.locateFile("quit_mask1.png"));
		m_imgQuitMask2		= new AlphaImage(Opbm.locateFile("quit_mask2.png"));

		// Apply the overlay mask and coloring for the neutral image
		m_imgQuitNeutral.applyAlphaMask(m_imgQuitMask1);
		m_imgQuitNeutral.scaleContrast(0.35);
		m_imgQuitNeutral.scaleBrightness(0.55);
		m_imgQuitNeutral.recolorize(AlphaImage.makeARGB(255, 192, 0, 0));
		m_imgQuitNeutral.overlayImageExcludeColor(m_imgQuitMask2, AlphaImage.makeARGB(0, 0, 0, 0));

		// Apply the overlay mask and coloring for the over image (when the mouse is over it)
		m_imgQuitOver.applyAlphaMask(m_imgQuitMask1);
		m_imgQuitOver.scaleContrast(0.95);
		m_imgQuitOver.scaleBrightness(0.95);
		m_imgQuitOver.recolorize(AlphaImage.makeARGB(255, 215, 0, 0));
		m_imgQuitOver.overlayImageExcludeColor(m_imgQuitMask2, AlphaImage.makeARGB(0, 0, 0, 0));

		// Create the hot track label
		m_lblQuit = new JLabelHotTrack(this);
		m_lblQuit.setBounds(4, 55, 61, 29);
		m_lblQuit.setIdentifier("sw_quit");
		m_lblQuit.setType(JLabelHotTrack.CLICK_ACTION);

		// Create the neutral label, and add it to the hot track
		neutral = new JLabel();
		neutral.setBounds(4, 55, 61, 29);
		neutral.setIcon(new ImageIcon(m_imgQuitNeutral.getBufferedImage()));
		neutral.setVisible(false);
		m_pan.add(neutral);
		m_pan.moveToFront(neutral);
		m_lblQuit.setNeutral(neutral);

		// Create the over label (when the mouse is over it), and add it to the hot track
		over = new JLabel();
		over.setBounds(4, 55, 61, 29);
		over.setIcon(new ImageIcon(m_imgQuitOver.getBufferedImage()));
		over.setVisible(false);
		m_pan.add(over);
		m_pan.moveToFront(over);
		m_lblQuit.setOver(over);

		// Display the quit button
		m_lblQuit.setNeutral();
		m_lblQuit.show(neutral);
	}

	private void extractTrialRun()
	{
		JLabel neutral, over;

		// Grab our source images
		m_imgTrialNeutral	= m_imgBackground.extractImage(133, 89, 348, 124);
		m_imgTrialOver		= m_imgBackground.extractImage(133, 89, 348, 124);
		m_imgTrialMask1		= new AlphaImage(Opbm.locateFile("trial_run_mask1.png"));
		m_imgTrialMask2		= new AlphaImage(Opbm.locateFile("trial_run_mask2.png"));

		// Apply the overlay mask and coloring for the neutral image
		m_imgTrialNeutral.applyAlphaMask(m_imgTrialMask1);
		m_imgTrialNeutral.scaleBrightness(0.55);
		m_imgTrialNeutral.recolorize(AlphaImage.makeARGB(255, 32, 92, 215));
		m_imgTrialNeutral.overlayImageExcludeColor(m_imgTrialMask2, AlphaImage.makeARGB(0, 0, 0, 0));

		// Apply the overlay mask and coloring for the over image (when the mouse is over it)
		m_imgTrialOver.applyAlphaMask(m_imgTrialMask1);
		m_imgTrialOver.scaleBrightness(0.95);
		m_imgTrialOver.recolorize(AlphaImage.makeARGB(255, 32, 92, 215));
		m_imgTrialOver.overlayImageExcludeColor(m_imgTrialMask2, AlphaImage.makeARGB(0, 0, 0, 0));

		// Create the hot track label
		m_lblTrial = new JLabelHotTrack(this);
		m_lblTrial.setBounds(133, 89, 216, 36);
		m_lblTrial.setIdentifier("sw_trial");
		m_lblTrial.setType(JLabelHotTrack.CLICK_ACTION);

		// Create the neutral label, and add it to the hot track
		neutral = new JLabel();
		neutral.setBounds(133, 89, 216, 36);
		neutral.setIcon(new ImageIcon(m_imgTrialNeutral.getBufferedImage()));
		neutral.setVisible(false);
		m_pan.add(neutral);
		m_pan.moveToFront(neutral);
		m_lblTrial.setNeutral(neutral);

		// Create the over label (when the mouse is over it), and add it to the hot track
		over = new JLabel();
		over.setBounds(133, 89, 216, 36);
		over.setIcon(new ImageIcon(m_imgTrialOver.getBufferedImage()));
		over.setVisible(false);
		m_pan.add(over);
		m_pan.moveToFront(over);
		m_lblTrial.setOver(over);

		// Display the quit button
		m_lblTrial.setNeutral();
		m_lblTrial.show(neutral);
	}

	private void extractOfficialRun()
	{
		JLabel neutral, over;

		// Grab our source images
		m_imgOfficialNeutral	= m_imgBackground.extractImage(133, 128, 348, 163);
		m_imgOfficialOver		= m_imgBackground.extractImage(133, 128, 348, 163);
		m_imgOfficialMask1		= new AlphaImage(Opbm.locateFile("official_run_mask1.png"));
		m_imgOfficialMask2		= new AlphaImage(Opbm.locateFile("official_run_mask2.png"));

		// Apply the overlay mask and coloring for the neutral image
		m_imgOfficialNeutral.applyAlphaMask(m_imgOfficialMask1);
		m_imgOfficialNeutral.scaleBrightness(0.55);
		m_imgOfficialNeutral.recolorize(AlphaImage.makeARGB(255, 32, 164, 32));
		m_imgOfficialNeutral.overlayImageExcludeColor(m_imgOfficialMask2, AlphaImage.makeARGB(0, 0, 0, 0));

		// Apply the overlay mask and coloring for the over image (when the mouse is over it)
		m_imgOfficialOver.applyAlphaMask(m_imgOfficialMask1);
		m_imgOfficialOver.scaleBrightness(0.95);
		m_imgOfficialOver.recolorize(AlphaImage.makeARGB(255, 32, 164, 32));
		m_imgOfficialOver.overlayImageExcludeColor(m_imgOfficialMask2, AlphaImage.makeARGB(0, 0, 0, 0));

		// Create the hot track label
		m_lblOfficial = new JLabelHotTrack(this);
		m_lblOfficial.setBounds(133, 128, 216, 36);
		m_lblOfficial.setIdentifier("sw_official");
		m_lblOfficial.setType(JLabelHotTrack.CLICK_ACTION);

		// Create the neutral label, and add it to the hot track
		neutral = new JLabel();
		neutral.setBounds(133, 128, 216, 36);
		neutral.setIcon(new ImageIcon(m_imgOfficialNeutral.getBufferedImage()));
		neutral.setVisible(false);
		m_pan.add(neutral);
		m_pan.moveToFront(neutral);
		m_lblOfficial.setNeutral(neutral);

		// Create the over label (when the mouse is over it), and add it to the hot track
		over = new JLabel();
		over.setBounds(133, 128, 216, 36);
		over.setIcon(new ImageIcon(m_imgOfficialOver.getBufferedImage()));
		over.setVisible(false);
		m_pan.add(over);
		m_pan.moveToFront(over);
		m_lblOfficial.setOver(over);

		// Display the quit button
		m_lblOfficial.setNeutral();
		m_lblOfficial.show(neutral);
	}

	private void extractViewPreviousResults()
	{
		JLabel neutral, over;

		// Grab our source images
		m_imgPreviousNeutral	= m_imgBackground.extractImage(119, 257, 359, 282);
		m_imgPreviousOver		= m_imgBackground.extractImage(119, 257, 359, 282);
		m_imgPreviousMask1		= new AlphaImage(Opbm.locateFile("view_previous_results_mask1.png"));
		m_imgPreviousMask2		= new AlphaImage(Opbm.locateFile("view_previous_results_mask2.png"));

		// Apply the overlay mask and coloring for the neutral image
		m_imgPreviousNeutral.applyAlphaMask(m_imgPreviousMask1);
		m_imgPreviousNeutral.scaleBrightness(0.55);
		m_imgPreviousNeutral.recolorize(AlphaImage.makeARGB(255, 32, 92, 215));
		m_imgPreviousNeutral.overlayImageExcludeColor(m_imgPreviousMask2, AlphaImage.makeARGB(0, 0, 0, 0));

		// Apply the overlay mask and coloring for the over image (when the mouse is over it)
		m_imgPreviousOver.applyAlphaMask(m_imgPreviousMask1);
		m_imgPreviousOver.scaleBrightness(0.95);
		m_imgPreviousOver.recolorize(AlphaImage.makeARGB(255, 32, 92, 215));
		m_imgPreviousOver.overlayImageExcludeColor(m_imgPreviousMask2, AlphaImage.makeARGB(0, 0, 0, 0));

		// Create the hot track label
		m_lblPrevious = new JLabelHotTrack(this);
		m_lblPrevious.setBounds(119, 257, 240, 25);
		m_lblPrevious.setIdentifier("sw_previous");
		m_lblPrevious.setType(JLabelHotTrack.CLICK_ACTION);

		// Create the neutral label, and add it to the hot track
		neutral = new JLabel();
		neutral.setBounds(119, 257, 240, 25);
		neutral.setIcon(new ImageIcon(m_imgPreviousNeutral.getBufferedImage()));
		neutral.setVisible(false);
		m_pan.add(neutral);
		m_pan.moveToFront(neutral);
		m_lblPrevious.setNeutral(neutral);

		// Create the over label (when the mouse is over it), and add it to the hot track
		over = new JLabel();
		over.setBounds(119, 257, 240, 25);
		over.setIcon(new ImageIcon(m_imgPreviousOver.getBufferedImage()));
		over.setVisible(false);
		m_pan.add(over);
		m_pan.moveToFront(over);
		m_lblPrevious.setOver(over);

		// Display the quit button
		m_lblPrevious.setNeutral();
		m_lblPrevious.show(neutral);
	}

	private void extractDeveloperInterface()
	{
		JLabel neutral, over;

		// Grab our source images
		m_imgDeveloperNeutral	= m_imgBackground.extractImage(119, 285, 359, 310);
		m_imgDeveloperOver		= m_imgBackground.extractImage(119, 285, 359, 310);
		m_imgDeveloperMask1		= new AlphaImage(Opbm.locateFile("developer_interface_mask1.png"));
		m_imgDeveloperMask2		= new AlphaImage(Opbm.locateFile("developer_interface_mask2.png"));

		// Apply the overlay mask and coloring for the neutral image
		m_imgDeveloperNeutral.applyAlphaMask(m_imgDeveloperMask1);
		m_imgDeveloperNeutral.scaleBrightness(0.55);
		m_imgDeveloperNeutral.recolorize(AlphaImage.makeARGB(255, 32, 164, 32));
		m_imgDeveloperNeutral.overlayImageExcludeColor(m_imgDeveloperMask2, AlphaImage.makeARGB(0, 0, 0, 0));

		// Apply the overlay mask and coloring for the over image (when the mouse is over it)
		m_imgDeveloperOver.applyAlphaMask(m_imgDeveloperMask1);
		m_imgDeveloperOver.scaleBrightness(0.95);
		m_imgDeveloperOver.recolorize(AlphaImage.makeARGB(255, 32, 164, 32));
		m_imgDeveloperOver.overlayImageExcludeColor(m_imgDeveloperMask2, AlphaImage.makeARGB(0, 0, 0, 0));

		// Create the hot track label
		m_lblDeveloper = new JLabelHotTrack(this);
		m_lblDeveloper.setBounds(119, 285, 240, 25);
		m_lblDeveloper.setIdentifier("sw_developer");
		m_lblDeveloper.setType(JLabelHotTrack.CLICK_ACTION);

		// Create the neutral label, and add it to the hot track
		neutral = new JLabel();
		neutral.setBounds(119, 285, 240, 25);
		neutral.setIcon(new ImageIcon(m_imgDeveloperNeutral.getBufferedImage()));
		neutral.setVisible(false);
		m_pan.add(neutral);
		m_pan.moveToFront(neutral);
		m_lblDeveloper.setNeutral(neutral);

		// Create the over label (when the mouse is over it), and add it to the hot track
		over = new JLabel();
		over.setBounds(119, 285, 240, 25);
		over.setIcon(new ImageIcon(m_imgDeveloperOver.getBufferedImage()));
		over.setVisible(false);
		m_pan.add(over);
		m_pan.moveToFront(over);
		m_lblDeveloper.setOver(over);

		// Display the quit button
		m_lblDeveloper.setNeutral();
		m_lblDeveloper.show(neutral);
	}

	public void clickActionCallback(JLabelHotTrack jlht)
	{
		if (jlht.getIdentifier().equalsIgnoreCase("sw_quit"))
		{	// Quit is clicked
			System.exit(0);

		} else if (jlht.getIdentifier().equalsIgnoreCase("sw_trial")) {
			// Trial Run is clicked
			m_opbm.getBenchmarkMaster().benchmarkTrialRun(m_opbm);

		} else if (jlht.getIdentifier().equalsIgnoreCase("sw_official")) {
			// Official Run is clicked
			m_opbm.getBenchmarkMaster().benchmarkOfficialRun(m_opbm);

		} else if (jlht.getIdentifier().equalsIgnoreCase("sw_previous")) {
			// View Previous Results is clicked
			m_opbm.getCommandMaster().processCommand(this, "prompt_run_results_viewer", null, null, null, null, null, null, null, null, null, null);

		} else if (jlht.getIdentifier().equalsIgnoreCase("sw_developer")) {
			// Developer Interface is clicked, switch to developer mode
			m_opbm.getCommandMaster().processCommand(this, "developer", null, null, null, null, null, null, null, null, null, null);

		}
	}

	private JLayeredPane				m_pan;
	private JLabel						m_lblBackground;	// The background image
	private AlphaImage					m_imgBackground;

	private AlphaImage					m_imgQuitNeutral;
	private AlphaImage					m_imgQuitOver;
	private AlphaImage					m_imgQuitMask1;
	private AlphaImage					m_imgQuitMask2;
	private JLabelHotTrack				m_lblQuit;

	private AlphaImage					m_imgTrialNeutral;
	private AlphaImage					m_imgTrialOver;
	private AlphaImage					m_imgTrialMask1;
	private AlphaImage					m_imgTrialMask2;
	private JLabelHotTrack				m_lblTrial;

	private AlphaImage					m_imgOfficialNeutral;
	private AlphaImage					m_imgOfficialOver;
	private AlphaImage					m_imgOfficialMask1;
	private AlphaImage					m_imgOfficialMask2;
	private JLabelHotTrack				m_lblOfficial;

	private AlphaImage					m_imgPreviousNeutral;
	private AlphaImage					m_imgPreviousOver;
	private AlphaImage					m_imgPreviousMask1;
	private AlphaImage					m_imgPreviousMask2;
	private JLabelHotTrack				m_lblPrevious;

	private AlphaImage					m_imgDeveloperNeutral;
	private AlphaImage					m_imgDeveloperOver;
	private AlphaImage					m_imgDeveloperMask1;
	private AlphaImage					m_imgDeveloperMask2;
	private JLabelHotTrack				m_lblDeveloper;

	private Dimension					m_prefSize;
	private int							m_width;
	private int							m_height;
	private int							m_actual_width;
	private int							m_actual_height;
}
