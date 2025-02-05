# HELYX-OS
### The HELYX-OS project has been deprecated but you can still download/compile the last version released by ENGYS following the instructions provided in these pages. Please [contact us](https://engys.com/contact-us/) if you want to learn nore about our open-source CFD software tools HELYX and ELEMENTS for enterprise applications.

## About HELYX-OS

HELYX-OS is an open-source Graphical User Interface designed to work natively with OpenFOAM [version 4.1](http://www.openfoam.org/archive/4.1/download/source.php) and OpenFOAM v1606+. The GUI is developed by [ENGYS](http://engys.com/) using Java+[VTK](https://www.vtk.org/) and delivered to the public under the GNU General Public License.
 
HELYX-OS has been created to facilitate the usage of the standard OpenFOAM libraries, by removing the complexity of text based case definition in favour of an intuitive easy-to-use graphical user interface. The GUI is focused on pre-processing tasks, including meshing, case definition and solver execution.  Visit the [HELYX-OS Project Page](http://engys.github.io/HELYX-OS/) for more information.

## Installing the latest binary file for 64 bit linux
Visit [https://engys.github.io/HELYX-OS/](https://engys.github.io/HELYX-OS/) to download the latest HELYX-OS binary and view [installation instructions](https://engys.github.io/HELYX-OS/installation/). 

## Compiling HELYX-OS on your own

### Prerequisites
 - Installation of precompiled HELYX-OS X.X.X
 - Oracle Java version > 1.7
 - Oracle JDK
 - Ant > 1.7

### Steps to Compile
1.  Make sure the Oracle based java and JDK are installed in your machine (use `java -version` and `javac --version`) and configured correctly.
2.  Clone the HELYX-OS git repository in a clean folder (e.g. /home/user/git) 

        git clone https://github.com/ENGYS/HELYX-OS

3. Navigate to the HELYX-OS directory (e.g. /home/user/Engys/HELYX-OS/vX.X.X)
4. Move the sources in the HELYX-OS folder using the command
 
        cp -R /home/user/git/HELYX-OS/* .

5. Compile using the command

        ant -buildfile build_src.xml

6. Run HELYX-OS with the command
 
        ./HELYX-OS.sh

## Disclaimer
This offering is not approved or endorsed by OpenCFD Limited, producer and distributor of the OpenFOAM software via www.openfoam.com, and owner of the OPENFOAM® and OpenCFD®  trade marks.
