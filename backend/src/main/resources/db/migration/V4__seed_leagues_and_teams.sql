-- Create league table (idempotent pattern)
CREATE TABLE IF NOT EXISTS league (
                                      code  VARCHAR(10) PRIMARY KEY,
    name  VARCHAR(120) NOT NULL
    );

-- Upsert helper (PostgreSQL). If you're on H2/MySQL, adjust the ON CONFLICT/ON DUPLICATE KEY accordingly.
-- Seed the 12 leagues you use in the app
INSERT INTO league(code, name) VALUES
                                   ('PL',  'Premier League'),
                                   ('PD',  'La Liga'),
                                   ('SA',  'Serie A'),
                                   ('BL1', 'Bundesliga'),
                                   ('FL1', 'Ligue 1'),
                                   ('CL',  'UEFA Champions League'),
                                   ('ELC', 'EFL Championship'),
                                   ('BSA', 'Brasileirão Série A'),
                                   ('MLI', 'Copa Libertadores'),
                                   ('MLS', 'Major League Soccer'),
                                   ('CLI', 'Copa Sudamericana'),
                                   ('WC',  'FIFA World Cup')
    ON CONFLICT (code) DO UPDATE SET name = EXCLUDED.name;

-- Ensure team table has a league column (nullable OK)
-- If your schema already has it, this is a no-op for Postgres
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='team' AND column_name='league'
  ) THEN
    EXECUTE 'ALTER TABLE team ADD COLUMN league VARCHAR(10)';
END IF;
END $$;

-- ===========
-- PREMIER LEAGUE (PL) — 20 clubs (2024-25)
-- ===========
INSERT INTO team(name, league)
VALUES
    ('Arsenal', 'PL'),
    ('Aston Villa', 'PL'),
    ('AFC Bournemouth', 'PL'),
    ('Brentford', 'PL'),
    ('Brighton & Hove Albion', 'PL'),
    ('Chelsea', 'PL'),
    ('Crystal Palace', 'PL'),
    ('Everton', 'PL'),
    ('Fulham', 'PL'),
    ('Ipswich Town', 'PL'),
    ('Leicester City', 'PL'),
    ('Liverpool', 'PL'),
    ('Manchester City', 'PL'),
    ('Manchester United', 'PL'),
    ('Newcastle United', 'PL'),
    ('Nottingham Forest', 'PL'),
    ('Southampton', 'PL'),
    ('Tottenham Hotspur', 'PL'),
    ('West Ham United', 'PL'),
    ('Wolverhampton Wanderers', 'PL')
    ON CONFLICT DO NOTHING;

-- ===========
-- LA LIGA (PD) — 20 clubs (2024-25)
-- Note: La Liga promotes/relegates annually; this seed uses a recent stable set.
-- ===========
INSERT INTO team(name, league)
VALUES
    ('Real Madrid', 'PD'),
    ('FC Barcelona', 'PD'),
    ('Atlético de Madrid', 'PD'),
    ('Athletic Club', 'PD'),
    ('Real Sociedad', 'PD'),
    ('Villarreal', 'PD'),
    ('Valencia', 'PD'),
    ('Real Betis', 'PD'),
    ('Sevilla FC', 'PD'),
    ('Getafe', 'PD'),
    ('Girona', 'PD'),
    ('Osasuna', 'PD'),
    ('Rayo Vallecano', 'PD'),
    ('Celta de Vigo', 'PD'),
    ('UD Las Palmas', 'PD'),
    ('Deportivo Alavés', 'PD'),
    ('RCD Mallorca', 'PD'),
    ('CD Leganés', 'PD'),
    ('RCD Espanyol', 'PD'),
    ('Real Valladolid', 'PD')
    ON CONFLICT DO NOTHING;

-- ===========
-- SERIE A (SA) — 20 clubs
-- ===========
INSERT INTO team(name, league)
VALUES
    ('Inter', 'SA'),
    ('AC Milan', 'SA'),
    ('Juventus', 'SA'),
    ('AS Roma', 'SA'),
    ('Lazio', 'SA'),
    ('Napoli', 'SA'),
    ('Atalanta', 'SA'),
    ('Fiorentina', 'SA'),
    ('Torino', 'SA'),
    ('Bologna', 'SA'),
    ('Udinese', 'SA'),
    ('Sassuolo', 'SA'),
    ('Genoa', 'SA'),
    ('Sampdoria', 'SA'),
    ('Parma', 'SA'),
    ('Cagliari', 'SA'),
    ('Empoli', 'SA'),
    ('Verona', 'SA'),
    ('Monza', 'SA'),
    ('Lecce', 'SA')
    ON CONFLICT DO NOTHING;

-- ===========
-- BUNDESLIGA (BL1) — 18 clubs
-- ===========
INSERT INTO team(name, league)
VALUES
    ('Bayern München', 'BL1'),
    ('Borussia Dortmund', 'BL1'),
    ('RB Leipzig', 'BL1'),
    ('Bayer Leverkusen', 'BL1'),
    ('VfB Stuttgart', 'BL1'),
    ('Eintracht Frankfurt', 'BL1'),
    ('SC Freiburg', 'BL1'),
    ('TSG Hoffenheim', 'BL1'),
    ('VfL Wolfsburg', 'BL1'),
    ('Werder Bremen', 'BL1'),
    ('Borussia Mönchengladbach', 'BL1'),
    ('1. FC Union Berlin', 'BL1'),
    ('1. FSV Mainz 05', 'BL1'),
    ('FC Augsburg', 'BL1'),
    ('VfL Bochum', 'BL1'),
    ('1. FC Heidenheim', 'BL1'),
    ('Holstein Kiel', 'BL1'),
    ('Fortuna Düsseldorf', 'BL1')
    ON CONFLICT DO NOTHING;

