import loadrecipes from "./loadrecipes"

function containsAllSubstrings(haystacks, needles) {
    return needles.every(n => haystacks.some(h => h.toUpperCase().indexOf(n.toUpperCase()) != -1));
}

function findRecipes(yieldPredicate, ...ingredients) {
    const recipes = loadrecipes();
    return recipes.filter(yieldPredicate)
                  .filter(r => containsAllSubstrings(r.ingredients, ingredients));   
}

const yieldPredicate = y => y >= 3 && y <= 6;
const recipes = findRecipies(yieldPredicate, "Eggs", "Onion");
recipes.forEach(r => {
    console.log(`${r.id}. ${r.name} ${r.url} ${r.publishDate}`)
});

