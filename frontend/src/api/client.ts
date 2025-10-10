// src/api/client.ts
import axios from "axios";

const API_BASE = (import.meta.env.VITE_API_BASE ?? "/").trim();

// ---- Session token ----
const TOKEN_KEY = "session_token";
export function getToken(): string | null { return localStorage.getItem(TOKEN_KEY); }
export function setToken(token: string) { if (!token) return; localStorage.setItem(TOKEN_KEY, token); window.dispatchEvent(new Event("storage")); }
export function clearToken() { localStorage.removeItem(TOKEN_KEY); window.dispatchEvent(new Event("storage")); }

// ---- Axios ----
export const api = axios.create({
    baseURL: API_BASE || "/",
    withCredentials: false,
    headers: { "Content-Type": "application/json" },
});
api.interceptors.request.use((config) => {
    const tok = getToken();
    if (tok) config.headers["X-Session-Token"] = tok;
    return config;
});

// ---- Helpers ----
export async function getJson<T>(path: string): Promise<T> { const r = await api.get<T>(path); return r.data; }
export async function postJson<T>(path: string, body?: any): Promise<T> { const r = await api.post<T>(path, body ?? {}); return r.data; }
export async function putJson<T>(path: string, body?: any): Promise<T> { const r = await api.put<T>(path, body ?? {}); return r.data; }
export async function del<T>(path: string): Promise<T> { const r = await api.delete<T>(path); return r.data; }


type VerifyResp = { token?: string; sessionToken?: string };
export async function requestLoginCode(email: string): Promise<void> {
    await postJson("/api/auth/request", { email });
}
export async function verifyLoginCode(email: string, code: string): Promise<{ token: string }> {
    const r = await api.post<VerifyResp>("/api/auth/verify", { email, code });
    const bodyToken = r.data?.token || r.data?.sessionToken;
    const headerToken =
        (r.headers["x-session-token"] as string | undefined) ??
        (r.headers["X-Session-Token"] as unknown as string | undefined);
    const tok = bodyToken || headerToken;
    if (!tok) throw new Error("No session token returned from /api/auth/verify");
    setToken(tok);
    return { token: tok };
}


// ME (PROFILE)

export type Me = { id: number; email: string; name: string | null; isAdmin: boolean };
export async function getMe(): Promise<Me> { return getJson<Me>("/me"); }
export async function updateMyName(name: string): Promise<Me> { return putJson<Me>("/me/profile", { name }); }


// SUBSCRIPTIONS (/api/subscriptions)

export type Subscription = { id: number; teamId: number; email?: string };
export async function listMySubscriptions(): Promise<Subscription[]> { return getJson<Subscription[]>("/api/subscriptions"); }
export async function createSubscription(teamId: number): Promise<Subscription> { return postJson<Subscription>("/api/subscriptions", { teamId }); }
export async function deleteSubscription(id: number): Promise<void> { await del<void>(`/api/subscriptions/${id}`); }

// LEAGUES / TEAMS  (served by our new LeagueController)

export type League = { code: string; name: string; teamCount?: number };
export type Team = { id: number; name: string; league?: string };

export async function listLeagues(): Promise<League[]> {
    return getJson<League[]>("/api/leagues");
}

export async function listTeams(leagueCode: string): Promise<Team[]> {
    return getJson<Team[]>(`/api/leagues/${leagueCode}/teams`);
}
