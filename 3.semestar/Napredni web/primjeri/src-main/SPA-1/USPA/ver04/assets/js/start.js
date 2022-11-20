import Uspa from "./uspa.js"
import App from "./App.js"
import Obs from "./lib/Observer.js"
const app = new Uspa();

app.mount("my-app", new App());
