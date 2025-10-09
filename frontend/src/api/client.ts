// src/api/client.ts
// Minimal fetch wrapper with baseURL + session token header.

const BASE = "/api"; // Vite proxy recommended: /api -> http://localhost:8080

const TOKEN_KEY = "sessionToken";

export function getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
}
export function setToken(token: string) {
    localStorage.setItem(TOKEN_KEY, token);
}
export function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
}

function withBase(url: string) {
    // If caller passes absolute (http...) keep it; else prefix /api
    if (/^https?:\/\//i.test(url)) return url;
    return `${BASE}${url.startsWith("/") ? "" : "/"}${url}`;
}

type FetchOpts = Omit<RequestInit, "headers"> & {
    headers?: Record<string, string>;
};

async function request<T>(method: string, url: string, opts: FetchOpts = {}): Promise<T> {
    const headers: Record<string, string> = {
        "Accept": "application/json",
        ...opts.headers,
    };

    // Attach session token if present
    const token = getToken();
    if (token) {
        headers["X-Session-Token"] = token;
    }

    const res = await fetch(withBase(url), { ...opts, method, headers });

    // Grab token if backend returns it in header on /auth/verify
    const headerToken = res.headers.get("X-Session-Token");
    if (headerToken) setToken(headerToken);

    if (!res.ok) {
        let message = `${res.status} ${res.statusText}`;
        try {
            const data = await res.json();
            if (data?.message) message = data.message;
            // bubble the json error as well
            throw Object.assign(new Error(message), { response: { status: res.status, data } });
        } catch {
            throw Object.assign(new Error(message), { response: { status: res.status } });
        }
    }

    // Some endpoints may return no content
    const text = await res.text();
    if (!text) return undefined as unknown as T;

    try {
        return JSON.parse(text) as T;
    } catch {
        // Non-JSON response
        return text as unknown as T;
    }
}

export function getJson<T>(url: string, opts?: FetchOpts) {
    return request<T>("GET", url, opts);
}
export function postJson<T>(url: string, body?: any, opts?: FetchOpts) {
    return request<T>("POST", url, {
        ...opts,
        headers: { "Content-Type": "application/json", ...(opts?.headers || {}) },
        body: body !== undefined ? JSON.stringify(body) : undefined,
    });
}
export function del<T>(url: string, opts?: FetchOpts) {
    return request<T>("DELETE", url, opts);
}
