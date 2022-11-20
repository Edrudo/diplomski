const data = require("./recipes.json");

function load() {
    const recipes = data.map(function (item, index)  {
        return {
            id: index + 1, 
            ...item, 
            recipeYield : Number(item.recipeYield),
            ingredients : item.ingredients ? item.ingredients.split("\n") : []
        }
    });
    return recipes;
}

function containsAllSubstrings(haystacks, needles) {
    return needles.every(n => haystacks.some(h => h.toUpperCase().indexOf(n.toUpperCase()) != -1));
}

function findRecipes(yieldPredicate, ...ingredients) {
    const recipes = load();
    return recipes.filter(yieldPredicate)
                  .filter(r => containsAllSubstrings(r.ingredients, ingredients));   
}

const yieldPredicate = y => y >= 3 && y <= 6;
const recipes = findRecipies(yieldPredicate, "Eggs", "Onion");
recipes.forEach(r => {
    console.log(`${r.id}. ${r.name} ${r.url} ${r.publishDate}`)
});

