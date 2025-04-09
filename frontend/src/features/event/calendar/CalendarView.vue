<script lang="ts" setup>
import {ref, computed, onMounted, nextTick} from 'vue';
import {useRouter} from 'vue-router';
import {useEventStore} from '@/features/event/store/event-store';
import type {Event} from '@/entities/event/model';

const router = useRouter();
const eventStore = useEventStore();

const currentDate = ref(new Date());
const animateEvents = ref(false);

const currentMonthYear = computed(() =>
    currentDate.value.toLocaleString('default', {month: 'long', year: 'numeric'})
);

const weekdayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
const daysInMonth = computed(() => {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  const lastDay = new Date(year, month + 1, 0).getDate();
  return Array.from({length: lastDay}, (_, i) => i + 1);
});

// Offset for first day of the month (0 = Sunday, etc.)
const firstDayOffset = computed(() => {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  const firstDay = new Date(year, month, 1);
  return firstDay.getDay();
});


// Calculate the set of events that overlap with the current month
const monthlyEvents = computed(() => {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  const monthStart = new Date(year, month, 1, 0, 0, 0);
  const monthEnd = new Date(year, month + 1, 0, 23, 59, 59);
  return eventStore.events.filter(ev => {
    const evStart = new Date(ev.startDateTime);
    const evEnd = new Date(ev.endDateTime);
    // Check if event overlaps any part of this month
    return evEnd >= monthStart && evStart <= monthEnd;
  });
});


//
// Compute a mapping from event ID to a "slot" based on duration (longer events first)
// Then if durations are equal, use start time; this slot will be used to consistently order
// events across all days where they appear.
//
const eventSlots = computed(() => {
  const events = [...monthlyEvents.value];
  events.sort((a, b) => {
    const aStart = new Date(a.startDateTime);
    const aEnd = new Date(a.endDateTime);
    const bStart = new Date(b.startDateTime);
    const bEnd = new Date(b.endDateTime);
    const durationA = aEnd.getTime() - aStart.getTime();
    const durationB = bEnd.getTime() - bStart.getTime();
    if (durationA !== durationB) {
      return durationB - durationA; // longer events first
    } else {
      return aStart.getTime() - bStart.getTime(); // then earlier events
    }
  });
  const slots = new Map<number, number>();
  events.forEach((ev, idx) => {
    slots.set(ev.id, idx);
  });
  return slots;
});

/**
 * Returns events overlapping a given day sorted by their pre-computed slot index.
 * This ensures that the same event appears in the same vertical position (slot)
 * across all days it spans.
 */
function eventsByDay(day: number): Event[] {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  const dayStart = new Date(year, month, day, 0, 0, 0);
  const dayEnd = new Date(year, month, day, 23, 59, 59);

  const dayEvents = eventStore.events.filter(ev => {
    const evStart = new Date(ev.startDateTime);
    const evEnd = new Date(ev.endDateTime);
    return evStart <= dayEnd && evEnd >= dayStart;
  });

  const slots = eventSlots.value;
  dayEvents.sort((a, b) => {
    // Compare by slot index (if not found, default to zero)
    const slotA = slots.get(a.id) ?? 0;
    const slotB = slots.get(b.id) ?? 0;
    return slotA - slotB;
  });
  return dayEvents;
}

// Utility functions to check if an event starts or ends on a given day
function isEventStart(ev: Event, day: number): boolean {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  const eventStart = new Date(ev.startDateTime);
  return eventStart.getFullYear() === year &&
      eventStart.getMonth() === month &&
      eventStart.getDate() === day;
}

function isEventEnd(ev: Event, day: number): boolean {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  const eventEnd = new Date(ev.endDateTime);
  return eventEnd.getFullYear() === year &&
      eventEnd.getMonth() === month &&
      eventEnd.getDate() === day;
}

// Navigation functions for month
function nextMonth() {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  currentDate.value = new Date(year, month + 1, 1);
  loadEvents();
}

function prevMonth() {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  currentDate.value = new Date(year, month - 1, 1);
  loadEvents();
}

// Load events from store and trigger fade-in animation
async function loadEvents() {
  animateEvents.value = true;
  await eventStore.fetchEvents();
  await nextTick();
  setTimeout(() => {
    animateEvents.value = false;
  }, 500);
}

// Refresh handler
function handleRefresh() {
  loadEvents();
}

// Navigation actions:
function viewEvent(id: number) {
  router.push({name: 'EventDetailsPage', params: {id}});
}

