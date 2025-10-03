export type User = {
  id: number;
  email: string;
  name: string;
  createdAt: string;
};

export type Match = {
  id: number;
  homeTeamId: number;
  awayTeamId: number;
  kickoffAt: string;
  venue?: string | null;
};

export type Team = {
  id: number;
  name: string;
  shortName?: string | null;
};