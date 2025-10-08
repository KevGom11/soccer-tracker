import { Route, Routes } from "react-router-dom";
import NavBar from "@/components/NavBar";
import Matches from "@/pages/Matches";
import Users from "@/pages/Users";
import LandingPage from "@/pages/LandingPage";

export default function App() {
    return (
        <div className="min-h-screen flex flex-col">
            <NavBar />
            <main className="flex-1">
                <Routes>
                    <Route path="/" element={<LandingPage />} />
                    <Route path="/matches" element={<Matches />} />
                    <Route path="/users" element={<Users />} />
                </Routes>
            </main>
            <footer className="border-t text-center py-6 text-sm text-gray-500 bg-white">
                Built with React + Vite + Tailwind — <span className="font-medium text-emerald-600">By Kevin Gomes</span>
            </footer>
        </div>
    );
}
