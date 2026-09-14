import { AppBar, Box, Button, Container, Toolbar, Typography } from '@mui/material';
import { Link, Route, Routes } from 'react-router-dom';
import ProductList from './pages/ProductList';
import POS from './pages/POS';

function HomePage() {
  return (
    <Container maxWidth="md" sx={{ py: 8 }}>
      <Typography variant="overline" color="secondary">Task 01</Typography>
      <Typography variant="h2" component="h1" gutterBottom>
        POS Order & Inventory
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 3 }}>
        Manage products, inventory, cart, and orders with safe concurrent checkout.
      </Typography>
      <Box sx={{ display: 'flex', gap: 2 }}>
        <Button component={Link} to="/products" variant="contained" color="primary">
          Go to Products
        </Button>
      </Box>
    </Container>
  );
}

function App() {
  return (
    <Box minHeight="100vh" sx={{ backgroundColor: '#f5f7f8' }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>Techloom POS</Typography>
          <Button component={Link} to="/" color="inherit">Dashboard</Button>
          <Button component={Link} to="/products" color="inherit">Products</Button>
          <Button component={Link} to="/pos" color="inherit">POS Checkout</Button>
        </Toolbar>
      </AppBar>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/products" element={<ProductList />} />
        <Route path="/pos" element={<POS />} />
      </Routes>
    </Box>
  );
}

export default App;
