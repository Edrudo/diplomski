<template>

  <small-card v-if="!editMode">
    <router-link :to="'/recipes/' + id" class="mr-3">
      <img class="card-img-top" style="width: 190px;" :src="recipe.image">
    </router-link>

    <div class="card-body">
      <h5 class="card-title">{{ recipe.name }}</h5>
      <p class="card-text">{{ recipe.description }}</p>
      <span class="badge badge-primary">
        Cook/prep time: {{ recipe.cookTime }}/{{ recipe.prepTime }}
      </span>
      <span class="badge badge-secondary">Yield: {{ recipe.recipeYield }}</span>
      <span class="badge badge-success">Pusblished: {{ recipe.datePublished }}</span>
      <a :href="recipe.url" target="_blank" class="card-link">
        <span class="badge badge-success">{{ recipe.url.substring(0, 20) }}...</span>
      </a>
    </div>
    <details v-if="recipe.ingredients">
      <summary><h3>Ingredients</h3></summary>
      <ul class="list-group list-group-flush">
        <li v-for="ingredient in recipe.ingredients" :key="ingredient" class="list-group-item">{{ ingredient }} </li>
      </ul>
    </details> 
    <button class="btn btn-danger" @click="deleteRecipe">Delete</button>
    <button v-if="canEdit" class="btn btn-primary" @click="editMode = true">Edit</button>
  </small-card>
  <div v-if="editMode" class="editForm">
    	<form @submit.prevent="submitChanges">
        <div class="form-group">
          <label for="name">Name</label>
          <input type="text" class="form-control" id="name" placeholder="name"  v-model="recipe.name">
        </div>
        
        <div class="form-group">
          <label for="description">Description</label>
          <textarea class="form-control" id="description" rows="3" v-model="recipe.description"></textarea>
        </div>
        
        <button class="btn btn-success" type="submit">Save</button>
        <button class="btn btn-warning" @click.stop="exitSingleRecipe()">Cancel</button>
      </form>
      
  </div>
</template>


<script>
export default {
  emits: ["recipeDeleted", "recipeUpdated"], 
  props: [
    "id",
    "canEdit"
  ],

  data() {
    return {
      editMode: false,
      recipe: {url:""},
    }
  },  
  methods: {
    async deleteRecipe() {
      await this.$store.dispatch('deleteRecipe', { id: this.id });
      this.$emit('recipeDeleted', { id: this.id });
    },
    exitSingleRecipe() {
      this.$router.push({ path: '/recipes' });
    }, 
    async submitChanges() {
      console.log("submitting changes");
      await this.$store.dispatch('updateRecipe', this.recipe);
      this.$emit('recipeUpdated', this.recipe);
      this.exitSingleRecipe();
    }
  },
  async created() {
    this.recipe = {... await this.$store.getters.getRecipeById(this.id) };    
  }
};

</script>
<style scoped>
  div.card-body .badge {
      white-space: pre-wrap;
  }
  div.editForm {
    min-width: 500px;
    border: 0.2rem gray solid;
    border-radius: 3px;
    padding: 1.5rem;
  }
</style>