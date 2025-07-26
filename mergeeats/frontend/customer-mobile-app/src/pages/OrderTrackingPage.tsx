import React, { useEffect, useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  Stepper,
  Step,
  StepLabel,
  StepContent,
  Grid,
  Chip,
  Divider,
  CircularProgress,
  Alert,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
} from '@mui/material';
import {
  Restaurant,
  LocalShipping,
  Person,
  CheckCircle,
  Schedule,
  LocationOn,
} from '@mui/icons-material';
import { useParams } from 'react-router-dom';
import { useOrder } from '../contexts/OrderContext';

const OrderTrackingPage: React.FC = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const { currentOrder, trackOrder, loading } = useOrder();
  const [estimatedTime, setEstimatedTime] = useState<string>('');

  useEffect(() => {
    if (orderId) {
      trackOrder(orderId);
    }
  }, [orderId, trackOrder]);

  useEffect(() => {
    if (currentOrder?.estimatedDeliveryTime) {
      const deliveryTime = new Date(currentOrder.estimatedDeliveryTime);
      const now = new Date();
      const diffInMinutes = Math.ceil((deliveryTime.getTime() - now.getTime()) / (1000 * 60));
      
      if (diffInMinutes > 0) {
        setEstimatedTime(`${diffInMinutes} minutes`);
      } else {
        setEstimatedTime('Arriving soon');
      }
    }
  }, [currentOrder]);

  const getOrderSteps = () => {
    const steps = [
      { label: 'Order Placed', completed: true },
      { label: 'Restaurant Confirmed', completed: currentOrder?.status !== 'PENDING' },
      { label: 'Food Being Prepared', completed: ['CONFIRMED', 'PREPARING', 'READY', 'OUT_FOR_DELIVERY', 'DELIVERED'].includes(currentOrder?.status || '') },
      { label: 'Out for Delivery', completed: ['OUT_FOR_DELIVERY', 'DELIVERED'].includes(currentOrder?.status || '') },
      { label: 'Delivered', completed: currentOrder?.status === 'DELIVERED' },
    ];
    return steps;
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'warning';
      case 'CONFIRMED': return 'info';
      case 'PREPARING': return 'primary';
      case 'READY': return 'success';
      case 'OUT_FOR_DELIVERY': return 'secondary';
      case 'DELIVERED': return 'success';
      case 'CANCELLED': return 'error';
      default: return 'default';
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  if (!currentOrder) {
    return (
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Alert severity="error">
          Order not found
        </Alert>
      </Container>
    );
  }

  const steps = getOrderSteps();

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
        Order #{currentOrder.id}
      </Typography>

      <Grid container spacing={3}>
        {/* Order Status */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h6">Order Status</Typography>
                <Chip
                  label={currentOrder.status.replace('_', ' ')}
                  color={getStatusColor(currentOrder.status) as any}
                  variant="filled"
                />
              </Box>

              <Stepper orientation="vertical">
                {steps.map((step, index) => (
                  <Step key={step.label} active={step.completed} completed={step.completed}>
                    <StepLabel>
                      <Typography variant="subtitle1" fontWeight="medium">
                        {step.label}
                      </Typography>
                    </StepLabel>
                    <StepContent>
                      <Typography variant="body2" color="text.secondary">
                        {step.completed ? 'Completed' : 'In progress...'}
                      </Typography>
                    </StepContent>
                  </Step>
                ))}
              </Stepper>
            </CardContent>
          </Card>
        </Grid>

        {/* Order Details */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Order Details
              </Typography>

              <List dense>
                <ListItem>
                  <ListItemIcon>
                    <Restaurant />
                  </ListItemIcon>
                  <ListItemText
                    primary="Restaurant"
                    secondary={currentOrder.restaurantName}
                  />
                </ListItem>

                <ListItem>
                  <ListItemIcon>
                    <Schedule />
                  </ListItemIcon>
                  <ListItemText
                    primary="Estimated Delivery"
                    secondary={estimatedTime}
                  />
                </ListItem>

                {currentOrder.deliveryPartnerName && (
                  <ListItem>
                    <ListItemIcon>
                      <Person />
                    </ListItemIcon>
                    <ListItemText
                      primary="Delivery Partner"
                      secondary={currentOrder.deliveryPartnerName}
                    />
                  </ListItem>
                )}

                <ListItem>
                  <ListItemIcon>
                    <LocationOn />
                  </ListItemIcon>
                  <ListItemText
                    primary="Delivery Address"
                    secondary={currentOrder.deliveryAddress}
                  />
                </ListItem>
              </List>

              <Divider sx={{ my: 2 }} />

              <Typography variant="h6" gutterBottom>
                Order Items
              </Typography>
              {currentOrder.items.map((item) => (
                <Box key={item.menuItemId} display="flex" justifyContent="space-between" mb={1}>
                  <Typography variant="body2">
                    {item.name} x {item.quantity}
                  </Typography>
                  <Typography variant="body2">
                    ₹{item.price * item.quantity}
                  </Typography>
                </Box>
              ))}

              <Divider sx={{ my: 2 }} />

              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2">Subtotal</Typography>
                <Typography variant="body2">₹{currentOrder.totalAmount}</Typography>
              </Box>
              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2">Delivery Fee</Typography>
                <Typography variant="body2">₹50.00</Typography>
              </Box>
              <Box display="flex" justifyContent="space-between" mb={2}>
                <Typography variant="body2">Tax</Typography>
                <Typography variant="body2">₹{(currentOrder.totalAmount * 0.18).toFixed(2)}</Typography>
              </Box>
              <Divider />
              <Box display="flex" justifyContent="space-between" mt={2}>
                <Typography variant="h6" fontWeight="bold">Total</Typography>
                <Typography variant="h6" fontWeight="bold" color="primary">
                  ₹{(currentOrder.totalAmount + 50 + currentOrder.totalAmount * 0.18).toFixed(2)}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default OrderTrackingPage; 