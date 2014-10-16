VMRC - Virtual Machine image Repository &amp; Catalog
====

<!-- language: lang-none -->
    ____   ____  ____    ____  _______      ______  
    |_  _| |_  _||_   \  /   _||_   __ \   .' ___  |
     \ \   / /    |   \/   |    | |__) | / .'   \_|
      \ \ / /     | |\  /| |    |  __ /  | |
       \ ' /     _| |_\/_| |_  _| |  \ \_\ `.___.'\
        \_/     |_____||_____||____| |___|`.____ .'


 A Virtual Machine Image Repository & Catalog
 Developed by the [Grid and High Performance Computing Group (GRyCAP)](http://www.grycap.upv.es) at the
 [Universitat Politecnica de Valencia (UPV)](http://www.upv.es).

 Web page: http://www.grycap.upv.es/vmrc

0. Introduction
===============
 VMRC is client-server system (based on Web Services) to index and store Virtual Machine Images (VMI)
 along with its metadata (OS, applications, etc.). It supports matchmaking to obtain the appropriate VMIs
 that satisfy a given set of hard (must) requirements and soft (should) requirements.

 It is useful as a catalog of VMIs that can  stored on the VMI repository systems of the different Cloud Management Platforms (such as OpenNebula or OpenStack) or on public Clouds (such as Amazon Web Services).
 This way, customized VMIs are indexed in VMRC and applications can query, as an example, for a VMI based on Ubuntu 12.04 LTS with Java and Octave already installed.

 Current version: 2.1.2

 This repository only includes the VMRC server. Additional packages available in GitHub are:
   - [vmrc-client](http://www.github.com/grycap/vmrc-client) (CLI and Java-based API to access VMRC)
   - [vmrc-web](http://www.github.com/grycap/vmrc-web) (simple web-based frontend to list VMIs from the VMRC Server with a web-browser)  

1. Features
===========
+ Multi-user support.
+ Fine-grained authorization (per User and per VMI).
+ Hypervisor-agnostic
+ OVF support


 2. Compilation & Installation
 ===============
VMRC has been developed in Java using Spring, Hibernate and an SQL-based backend (such as MySQL or HSQLDB).
It is a web service that has to be deployed in an application server (such as Apache Tomcat).

 2.1  Requirements
 ----------------
+ Java JDK 1.7+
+ Maven (to compile from sources and generate the WAR file)  
+ Apache Tomcat (to deploy the WAR). Works on version 7+ up to 7.0.28 included.
+ (Optional) An SQL-based database (tested on MySQL 5+)

VMRC server can operate with an in-memory database provided by HSQLDB (just for testing).

 2.2 Compilation and Installation (from sources)
 ---------------------------------------------
1. clone this repository:
```
git clone https://github.com/grycap/vmrc.git
```

2. Compile from sources with the command:
```
mvn2 package -Dmaven.test.skip=true
```
The generated WAR file will be located in target/vmrc.war
3. Deploy vmrc.war into Apache Tomcat (simply drop the war file into Tomcat's webapps folder).
4. Start Apache Tomcat
5. VMRC server's endpoint should be available at http://your_ip:8080/vmrc/vmrc (no GUI, just a WS endpoint)
6. If you want a web-based ultra-simple front-end, consider installing [vmrc-gui](http://www.grycap.upv.es/grycap/vmrc-web)  
7. Check the VMRC server's manual available at the website http://www.grycap.upv.es/vmrc for further configuration



 3. Configuration
==================

The VMRC Server is pre-configured with an in-memory HSQLDB-based database. This should be employed for testing purposes.
If you want to use another SQL-based backend, proceed accordingly by configuring
``
[TOMCAT_HOME]/webapps/vmrc/WEB-INF/classes/hibernate.cfg.xml
``

A MySQL-based configuration is provided for your convenience)
