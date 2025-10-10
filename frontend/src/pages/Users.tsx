import { useEffect, useState } from "react";
import { api, getMe } from "@/api/client";

type AppUser = { id: number; email: string; name: string | null };

export default function Users() {
  const [users, setUsers] = useState<AppUser[] | null>(null);
  const [error, setError] = useState<string>("");

  useEffect(() => {
    let mounted = true;

    async function load() {
      try {
        const me = await getMe();
        if (!me.isAdmin) {
          if (mounted) setError("Admin only — you do not have permission to view users.");
          return;
        }
        const res = await api.get<AppUser[]>("/api/users");
        if (mounted) setUsers(res.data);
      } catch (e: any) {
        const status = e?.response?.status;
        if (status === 401) setError("Please sign in to view this page.");
        else if (status === 403) setError("Admin only — you do not have permission to view users.");
        else setError("Failed to load users.");
      }
    }

    load();
    return () => {
      mounted = false;
    };
  }, []);

  if (error) {
    return (
        <div className="max-w-3xl mx-auto p-6">
          <h1 className="text-2xl font-bold mb-3">Users</h1>
          <div className="p-3 rounded bg-red-50 text-red-700 border border-red-200">{error}</div>
        </div>
    );
  }

  if (!users) {
    return (
        <div className="max-w-3xl mx-auto p-6">
          <h1 className="text-2xl font-bold mb-3">Users</h1>
          <p>Loading…</p>
        </div>
    );
  }

  return (
      <div className="max-w-4xl mx-auto p-6">
        <h1 className="text-2xl font-bold mb-4">Users</h1>
        <div className="border rounded">
          <table className="w-full text-sm">
            <thead className="bg-gray-50">
            <tr>
              <th className="text-left p-2 border-b">ID</th>
              <th className="text-left p-2 border-b">Email</th>
              <th className="text-left p-2 border-b">Name</th>
            </tr>
            </thead>
            <tbody>
            {users.map((u) => (
                <tr key={u.id} className="odd:bg-white even:bg-gray-50">
                  <td className="p-2 border-b">{u.id}</td>
                  <td className="p-2 border-b">{u.email}</td>
                  <td className="p-2 border-b">{u.name ?? <span className="text-gray-400">—</span>}</td>
                </tr>
            ))}
            </tbody>
          </table>
        </div>
      </div>
  );
}

