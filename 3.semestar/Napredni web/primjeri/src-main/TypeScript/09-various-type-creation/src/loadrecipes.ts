import * as data from "./recipes.json"
function load() {
    let recipes = data.map(function (item, index)  {
        return {
            id: index + 1, 
            ...item, 
            recipeYield : Number(item.recipeYield),
            ingredients : item.ingredients ? item.ingredients.split("\n") : []
        }
    });
    return recipes;
}

export default load;