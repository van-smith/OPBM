/*
 * OPBM - Office Productivity Benchmark
 *
 * This class is a graphical image processor class.  It is based off the
 * BufferedImage base class, but extends its functionality to include a host
 * of functions to support easy alpha-masking, and the requirements of a
 * skinned GUI.
 *
 * Last Updated:  Aug 01, 2011
 *
 * by Van Smith
 * Cossatot Analytics Laboratories, LLC. (Cana Labs)
 *
 * (c) Copyright Cana Labs.
 * Free software licensed under the GNU GPL2.
 *
 * @version 1.0.2
 *
 */

package opbm.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import opbm.Opbm;

public final class AlphaImage
{
	public AlphaImage(int	width,
					  int	height)
	{
		m_img		= new BufferedImage(Math.max(width, 1), Math.max(height, 1), BufferedImage.TYPE_INT_ARGB);
		fill(makeARGB(255,0,0,0));
	}

	public AlphaImage(Image img)
	{
		m_img = (BufferedImage)img;
		fill(makeARGB(255,0,0,0));
	}

	public AlphaImage(AlphaImage img)
	{
		m_img = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		copy(img);
	}

	public AlphaImage(String filename)
	{
		load(filename);
	}

	public void load(String filename)
	{
		ImageIcon img;
		try {
			img		= new ImageIcon(ImageIO.read(new File(filename)));
			m_img	= (BufferedImage)img.getImage();

		} catch (IOException ex) {
		}
	}

	public void drawLine(int		startX,
						 int		startY,
						 int		endX,
						 int		endY,
						 int		argb)
	{
		double y, x, width, height, deltaX, deltaY, thisX, thisY;
		int i, points;

		if (startX == endX) {
			// It's a vertical line
			drawVerticalLine(startX, startY, endY, argb);

		} else if (startY == endY) {
			// It's a horizontal line
			drawHorizontalLine(startY, startX, endX, argb);

		} else {
			// If we get here, it's an arbitrary line
			width	= (double)(endX - startX);
			height	= (double)(endY - startY);
			points = (int)Math.sqrt((width*width) + (height*height));

			thisX	= (double)startX;
			thisY	= (double)startY;
			deltaX	= width  / (double)points;
			deltaY	= height / (double)points;
			for (i = 0; i < points; i++)
			{
				m_img.setRGB((int)thisX, (int)thisY, argb);
				thisX += deltaX;
				thisY += deltaY;
			}
		}
	}

	public void drawVerticalLine(int	x,
								 int	startY,
								 int	endY,
								 int	argb)
	{
		int y;

		for (y = startY; y <= endY; y++)
		{
			if (x >= 0 && x < m_img.getWidth() && y >= 0 && y < m_img.getHeight())
				m_img.setRGB(x, y, argb);
		}
	}

	public void drawHorizontalLine(int	y,
								   int	startX,
								   int	endX,
								   int	argb)
	{
		int x;

		for (x = startX; x <= endX; x++)
		{
			if (x >= 0 && x < m_img.getWidth() && y >= 0 && y < m_img.getHeight())
				m_img.setRGB(x, y, argb);
		}
	}

	public void drawHighlightedVerticalRectangle(int	startX,
												 int	startY,
												 int	endX,
												 int	endY,
												 int	rgbaBorder,
												 int	rgbaFill)
	{
		int x, y, midX, rgbaBorderLow, rgbaFillLow;

		// Compute the midpoint and low colors
		midX = (startX + endX) / 2;
		rgbaBorderLow	= darkenARGB(rgbaBorder, 40);
		rgbaFillLow		= rgbaFill;	//darkenARGB(rgbaFill, 30);

		// Draw the rectangle
		for (y = startY; y < endY; y++)
		{
			if (y >= 0 && y < getHeight())
			{
				for (x = startX; x < endX; x++)
				{
					if (x >= 0 && x < getWidth())
					{
						if (y == startY || x == startX) {
							// Border Highlight
							m_img.setRGB(x, y, rgbaBorder);

						} else if (y == endY - 1 || x == endX - 1) {
							// Border Lowlight
							m_img.setRGB(x, y, rgbaBorderLow);

						} else if (x < midX) {
							// Fill Highlight
							m_img.setRGB(x, y, rgbaFill);

						} else {
							// Fill Lowlight
							m_img.setRGB(x, y, rgbaFillLow);

						}
					}
				}
			}
		}
	}

	public void drawHighlightedHorizontalRectangle(int		startX,
												   int		startY,
												   int		endX,
												   int		endY,
												   int		rgbaBorder,
												   int		rgbaFill)
	{
		int x, y, midY, rgbaBorderLow, rgbaFillLow;

		// Compute the midpoint and low colors
		midY = (startY + endY) / 2;
		rgbaBorderLow	= darkenARGB(rgbaBorder, 40);
		rgbaFillLow		= darkenARGB(rgbaFill, 30);

		// Draw the rectangle
		for (y = startY; y < endY; y++)
		{
			if (y >= 0 && y < getHeight())
			{
				for (x = startX; x < endX; x++)
				{
					if (x >= 0 && x < getWidth())
					{
						if (y == startY || x == startX) {
							// Border Highlight
							m_img.setRGB(x, y, rgbaBorder);

						} else if (y == endY - 1 || x == endX - 1) {
							// Border Lowlight
							m_img.setRGB(x, y, rgbaBorderLow);

						} else if (y < midY) {
							// Fill Highlight
							m_img.setRGB(x, y, rgbaFill);

						} else {
							// Fill Lowlight
							m_img.setRGB(x, y, rgbaFillLow);

						}
					}
				}
			}
		}
	}

	/**
	 * Draws a rectangle with an impress-style, which is a highlighted line
	 * every third line, a style which has a missing line below the switchY
	 * value, and is highlighted above that.
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @param switchY
	 */
	public void rainbowRectangle(int	startX,
								 int	startY,
								 int	endX,
								 int	endY,
								 int	switchY)
	{
		double percent;
		int y, line;

		line = 0;
		for (y = startY; y < endY; )
		{
			percent = (double)(y - startY) / (double)(endY - startY);
			if (line < switchY)
			{
				drawHorizontalLine(y,   startX,   endX,   AlphaImage.getOfColorLine(percent, 192));
				if (y+1 < endY)
					drawHorizontalLine(y+1, startX+2, endX-2, AlphaImage.getOfColorLine(percent, 64));

			} else {
				drawHorizontalLine(y,   startX, endX, AlphaImage.getOfColorLine(percent, 255));
				if (y+1 < endY)
				{
					drawHorizontalLine(y+1, startX, endX, AlphaImage.getOfColorLine(percent, 235));
					if (y+2 < endY)
						drawHorizontalLine(y+2, startX, endX, AlphaImage.getOfColorLine(percent, 215));
				}

			}
			y += 3;
			line += 3;
		}
	}

