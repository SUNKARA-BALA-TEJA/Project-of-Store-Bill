import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api'
import './BillPage.css'

function BillPage() {
  const [items, setItems] = useState([])
  const [customerName, setCustomerName] = useState('')
  const [showBill, setShowBill] = useState(false)
  const [showAddItem, setShowAddItem] = useState(false)
  const [products, setProducts] = useState([])
  const [search, setSearch] = useState('')
  const [addQty, setAddQty] = useState({})
  const navigate = useNavigate()
  const username = localStorage.getItem('username')
  const role = localStorage.getItem('role')

  useEffect(() => {
    const draft = localStorage.getItem('billDraft')
    if (draft) {
      setItems(JSON.parse(draft))
    } else {
      navigate('/shop')
    }
    fetchProducts()
  }, [])

  const fetchProducts = async (query = '') => {
    try {
      const res = await api.get(`/products?search=${query}`)
      setProducts(res.data)
    } catch (err) {
      console.error(err)
    }
  }

  const handleNext = () => {
    if (!customerName.trim()) {
      alert('Enter customer name')
      return
    }
    setShowBill(true)
  }

  const updateQuantity = (idx, delta) => {
    const newItems = [...items]
    let newQty = parseFloat(newItems[idx].quantity) + delta
    if (newQty < 0.001) return
    if (newQty > parseFloat(newItems[idx].product.stock)) {
      alert('Quantity exceeds available stock')
      return
    }
    newItems[idx].quantity = newQty.toString()
    setItems(newItems)
    localStorage.setItem('billDraft', JSON.stringify(newItems))
  }

  const removeItem = (idx) => {
    const newItems = items.filter((_, i) => i !== idx)
    setItems(newItems)
    localStorage.setItem('billDraft', JSON.stringify(newItems))
    if (newItems.length === 0) {
      alert('Bill cannot be empty')
      navigate('/shop')
    }
  }

  const addNewItem = (product) => {
    const qty = parseFloat(addQty[product.id] || 0)
    if (!qty || qty <= 0) {
      alert('Enter valid quantity')
      return
    }
    if (qty > parseFloat(product.stock)) {
      alert('Quantity exceeds available stock')
      return
    }
    const existingIdx = items.findIndex(item => item.product.id === product.id)
    if (existingIdx >= 0) {
      const newItems = [...items]
      newItems[existingIdx].quantity = (parseFloat(newItems[existingIdx].quantity) + qty).toString()
      setItems(newItems)
      localStorage.setItem('billDraft', JSON.stringify(newItems))
    } else {
      const newItems = [...items, { product, quantity: qty.toString() }]
      setItems(newItems)
      localStorage.setItem('billDraft', JSON.stringify(newItems))
    }
    setShowAddItem(false)
    setAddQty({})
  }

  const getSubtotal = () => {
    return items.reduce((sum, item) => {
      return sum + parseFloat(item.product.currentSellingPrice) * parseFloat(item.quantity)
    }, 0)
  }

  const confirmBill = async () => {
    try {
      const billItems = items.map(item => ({
        productId: item.product.id,
        quantity: parseFloat(item.quantity)
      }))
      const res = await api.post('/bills', {
        customerName,
        items: billItems,
        charges: 0,
        discount: 0
      })
      localStorage.setItem('lastInvoice', JSON.stringify(res.data))
      localStorage.removeItem('billDraft')
      navigate('/thankyou')
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to create bill')
    }
  }

  const logout = () => {
    localStorage.clear()
    navigate('/login')
  }

  return (
    <div className="bill-container">
      <header className="bill-header">
        <div className="logo">🛒 FreshCart</div>
        <div className="header-right">
          <span>👤 {username}</span>
          <button onClick={() => navigate('/settings')} className="icon-btn">⚙️ Settings</button>
          <button onClick={logout} className="icon-btn">Logout</button>
        </div>
      </header>

      <main className="bill-main">
        <section className="customer-section">
          <h2>Customer Information</h2>
          {!showBill ? (
            <div className="customer-form">
              <label>Customer Name:</label>
              <input type="text" value={customerName} onChange={(e) => setCustomerName(e.target.value)} />
              <button onClick={handleNext}>Next</button>
            </div>
          ) : (
            <div className="customer-display">
              <strong>Customer:</strong> {customerName}
            </div>
          )}
        </section>

        {showBill && (
          <>
            <section className="bill-table-section">
              <h2>Bill Details</h2>
              <table className="bill-table">
                <thead>
                  <tr>
                    <th>Product</th>
                    <th>Quantity</th>
                    <th>Unit Price</th>
                    <th>Amount</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {items.map((item, idx) => (
                    <tr key={idx}>
                      <td className="product-cell">
                        <img 
                          src={item.product.imagePath ? `http://localhost:8080${item.product.imagePath}` : 'https://via.placeholder.com/60'} 
                          alt={item.product.name}
                        />
                        <span>{item.product.name}</span>
                      </td>
                      <td>
                        <div className="qty-controls">
                          <button onClick={() => updateQuantity(idx, -0.5)}>-</button>
                          <span>{item.quantity} {item.product.unit}</span>
                          <button onClick={() => updateQuantity(idx, 0.5)}>+</button>
                        </div>
                      </td>
                      <td>₹{item.product.currentSellingPrice}/{item.product.unit}</td>
                      <td>₹{(parseFloat(item.product.currentSellingPrice) * parseFloat(item.quantity)).toFixed(2)}</td>
                      <td><button onClick={() => removeItem(idx)} className="remove-btn">Remove</button></td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <button onClick={() => setShowAddItem(true)} className="add-another-btn">+ Add Another Item</button>
            </section>

            <section className="bill-summary">
              <div className="summary-row">
                <span>Number of distinct products:</span>
                <span>{items.length}</span>
              </div>
              <div className="summary-row total">
                <span>Final Amount:</span>
                <span>₹{getSubtotal().toFixed(2)}</span>
              </div>
            </section>

            <div className="bill-actions">
              <button onClick={() => navigate('/shop')} className="back-btn">Back to Shopping</button>
              <button onClick={confirmBill} className="confirm-btn">Confirm Final Bill</button>
            </div>
          </>
        )}
      </main>

      {showAddItem && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3>Add Another Item</h3>
              <button onClick={() => setShowAddItem(false)}>✕</button>
            </div>
            <input 
              type="text" 
              placeholder="Search products..." 
              value={search}
              onChange={(e) => { setSearch(e.target.value); fetchProducts(e.target.value) }}
              className="modal-search"
            />
            <div className="modal-products">
              {products.map(product => (
                <div key={product.id} className="modal-product">
                  <img src={product.imagePath ? `http://localhost:8080${product.imagePath}` : 'https://via.placeholder.com/60'} alt={product.name} />
                  <div className="modal-product-info">
                    <h4>{product.name}</h4>
                    <p>₹{product.currentSellingPrice}/{product.unit} | Stock: {product.stock}</p>
                  </div>
                  <input 
                    type="number" 
                    step="0.001"
                    placeholder="Qty"
                    value={addQty[product.id] || ''}
                    onChange={(e) => setAddQty({...addQty, [product.id]: e.target.value})}
                  />
                  <button onClick={() => addNewItem(product)}>Add to Bill</button>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default BillPage
