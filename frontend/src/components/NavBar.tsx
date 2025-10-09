import { Link, NavLink, useNavigate } from "react-router-dom";
import { clearToken, getToken } from "@/api/client";
import { useEffect, useState } from "react";

export default function NavBar() {
    const linkBase = "px-3 py-2 rounded-lg text-sm font-medium";
    const active = "bg-emerald-100 text-emerald-700";
    const inactive = "text-gray-600 hover:bg-gray-100 hover:text-gray-900";

    const [authed, setAuthed] = useState<boolean>(!!getToken());
    const nav = useNavigate();

    // react to storage changes from other tabs
    useEffect(() => {
        const onStorage = () => setAuthed(!!getToken());
        window.addEventListener("storage", onStorage);
        return () => window.removeEventListener("storage", onStorage);
    }, []);

    const signOut = () => {
        clearToken();
        setAuthed(false);
        nav("/signin");
    };

    return (
        <header className="border-b bg-white sticky top-0 z-10">
            <div className="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">
                <Link to="/" className="text-lg font-semibold text-emerald-600">
                    Soccer Tracker
                </Link>
                <nav className="flex items-center gap-1">
                    <NavLink
                        to="/"
                        end
                        className={({ isActive }) => `${linkBase} ${isActive ? active : inactive}`}
                    >
                        Home
                    </NavLink>

                    <NavLink
                        to="/matches"
                        className={({ isActive }) => `${linkBase} ${isActive ? active : inactive}`}
                    >
                        Matches
                    </NavLink>

                    <NavLink
                        to="/users"
                        className={({ isActive }) => `${linkBase} ${isActive ? active : inactive}`}
                    >
                        Users
                    </NavLink>

                    {!authed ? (
                        <NavLink
                            to="/signin"
                            className={({ isActive }) =>
                                `${linkBase} ${
                                    isActive ? active : "bg-emerald-600 text-white hover:bg-emerald-700"
                                }`
                            }
                        >
                            Sign in
                        </NavLink>
                    ) : (
                        <button
                            onClick={signOut}
                            className="ml-2 px-3 py-2 rounded-lg text-sm font-medium bg-gray-100 text-gray-700 hover:bg-gray-200"
                        >
                            Sign out
                        </button>
                    )}
                </nav>
            </div>
        </header>
    );
}
