---
#
# Use the widgets beneath and the content will be
# inserted automagically in the webpage. To make
# this work, you have to use › layout: frontpage
#
layout: frontpage
header:
  image_fullwidth: 'meshHeader-2017.png'
widget1:
  title: "About HELYX-OS"
  url: 'https://github.com/ENGYS/HELYX-OS'
  image: '/images/HELYX-OS-v231-welcomescreen.png'
  text: 'HELYX-OS was an open-source GUI for OpenFOAM® v4.1 and v1606+, developed by ENGYS, but has been deprecated. While HELYX-OS is no longer actively developed, ENGYS continues to offer advanced CFD solutions with HELYX.'
widget2:
  title: "Legacy Installation"
  url: '/installation/'
  image: '/images/installation.png'
  text: 'HELYX-OS is no longer maintained, but you can still install the last available version. Follow the instructions to set up HELYX-OS on your system and continue using its features.                                                                                 '
widget3:
  title: "HELYX"
  url: 'http://engys.com/products/helyx'
  image: '/images/support-scaled.png'
  text: 'HELYX is ENGYS’s actively developed, open-source CFD software, offering advanced features, regular updates, and dedicated support.                                                            '
#
# Use the call for action to show a button on the frontpage
#
# To make internal links, just use a permalink like this
# url: /getting-started/
#
# To style the button in different colors, use no value
# to use the main color or success, alert or secondary.
# To change colors see sass/_01_settings_colors.scss
#

callforaction:
  url: https://github.com/ENGYS/HELYX-OS/releases/download/v2.4.0/HELYX-OS-2.4.0-linux-x86_64.bin
  text: Download HELYX-OS v2.4.0 (Legacy Version) for 64-bit Linux ›
  style: alert

permalink: /index.html
---
<div id="videoModal" class="reveal-modal large" data-reveal="">
  <div class="flex-video widescreen vimeo" style="display: block;">
    <iframe width="1280" height="720" src="https://www.youtube.com/embed/3b5zCFSmVvU" frameborder="0" allowfullscreen></iframe>
  </div>
  <a class="close-reveal-modal">&#215;</a>
</div>
