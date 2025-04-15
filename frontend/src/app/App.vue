<script setup lang="ts">
import Notifications from '@shared/components/Notifications.vue';
</script>

<template>
  <RouterView />
  <Notifications />
</template>

<style lang="scss">
body {
  margin: 40px;
  font-family: Arial, sans-serif;
  background-color: #f8f9fa;
}
</style>