	public int getHeight()		{	return(m_img.getHeight());		}
	public int getWidth()		{	return(m_img.getWidth());		}

	public int getARGB(int x, int y)
	{
		return(m_img.getRGB(x,y));
	}

	public void setARGB(int x, int y, int argb)
	{
		m_img.setRGB(x, y, argb);
	}

	public BufferedImage getBufferedImage()
	{
		return(m_img);
	}

	public static int makeARGB(int	alp,
							   int	red,
							   int	grn,
							   int	blu)
	{
		return((alp << 24) + (red << 16) + (grn << 8) + blu);
	}

	public static int makeRGB(int	red,
							  int	grn,
							  int	blu)
	{
		return((255 << 24) + (red << 16) + (grn << 8) + blu);
	}

	public static int darkenARGB(int	argb,
								 int	percentToDarkenAs0To100)
	{
		int alp, red, grn, blu;
		double k;

		k = (double)(100 - percentToDarkenAs0To100) / (double)100.0;
		alp = getAlp(argb);
		red = (int)((double)getRed(argb) * k);
		grn = (int)((double)getGrn(argb) * k);
		blu = (int)((double)getBlu(argb) * k);
		argb = makeARGB(alp, red, grn, blu);
		return(argb);
	}

	/**
	 * Called to get the percent of the color line, which extends from blue
	 * at the start to red at the end, through 10 distinct stages
	 * @param percent percentage through the color line to extract color
	 * @return
	 */
	public static int getOfColorLine(double	percent,
									 int	alp)
	{
		double thisPercent;
		int argb;

		percent = Math.abs(percent);
		if (percent == 0.0) {
			// dark blue exactly
			return(makeARGB(alp,0,0,128));

		} else if (percent > 0 && percent < 0.1) {
			// dark blue = rgb = (0,0,255)
			thisPercent = (percent / 0.1);
			return(makeARGB(alp, 0, 0, 128 + (int)(thisPercent * (double)128.0)));

		} else if (percent >= 0.1 && percent < 0.2) {
			// bright blue = rgb = (0,0,ff)
			thisPercent = ((percent - 0.1) / 0.1);
			return(makeARGB(alp, 0, (int)(thisPercent * (double)128.0), 255));

		} else if (percent >= 0.2 && percent < 0.3) {
			// blue cyan = rgb = (0,7f,ff)
			thisPercent = ((percent - 0.2) / 0.1);
			return(makeARGB(alp, 0, 128 + (int)(thisPercent * (double)128.0), 255));

		} else if (percent >= 0.3 && percent < 0.4) {
			// cyan = rgb = (0,ff,ff)
			thisPercent = ((percent - 0.3) / 0.1);
			return(makeARGB(alp, 0, 255, 255 - (int)(thisPercent * (double)128.0)));

		} else if (percent >= 0.4 && percent < 0.5) {
			// green cyan = rgb = (0,ff,7f)
			thisPercent = ((percent - 0.4) / 0.1);
			return(makeARGB(alp, 0, 255, 128 - (int)(thisPercent * (double)128.0)));

		} else if (percent >= 0.5 && percent < 0.6) {
			// green = rgb = (0,ff,0)
			thisPercent = ((percent - 0.5) / 0.1);
			return(makeARGB(alp, (int)(thisPercent * (double)128.0), 255, 0));

		} else if (percent >= 0.6 && percent < 0.7) {
			// yellow green = rgb = (7f,ff,00)
			thisPercent = ((percent - 0.6) / 0.1);
			return(makeARGB(alp, 128 + (int)(thisPercent * (double)128.0), 255, 0));

		} else if (percent >= 0.7 && percent < 0.8) {
			// yellow = rgb = (ff,ff,00)
			thisPercent = ((percent - 0.7) / 0.1);
			return(makeARGB(alp, 255, 255 - (int)(thisPercent * (double)128.0), 0));

		} else if (percent >= 0.8 && percent < 0.9) {
			// orangeish = rgb = (ff,7f,00)
			thisPercent = ((percent - 0.8) / 0.1);
			return(makeARGB(alp, 255, 128 - (int)(thisPercent * (double)128.0), 0));

		} else if (percent >= 0.9 && percent < 1.0) {
			// reddish = rgb = (ff,7f,7f)
			thisPercent = ((percent - 0.9) / 0.1);
			return(makeARGB(alp, 255, (int)(thisPercent * (double)128.0), (int)(thisPercent * (double)128.0)));

		} else if (percent == 1.0) {
			// red
			return(makeARGB(alp, 255, 128, 128));

		} else {
			// Invalid color range
			argb = 0;

		}
		return(argb);
	}

	public void applyAlphaMask(String filename)
	{
		AlphaImage img = new AlphaImage(filename);
		applyAlphaMask(img, 0, 0, m_img.getWidth(), m_img.getHeight());
	}

	public void applyAlphaMask(String	filename,
							   int		startX,
							   int		startY,
							   int		endX,
							   int		endY)
	{
		AlphaImage img = new AlphaImage(filename);
		applyAlphaMask(img, startX, startY, endX, endY);
	}

	public void applyAlphaMask(AlphaImage img)
	{
		applyAlphaMask(img, 0, 0, m_img.getWidth(), m_img.getHeight());
	}

	public void applyAlphaMask(AlphaImage	img,
							   int			startX,
							   int			startY)
	{
		applyAlphaMask(img, startX, startY, startX + img.getWidth(), startY + img.getHeight());
	}

	public void applyAlphaMask(AlphaImage	img,
							   int			startX,
							   int			startY,
							   int			endX,
							   int			endY)
	{
		int y, x, argb1, argb2, argb3, minHeight, minWidth, loadX, loadY;

		minWidth	= Math.min(endX, m_img.getWidth());
		minHeight	= Math.min(endY, m_img.getHeight());

		// Iterate through the selected area pixel by pixel
		loadY		= 0;
		for (y = startY; y < minHeight; y++)
		{
			if (loadY < img.getHeight())
			{
				loadX = 0;
				for (x = startX; x < minWidth; x++)
				{
					if (loadX < img.getWidth())
					{
						argb1 = img.getARGB(loadX, loadY);	// Mask image
						argb2 = m_img.getRGB(x,y);			// Target image
						// Update target with new mask value
						argb3 = AlphaImage.adjustTargetARGB_Via_RGBToAlpha(argb1, argb2);
						m_img.setRGB(x, y, argb3);
					}
					++loadX;
				}
			}
			++loadY;
		}
	}

	public void applyAlphaMaskEx(AlphaImage		img,
								 float			delta)
	{
		applyAlphaMaskEx(img, 0, 0, m_img.getWidth(), m_img.getHeight(), delta);
	}

