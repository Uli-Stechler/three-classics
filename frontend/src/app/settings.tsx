import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { readJson, writeJson } from './storage';

interface Settings {
  /** Ambient scene motion (falling snow, bubbles...). Pieces still move either way. */
  ambientMotion: boolean;
  setAmbientMotion: (on: boolean) => void;
}

const SettingsContext = createContext<Settings | null>(null);
const STORAGE_KEY = 'tc.settings';

function prefersReducedMotion(): boolean {
  return typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
}

export function SettingsProvider({ children }: { children: ReactNode }) {
  const [ambientMotion, setAmbientMotion] = useState<boolean>(() => {
    const stored = readJson<{ ambientMotion?: boolean }>(STORAGE_KEY, {});
    return stored.ambientMotion ?? !prefersReducedMotion();
  });

  useEffect(() => {
    writeJson(STORAGE_KEY, { ambientMotion });
  }, [ambientMotion]);

  const value = useMemo(() => ({ ambientMotion, setAmbientMotion }), [ambientMotion]);
  return <SettingsContext.Provider value={value}>{children}</SettingsContext.Provider>;
}

export function useSettings(): Settings {
  const settings = useContext(SettingsContext);
  if (!settings) throw new Error('useSettings must be used inside SettingsProvider');
  return settings;
}
