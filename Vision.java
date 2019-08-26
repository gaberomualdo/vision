import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class Vision {
	/* variable for color equality range,
	 * which is used to tell whether an
	 * RGB value is considered the same
	 * color as another (if sum of R, G,
	 * and B in both are within
	 * COLOR_EQUALITY_RANGE of eachother)
	 * COLOR_EQUALITY_RANGE = 75 */
	public static int COLOR_EQUALITY_RANGE = 75;
	
	// sum of array
	public static int sum(int[] array) {
	    int sum = 0;
	    for (int value : array) {
	    	sum += value;
	    }
	    return sum;
	}
	
	// method to set rgb value of pixel
	public static void setRGB(BufferedImage imageObj, int[] pixelCoords, int[] rgba) {
		int rgbaValue = (rgba[3]<<24) | (rgba[0]<<16) | (rgba[1]<<8) | rgba[2];
		imageObj.setRGB(pixelCoords[0], pixelCoords[1], rgbaValue);		
	}
	
	// method to get rgb value of pixel
	public static int[] getRGB(BufferedImage imageObj, int[] pixelCoords) {
		int pixelColor = imageObj.getRGB(pixelCoords[0], pixelCoords[1]);
		
		// alpha
		int a = (pixelColor>>24) & 0xff;

		// red
		int r = (pixelColor>>16) & 0xff;

		// green
		int g = (pixelColor>>8) & 0xff;
		
		// blue
		int b = pixelColor & 0xff;
		
		// return array of rgba
		int[] rgba = {r, g, b, a};
		return rgba;
	}
	
	// method to decide whether colors are considered equal
	public static boolean colorsAreEqual(int[] color1, int[] color2) {
		// colors are considered equal if difference of the sum of their parts is less than COLOR_EQUALITY_RANGE
		if(Math.abs(sum(color1) - sum(color2)) > COLOR_EQUALITY_RANGE) {
			return false;
		}

		// return true if all tests passed
		return true;
	}
	
	/* method which goes through every open pixel, and fills it with a color if the pixel's
	 * color is considered equal to the color. */
	public static void fillPixelsWithColor(BufferedImage imageObj, int[] color, ArrayList<int[]> openPixelCoordinates) {
		// variable for list of elements to remove at end of method
		List<int[]> coordinatesToRemove = new ArrayList<int[]>();

		// loop through all open pixels
		for(int i = 0; i < openPixelCoordinates.size(); i++) {
			// variable for current pixel coords
			int[] currentPixelCoords = openPixelCoordinates.get(i);
			
			

			// variable for current pixel value
			int[] currentPixelValue = getRGB(imageObj, currentPixelCoords);
			
			/* if current pixel color is considered equal to passed color value, then replace current pixel
			 * color with passed color value and remove index from openPixelIndeces variable */
			if(colorsAreEqual(currentPixelValue, color)) {
				setRGB(imageObj, currentPixelCoords, color);
				
				// remove index and add to removed amount var
				coordinatesToRemove.add(currentPixelCoords);
			}
		}
		
		// remove all elements to remove from openPixelCoordinates
		openPixelCoordinates.removeAll(coordinatesToRemove);
	}
				
	// main method
	public static void main(String[] args) {
		// if there are not 2 or more arguments (as expected), show error and exit
		if(args.length < 2) {
			System.out.println("Error: expected 2 arguments with original image path and output image path");
			return;
		}

		

		// put original image path and output image path in variables
		String originalImagePath = args[0];
		String outputImagePath = args[1];

		// original image variable
		BufferedImage originalImage = null;
		try {
		    originalImage = ImageIO.read(new File(originalImagePath));
		} catch (IOException e) {
			System.out.println("An error occured while reading the original image path");
			return;
		}
		
		// variables for image width and height
		int imageWidth = originalImage.getWidth();
		int imageHeight = originalImage.getHeight();

		// variable for open pixel coordinates
		ArrayList<int[]> openPixelCoordinates = new ArrayList<int[]>();
		
		// add all possible pixel coordinates to open pixel coordinates variable
		for(int y = 0; y < imageHeight; y++) {
			for(int x = 0; x < imageWidth; x++) {
				int[] currentCoords = {x, y};
				openPixelCoordinates.add(currentCoords);
			}
		}
		
		// variable for amount of colors in output photo
		int colorsAmount = 0;
		
		// while there are pixels to fill, continue filling
		while(openPixelCoordinates.size() > 0) {
			// get random open pixel index to try and fill with color of that pixel

			// variable for current pixel coordinates
			int[] currentPixelCoords = openPixelCoordinates.get((int) (Math.random() * openPixelCoordinates.size()));

			// variable for current pixel value
			int[] currentPixelValue = getRGB(originalImage, currentPixelCoords);

			// fill pixels with current pixel color
			fillPixelsWithColor(originalImage, currentPixelValue, openPixelCoordinates);

			// add to colorsAmount variable
			colorsAmount++;

			// print percent done
			System.out.println((100 - (( (float)openPixelCoordinates.size() / (imageWidth * imageHeight) ) * 100)) + "% done (Java)");
		}
		
		// write image to output path
	    try {
	    	File outputImageFile = new File(outputImagePath);
	    	ImageIO.write(originalImage, "png", outputImageFile);
	    } catch (IOException e) {
	    	System.out.println("An error occured while writing the original image path");
			return;
	    }
	}
}