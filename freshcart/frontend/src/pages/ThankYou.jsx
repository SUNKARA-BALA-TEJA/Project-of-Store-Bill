import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './ThankYou.css'

function ThankYou() {
  const [invoice, setInvoice] = useState(null)
  const navigate = useNavigate()

  useEffect(() => {
    const data = localStorage.getItem('lastInvoice')
    if (data) {
      setInvoice(JSON.parse(data))
    } else {
      navigate('/shop')
    }
  }, [])

  const printBill = () => {
    window.print()
  }

  const newBill = () => {
    localStorage.removeItem('lastInvoice')
    navigate('/shop')
  }

  if (!invoice) return null

  return (
    <div className="thankyou-container">
      <div className="thankyou-card">
        <div className="thankyou-header">
          <h1>🛒 FreshCart</h1>
        </div>
        <div className="invoice-section" id="invoice">
          <h2>INVOICE</h2>
          <div className="invoice-details">
            <p><strong>Invoice No:</strong> {invoice.invoiceNumber}</p>
            <p><strong>Date:</strong> {new Date(invoice.createdAt).toLocaleString()}</p>
            <p><strong>Customer Name:</strong> {invoice.customerName}</p>
          </div>
          <table className="invoice-table">
            <thead>
              <tr>
                <th>Product</th>
                <th>Qty</th>
                <th>Rate</th>
                <th>Amount</th>
              </tr>
            </thead>
            <tbody>
              {invoice.items.map((item, idx) => (
                <tr key={idx}>
                  <td>{item.productName}</td>
                  <td>{item.quantity} {item.unit}</td>
                  <td>₹{item.unitPrice}</td>
                  <td>₹{item.subtotal}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="invoice-total">
            <h2>FINAL AMOUNT: ₹{invoice.finalTotal}</h2>
          </div>
        </div>
        <div className="thankyou-message">
          <h2>THANK YOU, {invoice.customerName.toUpperCase()}!</h2>
          <h3>VISIT AGAIN.</h3>
        </div>
        <div className="thankyou-actions">
          <button onClick={printBill} className="print-btn">Print Bill</button>
          <button onClick={newBill} className="newbill-btn">Start New Bill</button>
        </div>
      </div>
    </div>
  )
}

export default ThankYou
