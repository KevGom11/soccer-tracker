import { Navigate, Route, Routes } from "react-router-dom";
import NavBar from "@/components/NavBar";
import Footer from "@/components/Footer";
import Matches from "@/pages/Matches";
import Users from "@/pages/Users";
import LandingPage from "@/pages/LandingPage";
import SignInPage from "@/pages/signin/SignInPage";
import ProfileSetup from "@/pages/profile/ProfileSetup";
import Subscriptions from "@/pages/Subscriptions";
import { useEffect, useState } from "react";
import { getMe, getToken } from "@/api/client";

function Protected({ children }: { children: JSX.Element }) {
    const [state, setState] = useState<"loading" | "ok" | "anon">("loading");
    const [needsProfile, setNeedsProfile] = useState(false);

    useEffect(() => {
        const t = getToken();
        if (!t) { setState("anon"); return; }
        getMe().then((m) => {
            setNeedsProfile(!m.name);
            setState("ok");
        }).catch(() => setState("anon"));
    }, []);

    if (state === "loading") return null;
    if (state === "anon") return <Navigate to="/signin" replace />;
    if (needsProfile) return <Navigate to="/profile-setup" replace />;
    return children;
}

export default function App() {
    return (
        <div className="min-h-screen flex flex-col bg-white">
            <NavBar />
            <main className="flex-1">
                <Routes>
                    <Route path="/" element={<LandingPage />} />
                    <Route path="/signin" element={<SignInPage />} />
                    <Route path="/profile-setup" element={<ProfileSetup />} />
                    {/* PUBLIC */}
                    <Route path="/matches" element={<Matches />} />
                    {/* PROTECTED */}
                    <Route path="/subscriptions" element={<Protected><Subscriptions /></Protected>} />
                    <Route path="/users" element={<Protected><Users /></Protected>} />
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </main>
            <Footer />
        </div>
    );
}
