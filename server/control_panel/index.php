<?php
include_once '../config/database.php';
include_once '../object/misja.php';

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

function urlAlboBrak($url) {
	if (isset($url) && !empty($url)) {
		return "<a href='$url'>Link</a>";
	}
	return "(brak)";
}

?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>Control Panel</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" type="text/css" media="screen" href="debug.css">	
	<link rel="stylesheet" type="text/css" media="screen" href="main.css">	
</head>


<body>
<h1>Panel Kontrolny</h1>

<?php
// Create connection
$database = new Database();
$conn = $database->getConnection();

$misja = new Misja($conn);
$misja->createTables();
$stmt = $misja->readAll();
if ($stmt->rowCount() > 0) {
	// output data of each row
	while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
		extract($row);  
		?>
		<div class="pc-div-misja">
			<h2><?=$nazwa;?> </h2>					
			<!-- Do not us this code! -->
			<dl>    
			<dt>Patron</dt><dd><?=$patron;?></dd>
			<dt>Adres</dt><dd><?=$adres;?></dd>
			<dt>Telefon</dt><dd><?=$telefon;?></dd>
			<dt>Fax</dt><dd><?=$fax;?></dd>
			<dt>Email</dt><dd><?=$email;?></dd>
			<dt>Strona www</dt><dd><?=$www;?></dd>
			</dl>

			<dl>    
			<dt>Kalendarz - Msze</dt><dd><?=urlAlboBrak($ics_url_msze);?></dd>
			<dt>Kalendarz - Spowiedź</dt><dd><?=urlAlboBrak($ics_url_spowiedz);?></dd>
			<dt>Kalendarz - Wydarzenia</dt><dd><?=urlAlboBrak($ics_url_wydarzenia);?></dd>
			<dt>Kalendarz - Rekolekcje</dt><dd><?=urlAlboBrak($ics_url_rekolekcje);?></dd>        		
			</dl>
			<a href="refresh_organizer.php?organizer_id=<?=$id;?>">Odświerz wydarzenia</a>
		</div>
		<?
	}
}              
?>

done.
</body>
</html>
