# To run this script type ...
# chef-apply
# As I learn more about chef I should be able to create a cookbook etc.

package 'tree'
package 'xclip'
package 'git-all'
# The following may have only installed the basic version of Eclipse, use apt-cache search <packagename> | grep <packagename>
package 'eclipse'




# Have installed Google Chrome Browser manually through Firefox browser which came with Ubuntu
# Google Chrome Browser is not in the apt-cache or in the Ubuntu Software Center.  Probably will need a custom chef receipe (?) using the steps package is not in repositories, will need to figure out custom configuration in Chef to run commands in http://askubuntu.com/questions/79280/how-to-install-chrome-browser-properly-via-command-line
# sudo apt-get install libxss1 libappindicator1 libindicator7
# wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
# sudo dpkg -i google-chrome*.deb
# ... becomes 'google-chrome-stable' after installation

