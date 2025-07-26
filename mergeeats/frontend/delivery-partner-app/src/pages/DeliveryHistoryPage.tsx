import React, { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
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
  TextField,
  InputAdornment,
  Button,
} from '@mui/material';
import {
  History,
  Restaurant,
  LocationOn,
  Star,
  Search,
  CalendarToday,
} from '@mui/icons-material';
import { useDelivery } from '../contexts/DeliveryContext';

const DeliveryHistoryPage: React.FC = () => {
  const { completedDeliveries, loading, error } = useDelivery();
  const [selectedDelivery, setSelectedDelivery] = useState<any>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  const handleViewDelivery = (delivery: any) => {
    setSelectedDelivery(delivery);
    setDialogOpen(true);
  };

  const filteredDeliveries = completedDeliveries.filter(delivery =>
    delivery.customerName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    delivery.restaurantName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    delivery.orderId.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getTotalEarnings = () => {
    return completedDeliveries.reduce((total, delivery) => total + (delivery.totalAmount * 0.1), 0); // Assuming 10% commission
  };

  const getAverageRating = () => {
    // TODO: Implement rating system
    return 4.5;
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
        Delivery History
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Summary Cards */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography variant="h4" fontWeight="bold" color="primary">
                    {completedDeliveries.length}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Total Deliveries
                  </Typography>
                </Box>
                <History color="primary" sx={{ fontSize: 40 }} />
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
                <Star color="success" sx={{ fontSize: 40 }} />
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
                    {getAverageRating()}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Average Rating
                  </Typography>
                </Box>
                <Star color="info" sx={{ fontSize: 40 }} />
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
                    {completedDeliveries.length > 0 ? (getTotalEarnings() / completedDeliveries.length).toFixed(2) : '0'}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Avg per Delivery
                  </Typography>
                </Box>
                <History color="warning" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Search */}
      <Box mb={3}>
        <TextField
          fullWidth
          placeholder="Search by customer name, restaurant, or order ID..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <Search />
              </InputAdornment>
            ),
          }}
        />
      </Box>

      {/* Delivery History List */}
      {filteredDeliveries.length === 0 ? (
        <Card>
          <CardContent>
            <Box textAlign="center" py={4}>
              <History sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
              <Typography variant="h6" color="text.secondary" gutterBottom>
                No Delivery History
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {searchTerm ? 'No deliveries match your search.' : 'You haven\'t completed any deliveries yet.'}
              </Typography>
            </Box>
          </CardContent>
        </Card>
      ) : (
        <Grid container spacing={3}>
          {filteredDeliveries.map((delivery) => (
            <Grid item xs={12} md={6} key={delivery.id}>
              <Card>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                    <Typography variant="h6" fontWeight="bold">
                      Order #{delivery.orderId.slice(-8)}
                    </Typography>
                    <Chip
                      label="Delivered"
                      color="success"
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
                      Order Value: ₹{delivery.totalAmount}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Earnings: ₹{(delivery.totalAmount * 0.1).toFixed(2)}
                    </Typography>
                  </Box>

                  <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Typography variant="body2" color="text.secondary">
                      {new Date(delivery.assignedAt).toLocaleDateString()}
                    </Typography>
                    <Button
                      variant="outlined"
                      size="small"
                      onClick={() => handleViewDelivery(delivery)}
                    >
                      View Details
                    </Button>
                  </Box>
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
                    <strong>Order Value:</strong> ₹{selectedDelivery.totalAmount}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Your Earnings:</strong> ₹{(selectedDelivery.totalAmount * 0.1).toFixed(2)}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Assigned At:</strong> {new Date(selectedDelivery.assignedAt).toLocaleString()}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Delivered At:</strong> {new Date(selectedDelivery.updatedAt).toLocaleString()}
                  </Typography>
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

export default DeliveryHistoryPage; 