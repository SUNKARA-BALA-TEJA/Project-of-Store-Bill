import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api'
import './Settings.css'

function Settings() {
  const [activeTab, setActiveTab] = useState('prices')
  const [products, setProducts] = useState([])
  const [prices, setPrices] = useState({})
  const [stocks, setStocks] = useState({})
  const [newProduct, setNewProduct] = useState({ name: '', category: '', unit: '', price: '', initialStock: '', minThreshold: '' })
  const [image, setImage] = useState(null)
  const [bills, setBills] = useState([])
  const [requests, setRequests] = useState([])
  const [requestFilter, setRequestFilter] = useState('all')
  const [billSearch, setBillSearch] = useState('')
  const [billCustomer, setBillCustomer] = useState('')
  const [billSort, setBillSort] = useState('latest')
  const navigate = useNavigate()
  const username = localStorage.getItem('username')
  const role = localStorage.getItem('role')

  useEffect(() => {
    if (role !== 'ADMIN') {
      alert('Access denied. Admin only.')
      navigate('/shop')
      return
    }
    fetchProducts()
    fetchBills()
    fetchRequests()
    const handleClick = () => {
      const menu = document.getElementById('profileMenu')
      if (menu) menu.style.display = 'none'
    }
    document.addEventListener('click', handleClick)
    return () => document.removeEventListener('click', handleClick)
  }, [])

  const fetchProducts = async () => {
    try {
      const res = await api.get('/products')
      setProducts(res.data)
    } catch (err) {
      console.error(err)
    }
  }

  const fetchBills = async () => {
    try {
      const res = await api.get('/bills')
      setBills(res.data)
    } catch (err) {
      console.error(err)
    }
  }

  const fetchRequests = async () => {
    try {
      const res = await api.get('/admin/requests')
      setRequests(res.data || [])
    } catch (err) {
      console.error(err)
    }
  }

  const approveRequest = async (id) => {
    try {
      await api.put(`/admin/requests/${id}/approve`)
      alert('User approved successfully')
      fetchRequests()
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to approve')
    }
  }

  const rejectRequest = async (id) => {
    try {
      await api.put(`/admin/requests/${id}/reject`)
      alert('Request rejected')
      fetchRequests()
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to reject')
    }
  }

  const deleteBill = async (billId, customerName) => {
    if (!window.confirm(`Are you sure you want to delete this bill?\nBill ID: ${billId}\nCustomer: ${customerName}`)) {
      return
    }
    try {
      await api.delete(`/admin/bills/${billId}`)
      alert('Bill deleted successfully')
      fetchBills()
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to delete bill')
    }
  }

  const updatePrice = async (productId) => {
    try {
      await api.post(`/admin/price?productId=${productId}&newPrice=${prices[productId]}`)
      alert('Price updated successfully')
      fetchProducts()
      setPrices({ ...prices, [productId]: '' })
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to update price')
    }
  }

  const addStock = async (productId) => {
    try {
      await api.post(`/admin/stock/add?productId=${productId}&quantity=${stocks[productId]}`)
      alert('Stock added successfully')
      fetchProducts()
      setStocks({ ...stocks, [productId]: '' })
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to add stock')
    }
  }

  const addNewProduct = async (e) => {
    e.preventDefault()
    try {
      const formData = new FormData()
      formData.append('name', newProduct.name)
      formData.append('category', newProduct.category)
      formData.append('unit', newProduct.unit)
      formData.append('price', newProduct.price)
      formData.append('initialStock', newProduct.initialStock)
      formData.append('minThreshold', newProduct.minThreshold)
      if (image) formData.append('image', image)
      await api.post('/admin/product', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
      alert('Product added successfully')
      fetchProducts()
      setNewProduct({ name: '', category: '', unit: '', price: '', initialStock: '', minThreshold: '' })
      setImage(null)
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to add product')
    }
  }

  const viewInvoice = async (invoiceNumber) => {
    try {
      const res = await api.get(`/bills/${invoiceNumber}`)
      alert(`Invoice: ${res.data.invoiceNumber}\nCustomer: ${res.data.customerName}\nTotal: ₹${res.data.finalTotal}`)
    } catch (err) {
      alert('Failed to load invoice')
    }
  }

  const logout = () => {
    localStorage.clear()
    navigate('/login')
  }

  return (
    <div className="settings-container">
      <header className="settings-header">
        <div className="logo">🛒 FreshCart - Settings</div>
        <div className="header-right">
          <div style={{position:'relative'}}>
            <button onClick={(e) => {
              e.stopPropagation()
              const menu = document.getElementById('profileMenu')
              if (menu) menu.style.display = menu.style.display === 'block' ? 'none' : 'block'
            }} className="icon-btn">👤 {username} (Admin)</button>
            <div id="profileMenu" style={{display:'none', position:'absolute', right:0, top:'40px', background:'white', border:'2px solid #FFD700', borderRadius:'5px', zIndex:1000, minWidth:'150px'}} onClick={(e) => e.stopPropagation()}>
              <div style={{padding:'8px 12px', cursor:'pointer'}} onClick={() => { document.getElementById('profileMenu').style.display='none'; navigate('/profile'); }}>My Profile</div>
              <div style={{padding:'8px 12px', cursor:'pointer'}} onClick={() => { document.getElementById('profileMenu').style.display='none'; navigate('/account-settings'); }}>Account Settings</div>
              <div style={{padding:'8px 12px', cursor:'pointer'}} onClick={() => { document.getElementById('profileMenu').style.display='none'; navigate('/account-settings'); }}>Change Password</div>
              <div style={{padding:'8px 12px', cursor:'pointer', borderTop:'1px solid #eee'}} onClick={() => { document.getElementById('profileMenu').style.display='none'; logout(); }}>Logout</div>
            </div>
          </div>
          <button onClick={() => navigate('/shop')} className="icon-btn">Back to Shop</button>
        </div>
      </header>

      <div className="settings-layout">
        <nav className="settings-nav">
          <button className={activeTab === 'prices' ? 'active' : ''} onClick={() => setActiveTab('prices')}>
            ⚙️ Modify Grocery Prices
          </button>
          <button className={activeTab === 'stock' ? 'active' : ''} onClick={() => setActiveTab('stock')}>
            📦 Add Stock to Existing Groceries
          </button>
          <button className={activeTab === 'add' ? 'active' : ''} onClick={() => setActiveTab('add')}>
            ➕ Add New Grocery
          </button>
          <button className={activeTab === 'requests' ? 'active' : ''} onClick={() => setActiveTab('requests')}>
            📝 Requests {requests.filter(r => r.status === 'PENDING' || r.requestStatus === 'PENDING').length > 0 ? `(${requests.filter(r => r.status === 'PENDING' || r.requestStatus === 'PENDING').length} Pending)` : ''}
          </button>
          <button className={activeTab === 'billmgmt' ? 'active' : ''} onClick={() => setActiveTab('billmgmt')}>
            🧾 Bill Management
          </button>
          <button className={activeTab === 'reports' ? 'active' : ''} onClick={() => setActiveTab('reports')}>
            📊 Data and Reports
          </button>
        </nav>

        <main className="settings-content">
          {activeTab === 'prices' && (
            <section>
              <h2>Modify Grocery Prices</h2>
              <table className="settings-table">
                <thead>
                  <tr>
                    <th>Product</th>
                    <th>Category</th>
                    <th>Current Price</th>
                    <th>Unit</th>
                    <th>New Price</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {products.map(product => (
                    <tr key={product.id}>
                      <td>
                        <div className="product-cell">
                          <img src={product.imagePath ? `http://localhost:8080${product.imagePath}` : 'https://via.placeholder.com/50'} alt={product.name} />
                          <span>{product.name}</span>
                        </div>
                      </td>
                      <td>{product.category}</td>
                      <td>₹{product.currentSellingPrice}</td>
                      <td>{product.unit}</td>
                      <td><input type="number" step="0.01" value={prices[product.id] || ''} onChange={(e) => setPrices({...prices, [product.id]: e.target.value})} /></td>
                      <td>
                        <button onClick={() => updatePrice(product.id)} className="action-btn">Save/Update</button>
                        <button onClick={async () => { if(confirm('Delete this product?')) { try { await api.delete(`/admin/product/${product.id}`); fetchProducts(); } catch(e){ alert('Failed'); } } }} className="remove-btn" style={{marginLeft:'5px'}}>Delete</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </section>
          )}

          {activeTab === 'stock' && (
            <section>
              <h2>Add Quantity to Existing Groceries</h2>
              <div className="stock-grid">
                {products.map(product => (
                  <div key={product.id} className="stock-card">
                    <img src={product.imagePath ? `http://localhost:8080${product.imagePath}` : 'https://via.placeholder.com/100'} alt={product.name} />
                    <h3>{product.name}</h3>
                    <p>Current stock: {product.stock} {product.unit}</p>
                    <div className="stock-input-group">
                      <input type="number" step="0.001" placeholder="Qty to add" value={stocks[product.id] || ''} onChange={(e) => setStocks({...stocks, [product.id]: e.target.value})} />
                      <button onClick={() => addStock(product.id)}>Add Stock</button>
                    </div>
                  </div>
                ))}
              </div>
            </section>
          )}

          {activeTab === 'add' && (
            <section>
              <h2>Add New Grocery</h2>
              <form onSubmit={addNewProduct} className="new-product-form">
                <div className="form-row">
                  <div className="form-group">
                    <label>Grocery Name</label>
                    <input type="text" value={newProduct.name} onChange={(e) => setNewProduct({...newProduct, name: e.target.value})} required />
                  </div>
                  <div className="form-group">
                    <label>Category</label>
                    <input type="text" value={newProduct.category} onChange={(e) => setNewProduct({...newProduct, category: e.target.value})} />
                  </div>
                </div>
                <div className="form-row">
                  <div className="form-group">
                    <label>Unit of Measurement</label>
                    <input type="text" placeholder="kg, litre, pieces" value={newProduct.unit} onChange={(e) => setNewProduct({...newProduct, unit: e.target.value})} required />
                  </div>
                  <div className="form-group">
                    <label>Selling Price</label>
                    <input type="number" step="0.01" value={newProduct.price} onChange={(e) => setNewProduct({...newProduct, price: e.target.value})} required />
                  </div>
                </div>
                <div className="form-row">
                  <div className="form-group">
                    <label>Initial Stock Quantity</label>
                    <input type="number" step="0.001" value={newProduct.initialStock} onChange={(e) => setNewProduct({...newProduct, initialStock: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>Minimum Stock Threshold</label>
                    <input type="number" value={newProduct.minThreshold} onChange={(e) => setNewProduct({...newProduct, minThreshold: e.target.value})} />
                  </div>
                </div>
                <div className="form-group">
                  <label>Product Image</label>
                  <input type="file" accept="image/*" onChange={(e) => setImage(e.target.files[0])} />
                </div>
                <button type="submit" className="submit-product-btn">Save Grocery</button>
              </form>
            </section>
          )}

          {activeTab === 'requests' && (
            <section>
              <h2>Registration Requests</h2>
              <div style={{marginBottom:'15px'}}>
                <select value={requestFilter} onChange={(e) => setRequestFilter(e.target.value)}>
                  <option value="all">All Requests</option>
                  <option value="PENDING">Pending</option>
                  <option value="APPROVED">Approved</option>
                  <option value="REJECTED">Rejected</option>
                </select>
              </div>
              <table className="settings-table">
                <thead>
                  <tr>
                    <th>Username</th>
                    <th>Full Name</th>
                    <th>Email Address</th>
                    <th>Registration Date</th>
                    <th>Current Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {(Array.isArray(requests) ? requests : []).filter(r => {
                    const st = r.status || r.requestStatus || r.accountStatus
                    if (requestFilter === 'all') return true
                    return st === requestFilter
                  }).map((r, idx) => (
                    <tr key={r.id || r.requestId || idx}>
                      <td>{r.username}</td>
                      <td>{r.fullName}</td>
                      <td>{r.email}</td>
                      <td>{r.requestedAt || r.createdAt ? new Date(r.requestedAt || r.createdAt).toLocaleString() : '-'}</td>
                      <td>{r.status || r.requestStatus || r.accountStatus}</td>
                      <td>
                        {(r.status === 'PENDING' || r.requestStatus === 'PENDING' || r.accountStatus === 'PENDING') && (
                          <>
                            <button onClick={() => approveRequest(r.id || r.userId || r.requestId)} className="action-btn">APPROVE</button>
                            <button onClick={() => rejectRequest(r.id || r.userId || r.requestId)} className="remove-btn" style={{marginLeft:'5px'}}>REJECT</button>
                          </>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </section>
          )}

          {activeTab === 'billmgmt' && (
            <section>
              <h2>Bill Management</h2>
              <div style={{display:'flex', gap:'10px', marginBottom:'15px', flexWrap:'wrap'}}>
                <input placeholder="Search by Bill ID" value={billSearch} onChange={(e) => setBillSearch(e.target.value)} />
                <input placeholder="Search by Customer Name" value={billCustomer} onChange={(e) => setBillCustomer(e.target.value)} />
                <select value={billSort} onChange={(e) => setBillSort(e.target.value)}>
                  <option value="latest">Sort by Latest</option>
                  <option value="oldest">Sort by Oldest</option>
                </select>
              </div>
              <table className="settings-table">
                <thead>
                  <tr>
                    <th>Bill ID</th>
                    <th>Customer Name</th>
                    <th>Bill Date</th>
                    <th>Number of Items</th>
                    <th>Total Quantity</th>
                    <th>Total Amount</th>
                    <th>Created By</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {(Array.isArray(bills) ? bills : [])
                    .filter(b => {
                      const idMatch = billSearch ? (b.invoiceNumber || b.billId || '').toString().toLowerCase().includes(billSearch.toLowerCase()) : true
                      const custMatch = billCustomer ? (b.customerName || '').toLowerCase().includes(billCustomer.toLowerCase()) : true
                      return idMatch && custMatch
                    })
                    .sort((a,b) => {
                      const da = new Date(a.createdAt || a.billDate || 0).getTime()
                      const db = new Date(b.createdAt || b.billDate || 0).getTime()
                      return billSort === 'latest' ? db - da : da - db
                    })
                    .map((bill, idx) => (
                    <tr key={bill.invoiceNumber || bill.billId || idx}>
                      <td>{bill.invoiceNumber || bill.billId}</td>
                      <td>{bill.customerName}</td>
                      <td>{bill.createdAt || bill.billDate ? new Date(bill.createdAt || bill.billDate).toLocaleString() : '-'}</td>
                      <td>{bill.itemCount || (bill.items ? bill.items.length : 0)}</td>
                      <td>{bill.totalQuantity || '-'}</td>
                      <td>₹{bill.finalTotal || bill.totalAmount}</td>
                      <td>{bill.createdBy || bill.username || '-'}</td>
                      <td><button onClick={() => deleteBill(bill.invoiceNumber || bill.billId, bill.customerName)} className="remove-btn">Delete</button></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </section>
          )}

          {activeTab === 'reports' && (
            <section>
              <h2>Data and Reports</h2>
              <h3>Saved Transaction History</h3>
              <table className="settings-table">
                <thead>
                  <tr>
                    <th>Invoice Number</th>
                    <th>Date</th>
                    <th>Customer Name</th>
                    <th>Items</th>
                    <th>Final Amount</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {bills.map(bill => (
                    <tr key={bill.invoiceNumber}>
                      <td>{bill.invoiceNumber}</td>
                      <td>{new Date(bill.createdAt).toLocaleString()}</td>
                      <td>{bill.customerName}</td>
                      <td>{bill.itemCount}</td>
                      <td>₹{bill.finalTotal}</td>
                      <td>{bill.status}</td>
                      <td><button onClick={() => viewInvoice(bill.invoiceNumber)} className="action-btn">View Invoice</button></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </section>
          )}
        </main>
      </div>
    </div>
  )
}

export default Settings
