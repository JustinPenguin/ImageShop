/* 
 * Names : Justin Peng and Stephen Hamlin
 * Section Leader : Katherine Erdman
 * This program allows you to take an original photo and apply filters to them
 */


import java.util.*;
import acm.graphics.*;

public class ImageShopAlgorithms implements ImageShopAlgorithmsInterface {
	/*
	 * Takes the columns of the original image and reprints them  backwards
	 * into a new image. 
	 * precondition: The original image is displayed
	 * postcondition: The columns are in reverse order
	 */
	public GImage flipHorizontal(GImage source) {
		int[][] pixels = source.getPixelArray();
		int[][] newImage = new int[pixels.length][pixels[0].length];
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				newImage[i][j] = pixels[i][pixels[0].length - j - 1];
			}
		}
		GImage modified = new GImage(newImage);
		return modified;
	}

	/*
	 * The image is rotated 90 degrees to the left by switching the rows and
	 * columns
	 * precondition: The image is printed before the rotation
	 * postcondition: The new image is printed but rotated 90 degrees to the
	 * left
	 */
	public GImage rotateLeft(GImage source) {
		int[][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int cols = pixels[0].length;
		int[][] newImage = new int[cols][rows];
		for (int i = cols - 1; i >= 0; i--) {
			for (int j = 0; j < rows; j++) {
				newImage[cols - i - 1][j] = pixels[j][i];
			}
		}
		GImage modified = new GImage(newImage);
		return modified;
	}

	/*
	 * The image is rotated 90 degrees to the left by switching the rows and
	 * columns while also reading the original columns in backwards
	 * precondition: The image is printed before the rotation postcondition: The
	 * new image is printed but rotated 90 degrees to the right
	 */
	public GImage rotateRight(GImage source) {
		int[][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int cols = pixels[0].length;
		int[][] newImage = new int[cols][rows];
		for (int i = cols - 1; i >= 0; i--) {
			for (int j = 0; j < rows; j++) {
				newImage[i][rows - j - 1] = pixels[j][i];
			}
		}
		GImage modified = new GImage(newImage);
		return modified;
	}

	/*
	 * Recreates the original image by using a nested for loop to create rows
	 * and columns based off of how many there were in the original image
	 * respectively. While going through each pixel, the method checks to see if
	 * the green to see if the units of green are at least double than that of
	 * both the red and blue. If so, it makes that pixel transparent. Otherwise,
	 * the pixel is recreated as the same as the original precondition: There's
	 * an original image postcondition: All pixels that are deemed "green" are
	 * transparent
	 * 
	 */
	public GImage greenScreen(GImage source) {
		int[][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int cols = pixels[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int pixel = pixels[i][j];
				int red = GImage.getRed(pixel);
				int green = GImage.getGreen(pixel);
				int blue = GImage.getBlue(pixel);
				int bigger = Math.max(blue, red);
				boolean greenBigger = green >= bigger * 2;
				if (greenBigger) {
					pixels[i][j] = GImage.createRGBPixel(red, green, blue, 0);
				} else {
					pixels[i][j] = GImage.createRGBPixel(red, green, blue);
				}
			}
		}
		GImage modified = new GImage(pixels);
		return modified;
	}

	/*
	 * Goes through each pixel in the image and sets the new equalize value for its RGB values
	 * precondition: The original image is displayed
	 * postcondition: The new image is equalized
	 */
	public GImage equalize(GImage source) {
		int[][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int cols = pixels[0].length;
		int[] histogram = computeLuminosityHistogram(source);
		int[] cumulativeHistogram = cumulativeLuminosityHistogram(histogram);
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int equalize = luminosityFormula(pixels, cumulativeHistogram, row, col);
				pixels[row][col] = GImage.createRGBPixel(equalize, equalize, equalize);
			}
		}
		GImage modified = new GImage(pixels);
		return modified;
	}
	/*
	 * Sets a new luminosity value to a pixel based off of the amounts of a luminosity there were for the luminosity of the
	 * current pixel by dividing the amount of times by the total amount of pixels.
	 * precondition: The picture has its normal RGB values
	 * postcondition: Each pixel has a newly calculated equalize value for each RGB but is not yet set
	 */
	private int luminosityFormula(int[][] pixels, int[] cumulativeHistogram, int row, int col) {
		int pixel = pixels[row][col];
		int red = GImage.getRed(pixel);
		int green = GImage.getGreen(pixel);
		int blue = GImage.getBlue(pixel);
		int luminosity = computeLuminosity(red, green, blue);
		double numPixels = pixels.length * pixels[0].length * 1.0;
		int numSamePix = cumulativeHistogram[luminosity];
		double equalize = 255.0 * numSamePix / numPixels;
		int newLum = (int) (equalize);
		return newLum;
	}
	/*
	 * Adds how many pixels there were for each luminosity plus all of numbers for the luminosities before it
	 * precondition: The luminosity for each pixel was detected and were added to a counter
	 * postcondition: A histogram is made of all of the added up luminosity counters
	 */
	private int[] cumulativeLuminosityHistogram(int[] histogram) {
		int iterations = histogram.length;
		int[] cumulativeHistogram = new int[iterations];
		for (int i = 0; i < iterations; i++) {
			if (i - 1 >= 0) {
				cumulativeHistogram[i] = histogram[i] + cumulativeHistogram[i - 1];
			} else {
				cumulativeHistogram[i] = histogram[i];
			}
		}
		return cumulativeHistogram;
	}
	/*
	 * Adds to a counter of how many times of each luminosity there are after each pixel's luminosity has been inspected
	 * precondition: The equalize button has been pressed
	 * postcondition: There is a histogram made of each luminosity's iteration
	 */
	private int[] computeLuminosityHistogram(GImage source) {
		int[][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int cols = pixels[0].length;
		int[] histogram = new int[256];
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int pixel = pixels[row][col];
				int red = GImage.getRed(pixel);
				int green = GImage.getGreen(pixel);
				int blue = GImage.getBlue(pixel);
				int luminosity = computeLuminosity(red, green, blue);
				histogram[luminosity]++;
			}
		}
		return histogram;
	}

	/*
	 * Creates an array of all of the pixels of the original image and then
	 * calculates the amount of rows and columns in the image. While placing
	 * each pixel with a nested for loop in the new inverted image, it uses the
	 * inverted color of the corresponding color of each pixel in the original
	 * pixel. precondition: The original image is displayed postcondition: The
	 * pixels of the image have inverted colors
	 */
	public GImage negative(GImage source) {
		int[][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int cols = pixels[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int pixel = pixels[i][j];
				int invertedRed = 255 - GImage.getRed(pixel);
				int invertedGreen = 255 - GImage.getGreen(pixel);
				int invertedBlue = 255 - GImage.getBlue(pixel);
				pixels[i][j] = GImage.createRGBPixel(invertedRed, invertedGreen, invertedBlue);
			}
		}
		GImage modified = new GImage(pixels);
		return modified;
	}

	/*
	 * Lets the user decide how many pixels on the x and y axis they want to
	 * shift the image precondition: The original image is displayed
	 * postcondition: The new image is shifted by the user inputted amount
	 */
	public GImage translate(GImage source, int dx, int dy) {
		int[][] pixels = source.getPixelArray();
		int newX;
		int newY;
		int rows = pixels.length;
		int cols = pixels[0].length;
		int[][] newImage = new int[rows][cols];
		for (int i = 0; i < rows; i++) {
			newY = dy + i;
			while (newY < 0) {
				newY += rows;
			}
			for (int j = 0; j < cols; j++) {
				newX = dx + j;
				while (newX < 0) {
					newX += cols;
				}
				newImage[newY % rows][newX % cols] = pixels[i][j];
			}
		}
		GImage modified = new GImage(newImage);
		return modified;
	}

	/*
	 * The image is blurred by averaging RGB values of every pixel the 8 pixels
	 * around each pixel precondition: An image is displayed postcondition: The
	 * image is now blurred with new RGB values for each pixel
	 */
	public GImage blur(GImage source) {
		int[][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int cols = pixels[0].length;
		int tot1;
		int tot2;
		int totRed = 0;
		int totGreen = 0;
		int totBlue = 0;
		int avgCounter = 0;
		int avgRed = 0;
		int avgBlue = 0;
		int avgGreen = 0;
		int[][] newImage = new int[rows][cols];
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				for (int i = -1; i < 2; i++) { // these get the pixels around the given pixel
					for (int j = -1; j < 2; j++) {
						tot1 = i + row;
						tot2 = j + col;
						if (tot2 < cols && tot2 >= 0 && tot1 < rows && tot1 >= 0) {
							int pixel = pixels[i + row][j + col];
							totRed += GImage.getRed(pixel);
							totBlue += GImage.getBlue(pixel);
							totGreen += GImage.getGreen(pixel);
							avgCounter++;
						}
					}
				}
				avgGreen = totGreen / avgCounter; // averages each of the RGB values
				avgRed = totRed / avgCounter;
				avgBlue = totBlue / avgCounter;
				newImage[row][col] = GImage.createRGBPixel(avgRed, avgGreen, avgBlue);
				avgCounter = 0;
				totRed = 0;
				totBlue = 0;
				totGreen = 0;
			}
		}
		GImage modified = new GImage(newImage);
		return modified;
	}
}
