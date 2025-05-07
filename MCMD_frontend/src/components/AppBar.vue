<template>
  <v-app-bar color="primary" prominent>
    <v-app-bar-nav-icon variant="text" @click="$emit('toggle-drawer')"></v-app-bar-nav-icon>

    <v-toolbar-title>Magnetocaloric Materials Database (MCMD)</v-toolbar-title>

    <v-spacer></v-spacer>

    <v-btn variant="text" icon="mdi-home" to="/"></v-btn>
    <v-btn variant="text" icon="mdi-database" to="/database"></v-btn>
    <v-btn variant="text" icon="mdi-information" to="/about"></v-btn>
  </v-app-bar>
</template>

<script>
export default {
  name: 'AppBar',
  data: () => ({
    search: '',
  }),
  emits: ['toggle-drawer']
}
</script>

<style scoped>
.v-app-bar {
  position: fixed !important;
  z-index: 1000 !important;
}
</style> 