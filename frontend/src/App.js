import React, { useState, useEffect } from 'react';
import api from './api';

function App() {
  const [formData, setFormData] = useState({ email: '', password: '', firstName: '', lastName: '', isTrainer: false });
  const [isLogin, setIsLogin] = useState(true);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [classes, setClasses] = useState([]);
  const [myBookings, setMyBookings] = useState([]);

  const [isTrainer, setIsTrainer] = useState(false);
  const [newClass, setNewClass] = useState({ name: '', trainerName: '', dateTime: '', capacity: 20 });

  const fetchData = () => {
    if (token) {
      try {
        // Dekodujemy token, aby sprawdzić role
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log("Zawartość Twojego tokena:", payload);
        if (payload.roles && payload.roles.includes('ROLE_TRAINER')) {
          setIsTrainer(true);
        }
      } catch(e) { console.error("Błąd dekodowania tokena", e); }

      api.get('/gym/classes').then(res => setClasses(res.data));
      api.get('/gym/bookings/my').then(res => setMyBookings(res.data));
    }
  };

  useEffect(() => {
    fetchData();
  }, [token]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isLogin) {
        const res = await api.post('/auth/login', { email: formData.email, password: formData.password });
        localStorage.setItem('token', res.data);
        setToken(res.data);
      } else {
        // Rejestracja z opcją isTrainer
        await api.post('/auth/register', formData);
        setIsLogin(true);
        alert('Zarejestrowano pomyślnie! Teraz się zaloguj.');
      }
    } catch (err) { alert('Błąd: ' + (err.response?.data || err.message)); }
  };

  const handleAddClass = async (e) => {
    e.preventDefault();
    try {
      await api.post('/gym/classes', newClass);
      alert('Dodano nowe zajęcia!');
      setNewClass({ name: '', trainerName: '', dateTime: '', capacity: 20 });
      fetchData(); // Odśwież listę
    } catch (err) { alert('Błąd: ' + (err.response?.data || err.message)); }
  };

  const handleBook = async (classId) => {
    try {
      await api.post('/gym/bookings', { gymClassId: classId });
      alert('Zapisano!');
      fetchData();
    } catch (err) { alert(err.response?.data || err.message); }
  };

  const handleCancel = async (bookingId) => {
    try {
      await api.delete(`/gym/bookings/${bookingId}`);
      alert('Odwołano!');
      fetchData();
    } catch (err) { alert(err.response?.data || err.message); }
  };

  if (token) {
    return (
      <div style={{ padding: '20px', fontFamily: 'Arial' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <h1>FitFlow Dashboard</h1>
            <button onClick={() => { localStorage.removeItem('token'); setToken(null); setIsTrainer(false); }}>Wyloguj</button>
        </div>

        {isTrainer && (
          <div style={{ background: '#fff3cd', padding: '20px', borderRadius: '10px', marginBottom: '30px', border: '1px solid #ffeeba' }}>
            <h3>Panel Trenera: Dodaj Zajęcia</h3>
            <form onSubmit={handleAddClass} style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
              <input placeholder="Nazwa" value={newClass.name} onChange={e => setNewClass({...newClass, name: e.target.value})} required />
              <input placeholder="Trener" value={newClass.trainerName} onChange={e => setNewClass({...newClass, trainerName: e.target.value})} required />
              <input type="datetime-local" value={newClass.dateTime} onChange={e => setNewClass({...newClass, dateTime: e.target.value})} required />
              <input type="number" placeholder="Miejsca" value={newClass.capacity} onChange={e => setNewClass({...newClass, capacity: e.target.value})} required />
              <button type="submit" style={{ background: '#856404', color: 'white', border: 'none', padding: '5px 15px', borderRadius: '5px', cursor: 'pointer' }}>Opublikuj</button>
            </form>
          </div>
        )}

        <h3 style={{marginTop: '30px'}}>Twoje Rezerwacje</h3>
        <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
          {myBookings.length === 0 ? <p>Brak rezerwacji.</p> : myBookings.map(b => (
            <div key={b.id} style={{ background: '#e3f2fd', padding: '10px', borderRadius: '5px', border: '1px solid #90caf9' }}>
              <strong>{b.gymClass.name}</strong><br/>
              <button onClick={() => handleCancel(b.id)} style={{ color: 'red', border: 'none', background: 'none', cursor: 'pointer', fontSize: '12px' }}>Wypisz się</button>
            </div>
          ))}
        </div>

        <h3 style={{marginTop: '30px'}}>Dostępne Zajęcia</h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '20px' }}>
          {classes.map(c => (
            <div key={c.id} style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '10px' }}>
              <h4>{c.name}</h4>
              <p>Trener: {c.trainerName}<br/>Dostępne miejsca: {c.availableSpots}</p>
              <button onClick={() => handleBook(c.id)} disabled={c.availableSpots <= 0} style={{ background: c.availableSpots <= 0 ? '#ccc' : '#4caf50', color: 'white', padding: '10px', width: '100%', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>
                {c.availableSpots <= 0 ? 'Brak miejsc' : 'Zapisz się'}
              </button>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div style={{ padding: '50px', textAlign: 'center' }}>
      <h2>FitFlow System</h2>
      <form onSubmit={handleSubmit} style={{ display: 'inline-block', textAlign: 'left', border: '1px solid #ccc', padding: '30px', borderRadius: '10px' }}>
        <h3 style={{marginTop: 0}}>{isLogin ? 'Logowanie' : 'Rejestracja'}</h3>
        {!isLogin && (
            <>
                <input placeholder="Imię" style={{display: 'block', marginBottom: '10px', width: '250px'}} onChange={e => setFormData({...formData, firstName: e.target.value})} />
                <input placeholder="Nazwisko" style={{display: 'block', marginBottom: '10px', width: '250px'}} onChange={e => setFormData({...formData, lastName: e.target.value})} />
                <label style={{fontSize: '12px', display: 'block', marginBottom: '10px'}}>
                    <input type="checkbox" onChange={e => setFormData({...formData, isTrainer: e.target.checked})} />
                    Zarejestruj jako Trener
                </label>
            </>
        )}
        <input placeholder="Email" style={{display: 'block', marginBottom: '10px', width: '250px'}} onChange={e => setFormData({...formData, email: e.target.value})} />
        <input placeholder="Hasło" type="password" style={{display: 'block', marginBottom: '10px', width: '250px'}} onChange={e => setFormData({...formData, password: e.target.value})} />
        <button type="submit" style={{width: '100%', padding: '10px', background: '#007bff', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer'}}>{isLogin ? 'Zaloguj' : 'Zarejestruj'}</button>
        <button type="button" onClick={() => setIsLogin(!isLogin)} style={{background: 'none', border: 'none', color: 'blue', textDecoration: 'underline', cursor: 'pointer', marginTop: '15px', width: '100%'}}>{isLogin ? 'Nie masz konta? Zarejestruj się' : 'Masz już konto? Zaloguj'}</button>
      </form>
    </div>
  );
}

export default App;