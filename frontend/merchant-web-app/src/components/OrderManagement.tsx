import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Grid,
  Avatar,
  Divider,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  Visibility,
  Edit,
  LocalShipping,
  Restaurant,
  AccessTime,
  AttachMoney,
} from '@mui/icons-material';

interface OrderItem {
  id: string;
  customerName: string;
  items: string;
  total: number;
  status: 'PENDING' | 'CONFIRMED' | 'PREPARING' | 'READY' | 'PICKED_UP' | 'DELIVERED' | 'CANCELLED';
  orderTime: string;
  estimatedDelivery: string;
  paymentStatus: 'PENDING' | 'COMPLETED' | 'FAILED';
  isGroupOrder?: boolean;
  isMerged?: boolean;
}

const OrderManagement: React.FC = () => {
  const [orders, setOrders] = useState<OrderItem[]>([
    {
      id: 'ORD-001',
      customerName: 'John Doe',
      items: 'Burger Deluxe, Fries, Coke',
      total: 24.99,
      status: 'PREPARING',
      orderTime: '10:30 AM',
      estimatedDelivery: '11:15 AM',
      paymentStatus: 'COMPLETED',
      isGroupOrder: false,
      isMerged: true,
    },
    {
      id: 'ORD-002',
      customerName: 'Jane Smith',
      items: 'Pizza Margherita, Garlic Bread',
      total: 32.50,
      status: 'CONFIRMED',
      orderTime: '10:25 AM',
      estimatedDelivery: '11:10 AM',
      paymentStatus: 'COMPLETED',
      isGroupOrder: true,
      isMerged: false,
    },
    {
      id: 'ORD-003',
      customerName: 'Mike Johnson',
      items: 'Caesar Salad, Grilled Chicken',
      total: 18.75,
      status: 'READY',
      orderTime: '10:15 AM',
      estimatedDelivery: '11:00 AM',
      paymentStatus: 'COMPLETED',
      isGroupOrder: false,
      isMerged: true,
    },
  ]);

  const [selectedOrder, setSelectedOrder] = useState<OrderItem | null>(null);
  const [detailsOpen, setDetailsOpen] = useState(false);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'warning';
      case 'CONFIRMED': return 'info';
      case 'PREPARING': return 'primary';
      case 'READY': return 'success';
      case 'PICKED_UP': return 'secondary';
      case 'DELIVERED': return 'default';
      case 'CANCELLED': return 'error';
      default: return 'default';
    }
  };

  const getPaymentStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'warning';
      case 'COMPLETED': return 'success';
      case 'FAILED': return 'error';
      default: return 'default';
    }
  };

  const handleStatusUpdate = (orderId: string, newStatus: string) => {
    setOrders(prevOrders =>
      prevOrders.map(order =>
        order.id === orderId ? { ...order, status: newStatus as any } : order
      )
    );
  };

  const handleViewDetails = (order: OrderItem) => {
    setSelectedOrder(order);
    setDetailsOpen(true);
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Order Management
      </Typography>

      {/* Order Statistics */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Restaurant sx={{ fontSize: 40, mr: 2, color: 'primary.main' }} />
                <Box>
                  <Typography variant="h5">
                    {orders.filter(o => o.status === 'PREPARING').length}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Preparing
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <AccessTime sx={{ fontSize: 40, mr: 2, color: 'success.main' }} />
                <Box>
                  <Typography variant="h5">
                    {orders.filter(o => o.status === 'READY').length}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Ready for Pickup
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <LocalShipping sx={{ fontSize: 40, mr: 2, color: 'info.main' }} />
                <Box>
                  <Typography variant="h5">
                    {orders.filter(o => o.status === 'PICKED_UP').length}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Out for Delivery
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <AttachMoney sx={{ fontSize: 40, mr: 2, color: 'warning.main' }} />
                <Box>
                  <Typography variant="h5">
                    ${orders.reduce((sum, order) => sum + order.total, 0).toFixed(2)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Total Revenue
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Orders Table */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Active Orders
          </Typography>
          <TableContainer component={Paper} variant="outlined">
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Order ID</TableCell>
                  <TableCell>Customer</TableCell>
                  <TableCell>Items</TableCell>
                  <TableCell>Total</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Payment</TableCell>
                  <TableCell>Order Time</TableCell>
                  <TableCell>Est. Delivery</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {orders.map((order) => (
                  <TableRow key={order.id} hover>
                    <TableCell>
                      <Typography variant="subtitle2" fontWeight="bold">
                        {order.id}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Avatar sx={{ mr: 1, width: 32, height: 32 }}>
                          {order.customerName[0]}
                        </Avatar>
                        {order.customerName}
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" sx={{ maxWidth: 200 }}>
                        {order.items}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="subtitle2" fontWeight="bold">
                        ${order.total.toFixed(2)}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <FormControl size="small" sx={{ minWidth: 120 }}>
                        <Select
                          value={order.status}
                          onChange={(e) => handleStatusUpdate(order.id, e.target.value)}
                        >
                          <MenuItem value="PENDING">Pending</MenuItem>
                          <MenuItem value="CONFIRMED">Confirmed</MenuItem>
                          <MenuItem value="PREPARING">Preparing</MenuItem>
                          <MenuItem value="READY">Ready</MenuItem>
                          <MenuItem value="PICKED_UP">Picked Up</MenuItem>
                          <MenuItem value="DELIVERED">Delivered</MenuItem>
                        </Select>
                      </FormControl>
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={order.paymentStatus}
                        size="small"
                        color={getPaymentStatusColor(order.paymentStatus) as any}
                      />
                    </TableCell>
                    <TableCell>{order.orderTime}</TableCell>
                    <TableCell>{order.estimatedDelivery}</TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', gap: 0.5 }}>
                        {order.isGroupOrder && (
                          <Tooltip title="Group Order">
                            <Chip label="Group" size="small" color="secondary" />
                          </Tooltip>
                        )}
                        {order.isMerged && (
                          <Tooltip title="AI Merged Order">
                            <Chip label="Merged" size="small" color="primary" />
                          </Tooltip>
                        )}
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', gap: 1 }}>
                        <Tooltip title="View Details">
                          <IconButton
                            size="small"
                            onClick={() => handleViewDetails(order)}
                          >
                            <Visibility />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Edit Order">
                          <IconButton size="small">
                            <Edit />
                          </IconButton>
                        </Tooltip>
                      </Box>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      {/* Order Details Dialog */}
      <Dialog open={detailsOpen} onClose={() => setDetailsOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>
          Order Details - {selectedOrder?.id}
        </DialogTitle>
        <DialogContent>
          {selectedOrder && (
            <Box>
              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <Typography variant="h6" gutterBottom>Customer Information</Typography>
                  <Typography><strong>Name:</strong> {selectedOrder.customerName}</Typography>
                  <Typography><strong>Order Time:</strong> {selectedOrder.orderTime}</Typography>
                  <Typography><strong>Estimated Delivery:</strong> {selectedOrder.estimatedDelivery}</Typography>
                </Grid>
                <Grid item xs={12} md={6}>
                  <Typography variant="h6" gutterBottom>Order Information</Typography>
                  <Typography><strong>Status:</strong> 
                    <Chip 
                      label={selectedOrder.status} 
                      size="small" 
                      color={getStatusColor(selectedOrder.status) as any}
                      sx={{ ml: 1 }}
                    />
                  </Typography>
                  <Typography><strong>Payment:</strong> 
                    <Chip 
                      label={selectedOrder.paymentStatus} 
                      size="small" 
                      color={getPaymentStatusColor(selectedOrder.paymentStatus) as any}
                      sx={{ ml: 1 }}
                    />
                  </Typography>
                  <Typography><strong>Total:</strong> ${selectedOrder.total.toFixed(2)}</Typography>
                </Grid>
              </Grid>
              <Divider sx={{ my: 2 }} />
              <Typography variant="h6" gutterBottom>Order Items</Typography>
              <Typography>{selectedOrder.items}</Typography>
              
              {(selectedOrder.isGroupOrder || selectedOrder.isMerged) && (
                <>
                  <Divider sx={{ my: 2 }} />
                  <Typography variant="h6" gutterBottom>Special Features</Typography>
                  {selectedOrder.isGroupOrder && (
                    <Chip label="Group Order - Multiple customers" color="secondary" sx={{ mr: 1 }} />
                  )}
                  {selectedOrder.isMerged && (
                    <Chip label="AI Merged - Optimized delivery" color="primary" />
                  )}
                </>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDetailsOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default OrderManagement;