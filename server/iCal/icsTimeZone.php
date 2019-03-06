<?php

class icsTimeZone {

	public $timezone;

	public function __construct($timezone = null)
	{
			$this->timezone = $timezone;		
	}

	/**
	 * Parse date and time from ics file and translate it to
	 * local time of the event creator
	 */
	public function parseDateTime($timeString) {
		$result = array();		
		//echo "<br>Parsing:datetime: ".$timeString;

		if (preg_match('`^([0-9]+)T([0-9]+)Z`m', $timeString, $m)) {
			$date = $m[1];
			$time = substr($m[2], 0, 4);
			//echo "<br> Date: $date, Time: $time (FORM #2: DATE WITH UTC TIME)";
			//echo "<br> UNIMPLEMENTED!!! ";			
			$result = $this->utcToLocalTime($date, $time);
		} else if (preg_match('`^TZID=(.*):([0-9]+)T([0-9]+)`m', $timeString, $m)) {
			$tz = $m[1];
			$date = $m[2];
			$time = substr($m[3], 0, 4);
			//echo "<br> Date: $date, Time: $time, Timezone: $tz (FORM #3: DATE WITH LOCAL TIME AND TIME ZONE REFERENCE)";						
			$result = $this->tzToLocalTime($date, $time, $tz);			
		} else if (preg_match('`^([0-9]+)T([0-9]+)`m', $timeString, $m)) {
			$date = $m[1];
			$time = substr($m[2], 0, 4);
			//echo "<br> Date: $date, Time: $time (FORM #1: DATE WITH LOCAL TIME)";
			
			$result["timezone"] = $this->timezone;
			$result["localDate"] = $date;
			$result["localTime"] = $time;
		} else {
			echo "<br> Unsuported format. See https://www.kanzaki.com/docs/ical/";			
		}

		//echo "<br><textarea style=' width: 300px; height: 142px; background: #dadada;'>";
		//print_r ($result);
		//echo "</textarea>";

		return $result;
	}

	public function utcToLocalTime($utcDateString, $utcTimeString) {		
		$date = DateTime::createFromFormat('Ymd Hi e', $utcDateString." ".$utcTimeString." UTC");
		
		// timezone of event creator
		$tz = new DateTimeZone($this->timezone );
		$date->setTimezone($tz);
		//echo "<br><b>utcToLocalTime</b>:".$date->format('Y-m-d H:i P');		

		return array(
			"localDate" => $date->format('Y-m-d'),
			"localTime" => $date->format('H:i'),
			"timezone" => $date->format('P'),			
		);
	}

	public function tzToLocalTime($utcDateString, $utcTimeString, $timezoneName) {		
		$date = DateTime::createFromFormat('Ymd Hi O', $utcDateString." ".$utcTimeString." ".$timezoneName);

		// timezone of event creator
		$tz = new DateTimeZone($this->timezone);

		$date->setTimezone($tz);
		//echo "<br><b>utcToLocalTime</b>:".$date->format('Y-m-d H:i P');		

		return array(
			"localDate" => $date->format('Y-m-d'),
			"localTime" => $date->format('H:i'),			
			"timezone" => $date->format('P'),	
			"origEventTimezone" => $timezoneName,
		);
	}	
}
	
?>