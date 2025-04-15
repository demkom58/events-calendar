import { defineStore } from 'pinia';
import { ref } from 'vue';

const DISAPPEAR_TIMEOUT: number = 5_000; // 5 seconds

type NotificationType = 'error' | 'waring' | 'info' | 'success';

export interface Notification {
    id: number;
    message: string;
    type: NotificationType;
}

export const useNotificationStore = defineStore('notification', () => {
    const notifications = ref<Notification[]>([]);
    let nextId = 1;

    function notify(message: string, type: NotificationType = 'error'): void {
        const id = nextId++;
        notifications.value.push({ id, message, type });
        setTimeout(() => removeNotification(id), DISAPPEAR_TIMEOUT);
    }

    function removeNotification(id: number): void {
        console.log(notifications.value);
        notifications.value = notifications.value.filter(n => n.id !== id);
    }

    return { notifications, notify, removeNotification };
});
