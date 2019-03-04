<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

include_once '../config/logger.php';

class ical_events
{
    private $db_conn;     

    public function __construct($db_conn = null)
	{     
        $this->db_conn = $db_conn;         
    }

    
    public function createTable() {
        // TABLE FOR ICAL FILES 
        info("Checking existiance of `ical_events` table");                
        $sql = "CREATE TABLE IF NOT EXISTS ical_events (
           id              INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
           pmk_id          INT(6),
           uid             VARCHAR(255),
           dateTimeStart   TIMESTAMP,
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
            error("Error while querying `ical_events` table: " + $stmt->errorInfo());
        } 

        return $stmt;
    }


    public function deleteTable() {
        $stmt = $this->db_conn->prepare('DELETE FROM ical_events');            
        if ($stmt->execute() !== TRUE) {
            error("Error deleting from `ical_events` table");
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
        DATE_FORMAT(tab.dateTimeStart, '%Y-%m-%dT%H:%i:%s') dateTimeStart, 
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
        FROM ical_events tab     
        WHERE tab.dateTimeStart >= '$dateFrom' 
        AND tab.dateTimeStart < '$dateTo'  
        AND tab.geoLatitude  > 0
        ORDER BY distance ASC
        LIMIT 100";
    
        // prepare query statement
        $stmt = $this->db_conn->prepare($query);
 
        // execute query
        if ($stmt->execute() !== TRUE) {
            error("Error while querying `ical_events` table: " + $stmt->errorInfo());
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
        DATE_FORMAT(tab.dateTimeStart, '%Y-%m-%dT%H:%i:%s') dateTimeStart, 
        tab.address, 
        tab.geoLatitude, 
        tab.geoLongitude, 
        tab.country       
        FROM ical_events tab     
        WHERE tab.dateTimeStart >= '$dateFrom' 
        AND tab.dateTimeStart < '$dateTo'      
        AND pmk_id = '$pmk'
        ORDER BY tab.dateTimeStart ASC
        LIMIT 100";
    
        // prepare query statement
        $stmt = $this->db_conn->prepare($query);
 
        // execute query
        if ($stmt->execute() !== TRUE) {
            error("Error while querying `ical_events` table: " + $stmt->errorInfo());
        } 
        
        return $stmt;       
    }

    public function readLocations(){
        $stmt = $this->db_conn->prepare("SELECT id, address, geoLatitude, geoLongitude FROM ical_events");            
        if ($stmt->execute() !== TRUE) {
             error("Error while querying `ical_events` table: " + $stmt->errorInfo());
        } 
        return $stmt;
    }

}

?>
