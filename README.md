# Photo Gallery

I recently went through an interview process with a company where I had to make a Flickr client as a take home assignment.
It was a funny coincidence, since in the 5th edition of the Big Nerd Ranch Android guide (which I wrote), one of the projects is a Flickr client.

Anyways, this is what I submitted.
The project in the book used the legacy toolkit, but this one is using all the latest and greatest tools and libraries.
It uses many of Google's libraries including: ViewModel, Hilt, and Navigation.
It uses third party libraries including: KotlinX DateTime, Coil, Turbine, and Telephoto.
Due to a limitation somewhere in Gradle, KMP, ???, there are both instrumented and plain unit tests.

In addition to all the base requirements, I also added:
- infinite scrolling
- pinch and zoom for the image on the detail page (very basic implementation here)
- rotation support (even better, my app handles process death!)

Thanks!
