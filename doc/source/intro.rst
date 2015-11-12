About VMRC
=============

VMRC is client-server system (based on Web Services) to index and store Virtual Machine Images (VMI)
 along with its metadata (OS, applications, etc.). It supports matchmaking to obtain the appropriate VMIs
 that satisfy a given set of hard (must) requirements and soft (should) requirements.

 It is useful as a catalog of VMIs that can  stored on the VMI repository systems of the different Cloud Management Platforms (such as OpenNebula or OpenStack) or on public Clouds (such as Amazon Web Services). This way, customized VMIs are indexed in VMRC and applications can query, as an example, for a VMI based on Ubuntu 12.04 LTS with Java and Octave already installed.

 Current version: 2.1.2

 This repository only includes the VMRC server. Additional packages available in GitHub are:

   - `vmrc-client <http://www.github.com/grycap/vmrc-client>`_ (CLI and Java-based API to access VMRC)

   - `vmrc-web <http://www.github.com/grycap/vmrc-web>`_ (Web-based GUI to access VMRC)

The easiest and fastest way to deploy VMRC is using Docker:

   - `VMRC Docker Image - with GUI (Recommended) <https://hub.docker.com/r/grycap/vmrc-web>`_

   - `VMRC Docker Image - no GUI <https://hub.docker.com/r/grycap/vmrc>`_


