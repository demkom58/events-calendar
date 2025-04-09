import { type Event } from './model';
import { fetchRequest } from '@/shared/lib/fetchUtil';

const API_URL = import.meta.env.VITE_API_ENDPOINT;
const EVENTS_URL = `${API_URL}/events`

export const EventApi = {
    async getAll(): Promise<Event[]> {
        return await fetchRequest(`${EVENTS_URL}`);
    },
    async getById(id: number): Promise<Event> {
        return await fetchRequest(`${EVENTS_URL}/${id}`);
    },
    async create(event: Event): Promise<Event> {
        return await fetchRequest(EVENTS_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(event)
        });
    },
    async update(id: number, event: Event): Promise<Event> {
        return await fetchRequest(`${EVENTS_URL}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(event)
        });
    },
    async delete(id: number): Promise<void> {
        await fetchRequest(`${EVENTS_URL}/${id}`, {
            method: 'DELETE'
        });
    }
};
