import { useEffect, useMemo, useState } from "react";

/** ---------------- Types ---------------- */
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

/** ---------------- Small fetch helper ---------------- */
async function getJson<T>(url: string): Promise<T> {
  const res = await fetch(`/api${url}`, { headers: { Accept: "application/json" } });
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `Request failed with ${res.status}`);
  }
  return res.json();
}

/** ---------------- Competitions (exactly your 12) ---------------- */
const LEAGUES: { code: string; label: string }[] = [
  { code: "PL",  label: "Premier League" },
  { code: "PD",  label: "La Liga" },
  { code: "SA",  label: "Serie A" },
  { code: "BL1", label: "Bundesliga" },
  { code: "FL1", label: "Ligue 1" },
  { code: "CL",  label: "UEFA Champions League" },
  { code: "ELC", label: "EFL Championship" },
  { code: "BSA", label: "Brasileirão Série A" },
  { code: "MLI", label: "Copa Libertadores" },
  { code: "MLS", label: "Major League Soccer" },
  { code: "CLI", label: "Copa Sudamericana" },
  { code: "WC",  label: "FIFA World Cup" },
];
const ALL_CODES = LEAGUES.map(l => l.code);

/** ---------------- Component ---------------- */
export default function Matches() {
  const [matches, setMatches] = useState<Match[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Tabs: "ALL" or one of the league codes
  const [activeTab, setActiveTab] = useState<string>("ALL");
  const [days, setDays] = useState<number>(30);

  // Build competitions CSV for the request
  const competitionsCsv = useMemo(() => {
    return activeTab === "ALL" ? ALL_CODES.join(",") : activeTab;
  }, [activeTab]);

  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        setLoading(true);
        setError(null);

        // Multi-league mode when activeTab === "ALL"
        const page = await getJson<ApiPage<Match>>(
            `/matches/upcoming?days=${days}&size=120&competitions=${encodeURIComponent(competitionsCsv)}`
        );

        if (!alive) return;
        const list = (page?.data ?? []).slice().sort((a, b) =>
            new Date(a.utcDate).getTime() - new Date(b.utcDate).getTime()
        );
        setMatches(list);
      } catch (e: any) {
        if (alive) setError(e?.message ?? "Failed to load matches");
      } finally {
        if (alive) setLoading(false);
      }
    })();
    return () => {
      alive = false;
    };
  }, [competitionsCsv, days]);

  // Group by competition when "ALL" is selected
  const grouped = useMemo(() => {
    if (activeTab !== "ALL") {
      const label = LEAGUES.find(l => l.code === activeTab)?.label ?? activeTab;
      return [[label, matches]] as [string, Match[]][];
    }
    const map = new Map<string, Match[]>();
    for (const m of matches) {
      const key = m.competition ?? "Other";
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(m);
    }
    for (const list of map.values()) {
      list.sort((a, b) => new Date(a.utcDate).getTime() - new Date(b.utcDate).getTime());
    }
    // Order groups by your LEAGUES order first, then alphabetically for others
    const order = new Map(LEAGUES.map((l, i) => [l.label, i]));
    return Array.from(map.entries()).sort(([a], [b]) => {
      const ai = order.has(a) ? (order.get(a) as number) : 999;
      const bi = order.has(b) ? (order.get(b) as number) : 999;
      if (ai !== bi) return ai - bi;
      return a.localeCompare(b);
    });
  }, [matches, activeTab]);

  return (
      <div className="p-6">
        {/* Title & controls */}
        <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <h1 className="text-2xl font-semibold">Upcoming Matches</h1>

          <div className="flex items-center gap-3">
            <label className="text-sm text-gray-600">Days Ahead</label>
            <select
                value={days}
                onChange={(e) => setDays(parseInt(e.target.value, 10))}
                className="rounded-xl border bg-white px-3 py-2 shadow-sm focus:outline-none focus:ring-2 focus:ring-emerald-300"
            >
              {[3, 7, 14, 30].map(d => <option key={d} value={d}>{d} days</option>)}
            </select>
          </div>
        </div>

        {/* Tabs */}
        <div className="mt-4 w-full overflow-x-auto">
          <div className="flex gap-2 min-w-max">
            <Tab
                label="All"
                active={activeTab === "ALL"}
                onClick={() => setActiveTab("ALL")}
            />
            {LEAGUES.map(l => (
                <Tab
                    key={l.code}
                    label={l.label}
                    active={activeTab === l.code}
                    onClick={() => setActiveTab(l.code)}
                />
            ))}
          </div>
        </div>

        {/* Status */}
        <div className="mt-4">
          {loading && <div className="text-gray-500">Loading fixtures…</div>}
          {error && <div className="text-red-600">Error: {error}</div>}
          {!loading && !error && matches.length === 0 && (
              <div className="text-gray-500">No matches found for your selection.</div>
          )}
        </div>

        {/* Content grouped by league */}
        <div className="mt-4 space-y-8">
          {grouped.map(([league, list]) => (
              <section key={league}>
                <h2 className="text-lg font-semibold mb-3">{league}</h2>
                <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                  {list.map((m) => (
                      <MatchCard key={m.id} m={m} />
                  ))}
                </div>
              </section>
          ))}
        </div>
      </div>
  );
}

/** ---------------- UI bits ---------------- */
function Tab({ label, active, onClick }: { label: string; active: boolean; onClick: () => void }) {
  return (
      <button
          onClick={onClick}
          className={[
            "px-4 py-2 rounded-full border transition",
            active
                ? "bg-emerald-500 text-white border-emerald-500 shadow"
                : "bg-white text-gray-700 border-gray-200 hover:border-emerald-300"
          ].join(" ")}
      >
        {label}
      </button>
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

        {(m.homeScore != null || m.awayScore != null) && (
            <div className="text-xs text-gray-500 mt-1">
              Score: {m.homeScore ?? "-"} : {m.awayScore ?? "-"}
            </div>
        )}

        {m.competition && (
            <div className="text-xs text-gray-500 mt-2">{m.competition}</div>
        )}
      </article>
  );
}
