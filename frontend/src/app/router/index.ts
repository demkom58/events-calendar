import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';


const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'CalendarPage',
        component: () => import('@/pages/calendar-page/CalendarPage.vue')
    },
    {
        path: '/events/new',
        name: 'AddEventPage',
        component: () => import('@/pages/add-event-page/AddEventPage.vue')
    },
    {
        path: '/events/:id',
        name: 'EventDetailsPage',
        component: () => import('@/pages/event-details-page/EventDetailsPage.vue'),
        props: true
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;
