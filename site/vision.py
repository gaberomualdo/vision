#!/usr/bin/env python

# imports
import sys
import random
import time
from PIL import Image

# function to get milliseconds since epoch
current_milli_time = lambda: int(round(time.time() * 1000))

# get milliseconds for start of program and store in var
startTime = current_milli_time();

# variable for color equality range,
# which is used to tell whether an
# RGB value is considered the same
# color as another (if sum of R, G,
# and B in both are within
# COLOR_EQUALITY_RANGE of eachother)
COLOR_EQUALITY_RANGE = 75

# if 2 arguments for original image path and output image path were not given, show error and end
if(len(sys.argv) < 3):
	print("Error: expected 2 arguments with original image path and output image path")
	sys.exit()

# variables for original image path and output image path
originalImagePath = sys.argv[1]
outputImagePath = sys.argv[2]

# image object of original image
originalImage = None
try:
	originalImage = Image.open(originalImagePath)
except:
	# error getting image
	print("An error occured in reading the original image path")
	sys.exit()

# resize image

# variables for current width and height of image
imageWidth, imageHeight = originalImage.size

# target pixel count variable — used to resize image
targetPixelCount = 300000

# resize image to have closest to targetPixelCount pixels if image is too big
if(imageWidth * imageHeight > targetPixelCount):
	scaleFactor = ((float(float(imageWidth) * imageHeight) / targetPixelCount) ** 0.5)
	resizedSize = [int(imageWidth / scaleFactor), int(imageHeight / scaleFactor)]

	originalImage.thumbnail(resizedSize, Image.ANTIALIAS)

# variables for width and height of image
imageWidth, imageHeight = originalImage.size

# variables for list of pixels in image
originalImagePixelValues = originalImage.load()

# list of pixel indeces which haven't been filled (intially set to all possible indeces)
openPixelIndeces = [];

# add all possible indeces to openPixelIndeces
for y in range(imageHeight):
	for x in range(imageWidth):
		openPixelIndeces.append([x, y])

# variable for amount of colors in photo
colorsAmount = 0;

# function to tell whether colors are considered equal
def colorsAreEqual(color1, color2):
	# colors are considered equal if difference of the sum of their parts is less than COLOR_EQUALITY_RANGE
	return abs(sum(color1) - sum(color2)) < COLOR_EQUALITY_RANGE

# function which goes through every open pixel, and fills it with a color if the pixel's
# color is considered equal to the color.
def fillPixelsWithColor(color):
	# variable for amount of elements that have been removed
	amountOfElementsRemoved = 0

	# loop through all open pixels
	for i in range(len(openPixelIndeces)):
		# variable for current index
		index = openPixelIndeces[i - amountOfElementsRemoved]

		# variable for current pixel value
		currentPixelValue = originalImagePixelValues[index[0], index[1]]

		# if current pixel color is considered equal to passed color value, then replace current pixel
		# color with passed color value and remove index from openPixelIndeces variable
		if(colorsAreEqual(currentPixelValue, color)):
			originalImagePixelValues[index[0], index[1]] = color
			
			# remove index and add to removed amount var
			del openPixelIndeces[i - amountOfElementsRemoved]
			amountOfElementsRemoved += 1

# while there are pixels to fill, continue filling
while(len(openPixelIndeces) > 0):
	# get random open pixel index to try and fill with color of that pixel

	# variable for current pixel index
	currentPixelIndex = openPixelIndeces[random.randint(0, len(openPixelIndeces) - 1)]

	# variable for current pixel value
	currentPixelValue = originalImagePixelValues[currentPixelIndex[0], currentPixelIndex[1]]

	# fill pixels with current pixel color
	fillPixelsWithColor(currentPixelValue)

	# add to colorsAmount variable
	colorsAmount += 1

	# print percent done
	print ("%s%% complete (Python)" % (((1 - (float(len(openPixelIndeces)) / (imageWidth * imageHeight))) * 100)))
	
# save new image at output path
try:
	originalImage.save(outputImagePath)

	# get milliseconds for end of program and store in var
	endTime = current_milli_time();

	# print amount of time program took to execute in milliseconds
	print(endTime - startTime)
except:
	# error getting image
	print("An error occured in writing the output image path")
	sys.exit()
