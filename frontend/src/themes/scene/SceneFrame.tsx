import type { ReactNode } from 'react';
import { ParticleCanvas, type ParticleFactory } from './ParticleCanvas';
import styles from './SceneFrame.module.css';

export type SceneMode = 'full' | 'thumbnail';

export interface SceneProps {
  /** Full-page backdrop, or a static card thumbnail that only moves while hovered. */
  mode?: SceneMode;
  animate: boolean;
  /** Darken and desaturate behind the board so the scene stays a backdrop. */
  scrim?: boolean;
}

interface SceneFrameProps extends SceneProps {
  name: string;
  particles?: ParticleFactory;
  children: ReactNode;
}

/**
 * Layers, back to front: an optional photo (set --scene-photo to a licensed image), the SVG
 * silhouettes, one particle canvas, then the scrim.
 */
export function SceneFrame({ name, mode = 'full', animate, scrim = false, particles, children }: SceneFrameProps) {
  return (
    <div className={styles.scene} data-scene={name} data-mode={mode} data-animate={animate} data-scrim={scrim} aria-hidden="true">
      <div className={styles.photo} />
      <div className={styles.layers}>{children}</div>
      {particles && <ParticleCanvas create={particles} animate={animate} className={styles.canvas} />}
      {scrim && <div className={styles.scrim} />}
    </div>
  );
}
