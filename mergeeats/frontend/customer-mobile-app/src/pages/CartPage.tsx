import React from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Button,
  Divider,
  Chip,
  Alert,
  CircularProgress,
  Grid,
} from '@mui/material';
import { Add, Remove, Delete } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';

const CartPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { items, removeItem, updateQuantity, getTotal, clearCart } = useCart();

  const handleCheckout = () => {
    if (!user) {
      navigate('/login');
      return;
    }
    navigate('/checkout');
  };

  const handleQuantityChange = (itemId: string, newQuantity: number) => {
    updateQuantity(itemId, newQuantity);
  };

  const handleRemoveItem = (itemId: string) => {
    removeItem(itemId);
  };

  if (items.length === 0) {
    return (
      <Container maxWidth="md" sx={{ py: 8 }}>
        <Box textAlign="center">
          <Typography variant="h5" gutterBottom>
            Your cart is empty
          </Typography>
          <Typography variant="body1" color="text.secondary" mb={4}>
            Add some delicious food to get started!
          </Typography>
          <Button
            variant="contained"
            size="large"
            onClick={() => navigate('/restaurants')}
          >
            Browse Restaurants
          </Button>
        </Box>
      </Container>
    );
  }

  const subtotal = getTotal();
  const deliveryFee = 50; // Fixed delivery fee
  const tax = subtotal * 0.18; // 18% GST
  const total = subtotal + deliveryFee + tax;

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
        Your Cart
      </Typography>

      <Grid container spacing={3}>
        {/* Cart Items */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Order from {items[0]?.restaurantName}
              </Typography>
              
              <List>
                {items.map((item, index) => (
                  <React.Fragment key={item.menuItem.id}>
                    <ListItem>
                      <ListItemText
                        primary={item.menuItem.name}
                        secondary={
                          <Box>
                            <Typography variant="body2" color="text.secondary">
                              {item.menuItem.description}
                            </Typography>
                            {item.menuItem.isVegetarian && (
                              <Chip label="Veg" color="success" size="small" sx={{ mt: 1 }} />
                            )}
                          </Box>
                        }
                      />
                      <ListItemSecondaryAction>
                        <Box display="flex" alignItems="center" gap={1}>
                          <IconButton
                            size="small"
                            onClick={() => handleQuantityChange(item.menuItem.id, item.quantity - 1)}
                            disabled={item.quantity <= 1}
                          >
                            <Remove />
                          </IconButton>
                          <Typography variant="body1" sx={{ minWidth: 30, textAlign: 'center' }}>
                            {item.quantity}
                          </Typography>
                          <IconButton
                            size="small"
                            onClick={() => handleQuantityChange(item.menuItem.id, item.quantity + 1)}
                          >
                            <Add />
                          </IconButton>
                          <Typography variant="h6" color="primary" sx={{ minWidth: 80, textAlign: 'right' }}>
                            ₹{item.menuItem.price * item.quantity}
                          </Typography>
                          <IconButton
                            color="error"
                            onClick={() => handleRemoveItem(item.menuItem.id)}
                          >
                            <Delete />
                          </IconButton>
                        </Box>
                      </ListItemSecondaryAction>
                    </ListItem>
                    {index < items.length - 1 && <Divider />}
                  </React.Fragment>
                ))}
              </List>
            </CardContent>
          </Card>
        </Grid>

        {/* Order Summary */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Order Summary
              </Typography>
              
              <Box mb={3}>
                <Box display="flex" justifyContent="space-between" mb={1}>
                  <Typography variant="body2">Subtotal</Typography>
                  <Typography variant="body2">₹{subtotal.toFixed(2)}</Typography>
                </Box>
                <Box display="flex" justifyContent="space-between" mb={1}>
                  <Typography variant="body2">Delivery Fee</Typography>
                  <Typography variant="body2">₹{deliveryFee.toFixed(2)}</Typography>
                </Box>
                <Box display="flex" justifyContent="space-between" mb={2}>
                  <Typography variant="body2">Tax (18% GST)</Typography>
                  <Typography variant="body2">₹{tax.toFixed(2)}</Typography>
                </Box>
                <Divider />
                <Box display="flex" justifyContent="space-between" mt={2}>
                  <Typography variant="h6" fontWeight="bold">Total</Typography>
                  <Typography variant="h6" fontWeight="bold" color="primary">
                    ₹{total.toFixed(2)}
                  </Typography>
                </Box>
              </Box>

              <Button
                variant="contained"
                fullWidth
                size="large"
                onClick={handleCheckout}
                sx={{ mb: 2 }}
              >
                Proceed to Checkout
              </Button>

              <Button
                variant="outlined"
                fullWidth
                onClick={() => navigate('/restaurants')}
              >
                Add More Items
              </Button>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default CartPage; 