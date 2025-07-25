import React, { useState, useEffect } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Container,
  Grid,
  Card,
  CardContent,
  Button,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  IconButton,
  Tab,
  Tabs,
  Box,
  Paper,
  Avatar,
  LinearProgress,
  Divider,
  Switch,
  FormControlLabel,
  Alert,
  Snackbar
} from '@mui/material';
import {
  DirectionsCar,
  LocationOn,
  Phone,
  CheckCircle,
  Cancel,
  Navigation,
  Assessment,
  Person,
  Notifications,
  MonetizationOn,
  Timer,
  Star,
  Route,
  LocalShipping,
  Assignment
} from '@mui/icons-material';
import axios from 'axios';
import './App.css';

interface DeliveryOrder {
  orderId: string;
  restaurantName: string;
  restaurantAddress: string;
  customerName: string;
  customerPhone: string;
  deliveryAddress: string;
  items: Array<{
    itemName: string;
    quantity: number;
  }>;
  totalAmount: number;
  deliveryFee: number;
  estimatedDistance: number;
  estimatedTime: number;
  status: string;
  pickupTime?: string;
  deliveryTime?: string;
  isGroupOrder: boolean;
  mergedOrderIds?: string[];
}

interface DeliveryPartner {
  partnerId: string;
  name: string;
  phone: string;
  rating: number;
  totalDeliveries: number;
  isOnline: boolean;
  currentLocation: {
    latitude: number;
    longitude: number;
  };
  vehicleType: string;
  earnings: {
    today: number;
    thisWeek: number;
    thisMonth: number;
  };
}

interface EarningsData {
  date: string;
  amount: number;
  deliveries: number;
}

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

