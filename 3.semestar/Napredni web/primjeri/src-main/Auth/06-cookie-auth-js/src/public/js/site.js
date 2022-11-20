document.getElementById('btnGetData').addEventListener("click", getdata);
document.getElementById('btnLogout').addEventListener("click", logout);
document.getElementById('btnLogin').addEventListener("click", login);

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
showLoginForm(document.cookie.indexOf('authenticated=') < 0);
//


function logout() {
    document.getElementById('content').innerHTML = '';
    return axios.post('logout')
                .then(res => {
                    showLoginForm(true);                  
                });
    
}

function login() {
    document.getElementById('content').innerHTML = '';
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    return axios.post('login', {username, password}) 
                    .then(res => {                        
                       showLoginForm(false)
                    })
                    .catch(function (error) {            
                        document.getElementById('content').innerHTML = error;           
                    });  
}

function getdata() {
    document.getElementById('content').innerHTML = '';
    axios.get('protected')
         .then(function (response) {
            document.getElementById('content').innerHTML = JSON.stringify(response.data);        
        })
        .catch(function (error) {            
            document.getElementById('content').innerHTML = error;           
        });        
}


