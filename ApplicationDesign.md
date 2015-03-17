# Application Design #

See our work flow diagram at http://userpages.umbc.edu/~jm1/gnimarkWorkFlow.pdf.

See our class diagram at http://userpages.umbc.edu/~jm1/gnimarkDesign.pdf.

## General Overview ##

Our application consists of two parts:

  * Client side:
    * A file with JavaScript code to support AJAX and communication with the server
    * A segment of code to be inserted into the hosting website's page

  * Server side:
    * An action page in Gnizr that receives information from the client side and sends the list of recommendations in response
    * Supporting Java Classes

# Prototype Screenshots #

Gnimark open screenshot at ![http://userpages.umbc.edu/~tavener1/gnimark_open.jpg](http://userpages.umbc.edu/~tavener1/gnimark_open.jpg)

Gnimark closed screenshot at ![http://userpages.umbc.edu/~tavener1/gnimark_closed.jpg](http://userpages.umbc.edu/~tavener1/gnimark_closed.jpg)

(Gnimark is at the bottom right of each screenshot.)

The above screenshots are from a working prototype of the system's front end.   This prototype makes ajax calls to a stub backend page to display the links shown in the screenshots.  Upon project completion, this stub backend page will be replaced with logic that can recommend bookmarks to a user based upon the content of the page gnimark is hosted on as well as what other users of Gnizr have been bookmarking.