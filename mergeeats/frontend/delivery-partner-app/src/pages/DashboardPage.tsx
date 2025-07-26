import React, { useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Chip,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  LocalShipping,
  TrendingUp,
  CheckCircle,
  Schedule,
  Visibility,
  VisibilityOff,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { useDelivery } from '../contexts/DeliveryContext';

const DashboardPage: React.FC = () => {
  const { deliveryPartner, loading: authLoading } = useAuth();
  const { 
    activeDeliveries, 
    deliveryRequests, 
    fetchActiveDeliveries, 
    fetchDeliveryRequests,
    loading: deliveryLoading,
    updateLocation 
  } = useDelivery();

  useEffect(() => {
    fetchActiveDeliveries();
    fetchDeliveryRequests();
  }, [fetchActiveDeliveries, fetchDeliveryRequests]);

  const handleToggleAvailability = async () => {
    // TODO: Implement availability toggle
    console.log('Toggle availability');
  };

  const handleUpdateLocation = async () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          await updateLocation(position.coords.latitude, position.coords.longitude);
        },
        (error) => {
          console.error('Error getting location:', error);
        }
      );
    }
  };

  const getTotalEarnings = () => {
    // TODO: Calculate from completed deliveries
    return 0;
  };

  const getCompletedDeliveries = () => {
    // TODO: Get from delivery history
    return 0;
  };

  if (authLoading || deliveryLoading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Typography variant="h4" component="h1" fontWeight="bold">
          Dashboard
        </Typography>
        <Box display="flex" gap={2}>
          <Button
            variant="outlined"
            onClick={handleUpdateLocation}
          >
            Update Location
          </Button>
          <Button
            variant={deliveryPartner?.isAvailable ? "outlined" : "contained"}
            startIcon={deliveryPartner?.isAvailable ? <Visibility /> : <VisibilityOff />}
            onClick={handleToggleAvailability}
          >
            {deliveryPartner?.isAvailable ? 'Available' : 'Unavailable'}
          </Button>
        </Box>
      </Box>

      {/* Key Metrics */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography variant="h4" fontWeight="bold" color="primary">
                    {activeDeliveries.length}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Active Deliveries
                  </Typography>
                </Box>
                <LocalShipping color="primary" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography variant="h4" fontWeight="bold" color="success.main">
                    ₹{getTotalEarnings().toFixed(2)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Total Earnings
                  </Typography>
                </Box>
                <TrendingUp color="success" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography variant="h4" fontWeight="bold" color="info.main">
                    {getCompletedDeliveries()}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Completed Deliveries
                  </Typography>
                </Box>
                <CheckCircle color="info" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography variant="h4" fontWeight="bold" color="warning.main">
                    {deliveryRequests.length}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Available Orders
                  </Typography>
                </Box>
                <Schedule color="warning" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Active Deliveries */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Active Deliveries
              </Typography>
              {activeDeliveries.length === 0 ? (
                <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
                  No active deliveries
                </Typography>
              ) : (
                <List>
                  {activeDeliveries.map((delivery) => (
                    <ListItem key={delivery.id} divider>
                      <ListItemText
                        primary={`Order #${delivery.orderId.slice(-8)}`}
                        secondary={
                          <Box>
                            <Typography variant="body2">
                              {delivery.customerName} • {delivery.restaurantName}
                            </Typography>
                            <Typography variant="caption" color="text.secondary">
                              {delivery.deliveryAddress}
                            </Typography>
                          </Box>
                        }
                      />
                      <ListItemSecondaryAction>
                        <Chip
                          label={delivery.status.replace('_', ' ')}
                          color="primary"
                          size="small"
                        />
                      </ListItemSecondaryAction>
                    </ListItem>
                  ))}
                </List>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Available Orders
              </Typography>
              {deliveryRequests.length === 0 ? (
                <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
                  No available orders
                </Typography>
              ) : (
                <List>
                  {deliveryRequests.slice(0, 5).map((delivery: any) => (
                    <ListItem key={delivery.id} divider>
                      <ListItemText
                        primary={`Order #${delivery.orderId?.slice(-8)}`}
                        secondary={`${delivery.restaurantName} → ${delivery.deliveryAddress}`}
                      />
                      <Button
                        variant="outlined"
                        size="small"
                        onClick={() => {/* TODO: Accept delivery */}}
                      >
                        Accept
                      </Button>
                    </ListItem>
                  ))}
                </List>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default DashboardPage; 