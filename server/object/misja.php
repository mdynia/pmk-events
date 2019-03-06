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
           //INSERT INTO misja (nazwa, ics_url, patron, adres, telefon, fax, email, www, country) VALUES('Kalendarz PMK Bielefeld-Paderborn','https://calendar.google.com/calendar/ical/3gmb7d1b2tip52qq3k0u07udic%40group.calendar.google.com/private-de918e03af951b68f17ce4bca1911a61/basic.ics', '', '', '', '', '', '', 'de');
           
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