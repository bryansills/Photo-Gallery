# Photo Gallery

I recently went through an interview process with a company where I had to make a Flickr client as a take home assignment.
It was a funny coincidence, since in the 5th edition of the Big Nerd Ranch Android guide (which I wrote), one of the projects is a Flickr client.

Anyways, this is what I submitted.
The project in the book used the legacy toolkit, but this one is using all the latest and greatest tools and libraries.
It uses many of Google's libraries including: ViewModel, Hilt, and Navigation.
It uses third party libraries including: KotlinX DateTime, Coil, Turbine, and Telephoto.
Due to a limitation somewhere in the intersection of Gradle and KMP, there are both instrumented and plain unit tests.

In addition to all the base requirements, I also added:
- infinite scrolling
- pinch and zoom for the image on the detail page (very basic implementation here)
- rotation support (even better, my app handles process death!)

Thanks!

## Setup

In the file [FlickrService.kt](app/src/main/java/ninja/bryansills/photogallery/network/FlickrService.kt), add your Flickr API key to the `FlickrModule.provideFlickr()` function.

## So you want to give me a job...

Companies love to give coding assignments to people when they are interviewing for jobs and I've done my fair share.
Here are a few projects of mine that I think do a good job of showing off my technical skills as an Android developer:

- [Photo Gallery](https://github.com/bryansills/Photo-Gallery) (this project): A Flickr client. Based on a project in the 5th edition of Big Nerd Ranch's Android guide (which I wrote), this Jetpack Compose implementation shows off infinite paging and clean unit tests.
- [Loud Ping](https://github.com/bryansills/LoudPing/): An unwieldy side project of mine trying to help me have a better relationship with the music I listen to. The UI side of things may be a bit basic, but I'm doing a lot of neat things in the Gradle world and one of my more thoroughly architected apps.
- [AllTrails](https://github.com/bryansills/AllTrails): An app to search for nearby restaurants to have lunch. The best app for showing off my UI skills.
- [Coda Pizza Multiplatform](https://github.com/bryansills/CodaPizzaMultiplatform): A multiplatform pizza customizing app. Based on a project in the 5th edition of Big Nerd Ranch's Android guide (which I wrote), I wrote and presented this Kotlin multiplatform app as a codelab at KotlinConf 2023 in Amsterdam.
