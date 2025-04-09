/**
 * A helper function that wraps the Fetch API with enhanced error handling and content type handling.
 * @param url - The endpoint URL.
 * @param options - Fetch options (method, headers, body, etc.).
 * @returns A Promise resolving to the expected type T.
 * @throws An error if the response is not ok.
 */
export async function fetchRequest<T>(
    url: string,
    options: RequestInit = {},
): Promise<T> {
    try {
        const response = await fetch(url, options);

        if (!response.ok) {
            // Try to extract error details from the response body:
            let errorMsg = `HTTP error: ${response.status} (${response.statusText})`;
            try {
                const errorText = await response.text();
                errorMsg += ` - ${errorText}`;
            } catch (innerError) {
                // Ignore if error reading text fails.
            }
            return Promise.reject(new Error(errorMsg));
        }

        // Check content type to decide how to parse:
        const contentType = response.headers.get('content-type') || '';
        if (contentType.includes('application/json')) {
            return (await response.json()) as T;
        } else {
            // Fallback: return as text (or you could decide to return a raw response)
            return (await response.text()) as unknown as T;
        }
    } catch (error) {
        console.error('Fetch request error:', error);
        throw error;
    }
}

/**
 * Constructs a URL query string from an object.
 * - Arrays are supported by appending each value with the same key.
 * @param params - An object representing the query parameters.
 * @returns A query string starting with '?' if parameters exist; otherwise, an empty string.
 */
export function buildQueryString(params: Record<string, unknown>): string {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
            if (Array.isArray(value)) {
                value.forEach((item) => query.append(key, String(item)));
            } else {
                query.append(key, String(value));
            }
        }
    });
    const queryString = query.toString();
    return queryString ? `?${queryString}` : '';
}
