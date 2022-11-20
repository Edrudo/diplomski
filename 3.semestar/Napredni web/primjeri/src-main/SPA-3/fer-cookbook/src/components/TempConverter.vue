<template>
  <small-card>
    <h3>Temperature converter</h3>
    <form>
      <div class="form-group row">
        <label for="celsius" class="col-sm-4 col-form-label">Celsius</label>
        <div class="col-8">
          <input
            type="number"
            class="form-control"
            placeholder="C"
            v-model.number="tempCelsius"
            @keyup="c2f"
          />
        </div>
      </div>
      <div class="form-group row">
        <label for="fahrenheit" class="col-4 col-form-label">Fahrenheit</label>
        <div class="col-8">
          <input
            type="number"
            class="form-control"
            placeholder="F"
            v-model.number="tempFahrenheit"
            @keyup="f2c"
          />
        </div>
      </div>
    </form>
  </small-card>
</template>


<script>
export default {
  data() {
    return {
      tempCelsius: 0,
      tempFahrenheit: 32,
    };
  },
  methods: {
    c2f() {
      this.tempFahrenheit = Math.round((this.tempCelsius * 9) / 5 + 32);
    },
    f2c() {
      this.tempCelsius = Math.round(((this.tempFahrenheit - 32) * 5) / 9);
    },
  },
};
</script>
