import type { GameView, MoveResult, NewGameRequest, Problem, StatsEntry } from './types';

export class ApiError extends Error {
  constructor(
    readonly status: number,
    readonly problem: Problem,
  ) {
    super(problem.detail ?? problem.title ?? `Request failed with ${status}`);
  }

  get isStale(): boolean {
    return this.status === 409;
  }

  get isNotFound(): boolean {
    return this.status === 404;
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    ...init,
    headers: { 'Content-Type': 'application/json', Accept: 'application/json', ...init?.headers },
  });
  if (!response.ok) {
    let problem: Problem = {};
    try {
      problem = (await response.json()) as Problem;
    } catch {
      // Not a problem-detail body; keep the status only.
    }
    throw new ApiError(response.status, problem);
  }
  return (await response.json()) as T;
}

export const api = {
  createGame<S, M>(body: NewGameRequest): Promise<MoveResult<S, M>> {
    return request('/api/games', { method: 'POST', body: JSON.stringify(body) });
  },

  getGame<S, M>(id: string): Promise<GameView<S, M>> {
    return request(`/api/games/${encodeURIComponent(id)}`);
  },

  move<S, M>(id: string, version: number, move: M): Promise<MoveResult<S, M>> {
    return request(`/api/games/${encodeURIComponent(id)}/moves`, {
      method: 'POST',
      body: JSON.stringify({ version, move }),
    });
  },

  stats(): Promise<StatsEntry[]> {
    return request('/api/stats');
  },
};
