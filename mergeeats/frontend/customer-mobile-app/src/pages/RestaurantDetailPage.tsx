import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Button,
  Chip,
  Rating,
  CircularProgress,
  Alert,
  Tabs,
  Tab,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Divider,
  Badge,
} from '@mui/material';
import {
  Add,
  Remove,
  ShoppingCart,
  Star,
  LocationOn,
  Phone,
  AccessTime,
  Favorite,
  FavoriteBorder,
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';
import axios from 'axios';

interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
  imageUrl?: string;
  category: string;
  isVegetarian: boolean;
  isAvailable: boolean;
}

interface Restaurant {
  id: string;
  name: string;
  description: string;
  cuisine: string;
  rating: number;
  totalReviews: number;
  imageUrl?: string;
  isOpen: boolean;
  phone: string;
  address: {
    street: string;
    city: string;
    state: string;
    zipCode: string;
  };
  openingHours: { [key: string]: string };
}

const RestaurantDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const { addItem, getItemCount } = useCart();

  const [restaurant, setRestaurant] = useState<Restaurant | null>(null);
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedCategory, setSelectedCategory] = useState(0);
  const [cartDialogOpen, setCartDialogOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState<MenuItem | null>(null);
  const [quantity, setQuantity] = useState(1);

  useEffect(() => {
    if (id) {
      fetchRestaurantDetails();
    }
  }, [id]);

  const fetchRestaurantDetails = async () => {
    try {
      setLoading(true);
      const [restaurantResponse, menuResponse] = await Promise.all([
        axios.get(`http://localhost:8080/api/restaurants/${id}`),
        axios.get(`http://localhost:8080/api/restaurants/${id}/menu`)
      ]);

      setRestaurant(restaurantResponse.data);
      setMenuItems(menuResponse.data);
    } catch (error: any) {
      setError('Failed to load restaurant details');
      console.error('Error fetching restaurant details:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = (item: MenuItem) => {
    if (!user) {
      navigate('/login');
      return;
    }

    setSelectedItem(item);
    setQuantity(1);
    setCartDialogOpen(true);
  };

  const handleConfirmAddToCart = () => {
    if (selectedItem && restaurant) {
      addItem(selectedItem, restaurant.id, restaurant.name);
      setCartDialogOpen(false);
      setSelectedItem(null);
    }
  };

  const handleQuantityChange = (newQuantity: number) => {
    if (newQuantity >= 1) {
      setQuantity(newQuantity);
    }
  };

  const categories = Array.from(new Set(menuItems.map(item => item.category)));

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  if (error || !restaurant) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Alert severity="error">
          {error || 'Restaurant not found'}
        </Alert>
      </Container>
    );
  }

  return (
    <>
      <Container maxWidth="lg" sx={{ py: 4 }}>
        {/* Restaurant Header */}
        <Card sx={{ mb: 4, overflow: 'hidden' }}>
          <Box position="relative">
            <CardMedia
              component="img"
              height="300"
              image={restaurant.imageUrl || '/default-restaurant.jpg'}
              alt={restaurant.name}
              sx={{ objectFit: 'cover' }}
            />
            <Box
              position="absolute"
              top={16}
              right={16}
              display="flex"
              gap={1}
            >
              <IconButton sx={{ bgcolor: 'rgba(255,255,255,0.9)' }}>
                <FavoriteBorder />
              </IconButton>
              <Badge badgeContent={getItemCount()} color="primary">
                <IconButton
                  sx={{ bgcolor: 'rgba(255,255,255,0.9)' }}
                  onClick={() => navigate('/cart')}
                >
                  <ShoppingCart />
                </IconButton>
              </Badge>
            </Box>
          </Box>
          <CardContent>
            <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
              <Box>
                <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
                  {restaurant.name}
                </Typography>
                <Typography variant="body1" color="text.secondary" mb={2}>
                  {restaurant.description}
                </Typography>
                <Box display="flex" alignItems="center" gap={2} mb={2}>
                  <Box display="flex" alignItems="center">
                    <Rating value={restaurant.rating} readOnly size="small" />
                    <Typography variant="body2" color="text.secondary" ml={1}>
                      ({restaurant.totalReviews} reviews)
                    </Typography>
                  </Box>
                  <Chip label={restaurant.cuisine} color="primary" variant="outlined" />
                  <Chip
                    label={restaurant.isOpen ? 'Open' : 'Closed'}
                    color={restaurant.isOpen ? 'success' : 'error'}
                  />
                </Box>
              </Box>
            </Box>

            <Grid container spacing={2}>
              <Grid item xs={12} md={4}>
                <Box display="flex" alignItems="center" gap={1}>
                  <LocationOn color="action" />
                  <Typography variant="body2">
                    {restaurant.address.street}, {restaurant.address.city}
                  </Typography>
                </Box>
              </Grid>
              <Grid item xs={12} md={4}>
                <Box display="flex" alignItems="center" gap={1}>
                  <Phone color="action" />
                  <Typography variant="body2">
                    {restaurant.phone}
                  </Typography>
                </Box>
              </Grid>
              <Grid item xs={12} md={4}>
                <Box display="flex" alignItems="center" gap={1}>
                  <AccessTime color="action" />
                  <Typography variant="body2">
                    Open Now
                  </Typography>
                </Box>
              </Grid>
            </Grid>
          </CardContent>
        </Card>

        {/* Menu Categories */}
        <Box mb={3}>
          <Tabs
            value={selectedCategory}
            onChange={(_, newValue) => setSelectedCategory(newValue)}
            variant="scrollable"
            scrollButtons="auto"
          >
            {categories.map((category, index) => (
              <Tab key={category} label={category} />
            ))}
          </Tabs>
        </Box>

        {/* Menu Items */}
        <Grid container spacing={3}>
          {menuItems
            .filter(item => item.category === categories[selectedCategory])
            .map((item) => (
              <Grid item xs={12} sm={6} md={4} key={item.id}>
                <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                  <CardMedia
                    component="img"
                    height="200"
                    image={item.imageUrl || '/default-food.jpg'}
                    alt={item.name}
                    sx={{ objectFit: 'cover' }}
                  />
                  <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
                    <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={1}>
                      <Typography variant="h6" component="h3" fontWeight="600">
                        {item.name}
                      </Typography>
                      {item.isVegetarian && (
                        <Chip label="Veg" color="success" size="small" />
                      )}
                    </Box>
                    
                    <Typography variant="body2" color="text.secondary" mb={2} sx={{ flexGrow: 1 }}>
                      {item.description}
                    </Typography>

                    <Box display="flex" justifyContent="space-between" alignItems="center">
                      <Typography variant="h6" color="primary" fontWeight="bold">
                        ₹{item.price}
                      </Typography>
                      <Button
                        variant="contained"
                        size="small"
                        disabled={!item.isAvailable}
                        onClick={() => handleAddToCart(item)}
                        startIcon={<Add />}
                      >
                        Add
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
        </Grid>
      </Container>

      {/* Add to Cart Dialog */}
      <Dialog open={cartDialogOpen} onClose={() => setCartDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add to Cart</DialogTitle>
        <DialogContent>
          {selectedItem && (
            <Box>
              <Box display="flex" alignItems="center" gap={2} mb={2}>
                <img
                  src={selectedItem.imageUrl || '/default-food.jpg'}
                  alt={selectedItem.name}
                  style={{ width: 80, height: 80, objectFit: 'cover', borderRadius: 8 }}
                />
                <Box>
                  <Typography variant="h6">{selectedItem.name}</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {selectedItem.description}
                  </Typography>
                  <Typography variant="h6" color="primary" fontWeight="bold">
                    ₹{selectedItem.price}
                  </Typography>
                </Box>
              </Box>
              
              <Divider sx={{ my: 2 }} />
              
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Typography variant="h6">Quantity</Typography>
                <Box display="flex" alignItems="center" gap={1}>
                  <IconButton
                    onClick={() => handleQuantityChange(quantity - 1)}
                    disabled={quantity <= 1}
                  >
                    <Remove />
                  </IconButton>
                  <TextField
                    value={quantity}
                    onChange={(e) => handleQuantityChange(parseInt(e.target.value) || 1)}
                    type="number"
                    size="small"
                    sx={{ width: 80 }}
                    inputProps={{ min: 1 }}
                  />
                  <IconButton onClick={() => handleQuantityChange(quantity + 1)}>
                    <Add />
                  </IconButton>
                </Box>
              </Box>
              
              <Divider sx={{ my: 2 }} />
              
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Typography variant="h6">Total</Typography>
                <Typography variant="h6" color="primary" fontWeight="bold">
                  ₹{selectedItem.price * quantity}
                </Typography>
              </Box>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCartDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleConfirmAddToCart} variant="contained">
            Add to Cart
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default RestaurantDetailPage; 