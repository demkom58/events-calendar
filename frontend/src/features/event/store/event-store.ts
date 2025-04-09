import {defineStore} from 'pinia';
import {ref} from 'vue';
import {EventApi} from '@/entities/event/api';
import {type Event} from '@/entities/event/model';

export const useEventStore = defineStore('event', () => {
    const events = ref<Event[]>([]);
    const loading = ref(false);
    const error = ref<string | null>(null);

    async function fetchEvents() {
        loading.value = true;
        try {
            events.value = await EventApi.getAll()
        } catch (err: any) {
            error.value = err.message;
        } finally {
            loading.value = false;
        }
    }

    async function addEvent(newEvent: Event) {
        events.value.push(await EventApi.create(newEvent));
    }

    async function updateEvent(id: number, eventData: Event) {
        const index = events.value.findIndex(e => e.id === id);
        if (index !== -1) {
            events.value[index] = await EventApi.update(id, eventData);
        }
    }

    async function deleteEvent(id: number) {
        await EventApi.delete(id);
        events.value = events.value.filter(e => e.id !== id);
    }

    return { events, loading, error, fetchEvents, addEvent, updateEvent, deleteEvent };
});
