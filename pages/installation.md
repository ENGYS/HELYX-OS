---
layout: page-fullwidth
header:
    image_fullwidth: 'meshHeader-scaled2-lighter.png'
title: "Installation of the Latest 64 Bit Linux Binaries"
teaser: ""
permalink: /installation/
---

HELYX-OS v2.4.0 is designed to work only with [OpenFOAM v4.1](http://www.openfoam.org/archive/4.1/download/source.php) or OpenFOAM v1606+.  The HELYX-OS installation instructions will vary slightly, depending on which version of OpenFOAM you have installed.  Additionally, users are free to download the HELYX-OS source and compile separately.  See the following sub sections for more information:

- [HELYX-OS Installation for OpenFOAM v4.1](#v41)
- [HELYX-OS Installation for OpenFOAM v1606+](#v1606)
- [HELYX-OS Installation from source](#fromSource)

Please report all bugs encountered with HELYX-OS on the [project issue tracker](https://github.com/ENGYS/HELYX-OS/issues).  

## <a name="v41"></a>HELYX-OS Installation for OpenFOAM v4.1 
1.  Before you proceed, please make sure you have a working version of [OpenFOAM v4.1](http://www.openfoam.org/archive/4.1/download/source.php).  If you are using Ubuntu 14.04 or greater, the installer provided can download and install the OpenFOAM v4.1, as explained below during steps 8 to 10.

2.  Download the [latest 64 Bit binary of HELYX-OS](https://github.com/ENGYS/HELYX-OS/releases/download/v2.4.0/HELYX-OS-2.4.0-linux-x86_64.bin) from the [project releases](https://github.com/ENGYS/HELYX-OS/releases) or the [project front page](http://engys.github.io/HELYX-OS/).  The installer name will have the form:

        HELYX-OS-<version>-linux-x86_64.bin

3.  Once the binary is downloaded, open a Linux terminal and navigate through the terminal to the location of the newly downloaded HELYX-OS installer.

4.  Change the permissions of the installer by executing ```chmod 755 <installer name>``` in the terminal, similar to:

        :~$ chmod 755 HELYX-OS-2.4.0-linux-x86_64.bin  
   
5.  Start the installer by typing ```./<installer name>``` while in the terminal, similar to:

        :~$ ./HELYX-OS-2.4.0-linux-x86_64.bin     

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
    
11.  To start HELYX-OS in the terminal, execute the ```HELYX-OS.sh``` file located in ```Engys/HELYX-OS/v2.4.0``` directory similar to:

         :~$ ~/Engys/HELYX-OS/v2.4.0/HELYX-OS.sh       

12.  Finally, start a new case and open Edit>Preferences in the menu.  Ensure that you have selected the core folder location of the OpenFOAM V4.1 installation and select "OK" to save your preferences.  You are now ready to start using HELYX-OS

Please report all bugs encountered with HELYX-OS on the [project issue tracker](https://github.com/ENGYS/HELYX-OS/issues).

## <a name="v1606"></a>HELYX-OS Installation for OpenFOAM v1606+

Installing HELYX-OS and using OpenFOAM v1606+ is identical to the sequence listed above, with several additional steps to link to the existing v1606+ installation.  At this point in the installation process, we assume that you are well acquainted with OpenFOAM+ and have a valid installation of v1606+ completed prior to the deployment of HELYX-OS.

### Using OpenFOAM 1606+ and Docker

1.  Open the newly installed HELYX-OS and go to Edit>Preferences
2.  In the "Misc" section check the "Use Docker" tick box and then type "openfoamplus/of_v1606plus_centos66" in the "Docker Image" textbox
3.  Press "OK" to apply settings 

### Using a Compiled Version of OpenFOAM 1606+ (without Docker)

1.  Open the newly installed HELYX-OS and go to Edit>Preferences
2.  Ensure that you have selected the core folder location of the OpenFOAM V1606+ installation
3.  Press "OK" to apply settings 

Please report all bugs encountered with HELYX-OS on the [project issue tracker](https://github.com/ENGYS/HELYX-OS/issues).

## <a name="fromSource"></a>Compilation from Source
For the latest instructions on how to compile HELYX-OS from source, please see the git repository [README](https://github.com/ENGYS/HELYX-OS/blob/master/README.md) file inside the [HELYX-OS repository](https://github.com/ENGYS/HELYX-OS).  Please report all bugs encountered with HELYX-OS on the [project issue tracker](https://github.com/ENGYS/HELYX-OS/issues).
