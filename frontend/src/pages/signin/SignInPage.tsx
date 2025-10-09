// src/pages/signin/SignInPage.tsx
import { useEffect, useMemo, useState } from "react";
import { getToken, postJson, setToken } from "@/api/client";
import { useNavigate } from "react-router-dom";

// Backend returns { sessionToken, user }
type VerifyOk = {
    sessionToken?: string;
    token?: string; // tolerate old shape just in case
    user?: { id: number; email: string; name: string };
};

// liberal, real-world email check: local@domain.tld (accepts dots/plus etc.)
const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default function SignInPage() {
    const nav = useNavigate();
    const [step, setStep] = useState<"email" | "code">("email");
    const [emailRaw, setEmailRaw] = useState("");
    const [code, setCode] = useState("");
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState<string | null>(null);

    const email = useMemo(() => emailRaw.trim().toLowerCase(), [emailRaw]);
    const emailLooksValid = useMemo(() => EMAIL_RE.test(email), [email]);

    useEffect(() => {
        if (getToken()) {
            nav("/users", { replace: true });
        }
    }, [nav]);

    async function sendCode(e: React.FormEvent) {
        e.preventDefault();
        if (!emailLooksValid) {
            setErr("Please enter a valid email address.");
            return;
        }
        setErr(null);
        setLoading(true);
        try {
            // Backend now accepts /api/auth/request (and /request-code)
            await postJson<void>("/auth/request", { email });
            setStep("code");
        } catch (ex: any) {
            const message = ex?.response?.data?.message ?? ex?.message ?? "Failed to send code";
            setErr(message);
        } finally {
            setLoading(false);
        }
    }

    async function verify(e: React.FormEvent) {
        e.preventDefault();
        setErr(null);
        setLoading(true);
        try {
            const res = await postJson<VerifyOk>("/auth/verify", { email, code });

            const tok = res?.sessionToken || res?.token;
            if (!tok) throw new Error("Verify succeeded but no session token was returned.");
            setToken(tok);

            if (!getToken()) throw new Error("Missing session token after verify.");
            nav("/users", { replace: true });
        } catch (ex: any) {
            const message = ex?.response?.data?.message ?? ex?.message ?? "Failed to verify code";
            setErr(message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="min-h-[70vh] grid place-items-center px-4">
            <div className="w-full max-w-md">
                <div className="bg-white shadow rounded-2xl p-6 border">
                    <h1 className="text-2xl font-bold text-emerald-700 mb-1">Sign in</h1>
                    <p className="text-sm text-gray-600 mb-4">
                        We’ll email you a one-time code to finish signing in.
                    </p>

                    {err && (
                        <div className="mb-3 rounded-lg border border-red-200 bg-red-50 p-2 text-sm text-red-700">
                            {err}
                        </div>
                    )}

                    {step === "email" && (
                        <form onSubmit={sendCode} className="space-y-3">
                            <label className="block">
                                <span className="text-sm text-gray-700">Email</span>
                                <input
                                    type="email"
                                    autoComplete="email"
                                    required
                                    value={emailRaw}
                                    onChange={(e) => setEmailRaw(e.target.value)}
                                    className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                    placeholder="you@example.com"
                                />
                            </label>

                            <button
                                type="submit"
                                disabled={!emailLooksValid || loading}
                                className="w-full rounded-lg bg-emerald-600 text-white font-medium px-4 py-2 hover:bg-emerald-700 disabled:opacity-50"
                            >
                                {loading ? "Sending..." : "Send code"}
                            </button>
                        </form>
                    )}

                    {step === "code" && (
                        <form onSubmit={verify} className="space-y-3">
                            <label className="block">
                                <span className="text-sm text-gray-700">Enter code</span>
                                <input
                                    type="text"
                                    inputMode="numeric"
                                    pattern="[0-9]*"
                                    autoComplete="one-time-code"
                                    required
                                    value={code}
                                    onChange={(e) => setCode(e.target.value)}
                                    className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                    placeholder="6-digit code"
                                />
                            </label>

                            <button
                                type="submit"
                                disabled={code.trim().length === 0 || loading}
                                className="w-full rounded-lg bg-emerald-600 text-white font-medium px-4 py-2 hover:bg-emerald-700 disabled:opacity-50"
                            >
                                {loading ? "Verifying..." : "Verify & continue"}
                            </button>

                            <button
                                type="button"
                                onClick={() => setStep("email")}
                                className="w-full rounded-lg bg-gray-100 text-gray-800 font-medium px-4 py-2 hover:bg-gray-200"
                            >
                                Use a different email
                            </button>
                        </form>
                    )}
                </div>

                <div className="text-center text-xs text-gray-500 mt-3">
                    Having trouble? Check spam or try a different email.
                </div>
            </div>
        </div>
    );
}

