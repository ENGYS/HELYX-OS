---
#
# Use the widgets beneath and the content will be
# inserted automagically in the webpage. To make
# this work, you have to use › layout: frontpage
#
layout: frontpage
header:
  image_fullwidth: meshHeader-scaled.png
widget1:
  title: "About HELYX-OS"
  url: 'http://engys.com/products/helyx-os'
  image: '/images/HELYX-OS-v231-welcomescreen-03.png'
  text: 'HELYX-OS is an open-source Graphical User Interface designed to work natively with the version 2.4.0 of the OpenFOAM library. The GUI is developed by ENGYS using Java+VTK and delivered to the public under the GNU General Public License.'
widget2:
  title: "Installation"
  url: '/installation/'
  image: 'http://engys.com/development/1.jpeg?width=229&height=188'
  text: 'After downloading the most recent binary, users can find more information about how to install HELYX-OS on their system to get up and running quickly.'
widget3:
  title: "Training  and Support"
  url: 'http://engys.com/services/support'
  image: 'http://engys.com/support/1.jpeg?width=229&height=188'
  text: 'HELYX-OS is provided as an Open-Source product, without any formal support from ENGYS.  For additional paid support and training with HELYX-OS and/or all other ENGYS products, please visit our website.'
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
  url: http://engys.com/products/helyx-os
  text: Download the latest HELYX-OS binary ›
  style: alert
permalink: /index.html
---
<div id="videoModal" class="reveal-modal large" data-reveal="">
  <div class="flex-video widescreen vimeo" style="display: block;">
    <iframe width="1280" height="720" src="https://www.youtube.com/embed/3b5zCFSmVvU" frameborder="0" allowfullscreen></iframe>
  </div>
  <a class="close-reveal-modal">&#215;</a>
</div>
