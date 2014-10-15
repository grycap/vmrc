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
 Developed by the Grid and High Performance Computing Group (GRyCAP)
 Universitat Politecnica de Valencia (UPV)
 http://www.grycap.upv.es/vmrc

 VMRC is client-server system (based on Web Services) to catalog and store Virtual Machine Images (VMI)
 along with its metadata (OS, applications, etc.). It supports matchmaking to obtain the appropriate VMIs
 that satisfy a given set of hard (must) requirements and soft (should) requirements.

 Current version: 2.1.2

 This bundle only includes the VMRC server. Additional packages available are:
   - VMRC Client (CLI and Java-based API to access VMRC Server)
   - VMRCWebGUI (simple web-based frontend to list VMIs from the VMRC Server with a web-browser)  

1. Features
===========
+ Multi-user support.
+ Fine-grained authorization (per User and per VMI).
+ Hypervisor-agnostic
+ OVF support


 2. Compilation
 ===============


 2.1  Requirements
 ----------------
  + Java JDK 1.7+
  + Maven (to compile from sources and generate the WAR file)  
  + Apache Tomcat (to deploy the WAR). Tested on version 7+.
  + (Optional) An SQL-based database (tested on MySQL 5+)

  VMRC server can operate with an in-memory database provided by HSQLDB (just for testing).

 2.2 Compilation and Installation (from sources)
 ---------------------------------------------

1. unzip package
2. Compile from sources with the command: mvn2 package -Dmaven.test.skip=true (the generated war file is located in target/vmrc.war)
3. Deploy vmrc.war into Apache Tomcat (simply drop the war file into Tomcat's webapps folder)
4. Start Apache Tomcat
5. VMRC server's endpoint should be available at http://your_ip:8080/vmrc/vmrc (no GUI, just a WS endpoint)
6. If you want a web-based simple front-end, consider installing VMRCWebGUI  
7. Check the VMRC server's manual available at the website http://www.grycap.upv.es/vmrc for further configuration



 3. Configuration
==================

The VMRC Server is pre-configured with an in-memory HSQLDB-based database. This should be employed for testing purposes.
If you want to use another SQL-based backend, proceed accordingly by configuring
[TOMCAT_HOME]/webapps/vmrc/WEB-INF/classes/hibernate.cfg.xml (MySQL-based configuration is provided for your convenience)
