-- === Seed Teams ===
INSERT INTO team (name, country) VALUES
                                     ('Atlanta United', 'USA'),
                                     ('Corinthians', 'Brazil'),
                                     ('Real Madrid', 'Spain'),
                                     ('Manchester United', 'England'),
                                     ('Bayern Munich', 'Germany');

-- === Seed Users ===
INSERT INTO app_user (email, name) VALUES
                                       ('kevgom11@gmail.com', 'Kevin Gomes'),
                                       ('kevin.intern26@gmail.com', 'Ana Intern');

-- === Seed Matches ===
-- Assume team IDs start at 1
INSERT INTO match (home_team_id, away_team_id, kickoff_at, external_id) VALUES
                                                                            (1, 2, NOW() + interval '1 day', 1001),
                                                                            (3, 4, NOW() + interval '2 days', 1002),
                                                                            (5, 1, NOW() + interval '3 days', 1003);

-- === Seed Subscriptions ===
-- Link users to teams
INSERT INTO subscription (user_id, team_id) VALUES
                                                (1, 1),  -- Kevin -> Atlanta United
                                                (1, 3),  -- Kevin -> Real Madrid
                                                (2, 4),  -- Ana -> Man Utd
                                                (2, 5);  -- Ana -> Bayern Munich
