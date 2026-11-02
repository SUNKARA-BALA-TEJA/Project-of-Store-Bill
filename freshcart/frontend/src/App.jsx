import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Shop from './pages/Shop'
import BillPage from './pages/BillPage'
import ThankYou from './pages/ThankYou'
import Settings from './pages/Settings'
import Profile from './pages/Profile'
import AccountSettings from './pages/AccountSettings'
import './App.css'

function App() {
  const isAuthenticated = () => {
    return localStorage.getItem('token') !== null
  }

  const PrivateRoute = ({ children }) => {
    return isAuthenticated() ? children : <Navigate to="/login" />
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/shop" element={<PrivateRoute><Shop /></PrivateRoute>} />
        <Route path="/bill" element={<PrivateRoute><BillPage /></PrivateRoute>} />
        <Route path="/thankyou" element={<PrivateRoute><ThankYou /></PrivateRoute>} />
        <Route path="/settings" element={<PrivateRoute><Settings /></PrivateRoute>} />
        <Route path="/profile" element={<PrivateRoute><Profile /></PrivateRoute>} />
        <Route path="/account-settings" element={<PrivateRoute><AccountSettings /></PrivateRoute>} />
        <Route path="/" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
