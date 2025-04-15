import { type AxiosResponse } from 'axios';
import type { Event } from './model';
import ax from "@shared/lib/requests.ts";

export interface ApiResponse<T> {
    data?: T;
    error?: string;
}

const EVENTS_URL: string = `/events`;

/**
 * Helper function that executes an axios request and returns an ApiResponse.
 */
async function handleRequest<T>(request: Promise<AxiosResponse<T>>): Promise<ApiResponse<T>> {
    try {
        const response = await request;
        return { data: response.data };
    } catch (error: any) {
        return { error: error.message || 'Unknown error' };
    }
}

export const EventApi = {
    async getAll(): Promise<ApiResponse<Event[]>> {
        return handleRequest(ax.get(EVENTS_URL));
    },
    async getById(id: number): Promise<ApiResponse<Event>> {
        return handleRequest(ax.get(`${EVENTS_URL}/${id}`));
    },
    async create(event: Event): Promise<ApiResponse<Event>> {
        return handleRequest(ax.post(EVENTS_URL, event));
    },
    async update(id: number, event: Event): Promise<ApiResponse<Event>> {
        return handleRequest(ax.put(`${EVENTS_URL}/${id}`, event));
    },
    async delete(id: number): Promise<ApiResponse<null>> {
        return handleRequest(ax.delete(`${EVENTS_URL}/${id}`));
    },
};
