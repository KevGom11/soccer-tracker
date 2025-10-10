import { Link, useNavigate } from "react-router-dom";
import { useEffect, useMemo, useRef, useState } from "react";
import { getJson } from "@/api/client";

/** ---------- Types (mirrors your backend) ---------- */
type Match = {
    id: number;
    competition?: string | null;
    status: string;
    utcDate: string;
    homeTeam: string;
    awayTeam: string;
    homeScore?: number | null;
    awayScore?: number | null;
};

type ApiPage<T> = {
    data: T[];
    count: number;
    page: number;
    size: number;
    totalPages: number;
};

/** ---------- League list (codes + labels) ---------- */
const LEAGUES: { code: string; label: string; emoji?: string }[] = [
    { code: "PL",  label: "Premier League",           emoji: "🏴‍☠️" },
    { code: "PD",  label: "La Liga",                  emoji: "🇪🇸" },
    { code: "SA",  label: "Serie A",                  emoji: "🇮🇹" },
    { code: "BL1", label: "Bundesliga",               emoji: "🇩🇪" },
    { code: "FL1", label: "Ligue 1",                  emoji: "🇫🇷" },
    { code: "CL",  label: "UEFA Champions League",    emoji: "⭐"   },
    { code: "ELC", label: "EFL Championship",         emoji: "🏴"   },
    { code: "BSA", label: "Brasileirão Série A",      emoji: "🇧🇷" },
    { code: "MLI", label: "Copa Libertadores",        emoji: "🏆"   },
    { code: "MLS", label: "Major League Soccer",      emoji: "🇺🇸" },
    { code: "CLI", label: "Copa Sudamericana",        emoji: "🥈"   },
    { code: "WC",  label: "FIFA World Cup",           emoji: "🌍"   },
];

export default function LandingPage() {
    return (
        <div className="relative bg-gradient-to-b from-emerald-50 to-white min-h-[calc(100vh-7rem)]">
            {/* decorative radial */}
            <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top,rgba(16,185,129,0.14),transparent_60%)]" />

            {/* --- Kevin Header Tagline --- */}
            <div className="text-center py-3 bg-white/80 backdrop-blur-sm border-b border-emerald-100 shadow-sm">
                <p className="text-sm text-gray-700">
                    Built by <span className="font-semibold text-emerald-700">Kevin Gomes</span> — a soccer fan and developer dedicated to bringing fans closer to the game.
                </p>
            </div>

            <Hero />
            <section className="max-w-6xl mx-auto px-4">
                <LeagueCarousel />
                <StatsRow />
                <LiveMatchPreview />
            </section>
        </div>
    );
}

/* ------------------------ HERO ------------------------ */
function Hero() {
    return (
        <section className="max-w-6xl mx-auto px-4 pt-14 pb-12 text-center">
            <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight text-emerald-700">
                Soccer Tracker
            </h1>
            <p className="mt-4 text-lg text-gray-600">
                Track your favorite leagues, teams, and matches — all in one place.
            </p>

            <div className="mt-8 flex flex-col sm:flex-row gap-3 justify-center">
                <Link
                    to="/matches"
                    className="inline-flex items-center justify-center rounded-2xl px-6 py-3 font-medium text-white bg-emerald-600 shadow hover:bg-emerald-700 transition"
                >
                    View Matches
                </Link>
                <Link
                    to="/users"
                    className="inline-flex items-center justify-center rounded-2xl px-6 py-3 font-medium text-emerald-700 bg-white border border-emerald-200 shadow-sm hover:border-emerald-400 hover:shadow transition"
                >
                    User Subscriptions
                </Link>
            </div>
        </section>
    );
}

/* ------------------ LEAGUE CAROUSEL ------------------- */
function LeagueCarousel() {
    const navigate = useNavigate();
    return (
        <section aria-labelledby="leagues" className="mt-2">
            <h2 id="leagues" className="text-lg font-semibold text-gray-900 mb-3">
                Explore by League
            </h2>
            <div
                className="flex gap-3 overflow-x-auto no-scrollbar scroll-px-4 snap-x snap-mandatory py-2"
                role="list"
            >
                {LEAGUES.map((lg) => (
                    <button
                        key={lg.code}
                        role="listitem"
                        onClick={() => navigate(`/matches?league=${encodeURIComponent(lg.code)}`)}
                        className="snap-start shrink-0 text-left min-w-[220px] px-4 py-3 rounded-2xl border bg-white hover:border-emerald-300 hover:shadow transition focus:outline-none focus:ring-2 focus:ring-emerald-300"
                        aria-label={`Open ${lg.label} matches`}
                    >
                        <div className="text-2xl">{lg.emoji ?? "⚽"}</div>
                        <div className="mt-1 text-sm font-semibold text-gray-900">{lg.label}</div>
                        <div className="text-xs text-gray-500">{lg.code}</div>
                    </button>
                ))}
            </div>
        </section>
    );
}

