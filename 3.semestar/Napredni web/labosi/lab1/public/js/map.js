(() => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(success, error);
  } else {
    alert("Geolocation is not supported by this browser");
  }    
})();

function success(position) {
  const latitude = position.coords.latitude;
  const longitude = position.coords.longitude;
  document.getElementById("longitude paragraph").innerHTML = "Your longitude: " + longitude
  document.getElementById("latitude paragraph").innerHTML = "Your latitude: " + latitude
  getMap(latitude, longitude);
}
function error() {
alert("Unable to retrieve location");
}

function getMap(latitude, longitude) {
  const map = L.map("map").setView([latitude, longitude], 5);
  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(map);
  L.marker([latitude, longitude]).addTo(map);
  L.marker([latitude, longitude], {
    icon: L.divIcon({
        html: "Your location",
        className: 'text-below-marker',
      })
  }).addTo(map);
}
