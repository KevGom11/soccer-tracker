import { useEffect, useState } from "react";
import { getMe, updateMyName } from "@/api/client";
import { useNavigate } from "react-router-dom";

export default function ProfileSetup() {
    const [name, setName] = useState("");
    const [saving, setSaving] = useState(false);
    const nav = useNavigate();

    useEffect(() => {
        getMe().then(m => setName(m.name ?? ""));
    }, []);

    async function save() {
        if (!name.trim()) return;
        setSaving(true);
        try {
            await updateMyName(name.trim());
            nav("/subscriptions", { replace: true });
        } finally {
            setSaving(false);
        }
    }

    return (
        <div className="min-h-[70vh] grid place-items-center p-6 bg-gradient-to-b from-gray-50 to-white">
            <div className="w-full max-w-md bg-white rounded-2xl shadow-lg p-6 border border-gray-100">
                <h1 className="text-2xl font-bold mb-4 text-emerald-700">Complete Your Profile</h1>
                <label className="block mb-3">
                    <span className="text-gray-700 font-medium">Your Name</span>
                    <input
                        className="mt-1 w-full border rounded-lg p-2 focus:ring-2 focus:ring-emerald-300 outline-none transition"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="e.g., Kevin Gomes"
                    />
                </label>
                <button
                    className="w-full mt-4 px-4 py-2 rounded-lg bg-emerald-600 text-white font-medium hover:bg-emerald-700 disabled:opacity-50"
                    disabled={!name.trim() || saving}
                    onClick={save}
                >
                    {saving ? "Saving..." : "Save"}
                </button>
            </div>
        </div>
    );
}
