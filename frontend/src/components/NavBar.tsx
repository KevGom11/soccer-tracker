import { Link, NavLink, useNavigate } from "react-router-dom";
import { clearToken, getToken, getMe } from "@/api/client";
import { useEffect, useState } from "react";

export default function NavBar() {
    const linkBase = "px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200";
    const active = "bg-emerald-600 text-white shadow-md";
    const inactive = "text-gray-700 hover:bg-emerald-50 hover:text-emerald-700";

    const [authed, setAuthed] = useState<boolean>(!!getToken());
    const [isAdmin, setIsAdmin] = useState<boolean>(false);
    const nav = useNavigate();

    useEffect(() => {
        const onStorage = () => setAuthed(!!getToken());
        window.addEventListener("storage", onStorage);
        return () => window.removeEventListener("storage", onStorage);
    }, []);

    useEffect(() => {
        if (authed) {
            getMe().then((m) => setIsAdmin(!!m.isAdmin)).catch(() => setIsAdmin(false));
        } else {
            setIsAdmin(false);
        }
    }, [authed]);

    function signOut() {
        clearToken();
        setAuthed(false);
        nav("/");
    }

    return (
        <header className="bg-white/90 backdrop-blur border-b sticky top-0 z-50 shadow-sm">
            <div className="max-w-6xl mx-auto p-3 flex items-center justify-between">
                <Link to="/" className="font-bold text-lg text-emerald-700 hover:text-emerald-800 transition">
                    ⚽ SoccerTracker
                </Link>
                <nav className="flex items-center gap-2">
                    <NavLink to="/" className={({ isActive }) => linkBase + " " + (isActive ? active : inactive)}>Home</NavLink>
                    <NavLink to="/matches" className={({ isActive }) => linkBase + " " + (isActive ? active : inactive)}>Matches</NavLink>
                    {authed && (
                        <NavLink to="/subscriptions" className={({ isActive }) => linkBase + " " + (isActive ? active : inactive)}>Subscriptions</NavLink>
                    )}
                    {authed && isAdmin && (
                        <NavLink to="/users" className={({ isActive }) => linkBase + " " + (isActive ? active : inactive)}>Users</NavLink>
                    )}
                    {!authed ? (
                        <NavLink to="/signin" className={({ isActive }) => linkBase + " " + (isActive ? active : inactive)}>Sign in</NavLink>
                    ) : (
                        <button
                            onClick={signOut}
                            className="ml-2 px-4 py-2 rounded-lg text-sm font-medium bg-gray-100 text-gray-700 hover:bg-gray-200 transition"
                        >
                            Sign out
                        </button>
                    )}
                </nav>
            </div>
        </header>
    );
}

