<?php 
// No direct access
defined('_JEXEC') or die; 
?>

<?
function renderSection($type, $arrayEvents, $eventList) {
	$dateFormat = $eventList->params["date_format"];
	?>
	<div class="pmk-events-list-<?=$type;?>">
	<div class="pmk-events-list-label"><?=$eventList->params["pokaz_".$type."_label"];?></div>
	<?php
	foreach($arrayEvents as $event) {
		if ($type == $event['type']) {	
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
	}
	?>
	</div>
	<?		
}
?>

<div class="pmk-events-list">
<?
setlocale(LC_ALL, 'pl_PL.UTF-8');
$response = json_decode($eventList->eventsJson, true); // decode json
$pmk = $response["data"]["hosts"][0];
$arrayEvents = $response["data"]["events"];
//var_dump($arrayEvents);
?>

<!-- MSZE ŚWIĘTE -->
<?
if (true == $eventList->params["pokaz_msza"]) {	
	renderSection("msza", $arrayEvents, $eventList);
}
?>

<!-- SPOWIEDZ -->
<?
if (true == $eventList->params["pokaz_spowiedz"]) {	
	renderSection("spowiedz", $arrayEvents, $eventList);
}
?>

<!-- WYDARZENIA -->
<?
if (true == $eventList->params["pokaz_wydarzenie"]) {	
	renderSection("wydarzenie", $arrayEvents, $eventList);
}
?>
</div>

