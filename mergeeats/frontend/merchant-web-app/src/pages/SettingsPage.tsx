import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  TextField,
  Button,
  Switch,
  FormControlLabel,
  Alert,
  CircularProgress,
  Divider,
  Chip,
} from '@mui/material';
import {
  Save,
  Restaurant,
  Schedule,
  LocationOn,
  Phone,
  Email,
} from '@mui/icons-material';
import { useRestaurant } from '../contexts/RestaurantContext';
import { useAuth } from '../contexts/AuthContext';

const SettingsPage: React.FC = () => {
  const { restaurant, loading: restaurantLoading, updateRestaurant } = useRestaurant();
  const { merchant } = useAuth();
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    cuisine: '',
    phone: '',
    email: '',
    address: {
      street: '',
      city: '',
      state: '',
      zipCode: '',
    },
    isOpen: true,
    openingHours: {
      monday: '09:00-22:00',
      tuesday: '09:00-22:00',
      wednesday: '09:00-22:00',
      thursday: '09:00-22:00',
      friday: '09:00-22:00',
      saturday: '09:00-22:00',
      sunday: '09:00-22:00',
    },
  });

  useEffect(() => {
    if (restaurant) {
      setFormData({
        name: restaurant.name || '',
        description: restaurant.description || '',
        cuisine: restaurant.cuisine || '',
        phone: restaurant.phone || '',
        email: restaurant.email || '',
        address: {
          street: restaurant.address?.street || '',
          city: restaurant.address?.city || '',
          state: restaurant.address?.state || '',
          zipCode: restaurant.address?.zipCode || '',
        },
        isOpen: restaurant.isOpen || true,
        openingHours: restaurant.openingHours || {
          monday: '09:00-22:00',
          tuesday: '09:00-22:00',
          wednesday: '09:00-22:00',
          thursday: '09:00-22:00',
          friday: '09:00-22:00',
          saturday: '09:00-22:00',
          sunday: '09:00-22:00',
        },
      });
    }
  }, [restaurant]);

  const handleChange = (field: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({ ...prev, [field]: e.target.value }));
  };

  const handleAddressChange = (field: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({
      ...prev,
      address: { ...prev.address, [field]: e.target.value }
    }));
  };

  const handleOpeningHoursChange = (day: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({
      ...prev,
      openingHours: { ...prev.openingHours, [day]: e.target.value }
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(false);

    try {
      await updateRestaurant(formData);
      setSuccess(true);
    } catch (error: any) {
      setError(error.message || 'Failed to update restaurant settings');
    } finally {
      setLoading(false);
    }
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
      <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
        Restaurant Settings
      </Typography>

      {success && (
        <Alert severity="success" sx={{ mb: 3 }}>
          Restaurant settings updated successfully!
        </Alert>
      )}

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit}>
        <Grid container spacing={3}>
          {/* Basic Information */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" gap={1} mb={3}>
                  <Restaurant color="primary" />
                  <Typography variant="h6">Basic Information</Typography>
                </Box>

                <TextField
                  fullWidth
                  label="Restaurant Name"
                  value={formData.name}
                  onChange={handleChange('name')}
                  margin="normal"
                  required
                />

                <TextField
                  fullWidth
                  label="Description"
                  value={formData.description}
                  onChange={handleChange('description')}
                  margin="normal"
                  multiline
                  rows={3}
                />

                <TextField
                  fullWidth
                  label="Cuisine Type"
                  value={formData.cuisine}
                  onChange={handleChange('cuisine')}
                  margin="normal"
                  required
                />

                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.isOpen}
                      onChange={(e) => setFormData(prev => ({ ...prev, isOpen: e.target.checked }))}
                    />
                  }
                  label="Restaurant is Open"
                  sx={{ mt: 2 }}
                />
              </CardContent>
            </Card>
          </Grid>

          {/* Contact Information */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" gap={1} mb={3}>
                  <Phone color="primary" />
                  <Typography variant="h6">Contact Information</Typography>
                </Box>

                <TextField
                  fullWidth
                  label="Phone Number"
                  value={formData.phone}
                  onChange={handleChange('phone')}
                  margin="normal"
                  required
                />

                <TextField
                  fullWidth
                  label="Email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange('email')}
                  margin="normal"
                  required
                />

                <Box display="flex" alignItems="center" gap={1} mt={3} mb={2}>
                  <LocationOn color="primary" />
                  <Typography variant="h6">Address</Typography>
                </Box>

                <TextField
                  fullWidth
                  label="Street Address"
                  value={formData.address.street}
                  onChange={handleAddressChange('street')}
                  margin="normal"
                  required
                />

                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="City"
                      value={formData.address.city}
                      onChange={handleAddressChange('city')}
                      margin="normal"
                      required
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="State"
                      value={formData.address.state}
                      onChange={handleAddressChange('state')}
                      margin="normal"
                      required
                    />
                  </Grid>
                </Grid>

                <TextField
                  fullWidth
                  label="ZIP Code"
                  value={formData.address.zipCode}
                  onChange={handleAddressChange('zipCode')}
                  margin="normal"
                  required
                />
              </CardContent>
            </Card>
          </Grid>

          {/* Opening Hours */}
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" gap={1} mb={3}>
                  <Schedule color="primary" />
                  <Typography variant="h6">Opening Hours</Typography>
                </Box>

                <Grid container spacing={2}>
                  {Object.entries(formData.openingHours).map(([day, hours]) => (
                    <Grid item xs={12} sm={6} md={3} key={day}>
                      <TextField
                        fullWidth
                        label={day.charAt(0).toUpperCase() + day.slice(1)}
                        value={hours}
                        onChange={handleOpeningHoursChange(day)}
                        margin="normal"
                        placeholder="09:00-22:00"
                      />
                    </Grid>
                  ))}
                </Grid>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        <Box display="flex" justifyContent="flex-end" mt={4}>
          <Button
            type="submit"
            variant="contained"
            size="large"
            startIcon={<Save />}
            disabled={loading}
          >
            {loading ? <CircularProgress size={24} /> : 'Save Changes'}
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default SettingsPage; 