	public void applyAlphaMaskEx(AlphaImage		img,
								 int			startX,
								 int			startY,
								 float			delta)
	{
		applyAlphaMaskEx(img, startX, startY, startX + img.getWidth(), startY + img.getHeight(), delta);
	}

	public void applyAlphaMaskEx(AlphaImage		img,
								 int			startX,
								 int			startY,
								 int			endX,
								 int			endY,
								 float			delta)
	{
		int alp, red, grn, blu;
		int y, x, argb1, argb2, argb3, minHeight, minWidth, loadX, loadY;

		minHeight	= endY;
		minWidth	= endX;
		loadY		= 0;
		for (y = startY; y <= minHeight; y++)
		{
			if (loadY < img.getHeight())
			{
				loadX = 0;
				for (x = startX; x <= minWidth; x++)
				{
					if (loadX < img.getWidth())
					{
						argb1 = img.getARGB(loadX, loadY);	// Mask image
						// Make the argb1 component have an altered alpha by its delta
						alp	= getAlp(argb1);
						red	= getRed(argb1);
						grn	= getGrn(argb1);
						blu	= getBlu(argb1);
						alp = (int)((float)alp * delta);
						alp	= Math.max(Math.min(alp, 255), 0);
						argb1 = makeARGB(alp, red, grn, blu);

						// Get value from the target image
						argb2 = m_img.getRGB(x,y);

						// Update target with new mask value
						argb3 = AlphaImage.adjustTargetARGB_Via_RGBToAlpha(argb1, argb2);

						// Set the pixel
						m_img.setRGB(x, y, argb3);
					}
					++loadX;
				}
			}
			++loadY;
		}
	}

	public void recolorizeByAlphaMask(AlphaImage	imgMask,
									  int			argb,
									  int			startX,
									  int			startY,
									  int			endX,
									  int			endY)
	{
		int x, y, argb2, argb3, imgAlphaX, imgAlphaY;
		int r1, g1, b1, a2;
		double r2, g2, b2;
		double r3, g3, b3;
		double alp2, alp3, malp3;
		int nred, ngrn, nblu;

		// Grab the colorize attributes
		r1			= getRed(argb);
		g1			= getGrn(argb);
		b1			= getBlu(argb);
		imgAlphaY	= 0;
		for (y = startY; y < endY; y++)
		{
			imgAlphaX = 0;
			for (x = startX; x < endX; x++)
			{
				// Extract the pixel's existing color
				argb2	= m_img.getRGB(x, y);
				a2		= getAlp(argb2);
				r2		= (double)getRed(argb2);
				g2		= (double)getGrn(argb2);
				b2		= (double)getBlu(argb2);
				// Create the grayscale for the target image
				alp2	= ((r2*0.35) + (g2*0.54) + (b2*0.11)) / (double)255.0;

				// Extract the map's mask
				argb3	= imgMask.getARGB(imgAlphaX, imgAlphaY);
				r3		= (double)getRed(argb3);
				g3		= (double)getGrn(argb3);
				b3		= (double)getBlu(argb3);
				// And compute its alpha value by its grayscale
				alp3	= ((r3*0.35) + (g3*0.54) + (b3*0.11)) / (double)255.0;
				malp3	= 1.0 - alp3;

				// And compute the new color values as a proportion of the existing color to the new colorized version based on the grayscale
				nred	= (int)(((alp2 * r1) * alp3) + (r2 * malp3));
				ngrn	= (int)(((alp2 * g1) * alp3) + (g2 * malp3));
				nblu	= (int)(((alp2 * b1) * alp3) + (b2 * malp3));

				// Set the new color relative to the proportion
				m_img.setRGB(x, y, makeARGB(a2, nred, ngrn, nblu));
				++imgAlphaX;
			}
			++imgAlphaY;
		}
	}

	public void darkenByAlphaMask(String filename)
	{
		AlphaImage img = new AlphaImage(filename);
		darkenByAlphaMask(img, 0, 0, m_img.getWidth(), m_img.getHeight());
	}

	public void darkenByAlphaMask(String	filename,
								  int		startX,
								  int		startY,
								  int		endX,
								  int		endY)
	{
		AlphaImage img = new AlphaImage(filename);
		darkenByAlphaMask(img, startX, startY, endX, endY);
	}

	public void darkenByAlphaMask(AlphaImage img)
	{
		darkenByAlphaMask(img, 0, 0, m_img.getWidth(), m_img.getHeight());
	}

	public void darkenByAlphaMask(AlphaImage	img,
								  int			startX,
								  int			startY,
								  int			endX,
								  int			endY)
	{
		int y, x, argb1, argb2, argb3, minHeight, minWidth, loadX, loadY;

		minHeight	= endY;
		minWidth	= endX;
		loadY		= 0;
		for (y = startY; y <= minHeight; y++)
		{
			if (loadY < img.getHeight())
			{
				loadX = 0;
				for (x = startX; x <= minWidth; x++)
				{
					if (loadX < img.getWidth())
					{
						argb1 = img.getARGB(loadX, loadY);	// Mask image
						argb2 = m_img.getRGB(x,y);			// Target image
						// Update target with new mask value
						argb3 = AlphaImage.darkenARGB_Via_RGBToAlpha(argb1, argb2);
						m_img.setRGB(x, y, argb3);
					}
					++loadX;
				}
			}
			++loadY;
		}
	}

	/**
	 * Uses the value of rgbToAlpha to adjust the argb's alpha channel to
	 * whatever the rgbToAlpha's grayscale value is
	 * @param rgbToAlpha
	 * @param argbToupdate
	 * @return
	 */
	public static int adjustTargetARGB_Via_RGBToAlpha(int	rgbToAlpha,
													  int	targetARGB)
	{
		int ired, igrn, iblu;
		double red, grn, blu, alp1, alp2;

		// Grab the grayscale values
		//alp = (double)getAlp(rgbToAlpha);	// We ignore the input value's alpha channel, which is why it's called rgbToAlpha here
		red		= (double)getRed(rgbToAlpha) * (double)0.35;		// 35% red
		grn		= (double)getGrn(rgbToAlpha) * (double)0.54;		// 54% green
		blu		= (double)getBlu(rgbToAlpha) * (double)0.11;		// 11% blue
		// Compute the grayscale proportional alpha
		alp1	= (red + grn + blu) / (double)255.0;

		// Extract the target components
		alp2	= getAlp(targetARGB);
		ired	= getRed(targetARGB);
		igrn	= getGrn(targetARGB);
		iblu	= getBlu(targetARGB);

		// Set the new value
		return(makeARGB((int)(alp1 * alp2), ired, igrn, iblu));
	}

