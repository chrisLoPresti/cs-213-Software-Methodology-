# README #

CS 213 Photo Album app for group 27

### How do I get set up? ###

The project comes with a stock user

To sign in as the stock user:

username = stock
password = stock

To sign in as the admin

username = admin
password = admin


### Attempts for extra credit. ###

When a new user is created, they automatically have the stock album, regardless of whats in it (0-however many pictures).

When a user creates an album and adds photos they are stored locally.
The original source path is saved , however if the photo is deleted or is
put into another directory, then how could the photo load? By saving them locally 
we removed that issue. Furthermore, they can be loaded in from any machine given this case,
as long as you have the login information. However, simulating security, if i gave you my
entire workspace with a bunch of albums and photos, the second you load up admin on another computer
it recognizes that the prior users and content dont belong to this machine, so it wipes the information,
preserving only the stock user.

A dummy user is provided with the default project. When you clone this project and run it, the dummy will
be deleted because he does not "belong" to the new computer. This is to show how the "Security" feature works.

When a user presses add photo a file chooser opens. Only one instance of file chooser can open at a time.
If multiple could open this could cause issues. While file chooser is open, the stage is on lockdown. 
Either pick a photo or close the file chooser in order to gain back control of the program.
While file chooser is open, if someone exits the window, file chooser is terminated along with the program.
