# Bubble Docs

Welcome to the base directory of the **Bubble** **Docs** **Application**.

The application's folders are the following:
* _bubbledocs-appserver_: contains the application server;
* _docs_: contains the reports for DS;
* _info_: contains the work scheduled for the forth release;
* _juddi-server_: contains the juddi server;
* _kerberos-lib_: contains the kerberos protocol;
* _KeyManager_: contains a manager to the kerberos keys;
* _PersistenceManager_: contains a class to deal with persistency;
* _sd-id_: contains the distributed authentication server;
* _sd-id-cli_: contains the client to test the sd-id server;
* _sd-store_: contains the reliable documents storage server;
* _sd-store-cli_: contains the client to test the sd-store server;
* _uddi-naming_: contains the uddi server; and
* _ws-handlers_: contains the web services handlers.

Enter inside the desired folder for further information. You can also visit the [wiki](https://github.com/tecnico-softeng-distsys-2015/A_05_09_22-project/wiki).

***

# Instructions for the discussion

## Requirements
* Java 1.8;  
* Tomcat;  
* MySQL server; and  
* 3 consoles opened in the directory of the project.

## Procedure

# Tomcat
1 - Start Tomcat from one of the consoles.  

# MySQL
2.1 - Start MySQL with the following credentials: user _root_ and passord _rootroot_.  
2.2 - Create a database named _bubbledb_.  

# Installing the project
3.1 - On the first console, do the following:  
> cd PersistenceManager  
> mvn clean install  
> cd ../kerberos-lib  
> mvn clean install  

3.2 - On the second console, do the following:  
> cd sd-id  
> mvn clean compile exec:java  

3.3 - On the third console, do the following:  
> cd sd-store  
> mvn clean compile exec:java  

3.4 - On the first console, do the following:  
> cd ..  
> mvn clean install  

It's OK if the last build fails. Our team couldn't end up the integration between both SE and DS projects.  

3.5 - On the second and the third console, press _enter_ to shutdown each of them.  

### SE discussion
4.1 - On the first console, do the following:  
> cd bubbledocs-appserver/  
> mvn clean test  

This is the test of the local functionalities.  
You should have the following results:  
* Tests run: 182, Failures: 0, Errors: 0, Skipped: 0  

Now, there stories to show the flux of the program. You can choose from the following instructions the one you want and read them closely.  
4.2 - On the first console, choose and do one of the following:

* This story, tests the functionalities of the project that are related with the management of users, their information and sessions in the application.  
> mvn clean test -Dtest=LocalStoryIDTest  

* This story, tests the functionalities of the project that are related with the creation and edition of documents in the application.  
> mvn clean test -Dtest=LocalStoryStoreTest  

* This story, tests the functionalities of the project that where required in the first release.  
> mvn clean test -Dtest=LocalStoryMainTest  

There are integration tests too.

4.3 - On the second console, do the following:  
> mvn clean compile exec:java  

4.4 - On the third console, do the following:  
> mvn clean compile exec:java  

4.5 - On the first console, choose and do one of the following:  
> mvn clean verify  

Again, there will be test failures.  

4.6 - On the second and the third console, press _enter_ to shutdown each of them.  

### DS discussion
Firstly, we show the SD-ID component.  
5.1 - On the second console, do the following:  
> mvn clean compile exec:java  

5.2 - On the first console, do the following:  
> cd ../sd-id-cli  
> mvn clean test  

Finally, we show the SD-Store component  
5.3 - On the third console, do the following:  
> mvn clean compile exec:java  

5.4 - On the first console, do the following:   
> cd ../sd-store-cli  
> mvn clean test  

___________________

Revision: Tomás Alves @ 2015-05-24
