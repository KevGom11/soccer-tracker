import { Link, NavLink } from "react-router-dom";

export default function NavBar() {
  const linkBase = "px-3 py-2 rounded-lg text-sm font-medium";
  const active = "bg-emerald-100 text-emerald-700";
  const inactive = "text-gray-600 hover:bg-gray-100 hover:text-gray-900";

  return (
    <header className="border-b bg-white sticky top-0 z-10">
      <div className="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">
        <Link to="/" className="text-lg font-semibold text-emerald-600">Soccer Tracker</Link>
        <nav className="flex items-center gap-1">
          <NavLink to="/matches" className={({isActive}) => `${linkBase} ${isActive ? active : inactive}`}>Matches</NavLink>
          <NavLink to="/users" className={({isActive}) => `${linkBase} ${isActive ? active : inactive}`}>Users</NavLink>
        </nav>
      </div>
    </header>
  );
}
