import { useEffect } from 'react';
import { Link } from 'react-router-dom';

// Placeholder until Milestone 5 builds the jungle home page.
export function Home() {
  useEffect(() => {
    document.title = 'Three Classics';
  }, []);

  return (
    <main style={{ padding: 'var(--space-7)' }}>
      <h1 style={{ fontFamily: 'var(--font-display)', fontSize: 'var(--text-2xl)' }}>Three Classics</h1>
      <p style={{ color: 'var(--ui-text-muted)' }}>Connect Four, Checkers and Backgammon against the computer.</p>
      <ul>
        <li>
          <Link to="/connect-four">Play Connect Four</Link>
        </li>
      </ul>
    </main>
  );
}