-- ===========
-- LIGUE 1 (FL1) — 18 clubs
-- ===========
INSERT INTO team(name, league)
VALUES
    ('Paris Saint-Germain', 'FL1'),
    ('Olympique de Marseille', 'FL1'),
    ('Olympique Lyonnais', 'FL1'),
    ('AS Monaco', 'FL1'),
    ('LOSC Lille', 'FL1'),
    ('Stade Rennais', 'FL1'),
    ('OGC Nice', 'FL1'),
    ('Montpellier HSC', 'FL1'),
    ('RC Strasbourg Alsace', 'FL1'),
    ('Stade de Reims', 'FL1'),
    ('FC Nantes', 'FL1'),
    ('FC Lorient', 'FL1'),
    ('Toulouse FC', 'FL1'),
    ('Stade Brestois 29', 'FL1'),
    ('AJ Auxerre', 'FL1'),
    ('Angers SCO', 'FL1'),
    ('AS Saint-Étienne', 'FL1'),
    ('Le Havre AC', 'FL1')
    ON CONFLICT DO NOTHING;

-- ===========
-- EFL CHAMPIONSHIP (ELC) — 24 clubs
-- ===========
INSERT INTO team(name, league)
VALUES
    ('Leeds United', 'ELC'),
    ('Southampton', 'ELC'),
    ('Leicester City', 'ELC'),
    ('Ipswich Town', 'ELC'),
    ('Norwich City', 'ELC'),
    ('Middlesbrough', 'ELC'),
    ('West Bromwich Albion', 'ELC'),
    ('Sunderland', 'ELC'),
    ('Watford', 'ELC'),
    ('Coventry City', 'ELC'),
    ('Hull City', 'ELC'),
    ('Blackburn Rovers', 'ELC'),
    ('Swansea City', 'ELC'),
    ('Stoke City', 'ELC'),
    ('Birmingham City', 'ELC'),
    ('Bristol City', 'ELC'),
    ('Cardiff City', 'ELC'),
    ('Millwall', 'ELC'),
    ('Queens Park Rangers', 'ELC'),
    ('Plymouth Argyle', 'ELC'),
    ('Sheffield Wednesday', 'ELC'),
    ('Preston North End', 'ELC'),
    ('Huddersfield Town', 'ELC'),
    ('Rotherham United', 'ELC')
    ON CONFLICT DO NOTHING;

-- ===========
-- BRASILEIRÃO SÉRIE A (BSA) — 20 clubs
-- ===========
INSERT INTO team(name, league)
VALUES
    ('Flamengo', 'BSA'),
    ('Palmeiras', 'BSA'),
    ('São Paulo', 'BSA'),
    ('Corinthians', 'BSA'),
    ('Santos', 'BSA'),
    ('Atlético Mineiro', 'BSA'),
    ('Cruzeiro', 'BSA'),
    ('Grêmio', 'BSA'),
    ('Internacional', 'BSA'),
    ('Fluminense', 'BSA'),
    ('Vasco da Gama', 'BSA'),
    ('Botafogo', 'BSA'),
    ('Athletico Paranaense', 'BSA'),
    ('Cuiabá', 'BSA'),
    ('Fortaleza', 'BSA'),
    ('Ceará', 'BSA'),
    ('Bahia', 'BSA'),
    ('Red Bull Bragantino', 'BSA'),
    ('América Mineiro', 'BSA'),
    ('Goiás', 'BSA')
    ON CONFLICT DO NOTHING;

-- ===========
-- MLS (MLS) — clubs (recent set; MLS expands periodically)
-- ===========
INSERT INTO team(name, league)
VALUES
    ('Atlanta United', 'MLS'),
    ('Austin FC', 'MLS'),
    ('CF Montréal', 'MLS'),
    ('Charlotte FC', 'MLS'),
    ('Chicago Fire', 'MLS'),
    ('FC Cincinnati', 'MLS'),
    ('Colorado Rapids', 'MLS'),
    ('Columbus Crew', 'MLS'),
    ('D.C. United', 'MLS'),
    ('FC Dallas', 'MLS'),
    ('Houston Dynamo', 'MLS'),
    ('Inter Miami', 'MLS'),
    ('LA Galaxy', 'MLS'),
    ('Los Angeles FC', 'MLS'),
    ('Minnesota United', 'MLS'),
    ('Nashville SC', 'MLS'),
    ('New England Revolution', 'MLS'),
    ('New York City FC', 'MLS'),
    ('New York Red Bulls', 'MLS'),
    ('Orlando City', 'MLS'),
    ('Philadelphia Union', 'MLS'),
    ('Portland Timbers', 'MLS'),
    ('Real Salt Lake', 'MLS'),
    ('San Jose Earthquakes', 'MLS'),
    ('Seattle Sounders', 'MLS'),
    ('Sporting Kansas City', 'MLS'),
    ('St. Louis City SC', 'MLS'),
    ('Toronto FC', 'MLS'),
    ('Vancouver Whitecaps', 'MLS'),
    ('San Diego FC', 'MLS')
    ON CONFLICT DO NOTHING;

-- ===========
-- Cups/International (CL, MLI, CLI, WC)
-- Intentionally no static team seeding here; participants vary season-to-season.
-- Your app can still list these leagues; team lists can be fetched dynamically if/when you add that later.
-- ===========
