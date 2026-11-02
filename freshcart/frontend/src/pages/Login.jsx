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

  const handleLogin = async (e) => {
    e.preventDefault()
    setError('')
    if (!username || !password) {
      setError('Please enter username and password')
      return
    }
    try {
      const res = await api.post('/auth/login', { username, password })
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('username', res.data.username)
      localStorage.setItem('role', res.data.role)
      navigate('/shop')
    } catch (err) {
      setError(err.response?.data?.error || 'Invalid credentials')
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
