const Calculator = {
  data() {
    return {
      tempCelsius: 0,
      tempFahrenheit: 32,
      startedDateAt: new Date().toLocaleDateString("hr-HR"),
      startedTimeAt: new Date().toLocaleTimeString("hr-HR"),
    };
  },
  methods: {
    c2f() {
      this.tempFahrenheit = Math.round((this.tempCelsius * 9) / 5 + 32);
    },
    f2c() {
      console.log(typeof this.tempFahrenheit);
      this.tempCelsius = Math.round(((this.tempFahrenheit - 32) * 5) / 9);
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
