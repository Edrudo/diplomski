const Calculator = {
  data() {
    return {
      tempCelsius: 0,
      tempFahrenheit: 32,
      startedDateAt: new Date().toLocaleDateString("hr-HR"),
      startedTimeAt: new Date().toLocaleTimeString("hr-HR"),
    };
  },
};

const app = Vue.createApp(Calculator);
app.mount("#my-app");
