<template>
  <div>
    <slot></slot>
  </div>
</template>
<style scoped>
    div {
        border: 1px gray solid;
        border-radius: 3px;
        width: 250px;
        box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
        padding: 10px;
        margin: 5px;
    }
</style>