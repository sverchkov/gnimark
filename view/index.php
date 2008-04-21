<?php
/*
 * This page is designe dto show how the ajax will fire and display what it needs to display.
 */
?>
<body onload="Gnimark()">

    <span id="Gnimark"></span>
    
    <script type="text/JavaScript" src="prototype.js"></script>
    <script type="text/JavaScript" src="Gnimark.js">window.onload = function(){ Gnimark(); }</script>

    <table onload="Gnimark()">
    	<tr>
    		<td>There is a bunch of content below.</td>
    	</tr>
    	<tr>
    		<td>
    		<?php for($i=0; $i < 20; $i++){ echo "This is a bunch of content. Yay. <BR>"; } ?>
    		</td>
    	</tr>
    </table>

</body>