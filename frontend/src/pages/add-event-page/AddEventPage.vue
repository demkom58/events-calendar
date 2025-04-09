<script lang="ts" setup>
import {useRouter} from 'vue-router';
import EventEditor from '@features/event/event-editor/EventEditor.vue';
import {useEventStore} from '@features/event/store/event-store';
import {type Event} from '@entities/event/model';

const router = useRouter();
const eventStore = useEventStore();

async function toCalendarPage() {
  await router.push({name: 'CalendarPage'});
}

async function handleSubmit(newEvent: Event) {
  await eventStore.addEvent(newEvent);
  await toCalendarPage();
}
</script>

<template>
  <button @click="toCalendarPage">Back</button>
  <div class="add-event-page">
    <h1>Add New Event</h1>
    <EventEditor submitText="Create Event" @submit="handleSubmit"/>
  </div>
</template>

