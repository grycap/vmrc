
VMRC Client
===============

Introduction
---------------

VMRC (Virtual Machine image Repository & Catalog) is client-server system (based on Web Services) to index Virtual Machine Images (VMI) along with its metadata (OS, applications, etc.). 
It supports matchmaking to obtain the appropriate VMIs  that satisfy a given set of hard (must) requirements and soft (should) requirements.

This section describes the command-line client (CLI)  of the VMRC.


Pre-requisites
------------------

The binary version of the VMRC client has been compiled with Java JDK 1.7+. Therefore, it requires a JRE (Java Runtime Environment) 1.7+. The source version might be compiled with Java JDK 1.5+. 
Make sure you have the environment variable JAVA_HOME correctly configured before attempting these steps. This guide assumes that you will use a Unix-based OS to run the VMRC client (such as GNU/Linux or OS/X). 
Since it has been developed in Java it might work in other operating systems.

Assumptions
-----------------

This guide assumes that a VMRC server has been properly configured at the deployed_vmrc host (by default `localhost`). Therefore, the following URI is available:

  - VMRC WS endpoint: http://the_deployed_vmrc_host:8080/vmrc/vmrc

  The following URI might also be available in case you installed the GUI:

    - VMRC Web GUI: http://the_deployed_vmrc_host:8080/vmrc-web-gui

Notice that if an HTTPS connector has been defined in Tomcat, then the following URLs can also be employed: 

  - VMRC GUI (HTTPS): https://the_deployed_vmrc_host:8444/vmrc-web-gui

  - VMRC WS endpoint: https://the_deployed_vmrc_host:8444/vmrc/vmrc

The vmrc-web-gui is not required for the client to work. Only the server is required. 

The command line client can seamlessly work with HTTP or HTTPS. Use whichever you want. If the VMRC server and client are located on the same machine, you can safely use localhost in the previous URLs. 

The VMRC client can seamlessly run from any platform with Java support. This includes GNU/Linux, OS X and Windows platforms. Being developed in Java, it should work on other platforms as well.

Deploying the VMRC Client
----------------------------------

1. The recommended approach to install the VMRC Client is to check out the latest version from the GitHub repository

  a. git clone https://github.com/grycap/vmrc-client.git

2. Alternatively, you can download a pre-compiled version of the VMRC client from the web page.

  a. Download the vmrc-client-<version>.bin.tar.gz from the aforementioned web site.

  b. Unpack the package into a destination folder

    b.i. tar zxvf vmrc-client-<version>.bin.tar.gz

We will assume that the VMRC client has been uncompressed (or checked out) at the $HOME/vmrc-client folder.

3. Set the appropriate environment variables.

  a. Add at the end of the $HOME/.bashrc file the following lines:
    
    export VMRC_CLIENT_LOCATION=$HOME/vmrc-client

    export PATH=$PATH:$VMRC_CLIENT_LOCATION

  b. Reload this configuration

    b.i. source $HOME/.bash

4. Set the CLI scripts executable

  a. chmod u+x $VMRC_CLIENT_LOCATION/tool/`*`.sh

5. Setup your user credentials

  a. Create the file $HOME/.vmrc/vmrc_auth with your credentials

    johndoe:mypassword

    Notice that the johndoe user must exist in the VMRC server. If no valid credentials are supplied, the VMRC client defaults to the “anonymous” user which has reduced privileges. If you do not have your user credentials yet, review the section “Operations performed by the Administrator” to learn how to create them.

6. Verify that the remote VMRC Server catalog can be contacted.

  Issue the following command, where VMRC_URL=https://the_deployed_vmrc_host:8444/vmrc/vmrc:

    ./vmrc.sh --list --uri $VMRC_URL

  If no error arises, then the VMRC client has been successfully deployed. 

  You might receive Log4J related errors and warnings but you can safely ignore them. 

  In Windows, you can invoke the CLI as follows

    java –jar vmrc-client.jar --list

  If you want to avoid specifying the VMRC_URL in each CLI invocation, you can define the $HOME/.vmrc/vmrc.properties file with the following content:

    vmrc.uri = https://the_deployed_vmrc_host:8444/vmrc/vmrc
 
  This way, the command can be issued as follows:

    ./vmrc.sh --list 


Using the VMRC Client
-----------------------------

The following commands assume these environment variables properly defined with your specific configuration. In particular, remember that the machine icaro.i3m.upv.es does not exist:

  export VMRC_URL=https://icaro.i3m.upv.es:8444/vmrc/vmrc

  export JAVA_HOME=/usr/lib/jvm/java-6-openjdk/

Adding a VMI to the catalogue without uploading the VMI files
---------------------------------------------------------------------------

This method creates a new entry in the catalogue to describe a VMI. This operation does not involve any file uploading to the VMRC repository.

  ./vmrc.sh --add ../src/test/vmis/sample1.vmi

