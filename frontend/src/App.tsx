import { Navigate, Route, Routes } from "react-router-dom";
import NavBar from "@/components/NavBar";
import Matches from "@/pages/Matches";
import Users from "@/pages/Users";
import LandingPage from "@/pages/LandingPage";
import SignInPage from "@/pages/signin/SignInPage";
import { getToken } from "@/api/client";

function Protected({ children }: { children: JSX.Element }) {
    // simple guard
    if (!getToken()) return <Navigate to="/signin" replace />;
    return children;
}

export default function App() {
    return (
        <div className="min-h-screen flex flex-col">
            <NavBar />
            <main className="flex-1">
                <Routes>
                    <Route path="/" element={<LandingPage />} />
                    <Route path="/matches" element={<Matches />} />
                    <Route path="/signin" element={<SignInPage />} />
                    <Route
                        path="/users"
                        element={
                            <Protected>
                                <Users />
                            </Protected>
                        }
                    />
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </main>
            <footer className="border-t text-center py-6 text-sm text-gray-500 bg-white">
                Built with React + Vite + Tailwind — <span className="font-medium text-emerald-600">By Kevin Gomes</span>
            </footer>
        </div>
    );
}

