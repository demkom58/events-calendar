<script lang="ts" setup>
import {ref, onMounted} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import EventEditor from '@features/event/event-editor/EventEditor.vue';
import {useEventStore} from '@features/event/store/event-store';
import {EventApi} from '@entities/event/api';
import type {Event} from '@entities/event/model';

const route = useRoute();
const router = useRouter();
const eventStore = useEventStore();

const event = ref<Event | null>(null);
const isEditing = ref<boolean>(false);

async function toCalendarPage(): Promise<void> {
  await router.push({name: 'CalendarPage'});
}

async function loadEvent(): Promise<void> {
  const id: number = Number(route.params.id);
  const res = await EventApi.getById(id);
  if (res.error) {
    console.error("Error loading event:", res.error);
    event.value = null;
  } else {
    event.value = res.data || null;
  }
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleString();
}

async function updateEvent(updated: Event): Promise<void> {
  // Update only the single event via the store update function.
  const res = await eventStore.updateEvent(updated.id, updated);
  if (res.error) {
    console.error("Error updating event:", res.error);
    return;
  }
  isEditing.value = false;
  // Update the local event reference using the updated event.
  event.value = res.data || updated;
}

async function deleteEvent(): Promise<void> {
  if (confirm('Are you sure you want to delete this event?')) {
    await eventStore.deleteEvent(Number(route.params.id));
    await toCalendarPage();
  }
}

onMounted((): void => {
  loadEvent();
});
</script>

<template>
  <div>
    <button @click="toCalendarPage">Back</button>
    <div v-if="event">
      <h1>Event Details</h1>
      <div v-if="!isEditing">
        <p><strong>Title:</strong> {{ event.title }}</p>
        <p><strong>Description:</strong> {{ event.description || 'N/A' }}</p>
        <p><strong>Start:</strong> {{ formatDate(event.startDateTime) }}</p>
        <p><strong>End:</strong> {{ formatDate(event.endDateTime) }}</p>
        <p><strong>Location:</strong> {{ event.location || 'N/A' }}</p>
        <div class="controls">
          <button @click="isEditing = true">Edit</button>
          <button @click="deleteEvent">Delete</button>
        </div>
      </div>
      <div v-else>
        <EventEditor :initialEvent="event" submitText="Save Changes" @submit="updateEvent"/>
        <button @click="isEditing = false" style="margin-top: 0.5rem">Cancel</button>
      </div>
    </div>
    <div v-else>
      <p>Loading event...</p>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
</style>
