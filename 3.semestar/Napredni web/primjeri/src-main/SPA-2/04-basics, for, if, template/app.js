const Calculator = {
  template: `
  <h3>Temperature calculator, the time is: {{ startedDateAt }} {{ startedTimeAt }} </h3>
  <form>
      <div class="form-group row">
          <label for="celsius" class="col-sm-4 col-form-label">Celsius</label>
          <div class="col-4">
              <input type="number" class="form-control" placeholder="C"  
                  v-model="tempCelsius" @keyup="c2f">
          </div>
      </div>
      <div class="form-group row">
          <label for="fahrenheit" class="col-4 col-form-label" >Fahrenheit</label>
          <div class="col-4">
              <input type="number" class="form-control" placeholder="F" 
                  v-model.number="tempFahrenheit" @keyup.enter="f2c" >
          </div>
      </div>
      <div class="form-group row">
          <div class="col-12">
              <button type="button" @click.prevent="logTemp">Log it</button>
          </div>
      </div>
      <div v-if="logItems.length > 0">
        <h2>Logged temperatures:</h2>
        <div class="form-group row">
            <ul>
                <li v-for="item in logItems" :class="logItemClass(item)">{{ item.C }}°C = {{ item.F }}°F</li>
            </ul>
        </div>

      </div>
  </form>  
  `,
  data() {
    return {
      tempCelsius: 0,
      tempFahrenheit: 32,
      startedDateAt: new Date().toLocaleDateString("hr-HR"),
      startedTimeAt: new Date().toLocaleTimeString("hr-HR"),
      logItems: []
    };
  },
  methods: {
    c2f() {
      this.tempFahrenheit = Math.round((this.tempCelsius * 9) / 5 + 32);
    },
    f2c() {
      console.log(typeof this.tempFahrenheit);
      this.tempCelsius = Math.round(((this.tempFahrenheit - 32) * 5) / 9);
    },
    logItemClass(item) {
      return  (item.C > 200) ? "hot" : "";
    },
    logTemp() {
      this.logItems.push({
        C: this.tempCelsius,
        F: this.tempFahrenheit
      })
    }
  },
  mounted() {
    window.setInterval(() => {
      this.startedTimeAt = new Date().toLocaleTimeString("hr-HR");
    }, 1000);
  }
};

const app = Vue.createApp(Calculator);
app.mount("#my-app");
