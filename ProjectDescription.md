Gnimark is a Gnizr bookmark recommendation applet. It recommends urls based upon the content, url, and bookmark labels for its current page.  Gnimark can recommend bookmarks to users who are not logged into Gnizr but logged in users can experience more advanced and specific bookmark recommendation functionality.

Consists of a small Javascript segment of code placed on a web page that will use AJAX to relay page data to the Gnimark Java back-end based in an installation of Gnizr.  The Gnimark back-end uses the page data provided by the JavaScript front-end to recommend bookmarks based on a 'clever system of weighting.'

This algorithm entails:
  * Determining all of the bookmarks for a current page and the relative frequencies of their tags on these bookmarks to create a system of weights.
  * Once a system of weights is established for a page, pages containing similar tags are ranked based upon the summation of the weights of their tags to create an overall score.  Pages are then ranked based upon this score.
  * Results are returned based on this ranking.

The Gnimark back-end is able to return a configurable number of recommendations to the front end.  Results can be paged for instances when large result sets are requested.  All AJAX communication between the front-end and the back-end is handled using JSON.

The Gnimark back-end integrates seamlessly into Gnizr and makes use of all applicable DAOs provided by Gnizr.