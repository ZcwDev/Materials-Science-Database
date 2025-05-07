<template>
    <v-alert
      v-if="message"
      type="error"
      closable
      class="mb-4"
      @click:close="$emit('update:message', null)"
    >
      {{ message }}
    </v-alert>
  </template>
  
  <script>
  export default {
    name: 'ErrorAlert',
    
    props: {
      message: {
        type: String,
        default: null
      }
    }
  }
  </script>
  