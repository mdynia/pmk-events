
var monthNames = [
  "Styczeń", "Luty", "Marzec",
  "Kwiecień", "Maj", "Czerwiec", "Lipiec",
  "Sierpień", "Wrzesień", "Październik",
  "Listopad", "Grudzień"
];

function loadEvents() {
  var request = new XMLHttpRequest();

  request.open('GET', 'http://localhost/pmk_events/api/getEventsByPmk.php?days=14&pmk=2', true);
  request.onload = function () {
    // Begin accessing JSON data here
    var data = JSON.parse(this.response);

    var mainDiv = document.getElementById("pmk_events");
  
    // if correct HTTP response code
    if (request.status >= 200 && request.status < 400) {      
      data.events.forEach(event => addEvent(mainDiv, event));      
    } else {
      console.log('error');
    }
  }
  
  request.send();
}

function addEvent(mainDiv, event) {
  console.log("Event: " + event.title);

  var node = document.createElement("div");
  node.className = 'pmk_event_div';

  // reformat the start date/time
  var dateTimeStartDate = new Date(event.dateTimeStart); //%Y-%m-%dT%H:%i:%s
  var time = dateTimeStartDate.toLocaleTimeString('pl', {
    hour: 'numeric',
    minute: '2-digit',    
  });
  event.dateTimeStart =  dateTimeStartDate.getDate() + " " + monthNames[dateTimeStartDate.getMonth()] + ", " + time;
  
  addEventColumn(node, event, "dateTimeStart");  
  
  addEventColumn(node, event, "title");
  addEventColumn(node, event, "description");
  
  addEventColumn(node, event, "address");
  addGoogleMapsLink(node, event);
  addEventColumn(node, event, "geoLatitude");
  addEventColumn(node, event, "geoLongitude");  

  mainDiv.appendChild(node); 
}

function addEventColumn(nodeDiv, event, field) {
  var col = document.createElement("div");
  col.className = 'pmk_event_detail_div pmk_event_detail_'+field;
  var textnode = document.createTextNode(event[field]);
  col.appendChild(textnode);
  nodeDiv.appendChild(col);
}

function addGoogleMapsLink(nodeDiv, event) {
  var col = document.createElement("div");
  col.className = 'pmk_event_detail_div pmk_event_map_url';
  var url = 'https://www.google.com/maps/place/' + event.address.replace(" ", "+");

  var a = document.createElement('a');
  a.href =  url;
  a.innerHTML = "Google Map";
  a.setAttribute('target', '_blank');

  col.appendChild(a);
  nodeDiv.appendChild(col);
}


