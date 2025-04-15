import { DateTime } from 'luxon';

/**
 * Converts an ISO string (with UTC info) to a format suitable for a datetime-local input.
 * Uses Luxon to transform the date into the local timezone.
 */
export function formatISOToLocalInput(isoString: string | undefined | null): string {
    if (!isoString) return '';
    try {
        const dt = DateTime.fromISO(isoString).toLocal();
        return dt.toFormat("yyyy-MM-dd'T'HH:mm");
    } catch (error) {
        console.error("Failed to format ISO string:", isoString, error);
        return '';
    }
}

/**
 * Converts a local datetime-local input string (YYYY-MM-DDTHH:mm) back to an ISO string that includes timezone info.
 * Uses Luxon for robust parsing.
 */
export function formatLocalInputToISO(localString: string | undefined | null): string {
    if (!localString) return '';
    try {
        const dt = DateTime.fromFormat(localString, "yyyy-MM-dd'T'HH:mm", { zone: 'local' });
        return <string>dt.toISO();
    } catch (error) {
        console.error("Failed to format local input string:", localString, error);
        return '';
    }
}
