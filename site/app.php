<?php

// set content type of page to JSON
header('Content-Type: application/json');

// variable for input image base64, which is sent via a POST request --> decode by replacing ":" with "+" in base64
$input_image = str_replace(":", "+", $_POST["input_image"]);

// get input image file extension by splitting with ";", then splitting with "/"
$input_image_type = explode("/", explode(";", $input_image)[0])[1];

// generate unique image ID using MD5 function and unique id based on current time
$input_image_id = md5(uniqid());

// input image filename is [image_id].[image_type]
$input_image_filename = $input_image_id . "." . $input_image_type;

// write image to input image filename
file_put_contents("inputted_images/" . $input_image_filename, file_get_contents($input_image));

// amount of visions to generate
define("AMOUNT_OF_VISIONS_TO_GENERATE", 3);

// output image filenames
$output_image_filenames = array();

// generate output image filenames with [image_id]_[count].[image_type]
for($i = 0; $i < AMOUNT_OF_VISIONS_TO_GENERATE; $i++) {
	array_push($output_image_filenames, $input_image_id . "_" . $i . "." . $input_image_type);
}

// conversion command variable (will be string of command used to generate Visions for input image)
$conversion_command = "";

// generate conversion command
for($visionCount = 0; $visionCount < AMOUNT_OF_VISIONS_TO_GENERATE; $visionCount++) {
	// if not first vision, add " & " to command at the beginning
	if($visionCount > 0) {
		$conversion_command .= " & ";
	}

	// add conversion command for current vision to conversion command var
	$conversion_command .= "python3 inputted_images/" . $input_image_filename . " outputted_images/" . $output_image_filenames[$visionCount];
}

// run conversion command
shell_exec($conversion_command);

// echo JSON of output image URLs
echo json_encode($output_image_filenames);
?>