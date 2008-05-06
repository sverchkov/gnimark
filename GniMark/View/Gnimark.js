/*
* Gnimark Ajaxcode.
* This bugger needs prototype to work.
*/

var view = 'open';
var inner = '';
var url = 'http://localhost/GnimarkDemo/Gnijax.php';

/**
* Takes page information and makes a call to the gnimark gnizr page.
* Expects a json response consisting of an array of links.
* Will then write those links to the screen.
*/
function Gnimark()
{
	//first things first, lets make an ajax call.
	var ajaxReq = new Ajax.Request(url, {
  		method:'get',
  		onSuccess: function(transport){
     		var json = transport.responseText.evalJSON();
     	      
     	    var link_string= '';
     	    for(i=0; i < json.length; i++)
     	    {
     	    	//build our list of links.
     	    	link_string = link_string + '<tr><td cellspacing=5 bgcolor=#FFF6CF>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=' + json[i]['url'] + '>' + json[i]['name'] + '</a></td></tr>';
     	    }  
     	    
     	    
     	    //update the inner html of the span we want to show the links in.
     	    $('Gnimark').innerHTML = '<table width=100% borderspacing=0 cellspacing=0 cellpadding=0 ><tr><td bgcolor=#FEF3B3><table width=100% borderspacing=0 cellspacing=2 cellpadding=0  style=\"border-width: 1px 1px 0px 1px; border-style: solid;\"><tr><td bgcolor=#FEF3B3 ><font color=#FE623C><b>Gnimark</b></font></td><td bgcolor=#FEF3B3 align=right><a href="Javascript:Gnimark_View_Switch()"><span id=oc_link>Close</span></a></td></tr></table></td></tr><tr><td colspan=2><table id=GnimarkList cellpadding=0 cellspacing=0 width=100% style=\"border-width: 1px 1px 1px 1px; border-style: solid;\">' + link_string + '</table></td></tr></table>';  
   		}
	});
}

function Gnimark_View_Switch()
{
	if(view == 'open')
	{
		Gnimark_Close();
		view = 'closed';
		$('oc_link').innerHTML = "Open";
	}
	else
	{
		Gnimark_Open();
		view = 'open';
		$('oc_link').innerHTML = "Close";
	}
}

function Gnimark_Open()
{
	$('GnimarkList').style.visibility = "visible";
}

function Gnimark_Close()
{
	$('GnimarkList').style.visibility = "hidden";
}