	/**
	 * Uses the value of rgbToAlpha to adjust the argb's brightness to
	 * whatever the rgbToAlpha's grayscale value is, only darkening or leaving
	 * the same the existing color value
	 * @param rgbToAlpha
	 * @param argbToupdate
	 * @return
	 */
	public static int darkenARGB_Via_RGBToAlpha(int	rgbToAlpha,
												int	targetARGB)
	{
		int alp2, red2, grn2, blu2;
		double alp1, red1, grn1, blu1;

		// Grab the grayscale values
		//alp = (double)getAlp(rgbToAlpha);	// We ignore the input value's alpha channel, which is why it's called rgbToAlpha here
		red1	= (double)getRed(rgbToAlpha) * (double)0.35;		// 35% red
		grn1	= (double)getGrn(rgbToAlpha) * (double)0.54;		// 54% green
		blu1	= (double)getBlu(rgbToAlpha) * (double)0.11;		// 11% blue
		// Compute the grayscale proportional alpha
		alp1	= (red1 + grn1 + blu1) / (double)255.0;

		// Extract the target components
		alp2	= getAlp(targetARGB);
		red2	= (int)((double)getRed(targetARGB) * alp1);
		grn2	= (int)((double)getGrn(targetARGB) * alp1);
		blu2	= (int)((double)getBlu(targetARGB) * alp1);

		// Set the new value
		return(makeARGB((int)alp2, red2, grn2, blu2));
	}

	/**
	 * Returns the alpha component of the 32-bit quantity argb, which is in
	 * the form (from msb to lsb) alpha:8,red:8,green:8,blu:8
	 * @param argb
	 * @return alpha component
	 */
	public static int getAlp(int argb)
	{
		return((int)(((argb & 0xff000000) >> 24) & 0xff));
	}

	/**
	 * Returns the red component of the 32-bit quantity argb, which is in
	 * the form (from msb to lsb) alpha:8,red:8,green:8,blu:8
	 * @param argb
	 * @return red component
	 */
	public static int getRed(int argb)
	{
		return((argb & 0x00ff0000) >> 16);
	}

	/**
	 * Returns the green component of the 32-bit quantity argb, which is in
	 * the form (from msb to lsb) alpha:8,red:8,green:8,blu:8
	 * @param argb
	 * @return green component
	 */
	public static int getGrn(int argb)
	{
		return((argb & 0x0000ff00) >> 8);
	}

	/**
	 * Returns the blue component of the 32-bit quantity argb, which is in
	 * the form (from msb to lsb) alpha:8,red:8,green:8,blu:8
	 * @param argb
	 * @return blue component
	 */
	public static int getBlu(int argb)
	{
		return(argb & 0x000000ff);
	}

	/**
	 * Simple accessor for current image, to colorize the entire thing
	 * @param argb
	 */
	public void colorize(int argb)
	{
		colorize(argb, 0, 0, m_img.getWidth(), m_img.getHeight());
	}

	/**
	 * Applies the specified argb color to the image, potentially leaving a
	 * percentage (1 - getAlp(argb)) of the original color in tact.
	 * @param argb
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void colorize(int	argb,
						 int	startX,
						 int	startY,
						 int	endX,
						 int	endY)
	{
		int x, y, argb2;
		int     r1, g1, b1;
		int a2, r2, g2, b2;
		int     r3, g3, b3;
		double alp1, alp2;

		// Grab the colorize attributes
		alp1	= (double)getAlp(argb) / (double)255.0;
		alp2	= 1.0 - alp1;			// Used for math, how much of the other-pixel's values are used
		r1		= getRed(argb);
		g1		= getGrn(argb);
		b1		= getBlu(argb);

		for (y = startY; y < endY; y++)
		{
			if (y >= 0 && y < getHeight())
			{
				for (x = startX; x < endX; x++)
				{
					if (x >= 0 && x < getWidth())
					{
						// Extract the pixel's existing color
						argb2	= m_img.getRGB(x, y);
						a2		= getAlp(argb2);
						r2		= getRed(argb2);
						g2		= getGrn(argb2);
						b2		= getBlu(argb2);

						// Create the merged colors
						r3		= (int)((r1 * alp1) + (r2 * alp2));
						g3		= (int)((g1 * alp1) + (g2 * alp2));
						b3		= (int)((b1 * alp1) + (b2 * alp2));

						// Set the new color
						m_img.setRGB(x, y, makeARGB(a2, r3, g3, b3));
					}
				}
			}
		}
	}

	/**
	 * Simple accessor for the current image, to recolorize the entire thing
	 * @param argb
	 */
	public void recolorize(int argb)
	{
		recolorize(argb, 0, 0, m_img.getWidth(), m_img.getHeight());
	}

	/**
	 * Recolorize the entire image by computing its grayscale component, and
	 * then applying the argb color to it completely.  Is slightly faster than
	 * colorize() with an argb value with an alpha component of 255.
	 * @param argb
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void recolorize(int	argb,
						   int	startX,
						   int	startY,
						   int	endX,
						   int	endY)
	{
		int x, y, argb2;
		int r1, g1, b1, a2;
		double r2, g2, b2;
		double rgb;

		// Grab the colorize attributes
		r1		= getRed(argb);
		g1		= getGrn(argb);
		b1		= getBlu(argb);

		for (y = startY; y < endY; y++)
		{
			for (x = startX; x < endX; x++)
			{
				// Extract the pixel's existing color
				argb2	= m_img.getRGB(x, y);
				a2		= getAlp(argb2);
				r2		= (double)getRed(argb2);
				g2		= (double)getGrn(argb2);
				b2		= (double)getBlu(argb2);

				// Create the grayscale
				rgb		= ((r2*0.35) + (g2*0.54) + (b2*0.11)) / (double)255.0;

				// Set the new color relative to the proportion
				m_img.setRGB(x, y, makeARGB(a2, (int)(rgb * r1), (int)(rgb * g1), (int)(rgb * b1)));
			}
		}
	}

	public void alphaize(int alpha)
	{
		alphaize(alpha, 0, 0, m_img.getWidth(), m_img.getHeight());
	}

	/**
	 * Sets the alpha value for the entire range, leaving the color info in tact
	 * @param alpha
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void alphaize(int	alpha,
						 int	startX,
						 int	startY,
						 int	endX,
						 int	endY)
	{
		int x, y, argb, red, grn, blu;

		for (y = startY; y < endY; y++)
		{
			if (y >= 0 && y < getHeight())
			{
				for (x = startX; x < endX; x++)
				{
					if (x >= 0 && x < getWidth())
					{
						// Extract the pixel's existing color
						argb	= m_img.getRGB(x, y);
						red		= getRed(argb);
						grn		= getGrn(argb);
						blu		= getBlu(argb);

						// Set the new alpha value color
						m_img.setRGB(x, y, makeARGB(alpha, red, grn, blu));
					}
				}
			}
		}
	}

	public void grayscale()
	{
		grayscale(0, 0, m_img.getWidth(), m_img.getHeight());
	}

	/**
	 * Grayscales the entire range
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void grayscale(int	startX,
						  int	startY,
						  int	endX,
						  int	endY)
	{
		int x, y, argb, alp, gs;
		double r, g, b;

		for (y = startY; y < endY; y++)
		{
			if (y >= 0 && y < getHeight())
			{
				for (x = startX; x < endX; x++)
				{
					if (x >= 0 && x < getWidth())
					{
						// Extract the pixel's existing color
						argb	= m_img.getRGB(x, y);
						alp		= getAlp(argb);
						r		= (double)getRed(argb) * 0.35;
						g		= (double)getGrn(argb) * 0.54;
						b		= (double)getBlu(argb) * 0.11;

						// Compute grayscale value
						gs		= (int)(r + g + b);

						// Set the new grayscale value as its color
						m_img.setRGB(x, y, makeARGB(alp, gs, gs, gs));
					}
				}
			}
		}
	}

	/**
	 * Fill the entire image with a specified color
	 * @param argb
	 */
	public void fill(int argb)
	{
		int x, y;

		for (y = 0; y < m_img.getHeight(); y++)
		{
			for (x = 0; x < m_img.getWidth(); x++)
			{
				m_img.setRGB(x, y, argb);
			}
		}
	}

