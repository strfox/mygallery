# mygallery

This is a simple self-hosted web interface to browse a multimedia file system.

## Requirements
- Java 11

## Usage

In the working directory, create an UTF-8 encoded text file called `mygallery.properties`.
Example properties:

    mygallery.entriesPerPage=100
    mygallery.galleryPath=/home/michel/Pictures/
  
Start the HTTP server on port 8080 by running:

    ./mvnw spring-boot:run
