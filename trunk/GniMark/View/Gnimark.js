/*
* Gnimark Ajaxcode.
* This bugger needs prototype to work.
*/

var view = 'open';
var inner = '';
var url = 'http://discerningtexan.blogspot.com/'; //specify the name of this page in gnizr.
var http = createRequestObject();

/**
* Takes page information and makes a call to the gnimark gnizr page.
* Expects a json response consisting of an array of links.
* Will then write those links to the screen.
*/
function Gnimark()
{

    http.open('get', 'proxy.php?message=\'c1' + url + '\'');
    http.onreadystatechange = handleResponse; //parses the result. //fills the board in with the starter values.
    http.send();
    
    http.open('get', 'proxy.php?message=\'' + url + '\'');
    http.onreadystatechange = handleResponse; //parses the result. //fills the board in with the starter values.
    http.send();

}

function Gnimark_View_Switch()
{
	if(view == 'open')
	{
		document.getElementById('GnimarkList').style.visibility = "hidden";
		view = 'closed';
		document.getElementById('oc_link').innerHTML = "Open";
	}
	else
	{
		document.getElementById('GnimarkList').style.visibility = "visible";
		view = 'open';
		document.getElementById('oc_link').innerHTML = "Close";
	}
}


function createRequestObject() 
{
    var ro;
  
            // almost everyone but MSIE less than 7
    try {
        ro = new XMLHttpRequest();

    // MSIE branches
    } catch (e) {
        try {
            ro = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (e) {
            try {
                ro = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (e) {
                ro = null;
            }
        }
    }
    return ro;
}


function handleResponse() 
{
    if(http.readyState == 4)
    {
        var response = http.responseText;

     	    array = response.split(",");
     	      
     	    var link_string= '';
     	    for(i=0; i < array.length - 1; i++)
     	    {
     	    	//build our list of links.
     	    	link_string = link_string + '<tr><td cellspacing=5 bgcolor=#FFF6CF>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=' + array[i]+ '>' + array[i] + '</a></td></tr>';
     	    }  

     	    //update the inner html of the span we want to show the links in.
     	    document.getElementById('Gnimark').innerHTML = '<table width=200 borderspacing=0 cellspacing=0 cellpadding=0 ><tr><td bgcolor=#FEF3B3><table width=100% borderspacing=0 cellspacing=2 cellpadding=0  style=\"border-width: 1px 1px 0px 1px; border-style: solid;\"><tr><td bgcolor=#FEF3B3 ><font color=#FE623C><b>Gnimark</b></font></td><td bgcolor=#FEF3B3 align=right><a href="Javascript:Gnimark_View_Switch()"><span id=oc_link>Close</span></a></td></tr></table></td></tr><tr><td colspan=2><table id=GnimarkList cellpadding=0 cellspacing=0 width=100% style=\"border-width: 1px 1px 1px 1px; border-style: solid;\">' + link_string + '</table></td></tr></table>';  
    }
}



	