<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

include_once '../config/logger.php';

class Event
{
    private $db_conn;     

    public function __construct($db_conn = null)
	{     
        $this->db_conn = $db_conn;         
    }

    
    public function createTable() {
        // TABLE FOR ICAL FILES 
        info("Checking existiance of `Event` table");                
        $sql = "CREATE TABLE IF NOT EXISTS Event (
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
           address         VARCHAR(1000),
           geoLatitude     DECIMAL(5,3),
           geoLongitude    DECIMAL(5,3),
           country         VARCHAR(2) NOT NULL,
           last_download   DATE            
        )";

        $stmt = $this->db_conn->prepare($sql);            
        if ($stmt->execute() !== TRUE) {
            error("Error while querying `Event` table: " + $stmt->errorInfo());
        } 

        return $stmt;
    }

    public function insertEvents($eventsList = []) {

        $separator = '';
        $sql = 'INSERT INTO Event (pmk_id, uid, title, description, date_start, time_start, date_end, time_end, address, geoLatitude, geoLongitude, country) values ';

        foreach($eventsList as $event) {
            extract($event); 
            $sql .= $separator . " (";
            $sql .= "'$pmk_id', ";
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


    public function deleteTable() {
        $stmt = $this->db_conn->prepare('DELETE FROM Event');            
        if ($stmt->execute() !== TRUE) {
            error("Error deleting from `Event` table");
        } 
        return $stmt;
    }
    

    // Read events by distance
    public function readByDistance($lat, $lon, $days = 7) {

        $dateFrom   = date('Y-m-d');
        $dateTo     = date('Y-m-d', strtotime("+".$days." days"));

        $query = "SELECT 
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
        tab.country,
        ( 6371
        * acos( cos( radians($lat) )
              * cos(  radians( tab.geoLatitude )   )
              * cos(  radians( tab.geoLongitude) - radians($lon) )
              + sin( radians($lat) )
              * sin( radians( tab.geoLatitude ) )
            )
        ) AS distance 
        FROM Event tab     
        WHERE tab.dateTimeStart >= '$dateFrom' 
        AND tab.dateTimeStart < '$dateTo'  
        AND tab.geoLatitude  > 0
        ORDER BY distance ASC
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
        FROM Event tab     
        WHERE tab.dateTimeStart >= '$dateFrom' 
        AND tab.dateTimeStart < '$dateTo'      
        AND pmk_id = '$pmk'
        ORDER BY tab.dateTimeStart ASC
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
        $stmt = $this->db_conn->prepare("SELECT id, address, geoLatitude, geoLongitude FROM Event");            
        if ($stmt->execute() !== TRUE) {
             error("Error while querying `Event` table: " + $stmt->errorInfo());
        } 
        return $stmt;
    }

}

?>
