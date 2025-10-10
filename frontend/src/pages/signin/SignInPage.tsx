import { useMemo, useState } from "react";
import { requestLoginCode, verifyLoginCode, updateMyName, getToken, getMe } from "@/api/client";
import { useNavigate } from "react-router-dom";

type Stage = "EMAIL" | "CODE" | "NAME";
const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default function SignInPage() {
    const [stage, setStage] = useState<Stage>("EMAIL");
    const [email, setEmail] = useState("");
    const [code, setCode] = useState("");
    const [name, setName] = useState("");
    const [busy, setBusy] = useState(false);
    const nav = useNavigate();

    if (getToken()) {
        nav("/", { replace: true });
        return null;
    }

    const validEmail = useMemo(() => EMAIL_RE.test(email), [email]);
    const canRequest = validEmail && !busy;
    const canVerify = code.trim().length >= 4 && !busy;
    const canFinish = name.trim().length >= 2 && !busy;

    async function requestCode() {
        setBusy(true);
        try {
            await requestLoginCode(email);
            setStage("CODE");
        } finally { setBusy(false); }
    }

    async function verifyCode() {
        setBusy(true);
        try {
            await verifyLoginCode(email, code);
            const me = await getMe();
            if (me?.name?.trim()) {
                nav("/", { replace: true });
                return;
            }
            setStage("NAME");
        } finally { setBusy(false); }
    }

    async function saveName() {
        setBusy(true);
        try {
            await updateMyName(name.trim());
            nav("/", { replace: true });
        } finally { setBusy(false); }
    }

    return (
        <div className="min-h-[80vh] flex items-center justify-center bg-gradient-to-b from-emerald-50 to-white p-6">
            <div className="w-full max-w-md bg-white rounded-2xl shadow-xl border border-gray-100 p-6">
                <h1 className="text-2xl font-bold text-emerald-700 mb-1">Sign in</h1>
                <p className="text-sm text-gray-600 mb-6">No password needed — check your email for a code.</p>

                {stage === "EMAIL" && (
                    <div className="space-y-3">
                        <label className="block">
                            <span>Email</span>
                            <input className="mt-1 w-full border rounded-lg p-2 focus:ring-2 focus:ring-emerald-300 outline-none transition" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="you@example.com" />
                        </label>
                        <button className="w-full px-4 py-2 rounded-lg bg-emerald-600 text-white hover:bg-emerald-700 disabled:opacity-50" disabled={!canRequest} onClick={requestCode}>Send Code</button>
                    </div>
                )}

                {stage === "CODE" && (
                    <div className="space-y-3">
                        <p>We sent a code to <strong>{email}</strong></p>
                        <label className="block">
                            <span>Code</span>
                            <input className="mt-1 w-full border rounded-lg p-2 tracking-widest focus:ring-2 focus:ring-emerald-300 outline-none transition" value={code} onChange={(e) => setCode(e.target.value)} placeholder="6-digit code" />
                        </label>
                        <div className="flex gap-2">
                            <button className="w-1/2 px-4 py-2 rounded-lg bg-gray-200 hover:bg-gray-300" onClick={() => setStage("EMAIL")}>Back</button>
                            <button className="w-1/2 px-4 py-2 rounded-lg bg-emerald-600 text-white hover:bg-emerald-700 disabled:opacity-50" disabled={!canVerify} onClick={verifyCode}>Verify</button>
                        </div>
                    </div>
                )}

                {stage === "NAME" && (
                    <div className="space-y-3">
                        <label className="block">
                            <span>Your Name</span>
                            <input className="mt-1 w-full border rounded-lg p-2 focus:ring-2 focus:ring-emerald-300 outline-none transition" value={name} onChange={(e) => setName(e.target.value)} placeholder="e.g., Kevin Gomes" />
                        </label>
                        <button className="w-full px-4 py-2 rounded-lg bg-emerald-600 text-white hover:bg-emerald-700 disabled:opacity-50" disabled={!canFinish} onClick={saveName}>Finish</button>
                    </div>
                )}
            </div>
        </div>
    );
}
