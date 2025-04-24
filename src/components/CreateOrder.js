import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, TextField, Typography, Box, Paper, Grid, Alert } from '@mui/material';
import { createOrder } from '../services/orderApi';

const CreateOrder = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  
  const [formData, setFormData] = useState({
    productId: '',
    productName: '',
    price: '',
    quantity: '1',
    shippingAddress: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      // Convert string values to appropriate types
      const orderData = {
        productId: Number(formData.productId),
        productName: formData.productName,
        price: Number(formData.price),
        quantity: Number(formData.quantity),
        shippingAddress: formData.shippingAddress
      };
      
      const response = await createOrder(orderData);
      setSuccess(true);
      setFormData({
        productId: '',
        productName: '',
        price: '',
        quantity: '1',
        shippingAddress: ''
      });
      
      // Redirect to order details after 2 seconds
      setTimeout(() => {
        navigate(`/orders/${response.id}`);
      }, 2000);
      
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to create order. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Create New Order
      </Typography>
      
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      
      {success && (
        <Alert severity="success" sx={{ mb: 2 }}>
          Order created successfully! Redirecting to order details...
        </Alert>
      )}
      
      <Paper elevation={3} sx={{ p: 3 }}>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Product ID"
                name="productId"
                value={formData.productId}
                onChange={handleChange}
                type="number"
                required
                disabled={loading}
                inputProps={{ min: 1 }}
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Product Name"
                name="productName"
                value={formData.productName}
                onChange={handleChange}
                required
                disabled={loading}
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Price"
                name="price"
                value={formData.price}
                onChange={handleChange}
                type="number"
                required
                disabled={loading}
                inputProps={{ min: 0.01, step: 0.01 }}
              />
            </Grid>
            
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Quantity"
                name="quantity"
                value={formData.quantity}
                onChange={handleChange}
                type="number"
                required
                disabled={loading}
                inputProps={{ min: 1 }}
              />
            </Grid>
            
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Shipping Address"
                name="shippingAddress"
                value={formData.shippingAddress}
                onChange={handleChange}
                required
                disabled={loading}
                multiline
                rows={3}
              />
            </Grid>
            
            <Grid item xs={12}>
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
                <Button 
                  variant="contained" 
                  color="primary" 
                  type="submit" 
                  disabled={loading}
                  sx={{ minWidth: 120 }}
                >
                  {loading ? 'Creating...' : 'Create Order'}
                </Button>
              </Box>
            </Grid>
          </Grid>
        </form>
      </Paper>
    </Box>
  );
};

export default CreateOrder; 