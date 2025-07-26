import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  TextField,
  InputAdornment,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Button,
  Chip,
  Rating,
  CircularProgress,
  Alert,
} from '@mui/material';
import { Search, LocationOn, Star } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

interface Restaurant {
  id: string;
  name: string;
  description: string;
  cuisine: string;
  rating: number;
  totalReviews: number;
  imageUrl?: string;
  isOpen: boolean;
  address: {
    city: string;
    state: string;
  };
}

const HomePage: React.FC = () => {
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [location, setLocation] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchRestaurants();
  }, []);

  const fetchRestaurants = async () => {
    try {
      setLoading(true);
      const response = await axios.get('http://localhost:8080/api/restaurants', {
        params: {
          page: 0,
          size: 12,
        },
      });
      setRestaurants(response.data);
    } catch (error: any) {
      setError('Failed to load restaurants');
      console.error('Error fetching restaurants:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    navigate('/restaurants', {
      state: { searchQuery, location }
    });
  };

  const handleRestaurantClick = (restaurantId: string) => {
    navigate(`/restaurants/${restaurantId}`);
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
      {/* Hero Section */}
      <Box textAlign="center" mb={6}>
        <Typography variant="h3" component="h1" gutterBottom fontWeight="bold" color="primary">
          Delicious Food Delivered
        </Typography>
        <Typography variant="h6" color="text.secondary" mb={4}>
          Order from your favorite restaurants and get it delivered to your doorstep
        </Typography>
      </Box>

      {/* Search Section */}
      <Box mb={6}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              placeholder="Search restaurants or cuisines..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search />
                  </InputAdornment>
                ),
              }}
            />
          </Grid>
          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              placeholder="Enter your location"
              value={location}
              onChange={(e) => setLocation(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LocationOn />
                  </InputAdornment>
                ),
              }}
            />
          </Grid>
          <Grid item xs={12} md={4}>
            <Button
              fullWidth
              variant="contained"
              size="large"
              onClick={handleSearch}
              sx={{ height: 56 }}
            >
              Search
            </Button>
          </Grid>
        </Grid>
      </Box>

      {/* Error Alert */}
      {error && (
        <Alert severity="error" sx={{ mb: 4 }}>
          {error}
        </Alert>
      )}

      {/* Featured Restaurants */}
      <Box mb={4}>
        <Typography variant="h4" component="h2" gutterBottom fontWeight="600">
          Featured Restaurants
        </Typography>
        <Typography variant="body1" color="text.secondary" mb={4}>
          Discover the best restaurants in your area
        </Typography>
      </Box>

      {/* Restaurant Grid */}
      <Grid container spacing={3}>
        {restaurants.map((restaurant) => (
          <Grid item xs={12} sm={6} md={4} key={restaurant.id}>
            <Card
              sx={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                cursor: 'pointer',
                transition: 'transform 0.2s, box-shadow 0.2s',
                '&:hover': {
                  transform: 'translateY(-4px)',
                  boxShadow: '0 8px 25px rgba(0,0,0,0.15)',
                },
              }}
              onClick={() => handleRestaurantClick(restaurant.id)}
            >
              <CardMedia
                component="img"
                height="200"
                image={restaurant.imageUrl || '/default-restaurant.jpg'}
                alt={restaurant.name}
                sx={{ objectFit: 'cover' }}
              />
              <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
                <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={1}>
                  <Typography variant="h6" component="h3" fontWeight="600" noWrap>
                    {restaurant.name}
                  </Typography>
                  <Chip
                    label={restaurant.isOpen ? 'Open' : 'Closed'}
                    color={restaurant.isOpen ? 'success' : 'error'}
                    size="small"
                  />
                </Box>
                
                <Typography variant="body2" color="text.secondary" mb={2} sx={{ flexGrow: 1 }}>
                  {restaurant.description}
                </Typography>

                <Box display="flex" alignItems="center" mb={1}>
                  <Rating value={restaurant.rating} readOnly size="small" />
                  <Typography variant="body2" color="text.secondary" ml={1}>
                    ({restaurant.totalReviews} reviews)
                  </Typography>
                </Box>

                <Box display="flex" justifyContent="space-between" alignItems="center">
                  <Chip label={restaurant.cuisine} size="small" variant="outlined" />
                  <Typography variant="body2" color="text.secondary">
                    {restaurant.address.city}, {restaurant.address.state}
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* View All Button */}
      <Box textAlign="center" mt={6}>
        <Button
          variant="outlined"
          size="large"
          onClick={() => navigate('/restaurants')}
          sx={{ px: 4, py: 1.5 }}
        >
          View All Restaurants
        </Button>
      </Box>
    </Container>
  );
};

export default HomePage; 