	/**
	 * Copies the source image to the destination image
	 * @param argb
	 */
	public void copy(AlphaImage img)
	{
		int x, y, maxX, maxY;

		maxX = Math.min(getWidth(), img.getWidth());
		maxY = Math.min(getHeight(), img.getHeight());
		for (y = 0; y < m_img.getHeight(); y++)
		{
			if (y <= maxY)
			{
				for (x = 0; x < m_img.getWidth(); x++)
				{
					if (x <= maxX)
						m_img.setRGB(x, y, img.getARGB(x, y));
				}
			}
		}
	}

	public void copyByAlphaMask(AlphaImage	img,
								AlphaImage	alphaMask)
	{
		int x, y, maxX, maxY, argb1, argb2, argbm, a1, a2, a3, r3, g3, b3;
		double alpm, rm, gm, bm, malpm, r1, g1, b1, r2, g2, b2;

		maxX = Math.min(getWidth(), img.getWidth());
		maxY = Math.min(getHeight(), img.getHeight());
		for (y = 0; y < m_img.getHeight(); y++)
		{
			if (y < maxY)
			{
				for (x = 0; x < m_img.getWidth(); x++)
				{
					if (x < maxX)
					{
						// Grab the mask value
						argbm	= alphaMask.getARGB(x, y);
						if (getAlp(argbm) != 0)
						{	// We only copy non-zero values, for non-zero values are "ignored" because the math leaves them as they were originally
							rm		= (double)getRed(argbm) / 255.0;
							gm		= (double)getGrn(argbm) / 255.0;
							bm		= (double)getBlu(argbm) / 255.0;
							alpm	= rm*0.35 + gm*0.54 + bm*0.11;	// alpha mask, computed by grayscale formula
							malpm	= 1.0 - alpm;					// "minus alpha mask"

							// Grab the source colors
							argb1	= img.getARGB(x, y);
							a1		= getAlp(argb1);
							r1		= (double)getRed(argb1);
							g1		= (double)getGrn(argb1);
							b1		= (double)getBlu(argb1);

							// Grab the existing color
							argb2	= m_img.getRGB(x, y);
							a2		= getAlp(argb2);
							r2		= (double)getRed(argb2);
							g2		= (double)getGrn(argb2);
							b2		= (double)getBlu(argb2);

							// Create the merged colors
							a3		= (int)((a1 * malpm) + (a2 * alpm));
							r3		= (int)((r1 * malpm) + (r2 * alpm));
							g3		= (int)((g1 * malpm) + (g2 * alpm));
							b3		= (int)((b1 * malpm) + (b2 * alpm));

							m_img.setRGB(x, y, makeARGB(a1, r3, g3, b3));
						}
					}
				}
			}
		}
	}

	public void overlayImage(AlphaImage		img,
							 Rectangle		rect,
							 int			alpha)
	{
		overlayImage(img, rect.x, rect.y, rect.width, rect.height, alpha);
	}

	/**
	 * Overlay the specified image on top of this one
	 * @param img
	 * @param offsetX
	 * @param offsetY
	 * @param alpha
	 */
	public void overlayImage(AlphaImage		img,
							 int			offsetX,
							 int			offsetY,
							 int			alpha)
	{
		overlayImage(img, offsetX, offsetY, img.getWidth(), img.getHeight(), alpha);
	}

	/**
	 * Overlay the specified image on top of this one
	 * @param img
	 * @param offsetX
	 * @param offsetY
	 * @param width
	 * @param height
	 * @param alpha
	 */
	public void overlayImage(AlphaImage		img,
							 int			offsetX,
							 int			offsetY,
							 int			width,
							 int			height,
							 int			alpha)
	{
		int x, y, argb1, argb2;
		double a1, r1, g1, b1;
		double a2, r2, g2, b2;
		int a3, r3, g3, b3;
		double alp1, alp2;

		// Compute our alpha values for the overlay
		alp1	= (double)alpha / (double)255.0;
		alp2	= 1.0 - alp1;			// Used for math, how much of the other-pixel's values are used

		for (y = offsetY; y < m_img.getHeight() && y - offsetY < img.getHeight(); y++)
		{
			for (x = offsetX; x < m_img.getWidth() && x - offsetX < img.getWidth(); x++)
			{
				// Grab the overlay image pixel
				argb1	= img.getARGB(x - offsetX, y - offsetY);
				a1		= (double)getAlp(argb1);
				r1		= (double)getRed(argb1);
				g1		= (double)getGrn(argb1);
				b1		= (double)getBlu(argb1);

				// Extract the existing image pixel
				argb2	= m_img.getRGB(x, y);
				a2		= (double)getAlp(argb2);
				r2		= (double)getRed(argb2);
				g2		= (double)getGrn(argb2);
				b2		= (double)getBlu(argb2);

				// Create the merged colors
				a3		= (int)((a1 * alp1) + (a2 * alp2));
				r3		= (int)((r1 * alp1) + (r2 * alp2));
				g3		= (int)((g1 * alp1) + (g2 * alp2));
				b3		= (int)((b1 * alp1) + (b2 * alp2));

				// Set the new color
				m_img.setRGB(x, y, makeARGB((int)a2, r3, g3, b3));
			}
		}
	}

	public void overlayImageExcludeColor(AlphaImage		img,
										 int			excludeColor)
	{
		overlayImageExcludeColor(img, 0, 0, img.getWidth(), img.getHeight(), 255, excludeColor);
	}

	public void overlayImageExcludeColor(AlphaImage		img,
										 int			alpha,
										 int			excludeColor)
	{
		overlayImageExcludeColor(img, 0, 0, img.getWidth(), img.getHeight(), alpha, excludeColor);
	}

