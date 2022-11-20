import loadrecipes from "./loadrecipes"

type Recipe = typeof recipes[0];
type RecipeInfo = Pick<Recipe, "name" | "description"> & {whereToEat? : string};
interface  CitiesTopMeal {
  [key:string] : RecipeInfo;  //index signature
};
let citiesAndMeals : CitiesTopMeal = {}; 

function createInfo(recipe : Recipe) : RecipeInfo {   
  return {name : recipe.name, description : recipe.description};
}

let recipes = loadrecipes();
let brief = recipes.map(r => createInfo(r))
                   .slice(1, 5);

citiesAndMeals.Zagreb = brief[0];
citiesAndMeals.Florence = brief[2];
console.log(citiesAndMeals);

brief[0].whereToEat = "Zagreb";                  
brief[2].whereToEat = "Florence";

brief.forEach(ri => console.log(`Eat ${ri.name} in ${ri.whereToEat ?? "unknown place"}`));
//console.log(citiesAndMeals);
          