# Bubble Docs SD-ID

The **SD-ID** is a service of distributed authentication used by the **BubbleDocs**'s users. Each user has an account saved in the server and it allows that user to authenticate to the **BubbleDocs** to edit his/her documents. More than that, this account grants the BubbleDocs the permission to execute other external services (e.g. [SD-STORE](https://github.com/tecnico-softeng-distsys-2015/A_05_09_22-project/wiki/SD_STORE)) on behalf of the user.

It's main objective is to prevent users from having to maintain multiple accounts (and respective secret passwords), one for each service or application that the user might use. At the same time, the **SD-ID** facilitates the development of new services and applications.

***

# Projecto de Sistemas Distribuídos #

## Primeira entrega ##

Grupo de SD 9

75624 - Andriy Zabolotnyy - andriymz@hotmail.com

75666 - Pedro Joaquim - pmj.tic@hotmail.com

76366 - Diogo Calado - dmcalado@gmail.com


Repositório:
[tecnico-softeng-distsys-2015/A_05_09_22-project](https://github.com/tecnico-softeng-distsys-2015/A_05_09_22-project/)


-------------------------------------------------------------------------------

## Service SD-ID


### Installing Instructions (in Portuguese): ###

[0] Iniciar sistema operativo

Selecionar Linux

[1] Iniciar servidores de apoio

JUDDI:
> startup.sh

[2] Criar pasta temporária

> cd Desktop
> mkdir project
> cd project

[3] Obter versão entregue

> git clone -b SD-ID_R_1 https://github.com/tecnico-softeng-distsys-2015/A_05_09_22-project/


[4] Construir e executar **servidor**

> cd  A_05_09_22/sd-id/
> mvn clean package 
> mvn exec:java


[5] Construir **cliente**

> cd  A_05_09_22/sd-id-cli/
> mvn clean package
> mvn exec:java


-------------------------------------------------------------------------------

### Test Instructions (in Portuguese): ###
*(Como verificar que todas as funcionalidades estão a funcionar correctamente)*


[1] Executar **testes do servidor** ...

> cd  A_05_09_22/sd-id/
> mvn test


[2] Executar **cliente de testes**

> cd A_05_09_22/sd-id/
> mvn exec:java


> cd A_05_09_22/sd-id-cli/
> mvn test

-------------------------------------------------------------------------------

### Other informations ###

This is a Java Web Service client

The client uses the wsimport tool to generate classes that can invoke the web service and perform the Java to XML data conversion.

The client needs access to the WSDL file, either using HTTP or using the local file system.

### Instructions using Maven:

You must start sd-id first.

The default WSDL file location is ${basedir}/../sd-id/src/main/resources, i.e. the WSDL is retrieved from the server sources, assuming they are next to the client project.

The WSDL could be copied to the client project and stored at ${basedir}/src/main/resources .

The WSDL location is specified in pom.xml
/project/build/plugins/plugin[artifactId="jaxws-maven-plugin"]/configuration/

The jaxws-maven-plugin is run at the "generate-sources" Maven phase (which happens before the compile phase).

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

### Project's Hierarchy
    .
    |__ sd-id
        |__ pom.xml
        |__ README.md
        \__ src
            |__ main
            |   |__ java
            |   |   \__ pt
            |   |       \__ ulisboa
            |   |           \__ tecnico
            |   |               \__ sdis
            |   |                   \__ id
            |   |                       |__ domain
            |   |                       \__ ws
            |   |                           |__ impl
            |   |                           \__ uddi
            |   \__ resources
            \__ test
                \__ java
                    \__ pt
                        \__ ulisboa
                            \__ tecnico
                                \__ sdis
                                    \__ id
                                         |__ domain
											  \__ ws
											       \__ impl

___________________

Revision: Tomás Alves @ 2015-04-17