The file my_vmi.vmi describes the VMI. This is a sample file:

 .. code::

    system.name = MyImage7

    system.hypervisor = vmware

    system.location = /opt/vm_images/dummy_img.qcow2

    cpu.arch = i686

    disk.size = 5000

    disk.os.name = Linux
 
    disk.os.flavour = Ubuntu

    disk.os.version = 11.15
  
    lllldisk.os.credentials.user = user2
  
    disk.os.credentials.password = passwd2
  
    disk.applications contains (name = com.mathworks.matlab, version = 8.0 )
  
    disk.applications contains (name = net.nbcr.opal, version = 2.2 )
  
    disk.applications contains (name = com.java, version = 1.6, path = /usr/local/bin/java )

........

Uploading the VMI files to the entry of the catalogue
--------------------------------------------------------------

This method uploads the file related to a VMI into VMRC. VMware disks might be split in different files whereas KVM image files are just a single file. Therefore, VMWare disks should be compressed in a bundle (a single file) before uploading the file to VMRC.

  ./vmrc.sh --upload $HOME/images/myvmi.img  --vmi MyImage7 

List all the VMI entries in the catalogue
-----------------------------------------------

This method lists all the VMI entries in the catalogue that can be listed considering the credentials supplied by the client. It obtains an XML description of the VMI entries.

  ./vmrc.sh --list 

Search for the Most Appropriate VMIs
-----------------------------------------------

This method searches for the most appropriate VMIs in the VMRC catalogue that satisfy the requirements imposed by the user. Hard requirements will certainly be met by the VMI. Soft requirements will also be considered according to the user ranking. It obtains a ranked XML description of the (up to 10) VMIs that satisfy those requirements.

  ./vmrc.sh --search req1.vmiq 

The specified file expresses the requirements that the VMI should met in order to be listed. Here comes a sample requirements file:

 .. code::
   
    system.hypervisor = kvm

    cpu.arch = i686

    disk.os.name = Linux

    disk.os.flavour = Ubuntu

    disk.os.version >= 11.15

    disk.applications contains (name = com.java, version >= 1.6)

    soft 25 disk.applications contains (name = net.nbcr.opal, version > 2.0) 

........

By default, requirements are considered ‘hard ‘ and these must be satisfied by the VMI. The soft requirements can be ranked by the user. If you need further information about this language, please refer to specific document that describes it.

Download the VMI image files to a local directory
-----------------------------------------------------------

This method downloads the specified VMI to a local directory in the client machine. The VMI should be stored in the VMRC repository. Otherwise, this command will fail.

  ./vmrc.sh --download /tmp/my_img.img --vmi MyImage7 

Removing a VMI from the catalogue
--------------------------------------------

This command deletes an entry in the VMRC catalogue. If there is a related VMI image in the repository it also deletes it.

  ./vmrc.sh --delete MyImage7 

Managing Permissions to VMI entries
-----------------------------------------------

Please refer to the VMRC Server document for further information about the permission model.

This is the syntax of the command: 

  ./vmrc.sh ---vmiAcl  <vmi_name> <operation> <perm>

Where operation=[list | search | upload | download | delete | add ] and perm = [owner | all] 

Operations performed by the Administrator
-----------------------------------------------------

All the following commands require using the $VMRC_SERVER_HOME/bin/vmrc-admin.sh tool and admin client-side credentials. In a Windows platform, the admin CLI tool can be invoked as follows:

  java –cp  vmrc-client.jar org.grycap.vmrc.client.cmd.admin.VMRCAdminCLI ---aduser john johndoe

These commands can be executed from any machine with network access to a VMRC Server, not only from the machine that hosts the VMRC Server.

Add a new User to the VMRC catalog
----------------------------------------------

To add a new user called john with password johndoe, you have to issue the following command:

  $VMRC_SERVER_HOME/bin/vmrc-admin.sh --adduser john johndoe

Delete a User from the VMRC catalog
----------------------------------------------

To delete user john, the following command is required:

  $VMRC_SERVER_HOME/bin/vmrc-admin.sh --deleteUser john 

Obtaining a list of Users from the VMRC catalog
-----------------------------------------------------------

To obtain a list of users from the VMRC catalog, yo can use the following command:

  $VMRC_SERVER_HOME/bin/vmrc-admin.sh --listUsers 
 
You can obtain extended information by producing XML output.

  $VMRC_SERVER_HOME/bin/vmrc-admin.sh --listUsers --xml

Change a User’s ACL
---------------------------

  $VMRC_SERVER_HOME/bin/vmrc-admin.sh –userAcl username operation perm

  Where operation = [add|list|upload|search|delete] perm = [all|owner|none]

Please refer to the VMRC server’s section for a detailed explanation of the permissions model.



















    



















































