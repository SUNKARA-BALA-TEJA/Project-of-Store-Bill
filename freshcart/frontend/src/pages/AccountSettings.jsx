import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api'
import './Profile.css'

function AccountSettings() {
  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [showCurrent, setShowCurrent] = useState(false)
  const [showNew, setShowNew] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()
  const role = localStorage.getItem('role')

  const handleChangePassword = async (e) => {
    e.preventDefault()
    setMessage('')
    setError('')
    if (!currentPassword || !newPassword || !confirmPassword) {
      setError('All fields are required')
      return
    }
    if (newPassword !== confirmPassword) {
      setError('New passwords do not match')
      return
    }
    if (newPassword === currentPassword) {
      setError('New password must be different from current password')
      return
    }
    try {
      await api.put('/user/change-password', { currentPassword, newPassword, confirmPassword })
      setMessage('Password changed successfully.')
      setCurrentPassword('')
      setNewPassword('')
      setConfirmPassword('')
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to change password')
    }
  }

  const logout = () => {
    localStorage.clear()
    navigate('/login')
  }

  return (
    <div className="profile-container">
      <header className="profile-header">
        <div className="logo">ðŸ›’ FreshCart</div>
        <div className="header-right">
          <button onClick={() => navigate('/shop')} className="icon-btn">Back to Shop</button>
          <button onClick={() => navigate('/profile')} className="icon-btn">My Profile</button>
          {role === 'ADMIN' && <button onClick={() => navigate('/settings')} className="icon-btn">Settings</button>}
          <button onClick={logout} className="icon-btn">Logout</button>
        </div>
      </header>
      <div className="profile-content">
        <h2>Account Settings - Change Password</h2>
        {message && <div className="success-msg">{message}</div>}
        {error && <div className="error-msg">{error}</div>}
        <form onSubmit={handleChangePassword} className="profile-card">
          <div className="form-group">
            <label>Current Password</label>
            <div style={{display:'flex', gap:'5px'}}>
              <input type={showCurrent ? 'text' : 'password'} value={currentPassword} onChange={(e) => setCurrentPassword(e.target.value)} style={{flex:1}} />
              <button type="button" onClick={() => setShowCurrent(!showCurrent)} className="icon-btn">{showCurrent ? 'Hide' : 'Show'}</button>
            </div>
          </div>
          <div className="form-group">
            <label>New Password</label>
            <div style={{display:'flex', gap:'5px'}}>
              <input type={showNew ? 'text' : 'password'} value={newPassword} onChange={(e) => setNewPassword(e.target.value)} style={{flex:1}} />
              <button type="button" onClick={() => setShowNew(!showNew)} className="icon-btn">{showNew ? 'Hide' : 'Show'}</button>
            </div>
          </div>
          <div className="form-group">
            <label>Confirm New Password</label>
            <div style={{display:'flex', gap:'5px'}}>
              <input type={showConfirm ? 'text' : 'password'} value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} style={{flex:1}} />
              <button type="button" onClick={() => setShowConfirm(!showConfirm)} className="icon-btn">{showConfirm ? 'Hide' : 'Show'}</button>
            </div>
          </div>
          <button type="submit" className="save-btn">Update Password</button>
        </form>
      </div>
    </div>
  )
}

export default AccountSettings