	public void overlayImageExcludeColor(AlphaImage		img,
										 Rectangle		rect,
										 int			alpha,
										 int			excludeColor)
	{
		overlayImageExcludeColor(img, rect.x, rect.y, rect.width, rect.height, alpha, excludeColor);
	}

	/**
	 * Overlay the specified image on top of this one, but exclude the
	 * excludeColor
	 * @param img
	 * @param offsetX
	 * @param offsetY
	 * @param alpha
	 * @param excludeColor
	 */
	public void overlayImageExcludeColor(AlphaImage		img,
										 int			offsetX,
										 int			offsetY,
										 int			alpha,
										 int			excludeColor)
	{
		overlayImageExcludeColor(img, offsetX, offsetY, img.getWidth(), img.getHeight(), alpha, excludeColor);
	}

	/**
	 * Overlay the specified image on top of this one
	 * @param img
	 * @param offsetX
	 * @param offsetY
	 * @param width
	 * @param height
	 * @param alpha
	 */
	public void overlayImageExcludeColor(AlphaImage		img,
										 int			offsetX,
										 int			offsetY,
										 int			width,
										 int			height,
										 int			alpha,
										 int			excludeColor)
	{
		int x, y, argb1, argb2;
		double a1, r1, g1, b1;
		double a2, r2, g2, b2;
		int a3, r3, g3, b3, rgb;
		double alp1, alp2;

		// Grab the excludeColor without an alpha component
		excludeColor = makeARGB(0, getRed(excludeColor), getGrn(excludeColor), getBlu(excludeColor));

		// Compute our alpha values for the overlay
		alp1	= (double)alpha / (double)255.0;
		alp2	= 1.0 - alp1;			// Used for math, how much of the other-pixel's values are used

		for (y = offsetY; y < m_img.getHeight() && y - offsetY < img.getHeight(); y++)
		{
			for (x = offsetX; x < m_img.getWidth() && x - offsetX < img.getWidth(); x++)
			{
				// Grab the overlay image pixel
				argb1	= img.getARGB(x - offsetX, y - offsetY);
				rgb		= makeARGB(0, getRed(argb1), getGrn(argb1), getBlu(argb1));
				if (rgb != excludeColor)
				{	// We are including this color
					a1		= (double)getAlp(argb1);
					r1		= (double)getRed(argb1);
					g1		= (double)getGrn(argb1);
					b1		= (double)getBlu(argb1);

					// Extract the existing image pixel
					argb2	= m_img.getRGB(x, y);
					a2		= (double)getAlp(argb2);
					r2		= (double)getRed(argb2);
					g2		= (double)getGrn(argb2);
					b2		= (double)getBlu(argb2);

					// Create the merged colors
					a3		= (int)((a1 * alp1) + (a2 * alp2));
					r3		= (int)((r1 * alp1) + (r2 * alp2));
					g3		= (int)((g1 * alp1) + (g2 * alp2));
					b3		= (int)((b1 * alp1) + (b2 * alp2));

					// Set the new color
					m_img.setRGB(x, y, makeARGB((int)a2, r3, g3, b3));
				}
			}
		}
	}

	public void drawStringInRectangle(Rectangle		rect,
									  String		text,
									  Color			c,
									  Font			font,
									  int			alphaDarken,
									  boolean		shadow)
	{
		int x, y;
		Rectangle r;

		r = getStringRectangle(text, font);
		x = (int)rect.getCenterX() - ((int)r.getWidth()  / 2);
		y = (int)rect.getCenterY() + ((int)r.getHeight() / 2);
		if (shadow)
			drawString(x+1, y+1, text, Color.BLACK, font, alphaDarken);
		drawString(x, y, text, c, font, alphaDarken);
	}

	/**
	 * Writes a string using the specified font at the specified location
	 * (which is the lower-left-most pixel of the text)
	 * @param x
	 * @param y
	 * @param text
	 * @param c
	 * @param font
	 * @param alphaDarken
	 */
	public void drawString(int		x,
						   int		y,
						   String	text,
						   Color	c,
						   Font		font,
						   int		alphaDarken)
	{
		Graphics2D	g2	= (Graphics2D)m_img.getGraphics();

		if (alphaDarken != 255)
		{	// Apply a darkening to the background around where the text will go
			Rectangle	r = getStringRectangle(text, font);
			// REMEMBER This code doesn't work, is misaligned vertically:
			colorize(makeARGB(alphaDarken,0,0,0), x-3+(int)r.getMinX(), y+(int)r.getMinY(), x+3+(int)r.getMaxX(), y+(int)r.getMaxY());
		}

		g2.setFont(font);
		g2.setColor(c);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawString(text, x, y);
	}

	public void drawStringRotated90DegreesCCW(int		x,
											  int		y,
											  String	text,
											  Color	c,
											  Font		font)
	{
		int argb, textX, textY, destX, destY;
		AlphaImage imgText;
		Graphics graphics;
		Rectangle rect;

		rect		= getStringRectangle(text, font);
		imgText		= new AlphaImage(rect.width, rect.height);
		graphics	= imgText.m_img.getGraphics();
		graphics.setFont(font);
		graphics.setColor(c);
		graphics.drawString(text, 0, rect.height - 2);

		// We have the text in our temporary area, now rotate it for drawing onto our target image
		rect = rotateRectangle90DegreesCCW(rect);
		destX = x - rect.width;
		for (textY = 0; textY < imgText.getHeight(); textY++)
		{
			if (destX > 0)
			{
				destY = y;
				for (textX = 0; textX < imgText.getWidth(); textX++)
				{
					if (destY > 0)
					{
						argb = imgText.getARGB(textX, textY);
						if (argb != 0xff000000)
							setARGB(destX, destY, argb);
					}

					// Move our target vertically on the image
					--destY;
				}
			}
			++destX;
		}
	}

	/**
	 * Determines how big the resulting string will be, used for determining
	 * the center coordinate or location of the text within another control
	 * @param text
	 * @param font
	 * @return
	 */
	public static Rectangle getStringRectangle(String		text,
											   Font			font)
	{
        BufferedImage		img		= new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        img.getGraphics().setFont(font);
        FontRenderContext	frc		= img.getGraphics().getFontMetrics().getFontRenderContext();
		Rectangle			rect	= font.getStringBounds(text, frc).getBounds();
        return(rect);
	}

	public static Rectangle getStringRectangleRotated90DegreesCCW(String	text,
																  Font		font)
	{
        BufferedImage		img		= new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        img.getGraphics().setFont(font);
        FontRenderContext	frc		= img.getGraphics().getFontMetrics().getFontRenderContext();
		Rectangle			rect	= font.getStringBounds(text, frc).getBounds();
        return(rotateRectangle90DegreesCCW(rect));
	}

