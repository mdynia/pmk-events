<?php
include_once 'config/database.php';
include_once 'iCal/EventsDownloader.php';


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

    <h1>Connecting to database </h1>
    <?php
// Create connection
$database = new Database();
$conn = $database->getConnection();
?>


    <h1>Downloading Event</h1>
    <?php
        $downloader = new EventsDownloader(1, $conn);
        $downloader->getNextDaysEvents(7);
    ?>

    <h1>DONE</h1>
</body>

</html>