<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

include_once __DIR__.'/../config/logger.php';

class Event
{
    private $db_conn;     

    public function __construct($db_conn = null)
	{     
        $this->db_conn = $db_conn;         
    }

    public function createTable() {
        // TABLE FOR ICAL FILES 
        info("Checking existiance of `events` table");                
        $sql = "CREATE TABLE IF NOT EXISTS events (
           id              INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
           pmk_id          INT(6),
           uid             VARCHAR(255),
           date_start      DATE,
           time_start      TIME,
           date_end        DATE,
           time_end        TIME,
           duration        INT,
           title           VARCHAR(255) NOT NULL,
           description     VARCHAR(1000),
           type            VARCHAR(255) NOT NULL,
           address         VARCHAR(1000),
           geoLatitude     DECIMAL(5,3),
           geoLongitude    DECIMAL(5,3),
           country         VARCHAR(2) NOT NULL,
           last_download   DATE            
        )";

        $stmt = $this->db_conn->prepare($sql);            
        if ($stmt->execute() !== TRUE) {
            error("Error while querying `events` table: " + $stmt->errorInfo());
        } 

        return $stmt;
    }

    public function insertEvents($eventsList = []) {

        $separator = '';
        $sql = 'INSERT INTO events (pmk_id, type, uid, title, description, date_start, time_start, date_end, time_end, address, geoLatitude, geoLongitude, country) values ';

        foreach($eventsList as $event) {
            extract($event); 
            $sql .= $separator . " (";
            $sql .= "'$pmk_id', ";
            $sql .= "'$type', ";
            $sql .= "'$uid', ";
            $sql .= "'$title', ";
            $sql .= "'$description', ";
            $sql .= "'$date_start', ";
            $sql .= "'$time_start', ";
            $sql .= "'$date_end', ";
            $sql .= "'$time_end', ";
            $sql .= "'$location', ";
            $sql .= "$geoLatitude, ";
            $sql .= "$geoLongitude, ";
            $sql .= "'de' ";            
            $sql .= " )";
            $separator = ', ';
        }

        $stmt = $this->db_conn->prepare($sql);
        // execute query
        if ($stmt->execute() !== TRUE) {
            error("Error while inserting values ");
            debug_r("sql", $sql);
        } 

        return $stmt;

    }

    public function deleteByOrganizer($organizer_id) {
        if (isset($organizer_id)) {
            $stmt = $this->db_conn->prepare('DELETE FROM events WHERE `pmk_id`='.$organizer_id);            
            if ($stmt->execute() !== TRUE) {
                error("Error deleting from `events` table");
            } 
            return $stmt;
        } 
        return null;
    }
    

    public function deleteTable() {
        $stmt = $this->db_conn->prepare('DELETE FROM events');            
        if ($stmt->execute() !== TRUE) {
            error("Error deleting from `events` table");
        } 
        return $stmt;
    }
    

    // Read events by distance
    public function readByDistance($lat, $lon, $days = 7, $maxDistance=100) {

        $dateFrom   = date('Y-m-d');
        $dateTo     = date('Y-m-d', strtotime("+".$days." days"));

        $query = "SELECT 
        id,
        uid,
        type,
        pmk_id,
        title, 
        description, 
        date_start,
        time_start,
        date_end,
        time_end,
        duration,
        address, 
        geoLatitude, 
        geoLongitude, 
        country,
        ceil( 
            100 * 6371 * acos( 
                cos(  radians($lat) )
              * cos(  radians( geoLatitude )  )
              * cos(  radians( geoLongitude) - radians($lon) )
              + sin( radians($lat) )
              * sin( radians( geoLatitude ) )
            )
        ) / 100.0 AS distance
        FROM events        
        WHERE date_start >= '$dateFrom'                 
        AND date_start < '$dateTo'  
        AND geoLatitude  > 0
        HAVING distance < $maxDistance
        ORDER BY date_start ASC, time_start ASC
        LIMIT 100";        
    
        // prepare query statement
        $stmt = $this->db_conn->prepare($query);
 
        // execute query
        if ($stmt->execute() !== TRUE) {
            error("Error while querying `Event` table: " + $stmt->errorInfo());
        } 

        return $stmt;
    }

    
    public function readByPmk($pmk, $days = 7) {
        
        $dateFrom   = date('Y-m-d');
        $dateTo     = date('Y-m-d', strtotime("+".$days." days"));

        $query = "SELECT 
        tab.id,
        tab.uid,
        tab.pmk_id,        
        tab.type,  
        tab.title, 
        tab.description, 
        tab.id,
        tab.title, 
        tab.description, 
        tab.date_start,
        tab.time_start,
        tab.date_end,
        tab.time_end,
        tab.duration,                
        tab.address, 
        tab.geoLatitude, 
        tab.geoLongitude, 
        tab.country       
        FROM events tab     
        WHERE tab.date_start >= '$dateFrom' 
        AND tab.date_start < '$dateTo'      
        AND pmk_id = '$pmk'
        ORDER BY tab.date_start ASC, tab.time_start ASC
        LIMIT 100";
    
        // prepare query statement
        $stmt = $this->db_conn->prepare($query);
 
        // execute query
        if ($stmt->execute() !== TRUE) {
            error("Error while querying `Event` table: " + $stmt->errorInfo());
        } 
        
        return $stmt;       
    }

    public function readLocations(){
        $stmt = $this->db_conn->prepare("SELECT id, address, geoLatitude, geoLongitude FROM events");            
        if ($stmt->execute() !== TRUE) {
             error("Error while querying `Event` table: " + $stmt->errorInfo());
        } 
        return $stmt;
    }

}

?>
