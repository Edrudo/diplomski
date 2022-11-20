import { createStore } from "vuex";

export default createStore({
  state: {
    user: null,
    allRecipes: [],
  },
  mutations: {
    setUser(store, newUser) {
      store.user = newUser;
    },
    setRecipes(store, recipes) {
      console.log("changing recipes from to", store.allRecipes, recipes);
      store.allRecipes = recipes;
    },
  },
  getters: {
    user(store) {
      return `👥 ${store.user}`;
    },
    isAuth(store) {
      return !!store.user;
    },
    allRecipes(store) {
      return store.allRecipes || [];
    },
    getRecipeById: (state, getters) => (id) => {
      return getters.allRecipes.find(rcp => rcp.id === id);
    }
  },
  actions: {
    async refreshRecipes(context) {
      if (context.getters.allRecipes.length === 0) {
        console.log("fetching recipes...");
        try {
          let response = await fetch("http://127.0.0.1:8888/recipes");
          if (response.ok) {
            let recipes = await response.json();
            recipes = recipes.slice(0, 10);
            context.commit("setRecipes", recipes);
          } else {
            throw new Error("HTTP-Error: " + response.status);
          }
        } catch (error) {
          console.error(error);
        }
      }
    },
    deleteRecipe(context, {id}) {
      console.log("deleting recipe ", id);
      context.commit("setRecipes", context.getters.allRecipes.filter((x) => x.id !== id));      
    },
    updateRecipe(context, recipe) {
      console.log("updating recipe (actions)", recipe);
      // pitanje je kako ovo napraviti, ucinkovitije bi bilo in-place ažurirati samo jedan element polja
      // ali ako gledamo polje kao jednu varijablu stanja onda ćemo napraviti novo polje s izmijenjenim tim jednim elementom:      
      let newArray = context.getters.allRecipes.map(x => x.id == recipe.id ? recipe : x);
      context.commit("setRecipes", newArray);
    },
  },
  modules: {},
});
