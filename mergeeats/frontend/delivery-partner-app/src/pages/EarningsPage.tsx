import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  SelectChangeEvent
} from '@mui/material';
import {
  TrendingUp,
  AccountBalance,
  Payment,
  CalendarToday,
  FilterList
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

interface Earning {
  id: string;
  date: string;
  amount: number;
  deliveryCount: number;
  status: 'PENDING' | 'PAID' | 'CANCELLED';
  paymentMethod: string;
}

interface EarningsSummary {
  totalEarnings: number;
  thisWeek: number;
  thisMonth: number;
  pendingAmount: number;
  totalDeliveries: number;
}

const EarningsPage: React.FC = () => {
  const { deliveryPartner } = useAuth();
  const [earnings, setEarnings] = useState<Earning[]>([]);
  const [summary, setSummary] = useState<EarningsSummary>({
    totalEarnings: 0,
    thisWeek: 0,
    thisMonth: 0,
    pendingAmount: 0,
    totalDeliveries: 0
  });
  const [filterDialogOpen, setFilterDialogOpen] = useState(false);
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    status: 'ALL'
  });

  useEffect(() => {
    // Mock data - replace with actual API call
    const mockEarnings: Earning[] = [
      {
        id: '1',
        date: '2024-01-15',
        amount: 45.50,
        deliveryCount: 3,
        status: 'PAID',
        paymentMethod: 'Direct Deposit'
      },
      {
        id: '2',
        date: '2024-01-14',
        amount: 32.75,
        deliveryCount: 2,
        status: 'PENDING',
        paymentMethod: 'Direct Deposit'
      },
      {
        id: '3',
        date: '2024-01-13',
        amount: 28.00,
        deliveryCount: 2,
        status: 'PAID',
        paymentMethod: 'Direct Deposit'
      }
    ];

    const mockSummary: EarningsSummary = {
      totalEarnings: 1250.75,
      thisWeek: 156.25,
      thisMonth: 450.50,
      pendingAmount: 32.75,
      totalDeliveries: 45
    };

    setEarnings(mockEarnings);
    setSummary(mockSummary);
  }, []);

  const handleFilterChange = (field: string) => (event: SelectChangeEvent | React.ChangeEvent<HTMLInputElement>) => {
    setFilters(prev => ({
      ...prev,
      [field]: event.target.value
    }));
  };

  const handleDateChange = (field: string) => (event: React.ChangeEvent<HTMLInputElement>) => {
    setFilters(prev => ({
      ...prev,
      [field]: event.target.value
    }));
  };

  const applyFilters = () => {
    // Apply filters logic here
    setFilterDialogOpen(false);
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PAID':
        return 'success';
      case 'PENDING':
        return 'warning';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Earnings Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Track your earnings and payment history
        </Typography>
      </Box>

      {/* Summary Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <AccountBalance color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Total Earnings</Typography>
              </Box>
              <Typography variant="h4" color="primary">
                ${summary.totalEarnings.toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <TrendingUp color="success" sx={{ mr: 1 }} />
                <Typography variant="h6">This Week</Typography>
              </Box>
              <Typography variant="h4" color="success.main">
                ${summary.thisWeek.toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <CalendarToday color="info" sx={{ mr: 1 }} />
                <Typography variant="h6">This Month</Typography>
              </Box>
              <Typography variant="h4" color="info.main">
                ${summary.thisMonth.toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Payment color="warning" sx={{ mr: 1 }} />
                <Typography variant="h6">Pending</Typography>
              </Box>
              <Typography variant="h4" color="warning.main">
                ${summary.pendingAmount.toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Filters and Table */}
      <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h6">Earnings History</Typography>
        <Button
          variant="outlined"
          startIcon={<FilterList />}
          onClick={() => setFilterDialogOpen(true)}
        >
          Filter
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Date</TableCell>
              <TableCell>Amount</TableCell>
              <TableCell>Deliveries</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Payment Method</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {earnings.map((earning) => (
              <TableRow key={earning.id}>
                <TableCell>{earning.date}</TableCell>
                <TableCell>${earning.amount.toFixed(2)}</TableCell>
                <TableCell>{earning.deliveryCount}</TableCell>
                <TableCell>
                  <Chip
                    label={earning.status}
                    color={getStatusColor(earning.status) as any}
                    size="small"
                  />
                </TableCell>
                <TableCell>{earning.paymentMethod}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Filter Dialog */}
      <Dialog open={filterDialogOpen} onClose={() => setFilterDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Filter Earnings</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Start Date"
                type="date"
                value={filters.startDate}
                onChange={handleDateChange('startDate')}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="End Date"
                type="date"
                value={filters.endDate}
                onChange={handleDateChange('endDate')}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Status</InputLabel>
                <Select
                  value={filters.status}
                  label="Status"
                  onChange={handleFilterChange('status')}
                >
                  <MenuItem value="ALL">All</MenuItem>
                  <MenuItem value="PAID">Paid</MenuItem>
                  <MenuItem value="PENDING">Pending</MenuItem>
                  <MenuItem value="CANCELLED">Cancelled</MenuItem>
                </Select>
              </FormControl>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setFilterDialogOpen(false)}>Cancel</Button>
          <Button onClick={applyFilters} variant="contained">Apply Filters</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default EarningsPage; 