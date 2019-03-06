<?php

class icsParser {
	/**
	 * @var string
	 */
	public $title;

	/**
	 * @var string
	 */
	public $description;

	/**
	 * @var string
	 */
	public $defaultTimezone;
	
	public $icsTimeZone;
	

	/**
	 * @var array
	 */
	public $events = array();

	/**
	 * @var array
	 */
	protected $_eventsByDate;


	public function __construct($content = null)
	{
		if ($content) {
			$isUrl  = strpos($content, 'http') === 0 && filter_var($content, FILTER_VALIDATE_URL);
			$isFile = strpos($content, "\n") === false && file_exists($content);
			if ($isUrl || $isFile) {
				$content = file_get_contents($content);
			}
			$this->parse($content);
		}
	}

	public function title()
	{
		return $this->summary;
	}

	public function description()
	{
		return $this->description;
	}

	public function events()
	{
		return $this->events;
	}


	/**
	 * Main function to query events in the format required by the pmk tool
	 * - local time 
	 */
	public function getEvents($start, $end) {
		$eventList = [];

		// get raw data
		$events = $this->eventsByDateBetween($start, $end);
		
		// map the data to simplified format
		foreach ($events as $date => $events) {
			foreach ($events as $event) {

				array_push($eventList , array(
					"title" =>$event->title(),
					"description" => $event->description(),
					"date_start" => $date,
					"time_start" => $event->timeStart,
					"date_end" => $date,
					"time_end" => $event->timeEnd,					
					"location" => $event->location,
					"uid" => $event->uid
				));
			}
		}

		return $eventList;
	}


	public function eventsByDateBetween($start, $end)
	{
		if ((string) (int) $start !== (string) $start) {
			$start = strtotime($start);
		}
		$start = date('Y-m-d', $start);

		if ((string) (int) $end !== (string) $end) {
			$end = strtotime($end);
		}
		$end = date('Y-m-d', $end);

		$return = array();
		foreach ($this->eventsByDate() as $date => $events) {
			if ($start <= $date && $date < $end) {
				$return[$date] = $events;
			}
		}

		return $return;
	}

	private function eventsByDate()
	{
		if (! $this->_eventsByDate) {
			$this->_eventsByDate = array();

			foreach ($this->events() as $event) {
				foreach ($event->occurrences() as $occurrence) {
					$date = $occurrence->format('Y-m-d');
					$this->_eventsByDate[$date][] = $event;
				}
			}
			ksort($this->_eventsByDate);
		}

		return $this->_eventsByDate;
	}

	public function eventsByDateSince($start)
	{
		if ((string) (int) $start !== (string) $start) {
			$start = strtotime($start);
		}
		$start = date('Y-m-d', $start);

		$return = array();
		foreach ($this->eventsByDate() as $date => $events) {
			if ($start <= $date) {
				$return[$date] = $events;
			}
		}

		return $return;
	}

	public function parse($content)
	{
		$content = str_replace("\r\n ", '', $content);

		// Title
		preg_match('`^X-WR-CALNAME:(.*)$`m', $content, $m);
		$this->title = $m ? trim($m[1]) : null;

		// Description
		preg_match('`^X-WR-CALDESC:(.*)$`m', $content, $m);
		$this->description = $m ? trim($m[1]) : null;

		// Default Timezone
		preg_match('`^X-WR-TIMEZONE:(.*)$`m', $content, $m);
		$this->defaultTimezone = $m ? trim($m[1]) : null;
		$this->icsTimeZone = new icsTimeZone($this->defaultTimezone);

		// Events
		preg_match_all('`BEGIN:VEVENT(.+)END:VEVENT`Us', $content, $m);
		foreach ($m[0] as $c) {
			$this->events[] = new icsEvent($this->icsTimeZone, $c);
		}

		return $this;
	}
}


?>
