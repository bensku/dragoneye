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
This will run fetch dependencies, compile Dragoneye and run all tests.
Finally, an jar with all dependencies is created.

Once done, you should have an executable jar <code>target</code>.
You can run it from command prompt:
```
java -jar target/dragoneye-0.0.1-SNAPSHOT-jar-with-dependencies.jar 
```
Dragoneye stores its data in a file named <code>dragoneye.db</code>
in the current directory.

### Test coverage and code quality
A test coverage report can be created with
```
mvn jacoco:report
```
It will be placed at <code>target/site/jacoco/index.html</code>.

A Checkstyle report can be created with
```
mvn jxr:jxr checkstyle:checkstyle
```
It will be placed at <code>target/site/checkstyle.html</code>. Unfortunately,
the resulting page looks rather ugly.'

### Javadoc
Javadoc of Dragoneye may be generated with
```
mvn javadoc:javadoc
```
You will need to ensure that your <code>JAVA_HOME</code> points to a valid
JDK installation. Even successful generation may produce errors, because
Dragoneye does not use Java Platform Module System.

## Development and Debugging
When Dragoneye crashes, it does not print stack trace by default.
Run it with JVM argument <code>-Ddragoneye.debug</code> to enable
printing stack traces to the console.

Development of Dragoneye has been done in Eclipse, but any IDE or
even a text editor should work.