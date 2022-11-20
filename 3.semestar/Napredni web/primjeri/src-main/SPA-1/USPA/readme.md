Recipes from here: 
  * https://github.com/fictivekin/openrecipes

Inspired by: 
  * https://www.youtube.com/watch?v=6BozpmSjk-Y&ab_channel=dcode
  * https://blog.jeremylikness.com/blog/build-a-spa-site-with-vanillajs/

## ver 01
  * Trivial DOM change via JS API.

## ver 02
  * server side app that handles versions, not in the foucus here
  * code moved to module
  * trivial routing, link overriding

## ver 03
  * Componentize: break the HTML into components:
    * initial App
    * Recipes view
    * Calculator view
  * Override all links so as not to leave the site but open "local pages" - views, that is - refactor Home and Abroad to:
    * Home - a list of all recipes
      * Fetch data from the server - async HTML function! 
      * Format and return HTML (included Bootstrap4 in the index.html for quick formatting of recipes and main menu)
    * Calculator - a calculator in the making (F<->C, etc.)

## ver04 
  * A hint of two-way binding:
    * introduce JS Proxy, use it to implement Observer pattern
    * use the Observer pattern to bind the HTML element's value to data (variables)
    * silly (re)rendering