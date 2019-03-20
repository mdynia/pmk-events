<?php 
// No direct access
defined('_JEXEC') or die; ?>

<div class="pmk-events-list">

<?
setlocale(LC_ALL, 'pl_PL.UTF-8');
$response = json_decode($eventList->eventsJson, true); // decode json
$pmk = $response["pmk"];
$arrayEvents = $response["events"];
$dateFormat = $eventList->params["date_format"];
?>

<?php
foreach($arrayEvents as $event) {
	$startDateArray = date_parse($event['date_start']); //2019-03-22
	$timestamp = mktime(0, 0, 0, $startDateArray["month"], $startDateArray["day"], $startDateArray["year"]);
	$startDateString =  ucfirst(strftime($dateFormat, $timestamp));
	$startTimeString = substr($event['time_start'], 0, 5);
	?>
	<div class="pmk-event-div">
		<div class="pmk-event-start-date-time"><?=$startDateString;?>, <?=$startTimeString;?></div>		
		<div class="pmk-event-title"><?=$event['title'];?></div>
		<div class="pmk-event-description"><?=$event['description'];?></div>
		<div class="pmk-event-address"><a href="https://www.google.com/maps/place/<?=urlencode($event['address']);?>" target="_blank"><?=$event['address'];?></a></div>	
	</div>
	<?php
}
?>

</div>



