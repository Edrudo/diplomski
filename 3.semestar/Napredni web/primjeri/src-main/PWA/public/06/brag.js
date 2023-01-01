import {get, set} from 'https://cdn.jsdelivr.net/npm/idb-keyval@6/+esm';

const player = document.getElementById('player');
const canvas = document.getElementById('cnvFood');
const beforeSnap = document.getElementById('beforeSnap');
const afterSnap = document.getElementById('afterSnap');
const snapName = document.getElementById('snapName');
const startCapture = function() {
  beforeSnap.classList.remove('d-none');
  beforeSnap.classList.add('d-flex', 'flex-column', 'align-items-center');
  afterSnap.classList.remove('d-flex', 'flex-column', 'align-items-center');
  afterSnap.classList.add('d-none');
  if (!('mediaDevices' in navigator)) {
    // fallback to file upload button, ili sl.
    // vidjet i custom API-je: webkitGetUserMedia i mozGetUserMedia
  } else {
    navigator.mediaDevices
        .getUserMedia({video: true, audio: false})
        .then((stream) => {
          player.srcObject = stream;
        })
        .catch((err) => {
          alert('Media stream not working');
          console.log(err);
        });
  }
};
startCapture();
const stopCapture = function() {
  afterSnap.classList.remove('d-none');
  afterSnap.classList.add('d-flex', 'flex-column', 'align-items-center');
  beforeSnap.classList.remove('d-flex', 'flex-column', 'align-items-center');
  beforeSnap.classList.add('d-none');
  player.srcObject.getVideoTracks().forEach(function(track) {
    track.stop();
  });
};
document.getElementById('btnSnap').addEventListener('click', function(event) {
  canvas.width = player.getBoundingClientRect().width;
  canvas.height = player.getBoundingClientRect().height;
  canvas
      .getContext('2d')
      .drawImage(player, 0, 0, canvas.width, canvas.height);
  stopCapture();
});
document
    .getElementById('btnUpload')
    .addEventListener('click', function(event) {
      event.preventDefault();
      if (!snapName.value.trim()) {
        alert('Give it a cathcy name!');
        return false;
      }
      if ('serviceWorker' in navigator && 'SyncManager' in window) {
        const url = canvas.toDataURL();
        fetch(url)
            .then((res) => res.blob())
            .then((blob) => {
              const ts = new Date().toISOString();
              const id = ts + snapName.value.replace(/\s/g, '_'); // ws->_
              set(id, {
                id,
                ts,
                title: snapName.value,
                image: blob,
              });
              return navigator.serviceWorker.ready;
            })
            .then((swRegistration) => {
              return swRegistration.sync.register('sync-snaps');
            })
            .then(() => {
              console.log('Queued for sync');
              startCapture();
            })
            .catch((error) => {
              alert(error);
              console.log(error);
            });
      } else {
        // fallback
        // pokusati poslati, pa ako ima mreze onda dobro...
        alert('TODO - vaš preglednik ne podržava bckg sync...');
      }
    });
