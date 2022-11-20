window.onload = () => {
    document.getElementById('btnGetData').addEventListener("click", getdata);
    document.getElementById('btnLogin').addEventListener("click", login);
    document.getElementById('btnLogout').addEventListener("click", logout);
};

const JWT_TOKEN = 'jwt';
//var token;
function getToken() {
  //  return token;
  return localStorage.getItem(JWT_TOKEN);
}
function storeToken(jwt) {
    //token = jwt;
    localStorage.setItem(JWT_TOKEN, jwt);
}
function removeToken() {
    //token = undefined;
    localStorage.removeItem(JWT_TOKEN);
}

function showLoginForm(show) {
    if (show) {
        document.getElementsByClassName('login-form')[0].style.display = "block";
        document.getElementById('btnLogout').style.display = "none";
    }
    else {
        document.getElementsByClassName('login-form')[0].style.display = "none";
        document.getElementById('btnLogout').style.display = "block";
    }
}
//hide login form if logged
showLoginForm(getToken() === undefined || getToken() === null);
//

const serverUri = 'http://localhost:4041';

function logout() {
    document.getElementById('content').innerHTML = '';
    removeToken();
    showLoginForm(true);
}

function login() {
    document.getElementById('content').innerHTML = '';
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    axios.post(`${serverUri}/login`, {username, password}) 
          .then(res => {
              const jwt = res.data;
              storeToken(jwt);
              showLoginForm(false);
          })
          .catch(function (error) {                          
              document.getElementById('content').innerHTML = error;           
          });  
}

function getdata() {
    document.getElementById('content').innerHTML = '';
    axios.get(`${serverUri}/protected`, { 
                headers : {
                    Authorization : `Bearer ${getToken()}`
                }
          })
         .then(function (response) {
            document.getElementById('content').innerHTML = JSON.stringify(response.data);        
        })
        .catch(function (error) {            
            document.getElementById('content').innerHTML = error;           
        });        
}


