
export default class  {
    constructor () {
        this.myData = {
            tempCelsius: 0,
            tempFahrenheit: 32
        };
    }
    getData() {
        return this.myData; // RFE: FWK should take care of the reference
    }
    onChange(key, value) {
        if (key === "tempCelsius") {
            this.myData.tempFahrenheit = parseFloat(value) * 9 / 5 + 32;
        } else if (key === "tempFahrenheit") {
            this.myData.tempCelsius = (parseFloat(value) - 32) * 5/ 9;
        }
    }    
    async getHtml() {
        return `
            <h1>Temperature calculator</h1>
            <form>
                <div class="form-group row">
                    <label for="celsius" class="col-sm-2 col-form-label">Celsius</label>
                    <div class="col-sm-10">
                        <input type="number" class="form-control" id="celsius" placeholder="C"  uspa-bind="tempCelsius">
                    </div>
                </div>
                <div class="form-group row">
                    <label for="fahrenheit" class="col-sm-2 col-form-label" >Fahrenheit</label>
                    <div class="col-sm-10">
                        <input type="number" class="form-control" id="fahrenheit" placeholder="F" uspa-bind="tempFahrenheit">
                    </div>
                </div>
            </form>
        `;
    }
}