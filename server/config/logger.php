<?php

    function debug_r($label = null, $structure = null) {   
        echo "<div class='debug_msg iCalCache'>";
        echo "<div class='debugHeader'>$label</div>";	
        echo "<textarea class='debug'>";	
        if (isset($structure)) {
            print_r ($structure);
        } else {
            echo "undefined";
        }        
        echo "</textarea>";	
        echo "</div>";
    }

    function info($text = null) {   
        echo "<div class='debug_msg info_msg iCalCache'>INFO (iCalCache): ";
        echo $text;
        echo "</div>";
    }

    function error($text = null) {   
        echo "<div class='debug_msg error_msg iCalCache'>ERROR (iCalCache): ";
        echo $text;
        echo "</div>";
    }
    
?>