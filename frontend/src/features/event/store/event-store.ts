import { defineStore } from 'pinia';
import { ref } from 'vue';
import { EventApi, type ApiResponse } from '@/entities/event/api';
import type { Event } from '@/entities/event/model';

export const useEventStore = defineStore('event', () => {
    const events = ref<Event[]>([]);
    const loading = ref<boolean>(false);
    const error = ref<string | null>(null);

    async function fetchEvents(): Promise<void> {
        loading.value = true;
        const res: ApiResponse<Event[]> = await EventApi.getAll();
        if (res.error) {
            error.value = res.error;
            events.value = [];
        } else {
            events.value = res.data || [];
            error.value = null;
        }
        loading.value = false;
    }

    async function addEvent(newEvent: Event): Promise<ApiResponse<Event>> {
        const res: ApiResponse<Event> = await EventApi.create(newEvent);
        if (res.error) {
            error.value = res.error;
        } else if (res.data) {
            events.value.push(res.data);
        }
        return res;
    }

    async function updateEvent(id: number, eventData: Event): Promise<ApiResponse<Event>> {
        const res: ApiResponse<Event> = await EventApi.update(id, eventData);
        if (res.error) {
            error.value = res.error;
        } else if (res.data) {
            const index = events.value.findIndex(e => e.id === id);
            if (index !== -1) {
                events.value.splice(index, 1, res.data);
            } else {
                events.value.push(res.data);
            }
        }
        return res;
    }

    async function deleteEvent(id: number): Promise<ApiResponse<null>> {
        const res: ApiResponse<null> = await EventApi.delete(id);
        if (res.error) {
            error.value = res.error;
        } else {
            events.value = events.value.filter(e => e.id !== id);
        }
        return res;
    }

    return { events, loading, error, fetchEvents, addEvent, updateEvent, deleteEvent };
});
