# To run this script type ...
# sudo chef-apply
# As I learn more about chef I should be able to create a cookbook etc.

package 'tree'
package 'xclip'
package 'graphviz'
package 'git-all'
# The following may have only installed the basic version of Eclipse, use apt-cache search <packagename> | grep <packagename>
#package 'eclipse'
package 'maven'


# For Eclipse, install the following plugins (add sites below to ) ...
# EGit http://download.eclipse.org/egit/updates
#  http://www.fuin.org/p2-repository/ (Expand "Maven osgi-bundles" and select "slf4j-api" - required for Maven integration)
# Maven Integration http://download.eclipse.org/technology/m2e/releases

# To uninstall a package, use the following ... (haven't had to try yet)
# sudo apt-get remove <packagename>

# Have installed Google Chrome Browser manually through Firefox browser which came with Ubuntu
# Google Chrome Browser is not in the apt-cache or in the Ubuntu Software Center.  Probably will need a custom chef receipe (?) using the steps package is not in repositories, will need to figure out custom configuration in Chef to run commands in http://askubuntu.com/questions/79280/how-to-install-chrome-browser-properly-via-command-line
# sudo apt-get install libxss1 libappindicator1 libindicator7
# wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
# sudo dpkg -i google-chrome*.deb
# ... becomes 'google-chrome-stable' after installation
# Has to be started from command line using ...
# google-chrome &





# Other
# use 'printenv' to see you environment variables


