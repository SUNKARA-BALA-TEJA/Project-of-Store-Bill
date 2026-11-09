import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import api from '../api'
import './Auth.css'

function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const getLoginErrorMessage = (err) => {
    // Axios has no response when the request never reached the server
    // (backend down, DNS failure, or a CORS preflight rejection).
    if (!err.response) {
      return 'Unable to reach the FreshCart server. Please make sure the backend is running and that this site is allowed to connect to it.'
    }

    const data = err.response.data || {}
    if (typeof data === 'string' && data.trim()) {
      return data
    }
    if (data.error) {
      return data.error
    }

    switch (err.response.status) {
      case 400:
        return 'Please check the username and password you entered.'
      case 401:
        return 'Invalid username or password.'
      case 403:
        return 'Your account is not allowed to sign in. Please contact the administrator.'
      case 404:
        return 'The login service could not be found. Please check the API configuration.'
      case 500:
      case 502:
      case 503:
      case 504:
        return 'The server is temporarily unavailable. Please try again in a moment.'
      default:
        return 'Login failed. Please try again.'
    }
  }

  const handleLogin = async (e) => {
    e.preventDefault()
    setError('')
    if (!username || !password) {
      setError('Please enter username and password')
      return
    }
    try {
      const res = await api.post('/auth/login', { username, password })
      if (!res.data?.token) {
        setError('Login failed because the server did not return an authentication token.')
        return
      }
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('username', res.data.username)
      localStorage.setItem('role', res.data.role)
      navigate('/shop')
    } catch (err) {
      setError(getLoginErrorMessage(err))
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <div className="logo">🛒 FreshCart</div>
          <h2>Welcome Back</h2>
          <p>Login to manage your grocery store</p>
        </div>
        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label>Username</label>
            <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
          </div>
          <div className="form-group">
            <label>Password</label>
            <div className="password-input">
              <input type={showPassword ? 'text' : 'password'} value={password} onChange={(e) => setPassword(e.target.value)} />
              <button type="button" onClick={() => setShowPassword(!showPassword)} className="show-btn">
                {showPassword ? 'Hide' : 'Show'}
              </button>
            </div>
          </div>
          {error && <div className="error-msg">{error}</div>}
          <button type="submit" className="auth-btn">Login</button>
        </form>
        <div className="auth-footer">
          Don't have an account? <Link to="/register">Register</Link>
        </div>
      </div>
    </div>
  )
}

export default Login
