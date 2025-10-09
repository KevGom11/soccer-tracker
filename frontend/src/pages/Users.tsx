import { useEffect, useState } from "react";
import { getJson } from "@/api/client";

type User = {
  id: number;
  email: string;
  name: string;
  createdAt: string;
};

export default function Users() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        // baseURL = /api ; token automatically attached by client.ts
        const data = await getJson<User[]>("/users");
        setUsers(data);
        setError(null);
      } catch (e: any) {
        const msg =
            e?.response?.data?.message ||
            e?.response?.statusText ||
            e?.message ||
            "Failed to fetch users";
        setError(msg);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  return (
      <div className="max-w-6xl mx-auto px-4 py-6">
        <h1 className="text-2xl font-bold">Users</h1>
        {loading && <div className="mt-6 text-gray-500">Loading…</div>}
        {error && <div className="mt-6 text-red-600">{error}</div>}

        {!loading && !error && users.length === 0 && (
            <div className="mt-6 text-gray-600">No users yet.</div>
        )}

        <div className="mt-6 grid gap-3">
          {users.map((u) => (
              <div key={u.id} className="p-4 rounded-2xl shadow-sm bg-white border">
                <div className="font-semibold">{u.name || "(No name)"}</div>
                <div className="text-sm text-gray-600">{u.email}</div>
                <div className="text-xs text-gray-500 mt-1">
                  Joined: {new Date(u.createdAt).toLocaleString()}
                </div>
              </div>
          ))}
        </div>
      </div>
  );
}
