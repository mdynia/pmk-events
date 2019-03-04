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
                $this->storeEvents($id, $events);
            }
        }

        // check event table for new locations
        $this->iCallocations->collectEventLocations();
        
    }


    private function storeEvents($pmk_id = null, $events = []) {
        
        $addressMap = [];

        if (sizeof($events) > 0) {
            $separator = '';
            $sql = 'INSERT INTO ical_events (pmk_id, uid, title, description, dateTimeStart,  address, geoLatitude, geoLongitude, country) values ';
            foreach ($events as $date => $events) {
	            foreach ($events as $event) {
                    $geoLatitude = 'NULL';
                    $geoLongitude = 'NULL';                
                    if (isset($this->locations[$event->location])) {
                            $evntLoc = $this->locations[$event->location];
                        if ($evntLoc["geoLatitude"] && $evntLoc["geoLongitude"]) {
                            $geoLatitude = $evntLoc["geoLatitude"];
                            $geoLongitude = $evntLoc["geoLongitude"];                        
                        } else {                        
                            debug_r("Empty coordinates in cache", $evntLoc);                    
                        }
                    
                    } else {
                        error("No geocoding found for location: ". $event->location);                                                       
                    }

                    $sql .= $separator. '(';                
                    $sql .= '\''.$pmk_id.'\', ';
                    $sql .= '\''.$event->uid.'\', ';
                    $sql  .= '\''.$event->title().'\', ';
                    $sql .= '\''.$event->description().'\', ';
                    $sql .= '\''.$date.' '.$event->godzina.':00\','; //dateTimeStart                
                    $sql .= '\''.$event->location.'\','; // address
                    $sql .= ''.$geoLatitude.','; // geoLatitude
                    $sql .= ''.$geoLongitude.','; // geoLongitude
                    $sql .= '\'de\')';                
                    $separator = ', ';

                    $addressMap[$event->location] = "address";
		        }
	        }
        
            debug_r("Events to be inserted", $events);

            info("Insert events into ical_events");                
            $stmt = $this->db_conn->prepare($sql);            
            if ($stmt->execute() !== TRUE) {
                error("Error inserting");
                debug_r("SQL", $sql);
            } 
        }

    }


}
?>