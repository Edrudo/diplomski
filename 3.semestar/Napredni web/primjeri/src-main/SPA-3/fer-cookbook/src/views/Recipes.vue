<template>
  <div v-if="selectedRecipe">
    <h2>Recipe #{{ id }}</h2><br>
    <div class="d-flex justify-content-center">
      <recipe-card :key="selectedRecipe.id"
          v-bind:id="selectedRecipe.id"
          @recipe-updated="recipeUpdated"
          @recipe-deleted="recipeDeleted"
          can-edit="true"
        ></recipe-card> 
      </div>
  </div>
  <div v-else>
    <h2>All recipes ({{$store.getters.allRecipes.length}})</h2>
    <hr>
    <div class="container-fluid p-2 d-flex flex-wrap">
      <recipe-card v-for="recipe in allRecipes"
        :key="recipe.id"
        v-bind:id="recipe.id"
        @recipe-updated="recipeUpdated"
        @recipe-deleted="recipeDeleted"
      ></recipe-card>
    </div>
  </div>
</template>

<script>
export default {
  props: ["id"],
  data() {
    return {
      selectedRecipe: null,
      selectedRecipeIndex: -1,
    };
  },
  computed: {
    allRecipes() { 
      return this.$store.getters.allRecipes;  // ima elegantnije: pogledati mapActions, mapGetters...
    }
  },
  watch: {
    $route(to, from) {
      console.log(`Route changed: ${from.path} -> ${to.path} `);
      this.selectedRecipe = this.allRecipes.find( x => x.id == this.$route.params.id);
      this.selectedRecipeIndex = this.allRecipes.findIndex( x => x.id == this.$route.params.id);
    },
  },
  methods: {
    recipeUpdated(recipe) {
      console.log("so now i know recipe is updated...", recipe);
    },
    recipeDeleted(args) {
      if (this.selectedRecipe && this.selectedRecipe.id === args.id) {
        this.selectedRecipe = null;
      }
    },
  },
  async mounted() {
    await this.$store.dispatch('refreshRecipes');
    this.selectedRecipe = this.allRecipes.find( x => x.id == this.$route.params.id);
  }
};
</script>
