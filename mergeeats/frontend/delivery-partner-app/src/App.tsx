import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { AuthProvider } from './contexts/AuthContext';
import { DeliveryProvider } from './contexts/DeliveryContext';

// Pages
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import ActiveDeliveriesPage from './pages/ActiveDeliveriesPage';
import DeliveryHistoryPage from './pages/DeliveryHistoryPage';
import EarningsPage from './pages/EarningsPage';
import ProfilePage from './pages/ProfilePage';

// Components
import Layout from './components/Layout/Layout';
import ProtectedRoute from './components/Auth/ProtectedRoute';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976D2', // Blue for delivery theme
    },
    secondary: {
      main: '#FF6B35',
    },
    background: {
      default: '#f5f5f5',
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h4: {
      fontWeight: 600,
    },
    h5: {
      fontWeight: 600,
    },
    h6: {
      fontWeight: 600,
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          textTransform: 'none',
          fontWeight: 600,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        },
      },
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <DeliveryProvider>
          <Router>
            <Layout>
              <Routes>
                {/* Public Routes */}
                <Route path="/login" element={<LoginPage />} />
                
                {/* Protected Routes */}
                <Route path="/" element={
                  <ProtectedRoute>
                    <DashboardPage />
                  </ProtectedRoute>
                } />
                <Route path="/active-deliveries" element={
                  <ProtectedRoute>
                    <ActiveDeliveriesPage />
                  </ProtectedRoute>
                } />
                <Route path="/delivery-history" element={
                  <ProtectedRoute>
                    <DeliveryHistoryPage />
                  </ProtectedRoute>
                } />
                <Route path="/earnings" element={
                  <ProtectedRoute>
                    <EarningsPage />
                  </ProtectedRoute>
                } />
                <Route path="/profile" element={
                  <ProtectedRoute>
                    <ProfilePage />
                  </ProtectedRoute>
                } />
              </Routes>
            </Layout>
          </Router>
        </DeliveryProvider>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
