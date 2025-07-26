import React, { useState } from 'react';
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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Grid,
  Alert,
  CircularProgress,
  Chip,
} from '@mui/material';
import { Edit, Delete, AddLocationAlt } from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

interface Address {
  id: string;
  street: string;
  city: string;
  state: string;
  zipCode: string;
  isDefault: boolean;
}

const AddressPage: React.FC = () => {
  const { user } = useAuth();
  const [addresses, setAddresses] = useState<Address[]>(user?.addresses || []);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingAddress, setEditingAddress] = useState<Address | null>(null);
  const [formData, setFormData] = useState({
    street: '',
    city: '',
    state: '',
    zipCode: '',
    isDefault: false,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleOpenDialog = (address?: Address) => {
    if (address) {
      setEditingAddress(address);
      setFormData({
        street: address.street,
        city: address.city,
        state: address.state,
        zipCode: address.zipCode,
        isDefault: address.isDefault,
      });
    } else {
      setEditingAddress(null);
      setFormData({ street: '', city: '', state: '', zipCode: '', isDefault: false });
    }
    setDialogOpen(true);
    setError(null);
    setSuccess(false);
  };

  const handleCloseDialog = () => {
    setDialogOpen(false);
    setEditingAddress(null);
    setError(null);
    setSuccess(false);
  };

  const handleChange = (field: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({ ...prev, [field]: e.target.value }));
  };

  const handleSave = () => {
    setLoading(true);
    setTimeout(() => {
      if (editingAddress) {
        setAddresses(prev => prev.map(addr => addr.id === editingAddress.id ? { ...editingAddress, ...formData } : addr));
      } else {
        setAddresses(prev => [
          ...prev,
          { ...formData, id: Math.random().toString(36).substr(2, 9), isDefault: addresses.length === 0 },
        ]);
      }
      setLoading(false);
      setSuccess(true);
      setDialogOpen(false);
    }, 1000);
  };

  const handleDelete = (id: string) => {
    setAddresses(prev => prev.filter(addr => addr.id !== id));
  };

  return (
    <Container maxWidth="sm" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
        My Addresses
      </Typography>
      <Card>
        <CardContent>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <Typography variant="h6">Delivery Addresses</Typography>
            <Button
              variant="contained"
              startIcon={<AddLocationAlt />}
              onClick={() => handleOpenDialog()}
            >
              Add Address
            </Button>
          </Box>
          <List>
            {addresses.map((address) => (
              <ListItem key={address.id} divider>
                <ListItemText
                  primary={
                    <Box display="flex" alignItems="center" gap={1}>
                      {address.isDefault && <Chip label="Default" color="primary" size="small" />}
                      <span>{address.street}, {address.city}, {address.state} {address.zipCode}</span>
                    </Box>
                  }
                />
                <ListItemSecondaryAction>
                  <IconButton onClick={() => handleOpenDialog(address)}>
                    <Edit />
                  </IconButton>
                  <IconButton color="error" onClick={() => handleDelete(address.id)}>
                    <Delete />
                  </IconButton>
                </ListItemSecondaryAction>
              </ListItem>
            ))}
          </List>
        </CardContent>
      </Card>

      <Dialog open={dialogOpen} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle>{editingAddress ? 'Edit Address' : 'Add Address'}</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Street Address"
                value={formData.street}
                onChange={handleChange('street')}
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="City"
                value={formData.city}
                onChange={handleChange('city')}
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="State"
                value={formData.state}
                onChange={handleChange('state')}
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="ZIP Code"
                value={formData.zipCode}
                onChange={handleChange('zipCode')}
                required
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button onClick={handleSave} variant="contained" disabled={loading}>
            {loading ? <CircularProgress size={20} /> : 'Save'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default AddressPage; 