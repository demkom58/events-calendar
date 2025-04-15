
<script lang="ts" setup>
import { useNotificationStore } from '@/shared/store/notification-store';
const notificationStore = useNotificationStore();
</script>

<template>
  <div class="notification-wrapper">
    <div
        v-for="n in notificationStore.notifications"
        :key="n.id"
        :class="['notification', n.type]"
        @click="notificationStore.removeNotification(n.id)"
    >
      {{ n.message }}
    </div>
  </div>
</template>

<style lang="scss" scoped>
.notification-wrapper {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.notification {
  padding: 10px 20px;
  border-radius: 4px;
  color: #fff;
  cursor: pointer;
  opacity: 0.9;
  transition: opacity 0.3s;

  &:hover {
    opacity: 1;
  }

  &.error {
    background-color: #e74c3c;
  }

  &.info {
    background-color: #3498db;
  }

  &.success {
    background-color: #2ecc71;
  }

  &.warning {
    background-color: #f39c12;
  }
}


</style>