function addEvent() {
  router.push({name: 'AddEventPage'});
}

onMounted(() => {
  loadEvents();
});
</script>

<template>
  <div class="calendar-view">
    <div class="calendar-header">
      <div class="nav-controls">
        <button @click="prevMonth">‹</button>
        <h2>{{ currentMonthYear }}</h2>
        <button @click="nextMonth">›</button>
      </div>
      <div class="action-controls">
        <button @click="handleRefresh">Refresh</button>
        <button @click="addEvent">Add Event</button>
      </div>
    </div>
    <div class="weekdays">
      <span v-for="dayName in weekdayNames" :key="dayName">{{ dayName }}</span>
    </div>
    <div class="days-grid">
      <!-- Empty cells for days before the month starts -->
      <div v-for="n in firstDayOffset" :key="'empty-' + n" class="day-cell empty"></div>

      <!-- Day cells -->
      <div v-for="day in daysInMonth" :key="day" class="day-cell">
        <div class="date-num">{{ day }}</div>
        <ul class="events-list" :class="{ 'fade-in': animateEvents }">
          <li
              v-for="event in eventsByDay(day)"
              :key="event.id"
              class="calendar-event"
              :class="{
                'event-start': isEventStart(event, day),
                'event-end': isEventEnd(event, day)
              }"
          >
            <button class="event-title" @click="viewEvent(event.id)">
              {{ event.title }}
            </button>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<style scoped>
.calendar-view {
  max-width: 900px;
  margin: 2rem auto;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f1f3f5;
  border-bottom: 1px solid #ddd;
}

.nav-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-controls h2 {
  margin: 0;
  font-size: 1.25rem;
}

.action-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  font-weight: bold;
  padding: 0.5rem;
  background: #e9ecef;
  border-bottom: 1px solid #ddd;
}

.days-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.0rem;
  padding: 0.5rem;
}

.day-cell {
  background: #fafafa;
  border: 1px solid #eee;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.day-cell.empty {
  background: transparent;
  border: none;
  padding: 0;
}

.date-num {
  font-size: 0.8rem;
  font-weight: bold;
  margin: 0.25rem;
}

.events-list {
  display: flex;
  flex-grow: 1;
  align-items: flex-start;
  flex-direction: column;
  gap: 0.25rem;
  margin: 0 0 10px;
  padding: 0;
  overflow-y: auto;
  overflow-x: hidden;
  transition: opacity 0.5s ease;
  opacity: 1;
}

.events-list.fade-in {
  opacity: 0;
  animation: fadeIn 0.5s forwards;
}

@keyframes fadeIn {
  to {
    opacity: 1;
  }
}

.calendar-event {
  width: 100%;
  box-sizing: border-box;
  background: #373737;
  color: #fff;
  padding: 0.25rem 0.4rem;
  font-size: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.calendar-event.event-start {
  margin-left: 10px;
  border-top-left-radius: 6px;
  border-bottom-left-radius: 6px;
}

.calendar-event.event-end {
  width: 80%;
  min-width: fit-content;
  margin-right: 10px;
  border-top-right-radius: 6px;
  border-bottom-right-radius: 6px;
}

.calendar-event.event-start.event-end {
  border-radius: 4px;
}

.event-title {
  cursor: pointer;
  flex-grow: 1;
  margin-right: 0.25rem;
  padding: 0.2rem 0.2rem;
}

.month-event-list ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.month-event-list li {
  padding: 0.5rem 0;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.event-title {
  cursor: pointer;
}

@media (max-width: 600px) {
  .calendar-view {
    max-width: 100%;
    margin: 0 auto;
  }

  .calendar-header {
    flex-direction: column;
    align-items: stretch;
    padding: 0.5rem;
  }

  .nav-controls,
  .action-controls {
    justify-content: space-between;
    margin-bottom: 0.5rem;
  }

  .nav-controls h2 {
    font-size: 1rem;
  }

  .weekdays {
    font-size: 0.75rem;
    padding: 0.25rem;
  }

  .days-grid {
    grid-template-columns: repeat(7, 1fr);
    padding: 0.25rem;
  }

  .day-cell {
    min-height: 80px;
  }

  .date-num {
    font-size: 0.7rem;
    margin: 0.15rem;
  }

  .calendar-event {
    font-size: 0.65rem;
    padding: 0.15rem 0.3rem;
  }

  .event-title {
    font-size: 0.65rem;
  }
}
</style>
