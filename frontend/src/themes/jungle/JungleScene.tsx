import type { CSSProperties } from 'react';
import { SceneFrame, type SceneProps } from '../scene/SceneFrame';
import type { ParticleFactory } from '../scene/ParticleCanvas';
import styles from './JungleScene.module.css';
import './palette.css';

interface Leaf {
  x: number;
  y: number;
  size: number;
  speed: number;
  angle: number;
  spin: number;
  phase: number;
}

/** A rare falling leaf: one every 6–10 s, never more than three at once. */
const leaves: ParticleFactory = (width, height) => {
  const falling: Leaf[] = [];
  let next = 2000 + Math.random() * 4000;
  return {
    update(dt, now) {
      if (now > next && falling.length < 3) {
        falling.push({
          x: width * (0.15 + Math.random() * 0.7),
          y: -20,
          size: 7 + Math.random() * 5,
          speed: 28 + Math.random() * 18,
          angle: Math.random() * Math.PI,
          spin: (Math.random() - 0.5) * 1.6,
          phase: Math.random() * Math.PI * 2,
        });
        next = now + 6000 + Math.random() * 4000;
      }
      for (const leaf of falling) {
        leaf.y += leaf.speed * dt;
        leaf.x += Math.sin(now / 1100 + leaf.phase) * 22 * dt;
        leaf.angle += leaf.spin * dt;
      }
      for (let i = falling.length - 1; i >= 0; i--) {
        if (falling[i].y > height + 20) falling.splice(i, 1);
      }
    },
    draw(ctx) {
      for (const leaf of falling) {
        ctx.save();
        ctx.translate(leaf.x, leaf.y);
        ctx.rotate(leaf.angle);
        ctx.fillStyle = 'rgba(92, 138, 95, 0.85)';
        ctx.beginPath();
        ctx.ellipse(0, 0, leaf.size, leaf.size * 0.42, 0, 0, Math.PI * 2);
        ctx.fill();
        ctx.strokeStyle = 'rgba(30, 60, 38, 0.6)';
        ctx.lineWidth = 0.8;
        ctx.beginPath();
        ctx.moveTo(-leaf.size, 0);
        ctx.lineTo(leaf.size, 0);
        ctx.stroke();
        ctx.restore();
      }
    },
  };
};

/** A palm frond: a curved rib with leaflets, drawn pointing up from its base at (0, 0). */
function palmFrond(length: number, curl: number, leaflets: number): string {
  let d = `M0 0 Q ${curl * 0.4} ${-length * 0.55} ${curl} ${-length}`;
  for (let i = 1; i <= leaflets; i++) {
    const t = i / (leaflets + 1);
    const x = curl * t * t;
    const y = -length * t;
    const size = 70 * Math.sin(Math.PI * t) + 12;
    d += ` M${x} ${y} q ${-size * 0.6} ${size * 0.15} ${-size} ${size * 0.55} q ${size * 0.5} ${-size * 0.25} ${size} ${-size * 0.5} Z`;
    d += ` M${x} ${y} q ${size * 0.6} ${size * 0.1} ${size} ${size * 0.5} q ${-size * 0.5} ${-size * 0.3} ${-size} ${-size * 0.45} Z`;
  }
  return d;
}

/** A split-leaf monstera on its stalk; the slits are cut out with the even-odd fill rule. */
const MONSTERA =
  'M-5 0 L-3 -120 L3 -120 L5 0 Z ' +
  'M0 -110 C -30 -130 -170 -160 -185 -260 C -200 -350 -110 -415 0 -418 C 110 -415 200 -350 185 -260 C 170 -160 30 -130 0 -110 Z ' +
  'M-188 -250 L -70 -235 L -186 -275 Z M-172 -330 L -60 -292 L -150 -356 Z M-120 -190 L -40 -200 L -132 -210 Z ' +
  'M188 -250 L 70 -235 L 186 -275 Z M172 -330 L 60 -292 L 150 -356 Z M120 -190 L 40 -200 L 132 -210 Z';

const BAMBOO = (height: number) => {
  let d = `M-9 0 L-7 ${-height} L7 ${-height} L9 0 Z`;
  for (let y = -90; y > -height; y -= 110) {
    d += ` M-11 ${y} h22 v6 h-22 Z`;
    d += ` M6 ${y} q 50 -20 90 -10 q -45 6 -90 18 Z`;
  }
  return d;
};

