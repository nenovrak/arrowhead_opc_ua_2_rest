# OPC to REST provider in Java

This project provides OPC to REST variable access for the Arrowhead Framework Generation 4.0




### Project structure

This is a multi module maven project, hereby the lists of all the modules and common dependencies.


* **client-common**: a common library module for the other maven modules. Contains all the data transfer objects and common dependencies.
* **provider**: client skeleton which registers a specific OPC to REST service into the Service Registry and runs a web server where the service is available
* **tester**: provides a client skeleton to test the OPC to REST service

