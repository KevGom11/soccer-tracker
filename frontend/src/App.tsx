import { Route, Routes, Navigate } from "react-router-dom";
import NavBar from "@/components/NavBar";
import Matches from "@/pages/Matches";
import Users from "@/pages/Users";

export default function App() {
  return (
    <div className="min-h-screen flex flex-col">
      <NavBar />
      <main className="flex-1">
        <Routes>
          <Route path="/" element={<Navigate to="/matches" replace />} />
          <Route path="/matches" element={<Matches />} />
          <Route path="/users" element={<Users />} />
        </Routes>
      </main>
      <footer className="border-t text-center py-6 text-sm text-gray-500 bg-white">
        Built with React + Vite + Tailwind
      </footer>
    </div>
  );
}
