import { useEffect, useMemo, useRef, useState } from "react";
import { getJson } from "@/api/client";
import { useSearchParams } from "react-router-dom";

/* ---------- Types (compatible with your backend) ---------- */
type Match = {
  id: number;
  externalId?: number | null;
  competition?: string | null;
  status: string;
  utcDate: string; // ISO
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

/* ---------- League definitions (12 required) ---------- */
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
const ALL_CODES = LEAGUES.map(l => l.code);

/* ---------- Small utilities ---------- */
function classNames(...xs: Array<string | false | null | undefined>) {
  return xs.filter(Boolean).join(" ");
}
function formatKickoff(iso: string) {
  const d = new Date(iso);
  return d.toLocaleString(undefined, {
    weekday: "short",
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}
function useDebounced<T>(value: T, ms = 250) {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const id = setTimeout(() => setDebounced(value), ms);
    return () => clearTimeout(id);
  }, [value, ms]);
  return debounced;
}

/* =========================================================
   Matches Page
   ========================================================= */
export default function Matches() {
  const [searchParams, setSearchParams] = useSearchParams();
  const urlLeague = searchParams.get("league");

  // initial tab from URL if valid; otherwise "ALL"
  const initialTab = useMemo(() => {
    const valid = LEAGUES.some(l => l.code === urlLeague);
    return valid ? (urlLeague as string) : "ALL";
  }, [urlLeague]);

  const [activeTab, setActiveTab] = useState<string>(initialTab);
  const [days, setDays] = useState<number>(30);
  const [query, setQuery] = useState<string>("");
  const debouncedQuery = useDebounced(query, 250);

  const [matches, setMatches] = useState<Match[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // fun UI-only “pin” state
  const [pinnedIds, setPinnedIds] = useState<Set<number>>(new Set());

  // keep URL in sync with league tab
  useEffect(() => {
    const current = new URLSearchParams(searchParams);
    if (activeTab === "ALL") {
      current.delete("league");
    } else {
      current.set("league", activeTab);
    }
    setSearchParams(current, { replace: true });
  }, [activeTab]); // eslint-disable-line react-hooks/exhaustive-deps

  const competitionsCsv = useMemo(() => {
    return activeTab === "ALL" ? ALL_CODES.join(",") : activeTab;
  }, [activeTab]);

  // fetch data
  const fetchKey = useMemo(() => `${competitionsCsv}-${days}`, [competitionsCsv, days]);
  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        setLoading(true);
        setError(null);
        const page = await getJson<ApiPage<Match>>(
            `/api/matches/upcoming?days=${days}&size=120&competitions=${encodeURIComponent(competitionsCsv)}`


      );
        if (!alive) return;
        const list = (page?.data ?? []).slice().sort((a, b) =>
            new Date(a.utcDate).getTime() - new Date(b.utcDate).getTime()
        );
        setMatches(list);
      } catch (e: any) {
        if (alive) {
          const msg =
              e?.response?.data?.message ||
              e?.response?.statusText ||
              e?.message ||
              "Failed to load matches";
          setError(msg);
        }
      } finally {
        if (alive) setLoading(false);
      }
    })();
    return () => { alive = false; };
  }, [fetchKey]);

  // keep tab in sync if URL changes (user navigates)
  useEffect(() => {
    if (urlLeague && LEAGUES.some(l => l.code === urlLeague)) {
      setActiveTab(urlLeague);
    } else if (!urlLeague) {
      // keep current
    }
  }, [urlLeague]);

  // client-side filter by team name
  const filtered = useMemo(() => {
    if (!debouncedQuery.trim()) return matches;
    const q = debouncedQuery.trim().toLowerCase();
    return matches.filter(m =>
        m.homeTeam.toLowerCase().includes(q) ||
        m.awayTeam.toLowerCase().includes(q)
    );
  }, [matches, debouncedQuery]);

  // group by competition when ALL is selected
  const grouped = useMemo(() => {
    if (activeTab !== "ALL") {
      const label = LEAGUES.find(l => l.code === activeTab)?.label ?? activeTab;
      return [[label, filtered]] as [string, Match[]][];
    }
    const map = new Map<string, Match[]>();
    for (const m of filtered) {
      const key = m.competition ?? "Other";
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(m);
    }
    for (const list of map.values()) {
      list.sort((a, b) => new Date(a.utcDate).getTime() - new Date(b.utcDate).getTime());
    }
    const order = new Map(LEAGUES.map((l, i) => [l.label, i]));
    return Array.from(map.entries()).sort(([a], [b]) => {
      const ai = order.has(a) ? (order.get(a) as number) : 999;
      const bi = order.has(b) ? (order.get(b) as number) : 999;
      if (ai !== bi) return ai - bi;
      return a.localeCompare(b);
    });
  }, [filtered, activeTab]);

  // pin handlers
  const togglePin = (id: number) => {
    setPinnedIds(prev => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id); else next.add(id);
      return next;
    });
  };

  return (
      <div className="relative min-h-[calc(100vh-7rem)] bg-gradient-to-b from-emerald-50 to-white">
        {/* decorative radial to match landing page */}
        <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top,rgba(16,185,129,0.12),transparent_60%)]" />

        {/* Header band */}
        <div className="relative border-b bg-white/80 backdrop-blur-sm">
          <div className="max-w-6xl mx-auto px-4 py-4 text-center">
            <h1 className="text-2xl font-semibold text-gray-900">Upcoming Matches</h1>
            <p className="text-sm text-gray-600 mt-1">
              Browse fixtures by league, adjust the time window, or quickly search by team name.
            </p>
          </div>
        </div>

        {/* Content container */}
        <div className="relative max-w-6xl mx-auto px-4">
          {/* Sticky Filter Bar */}
          <div className="sticky top-16 z-10 bg-white/85 backdrop-blur-sm border-b rounded-b-2xl shadow-sm">
            <div className="py-3 flex flex-col gap-3">
              {/* League tabs */}
              <div className="w-full overflow-x-auto">
                <div className="flex gap-2 min-w-max px-2">
                  <PillTab
                      label="All"
                      active={activeTab === "ALL"}
                      onClick={() => setActiveTab("ALL")}
                  />
                  {LEAGUES.map(l => (
                      <PillTab
                          key={l.code}
                          label={l.label}
                          badge={l.emoji}
                          active={activeTab === l.code}
                          onClick={() => setActiveTab(l.code)}
                          ariaLabel={`Show ${l.label}`}
                      />
                  ))}
                </div>
              </div>

              {/* Controls row */}
              <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-2 pb-1">
                <label className="inline-flex items-center gap-2 text-sm text-gray-700">
                  Days Ahead
                  <select
                      value={days}
                      onChange={(e) => setDays(parseInt(e.target.value, 10))}
                      className="rounded-xl border bg-white px-3 py-2 shadow-sm focus:outline-none focus:ring-2 focus:ring-emerald-300"
                      aria-label="Select days ahead"
                  >
                    {[3, 7, 14, 30].map(d => <option key={d} value={d}>{d} days</option>)}
                  </select>
                </label>

                <div className="relative flex-1">
                  <span className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">🔎</span>
                  <input
                      type="text"
                      value={query}
                      onChange={(e) => setQuery(e.target.value)}
                      placeholder="Quick search: team name…"
                      aria-label="Quick search by team name"
                      className="w-full rounded-xl border bg-white pl-9 pr-3 py-2 shadow-sm focus:outline-none focus:ring-2 focus:ring-emerald-300"
                  />
                </div>
              </div>
            </div>
          </div>

          {/* States */}
          <div className="mt-6">
            {loading && (
                <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                  {Array.from({ length: 6 }).map((_, i) => <SkeletonCard key={i} />)}
                </div>
            )}

            {error && (
                <ErrorNotice
                    message={error}
                    onRetry={() => {
                      // trigger refetch by toggling days briefly
                      setDays(d => (d === 30 ? 29 : 30));
                      setTimeout(() => setDays(30), 0);
                    }}
                />
            )}

            {!loading && !error && filtered.length === 0 && (
                <EmptyState label="No matches found for your filters." />
            )}
          </div>

          {/* Content grouped by league */}
          {!loading && !error && filtered.length > 0 && (
              <div className="mt-4 space-y-8 pb-10">
                {grouped.map(([leagueLabel, list]) => (
                    <section key={leagueLabel}>
                      <div className="flex items-center gap-2 mb-3">
                        <span className="text-lg font-semibold">{leagueLabel}</span>
                        {/* optional tiny chip */}
                        <span className="text-base">
                    {LEAGUES.find(l => l.label === leagueLabel)?.emoji ?? "⚽"}
                  </span>
                      </div>
                      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                        {list.map((m) => (
                            <MatchCard
                                key={m.id}
                                m={m}
                                pinned={pinnedIds.has(m.id)}
                                onTogglePin={() => togglePin(m.id)}
                            />
                        ))}
                      </div>
                    </section>
                ))}
              </div>
          )}
        </div>
      </div>
  );
}

