import React, { useState, useEffect } from 'react';
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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  CircularProgress,
  Stepper,
  Step,
  StepLabel,
} from '@mui/material';
import {
  LocalShipping,
  Restaurant,
  LocationOn,
  Phone,
  CheckCircle,
  Schedule,
} from '@mui/icons-material';
import { useDelivery } from '../contexts/DeliveryContext';

const steps = ['Order Confirmed', 'Picked Up', 'On the Way', 'Delivered'];

const ActiveDeliveriesPage: React.FC = () => {
  const { activeDeliveries, loading, error, updateDeliveryStatus } = useDelivery();
  const [selectedDelivery, setSelectedDelivery] = useState<any>(null);
  const [dialogOpen, setDialogOpen] = useState(false);

  const handleViewDelivery = (delivery: any) => {
    setSelectedDelivery(delivery);
    setDialogOpen(true);
  };

  const handleUpdateStatus = async (deliveryId: string, status: string) => {
    try {
      await updateDeliveryStatus(deliveryId, status);
    } catch (error) {
      console.error('Failed to update delivery status:', error);
    }
  };

  const getStatusStep = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return 0;
      case 'PICKED_UP':
        return 1;
      case 'ON_THE_WAY':
        return 2;
      case 'DELIVERED':
        return 3;
      default:
        return 0;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return 'info';
      case 'PICKED_UP':
        return 'primary';
      case 'ON_THE_WAY':
        return 'warning';
      case 'DELIVERED':
        return 'success';
      default:
        return 'default';
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
        Active Deliveries
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {activeDeliveries.length === 0 ? (
        <Card>
          <CardContent>
            <Box textAlign="center" py={4}>
              <LocalShipping sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
              <Typography variant="h6" color="text.secondary" gutterBottom>
                No Active Deliveries
              </Typography>
              <Typography variant="body2" color="text.secondary">
                You don't have any active deliveries at the moment.
              </Typography>
            </Box>
          </CardContent>
        </Card>
      ) : (
        <Grid container spacing={3}>
          {activeDeliveries.map((delivery) => (
            <Grid item xs={12} md={6} key={delivery.id}>
              <Card>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                    <Typography variant="h6" fontWeight="bold">
                      Order #{delivery.orderId.slice(-8)}
                    </Typography>
                    <Chip
                      label={delivery.status.replace('_', ' ')}
                      color={getStatusColor(delivery.status) as any}
                      size="small"
                    />
                  </Box>

                  <Box mb={2}>
                    <Box display="flex" alignItems="center" gap={1} mb={1}>
                      <Restaurant color="primary" fontSize="small" />
                      <Typography variant="body2" fontWeight="bold">
                        {delivery.restaurantName}
                      </Typography>
                    </Box>
                    <Typography variant="body2" color="text.secondary" ml={3}>
                      {delivery.restaurantAddress}
                    </Typography>
                  </Box>

                  <Box mb={2}>
                    <Box display="flex" alignItems="center" gap={1} mb={1}>
                      <LocationOn color="success" fontSize="small" />
                      <Typography variant="body2" fontWeight="bold">
                        {delivery.customerName}
                      </Typography>
                    </Box>
                    <Typography variant="body2" color="text.secondary" ml={3}>
                      {delivery.deliveryAddress}
                    </Typography>
                  </Box>

                  <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                    <Typography variant="body2" color="text.secondary">
                      ₹{delivery.totalAmount}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {new Date(delivery.assignedAt).toLocaleTimeString()}
                    </Typography>
                  </Box>

                  <Stepper activeStep={getStatusStep(delivery.status)} sx={{ mb: 2 }}>
                    {steps.map((label) => (
                      <Step key={label}>
                        <StepLabel>{label}</StepLabel>
                      </Step>
                    ))}
                  </Stepper>

                  <Box display="flex" gap={1}>
                    <Button
                      variant="outlined"
                      size="small"
                      startIcon={<Phone />}
                    >
                      Call Customer
                    </Button>
                    <Button
                      variant="outlined"
                      size="small"
                      onClick={() => handleViewDelivery(delivery)}
                    >
                      View Details
                    </Button>
                  </Box>

                  {/* Action Buttons based on status */}
                  {delivery.status === 'ASSIGNED' && (
                    <Button
                      variant="contained"
                      color="primary"
                      fullWidth
                      sx={{ mt: 2 }}
                      onClick={() => handleUpdateStatus(delivery.id, 'PICKED_UP')}
                    >
                      Mark as Picked Up
                    </Button>
                  )}

                  {delivery.status === 'PICKED_UP' && (
                    <Button
                      variant="contained"
                      color="warning"
                      fullWidth
                      sx={{ mt: 2 }}
                      onClick={() => handleUpdateStatus(delivery.id, 'ON_THE_WAY')}
                    >
                      Start Delivery
                    </Button>
                  )}

                  {delivery.status === 'ON_THE_WAY' && (
                    <Button
                      variant="contained"
                      color="success"
                      fullWidth
                      sx={{ mt: 2 }}
                      onClick={() => handleUpdateStatus(delivery.id, 'DELIVERED')}
                    >
                      Mark as Delivered
                    </Button>
                  )}
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      {/* Delivery Details Dialog */}
      <Dialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          Delivery Details #{selectedDelivery?.orderId?.slice(-8)}
        </DialogTitle>
        <DialogContent>
          {selectedDelivery && (
            <Box>
              <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                  <Typography variant="h6" gutterBottom>
                    Restaurant Information
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Name:</strong> {selectedDelivery.restaurantName}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Address:</strong> {selectedDelivery.restaurantAddress}
                  </Typography>
                </Grid>
                
                <Grid item xs={12} md={6}>
                  <Typography variant="h6" gutterBottom>
                    Customer Information
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Name:</strong> {selectedDelivery.customerName}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Phone:</strong> {selectedDelivery.customerPhone}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Address:</strong> {selectedDelivery.deliveryAddress}
                  </Typography>
                </Grid>
                
                <Grid item xs={12}>
                  <Typography variant="h6" gutterBottom>
                    Order Items
                  </Typography>
                  <List dense>
                    {selectedDelivery.orderItems.map((item: any, index: number) => (
                      <ListItem key={index}>
                        <ListItemText
                          primary={item.name}
                          secondary={`Quantity: ${item.quantity}`}
                        />
                      </ListItem>
                    ))}
                  </List>
                </Grid>

                <Grid item xs={12}>
                  <Typography variant="h6" gutterBottom>
                    Delivery Information
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Total Amount:</strong> ₹{selectedDelivery.totalAmount}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Assigned At:</strong> {new Date(selectedDelivery.assignedAt).toLocaleString()}
                  </Typography>
                  {selectedDelivery.estimatedDeliveryTime && (
                    <Typography variant="body2" gutterBottom>
                      <strong>Estimated Delivery:</strong> {selectedDelivery.estimatedDeliveryTime}
                    </Typography>
                  )}
                </Grid>
              </Grid>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ActiveDeliveriesPage; 