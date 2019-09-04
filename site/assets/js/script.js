// when page is loaded, display container and site
window.addEventListener("load", () => {
	document.querySelector("body > div.container").style.display = "inline-block";
});

// function that takes a given data URL for an image, and then processes and displays generated Visions for the image
const generateAndDisplayVisionsFromURL = (dataURL) => {
	// start loading
	document.querySelector("body > div.container > div.app > div.results").classList.add("loading");

	// remove any existing landscape display class(es)
	document.querySelector("body > div.container > div.app > div.results > div.output_photos_container").classList.remove("landscape");

	// remove any existing photos
	document.querySelector("body > div.container > div.app > div.results > div.output_photos_container").innerHTML = "";

	// send request to PHP with dataURL, and process results
	let phpAppRequest = new XMLHttpRequest();
	phpAppRequest.open("POST", "app.php", true);

	// set request header
	phpAppRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

	// process request when response is received
	phpAppRequest.onload = () => {
		if(phpAppRequest.status === 200) {
			// get list of outputted image paths and store in var
			const outputtedImagePaths = JSON.parse(phpAppRequest.response);

			// for each outputted image, display on page
			outputtedImagePaths.forEach((imagePath) => {
				document.querySelector("body > div.container > div.app > div.results > div.output_photos_container").innerHTML += `<div class='image_container'><img src="outputted_images/${imagePath}" alt="Generated Vision" onload="if(this.clientHeight < this.clientWidth) { this.parentElement.parentElement.classList.add('landscape'); }"><a class="download_image" href="outputted_images/${imagePath}" download><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path d="M14 10h5l-7 8-7-8h5v-10h4v10zm4.213-8.246l-1.213 1.599c2.984 1.732 5 4.955 5 8.647 0 5.514-4.486 10-10 10s-10-4.486-10-10c0-3.692 2.016-6.915 5-8.647l-1.213-1.599c-3.465 2.103-5.787 5.897-5.787 10.246 0 6.627 5.373 12 12 12s12-5.373 12-12c0-4.349-2.322-8.143-5.787-10.246z"/></svg></a></div>`;
			});

			// stop loading
			document.querySelector("body > div.container > div.app > div.results").classList.remove("loading");
		} else {
			// yield error as the request failed
			alert("Server Error.");
		}
	}

	// send request with encoded image data
	phpAppRequest.send("input_image=" + (dataURL.replace(/\+/g, ":")));
};

// when "Generate New Sets of Visions for Current Picture" is clicked, reload Visions
document.querySelector("body > div.container > div.app > div.results > button.action_btn.generate_new_visions").addEventListener("click", () => {
	generateAndDisplayVisionsFromURL( imageInputBox.getFile().getFileEncodeDataURL() );
});

// when "Generate Visions for Different Picture" is clicked, open input box again
document.querySelector("body > div.container > div.app > div.results > button.action_btn.generate_for_new_picture").addEventListener("click", () => {
	imageInputBox.browse();
});

// Vision uses FilePond for HTML file input. We implement it here:

// FilePond plugins
FilePond.registerPlugin(FilePondPluginFileEncode);

// create FilePond for image input box
const imageInputBox = FilePond.create(
	document.querySelector("body > div.container > div.app > div.image_input_container > input[type=file]"),
	{
		labelIdle: "Drag & Drop input image or <span class='filepond--label-action'> Browse File System </span>",
		onaddfile: (error, file) => {
			// file has been added to input box

			// if no error, process file
			if(!error) {
				// display results section and don't show image input box
				document.querySelector("body > div.container > div.app > div.image_input_container").style.display = "none";
				document.querySelector("body > div.container > div.app > div.results").style.display = "block";

				// variable for encoded image data
				const encodedImageData = file.getFileEncodeDataURL();

				// generate and display visions for encoded image variable
				generateAndDisplayVisionsFromURL(encodedImageData);
			}
		}
	}
);
