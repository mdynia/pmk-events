<?php
include_once '../config/logger.php';
include_once '../object/ical_events.php';
include_once '../object/misja.php';

include 'iCalLocations.php';

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
        debug_r("Locations (from location cache)" ,$this->locations);
    }

    public function getNextDaysEvents($num_days = 7) {

        // server timezone
        $dateFrom = Date('Y-m-d');
        $dateTo = Date('Y-m-d', strtotime("+".$num_days." days"));

        info("Delete everything from `ical_events`");        
        $icalEvents = new ical_events($this->db_conn);
        $icalEvents->createTable();
        $stmt = $icalEvents->deleteTable();


        info("Get list of iCal urls from 'misja'");               
        $misja = new Misja($this->db_conn);
        $misja->createTables();
        $stmt = $misja->readAll();
        if ($stmt->rowCount() > 0) {
            // output data of each row
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
                extract($row);                

                info("Downloading: " . $nazwa);
                
                $isUrl  = strpos($ics_url, 'http') === 0 && filter_var($ics_url, FILTER_VALIDATE_URL);			    
                $content = '';
			    if ($isUrl) {
                    $content = file_get_contents($ics_url);       
                    debug_r("iCal file",$content);                    
                }   
                                
                // PARSE iCAL
                info("Parsing receiver iCal file");
                $iCal = new iCal();
                $iCal->parse($content);
                
                info("Getting events by date (" . $dateFrom . " - " . $dateTo . ")");
                $events = $iCal->eventsByDateBetween($dateFrom, $dateTo);
                
                info("Store extracted events");                
                $eventDbList = $this->mapDbEvents($id, $events);
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

        if (sizeof($events) > 0) {
            
            foreach ($events as $date => $events) {
	            foreach ($events as $event) {

                    // compute location
                    $geoLatitude = 'NULL';
                    $geoLongitude = 'NULL';                
                    if (isset($this->locations[$event->location])) {
                            $evntLoc = $this->locations[$event->location];
                        if ($evntLoc["geoLatitude"] && $evntLoc["geoLongitude"]) {
                            $geoLatitude = $evntLoc["geoLatitude"];
                            $geoLongitude = $evntLoc["geoLongitude"];                        
                        } else {                        
                            error("Geocoding not yet specified in locaiton cache.");                                                
                        }
                    
                    } else {
                        error("No geocoding found for location: ". $event->location);                                                       
                    }


                    $eventDB = array(
                        "pmk_id" => $pmk_id,
                        "uid" => $event->uid,
                        "title" =>$event->title(),
                        "description" => $event->description(),
                        "dateTimeStart" => $date.' '.$event->godzina.':00',
                        "address" => $event->location,
                        "geoLatitude" =>$geoLatitude,
                        "geoLongitude" => $geoLongitude,
                        "country" => "de"
                    );

                    array_push($eventList ,$eventDB);                    

                    $addressMap[$event->location] = "address";
		        }
	        }

            return $eventList;            
        }

    }


}
?>