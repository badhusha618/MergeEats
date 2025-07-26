import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Rating,
  Chip,
  TextField,
  InputAdornment,
  CircularProgress,
  Alert,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import { Search, Restaurant, Star } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { restaurantAPI } from '../services/api';

interface Restaurant {
  id: string;
  name: string;
  description: string;
  cuisine: string;
  rating: number;
  totalOrders: number;
  imageUrl?: string;
  isOpen: boolean;
  deliveryTime: string;
  minimumOrder: number;
}

const RestaurantListPage: React.FC = () => {
  const navigate = useNavigate();
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [cuisineFilter, setCuisineFilter] = useState('');
  const [sortBy, setSortBy] = useState('rating');

  useEffect(() => {
    fetchRestaurants();
  }, []);

  const fetchRestaurants = async () => {
    try {
      setLoading(true);
      setError(null);

      const params: any = {};
      if (searchTerm) params.search = searchTerm;
      if (cuisineFilter) params.cuisine = cuisineFilter;

      const response = await restaurantAPI.getAll(params);
      setRestaurants(response.data);
    } catch (error: any) {
      setError(error.response?.data?.message || 'Failed to fetch restaurants');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    fetchRestaurants();
  };

  const handleRestaurantClick = (restaurantId: string) => {
    navigate(`/restaurant/${restaurantId}`);
  };

  const getCuisineOptions = () => {
    const cuisines = [...new Set(restaurants.map(r => r.cuisine))];
    return cuisines.sort();
  };

  const getSortedRestaurants = () => {
    let sorted = [...restaurants];
    
    switch (sortBy) {
      case 'rating':
        sorted.sort((a, b) => b.rating - a.rating);
        break;
      case 'deliveryTime':
        sorted.sort((a, b) => parseInt(a.deliveryTime) - parseInt(b.deliveryTime));
        break;
      case 'minimumOrder':
        sorted.sort((a, b) => a.minimumOrder - b.minimumOrder);
        break;
      default:
        break;
    }
    
    return sorted;
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
        Restaurants
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Search and Filters */}
      <Box mb={4}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              placeholder="Search restaurants..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search />
                  </InputAdornment>
                ),
              }}
            />
          </Grid>
          <Grid item xs={12} md={3}>
            <FormControl fullWidth>
              <InputLabel>Cuisine</InputLabel>
              <Select
                value={cuisineFilter}
                label="Cuisine"
                onChange={(e) => setCuisineFilter(e.target.value)}
              >
                <MenuItem value="">All Cuisines</MenuItem>
                {getCuisineOptions().map((cuisine) => (
                  <MenuItem key={cuisine} value={cuisine}>
                    {cuisine}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} md={3}>
            <FormControl fullWidth>
              <InputLabel>Sort By</InputLabel>
              <Select
                value={sortBy}
                label="Sort By"
                onChange={(e) => setSortBy(e.target.value)}
              >
                <MenuItem value="rating">Rating</MenuItem>
                <MenuItem value="deliveryTime">Delivery Time</MenuItem>
                <MenuItem value="minimumOrder">Minimum Order</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </Box>

      {/* Restaurant Grid */}
      {getSortedRestaurants().length === 0 ? (
        <Box textAlign="center" py={4}>
          <Restaurant sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" color="text.secondary" gutterBottom>
            No restaurants found
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Try adjusting your search criteria
          </Typography>
        </Box>
      ) : (
        <Grid container spacing={3}>
          {getSortedRestaurants().map((restaurant) => (
            <Grid item xs={12} sm={6} md={4} key={restaurant.id}>
              <Card 
                sx={{ 
                  cursor: 'pointer',
                  transition: 'transform 0.2s',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                  }
                }}
                onClick={() => handleRestaurantClick(restaurant.id)}
              >
                {restaurant.imageUrl && (
                  <CardMedia
                    component="img"
                    height="200"
                    image={restaurant.imageUrl}
                    alt={restaurant.name}
                  />
                )}
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={1}>
                    <Typography variant="h6" component="h2" fontWeight="bold">
                      {restaurant.name}
                    </Typography>
                    <Chip
                      label={restaurant.isOpen ? 'Open' : 'Closed'}
                      color={restaurant.isOpen ? 'success' : 'error'}
                      size="small"
                    />
                  </Box>
                  
                  <Typography variant="body2" color="text.secondary" mb={2}>
                    {restaurant.description}
                  </Typography>
                  
                  <Box display="flex" alignItems="center" gap={1} mb={1}>
                    <Rating value={restaurant.rating} readOnly size="small" />
                    <Typography variant="body2" color="text.secondary">
                      ({restaurant.rating})
                    </Typography>
                  </Box>
                  
                  <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Chip label={restaurant.cuisine} size="small" variant="outlined" />
                    <Typography variant="body2" color="text.secondary">
                      {restaurant.deliveryTime} min
                    </Typography>
                  </Box>
                  
                  <Typography variant="body2" color="text.secondary" mt={1}>
                    Min. order: ₹{restaurant.minimumOrder}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Container>
  );
};

export default RestaurantListPage; 