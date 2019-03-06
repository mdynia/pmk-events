<?php
include_once '../../config/database.php';
include_once'../icsParser.php';


ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);


?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>Download PMK Calendars</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" type="text/css" media="screen" href="main.css">
	<script src="main.js"></script>	
	
</head>
<body>
<h1>DOWNLOAD PMK EVENTS</h1>

<?php

$dateFrom = "2019-03-06";
$dateTo   = "2019-04-06";


$parser = new icsParser('basic.ics');
$eventList = $parser->getEvents($dateFrom, $dateTo);

echo "<textarea style='width: 100%; height: 400px;'>";
print_r ($eventList);
echo "</textarea>";



?>	
</body>
</html>
