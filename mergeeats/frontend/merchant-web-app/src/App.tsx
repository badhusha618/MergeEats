import React, { useState, useEffect } from 'react';
import {
  AppBar,
  Box,
  CssBaseline,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  ThemeProvider,
  createTheme,
  Card,
  CardContent,
  Grid,
  Avatar,
  Chip,
  Button,
  Badge,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Dashboard,
  Restaurant,
  ShoppingCart,
  Analytics,
  Settings,
  Notifications,
  TrendingUp,
  People,
  LocalShipping,
  AttachMoney,
} from '@mui/icons-material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar, PieChart, Pie, Cell } from 'recharts';
import OrderManagement from './components/OrderManagement';
import './App.css';

const drawerWidth = 240;

// Create a modern theme
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#ff6b35',
    },
    background: {
      default: '#f5f5f5',
    },
  },
  typography: {
    h4: {
      fontWeight: 600,
    },
    h6: {
      fontWeight: 500,
    },
  },
});

// Sample data for charts
const orderData = [
  { name: 'Mon', orders: 12, revenue: 450 },
  { name: 'Tue', orders: 19, revenue: 680 },
  { name: 'Wed', orders: 15, revenue: 520 },
  { name: 'Thu', orders: 22, revenue: 780 },
  { name: 'Fri', orders: 28, revenue: 980 },
  { name: 'Sat', orders: 35, revenue: 1200 },
  { name: 'Sun', orders: 25, revenue: 850 },
];

const statusData = [
  { name: 'Pending', value: 8, color: '#ff9800' },
  { name: 'Preparing', value: 12, color: '#2196f3' },
  { name: 'Ready', value: 5, color: '#4caf50' },
  { name: 'Delivered', value: 45, color: '#8bc34a' },
];

const menuItems = [
  { name: 'Dashboard', icon: <Dashboard />, active: true },
  { name: 'Orders', icon: <ShoppingCart />, badge: 8 },
  { name: 'Menu', icon: <Restaurant /> },
  { name: 'Analytics', icon: <Analytics /> },
  { name: 'Delivery', icon: <LocalShipping /> },
  { name: 'Settings', icon: <Settings /> },
];

const recentOrders = [
  { id: '#12345', customer: 'John Doe', items: 'Burger, Fries', amount: '$24.99', status: 'Preparing', time: '10:30 AM' },
  { id: '#12346', customer: 'Jane Smith', items: 'Pizza, Coke', amount: '$18.50', status: 'Ready', time: '10:25 AM' },
  { id: '#12347', customer: 'Mike Johnson', items: 'Salad, Water', amount: '$12.99', status: 'Delivered', time: '10:15 AM' },
  { id: '#12348', customer: 'Sarah Wilson', items: 'Pasta, Wine', amount: '$32.00', status: 'Pending', time: '10:35 AM' },
];

