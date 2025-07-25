import React, { useState, useEffect } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Container,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Button,
  Chip,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Badge,
  IconButton,
  Tab,
  Tabs,
  Box,
  Paper,
  Avatar,
  LinearProgress,
  Divider
} from '@mui/material';
import {
  Restaurant,
  ShoppingCart,
  Search,
  LocationOn,
  Star,
  Group,
  Delivery,
  Payment,
  History,
  Person,
  Add,
  Remove,
  TrackChanges
} from '@mui/icons-material';
import axios from 'axios';
import './App.css';

interface Restaurant {
  restaurantId: string;
  name: string;
  description: string;
  cuisineTypes: string[];
  rating: number;
  deliveryFee: number;
  estimatedPreparationTime: number;
  imageUrl?: string;
}

interface MenuItem {
  itemId: string;
  name: string;
  description: string;
  price: number;
  category: string;
  imageUrl?: string;
}

interface CartItem {
  menuItemId: string;
  itemName: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

interface Order {
  orderId: string;
  restaurantName: string;
  items: CartItem[];
  totalAmount: number;
  status: string;
  deliveryAddress: string;
  estimatedDeliveryTime?: string;
  isGroupOrder: boolean;
  groupOrderId?: string;
}

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

function App() {
  const [currentTab, setCurrentTab] = useState(0);
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);
  const [selectedRestaurant, setSelectedRestaurant] = useState<Restaurant | null>(null);
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [isGroupOrderDialog, setIsGroupOrderDialog] = useState(false);
  const [groupOrderCode, setGroupOrderCode] = useState('');
  const [userLocation, setUserLocation] = useState('123 Main St, City');
  const [isLoading, setIsLoading] = useState(false);

  // Sample data for demonstration
  const sampleRestaurants: Restaurant[] = [
    {
      restaurantId: '1',
      name: 'Pizza Palace',
      description: 'Authentic Italian pizzas with fresh ingredients',
      cuisineTypes: ['Italian', 'Pizza'],
      rating: 4.5,
      deliveryFee: 2.99,
      estimatedPreparationTime: 25,
      imageUrl: 'https://via.placeholder.com/300x200?text=Pizza+Palace'
    },
    {
      restaurantId: '2',
      name: 'Burger Barn',
      description: 'Gourmet burgers and crispy fries',
      cuisineTypes: ['American', 'Burgers'],
      rating: 4.2,
      deliveryFee: 1.99,
      estimatedPreparationTime: 20,
      imageUrl: 'https://via.placeholder.com/300x200?text=Burger+Barn'
    },
    {
      restaurantId: '3',
      name: 'Sushi Zen',
      description: 'Fresh sushi and Japanese cuisine',
      cuisineTypes: ['Japanese', 'Sushi'],
      rating: 4.8,
      deliveryFee: 3.99,
      estimatedPreparationTime: 30,
      imageUrl: 'https://via.placeholder.com/300x200?text=Sushi+Zen'
    }
  ];

  const sampleMenuItems: MenuItem[] = [
    {
      itemId: '1',
      name: 'Margherita Pizza',
      description: 'Classic pizza with tomato sauce, mozzarella, and basil',
      price: 14.99,
      category: 'Pizza'
    },
    {
      itemId: '2',
      name: 'Pepperoni Pizza',
      description: 'Pizza with pepperoni and mozzarella cheese',
      price: 16.99,
      category: 'Pizza'
    },
    {
      itemId: '3',
      name: 'Caesar Salad',
      description: 'Fresh romaine lettuce with Caesar dressing',
      price: 8.99,
      category: 'Salads'
    }
  ];

  const sampleOrders: Order[] = [
    {
      orderId: 'ORD001',
      restaurantName: 'Pizza Palace',
      items: [
        {
          menuItemId: '1',
          itemName: 'Margherita Pizza',
          quantity: 1,
          unitPrice: 14.99,
          totalPrice: 14.99
        }
      ],
      totalAmount: 17.98,
      status: 'PREPARING',
      deliveryAddress: '123 Main St, City',
      estimatedDeliveryTime: '25 minutes',
      isGroupOrder: false
    }
  ];

