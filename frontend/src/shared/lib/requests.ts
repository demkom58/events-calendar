import axios from 'axios';
import { useNotificationStore } from '@/shared/store/notification-store';

const ax = axios.create({
    baseURL: import.meta.env.VITE_API_ENDPOINT,
    headers: { 'Content-Type': 'application/json' }
});

ax.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.data) {
            const errorData = error.response.data;
            const errorMessage = errorData.detail || errorData.title || 'An error occurred.';
            useNotificationStore().notify(errorMessage, 'error');
        } else {
            useNotificationStore().notify('Network error occurred.', 'error');
        }
        return Promise.reject(error);
    }
);

export default ax;
