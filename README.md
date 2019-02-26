# OPC to REST provider in Java

This project provides a proof-of-concept OPC to REST variable access for the Arrowhead Framework Generation 4.0. In order to run this, you must:
1. Install the [Arrowhead Core Systems](www.github.com/arrowhead-f/core-java). 
2. Clone this  repository and update the config files (/provider/config/default.conf and /tester/config/default.conf) to properly reflect the IP:s and ports of the Core systems installed in step 1.
3. Build this repository using Maven (run mvn install)


### Project structure

This is a multi module maven project, hereby the lists of all the modules and common dependencies.


* **client-common**: a common library module for the other maven modules. Contains all the data transfer objects and common dependencies.
* **provider**: Provider skeleton which registers the OPC to REST service into the Service Registry and runs a web server where the service is available
* **tester**: provides a client skeleton to test the OPC to REST service

### Authors

Niklas Karvonen - Luleå University of Technology (Github: nenovrak) 
Diego Rovere TTS Network ()
