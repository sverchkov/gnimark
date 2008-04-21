<?php
/*
 * Im just going to spawn a fake array of links and turn it to json.
 */

//make the link array.
$links = array(array("url" => "http:test.php", "name" => "blogger.com"), 
               array("url" => "http:test2.php", "name" => "cnn.com"), 
               array("url" => "http:test3.php", "name" => "umbc.edu"));

//make it json.
$json = json_encode($links);

//print it so ajax can capture it.
echo "$json";

//done.
?>