# Dragoneye
Dragoneye is a helper application for RPG game masters. It allows quick,
structured note-taking *during* games. Later, the GM may revisit the notes
and move important information to somewhere else.

The project was created for course "Ohjelmistotekniikka". Currently, it is
a minimal viable product; you can use it, and core functionality is there.
However, for real use, a good general purpose note-taking application is
better.

## Requirements
Dragoneye requires Java 11, which you can download from
[AdoptOpenJDK](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot).
University computers, at least VDI, has Java 11 readily available, no need
to download anything there.

As binary packages are not currently provided, you'll also need a reasonably
new version of Maven. University computers, again, have it.

## Installation
Clone this repository and run Maven there:
```
mvn package
```
This might take a few minutes, because Maven will need to download all
dependencies of Dragoneye.

Once done, you should have an executable jar <code>target</code>.
You can run it from command prompt:
```
java -jar target/dragoneye-0.0.1-SNAPSHOT-jar-with-dependencies.jar 
```
Dragoneye stores its data in a file named <code>dragoneye.db</code>
in the current directory.