function App() {
  const [currentTab, setCurrentTab] = useState(0);
  const [partner, setPartner] = useState<DeliveryPartner | null>(null);
  const [availableOrders, setAvailableOrders] = useState<DeliveryOrder[]>([]);
  const [activeOrders, setActiveOrders] = useState<DeliveryOrder[]>([]);
  const [completedOrders, setCompletedOrders] = useState<DeliveryOrder[]>([]);
  const [earningsData, setEarningsData] = useState<EarningsData[]>([]);
  const [selectedOrder, setSelectedOrder] = useState<DeliveryOrder | null>(null);
  const [isOrderDetailsOpen, setIsOrderDetailsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');

  // Sample data for demonstration
  const samplePartner: DeliveryPartner = {
    partnerId: 'DP001',
    name: 'John Smith',
    phone: '+1234567890',
    rating: 4.8,
    totalDeliveries: 234,
    isOnline: true,
    currentLocation: {
      latitude: 40.7128,
      longitude: -74.0060
    },
    vehicleType: 'Motorcycle',
    earnings: {
      today: 85.50,
      thisWeek: 420.75,
      thisMonth: 1680.25
    }
  };

  const sampleAvailableOrders: DeliveryOrder[] = [
    {
      orderId: 'ORD001',
      restaurantName: 'Pizza Palace',
      restaurantAddress: '123 Restaurant St',
      customerName: 'Alice Johnson',
      customerPhone: '+1987654321',
      deliveryAddress: '456 Customer Ave',
      items: [
        { itemName: 'Margherita Pizza', quantity: 1 },
        { itemName: 'Caesar Salad', quantity: 1 }
      ],
      totalAmount: 23.98,
      deliveryFee: 4.50,
      estimatedDistance: 2.5,
      estimatedTime: 15,
      status: 'READY_FOR_PICKUP',
      isGroupOrder: false
    },
    {
      orderId: 'ORD002',
      restaurantName: 'Burger Barn',
      restaurantAddress: '789 Food Court',
      customerName: 'Bob Wilson',
      customerPhone: '+1555666777',
      deliveryAddress: '321 Delivery Blvd',
      items: [
        { itemName: 'Classic Burger', quantity: 2 },
        { itemName: 'Fries', quantity: 2 }
      ],
      totalAmount: 28.50,
      deliveryFee: 3.75,
      estimatedDistance: 1.8,
      estimatedTime: 12,
      status: 'READY_FOR_PICKUP',
      isGroupOrder: true,
      mergedOrderIds: ['ORD002', 'ORD003']
    }
  ];

  const sampleActiveOrders: DeliveryOrder[] = [
    {
      orderId: 'ORD004',
      restaurantName: 'Sushi Zen',
      restaurantAddress: '555 Sushi Lane',
      customerName: 'Carol Davis',
      customerPhone: '+1444555666',
      deliveryAddress: '888 Home Street',
      items: [
        { itemName: 'Salmon Roll', quantity: 2 },
        { itemName: 'Miso Soup', quantity: 1 }
      ],
      totalAmount: 32.75,
      deliveryFee: 5.25,
      estimatedDistance: 3.2,
      estimatedTime: 18,
      status: 'PICKED_UP',
      pickupTime: '2:30 PM',
      isGroupOrder: false
    }
  ];

  const sampleEarningsData: EarningsData[] = [
    { date: '2024-01-01', amount: 95.50, deliveries: 8 },
    { date: '2024-01-02', amount: 120.25, deliveries: 10 },
    { date: '2024-01-03', amount: 85.75, deliveries: 7 },
    { date: '2024-01-04', amount: 110.00, deliveries: 9 },
    { date: '2024-01-05', amount: 85.50, deliveries: 6 }
  ];

  useEffect(() => {
    // Load sample data
    setPartner(samplePartner);
    setAvailableOrders(sampleAvailableOrders);
    setActiveOrders(sampleActiveOrders);
    setEarningsData(sampleEarningsData);
  }, []);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setCurrentTab(newValue);
  };

  const toggleOnlineStatus = async () => {
    if (!partner) return;

    setIsLoading(true);
    try {
      // In real app, make API call
      // await axios.patch(`${API_BASE_URL}/api/delivery/partners/${partner.partnerId}/status`, {
      //   isOnline: !partner.isOnline
      // });

      setPartner({ ...partner, isOnline: !partner.isOnline });
      setSnackbarMessage(partner.isOnline ? 'You are now offline' : 'You are now online');
      setSnackbarOpen(true);
    } catch (error) {
      console.error('Error updating status:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const acceptOrder = async (order: DeliveryOrder) => {
    setIsLoading(true);
    try {
      // In real app, make API call
      // await axios.post(`${API_BASE_URL}/api/delivery/orders/${order.orderId}/accept`);

      setAvailableOrders(availableOrders.filter(o => o.orderId !== order.orderId));
      setActiveOrders([{ ...order, status: 'ASSIGNED' }, ...activeOrders]);
      setSnackbarMessage('Order accepted successfully!');
      setSnackbarOpen(true);
    } catch (error) {
      console.error('Error accepting order:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const updateOrderStatus = async (orderId: string, newStatus: string) => {
    setIsLoading(true);
    try {
      // In real app, make API call
      // await axios.patch(`${API_BASE_URL}/api/delivery/orders/${orderId}/status`, {
      //   status: newStatus
      // });

      if (newStatus === 'DELIVERED') {
        const completedOrder = activeOrders.find(o => o.orderId === orderId);
        if (completedOrder) {
          setActiveOrders(activeOrders.filter(o => o.orderId !== orderId));
          setCompletedOrders([
            { ...completedOrder, status: newStatus, deliveryTime: new Date().toLocaleTimeString() },
            ...completedOrders
          ]);
          
          // Update earnings
          if (partner) {
            const newEarnings = partner.earnings.today + completedOrder.deliveryFee;
            setPartner({
              ...partner,
              earnings: { ...partner.earnings, today: newEarnings },
              totalDeliveries: partner.totalDeliveries + 1
            });
          }
        }
      } else {
        setActiveOrders(activeOrders.map(order =>
          order.orderId === orderId
            ? { ...order, status: newStatus, pickupTime: newStatus === 'PICKED_UP' ? new Date().toLocaleTimeString() : order.pickupTime }
            : order
        ));
      }

      setSnackbarMessage(`Order ${newStatus.toLowerCase().replace('_', ' ')}`);
      setSnackbarOpen(true);
    } catch (error) {
      console.error('Error updating order status:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const openOrderDetails = (order: DeliveryOrder) => {
    setSelectedOrder(order);
    setIsOrderDetailsOpen(true);
  };

  const renderDashboard = () => (
    <Container maxWidth="lg" sx={{ mt: 2 }}>
      {/* Status Toggle */}
      <Paper sx={{ p: 2, mb: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Box>
            <Typography variant="h6">Delivery Status</Typography>
            <Typography variant="body2" color="text.secondary">
              {partner?.isOnline ? 'You are online and ready for deliveries' : 'You are offline'}
            </Typography>
          </Box>
          <FormControlLabel
            control={
              <Switch
                checked={partner?.isOnline || false}
                onChange={toggleOnlineStatus}
                disabled={isLoading}
              />
            }
            label={partner?.isOnline ? 'Online' : 'Offline'}
          />
        </Box>
      </Paper>

      {/* Stats Cards */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={6} sm={3}>
          <Paper sx={{ p: 2, textAlign: 'center' }}>
            <MonetizationOn sx={{ fontSize: 40, color: 'success.main', mb: 1 }} />
            <Typography variant="h6">${partner?.earnings.today.toFixed(2)}</Typography>
            <Typography variant="body2" color="text.secondary">Today</Typography>
          </Paper>
        </Grid>
        <Grid item xs={6} sm={3}>
          <Paper sx={{ p: 2, textAlign: 'center' }}>
            <Assignment sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
            <Typography variant="h6">{activeOrders.length}</Typography>
            <Typography variant="body2" color="text.secondary">Active</Typography>
          </Paper>
        </Grid>
        <Grid item xs={6} sm={3}>
          <Paper sx={{ p: 2, textAlign: 'center' }}>
            <Star sx={{ fontSize: 40, color: 'warning.main', mb: 1 }} />
            <Typography variant="h6">{partner?.rating}</Typography>
            <Typography variant="body2" color="text.secondary">Rating</Typography>
          </Paper>
        </Grid>
        <Grid item xs={6} sm={3}>
          <Paper sx={{ p: 2, textAlign: 'center' }}>
            <LocalShipping sx={{ fontSize: 40, color: 'info.main', mb: 1 }} />
            <Typography variant="h6">{partner?.totalDeliveries}</Typography>
            <Typography variant="body2" color="text.secondary">Total</Typography>
          </Paper>
        </Grid>
      </Grid>

      {/* Available Orders */}
      <Typography variant="h6" gutterBottom>
        Available Orders ({availableOrders.length})
      </Typography>
      {availableOrders.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center', mb: 3 }}>
          <Typography variant="body1" color="text.secondary">
            No orders available at the moment
          </Typography>
          <Typography variant="body2" color="text.secondary">
            New orders will appear here when restaurants are ready
          </Typography>
        </Paper>
      ) : (
        <Grid container spacing={2} sx={{ mb: 3 }}>
          {availableOrders.map((order) => (
            <Grid item xs={12} key={order.orderId}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography variant="h6">{order.restaurantName}</Typography>
                    <Box sx={{ display: 'flex', gap: 1 }}>
                      {order.isGroupOrder && (
                        <Chip label="Group Order" size="small" color="secondary" />
                      )}
                      <Chip label={`$${order.deliveryFee.toFixed(2)}`} color="success" />
                    </Box>
                  </Box>
                  
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={6}>
                      <Typography variant="body2" gutterBottom>
                        <LocationOn sx={{ fontSize: 16, mr: 0.5 }} />
                        Pickup: {order.restaurantAddress}
                      </Typography>
                      <Typography variant="body2" gutterBottom>
                        <LocationOn sx={{ fontSize: 16, mr: 0.5 }} />
                        Delivery: {order.deliveryAddress}
                      </Typography>
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <Typography variant="body2" gutterBottom>
                        <Timer sx={{ fontSize: 16, mr: 0.5 }} />
                        {order.estimatedTime} min • {order.estimatedDistance} km
                      </Typography>
                      <Typography variant="body2" gutterBottom>
                        <Phone sx={{ fontSize: 16, mr: 0.5 }} />
                        {order.customerName}: {order.customerPhone}
                      </Typography>
                    </Grid>
                  </Grid>
                  
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2 }}>
                    <Button
                      variant="outlined"
                      size="small"
                      onClick={() => openOrderDetails(order)}
                    >
                      View Details
                    </Button>
                    <Button
                      variant="contained"
                      onClick={() => acceptOrder(order)}
                      disabled={isLoading || !partner?.isOnline}
                    >
                      Accept Order
                    </Button>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      {/* Active Orders */}
      {activeOrders.length > 0 && (
        <>
          <Typography variant="h6" gutterBottom>
            Active Deliveries ({activeOrders.length})
          </Typography>
          <Grid container spacing={2}>
            {activeOrders.map((order) => (
              <Grid item xs={12} key={order.orderId}>
                <Card sx={{ border: '2px solid', borderColor: 'primary.main' }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                      <Typography variant="h6">{order.restaurantName}</Typography>
                      <Chip 
                        label={order.status.replace('_', ' ')} 
                        color="primary"
                        icon={order.status === 'PICKED_UP' ? <DirectionsCar /> : <Assignment />}
                      />
                    </Box>
                    
                    <Typography variant="body2" gutterBottom>
                      <LocationOn sx={{ fontSize: 16, mr: 0.5 }} />
                      Deliver to: {order.deliveryAddress}
                    </Typography>
                    
                    <Typography variant="body2" gutterBottom>
                      <Phone sx={{ fontSize: 16, mr: 0.5 }} />
                      {order.customerName}: {order.customerPhone}
                    </Typography>
                    
                    {order.pickupTime && (
                      <Typography variant="body2" color="success.main" gutterBottom>
                        Picked up at: {order.pickupTime}
                      </Typography>
                    )}
                    
                    <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
                      <Button
                        variant="outlined"
                        size="small"
                        startIcon={<Navigation />}
                      >
                        Navigate
                      </Button>
                      
                      {order.status === 'ASSIGNED' && (
                        <Button
                          variant="contained"
                          size="small"
                          onClick={() => updateOrderStatus(order.orderId, 'PICKED_UP')}
                          disabled={isLoading}
                        >
                          Mark Picked Up
                        </Button>
                      )}
                      
                      {order.status === 'PICKED_UP' && (
                        <Button
                          variant="contained"
                          color="success"
                          size="small"
                          onClick={() => updateOrderStatus(order.orderId, 'DELIVERED')}
                          disabled={isLoading}
                        >
                          Mark Delivered
                        </Button>
                      )}
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </>
      )}
    </Container>
  );

  const renderEarnings = () => (
    <Container maxWidth="lg" sx={{ mt: 2 }}>
      <Typography variant="h5" gutterBottom>
        Earnings Dashboard
      </Typography>
      
      {/* Earnings Summary */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={4}>
          <Paper sx={{ p: 3, textAlign: 'center' }}>
            <Typography variant="h4" color="success.main">
              ${partner?.earnings.today.toFixed(2)}
            </Typography>
            <Typography variant="body1">Today</Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} sm={4}>
          <Paper sx={{ p: 3, textAlign: 'center' }}>
            <Typography variant="h4" color="primary.main">
              ${partner?.earnings.thisWeek.toFixed(2)}
            </Typography>
            <Typography variant="body1">This Week</Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} sm={4}>
          <Paper sx={{ p: 3, textAlign: 'center' }}>
            <Typography variant="h4" color="secondary.main">
              ${partner?.earnings.thisMonth.toFixed(2)}
            </Typography>
            <Typography variant="body1">This Month</Typography>
          </Paper>
        </Grid>
      </Grid>

      {/* Recent Earnings */}
      <Paper sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>
          Recent Earnings
        </Typography>
        <List>
          {earningsData.map((day, index) => (
            <ListItem key={index}>
              <ListItemIcon>
                <MonetizationOn />
              </ListItemIcon>
              <ListItemText
                primary={`$${day.amount.toFixed(2)}`}
                secondary={`${day.date} • ${day.deliveries} deliveries`}
              />
            </ListItem>
          ))}
        </List>
      </Paper>
    </Container>
  );

  const renderHistory = () => (
    <Container maxWidth="lg" sx={{ mt: 2 }}>
      <Typography variant="h5" gutterBottom>
        Delivery History
      </Typography>
      
      {completedOrders.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6" color="text.secondary">
            No completed deliveries yet
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Your completed deliveries will appear here
          </Typography>
        </Paper>
      ) : (
        <Grid container spacing={2}>
          {completedOrders.map((order) => (
            <Grid item xs={12} key={order.orderId}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography variant="h6">{order.restaurantName}</Typography>
                    <Chip label="Delivered" color="success" icon={<CheckCircle />} />
                  </Box>
                  
                  <Typography variant="body2" gutterBottom>
                    Customer: {order.customerName}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    Delivered to: {order.deliveryAddress}
                  </Typography>
                  
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                      Delivered at: {order.deliveryTime}
                    </Typography>
                    <Typography variant="h6" color="success.main">
                      +${order.deliveryFee.toFixed(2)}
                    </Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Container>
  );

  const renderProfile = () => (
    <Container maxWidth="sm" sx={{ mt: 2 }}>
      <Paper sx={{ p: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
          <Avatar sx={{ width: 60, height: 60, mr: 2 }}>
            <Person />
          </Avatar>
          <Box>
            <Typography variant="h6">{partner?.name}</Typography>
            <Typography variant="body2" color="text.secondary">
              {partner?.phone}
            </Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', mt: 0.5 }}>
              <Star sx={{ color: 'gold', fontSize: 16, mr: 0.5 }} />
              <Typography variant="body2">{partner?.rating} rating</Typography>
            </Box>
          </Box>
        </Box>
        
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid item xs={6}>
            <Paper sx={{ p: 2, textAlign: 'center' }}>
              <Typography variant="h6">{partner?.totalDeliveries}</Typography>
              <Typography variant="body2" color="text.secondary">
                Total Deliveries
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={6}>
            <Paper sx={{ p: 2, textAlign: 'center' }}>
              <Typography variant="h6">{partner?.vehicleType}</Typography>
              <Typography variant="body2" color="text.secondary">
                Vehicle Type
              </Typography>
            </Paper>
          </Grid>
        </Grid>
        
        <Button variant="contained" fullWidth sx={{ mb: 2 }}>
          Edit Profile
        </Button>
        
        <Button variant="outlined" fullWidth>
          Vehicle Settings
        </Button>
      </Paper>
    </Container>
  );

  return (
    <div className="App">
      <AppBar position="fixed">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            MergeEats Delivery
          </Typography>
          <IconButton color="inherit">
            <Notifications />
          </IconButton>
        </Toolbar>
      </AppBar>

      <Box sx={{ mt: 8 }}>
        <Tabs
          value={currentTab}
          onChange={handleTabChange}
          centered
          sx={{ borderBottom: 1, borderColor: 'divider' }}
        >
          <Tab icon={<DirectionsCar />} label="Dashboard" />
          <Tab icon={<Assessment />} label="Earnings" />
          <Tab icon={<Route />} label="History" />
          <Tab icon={<Person />} label="Profile" />
        </Tabs>

        {currentTab === 0 && renderDashboard()}
        {currentTab === 1 && renderEarnings()}
        {currentTab === 2 && renderHistory()}
        {currentTab === 3 && renderProfile()}
      </Box>

      {/* Order Details Dialog */}
      <Dialog 
        open={isOrderDetailsOpen} 
        onClose={() => setIsOrderDetailsOpen(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>
          Order Details - {selectedOrder?.orderId}
        </DialogTitle>
        <DialogContent>
          {selectedOrder && (
            <Box>
              <Typography variant="h6" gutterBottom>
                {selectedOrder.restaurantName}
              </Typography>
              
              <Typography variant="body2" gutterBottom>
                <strong>Customer:</strong> {selectedOrder.customerName}
              </Typography>
              <Typography variant="body2" gutterBottom>
                <strong>Phone:</strong> {selectedOrder.customerPhone}
              </Typography>
              <Typography variant="body2" gutterBottom>
                <strong>Pickup:</strong> {selectedOrder.restaurantAddress}
              </Typography>
              <Typography variant="body2" gutterBottom>
                <strong>Delivery:</strong> {selectedOrder.deliveryAddress}
              </Typography>
              
              <Divider sx={{ my: 2 }} />
              
              <Typography variant="subtitle1" gutterBottom>
                Order Items:
              </Typography>
              <List dense>
                {selectedOrder.items.map((item, index) => (
                  <ListItem key={index}>
                    <ListItemText
                      primary={`${item.quantity}x ${item.itemName}`}
                    />
                  </ListItem>
                ))}
              </List>
              
              <Divider sx={{ my: 2 }} />
              
              <Typography variant="body2" gutterBottom>
                <strong>Total Amount:</strong> ${selectedOrder.totalAmount.toFixed(2)}
              </Typography>
              <Typography variant="body2" gutterBottom>
                <strong>Delivery Fee:</strong> ${selectedOrder.deliveryFee.toFixed(2)}
              </Typography>
              <Typography variant="body2" gutterBottom>
                <strong>Distance:</strong> {selectedOrder.estimatedDistance} km
              </Typography>
              <Typography variant="body2" gutterBottom>
                <strong>Estimated Time:</strong> {selectedOrder.estimatedTime} minutes
              </Typography>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setIsOrderDetailsOpen(false)}>Close</Button>
          {selectedOrder && availableOrders.find(o => o.orderId === selectedOrder.orderId) && (
            <Button 
              variant="contained" 
              onClick={() => {
                acceptOrder(selectedOrder);
                setIsOrderDetailsOpen(false);
              }}
              disabled={isLoading || !partner?.isOnline}
            >
              Accept Order
            </Button>
          )}
        </DialogActions>
      </Dialog>

      {/* Snackbar for notifications */}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={3000}
        onClose={() => setSnackbarOpen(false)}
        message={snackbarMessage}
      />

      {isLoading && (
        <LinearProgress 
          sx={{ 
            position: 'fixed', 
            top: 64, 
            left: 0, 
            right: 0, 
            zIndex: 1300 
          }} 
        />
      )}
    </div>
  );
}

export default App;