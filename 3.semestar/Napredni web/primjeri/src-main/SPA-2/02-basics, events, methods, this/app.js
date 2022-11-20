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
      this.tempCelsius = this.toFloat(this.$refs.tempCelsius.value);
      this.tempFahrenheit = Math.round((this.tempCelsius * 9) / 5 + 32);
      alert("C=" + this.tempCelsius + " F = " + this.tempFahrenheit); // "zaglavi" resubmit, ako nismo onesposobili resubmit, da se vidi što se zbiva..
    },
    f2c(event) {
      console.log("event", event);
      //event.preventDefault();  // kada ne bi imali event modifier morali bi sami otkazati
      this.tempFahrenheit = this.toFloat(this.$refs.tempFahrenheit.value);
      this.tempCelsius = Math.round(((this.tempFahrenheit - 32) * 5) / 9);
    },
    toFloat(value) {
      // suvišno, samo da se vidi this.method
      // console.log("toFloat", this);  // pogledajte što je this - Proxy!
      return parseFloat(value);
    },
  },
};

const app = Vue.createApp(Calculator);
app.mount("#my-app");
