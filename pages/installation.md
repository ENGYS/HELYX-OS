---
layout: page-fullwidth
header:
    image_fullwidth: 'meshHeader-scaled2-lighter.png'
title: "Installation of the Latest 64 Bit Linux Binaries"
teaser: ""
permalink: /installation/
---

1.  Before you proceed, please make sure you have a working version of [OpenFOAM-2.4.0](http://www.openfoam.org/archive/2.4.0/download/source.php) installed in your system. HELYX-OS v2.3.1 is designed to work only with this particular version of OpenFOAM. If you are using Ubuntu 14.04 or greater, the installer provided can download and install the proper version of OpenFOAM, as explained below during steps 8 to 10.

2.  Download the [latest 64 Bit binary of HELYX-OS](https://github.com/ENGYS/HELYX-OS/releases/download/v2.3.1/HELYX-OS-2.3.1-linux-x86_64.bin) from the [project releases](https://github.com/ENGYS/HELYX-OS/releases) or the [project front page](http://engys.github.io/HELYX-OS/).  The installer name will have the form:

        HELYX-OS-<version>-linux-x86_64.bin

3.  Once the binary is downloaded, open a Linux terminal and navigate through the terminal to the location of the newly downloaded HELYX-OS installer.

4.  Change the permissions of the installer by executing ```chmod 755 <installer name>``` in the terminal, similar to:

        :~$ chmod 755 HELYX-OS-2.3.1-linux-x86_64.bin  
   
5.  Start the installer by typing ```./<installer name>``` while in the terminal, similar to:

        :~$ ./HELYX-OS-2.3.1-linux-x86_64.bin     

6.  At this point, the installer will begin and the user will be guided through the installation process:

        Waiting for HELYX-OS installer to start...
        This program will install HELYX-OS on your system. Do you want to continue?
        1) Yes
        2) No
        #?        

    In order to continue with the installation, you must type **1** in the terminal and press enter.<br>

7.  The user will be presented with a copy of the Gnu Public License (press enter or space to read through the GPL):

        Do you accept the terms of agreement?
        1) Agree
        2) Exit
        #?

    In order to continue with the installation, you must agree by typing **1** in the terminal and press enter.<br>

8.  Select which components to install:

        Select components to install
        1) HELYX-OS-GUI
        2) HELYX-OS-GUI and Kernel
        #?

    For users with an existing installations of OpenFOAM (refer to step 1 for compatability), select to install only the GUI by typing **1** and hitting enter.  Otherwise, to install the HELYX-OS GUI with the compatible version of OpenFOAM (for Ubuntu 14.04 or later only), type **2** and press enter.

9.  The user will be prompted to enter **the full path** to where HELYX-OS will be installed:

        Select destination folder for HELYX-OS GUI:

    For example, if a user named Scott wants to install HELYX-OS to their home folder (this is the suggested location), then they would enter:

        Select destination folder for HELYX-OS GUI: /home/scott/
    
    and a folder named ```Engys```, containing HELYX-OS will be created in the user's home folder.  The GUI will install and produce the following output:

        Select destination folder for HELYX-OS GUI: /home/scott
        Installing in: /home/scott
        Installing HELYX-OS GUI ................done.

10.  If you previously selected option **2** in step 8, you will now be prompted to select your linux distribution:
        
        Please select your linux distribution:
        1) Ubuntu (14.04 or later)
        2) Other
        #?
        
    Selecting **1** will install the compatible OpenFOAM deb package for 64 bit Ubuntu on 14.04 or later.  For other linux distributions, the compatible version of OpenFOAM must be installed manually by the user.
    
11.  To start HELYX-OS in the terminal, execute the ```HELYX-OS.sh``` file located in ```engys/HELYX-OS/v2.3.1``` directory similar to:

        :~$ ~/Engys/HELYX-OS/v2.3.1/HELYX-OS.sh       

Please report all bugs encountered with HELYX-OS on the [project issue tracker](https://github.com/ENGYS/HELYX-OS/issues).

## Compilation from Source
For the latest instructions on how to compile HELYX-OS from source, please see the git repository [README](https://github.com/ENGYS/HELYX-OS/blob/master/README.md) file inside the [HELYX-OS repository](https://github.com/ENGYS/HELYX-OS).
