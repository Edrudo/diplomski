export default class {
    async getHtml() {

        try {
            let response = await fetch('/recipes');

            if (response.ok) {
                let allRecipes = await response.json();
                let recipes = allRecipes.filter(x => Math.random()>0.95).reverse();  // bcs the last items tend to have valid image...

                return `<h1>Recipes</h1><div class="container-fluid p-2 d-flex flex-wrap">`
                    + recipes.map(rcp => `
               <div class="card mt-2 mr-2" style="width: 200px;">
               <img class="card-img-top" style="width: 190px;" src="${rcp.image}">
               <div class="card-body">
                 <h5 class="card-title">${rcp.name}</h5>
                 <p class="card-text">${rcp.description}</p>
                 <span class="badge badge-primary">Cook/prep time: ${rcp.cookTime}/${rcp.prepTime}</span>
                 <span class="badge badge-secondary">Yield: ${rcp.recipeYield}</span>
                 <span class="badge badge-success">Pusblished: ${rcp.datePublished}</span>
               </div>
               <div class="card-body">
                 <a href="${rcp.url}" class="card-link">Source</a>
               </div>
               <details>
                 <summary><h3>Ingredients</h3></summary>
                 <ul class="list-group list-group-flush">
                     ${rcp.ingredients.split("\n").map(x => '<li class="list-group-item">' + x + '</li>').join('')}
                 </ul>
               </details>
              
               
             </div>
               `).join('') + '</div>';
            } else {
                throw new Error("HTTP-Error: " + response.status);
            }
        } catch (error) {
            console.error(error);
            return `<h1>Home view (error occured)</h1>`;
        }

    }
}

// {
//     "name": "Easter Leftover Sandwich",
//     "ingredients": "12 whole Hard Boiled Eggs\n1/2 cup Mayonnaise\n3 Tablespoons Grainy Dijon Mustard\n Salt And Pepper, to taste\n Several Dashes Worcestershire Sauce\n Leftover Baked Ham, Sliced\n Kaiser Rolls Or Other Bread\n Extra Mayonnaise And Dijon, For Spreading\n Swiss Cheese Or Other Cheese Slices\n Thinly Sliced Red Onion\n Avocado Slices\n Sliced Tomatoes\n Lettuce, Spinach, Or Arugula",
//     "url": "http://thepioneerwoman.com/cooking/2013/04/easter-leftover-sandwich/",
//     "image": "http://static.thepioneerwoman.com/cooking/files/2013/03/leftoversandwich.jpg",
//     "cookTime": "PT",
//     "recipeYield": "8",
//     "datePublished": "2013-04-01",
//     "prepTime": "PT15M",
//     "description": "Got leftover Easter eggs?    Got leftover Easter ham?    Got a hearty appetite?    Good! You've come to the right place!    I..."
// }