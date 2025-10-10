import { useEffect, useMemo, useState } from "react";
import {
    createSubscription,
    deleteSubscription,
    listMySubscriptions,
    listLeagues,
    listTeams,
    type League,
    type Team,
} from "@/api/client";

export default function Subscriptions() {
    const [leagues, setLeagues] = useState<League[]>([]);
    const [selected, setSelected] = useState<string>("");
    const [teams, setTeams] = useState<Team[]>([]);
    const [subs, setSubs] = useState<{ id: number; teamId: number }[]>([]);
    const [loadingLeagues, setLoadingLeagues] = useState(true);
    const [loadingTeams, setLoadingTeams] = useState(false);
    const [savingTeamId, setSavingTeamId] = useState<number | null>(null);
    const [error, setError] = useState<string>("");

    useEffect(() => {
        (async () => {
            try {
                const [ls, mine] = await Promise.all([listLeagues(), listMySubscriptions()]);
                setLeagues(ls);
                setSubs(mine.map((s) => ({ id: s.id, teamId: s.teamId })));
            } catch (e: any) {
                setError(humanErr(e, "Failed to load leagues or your subscriptions."));
            } finally {
                setLoadingLeagues(false);
            }
        })();
    }, []);

    async function refreshSubs() {
        try {
            const mine = await listMySubscriptions();
            setSubs(mine.map((s) => ({ id: s.id, teamId: s.teamId })));
        } catch (e: any) {
            setError(humanErr(e, "Failed to refresh your subscriptions."));
        }
    }

    async function chooseLeague(code: string) {
        setSelected(code);
        setTeams([]);
        setLoadingTeams(true);
        setError("");
        try {
            const ts = await listTeams(code);
            setTeams(ts);
            if (ts.length === 0) setError("No teams found for this league.");
        } catch (e: any) {
            setError(humanErr(e, "Failed to load teams for that league."));
        } finally {
            setLoadingTeams(false);
        }
    }

    const subTeamIds = useMemo(() => new Set(subs.map((s) => s.teamId)), [subs]);

    async function toggleTeam(teamId: number) {
        setSavingTeamId(teamId);
        setError("");
        try {
            const existing = subs.find((s) => s.teamId === teamId);
            if (existing) {
                await deleteSubscription(existing.id);
            } else {
                await createSubscription(teamId);
            }
            await refreshSubs();
        } catch (e: any) {
            setError(humanErr(e, "Failed to update subscription."));
        } finally {
            setSavingTeamId(null);
        }
    }

    return (
        <div className="bg-gradient-to-b from-emerald-50/50 to-white">
            <div className="max-w-5xl mx-auto p-6 space-y-8">
                <header>
                    <h1 className="text-3xl font-bold text-emerald-700">Subscriptions</h1>
                    <p className="text-gray-600 mt-1">
                        Pick a league, then subscribe to your favorite teams to receive match updates.
                    </p>
                </header>

                {error && (
                    <div className="p-3 rounded-lg bg-red-50 text-red-700 border border-red-200">
                        {error}
                    </div>
                )}

                {/* Leagues */}
                <section className="space-y-3">
                    <h2 className="text-xl font-semibold">Leagues</h2>
                    {loadingLeagues ? (
                        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                            {[...Array(6)].map((_, i) => (
                                <div key={i} className="h-20 rounded-xl border bg-white animate-pulse" />
                            ))}
                        </div>
                    ) : leagues.length === 0 ? (
                        <p className="text-gray-600">No leagues available.</p>
                    ) : (
                        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                            {leagues.map((l) => (
                                <button
                                    key={l.code}
                                    className={`text-left border rounded-xl p-4 bg-white hover:border-emerald-300 hover:shadow-md transition group ${
                                        selected === l.code ? "border-emerald-400 ring-2 ring-emerald-200" : ""
                                    }`}
                                    onClick={() => chooseLeague(l.code)}
                                >
                                    <div className="font-semibold text-gray-800 group-hover:text-emerald-700">{l.name}</div>
                                    <div className="text-xs text-gray-500 mt-1">
                                        {l.code}{typeof l.teamCount === "number" ? ` • ${l.teamCount}` : ""}
                                    </div>
                                </button>
                            ))}
                        </div>
                    )}
                </section>

                {/* Teams in selected league */}
                {selected && (
                    <section className="space-y-3">
                        <h2 className="text-xl font-semibold">Teams in {selected}</h2>
                        {loadingTeams ? (
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                {[...Array(6)].map((_, i) => (
                                    <div key={i} className="h-14 rounded-xl border bg-white animate-pulse" />
                                ))}
                            </div>
                        ) : teams.length === 0 ? (
                            <p className="text-gray-600">No teams found for this league.</p>
                        ) : (
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                {teams.map((t) => {
                                    const on = subTeamIds.has(t.id);
                                    const busy = savingTeamId === t.id;
                                    return (
                                        <div
                                            key={t.id}
                                            className="border rounded-xl p-3 flex items-center justify-between bg-white hover:shadow transition"
                                        >
                                            <span className="font-medium text-gray-800">{t.name}</span>
                                            <button
                                                disabled={busy}
                                                className={`px-3 py-1.5 rounded-lg text-white text-sm font-medium disabled:opacity-60 ${
                                                    on ? "bg-red-600 hover:bg-red-700" : "bg-emerald-600 hover:bg-emerald-700"
                                                }`}
                                                onClick={() => toggleTeam(t.id)}
                                            >
                                                {busy ? "Saving…" : on ? "Unsubscribe" : "Subscribe"}
                                            </button>
                                        </div>
                                    );
                                })}
                            </div>
                        )}
                    </section>
                )}

                {/* Current subs quick list */}
                <section className="space-y-2">
                    <h2 className="text-xl font-semibold">Your current team subscriptions</h2>
                    {subs.length === 0 ? (
                        <p className="text-gray-600">None yet. Pick a league above to get started.</p>
                    ) : (
                        <div className="flex flex-wrap gap-2">
                            {subs.map((s) => (
                                <span
                                    key={s.id}
                                    className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-sm bg-emerald-100 text-emerald-800 border border-emerald-200"
                                    title={`Team ID: ${s.teamId}`}
                                >
                  Team {s.teamId}
                </span>
                            ))}
                        </div>
                    )}
                </section>
            </div>
        </div>
    );
}

/** Turn fetch/HTTP errors into something readable for the banner */
function humanErr(e: any, fallback: string): string {
    const msg =
        e?.response?.data?.message ||
        e?.response?.data?.error ||
        e?.message ||
        (typeof e === "string" ? e : "");
    return msg ? `${fallback} (${msg})` : fallback;
}
