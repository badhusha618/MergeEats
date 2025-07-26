import React, { useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  Grid,
  Chip,
  Button,
  CircularProgress,
  Alert,
  List,
  ListItem,
  ListItemText,
  Divider,
} from '@mui/material';
import { Receipt, Restaurant, Schedule } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useOrder } from '../contexts/OrderContext';

const OrderHistoryPage: React.FC = () => {
  const navigate = useNavigate();
  const { orders, getOrders, loading } = useOrder();

  useEffect(() => {
    getOrders();
  }, [getOrders]);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'DELIVERED': return 'success';
      case 'CANCELLED': return 'error';
      case 'OUT_FOR_DELIVERY': return 'secondary';
      default: return 'warning';
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
        Order History
      </Typography>

      {orders.length === 0 ? (
        <Box textAlign="center" py={8}>
          <Typography variant="h6" color="text.secondary" gutterBottom>
            No orders yet
          </Typography>
          <Typography variant="body1" color="text.secondary" mb={4}>
            Start ordering delicious food from our restaurants!
          </Typography>
          <Button
            variant="contained"
            size="large"
            onClick={() => navigate('/restaurants')}
          >
            Browse Restaurants
          </Button>
        </Box>
      ) : (
        <List>
          {orders.map((order, index) => (
            <React.Fragment key={order.id}>
              <ListItem
                component={Card}
                sx={{ mb: 2, cursor: 'pointer' }}
                onClick={() => navigate(`/orders/${order.id}/track`)}
              >
                <CardContent sx={{ width: '100%' }}>
                  <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                    <Box>
                      <Typography variant="h6" fontWeight="bold">
                        Order #{order.id.slice(-8)}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {formatDate(order.createdAt)}
                      </Typography>
                    </Box>
                    <Chip
                      label={order.status.replace('_', ' ')}
                      color={getStatusColor(order.status) as any}
                      variant="filled"
                    />
                  </Box>

                  <Box display="flex" alignItems="center" mb={2}>
                    <Restaurant sx={{ mr: 1, color: 'text.secondary' }} />
                    <Typography variant="body1">
                      {order.restaurantName}
                    </Typography>
                  </Box>

                  <Box mb={2}>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      Items:
                    </Typography>
                    {order.items.map((item) => (
                      <Typography key={item.menuItemId} variant="body2">
                        • {item.name} x {item.quantity}
                      </Typography>
                    ))}
                  </Box>

                  <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Typography variant="h6" color="primary" fontWeight="bold">
                      ₹{order.totalAmount}
                    </Typography>
                    <Button
                      variant="outlined"
                      size="small"
                      onClick={(e) => {
                        e.stopPropagation();
                        navigate(`/orders/${order.id}/track`);
                      }}
                    >
                      Track Order
                    </Button>
                  </Box>
                </CardContent>
              </ListItem>
              {index < orders.length - 1 && <Divider />}
            </React.Fragment>
          ))}
        </List>
      )}
    </Container>
  );
};

export default OrderHistoryPage; 