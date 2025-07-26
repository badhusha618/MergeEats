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
  Tabs,
  Tab,
  Alert,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from '@mui/material';
import {
  Receipt,
  Schedule,
  CheckCircle,
  Cancel,
  Visibility,
} from '@mui/icons-material';
import { useOrder } from '../contexts/OrderContext';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`orders-tabpanel-${index}`}
      aria-labelledby={`orders-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ pt: 3 }}>{children}</Box>}
    </div>
  );
}

const OrdersPage: React.FC = () => {
  const { orders, pendingOrders, completedOrders, loading, error, fetchOrders, updateOrderStatus } = useOrder();
  const [tabValue, setTabValue] = useState(0);
  const [selectedOrder, setSelectedOrder] = useState<any>(null);
  const [orderDialogOpen, setOrderDialogOpen] = useState(false);

  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  const handleViewOrder = (order: any) => {
    setSelectedOrder(order);
    setOrderDialogOpen(true);
  };

  const handleUpdateStatus = async (orderId: string, status: string) => {
    try {
      await updateOrderStatus(orderId, status);
    } catch (error) {
      console.error('Failed to update order status:', error);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'warning';
      case 'CONFIRMED':
        return 'info';
      case 'PREPARING':
        return 'primary';
      case 'READY_FOR_PICKUP':
        return 'secondary';
      case 'DELIVERED':
        return 'success';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'PENDING':
        return <Schedule />;
      case 'CONFIRMED':
        return <Receipt />;
      case 'PREPARING':
        return <Schedule />;
      case 'READY_FOR_PICKUP':
        return <CheckCircle />;
      case 'DELIVERED':
        return <CheckCircle />;
      case 'CANCELLED':
        return <Cancel />;
      default:
        return <Receipt />;
    }
  };

  const renderOrderList = (orderList: any[]) => {
    if (loading) {
      return (
        <Box display="flex" justifyContent="center" py={4}>
          <CircularProgress />
        </Box>
      );
    }

    if (orderList.length === 0) {
      return (
        <Box textAlign="center" py={4}>
          <Typography variant="body1" color="text.secondary">
            No orders found
          </Typography>
        </Box>
      );
    }

    return (
      <List>
        {orderList.map((order) => (
          <Card key={order.id} sx={{ mb: 2 }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="flex-start">
                <Box flex={1}>
                  <Box display="flex" alignItems="center" gap={2} mb={1}>
                    <Typography variant="h6" fontWeight="bold">
                      Order #{order.id.slice(-8)}
                    </Typography>
                    <Chip
                      icon={getStatusIcon(order.status)}
                      label={order.status.replace('_', ' ')}
                      color={getStatusColor(order.status) as any}
                      size="small"
                    />
                  </Box>
                  
                  <Typography variant="body2" color="text.secondary" mb={1}>
                    Customer: {order.customerName} • Phone: {order.customerPhone}
                  </Typography>
                  
                  <Typography variant="body2" color="text.secondary" mb={1}>
                    Items: {order.items.length} • Total: ₹{order.totalAmount}
                  </Typography>
                  
                  <Typography variant="body2" color="text.secondary" mb={2}>
                    {new Date(order.createdAt).toLocaleString()}
                  </Typography>

                  {order.status === 'PENDING' && (
                    <Box display="flex" gap={1}>
                      <Button
                        variant="contained"
                        color="success"
                        size="small"
                        onClick={() => handleUpdateStatus(order.id, 'CONFIRMED')}
                      >
                        Accept Order
                      </Button>
                      <Button
                        variant="outlined"
                        color="error"
                        size="small"
                        onClick={() => handleUpdateStatus(order.id, 'CANCELLED')}
                      >
                        Reject Order
                      </Button>
                    </Box>
                  )}

                  {order.status === 'CONFIRMED' && (
                    <Button
                      variant="contained"
                      color="primary"
                      size="small"
                      onClick={() => handleUpdateStatus(order.id, 'PREPARING')}
                    >
                      Start Preparing
                    </Button>
                  )}

                  {order.status === 'PREPARING' && (
                    <Button
                      variant="contained"
                      color="secondary"
                      size="small"
                      onClick={() => handleUpdateStatus(order.id, 'READY_FOR_PICKUP')}
                    >
                      Ready for Pickup
                    </Button>
                  )}
                </Box>
                
                <Button
                  variant="outlined"
                  size="small"
                  startIcon={<Visibility />}
                  onClick={() => handleViewOrder(order)}
                >
                  View Details
                </Button>
              </Box>
            </CardContent>
          </Card>
        ))}
      </List>
    );
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
        Order Management
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={tabValue} onChange={handleTabChange} aria-label="orders tabs">
          <Tab label={`Pending (${pendingOrders.length})`} />
          <Tab label={`Completed (${completedOrders.length})`} />
          <Tab label={`All Orders (${orders.length})`} />
        </Tabs>
      </Box>

      <TabPanel value={tabValue} index={0}>
        {renderOrderList(pendingOrders)}
      </TabPanel>

      <TabPanel value={tabValue} index={1}>
        {renderOrderList(completedOrders)}
      </TabPanel>

      <TabPanel value={tabValue} index={2}>
        {renderOrderList(orders)}
      </TabPanel>

      {/* Order Details Dialog */}
      <Dialog
        open={orderDialogOpen}
        onClose={() => setOrderDialogOpen(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          Order Details #{selectedOrder?.id?.slice(-8)}
        </DialogTitle>
        <DialogContent>
          {selectedOrder && (
            <Box>
              <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                  <Typography variant="h6" gutterBottom>
                    Customer Information
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Name:</strong> {selectedOrder.customerName}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Phone:</strong> {selectedOrder.customerPhone}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Delivery Address:</strong> {selectedOrder.deliveryAddress}
                  </Typography>
                  {selectedOrder.specialInstructions && (
                    <Typography variant="body2" gutterBottom>
                      <strong>Special Instructions:</strong> {selectedOrder.specialInstructions}
                    </Typography>
                  )}
                </Grid>
                
                <Grid item xs={12} md={6}>
                  <Typography variant="h6" gutterBottom>
                    Order Information
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Order ID:</strong> {selectedOrder.id}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Status:</strong> {selectedOrder.status.replace('_', ' ')}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Created:</strong> {new Date(selectedOrder.createdAt).toLocaleString()}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Total Amount:</strong> ₹{selectedOrder.totalAmount}
                  </Typography>
                </Grid>
                
                <Grid item xs={12}>
                  <Typography variant="h6" gutterBottom>
                    Order Items
                  </Typography>
                  <List dense>
                    {selectedOrder.items.map((item: any, index: number) => (
                      <ListItem key={index}>
                        <ListItemText
                          primary={item.name}
                          secondary={`Quantity: ${item.quantity}`}
                        />
                        <ListItemSecondaryAction>
                          <Typography variant="body2">
                            ₹{item.price}
                          </Typography>
                        </ListItemSecondaryAction>
                      </ListItem>
                    ))}
                  </List>
                </Grid>
              </Grid>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOrderDialogOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default OrdersPage; 