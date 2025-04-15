<script lang="ts" setup>
import { ref, watch } from 'vue';
import type { Event } from '@entities/event/model';
import { formatLocalInputToISO, formatISOToLocalInput } from '@shared/lib/time-util';

const props = defineProps<{
  initialEvent?: Event;
  submitText?: string;
}>();

const emits = defineEmits<{
  (e: 'submit', event: Event): void;
}>();

const localEvent = ref<Event>({
  id: 0,
  title: '',
  description: '',
  startDateTime: '',
  endDateTime: '',
  location: ''
});

const error = ref<string | null>(null);

if (props.initialEvent) {
  localEvent.value = {
    ...props.initialEvent,
    startDateTime: formatISOToLocalInput(props.initialEvent.startDateTime),
    endDateTime: formatISOToLocalInput(props.initialEvent.endDateTime)
  };
}

watch(() => localEvent.value, (): void => {
  error.value = null;
});

function submitForm(): void {
  const formattedStart: string = formatLocalInputToISO(localEvent.value.startDateTime);
  const formattedEnd: string = formatLocalInputToISO(localEvent.value.endDateTime);

  if (new Date(formattedEnd) <= new Date(formattedStart)) {
    error.value = 'End date/time must be after start date/time';
    return;
  }

  localEvent.value.startDateTime = formattedStart;
  localEvent.value.endDateTime = formattedEnd;

  emits('submit', localEvent.value);
}
</script>

<template>
  <form @submit.prevent="submitForm">
    <div>
      <label for="title">Title:</label>
      <input id="title" v-model="localEvent.title" type="text" required/>
    </div>
    <div>
      <label for="description">Description:</label>
      <textarea id="description" v-model="localEvent.description" placeholder="Optional"></textarea>
    </div>
    <div>
      <label for="start">Start Date/Time:</label>
      <input id="start" v-model="localEvent.startDateTime" type="datetime-local" required/>
    </div>
    <div>
      <label for="end">End Date/Time:</label>
      <input id="end" v-model="localEvent.endDateTime" type="datetime-local" required/>
    </div>
    <div>
      <label for="location">Location:</label>
      <input id="location" v-model="localEvent.location" type="text" placeholder="Optional"/>
    </div>
    <p v-if="error" class="error">{{ error }}</p>
    <button type="submit">{{ submitText }}</button>
  </form>
</template>

<style lang="scss" scoped>
.error {
  color: red;
  margin-top: 0.5rem;
  font-size: 0.9rem;
}
input[type="text"],
input[type="datetime-local"],
textarea {
  width: 100%;
  padding: 0.5rem;
  margin: 0.25rem 0 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}
</style>