// Fronds framing the edges. Each sways from its base: left/bottom in %, size in vh, period in s.
const FRONDS: { kind: 'palm' | 'monstera' | 'bamboo'; left: number; bottom: number; size: number; rotate: number; period: number; delay: number; flip?: boolean }[] = [
  { kind: 'bamboo', left: 2, bottom: -2, size: 95, rotate: -3, period: 9, delay: 0 },
  { kind: 'bamboo', left: 6.5, bottom: -4, size: 80, rotate: 2, period: 8, delay: -3 },
  { kind: 'palm', left: 9, bottom: -6, size: 62, rotate: 38, period: 7, delay: -1.5 },
  { kind: 'palm', left: 93, bottom: -8, size: 70, rotate: -40, period: 8.5, delay: -4, flip: true },
  { kind: 'monstera', left: 84, bottom: -10, size: 40, rotate: -12, period: 10, delay: -2 },
  { kind: 'monstera', left: 13, bottom: -12, size: 30, rotate: 18, period: 6.5, delay: -5, flip: true },
];

export function JungleScene(props: SceneProps) {
  return (
    <SceneFrame name="jungle" particles={leaves} {...props}>
      <div className={styles.sky} />
      <svg className={styles.ridges} viewBox="0 0 1600 1000" preserveAspectRatio="xMidYMax slice" aria-hidden="true">
        <path
          className={styles.far}
          d="M0 640 C 40 560 70 480 110 470 C 150 460 170 560 210 600 C 250 520 280 380 330 370 C 380 360 400 520 440 590 C 480 560 520 470 570 470 C 620 470 640 560 690 600 C 740 520 780 400 840 390 C 900 380 930 540 970 600 C 1010 560 1050 480 1100 480 C 1150 480 1170 560 1210 590 C 1250 520 1300 420 1360 420 C 1420 420 1450 560 1490 600 C 1530 570 1570 540 1600 540 V1000 H0 Z"
        />
        <path
          className={styles.mid}
          d="M0 760 C 60 700 120 650 180 660 C 240 670 270 730 320 740 C 380 680 430 610 500 620 C 570 630 600 720 660 740 C 720 700 790 650 860 660 C 930 670 970 740 1030 750 C 1100 690 1170 640 1240 650 C 1310 660 1350 730 1410 740 C 1470 700 1540 670 1600 680 V1000 H0 Z"
        />
        <path className={styles.canopy} d="M0 860 C 120 820 240 840 360 830 C 520 815 640 860 800 850 C 960 840 1080 810 1240 830 C 1380 848 1500 830 1600 820 V1000 H0 Z" />
      </svg>
      <div className={styles.shafts}>
        {[0, 1, 2].map((i) => (
          <span key={i} className={styles.shaft} style={{ left: `${18 + i * 22}%`, animationDuration: `${8 + i * 2}s`, animationDelay: `${-i * 3}s` }} />
        ))}
      </div>
      <div className={styles.mist} style={{ top: '52%', animationDuration: '72s' }} />
      <div className={styles.mist} style={{ top: '64%', animationDuration: '88s', animationDelay: '-30s' }} />
      {FRONDS.map((frond, i) => (
        <div
          key={i}
          className={styles.frond}
          style={
            {
              left: `${frond.left}%`,
              bottom: `${frond.bottom}%`,
              height: `${frond.size}vh`,
              '--rest': `${frond.rotate}deg`,
              animationDuration: `${frond.period}s`,
              animationDelay: `${frond.delay}s`,
            } as CSSProperties
          }
        >
          <svg viewBox="-200 -420 400 420" preserveAspectRatio="xMidYMax meet" aria-hidden="true" style={frond.flip ? { transform: 'scaleX(-1)' } : undefined}>
            <path
              className={frond.kind === 'bamboo' ? styles.bamboo : styles.leaf}
              fillRule={frond.kind === 'monstera' ? 'evenodd' : undefined}
              d={frond.kind === 'palm' ? palmFrond(400, 120, 11) : frond.kind === 'monstera' ? MONSTERA : BAMBOO(410)}
            />
          </svg>
        </div>
      ))}
    </SceneFrame>
  );
}
