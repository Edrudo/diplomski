(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(success, error);
    } else {
      alert("Geolocation is not supported by this browser");
    }    
})();

var url

if(process.env.PORT){
  url = process.env.HOST
}else{
  url = "http://localhost:5000"
}

function success(position) {
  const latitude = position.coords.latitude;
  const longitude = position.coords.longitude;
  document.getElementById("longitude paragraph").innerHTML = "Your longitude: " + longitude
  document.getElementById("latitude paragraph").innerHTML = "Your latitude: " + latitude
  getMap(latitude, longitude);

  // sending registered users location
  fetch("/registeredUserLocation", {
      method: 'POST',
      body: JSON.stringify({longitude: longitude, latitude: latitude}),
      headers: {
          'Content-type': 'application/json; charset=UTF-8'
      }
  }).then(response => response.json())
}
function error() {
  alert("Unable to retrieve location");
}

function getMap(latitude, longitude) {


  const map = L.map("map").setView([latitude, longitude], 5);
  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(map);
  L.marker([latitude, longitude]).addTo(map);

  fetch("/registeredUserLocation", {
      method: 'GET',
  }).then(response => response.json().then(array => {
    array.forEach(element => {
      L.marker([element.latitude, element.longitude]).on('click', (e)=>{
        document.getElementById('infoClickDiv').innerHTML = "Name:" + element.name + ", time: " + Date(element.timestamp)
      }).addTo(map)
    });
  }))
}