/* ==================== UI Bits ==================== */

function PillTab(props: {
  label: string;
  active: boolean;
  onClick: () => void;
  badge?: string;
  ariaLabel?: string;
}) {
  const { label, active, onClick, badge, ariaLabel } = props;
  return (
      <button
          onClick={onClick}
          className={classNames(
              "px-4 py-2 rounded-full border transition focus:outline-none focus:ring-2",
              active
                  ? "bg-emerald-500 text-white border-emerald-500 shadow focus:ring-emerald-300"
                  : "bg-white text-gray-700 border-gray-200 hover:border-emerald-300 focus:ring-emerald-200"
          )}
          aria-label={ariaLabel ?? label}
      >
      <span className="inline-flex items-center gap-2">
        {badge ? <span className="text-base">{badge}</span> : null}
        <span>{label}</span>
      </span>
      </button>
  );
}

function SkeletonCard() {
  return (
      <div className="rounded-2xl border bg-white p-4 shadow-sm animate-pulse">
        <div className="h-3 w-20 bg-gray-200 rounded mb-2" />
        <div className="h-4 w-40 bg-gray-200 rounded" />
        <div className="mt-4 h-5 w-full bg-gray-200 rounded" />
        <div className="mt-2 h-3 w-24 bg-gray-200 rounded" />
      </div>
  );
}

