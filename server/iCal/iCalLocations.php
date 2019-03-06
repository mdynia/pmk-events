<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include_once '../config/logger.php';

/*
*  Class for maintaining locations. 
*
*
*/


class iCalLocations
{
    private $db_conn;

    public function __construct($db_conn = null)
	{     
        $this->db_conn = $db_conn;        
        $this->createTables();
    }


    public function getLocations() {
        $locations = [];

        // GET ICAL FILES FROM DB
        info("Querying `ical_locations` table");                
        $stmt = $this->db_conn->prepare("SELECT id, address, geoLatitude, geoLongitude  FROM ical_locations");            
        if ($stmt->execute() !== TRUE) {
             error("Error while querying `ical_locations` table: " + $stmt->errorInfo());
        } 

        if ($stmt->rowCount() > 0) {
            // output data of each row
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {                
                $address = $row["address"];
                $locations[$address] = $row;                
            }
        }

        return $locations;
  
    }

    public function collectEventLocations() {
        $locationsExisting = $this->getLocations();
        $locationsNew = [];

        // GET ICAL FILES FROM DB

        info("Querying `Event` table");                
        $icalEvents = new Event($this->db_conn);
        $stmt = $icalEvents->readLocations();
        if ($stmt->rowCount() > 0) {
            // output data of each row
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {                
                $address = $row["address"];

                if (!isset($locationsExisting[$address])) {
                    $locationsNew[$address] = "new address";
                }                
            }
        }

        if (!empty($locationsNew)) {
            $separator = '';
            $sql = 'INSERT INTO ical_locations (address) values ';
            foreach ($locationsNew as $address => $tmp) {
                $sql .= $separator. '(';
                $sql .= '\''.$address.'\' ';
                $sql .= ')';                
                $separator = ', ';
            }
    
            info("Insert events into ical_locations");                
            $stmt = $this->db_conn->prepare($sql);            
            if ($stmt->execute() !== TRUE) {
                self::error("Error inserting");
                self::debug_r("SQL", $sql);
            } 
        }
    }


    private function createTables()	{

        // TABLE FOR ICAL FILES 
        info("Checking existiance of `ical_locations` table");                
        $sql = "CREATE TABLE IF NOT EXISTS ical_locations (
            id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
                address         VARCHAR(1000),
                geoLatitude     DECIMAL(5,3),
                geoLongitude    DECIMAL(5,3)
        )";
 
        $stmt = $this->db_conn->prepare($sql);            
        if ($stmt->execute() !== TRUE) {
             error("Error `ical_locations` creating table: " + $stmt->errorInfo());
        } 
        // INSERT INTO ical_locations (address,geoLatitude, geoLongitude) VALUES('Pankratiusstraße\, 33098 Paderborn\, Germany','51.704066', '8.748254');

    }



    

}