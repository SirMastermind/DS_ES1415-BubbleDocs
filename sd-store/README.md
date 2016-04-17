# Bubble Docs SD-STORE

The **SD-STORE** is a reliable documents storage service. The main concern of **SD-STORE** is to provide users and applications an alternative high storage capacity and high availability in order to safeguard their documents. Each registered user of [SD-ID](https://github.com/tecnico-softeng-distsys-2015/A_05_09_22-project/wiki/SD_ID) can safeguard reliable copies of his/her documents. If necessary (e.g. in case of loss or corruption of a document), any user can retrieve the latest copy of his/her documents.


***

# Projecto de Sistemas Distribuídos #

## Segunda entrega ##

Grupo de SD 22

75541 - Tomás Alves - sir.mastermind94@gmail.com

75551 - Miguel Santos - miguel_sl23@hotmail.com

76231 - Tiago Rechau - tiago.rechau@sapo.pt


Repositório:
[tecnico-softeng-distsys-2015/A_05_09_22-project](https://github.com/tecnico-softeng-distsys-2015/A_05_09_22-project/)


-------------------------------------------------------------------------------

## Service SD-STORE


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

> git clone -b SD-STORE_R_1 https://github.com/tecnico-softeng-distsys-2015/A_05_09_22-project/


[4] Construir e executar **servidor**

> cd  A_05_09_22/sd-store/
> mvn clean package 
> mvn exec:java


[5] Construir **cliente**

> cd  A_05_09_22/sd-store-cli/
> mvn clean package
> mvn exec:java


-------------------------------------------------------------------------------

### Test Instructions (in Portuguese): ###
*(Como verificar que todas as funcionalidades estão a funcionar correctamente)*


[1] Executar **testes do servidor** ...

> cd  A_05_09_22/sd-store/
> mvn test


[2] Executar **cliente de testes**

> cd A_05_09_22/sd-store/
> mvn exec:java


> cd A_05_09_22/sd-store-cli/
> mvn test

[3] Instalar todos os servidores e serviços
> cd  A_05_09_22/sd-id/
> mvn clean install

-------------------------------------------------------------------------------

### Other informations ###

This is a Java Web Service client

The client uses the wsimport tool to generate classes that can invoke the web service and perform the Java to XML data conversion.

The client needs access to the WSDL file, either using HTTP or using the local file system.

### Instructions using Maven:

You must start sd-store first.

The default WSDL file location is ${basedir}/../sd-store/src/main/resources, i.e. the WSDL is retrieved from the server sources, assuming they are next to the client project.

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

___________________

Revision: Tomás Alves @ 2015-05-13