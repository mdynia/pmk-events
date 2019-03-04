<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include_once '../config/logger.php';

class Misja
{
    private $db_conn;     

    public function __construct($db_conn = null)
	{     
        $this->db_conn = $db_conn;         
    }

    
    public function createTables() {           
           info("Checking existiance of `misja` table");                
           $sql = "CREATE TABLE IF NOT EXISTS misja (
                   id               INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
                   nazwa            VARCHAR(255) NOT NULL,
                   patron           VARCHAR(255) NOT NULL,
                   adres            VARCHAR(255) NOT NULL,
                   telefon          VARCHAR(255) NOT NULL,
                   fax              VARCHAR(255) NOT NULL,
                   email            VARCHAR(255) NOT NULL,
                   www              VARCHAR(255) NOT NULL,
                   country          VARCHAR(2) NOT NULL,
                   ics_url          VARCHAR(500) NOT NULL,                   
                   last_download    TIMESTAMP
           )";
   
           $stmt = $this->db_conn->prepare($sql);            
           if ($stmt->execute() !== TRUE) {
               error("Error `misja` creating table: " + $stmt->errorInfo());
           } 
           //INSERT INTO misja (nazwa,ics_url, country) VALUES('Kalendarz PMK Bielefeld-Paderborn','https://calendar.google.com/calendar/ical/qu27nqfsmas8lq7cbh4nd7b2ok%40group.calendar.google.com/private-140036b0fd5200ee327aed2b5631a56a/basic.ics', 'de');
           //INSERT INTO misja (nazwa,ics_url, country) VALUES('Kalendarz Dynia','https://calendar.google.com/calendar/ical/mdynia%40gmail.com/private-ef8d19774e82853c47d317476709f267/basic.ics', 'de');   
    }

    public function readById($pmk_id) {
        $query = "SELECT         
        *       
        FROM misja
        WHERE id = $pmk_id
        ";
    
        // prepare query statement
        $stmt = $this->db_conn->prepare($query);
 
        // execute query
        if ($stmt->execute() !== TRUE) {
            error("Error while querying `misja` table: " + $stmt->errorInfo());
        } 
        
        return $stmt;
    }



    public function readAll() {

        $query = "SELECT         
        *       
        FROM misja
        ORDER BY last_download ASC";
    
        // prepare query statement
        $stmt = $this->db_conn->prepare($query);
 
        // execute query
        if ($stmt->execute() !== TRUE) {
            error("Error while querying `misja` table: " + $stmt->errorInfo());
        } 
        
        return $stmt;
    }
        
}