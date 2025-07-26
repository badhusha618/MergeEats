import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  TrendingUp,
  TrendingDown,
  Restaurant,
  Receipt,
  People,
  Star,
} from '@mui/icons-material';
import { useOrder } from '../contexts/OrderContext';
import { useRestaurant } from '../contexts/RestaurantContext';

const AnalyticsPage: React.FC = () => {
  const { orders, loading: ordersLoading } = useOrder();
  const { restaurant, loading: restaurantLoading } = useRestaurant();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Calculate metrics
  const totalOrders = orders.length;
  const completedOrders = orders.filter(order => order.status === 'DELIVERED').length;
  const cancelledOrders = orders.filter(order => order.status === 'CANCELLED').length;
  const totalRevenue = orders
    .filter(order => order.status === 'DELIVERED')
    .reduce((sum, order) => sum + order.totalAmount, 0);
  const averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;
  const completionRate = totalOrders > 0 ? (completedOrders / totalOrders) * 100 : 0;

  // Get recent orders for table
  const recentOrders = orders
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 10);

  // Calculate daily revenue for the last 7 days
  const getDailyRevenue = () => {
    const dailyData = [];
    const today = new Date();
    
    for (let i = 6; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(date.getDate() - i);
      const dayOrders = orders.filter(order => {
        const orderDate = new Date(order.createdAt);
        return orderDate.toDateString() === date.toDateString() && order.status === 'DELIVERED';
      });
      const dayRevenue = dayOrders.reduce((sum, order) => sum + order.totalAmount, 0);
      dailyData.push({
        date: date.toLocaleDateString(),
        revenue: dayRevenue,
        orders: dayOrders.length
      });
    }
    return dailyData;
  };

  const dailyRevenue = getDailyRevenue();

  if (ordersLoading || restaurantLoading || loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
        Analytics Dashboard
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Key Metrics */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography variant="h4" fontWeight="bold" color="primary">
                    {totalOrders}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Total Orders
                  </Typography>
                </Box>
                <Receipt color="primary" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography variant="h4" fontWeight="bold" color="success.main">
                    ₹{totalRevenue.toFixed(2)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Total Revenue
                  </Typography>
                </Box>
                <TrendingUp color="success" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography variant="h4" fontWeight="bold" color="info.main">
                    ₹{averageOrderValue.toFixed(2)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Avg Order Value
                  </Typography>
                </Box>
                <Restaurant color="info" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography variant="h4" fontWeight="bold" color="warning.main">
                    {completionRate.toFixed(1)}%
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Completion Rate
                  </Typography>
                </Box>
                <Star color="warning" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Daily Revenue Chart */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Daily Revenue (Last 7 Days)
              </Typography>
              <Box>
                {dailyRevenue.map((day, index) => (
                  <Box key={index} display="flex" justifyContent="space-between" alignItems="center" py={1}>
                    <Typography variant="body2">{day.date}</Typography>
                    <Box display="flex" alignItems="center" gap={2}>
                      <Typography variant="body2" color="text.secondary">
                        {day.orders} orders
                      </Typography>
                      <Typography variant="body2" fontWeight="bold">
                        ₹{day.revenue.toFixed(2)}
                      </Typography>
                    </Box>
                  </Box>
                ))}
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Order Status Distribution
              </Typography>
              <Box>
                <Box display="flex" justifyContent="space-between" alignItems="center" py={1}>
                  <Typography variant="body2">Completed</Typography>
                  <Chip label={completedOrders} color="success" size="small" />
                </Box>
                <Box display="flex" justifyContent="space-between" alignItems="center" py={1}>
                  <Typography variant="body2">Cancelled</Typography>
                  <Chip label={cancelledOrders} color="error" size="small" />
                </Box>
                <Box display="flex" justifyContent="space-between" alignItems="center" py={1}>
                  <Typography variant="body2">Pending</Typography>
                  <Chip label={totalOrders - completedOrders - cancelledOrders} color="warning" size="small" />
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Recent Orders Table */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Recent Orders
          </Typography>
          <TableContainer component={Paper} variant="outlined">
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Order ID</TableCell>
                  <TableCell>Customer</TableCell>
                  <TableCell>Amount</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Date</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {recentOrders.map((order) => (
                  <TableRow key={order.id}>
                    <TableCell>#{order.id.slice(-8)}</TableCell>
                    <TableCell>{order.customerName}</TableCell>
                    <TableCell>₹{order.totalAmount}</TableCell>
                    <TableCell>
                      <Chip
                        label={order.status.replace('_', ' ')}
                        color={
                          order.status === 'DELIVERED' ? 'success' :
                          order.status === 'CANCELLED' ? 'error' :
                          order.status === 'PENDING' ? 'warning' : 'primary'
                        }
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      {new Date(order.createdAt).toLocaleDateString()}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>
    </Container>
  );
};

export default AnalyticsPage; 