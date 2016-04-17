# Bubble Docs Application Server

The **BubbleDocs** application's main goal is to support the creation, management, edition of various types of documents: spreadsheets, text and presentation. The users can interact amongst themselves through the edition of permissions to access specific files. Every user is identified by his username and password, which are an exclusive pair, and has permissions to read and/or write documents.

### Instructions using Maven:

To compile:
mvn compile

To run using exec plugin:
mvn exec:java

To test:
mvn test

### To configure the project in Eclipse:

If Eclipse files (.project, .classpath) exist: 'File', 'Import...', 'General'-'Existing Projects into Workspace' 'Select root directory' and 'Browse' to the project base folder.

Check if everything is OK and 'Finish'.

If Eclipse files do not exist:
'File', 'Import...', 'Maven'-'Existing Maven Projects'.
'Browse' to the project base folder.
Check that the desired POM is selected and 'Finish'.

To run:
Select the main class and click 'Run' (the green play button).
Specify arguments using 'Run Configurations'

***

### Project's hierarchy

    .
    |__ bubbledocs-appserver
        |__ .gitignore
        |__ README.md
        |__ pom.xml
        \__ src
             |__ main
             |   |__ dml
             |   |   \__ bubbledocs.dml
             |   |__ java
             |   |   \__ pt
             |   |       \__ tecnico
             |   |          \__ bubbledocs
             |   |              |__ BubbleDocsApplication.java
             |   |              |__ domain
             |   |              |__ exception
             |   |              |__ service
             |   |              |   \__ remote
             |   |              \__ toolkit
             |   \__ resources
             |      |__ fenix-framework-jvstm-ojb.properties
             |      \__ log4j.properties
             \__ test
                \__ java
                    \__ pt
                        \__ tecnico
                            \__ bubbledocs
                                \__ service
___________________

Revision: Tomás Alves @ 2015-04-16