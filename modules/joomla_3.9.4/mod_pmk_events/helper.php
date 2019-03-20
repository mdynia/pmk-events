<?php

class PmkEventList
{
    public $eventsJson;

    public $params;

    public function PmkEventList($params)
    {
        $this->eventsJson = "{}";
        $this->params = $params;        
    }

    public function query()
    {
        // Parameters
        $params = $this->params;
        $servername = $params->get('servername', '');
        $api_url = $params->get('api_url', '');
        $api_key = $params->get('api_key', '');
        $days = $params->get('days', '');
        $user_id = $params->get('user_id', '');
        

        $url = $servername . $api_url . "getEventsByPmk.php?days=".$days."&pmk=".$user_id."&api_key=".$api_key;
        //echo $url;

        $this->eventsJson = PmkEventList::CallAPI("GET", $url, "username", "password");
        //echo $this->eventsJson;
    }

    private function callAPI($method, $url, $username, $password, $data = false)
    {
        $curl = curl_init();

        switch ($method) {
            case "POST":
                curl_setopt($curl, CURLOPT_POST, 1);

                if ($data) {
                    curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
                }

                break;
            case "PUT":
                curl_setopt($curl, CURLOPT_PUT, 1);
                break;
            default:
                if ($data) {
                    $url = sprintf("%s?%s", $url, http_build_query($data));
                }

        }

        // Optional Authentication:
        curl_setopt($curl, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
        curl_setopt($curl, CURLOPT_USERPWD, $username . ":" . $password);

        curl_setopt($curl, CURLOPT_URL, $url);
        curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);

        $result = curl_exec($curl);

        curl_close($curl);

        return $result;
    }

}