  useEffect(() => {
    // Load sample data
    setRestaurants(sampleRestaurants);
    setOrders(sampleOrders);
  }, []);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setCurrentTab(newValue);
  };

  const handleRestaurantSelect = (restaurant: Restaurant) => {
    setSelectedRestaurant(restaurant);
    setMenuItems(sampleMenuItems);
    setIsMenuOpen(true);
  };

  const addToCart = (menuItem: MenuItem) => {
    const existingItem = cart.find(item => item.menuItemId === menuItem.itemId);
    if (existingItem) {
      setCart(cart.map(item =>
        item.menuItemId === menuItem.itemId
          ? { ...item, quantity: item.quantity + 1, totalPrice: (item.quantity + 1) * item.unitPrice }
          : item
      ));
    } else {
      setCart([...cart, {
        menuItemId: menuItem.itemId,
        itemName: menuItem.name,
        quantity: 1,
        unitPrice: menuItem.price,
        totalPrice: menuItem.price
      }]);
    }
  };

  const removeFromCart = (menuItemId: string) => {
    const existingItem = cart.find(item => item.menuItemId === menuItemId);
    if (existingItem && existingItem.quantity > 1) {
      setCart(cart.map(item =>
        item.menuItemId === menuItemId
          ? { ...item, quantity: item.quantity - 1, totalPrice: (item.quantity - 1) * item.unitPrice }
          : item
      ));
    } else {
      setCart(cart.filter(item => item.menuItemId !== menuItemId));
    }
  };

  const getTotalAmount = () => {
    return cart.reduce((total, item) => total + item.totalPrice, 0);
  };

  const handlePlaceOrder = async (isGroupOrder = false) => {
    if (cart.length === 0 || !selectedRestaurant) return;

    setIsLoading(true);
    try {
      const orderData = {
        userId: 'user123', // In real app, get from auth
        restaurantId: selectedRestaurant.restaurantId,
        items: cart,
        deliveryAddress: userLocation,
        isGroupOrder,
        groupOrderId: isGroupOrder ? groupOrderCode : undefined
      };

      // In real app, make API call
      // const response = await axios.post(`${API_BASE_URL}/api/orders/create`, orderData);
      
      // Simulate API call
      const newOrder: Order = {
        orderId: `ORD${Date.now()}`,
        restaurantName: selectedRestaurant.name,
        items: [...cart],
        totalAmount: getTotalAmount() + selectedRestaurant.deliveryFee,
        status: 'PENDING',
        deliveryAddress: userLocation,
        isGroupOrder,
        groupOrderId: isGroupOrder ? groupOrderCode : undefined
      };

      setOrders([newOrder, ...orders]);
      setCart([]);
      setIsCartOpen(false);
      setIsMenuOpen(false);
      setCurrentTab(2); // Switch to orders tab
    } catch (error) {
      console.error('Error placing order:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const renderRestaurants = () => (
    <Container maxWidth="lg" sx={{ mt: 2 }}>
      <TextField
        fullWidth
        placeholder="Search restaurants..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        InputProps={{
          startAdornment: <Search sx={{ mr: 1 }} />
        }}
        sx={{ mb: 3 }}
      />
      
      <Grid container spacing={3}>
        {restaurants
          .filter(restaurant => 
            restaurant.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            restaurant.cuisineTypes.some(cuisine => 
              cuisine.toLowerCase().includes(searchQuery.toLowerCase())
            )
          )
          .map((restaurant) => (
            <Grid item xs={12} sm={6} md={4} key={restaurant.restaurantId}>
              <Card 
                sx={{ cursor: 'pointer', '&:hover': { elevation: 8 } }}
                onClick={() => handleRestaurantSelect(restaurant)}
              >
                <CardMedia
                  component="img"
                  height="140"
                  image={restaurant.imageUrl}
                  alt={restaurant.name}
                />
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {restaurant.name}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    {restaurant.description}
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <Star sx={{ color: 'gold', mr: 0.5 }} />
                    <Typography variant="body2">{restaurant.rating}</Typography>
                  </Box>
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, mb: 1 }}>
                    {restaurant.cuisineTypes.map((cuisine) => (
                      <Chip key={cuisine} label={cuisine} size="small" />
                    ))}
                  </Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Typography variant="body2">
                      Delivery: ${restaurant.deliveryFee}
                    </Typography>
                    <Typography variant="body2">
                      {restaurant.estimatedPreparationTime} min
                    </Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
      </Grid>
    </Container>
  );

  const renderOrders = () => (
    <Container maxWidth="lg" sx={{ mt: 2 }}>
      <Typography variant="h5" gutterBottom>
        Your Orders
      </Typography>
      {orders.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6" color="text.secondary">
            No orders yet
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Start by browsing restaurants and placing your first order!
          </Typography>
        </Paper>
      ) : (
        <Grid container spacing={2}>
          {orders.map((order) => (
            <Grid item xs={12} key={order.orderId}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography variant="h6">
                      {order.restaurantName}
                    </Typography>
                    <Chip 
                      label={order.status} 
                      color={order.status === 'DELIVERED' ? 'success' : 'primary'}
                      icon={<TrackChanges />}
                    />
                  </Box>
                  
                  {order.isGroupOrder && (
                    <Chip 
                      label={`Group Order: ${order.groupOrderId}`} 
                      icon={<Group />}
                      sx={{ mb: 2 }}
                    />
                  )}
                  
                  <List dense>
                    {order.items.map((item, index) => (
                      <ListItem key={index}>
                        <ListItemText
                          primary={`${item.quantity}x ${item.itemName}`}
                          secondary={`$${item.totalPrice.toFixed(2)}`}
                        />
                      </ListItem>
                    ))}
                  </List>
                  
                  <Divider sx={{ my: 1 }} />
                  
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Typography variant="h6">
                      Total: ${order.totalAmount.toFixed(2)}
                    </Typography>
                    {order.status !== 'DELIVERED' && (
                      <Button 
                        variant="outlined" 
                        startIcon={<LocationOn />}
                        size="small"
                      >
                        Track Order
                      </Button>
                    )}
                  </Box>
                  
                  {order.estimatedDeliveryTime && (
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                      Estimated delivery: {order.estimatedDeliveryTime}
                    </Typography>
                  )}
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
            <Typography variant="h6">John Doe</Typography>
            <Typography variant="body2" color="text.secondary">
              john.doe@example.com
            </Typography>
          </Box>
        </Box>
        
        <TextField
          fullWidth
          label="Delivery Address"
          value={userLocation}
          onChange={(e) => setUserLocation(e.target.value)}
          sx={{ mb: 2 }}
          InputProps={{
            startAdornment: <LocationOn sx={{ mr: 1 }} />
          }}
        />
        
        <Button variant="contained" fullWidth sx={{ mb: 2 }}>
          Update Profile
        </Button>
        
        <Button variant="outlined" fullWidth>
          Order History
        </Button>
      </Paper>
    </Container>
  );

  return (
    <div className="App">
      <AppBar position="fixed">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            MergeEats Customer
          </Typography>
          <IconButton color="inherit" onClick={() => setIsCartOpen(true)}>
            <Badge badgeContent={cart.length} color="secondary">
              <ShoppingCart />
            </Badge>
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
          <Tab icon={<Restaurant />} label="Restaurants" />
          <Tab icon={<Group />} label="Group Orders" />
          <Tab icon={<History />} label="Orders" />
          <Tab icon={<Person />} label="Profile" />
        </Tabs>

        {currentTab === 0 && renderRestaurants()}
        {currentTab === 1 && (
          <Container maxWidth="sm" sx={{ mt: 4 }}>
            <Paper sx={{ p: 3, textAlign: 'center' }}>
              <Group sx={{ fontSize: 60, color: 'primary.main', mb: 2 }} />
              <Typography variant="h5" gutterBottom>
                Group Ordering
              </Typography>
              <Typography variant="body1" color="text.secondary" gutterBottom>
                Join a group order or start a new one with friends!
              </Typography>
              
              <TextField
                fullWidth
                label="Enter Group Order Code"
                value={groupOrderCode}
                onChange={(e) => setGroupOrderCode(e.target.value)}
                sx={{ my: 2 }}
              />
              
              <Button 
                variant="contained" 
                fullWidth 
                sx={{ mb: 1 }}
                disabled={!groupOrderCode}
              >
                Join Group Order
              </Button>
              
              <Button variant="outlined" fullWidth>
                Start New Group Order
              </Button>
            </Paper>
          </Container>
        )}
        {currentTab === 2 && renderOrders()}
        {currentTab === 3 && renderProfile()}
      </Box>

      {/* Menu Dialog */}
      <Dialog 
        open={isMenuOpen} 
        onClose={() => setIsMenuOpen(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          {selectedRestaurant?.name} Menu
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2}>
            {menuItems.map((item) => (
              <Grid item xs={12} key={item.itemId}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Box>
                        <Typography variant="h6">{item.name}</Typography>
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                          {item.description}
                        </Typography>
                        <Typography variant="h6" color="primary">
                          ${item.price.toFixed(2)}
                        </Typography>
                      </Box>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        {cart.find(cartItem => cartItem.menuItemId === item.itemId) ? (
                          <Box sx={{ display: 'flex', alignItems: 'center' }}>
                            <IconButton onClick={() => removeFromCart(item.itemId)}>
                              <Remove />
                            </IconButton>
                            <Typography sx={{ mx: 1 }}>
                              {cart.find(cartItem => cartItem.menuItemId === item.itemId)?.quantity}
                            </Typography>
                            <IconButton onClick={() => addToCart(item)}>
                              <Add />
                            </IconButton>
                          </Box>
                        ) : (
                          <Button 
                            variant="contained" 
                            onClick={() => addToCart(item)}
                            startIcon={<Add />}
                          >
                            Add
                          </Button>
                        )}
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setIsMenuOpen(false)}>Close</Button>
          <Button 
            variant="contained" 
            onClick={() => setIsCartOpen(true)}
            disabled={cart.length === 0}
          >
            View Cart ({cart.length})
          </Button>
        </DialogActions>
      </Dialog>

      {/* Cart Dialog */}
      <Dialog 
        open={isCartOpen} 
        onClose={() => setIsCartOpen(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Your Cart</DialogTitle>
        <DialogContent>
          {cart.length === 0 ? (
            <Typography>Your cart is empty</Typography>
          ) : (
            <List>
              {cart.map((item) => (
                <ListItem key={item.menuItemId}>
                  <ListItemText
                    primary={item.itemName}
                    secondary={`$${item.unitPrice.toFixed(2)} each`}
                  />
                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <IconButton onClick={() => removeFromCart(item.menuItemId)}>
                      <Remove />
                    </IconButton>
                    <Typography sx={{ mx: 1 }}>{item.quantity}</Typography>
                    <IconButton onClick={() => addToCart({ 
                      itemId: item.menuItemId, 
                      name: item.itemName, 
                      price: item.unitPrice,
                      description: '',
                      category: ''
                    })}>
                      <Add />
                    </IconButton>
                    <Typography sx={{ ml: 2, minWidth: 60 }}>
                      ${item.totalPrice.toFixed(2)}
                    </Typography>
                  </Box>
                </ListItem>
              ))}
              <Divider />
              <ListItem>
                <ListItemText primary="Delivery Fee" />
                <Typography>${selectedRestaurant?.deliveryFee.toFixed(2)}</Typography>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary={<Typography variant="h6">Total</Typography>}
                />
                <Typography variant="h6">
                  ${(getTotalAmount() + (selectedRestaurant?.deliveryFee || 0)).toFixed(2)}
                </Typography>
              </ListItem>
            </List>
          )}
          {isLoading && <LinearProgress />}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setIsCartOpen(false)}>Close</Button>
          <Button 
            variant="outlined" 
            onClick={() => setIsGroupOrderDialog(true)}
            disabled={cart.length === 0 || isLoading}
          >
            Group Order
          </Button>
          <Button 
            variant="contained" 
            onClick={() => handlePlaceOrder(false)}
            disabled={cart.length === 0 || isLoading}
          >
            Place Order
          </Button>
        </DialogActions>
      </Dialog>

      {/* Group Order Dialog */}
      <Dialog 
        open={isGroupOrderDialog} 
        onClose={() => setIsGroupOrderDialog(false)}
      >
        <DialogTitle>Group Order</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Group Order Code (optional)"
            value={groupOrderCode}
            onChange={(e) => setGroupOrderCode(e.target.value)}
            helperText="Leave empty to create a new group order"
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setIsGroupOrderDialog(false)}>Cancel</Button>
          <Button 
            variant="contained" 
            onClick={() => {
              handlePlaceOrder(true);
              setIsGroupOrderDialog(false);
            }}
          >
            Create Group Order
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default App;