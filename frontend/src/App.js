import React, { useState, useEffect } from 'react';
import api from './api';

function App() {
  const [formData, setFormData] = useState({ email: '', password: '', firstName: '', lastName: '' });
  const [isLogin, setIsLogin] = useState(true);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [classes, setClasses] = useState([]);
  const [message, setMessage] = useState('');
  const [myUserId, setMyUserId] = useState(1); // Tymczasowe ID dla testu zapisu

  const fetchClasses = async () => {
    try {
      const res = await api.get('/gym/classes');
      setClasses(res.data);
    } catch (err) {
      console.error("Błąd pobierania zajęć", err);
    }
  };

  useEffect(() => {
    if (token) fetchClasses();
  }, [token]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isLogin) {
        const res = await api.post('/auth/login', { email: formData.email, password: formData.password });
        localStorage.setItem('token', res.data);
        setToken(res.data);
        setMessage('Zalogowano!');
      } else {
        await api.post('/auth/register', formData);
        setMessage('Zarejestrowano! Zaloguj się.');
        setIsLogin(true);
      }
    } catch (err) {
      setMessage('Błąd: ' + (err.response?.data || err.message));
    }
  };

  const handleBook = async (classId) => {
    try {
      await api.post('/gym/bookings', {
        userId: myUserId, // Twoje ID z bazy (możesz sprawdzić w fitflow-auth-db)
        gymClassId: classId
      });
      alert('Zapisano na zajęcia!');
    } catch (err) {
      alert('Błąd zapisu: ' + (err.response?.data || err.message));
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setClasses([]);
  };

  if (token) {
    return (
      <div style={{ padding: '40px', fontFamily: 'Arial' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <h1>Dashboard FitFlow</h1>
            <button onClick={logout}>Wyloguj</button>
        </div>

        <div style={{ background: '#f0f0f0', padding: '10px', marginBottom: '20px' }}>
            <label>Moje User ID (z bazy auth): </label>
            <input type="number" value={myUserId} onChange={e => setMyUserId(e.target.value)} />
            <small> (Potrzebne do testu zapisu)</small>
        </div>

        <hr />
        <h3>Aktualny Grafik Zajęć</h3>
        <div style={{ display: 'grid', gap: '15px' }}>
          {classes.length === 0 ? <p>Brak zajęć w grafiku.</p> : classes.map((item) => (
            <div key={item.id} style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '8px' }}>
              <strong>{item.name}</strong><br/>
              <span>Trener: {item.trainerName}</span><br/>
              <span>Data: {new Date(item.dateTime).toLocaleString()}</span><br/>
              <span>Miejsca: {item.capacity}</span><br/>
              <button
                onClick={() => handleBook(item.id)}
                style={{ marginTop: '10px', background: '#28a745', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '4px', cursor: 'pointer' }}
              >
                Zapisz się
              </button>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div style={{ padding: '40px', fontFamily: 'Arial' }}>
      <h1>FitFlow System</h1>
      <h3>{isLogin ? 'Logowanie' : 'Rejestracja'}</h3>
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', width: '300px', gap: '10px' }}>
        {!isLogin && (
          <>
            <input placeholder="Imię" onChange={e => setFormData({...formData, firstName: e.target.value})} />
            <input placeholder="Nazwisko" onChange={e => setFormData({...formData, lastName: e.target.value})} />
          </>
        )}
        <input placeholder="Email" type="email" onChange={e => setFormData({...formData, email: e.target.value})} />
        <input placeholder="Hasło" type="password" onChange={e => setFormData({...formData, password: e.target.value})} />
        <button type="submit">{isLogin ? 'Zaloguj' : 'Zarejestruj'}</button>
      </form>
      <button onClick={() => setIsLogin(!isLogin)} style={{ marginTop: '20px' }}>
        {isLogin ? 'Nie masz konta? Zarejestruj się' : 'Masz już konto? Zaloguj się'}
      </button>
      <p>{message}</p>
    </div>
  );
}

export default App;