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
  Restaurant,
  Receipt,
  TrendingUp,
  People,
  Visibility,
  VisibilityOff,
} from '@mui/icons-material';
import { useRestaurant } from '../contexts/RestaurantContext';
import { useOrder } from '../contexts/OrderContext';

const DashboardPage: React.FC = () => {
  const { restaurant, loading: restaurantLoading, toggleRestaurantStatus } = useRestaurant();
  const { orders, pendingOrders, fetchOrders, loading: ordersLoading } = useOrder();

  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  const handleToggleStatus = async () => {
    try {
      await toggleRestaurantStatus();
    } catch (error) {
      console.error('Failed to toggle restaurant status:', error);
    }
  };

  const getTotalRevenue = () => {
    return orders
      .filter(order => order.status === 'DELIVERED')
      .reduce((total, order) => total + order.totalAmount, 0);
  };

  const getAverageOrderValue = () => {
    const completedOrders = orders.filter(order => order.status === 'DELIVERED');
    if (completedOrders.length === 0) return 0;
    return getTotalRevenue() / completedOrders.length;
  };

  if (restaurantLoading) {
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
        <Button
          variant={restaurant?.isOpen ? "outlined" : "contained"}
          startIcon={restaurant?.isOpen ? <Visibility /> : <VisibilityOff />}
          onClick={handleToggleStatus}
        >
          {restaurant?.isOpen ? 'Open' : 'Closed'}
        </Button>
      </Box>

      {/* Key Metrics */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography variant="h4" fontWeight="bold" color="primary">
                    {pendingOrders.length}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Pending Orders
                  </Typography>
                </Box>
                <Receipt color="primary" sx={{ fontSize: 40 }} />
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
                    ₹{getTotalRevenue().toFixed(2)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Total Revenue
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
                    ₹{getAverageOrderValue().toFixed(2)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Avg Order Value
                  </Typography>
                </Box>
                <People color="info" sx={{ fontSize: 40 }} />
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
                    {orders.length}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Total Orders
                  </Typography>
                </Box>
                <Restaurant color="warning" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Recent Orders */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Recent Orders
              </Typography>
              {ordersLoading ? (
                <Box display="flex" justifyContent="center" py={4}>
                  <CircularProgress />
                </Box>
              ) : orders.length === 0 ? (
                <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
                  No orders yet
                </Typography>
              ) : (
                <List>
                  {orders.slice(0, 5).map((order) => (
                    <ListItem key={order.id} divider>
                      <ListItemText
                        primary={`Order #${order.id.slice(-8)}`}
                        secondary={
                          <Box>
                            <Typography variant="body2">
                              {order.customerName} • ₹{order.totalAmount}
                            </Typography>
                            <Typography variant="caption" color="text.secondary">
                              {new Date(order.createdAt).toLocaleString()}
                            </Typography>
                          </Box>
                        }
                      />
                      <ListItemSecondaryAction>
                        <Chip
                          label={order.status.replace('_', ' ')}
                          color={
                            order.status === 'PENDING' ? 'warning' :
                            order.status === 'CONFIRMED' ? 'info' :
                            order.status === 'PREPARING' ? 'primary' :
                            order.status === 'DELIVERED' ? 'success' : 'error'
                          }
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
                Restaurant Status
              </Typography>
              <Box>
                <Typography variant="body1" gutterBottom>
                  <strong>Name:</strong> {restaurant?.name}
                </Typography>
                <Typography variant="body1" gutterBottom>
                  <strong>Cuisine:</strong> {restaurant?.cuisine}
                </Typography>
                <Typography variant="body1" gutterBottom>
                  <strong>Status:</strong> 
                  <Chip
                    label={restaurant?.isOpen ? 'Open' : 'Closed'}
                    color={restaurant?.isOpen ? 'success' : 'error'}
                    size="small"
                    sx={{ ml: 1 }}
                  />
                </Typography>
                <Typography variant="body1" gutterBottom>
                  <strong>Phone:</strong> {restaurant?.phone}
                </Typography>
                <Typography variant="body1" gutterBottom>
                  <strong>Address:</strong> {restaurant?.address.street}, {restaurant?.address.city}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default DashboardPage; 