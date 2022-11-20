import Recipes from "./views/Recipes.js";
import Calculator from "./views/Calculator.js";
export default class  {
    getViews() {
        return {
            Recipes,
            Calculator
        };
    }
    async getHtml() {
        return `
        <div class="d-flex justify-content-between bg-light border border-bottom border-primary p-3">
            <h1>My cookbook in the making ver3...</h1>
            <h2 class="d-flex">
                <ul class="d-flex ml-5 "  style="list-style: none;">
                    <li class="mr-3"><a data-link href="Recipes">Recipes</a></li>    
                    <li class="mr-3"><a data-link href="Calculator" >Calculator</a></li>        
                </ul>
            </h2>
        </div>
        <router-view></router-view>
        `;
    }
}