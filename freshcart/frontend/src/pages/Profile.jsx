import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api'
import './Profile.css'

function Profile() {
  const [profile, setProfile] = useState(null)
  const [editMode, setEditMode] = useState(false)
  const [fullName, setFullName] = useState('')
  const [email, setEmail] = useState('')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()
  const role = localStorage.getItem('role')
  const username = localStorage.getItem('username')

  useEffect(() => {
    fetchProfile()
  }, [])

  const fetchProfile = async () => {
    try {
      const res = await api.get('/user/profile')
      setProfile(res.data)
      setFullName(res.data.fullName || '')
      setEmail(res.data.email || '')
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to load profile')
    }
  }

  const handleUpdate = async (e) => {
    e.preventDefault()
    setMessage('')
    setError('')
    try {
      await api.put('/user/profile', { fullName, email })
      setMessage('Profile updated successfully')
      setEditMode(false)
      fetchProfile()
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to update profile')
    }
  }

  const logout = () => {
    localStorage.clear()
    navigate('/login')
  }

  if (!profile) {
    return <div className="profile-container"><p>Loading...</p></div>
  }

  return (
    <div className="profile-container">
      <header className="profile-header">
        <div className="logo">🛒 FreshCart</div>
        <div className="header-right">
          <button onClick={() => navigate('/shop')} className="icon-btn">Back to Shop</button>
          {role === 'ADMIN' && <button onClick={() => navigate('/settings')} className="icon-btn">Settings</button>}
          <button onClick={() => navigate('/account-settings')} className="icon-btn">Account Settings</button>
          <button onClick={logout} className="icon-btn">Logout</button>
        </div>
      </header>
      <div className="profile-content">
        <h2>My Profile</h2>
        {message && <div className="success-msg">{message}</div>}
        {error && <div className="error-msg">{error}</div>}
        {!editMode ? (
          <div className="profile-card">
            <div className="profile-row"><strong>Full Name:</strong> {profile.fullName || '-'}</div>
            <div className="profile-row"><strong>Username:</strong> {profile.username}</div>
            <div className="profile-row"><strong>Email Address:</strong> {profile.email || '-'}</div>
            <div className="profile-row"><strong>User ID:</strong> {profile.userId || profile.id}</div>
            <div className="profile-row"><strong>Account Role:</strong> {profile.accountRole || profile.role}</div>
            <div className="profile-row"><strong>Account Status:</strong> {profile.accountStatus || profile.status}</div>
            <div className="profile-row"><strong>Registration Date:</strong> {profile.registrationDate || profile.createdAt ? new Date(profile.registrationDate || profile.createdAt).toLocaleString() : '-'}</div>
            <div className="profile-row"><strong>Last Login Date:</strong> {profile.lastLoginDate ? new Date(profile.lastLoginDate).toLocaleString() : '-'}</div>
            <button onClick={() => setEditMode(true)} className="edit-btn">Edit Profile</button>
          </div>
        ) : (
          <form onSubmit={handleUpdate} className="profile-card">
            <div className="form-group">
              <label>Full Name</label>
              <input type="text" value={fullName} onChange={(e) => setFullName(e.target.value)} />
            </div>
            <div className="form-group">
              <label>Email Address</label>
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
            </div>
            <div className="form-group">
              <label>Username (not editable)</label>
              <input type="text" value={profile.username} disabled />
            </div>
            <div className="button-group">
              <button type="submit" className="save-btn">Save Changes</button>
              <button type="button" onClick={() => {setEditMode(false); setFullName(profile.fullName||''); setEmail(profile.email||''); setError(''); setMessage('')}} className="cancel-btn">Cancel</button>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}

export default Profile