	/**
	 * Rotates on lower-left point
	 * @param rect
	 * @return
	 */
	public static Rectangle rotateRectangle90DegreesCCW(Rectangle rect)
	{
		Rectangle newRect;

		newRect			= new Rectangle();
		newRect.x		= rect.x - rect.height;
		newRect.y		= rect.y + rect.height - rect.width;
		newRect.width	= rect.height;
		newRect.height	= rect.width;
		return(newRect);
	}

	/**
	 * Saves the AlphaImage to disk as a PNG file
	 * @param filename
	 */
	public void writePNG(String filename)
	{
        try
		{
            ImageIO.write(m_img, "png", new File(filename));

        } catch(Exception e) {
        }
	}

	public static int getButtonWidth(String		text,
									 Font		font)
	{
		getButtonWidthAndHeight(text, font);
		return(m_buttonWidth);
	}

	public static int getButtonHeight(String	text,
									  Font		font)
	{
		return(m_buttonLeft.getHeight());
	}

	public static Rectangle getButtonRect(String	text,
										  Font		font)
	{
		getButtonWidthAndHeight(text, font);
		return(m_buttonRect);
	}

	/**
	 * Creates a simple themed button AlphaImage
	 * @param text
	 * @param font
	 * @param argbButton
	 * @param textColor
	 * @return
	 */
	public static AlphaImage createButton(String	text,
										  Font		font,
										  int		argbButton,
										  Color		textColor)
	{
		int i, x, y, width, height;
		AlphaImage button;
		Rectangle rect;

		getButtonWidthAndHeight(text, font);
		width	= m_buttonWidth;
		height	= m_buttonHeight;
		rect	= m_buttonRect;

		// Create the button's image in the right size
		button = new AlphaImage(width, height);

		// Overlay the left portion
		button.overlayImage(m_buttonLeft, 0, 0, 255);
		button.applyAlphaMask(m_buttonLeftAlphaMask, 0, 0);
		// Overlay the middle portion repeatedly
		for (i = m_buttonLeft.getWidth(); i < width - m_buttonRight.getWidth(); i += m_buttonMiddle.getWidth())
		{
			button.overlayImage(m_buttonMiddle, i, 0, 255);
// REMEMBER need to make sure we don't overlay an alpha mask here only to be done again by the right portion below
			button.applyAlphaMask(m_buttonMiddleAlphaMask, i, 0);
		}
		// Overlay the right portion
		button.overlayImage(m_buttonRight, width - m_buttonRight.getWidth(), 0, 255);
		button.applyAlphaMask(m_buttonRightAlphaMask, width - m_buttonRight.getWidth(), 0);

		// Colorize the button
		button.recolorize(argbButton);

		// Add the text
		x = (button.getWidth()  - (int)rect.getWidth())  / 2;
		y = ((button.getHeight() - (int)rect.getHeight()) / 2) + (int)(rect.getHeight() * 0.80);
		button.drawString(x+1, y+1, text, Color.BLACK, font, 255);
		button.drawString(x, y, text, textColor, font, 255);

		// All done
		return(button);
	}

	public static void getButtonWidthAndHeight(String	text,
											   Font		font)
	{
		// Determine the text size coordinates
		m_buttonRect = getStringRectangle(text, font);

		// Load the button images (if they're not already loaded
		if (m_buttonLeft == null)
			m_buttonLeft = new AlphaImage(Opbm.locateFile("button_left.png"));
		if (m_buttonMiddle == null)
			m_buttonMiddle = new AlphaImage(Opbm.locateFile("button_middle.png"));
		if (m_buttonRight == null)
			m_buttonRight = new AlphaImage(Opbm.locateFile("button_right.png"));

		// Load the button mask images (if they're not already loaded
		if (m_buttonLeftAlphaMask == null)
			m_buttonLeftAlphaMask = new AlphaImage(Opbm.locateFile("button_left_mask.png"));
		if (m_buttonMiddleAlphaMask == null)
			m_buttonMiddleAlphaMask = new AlphaImage(Opbm.locateFile("button_middle_mask.png"));
		if (m_buttonRightAlphaMask == null)
			m_buttonRightAlphaMask = new AlphaImage(Opbm.locateFile("button_right_mask.png"));

		// Determine the button's required width
		m_buttonWidth	= m_buttonLeft.getWidth() + m_buttonMiddle.getWidth() + m_buttonRight.getWidth();
		m_buttonHeight	= m_buttonLeft.getHeight();
		if (m_buttonRect.getWidth() > m_buttonWidth - 10)
			m_buttonWidth = (int)m_buttonRect.getWidth() + 10;
	}

	/**
	 * Create a new AlphaImage by extracting the rectangular coordinates from
	 * the current image
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @return
	 */
	public AlphaImage extractImage(int		startX,
								   int		startY,
								   int		endX,
								   int		endY)
	{
		int x, y, width, height;

		width	= Math.max(endX - startX, 1);
		height	= Math.max(endY - startY, 1);

		AlphaImage img = new AlphaImage(width, height);
		for (y = 0; y < height; y++)
		{
			if (startY + y < m_img.getHeight())
			{
				for (x = 0; x < width; x++)
				{
					if (startX + x < m_img.getWidth())
						img.setARGB(x, y, getARGB(startX + x, startY + y));
				}
			}
		}
		return(img);
	}

	public void makeAllOtherColorsHaveAlpha(int rgb)
	{
		makeAllOtherColorsHaveAlpha(0, 0, m_img.getWidth(), m_img.getHeight(), rgb);
	}

	public void makeAllOtherColorsHaveAlpha(int		startX,
											int		startY,
											int		endX,
											int		endY,
											int		rgb)
	{
		int x, y, argb, alp, red, grn, blu, rred, rgrn, rblu;

		rred	= getRed(rgb);
		rgrn	= getGrn(rgb);
		rblu	= getBlu(rgb);
		for (y = startY; y < endY; y++)
		{
			for (x = startX; x < endX; x++)
			{
				argb	= getARGB(x, y);
				alp		= getAlp(argb);
				red		= getRed(argb);
				grn		= getRed(argb);
				blu		= getRed(argb);
				if (red != rred || grn != rgrn || blu != rblu)
				{	// This is not the specified color, it needs to have an alpha value
					if (alp == 0)
						setARGB(x, y, makeARGB(255, red, grn, blu));
					//else it already has an alpha value
				}
			}
		}
	}

	public void scaleBrightness(double brightness)
	{
		scaleBrightness(brightness, 0, 0, m_img.getWidth(), m_img.getHeight());
	}

