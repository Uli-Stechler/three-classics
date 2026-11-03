import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { ToastProvider } from '../components/Toast';
import { CheckersPage } from '../games/checkers/CheckersPage';
import { ConnectFourPage } from '../games/connect-four/ConnectFourPage';
import { Home } from '../pages/Home';
import { SettingsProvider } from './settings';

export function App() {
  return (
    <SettingsProvider>
      <ToastProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/connect-four" element={<ConnectFourPage />} />
            <Route path="/checkers" element={<CheckersPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </ToastProvider>
    </SettingsProvider>
  );
}