function App() {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [selectedMenu, setSelectedMenu] = useState('Dashboard');

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Pending': return 'warning';
      case 'Preparing': return 'info';
      case 'Ready': return 'success';
      case 'Delivered': return 'default';
      default: return 'default';
    }
  };

  const drawer = (
    <div>
      <Toolbar>
        <Typography variant="h6" noWrap component="div" sx={{ color: 'primary.main', fontWeight: 'bold' }}>
          MergeEats
        </Typography>
      </Toolbar>
      <List>
        {menuItems.map((item) => (
          <ListItem key={item.name} disablePadding>
            <ListItemButton 
              selected={selectedMenu === item.name}
              onClick={() => setSelectedMenu(item.name)}
              sx={{
                '&.Mui-selected': {
                  backgroundColor: 'primary.main',
                  color: 'white',
                  '&:hover': {
                    backgroundColor: 'primary.dark',
                  },
                  '& .MuiListItemIcon-root': {
                    color: 'white',
                  },
                },
              }}
            >
              <ListItemIcon>
                {item.badge ? (
                  <Badge badgeContent={item.badge} color="error">
                    {item.icon}
                  </Badge>
                ) : (
                  item.icon
                )}
              </ListItemIcon>
              <ListItemText primary={item.name} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </div>
  );

  const DashboardContent = () => (
    <Box>
      {/* Stats Cards */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ height: '100%', background: 'linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', color: 'white' }}>
                <TrendingUp sx={{ fontSize: 40, mr: 2 }} />
                <Box>
                  <Typography variant="h4" component="div">
                    156
                  </Typography>
                  <Typography variant="body2">
                    Today's Orders
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ height: '100%', background: 'linear-gradient(45deg, #2196F3 30%, #21CBF3 90%)' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', color: 'white' }}>
                <AttachMoney sx={{ fontSize: 40, mr: 2 }} />
                <Box>
                  <Typography variant="h4" component="div">
                    $2,340
                  </Typography>
                  <Typography variant="body2">
                    Today's Revenue
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ height: '100%', background: 'linear-gradient(45deg, #4CAF50 30%, #8BC34A 90%)' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', color: 'white' }}>
                <People sx={{ fontSize: 40, mr: 2 }} />
                <Box>
                  <Typography variant="h4" component="div">
                    89
                  </Typography>
                  <Typography variant="body2">
                    Active Customers
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ height: '100%', background: 'linear-gradient(45deg, #FF9800 30%, #FFC107 90%)' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', color: 'white' }}>
                <LocalShipping sx={{ fontSize: 40, mr: 2 }} />
                <Box>
                  <Typography variant="h4" component="div">
                    12
                  </Typography>
                  <Typography variant="body2">
                    Active Deliveries
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Charts */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Weekly Orders & Revenue
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={orderData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Line type="monotone" dataKey="orders" stroke="#8884d8" strokeWidth={2} />
                  <Line type="monotone" dataKey="revenue" stroke="#82ca9d" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Order Status
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={statusData}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={100}
                    paddingAngle={5}
                    dataKey="value"
                  >
                    {statusData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Recent Orders */}
      <Card>
        <CardContent>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="h6">
              Recent Orders
            </Typography>
            <Button variant="outlined" size="small">
              View All
            </Button>
          </Box>
          <Grid container spacing={2}>
            {recentOrders.map((order) => (
              <Grid item xs={12} key={order.id}>
                <Card variant="outlined">
                  <CardContent sx={{ py: 1 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Avatar sx={{ mr: 2, bgcolor: 'primary.main' }}>
                          {order.customer[0]}
                        </Avatar>
                        <Box>
                          <Typography variant="subtitle2" fontWeight="bold">
                            {order.id} - {order.customer}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            {order.items}
                          </Typography>
                        </Box>
                      </Box>
                      <Box sx={{ textAlign: 'right' }}>
                        <Typography variant="subtitle2" fontWeight="bold">
                          {order.amount}
                        </Typography>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <Chip 
                            label={order.status} 
                            size="small" 
                            color={getStatusColor(order.status) as any}
                          />
                          <Typography variant="caption" color="text.secondary">
                            {order.time}
                          </Typography>
                        </Box>
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </CardContent>
      </Card>
    </Box>
  );

  return (
    <ThemeProvider theme={theme}>
      <Box sx={{ display: 'flex' }}>
        <CssBaseline />
        <AppBar
          position="fixed"
          sx={{
            width: { sm: `calc(100% - ${drawerWidth}px)` },
            ml: { sm: `${drawerWidth}px` },
          }}
        >
          <Toolbar>
            <IconButton
              color="inherit"
              aria-label="open drawer"
              edge="start"
              onClick={handleDrawerToggle}
              sx={{ mr: 2, display: { sm: 'none' } }}
            >
              <MenuIcon />
            </IconButton>
            <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
              {selectedMenu}
            </Typography>
            <IconButton color="inherit">
              <Badge badgeContent={4} color="error">
                <Notifications />
              </Badge>
            </IconButton>
          </Toolbar>
        </AppBar>
        <Box
          component="nav"
          sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
          aria-label="mailbox folders"
        >
          <Drawer
            variant="temporary"
            open={mobileOpen}
            onClose={handleDrawerToggle}
            ModalProps={{
              keepMounted: true,
            }}
            sx={{
              display: { xs: 'block', sm: 'none' },
              '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
            }}
          >
            {drawer}
          </Drawer>
          <Drawer
            variant="permanent"
            sx={{
              display: { xs: 'none', sm: 'block' },
              '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
            }}
            open
          >
            {drawer}
          </Drawer>
        </Box>
        <Box
          component="main"
          sx={{ flexGrow: 1, p: 3, width: { sm: `calc(100% - ${drawerWidth}px)` } }}
        >
          <Toolbar />
          {selectedMenu === 'Dashboard' && <DashboardContent />}
          {selectedMenu === 'Orders' && <OrderManagement />}
          {selectedMenu !== 'Dashboard' && selectedMenu !== 'Orders' && (
            <Typography variant="h4">
              {selectedMenu} - Coming Soon!
            </Typography>
          )}
        </Box>
      </Box>
    </ThemeProvider>
  );
}

export default App;
