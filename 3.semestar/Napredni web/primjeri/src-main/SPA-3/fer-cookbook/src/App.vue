<template>
  <div v-if="isAuth">
    <div class="d-flex justify-content-between bg-light border border-bottom border-dark p-3">
        <h1>My cookbook (tag 8.0)</h1>
        <h2 class="d-flex">
            <div class="nav d-flex ml-5">
                <router-link to="/" class="mr-3">Recipes</router-link>
                <router-link to="/calculator" class="mr-3">Calculator</router-link>
                <router-link to="/disco" class="mr-3">Disco!</router-link>
            </div>
            <div class="ml-5">{{ $store.getters.user }}</div>
        </h2>
    </div>
    <router-view />
  </div>
  <div v-else class="d-flex justify-content-center p-5">
    <user-login class="mt-5"></user-login>
  </div>
</template>

<script>
import UserLogin from './components/UserLogin.vue';
export default ({
  components: {
    UserLogin
  },
  computed: {
    isAuth() {
      return this.$store.getters.isAuth;
    }
  }
})
</script>


<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
div.nav a.router-link-exact-active {
  background-color:gold;
}
</style>
