<?php
    ini_set('display_errors', 1);
    ini_set('display_startup_errors', 1);
    error_reporting(E_ALL);

    // E.G. http://localhost/pmk_events/api/getEventsByPmk.php?days=14&pmk=1

    // required headers
    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");

    // include database and object files
    include_once '../config/database.php';
    include_once '../object/Event.php';
    include_once '../object/misja.php';

    // PARAM: DAYS 
    $days = 7;
    $paramDays = $_GET['days'];
    if (isset($paramDays) && $paramDays > 0 && $paramDays <= 14)  {        
        $days = $_GET['days'];
    } 
    
    // PARAM: pmk_id
    $paramPmk = $_GET['pmk'];    
    $pmk_id = 1;
    if (isset($paramPmk) && $paramPmk > 0 && $paramPmk <= 1000)  {                
        $pmk_id = $paramPmk;
    } 
    
    // CONNECT DB
    $database = new Database();
    $db = $database->getConnection();
    
    $pmk = array(
        "name" => "PMK Bielefeld",
        "url" => "http:// Bielefeld",

    );


    $result = [];    
    
    // GET PMK RECORD
    $misja = new Misja($db);    
    $stmt = $misja->readById($pmk_id);
    $result["pmk"] =  $stmt->fetch(PDO::FETCH_ASSOC);

    
    $result["events"] = [];    
    //get results from DB   
    $icalEvents = new Event($db);
    $stmt = $icalEvents->readByPmk($pmk_id, $days);
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
        extract($row);
 
        $event = array(
            "id" => $id,
            "title" => $title,
            "dateTimeStart" => $dateTimeStart,            
            "address" => $address,
            "geoLatitude" => $geoLatitude,
            "geoLongitude" => $geoLongitude,
            "description" => html_entity_decode($description)            
        );
 
        array_push($result["events"], $event);
    }
    
        
    // set response code - 200 OK
    http_response_code(200);

    // show products data in json format
    echo json_encode($result);
    
 ?>