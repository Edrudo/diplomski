import loadrecipes from "./loadrecipes"

type numberPredicate = (x : number) => boolean;

function containsAllSubstrings(haystacks:string[], needles:string[]) {
    return needles.every(n => haystacks.some(h => h.toUpperCase().indexOf(n.toUpperCase()) != -1));
}

function findRecipes(yieldPredicate : numberPredicate, ...ingredients:string[]) {    
    const recipes = loadrecipes();
    return recipes.filter(r => yieldPredicate(r.recipeYield))
                  .filter(r => containsAllSubstrings(r.ingredients, ingredients));    
}

const yieldPredicate = y => y >= 3 && y <= 6;
const recipes = findRecipes(yieldPredicate, "Eggs", "Onion");
recipes.forEach(r => {
    console.log(`${r.id}. ${r.name} ${r.url} ${r.datePublished}`)
});