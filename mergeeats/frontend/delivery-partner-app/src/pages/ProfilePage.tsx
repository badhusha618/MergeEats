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
  Avatar,
  Divider,
} from '@mui/material';
import {
  Save,
  Person,
  Phone,
  Email,
  DirectionsCar,
  LocationOn,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const ProfilePage: React.FC = () => {
  const { deliveryPartner, loading: authLoading } = useAuth();
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    vehicleType: '',
    vehicleNumber: '',
    isAvailable: true,
  });

  useEffect(() => {
    if (deliveryPartner) {
      setFormData({
        name: deliveryPartner.name || '',
        email: deliveryPartner.email || '',
        phone: deliveryPartner.phone || '',
        vehicleType: deliveryPartner.vehicleType || '',
        vehicleNumber: deliveryPartner.vehicleNumber || '',
        isAvailable: deliveryPartner.isAvailable || true,
      });
    }
  }, [deliveryPartner]);

  const handleChange = (field: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({ ...prev, [field]: e.target.value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(false);

    try {
      // TODO: Implement profile update API call
      setTimeout(() => {
        setLoading(false);
        setSuccess(true);
      }, 1000);
    } catch (error: any) {
      setError('Failed to update profile');
      setLoading(false);
    }
  };

  if (authLoading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
        Profile Settings
      </Typography>

      {success && (
        <Alert severity="success" sx={{ mb: 3 }}>
          Profile updated successfully!
        </Alert>
      )}

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit}>
        <Grid container spacing={3}>
          {/* Profile Information */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" gap={1} mb={3}>
                  <Person color="primary" />
                  <Typography variant="h6">Personal Information</Typography>
                </Box>

                <Box display="flex" flexDirection="column" alignItems="center" mb={3}>
                  <Avatar sx={{ width: 80, height: 80, mb: 2 }}>
                    {formData.name.charAt(0).toUpperCase()}
                  </Avatar>
                  <Typography variant="h6">{formData.name}</Typography>
                  <Typography variant="body2" color="text.secondary">{formData.email}</Typography>
                </Box>

                <TextField
                  fullWidth
                  label="Full Name"
                  value={formData.name}
                  onChange={handleChange('name')}
                  margin="normal"
                  required
                />

                <TextField
                  fullWidth
                  label="Email"
                  type="email"
                  value={formData.email}
                  disabled
                  margin="normal"
                  helperText="Email cannot be changed"
                />

                <TextField
                  fullWidth
                  label="Phone Number"
                  value={formData.phone}
                  onChange={handleChange('phone')}
                  margin="normal"
                  required
                />
              </CardContent>
            </Card>
          </Grid>

          {/* Vehicle Information */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" gap={1} mb={3}>
                  <DirectionsCar color="primary" />
                  <Typography variant="h6">Vehicle Information</Typography>
                </Box>

                <TextField
                  fullWidth
                  label="Vehicle Type"
                  value={formData.vehicleType}
                  onChange={handleChange('vehicleType')}
                  margin="normal"
                  required
                  select
                  SelectProps={{
                    native: true,
                  }}
                >
                  <option value="">Select Vehicle Type</option>
                  <option value="bike">Motorcycle</option>
                  <option value="scooter">Scooter</option>
                  <option value="car">Car</option>
                  <option value="bicycle">Bicycle</option>
                </TextField>

                <TextField
                  fullWidth
                  label="Vehicle Number"
                  value={formData.vehicleNumber}
                  onChange={handleChange('vehicleNumber')}
                  margin="normal"
                  required
                  placeholder="e.g., KA-01-AB-1234"
                />

                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.isAvailable}
                      onChange={(e) => setFormData(prev => ({ ...prev, isAvailable: e.target.checked }))}
                    />
                  }
                  label="Available for Deliveries"
                  sx={{ mt: 2 }}
                />
              </CardContent>
            </Card>
          </Grid>

          {/* Performance Summary */}
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Performance Summary
                </Typography>
                <Grid container spacing={3}>
                  <Grid item xs={12} sm={6} md={3}>
                    <Box textAlign="center">
                      <Typography variant="h4" color="primary" fontWeight="bold">
                        150
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Total Deliveries
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={12} sm={6} md={3}>
                    <Box textAlign="center">
                      <Typography variant="h4" color="success.main" fontWeight="bold">
                        4.8
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Average Rating
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={12} sm={6} md={3}>
                    <Box textAlign="center">
                      <Typography variant="h4" color="info.main" fontWeight="bold">
                        ₹2,450
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        This Week's Earnings
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={12} sm={6} md={3}>
                    <Box textAlign="center">
                      <Typography variant="h4" color="warning.main" fontWeight="bold">
                        98%
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        On-Time Delivery
                      </Typography>
                    </Box>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          {/* Preferences */}
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Preferences
                </Typography>
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6}>
                    <FormControlLabel
                      control={<Switch defaultChecked />}
                      label="Push Notifications"
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <FormControlLabel
                      control={<Switch defaultChecked />}
                      label="SMS Notifications"
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <FormControlLabel
                      control={<Switch />}
                      label="Auto-Accept Orders"
                    />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <FormControlLabel
                      control={<Switch defaultChecked />}
                      label="Location Sharing"
                    />
                  </Grid>
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

export default ProfilePage; 