/* -------------------- STATS COUNTERS ------------------ */
function useCountUp(target: number, durationMs = 1000) {
    const [value, setValue] = useState(0);
    const started = useRef(false);
    const ref = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        if (!ref.current || started.current) return;
        const node = ref.current;
        const io = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting) {
                    started.current = true;
                    const start = performance.now();
                    const tick = (t: number) => {
                        const p = Math.min(1, (t - start) / durationMs);
                        setValue(Math.floor(p * target));
                        if (p < 1) requestAnimationFrame(tick);
                    };
                    requestAnimationFrame(tick);
                }
            },
            { threshold: 0.2 }
        );
        io.observe(node);
        return () => io.disconnect();
    }, [target, durationMs]);

    return { ref, value };
}

function StatCard({ label, value, suffix = "" }: { label: string; value: number; suffix?: string }) {
    const { ref, value: shown } = useCountUp(value, 900);
    return (
        <div ref={ref} className="rounded-2xl border bg-white p-5 shadow-sm">
            <div className="text-2xl font-extrabold text-emerald-700">
                {shown}
                {suffix}
            </div>
            <div className="text-sm text-gray-600">{label}</div>
        </div>
    );
}

function StatsRow() {
    return (
        <section aria-labelledby="stats" className="mt-8">
            <h2 id="stats" className="sr-only">Key stats</h2>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <StatCard label="Leagues" value={12} />
                <StatCard label="Teams" value={200} suffix="+" />
                <StatCard label="Matches Tracked" value={1000} suffix="+" />
            </div>
        </section>
    );
}

/* ------------------ LIVE MATCH PREVIEW ---------------- */
function LiveMatchPreview() {
    const [data, setData] = useState<Match[]>([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState<string | null>(null);

    // FIX: use /api prefix for backend
    const endpoint = useMemo(() => "/api/matches/upcoming?days=7&size=3", []);

    useEffect(() => {
        let ok = true;
        (async () => {
            try {
                setLoading(true);
                setErr(null);
                const res = await getJson<ApiPage<Match>>(endpoint);
                if (!ok) return;
                setData((res?.data ?? []).slice(0, 3));
            } catch (e: any) {
                if (ok) setErr(e?.response?.data?.message || e?.message || "Failed to load matches");
            } finally {
                if (ok) setLoading(false);
            }
        })();
        return () => { ok = false; };
    }, [endpoint]);

    return (
        <section aria-labelledby="preview" className="mt-10 mb-8">
            <div className="flex items-center justify-between">
                <h2 id="preview" className="text-lg font-semibold text-gray-900">Coming Up This Week</h2>
                <Link
                    to="/matches"
                    className="text-sm font-medium text-emerald-700 hover:text-emerald-800"
                >
                    View All Matches →
                </Link>
            </div>

            <div className="mt-3">
                {loading && <div className="text-gray-500">Loading fixtures…</div>}
                {err && <div className="text-red-600">Error: {err}</div>}
                {!loading && !err && data.length === 0 && (
                    <div className="text-gray-500">No upcoming matches in the next 7 days.</div>
                )}
            </div>

            <div className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                {data.map((m) => <MatchCard key={m.id} m={m} />)}
            </div>
        </section>
    );
}

function MatchCard({ m }: { m: Match }) {
    const kickoff = new Date(m.utcDate);
    return (
        <article className="rounded-2xl border p-4 bg-white shadow-sm">
            <div className="text-xs text-gray-500">Kickoff</div>
            <div className="font-medium">
                {kickoff.toLocaleString(undefined, {
                    weekday: "short",
                    year: "numeric",
                    month: "short",
                    day: "numeric",
                    hour: "2-digit",
                    minute: "2-digit",
                })}
            </div>

            <div className="flex items-center justify-between mt-3">
                <div className="font-semibold">{m.homeTeam}</div>
                <div className="text-gray-400">vs</div>
                <div className="font-semibold">{m.awayTeam}</div>
            </div>

            {m.competition && (
                <div className="text-xs text-gray-500 mt-2">{m.competition}</div>
            )}
        </article>
    );
}
