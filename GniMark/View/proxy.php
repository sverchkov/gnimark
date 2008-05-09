<?php
 // Open the Curl session 
 
   //http://localhost:8080/servlet/Login
   //http://192.168.1.4:8080/search.jsp
   //http://192.168.1.5:8080/servlet/Login
          $session = curl_init('http://localhost:8080/gnizr/helloworld/sayHello.action?' . $_SERVER['QUERY_STRING']); 
           
          // Don't return HTTP headers. Do return the contents of the call 
          curl_setopt($session, CURLOPT_HEADER, false); 
          curl_setopt($session, CURLOPT_RETURNTRANSFER, true); 
           
          // Make the call 
          $text = curl_exec($session); 

          header("Content-Type: text/xml");           
          echo "$text";
          curl_close($session);
?>
