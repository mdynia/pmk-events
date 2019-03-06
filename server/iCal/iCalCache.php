<?php
include_once '../config/logger.php';
include_once '../object/events.php';
include_once '../object/misja.php';

include_once 'icsParser.php';
include_once "icsEvent.php";
include_once "icsTimeZone.php";
include_once 'iCalLocations.php';


/*
*  Class for accessing list of iCal files. 
*
*  The content is downloaded on demand once a day and cached in database table 'ical_files' where:
*   id - row id
*   description - description of the file (e.g. events of PMK Bieleld)
*   url - public url for accessing ics file
*   last_download - date of the last succesfull download
*   content - raw content of the file
*
*/


class iCalCache
{
    private $db_conn;
    private $locations;
    private $iCallocations;


    public function __construct($db_conn = null)
	{     
        $this->db_conn = $db_conn; 
        $this->iCallocations = new iCalLocations($db_conn);
        $this->locations = $this->iCallocations->getLocations();
        //debug_r("Locations (from location cache)" ,$this->locations);
    }

    public function getNextDaysEvents($num_days = 7) {
        $dateFrom = Date('Y-m-d');
        $dateTo = Date('Y-m-d', strtotime("+".$num_days." days"));

        info("Delete everything from `Event`");        
        $icalEvents = new Event($this->db_conn);
        $icalEvents->createTable();
        $stmt = $icalEvents->deleteTable();


        info("Get list of ics urls from 'misja'");               
        $misja = new Misja($this->db_conn);
        $misja->createTables();
        $stmt = $misja->readAll();
        if ($stmt->rowCount() > 0) {
            // output data of each row
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
                extract($row);                

                echo "<h1>Downloading: " . $nazwa. "</h1>";
                
                $isUrl  = strpos($ics_url, 'http') === 0 && filter_var($ics_url, FILTER_VALIDATE_URL);			    
                info("Download url: ".$ics_url);
                $content = '';
			    if ($isUrl) {
                    $content = file_get_contents($ics_url);       
                    debug_r("iCal file",$content);                    
                }   
                                
                // PARSE iCAL
                info("Parse received ics file");
                $parser = new icsParser();
                $parser->parse($content);

                info("Getting events by date (" . $dateFrom . " - " . $dateTo . ")");
                $eventList = $parser->getEvents($dateFrom, $dateTo);

                debug_r("Extracted events", $eventList);

                info("Store extracted events");                
                $eventDbList = $this->mapDbEvents($id, $eventList);

                debug_r("Event to be inserted", $eventDbList);
                $icalEvents->insertEvents($eventDbList);
            }
        }

        // check event table for new locations
        $this->iCallocations->collectEventLocations();        
    }


    private function mapDbEvents($pmk_id = null, $events = []) {
        
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
                error("No geocoding found for location: ". $location);                                                       
            }

            // add additional fields
            $event["pmk_id"] = $pmk_id;
            $event["geoLatitude"] = $geoLatitude;
            $event["geoLongitude"] = $geoLongitude;

            array_push($eventList ,$event);                    

            $addressMap[$location] = "address";
        }
    
        return $eventList;            
    }
}
?>