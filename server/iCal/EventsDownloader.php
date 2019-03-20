<?php
include_once __DIR__ . '/../config/logger.php';
include_once __DIR__ . '/../object/events.php';
include_once __DIR__ . '/../object/misja.php';

include_once __DIR__ . '/icsParser.php';
include_once __DIR__ . "/icsEvent.php";
include_once __DIR__ . "/icsTimeZone.php";
include_once __DIR__ . '/iCalLocations.php';

class EventsDownloader
{
    private $db_conn;
    private $organizer_id;

    private $ics_url_array;

    private $locations;
    private $iCallocations;

    public function __construct($organizer_id, $db_conn = null)
    {
        $this->ics_url_array = [];
        $this->organizer_id = $organizer_id;
        $this->db_conn = $db_conn;
        $this->iCallocations = new iCalLocations($db_conn);
        $this->locations = $this->iCallocations->getLocations();
        //debug_r("Locations (from location cache)" ,$this->locations);

        if (isset($organizer_id)) {
            info("Querying organizer with id: " . $organizer_id);
            $misja = new Misja($this->db_conn);
            $misja->createTables();
            $stmt = $misja->readById($organizer_id);
            if ($stmt->rowCount() > 0) {
                // single row expected
                $row = $stmt->fetch(PDO::FETCH_ASSOC);
                extract($row);

                info("Misja: " . $nazwa);

                $this->ics_url_array["msza"] = $ics_url_msze;
                $this->ics_url_array["spowiedz"] = $ics_url_spowiedz;
                $this->ics_url_array["wydarzenie"] = $ics_url_wydarzenia;
                $this->ics_url_array["rekolekcje"] = $ics_url_rekolekcje;
            }
        }

        debug_r("Calendar URLs", $this->ics_url_array);
    }

    public function getNextDaysEvents($num_days = 7)
    {
        
        $icalEvents = new Event($this->db_conn);
        $icalEvents->createTable();

        info("Delete events of organizer: " . $this->organizer_id);
        $icalEvents->deleteByOrganizer($this->organizer_id);

        info("Get list of ics urls from 'misja'");
        
        EventsDownloader::downloadIcs($icalEvents, "msza",       $this->ics_url_array["msza"],           $num_days);
        EventsDownloader::downloadIcs($icalEvents, "spowiedz",   $this->ics_url_array["spowiedz"],       $num_days);
        EventsDownloader::downloadIcs($icalEvents, "wydarzenie", $this->ics_url_array["wydarzenie"],     $num_days);
        EventsDownloader::downloadIcs($icalEvents, "rekolekcje", $this->ics_url_array["rekolekcje"],     $num_days);
        
        // check event table for new locations
        $this->iCallocations->collectEventLocations();
    }


    public function downloadIcs($icalEvents, $type, $ics_url, $num_days = 7)
    {
        $dateFrom = Date('Y-m-d');
        $dateTo = Date('Y-m-d', strtotime("+" . $num_days . " days"));

        $isUrl = strpos($ics_url, 'http') === 0 && filter_var($ics_url, FILTER_VALIDATE_URL);
        info("Download url: " . $ics_url);
        $content = '';
        if ($isUrl) {
            $content = file_get_contents($ics_url);
            debug_r("iCal file", $content);
        }

        // PARSE iCAL
        info("Parse received ics file");
        $parser = new icsParser();
        $parser->parse($content);

        info("Getting events by date (" . $dateFrom . " - " . $dateTo . ")");
        $eventList = $parser->getEvents($dateFrom, $dateTo);

        debug_r("Extracted events", $eventList);

        info("Store extracted events");
        $eventDbList = $this->mapDbEvents($type, $eventList);

        debug_r("Event to be inserted", $eventDbList);
        $icalEvents->insertEvents($eventDbList);
        
    }




    private function mapDbEvents($type, $events = [])
    {

        $addressMap = [];
        $eventList = [];

        foreach ($events as $event) {
            extract($event);

            // compute location
            $geoLatitude = 'NULL';
            $geoLongitude = 'NULL';
            if (isset($this->locations[$location])) {
                $evntLoc = $this->locations[$location];
                if ($evntLoc["geoLatitude"] && $evntLoc["geoLongitude"]) {
                    $geoLatitude = $evntLoc["geoLatitude"];
                    $geoLongitude = $evntLoc["geoLongitude"];
                } else {
                    error("Geocoding not yet specified in locaiton cache.");
                }
            } else {
                error("No geocoding found for location: " . $location);
            }

            // add additional fields            
            $event["type"] = $type;
            $event["pmk_id"] = $this->organizer_id;
            $event["geoLatitude"] = $geoLatitude;
            $event["geoLongitude"] = $geoLongitude;

            array_push($eventList, $event);

            $addressMap[$location] = "address";
        }

        return $eventList;
    }
}