function ErrorNotice({ message, onRetry }: { message: string; onRetry: () => void }) {
  return (
      <div className="rounded-2xl border border-red-200 bg-red-50 text-red-800 p-4 shadow-sm">
        <div className="font-medium">Something went wrong</div>
        <div className="text-sm mt-1">{message}</div>
        <button
            onClick={onRetry}
            className="mt-3 inline-flex items-center rounded-xl bg-red-600 text-white text-sm px-3 py-2 hover:bg-red-700"
        >
          Try again
        </button>
      </div>
  );
}

function EmptyState({ label }: { label: string }) {
  return (
      <div className="rounded-2xl border bg-white p-8 shadow-sm text-center text-gray-600">
        {label}
      </div>
  );
}

function MatchCard({
                     m, pinned, onTogglePin,
                   }: {
  m: Match;
  pinned: boolean;
  onTogglePin: () => void;
}) {
  return (
      <article
          className="group rounded-2xl border p-4 bg-white shadow-sm hover:shadow-md transition focus-within:ring-2 focus-within:ring-emerald-300"
          tabIndex={0}
      >
        <div className="flex items-start justify-between gap-3">
          <div>
            <div className="text-xs text-gray-500">Kickoff</div>
            <div className="font-medium">{formatKickoff(m.utcDate)}</div>
          </div>

          <button
              onClick={onTogglePin}
              className={classNames(
                  "ml-auto -mt-1 rounded-full border px-2 py-1 text-xs transition",
                  pinned
                      ? "bg-emerald-600 text-white border-emerald-600"
                      : "bg-white text-gray-600 border-gray-200 hover:border-emerald-300"
              )}
              aria-pressed={pinned}
              aria-label={pinned ? "Unpin match" : "Pin match"}
              title={pinned ? "Unpin match" : "Pin match"}
          >
            {pinned ? "📌 Pinned" : "📍 Pin"}
          </button>
        </div>

        <div className="flex items-center justify-between mt-4">
          <div className="font-semibold">{m.homeTeam}</div>
          <div className="text-gray-400">vs</div>
          <div className="font-semibold">{m.awayTeam}</div>
        </div>

        {(m.homeScore != null || m.awayScore != null) && (
            <div className="text-xs text-gray-500 mt-1">
              Score: {m.homeScore ?? "-"} : {m.awayScore ?? "-"}
            </div>
        )}

        {m.competition && (
            <div className="text-xs text-gray-500 mt-3">{m.competition}</div>
        )}
      </article>
  );
}
