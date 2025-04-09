<script lang="ts" setup>
import {ref, watch, defineProps, defineEmits} from 'vue';
import {type Event} from '@entities/event/model';

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
  localEvent.value = {...props.initialEvent};
}

watch(() => localEvent.value, () => {
  error.value = null;
});

function submitForm() {
  if (new Date(localEvent.value.endDateTime) <= new Date(localEvent.value.startDateTime)) {
    error.value = 'End date/time must be after start date/time';
    return;
  }
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

<style scoped>
.error {
  color: red;
  margin-top: 0.5rem;
  font-size: 0.9rem;
}

/* You might also style input and textarea elements for consistency */
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
