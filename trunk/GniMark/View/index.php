<body onload="Gnimark()">

    <span id="Gnimark"></span>
    
    <script type="text/JavaScript" src="prototype.js"></script>
    <script type="text/JavaScript" src="Gnimark.js"></script>

    <table>
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