	/**
	 * Increase the brightness into the given area
	 * @param brightness percentage
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void scaleBrightness(double	brightness,
								int		startX,
								int		startY,
								int		endX,
								int		endY)
	{
		double brightness2;
		int x, y, argb, alp;
		double red1, grn1, blu1;
		int red2, grn2, blu2;

		brightness2 = 1.0 - brightness;
		for (y = startY; y < endY; y++)
		{
			for (x = startX; x < endX; x++)
			{
				// Grab the current value
				argb	= getARGB(x, y);
				alp		= getAlp(argb);
				red1	= ((double)getRed(argb) / (double)255.0);
				grn1	= ((double)getGrn(argb) / (double)255.0);
				blu1	= ((double)getBlu(argb) / (double)255.0);

				// Increase the brightness
				red2	= (int)((brightness + (brightness2 * red1)) * 255.0);
				grn2	= (int)((brightness + (brightness2 * grn1)) * 255.0);
				blu2	= (int)((brightness + (brightness2 * blu1)) * 255.0);

				// Store the new color
				setARGB(x, y, makeARGB(alp, red2, grn2, blu2));
			}
		}
	}

	/**
	 * Increase the brightness into the given area
	 * @param brightness percentage
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void scaleBrightnessByAlphaMask(double		brightness,
										   AlphaImage	imgMask,
										   int			startX,
										   int			startY,
										   int			endX,
										   int			endY)
	{
		double brightness2;
		int x, y, argb, argb2, alp, alphaX, alphaY;
		double r1, g1, b1;
		double r2, g2, b2;
		double br1, bg1, bb1;
		int red, grn, blu;
		double alp2, malp2;

		brightness2 = 1.0 - brightness;
		alphaY = 0;
		for (y = startY; y < endY; y++)
		{
			alphaX = 0;
			for (x = startX; x < endX; x++)
			{
				// Grab the current color value
				argb	= getARGB(x, y);
				alp		= getAlp(argb);
				r1		= ((double)getRed(argb) / (double)255.0);
				g1		= ((double)getGrn(argb) / (double)255.0);
				b1		= ((double)getBlu(argb) / (double)255.0);
				// Increase the brightness for these components
				br1	= (brightness + (brightness2 * r1)) * 255.0;
				bg1	= (brightness + (brightness2 * g1)) * 255.0;
				bb1	= (brightness + (brightness2 * b1)) * 255.0;

				// Extract the alphaMask value
				argb2	= imgMask.getARGB(alphaX, alphaY);
				r2		= ((double)getRed(argb2) / (double)255.0);
				g2		= ((double)getGrn(argb2) / (double)255.0);
				b2		= ((double)getBlu(argb2) / (double)255.0);
				// Compute its grayscale, to derive the proportion of the original color that should shine through
				alp2	= (r2*0.35) + (g2*0.54) + (b2*0.11);
				malp2	= 1.0 - alp2;

				// Compute the new target color
				red	= (int)((r1 * malp2 * 255.0) + (br1 * alp2));
				grn	= (int)((g1 * malp2 * 255.0) + (bg1 * alp2));
				blu	= (int)((g1 * malp2 * 255.0) + (bb1 * alp2));

				// Store the new color
				setARGB(x, y, makeARGB(alp, red, grn, blu));
				++alphaX;
			}
			++alphaY;
		}
	}

	public void scaleContrast(double brightness)
	{
		scaleContrast(brightness, 0, 0, m_img.getWidth(), m_img.getHeight());
	}

	/**
	 * Adjust the contrast. Uses a 3rd power of -1.0 <= x <= 1.0, and the
	 * resulting y is the adjustment factor for the given color
	 * @param brightness percentage
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void scaleContrast(double	contrast,
							  int		startX,
							  int		startY,
							  int		endX,
							  int		endY)
	{
		double contrast2;
		int x, y, argb, alp;
		double red1, grn1, blu1;
		int red2, grn2, blu2;

		contrast	= contrast * -1.0;
		contrast2	= contrast * contrast * contrast;
		for (y = startY; y < endY; y++)
		{
			for (x = startX; x < endX; x++)
			{
				// Grab the current value
				argb	= getARGB(x, y);
				alp		= getAlp(argb);
				red1	= ((double)getRed(argb) / (double)255.0);
				grn1	= ((double)getGrn(argb) / (double)255.0);
				blu1	= ((double)getBlu(argb) / (double)255.0);

				// Adjust the contrast
				if (red1 >= 0.5)
					red2 = (int)Math.max(Math.min(((red1 + contrast2) * 255.0), 255.0), 0.0);
				else
					red2 = (int)Math.max(Math.min(((red1 - contrast2) * 255.0), 255.0), 0.0);

				if (grn1 >= 0.5)
					grn2 = (int)Math.max(Math.min(((grn1 + contrast2) * 255.0), 255.0), 0.0);
				else
					grn2 = (int)Math.max(Math.min(((grn1 - contrast2) * 255.0), 255.0), 0.0);

				if (blu1 >= 0.5)
					blu2 = (int)Math.max(Math.min(((blu1 + contrast2) * 255.0), 255.0), 0.0);
				else
					blu2 = (int)Math.max(Math.min(((blu1 - contrast2) * 255.0), 255.0), 0.0);


				// Store the new color
				setARGB(x, y, makeARGB(alp, red2, grn2, blu2));
			}
		}
	}

	public void scale(double	scaleX,
					  double	scaleY)
	{
		AffineTransform tx = new AffineTransform();
		tx.scale(scaleX, scaleY);
//		tx.shear(shiftx, shifty);
//		tx.translate(x, y);
//		tx.rotate(radians, bufferedImage.getWidth()/2, bufferedImage.getHeight()/2);

		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		m_img = op.filter(m_img, null);
	}

	public Rectangle getRectangle()
	{
		return(new Rectangle(0, 0, m_img.getWidth(), m_img.getHeight()));
	}

	/**
	 * Draws the name repeatedly across the image using shadows and various fonts
	 * @param name text to repeat
	 */
	public void nameify(String	name)
	{
// REMEMBER going to do this to allow the image to be populated with text all about
	}

	public static Color convertARGBtoColor(int argb)
	{
		return(new Color(getRed(argb), getGrn(argb), getBlu(argb)));
	}

	public int getUserValue()				{	return(m_userValue);		}
	public void setUserValue(int value)		{	m_userValue = value;		}

	private	BufferedImage			m_img;

	// Data assigned by users
	private int						m_userValue;

	// Used for button creation
	private static AlphaImage		m_buttonLeft				= null;
	private static AlphaImage		m_buttonMiddle				= null;
	private static AlphaImage		m_buttonRight				= null;
	private static AlphaImage		m_buttonLeftAlphaMask		= null;
	private static AlphaImage		m_buttonMiddleAlphaMask		= null;
	private static AlphaImage		m_buttonRightAlphaMask		= null;

	private static int				m_buttonWidth;
	private static int				m_buttonHeight;
	private static Rectangle		m_buttonRect;
}
