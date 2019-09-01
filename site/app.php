<?php

// variable for input image base64, which is sent via a POST request --> decode by replacing ":" with "+" in base64
$input_image = str_replace(":", "+", $_POST["input_image"]);

// get base64 text by splitting input base64 with ",", and getting second part
$input_image_base64 = explode(",", $input_image)[1];

// get input image file extension by splitting with ";", then splitting with "/"
$input_image_type = explode("/", explode(";", $input_image)[0])[1];

// generate unique image ID using MD5 function and unique id based on current time
$input_image_id = md5(uniqid());

// input image filename is [image_id].[image_type]
$input_image_filename = $input_image_id . "." . $input_image_type;

// conversion command variable (will be string of command used to generate Visions for input image)

?>