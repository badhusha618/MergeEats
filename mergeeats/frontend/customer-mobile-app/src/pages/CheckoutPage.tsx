import React, { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  Grid,
  TextField,
  Button,
  Radio,
  RadioGroup,
  FormControlLabel,
  FormControl,
  FormLabel,
  Divider,
  Alert,
  Stepper,
  Step,
  StepLabel,
  CircularProgress,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';
import { useOrder } from '../contexts/OrderContext';

const steps = ['Delivery Address', 'Payment Method', 'Review Order'];

const CheckoutPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { items, getTotal, clearCart } = useCart();
  const { createOrder, loading } = useOrder();

  const [activeStep, setActiveStep] = useState(0);
  const [deliveryAddress, setDeliveryAddress] = useState({
    street: '',
    city: '',
    state: '',
    zipCode: '',
    instructions: '',
  });
  const [paymentMethod, setPaymentMethod] = useState('card');
  const [cardDetails, setCardDetails] = useState({
    number: '',
    expiry: '',
    cvv: '',
    name: '',
  });

  const subtotal = getTotal();
  const deliveryFee = 50;
  const tax = subtotal * 0.18;
  const total = subtotal + deliveryFee + tax;

  const handleNext = () => {
    setActiveStep((prevStep) => prevStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevStep) => prevStep - 1);
  };

  const handlePlaceOrder = async () => {
    try {
      const orderData = {
        restaurantId: items[0].restaurantId,
        items: items.map(item => ({
          menuItemId: item.menuItem.id,
          name: item.menuItem.name,
          price: item.menuItem.price,
          quantity: item.quantity,
        })),
        deliveryAddress: `${deliveryAddress.street}, ${deliveryAddress.city}, ${deliveryAddress.state} ${deliveryAddress.zipCode}`,
        paymentMethod,
      };

      const order = await createOrder(orderData);
      clearCart();
      navigate(`/orders/${order.id}/track`);
    } catch (error) {
      console.error('Error placing order:', error);
    }
  };

  const renderDeliveryAddress = () => (
    <Grid container spacing={3}>
      <Grid item xs={12}>
        <TextField
          fullWidth
          label="Street Address"
          value={deliveryAddress.street}
          onChange={(e) => setDeliveryAddress(prev => ({ ...prev, street: e.target.value }))}
          required
        />
      </Grid>
      <Grid item xs={12} sm={6}>
        <TextField
          fullWidth
          label="City"
          value={deliveryAddress.city}
          onChange={(e) => setDeliveryAddress(prev => ({ ...prev, city: e.target.value }))}
          required
        />
      </Grid>
      <Grid item xs={12} sm={6}>
        <TextField
          fullWidth
          label="State"
          value={deliveryAddress.state}
          onChange={(e) => setDeliveryAddress(prev => ({ ...prev, state: e.target.value }))}
          required
        />
      </Grid>
      <Grid item xs={12} sm={6}>
        <TextField
          fullWidth
          label="ZIP Code"
          value={deliveryAddress.zipCode}
          onChange={(e) => setDeliveryAddress(prev => ({ ...prev, zipCode: e.target.value }))}
          required
        />
      </Grid>
      <Grid item xs={12}>
        <TextField
          fullWidth
          label="Delivery Instructions (Optional)"
          multiline
          rows={3}
          value={deliveryAddress.instructions}
          onChange={(e) => setDeliveryAddress(prev => ({ ...prev, instructions: e.target.value }))}
        />
      </Grid>
    </Grid>
  );

  const renderPaymentMethod = () => (
    <Box>
      <FormControl component="fieldset">
        <FormLabel component="legend">Payment Method</FormLabel>
        <RadioGroup value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)}>
          <FormControlLabel value="card" control={<Radio />} label="Credit/Debit Card" />
          <FormControlLabel value="upi" control={<Radio />} label="UPI" />
          <FormControlLabel value="cod" control={<Radio />} label="Cash on Delivery" />
        </RadioGroup>
      </FormControl>

      {paymentMethod === 'card' && (
        <Box mt={3}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Card Number"
                value={cardDetails.number}
                onChange={(e) => setCardDetails(prev => ({ ...prev, number: e.target.value }))}
                placeholder="1234 5678 9012 3456"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Expiry Date"
                value={cardDetails.expiry}
                onChange={(e) => setCardDetails(prev => ({ ...prev, expiry: e.target.value }))}
                placeholder="MM/YY"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="CVV"
                value={cardDetails.cvv}
                onChange={(e) => setCardDetails(prev => ({ ...prev, cvv: e.target.value }))}
                placeholder="123"
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Cardholder Name"
                value={cardDetails.name}
                onChange={(e) => setCardDetails(prev => ({ ...prev, name: e.target.value }))}
              />
            </Grid>
          </Grid>
        </Box>
      )}
    </Box>
  );

  const renderReviewOrder = () => (
    <Box>
      <Typography variant="h6" gutterBottom>
        Order Summary
      </Typography>
      
      <Card variant="outlined" sx={{ mb: 3 }}>
        <CardContent>
          {items.map((item) => (
            <Box key={item.menuItem.id} display="flex" justifyContent="space-between" mb={1}>
              <Typography>
                {item.menuItem.name} x {item.quantity}
              </Typography>
              <Typography>₹{item.menuItem.price * item.quantity}</Typography>
            </Box>
          ))}
          <Divider sx={{ my: 2 }} />
          <Box display="flex" justifyContent="space-between" mb={1}>
            <Typography>Subtotal</Typography>
            <Typography>₹{subtotal.toFixed(2)}</Typography>
          </Box>
          <Box display="flex" justifyContent="space-between" mb={1}>
            <Typography>Delivery Fee</Typography>
            <Typography>₹{deliveryFee.toFixed(2)}</Typography>
          </Box>
          <Box display="flex" justifyContent="space-between" mb={2}>
            <Typography>Tax (18% GST)</Typography>
            <Typography>₹{tax.toFixed(2)}</Typography>
          </Box>
          <Divider />
          <Box display="flex" justifyContent="space-between" mt={2}>
            <Typography variant="h6" fontWeight="bold">Total</Typography>
            <Typography variant="h6" fontWeight="bold" color="primary">
              ₹{total.toFixed(2)}
            </Typography>
          </Box>
        </CardContent>
      </Card>

      <Typography variant="h6" gutterBottom>
        Delivery Address
      </Typography>
      <Typography variant="body2" color="text.secondary" mb={3}>
        {deliveryAddress.street}, {deliveryAddress.city}, {deliveryAddress.state} {deliveryAddress.zipCode}
      </Typography>

      <Typography variant="h6" gutterBottom>
        Payment Method
      </Typography>
      <Typography variant="body2" color="text.secondary" mb={3}>
        {paymentMethod === 'card' ? 'Credit/Debit Card' : 
         paymentMethod === 'upi' ? 'UPI' : 'Cash on Delivery'}
      </Typography>
    </Box>
  );

  const getStepContent = (step: number) => {
    switch (step) {
      case 0:
        return renderDeliveryAddress();
      case 1:
        return renderPaymentMethod();
      case 2:
        return renderReviewOrder();
      default:
        return 'Unknown step';
    }
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
        Checkout
      </Typography>

      <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
        {steps.map((label) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>

      <Card>
        <CardContent>
          {getStepContent(activeStep)}
        </CardContent>
      </Card>

      <Box display="flex" justifyContent="space-between" mt={4}>
        <Button
          disabled={activeStep === 0}
          onClick={handleBack}
        >
          Back
        </Button>
        
        <Box>
          {activeStep === steps.length - 1 ? (
            <Button
              variant="contained"
              onClick={handlePlaceOrder}
              disabled={loading}
            >
              {loading ? <CircularProgress size={24} /> : 'Place Order'}
            </Button>
          ) : (
            <Button
              variant="contained"
              onClick={handleNext}
            >
              Next
            </Button>
          )}
        </Box>
      </Box>
    </Container>
  );
};

export default CheckoutPage; 