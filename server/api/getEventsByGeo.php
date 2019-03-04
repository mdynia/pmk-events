<?php
    // E.G. http://localhost/pmk_events/api/getEventsByGeo.php?days=14&lat=51.4&lon=8.4

    // required headers
    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");

    // include database and object files
    include_once '../config/database.php';
    include_once '../object/ical_events.php';

    // PARAM: DAYS 
    $days = 7;
    $paramDays = $_GET['days'];
    if (isset($paramDays) && $paramDays > 0 && $paramDays <= 14)  {        
        $days = $_GET['days'];
    } 
    
    // PARAM: lat
    $paramLat = $_GET['lat'];    
    $lat = 51.5;
    if (isset($paramLat) && $paramLat > -90 && $paramLat <= 90)  {                
        $lat = $paramLat;
    } 
    
    $paramLon = $_GET['lon'];    
    $lon = 8.7;
    if (isset($paramLon) && $paramLon > -90 && $paramLon <= 90)  {                
        $lon = $paramLon;
    } 

    // CONNECT DB
    $database = new Database();
    $db = $database->getConnection();

    $result = [];
    $result["records"] = [];
    
    //get results
    $icalEvents = new ical_events($db);    
    $stmt = $icalEvents->readByDistance($lat, $lon, $days);
    
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
        // extract row
        // this will make $row['name'] to
        // just $name only
        extract($row);
 
        $event = array(
            "id" => $id,
            "title" => $title,
            "dateTimeStart" => $dateTimeStart,            
            "address" => $address,
            "distance" => $distance,
            "geoLatitude" => $geoLatitude,
            "geoLongitude" => $geoLongitude,
            "description" => html_entity_decode($description)            
        );
 
        array_push($result["records"], $event);
    }
    
    
        
    // set response code - 200 OK
    http_response_code(200);

    // show products data in json format
    echo json_encode($result);
    
 ?>