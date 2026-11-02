import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api'
import './Shop.css'

function Shop() {
  const [search, setSearch] = useState('')
  const [products, setProducts] = useState([])
  const [selectedItems, setSelectedItems] = useState([])
  const [quantities, setQuantities] = useState({})
  const navigate = useNavigate()
  const username = localStorage.getItem('username')
  const role = localStorage.getItem('role')

  useEffect(() => {
    fetchProducts()
    const handleClick = () => {
      const menu = document.getElementById('profileMenu')
      if (menu) menu.style.display = 'none'
    }
    document.addEventListener('click', handleClick)
    return () => document.removeEventListener('click', handleClick)
  }, [])

  const fetchProducts = async (query = '') => {
    try {
      const res = await api.get(`/products?search=${query}`)
      setProducts(res.data)
    } catch (err) {
      console.error(err)
    }
  }

  const handleSearch = (e) => {
    e.preventDefault()
    fetchProducts(search)
  }

  const addItem = (product) => {
    const qty = parseFloat(quantities[product.id] || 0)
    if (!qty || qty <= 0) {
      alert('Enter valid quantity')
      return
    }
    if (qty > parseFloat(product.stock)) {
      alert('Quantity exceeds available stock')
      return
    }
    const existing = selectedItems.find(item => item.product.id === product.id)
    if (existing) {
      setSelectedItems(selectedItems.map(item => 
        item.product.id === product.id 
          ? { ...item, quantity: (parseFloat(item.quantity) + qty).toString() }
          : item
      ))
    } else {
      setSelectedItems([...selectedItems, { product, quantity: qty.toString() }])
    }
    setQuantities({ ...quantities, [product.id]: '' })
  }

  const updateQuantity = (productId, delta) => {
    setSelectedItems(selectedItems.map(item => {
      if (item.product.id === productId) {
        let newQty = parseFloat(item.quantity) + delta
        if (newQty < 0.001) return item
        if (newQty > parseFloat(item.product.stock)) {
          alert('Quantity exceeds available stock')
          return item
        }
        return { ...item, quantity: newQty.toString() }
      }
      return item
    }))
  }

  const removeItem = (productId) => {
    setSelectedItems(selectedItems.filter(item => item.product.id !== productId))
  }

  const getTotal = () => {
    return selectedItems.reduce((sum, item) => {
      return sum + parseFloat(item.product.currentSellingPrice) * parseFloat(item.quantity)
    }, 0).toFixed(2)
  }

  const handleSubmit = () => {
    if (selectedItems.length === 0) {
      alert('Select at least one product')
      return
    }
    localStorage.setItem('billDraft', JSON.stringify(selectedItems))
    navigate('/bill')
  }

  const logout = () => {
    localStorage.clear()
    navigate('/login')
  }

  return (
    <div className="shop-container">
      <header className="shop-header">
        <div className="header-left">
          <div className="logo">🛒 FreshCart</div>
        </div>
        <form onSubmit={handleSearch} className="search-form">
          <input 
            type="text" 
            placeholder="Search groceries by name..." 
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <button type="submit">Search</button>
        </form>
        <div className="header-right">
          <div style={{position:'relative'}}>
            <button onClick={(e) => {
              e.stopPropagation()
              const menu = document.getElementById('profileMenu')
              if (menu) menu.style.display = menu.style.display === 'block' ? 'none' : 'block'
            }} className="icon-btn">👤 {username}</button>
            <div id="profileMenu" style={{display:'none', position:'absolute', right:0, top:'40px', background:'white', border:'2px solid #FFD700', borderRadius:'5px', zIndex:1000, minWidth:'150px'}} onClick={(e) => e.stopPropagation()}>
              <div style={{padding:'8px 12px', cursor:'pointer'}} onClick={() => { document.getElementById('profileMenu').style.display='none'; navigate('/profile'); }}>My Profile</div>
              <div style={{padding:'8px 12px', cursor:'pointer'}} onClick={() => { document.getElementById('profileMenu').style.display='none'; navigate('/account-settings'); }}>Account Settings</div>
              <div style={{padding:'8px 12px', cursor:'pointer'}} onClick={() => { document.getElementById('profileMenu').style.display='none'; navigate('/account-settings'); }}>Change Password</div>
              <div style={{padding:'8px 12px', cursor:'pointer', borderTop:'1px solid #eee'}} onClick={() => { document.getElementById('profileMenu').style.display='none'; logout(); }}>Logout</div>
            </div>
          </div>
          {role === 'ADMIN' && <button onClick={() => navigate('/settings')} className="icon-btn">⚙️ Settings</button>}
          <span className="cart-badge">🛍️ {selectedItems.length} items</span>
        </div>
      </header>

      <main className="shop-main">
        <section className="products-section">
          <h2>Available Products</h2>
          <div className="products-grid">
            {products.map(product => (
              <div key={product.id} className="product-card">
                <img 
                  src={product.imagePath ? `http://localhost:8080${product.imagePath}` : 'https://via.placeholder.com/150'} 
                  alt={product.name}
                  onError={(e) => { e.target.src = 'https://via.placeholder.com/150' }}
                />
                <h3>{product.name}</h3>
                <p className="price">₹{product.currentSellingPrice}/{product.unit}</p>
                <p className="stock">Available: {product.stock} {product.unit}</p>
                <div className="add-section">
                  <input 
                    type="number" 
                    step="0.001"
                    placeholder="Qty"
                    value={quantities[product.id] || ''}
                    onChange={(e) => setQuantities({...quantities, [product.id]: e.target.value})}
                  />
                  <button onClick={() => addItem(product)}>Add Item</button>
                </div>
              </div>
            ))}
          </div>
        </section>

        <section className="selected-section">
          <h2>Selected Groceries</h2>
          {selectedItems.length === 0 ? (
            <p className="empty">No items selected yet</p>
          ) : (
            <>
              <div className="selected-list">
                {selectedItems.map(item => (
                  <div key={item.product.id} className="selected-item">
                    <img 
                      src={item.product.imagePath ? `http://localhost:8080${item.product.imagePath}` : 'https://via.placeholder.com/80'} 
                      alt={item.product.name}
                    />
                    <div className="item-info">
                      <h4>{item.product.name}</h4>
                      <p>₹{item.product.currentSellingPrice}/{item.product.unit}</p>
                    </div>
                    <div className="item-qty">
                      <button onClick={() => updateQuantity(item.product.id, -0.5)}>-</button>
                      <span>{item.quantity} {item.product.unit}</span>
                      <button onClick={() => updateQuantity(item.product.id, 0.5)}>+</button>
                    </div>
                    <div className="item-subtotal">
                      ₹{(parseFloat(item.product.currentSellingPrice) * parseFloat(item.quantity)).toFixed(2)}
                    </div>
                    <button onClick={() => removeItem(item.product.id)} className="remove-btn">Remove</button>
                  </div>
                ))}
              </div>
              <div className="total-section">
                <h3>Current Total: ₹{getTotal()}</h3>
                <button onClick={handleSubmit} className="submit-btn">Submit and Continue</button>
              </div>
            </>
          )}
        </section>
      </main>
    </div>
  )
}

export default Shop
