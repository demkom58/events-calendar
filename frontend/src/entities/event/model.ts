export interface Event {
    id: number;
    title: string;
    description?: string;
    startDateTime: string;  // ISO date string with timezone
    endDateTime: string;    // ISO date string with timezone
    location?: string;
}
