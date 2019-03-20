<?php

class Database {

    // specify your own database credentials
    private $host = "rdbms.strato.de";
    private $db_name = "DB3697321";
    private $username = "U3697321";
    private $password = "ezqyCIUkATgJ99zr2dJh"; 

    public $conn;
 
    // get the database connection
    public function getConnection(){
 
        $this->conn = null;
 
        try
        {
            $this->conn = new PDO("mysql:host=" . $this->host . ";dbname=" . $this->db_name, $this->username, $this->password);
            $this->conn->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_WARNING );
            $this->conn->exec("set names utf8");
        } catch(PDOException $exception){
            echo "Connection error: " . $exception->getMessage();
        }
 
        return $this->conn;
    }
}
?>