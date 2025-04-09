export interface Event {
    id: number;
    title: string;
    description?: string;
    startDateTime: string;  // ISO date string
    endDateTime: string;    // ISO date string
    location?: string;
}
