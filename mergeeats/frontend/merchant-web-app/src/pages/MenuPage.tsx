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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControlLabel,
  Switch,
  IconButton,
  Chip,
  Alert,
  CircularProgress,
  Fab,
} from '@mui/material';
import {
  Add,
  Edit,
  Delete,
  Visibility,
  VisibilityOff,
} from '@mui/icons-material';
import { useRestaurant } from '../contexts/RestaurantContext';

interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
  category: string;
  isVegetarian: boolean;
  isAvailable: boolean;
  imageUrl?: string;
}

const MenuPage: React.FC = () => {
  const { menuItems, loading, error, addMenuItem, updateMenuItem, deleteMenuItem, fetchRestaurant } = useRestaurant();
  const [openDialog, setOpenDialog] = useState(false);
  const [editingItem, setEditingItem] = useState<MenuItem | null>(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    category: '',
    isVegetarian: false,
    isAvailable: true,
    imageUrl: '',
  });

  useEffect(() => {
    fetchRestaurant();
  }, [fetchRestaurant]);

  const handleOpenDialog = (item?: MenuItem) => {
    if (item) {
      setEditingItem(item);
      setFormData({
        name: item.name,
        description: item.description,
        price: item.price.toString(),
        category: item.category,
        isVegetarian: item.isVegetarian,
        isAvailable: item.isAvailable,
        imageUrl: item.imageUrl || '',
      });
    } else {
      setEditingItem(null);
      setFormData({
        name: '',
        description: '',
        price: '',
        category: '',
        isVegetarian: false,
        isAvailable: true,
        imageUrl: '',
      });
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditingItem(null);
  };

  const handleSubmit = async () => {
    try {
      const itemData = {
        name: formData.name,
        description: formData.description,
        price: parseFloat(formData.price),
        category: formData.category,
        isVegetarian: formData.isVegetarian,
        isAvailable: formData.isAvailable,
        imageUrl: formData.imageUrl || undefined,
      };

      if (editingItem) {
        await updateMenuItem(editingItem.id, itemData);
      } else {
        await addMenuItem(itemData);
      }
      handleCloseDialog();
    } catch (error) {
      console.error('Failed to save menu item:', error);
    }
  };

  const handleDelete = async (itemId: string) => {
    if (window.confirm('Are you sure you want to delete this item?')) {
      try {
        await deleteMenuItem(itemId);
      } catch (error) {
        console.error('Failed to delete menu item:', error);
      }
    }
  };

  const handleToggleAvailability = async (item: MenuItem) => {
    try {
      await updateMenuItem(item.id, { isAvailable: !item.isAvailable });
    } catch (error) {
      console.error('Failed to update item availability:', error);
    }
  };

  const categories = [...new Set(menuItems.map(item => item.category))];

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Typography variant="h4" component="h1" fontWeight="bold">
          Menu Management
        </Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => handleOpenDialog()}
        >
          Add Menu Item
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Categories */}
      {categories.map((category) => (
        <Box key={category} mb={4}>
          <Typography variant="h5" gutterBottom fontWeight="bold" color="primary">
            {category}
          </Typography>
          <Grid container spacing={3}>
            {menuItems
              .filter(item => item.category === category)
              .map((item) => (
                <Grid item xs={12} sm={6} md={4} key={item.id}>
                  <Card>
                    {item.imageUrl && (
                      <CardMedia
                        component="img"
                        height="200"
                        image={item.imageUrl}
                        alt={item.name}
                      />
                    )}
                    <CardContent>
                      <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={1}>
                        <Typography variant="h6" component="h2" fontWeight="bold">
                          {item.name}
                        </Typography>
                        <Box display="flex" gap={1}>
                          <IconButton
                            size="small"
                            onClick={() => handleToggleAvailability(item)}
                          >
                            {item.isAvailable ? <Visibility /> : <VisibilityOff />}
                          </IconButton>
                          <IconButton
                            size="small"
                            onClick={() => handleOpenDialog(item)}
                          >
                            <Edit />
                          </IconButton>
                          <IconButton
                            size="small"
                            color="error"
                            onClick={() => handleDelete(item.id)}
                          >
                            <Delete />
                          </IconButton>
                        </Box>
                      </Box>
                      
                      <Typography variant="body2" color="text.secondary" mb={2}>
                        {item.description}
                      </Typography>
                      
                      <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Typography variant="h6" color="primary" fontWeight="bold">
                          ₹{item.price}
                        </Typography>
                        <Box display="flex" gap={1}>
                          {item.isVegetarian && (
                            <Chip label="Veg" color="success" size="small" />
                          )}
                          <Chip
                            label={item.isAvailable ? 'Available' : 'Unavailable'}
                            color={item.isAvailable ? 'success' : 'error'}
                            size="small"
                          />
                        </Box>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
          </Grid>
        </Box>
      ))}

      {/* Add Item Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle>
          {editingItem ? 'Edit Menu Item' : 'Add Menu Item'}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 1 }}>
            <TextField
              fullWidth
              label="Item Name"
              value={formData.name}
              onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              label="Description"
              value={formData.description}
              onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
              margin="normal"
              multiline
              rows={3}
            />
            <TextField
              fullWidth
              label="Price"
              type="number"
              value={formData.price}
              onChange={(e) => setFormData(prev => ({ ...prev, price: e.target.value }))}
              margin="normal"
              required
              InputProps={{
                startAdornment: '₹',
              }}
            />
            <TextField
              fullWidth
              label="Category"
              value={formData.category}
              onChange={(e) => setFormData(prev => ({ ...prev, category: e.target.value }))}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              label="Image URL (optional)"
              value={formData.imageUrl}
              onChange={(e) => setFormData(prev => ({ ...prev, imageUrl: e.target.value }))}
              margin="normal"
            />
            <Box sx={{ mt: 2 }}>
              <FormControlLabel
                control={
                  <Switch
                    checked={formData.isVegetarian}
                    onChange={(e) => setFormData(prev => ({ ...prev, isVegetarian: e.target.checked }))}
                  />
                }
                label="Vegetarian"
              />
              <FormControlLabel
                control={
                  <Switch
                    checked={formData.isAvailable}
                    onChange={(e) => setFormData(prev => ({ ...prev, isAvailable: e.target.checked }))}
                  />
                }
                label="Available"
              />
            </Box>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained">
            {editingItem ? 'Update' : 'Add'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default MenuPage; 