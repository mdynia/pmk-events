<?php
include_once 'config/database.php';
include_once 'object/misja.php';

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>Control Panel</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" type="text/css" media="screen" href="main.css">	
</head>
<body>

<h1>Connecting to database </h1>

<?php
// Create connection
$database = new Database();
$conn = $database->getConnection();
?>

<h1>Control Panel</h1>

<?
$misja = new Misja($conn);
$misja->createTables();
$stmt = $misja->readAll();
if ($stmt->rowCount() > 0) {
	// output data of each row
	while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
		extract($row);  
		?>
		<?=$nazwa;?> <a href="refresh_organizer.php?organizer_id=<?=$id;?>">Odświerz wydarzenia</a>
		<?
	}
}              
?>

<h1>DONE</h1>
</body>
</html>
