import React, { useState, useEffect } from 'react';
import api from './api';
import ProfilePage from './ProfilePage';
import MembershipPanel from './MembershipPanel';

function App() {
  const [formData, setFormData] = useState({ email: '', password: '', firstName: '', lastName: '', isTrainer: false });
  const [isLogin, setIsLogin] = useState(true);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [classes, setClasses] = useState([]);
  const [myBookings, setMyBookings] = useState([]);

  const [isTrainer, setIsTrainer] = useState(false);
  const [newClass, setNewClass] = useState({ name: '', trainerId: null, dateTime: '', capacity: 20 });
  const [showProfile, setShowProfile] = useState(false);
  const [showMembership, setShowMembership] = useState(false);
  const [trainers, setTrainers] = useState([]);
  const [userId, setUserId] = useState(null);

  const fetchData = () => {
    if (token) {
      try {
        // Dekodujemy token, aby sprawdzić role
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log("Zawartość Twojego tokena:", payload);

        // Zapisz userId
        if (payload.userId) {
          setUserId(payload.userId);
        }

        if (payload.roles && payload.roles.includes('ROLE_TRAINER')) {
          setIsTrainer(true);
        }
      } catch(e) { console.error("Błąd dekodowania tokena", e); }

      api.get('/gym/classes').then(res => setClasses(res.data));
      api.get('/gym/bookings/my').then(res => setMyBookings(res.data));

      // Pobierz listę dostępnych trenerów
      api.get('/gym/classes/available-trainers')
        .then(res => {
          console.log('Trainers loaded:', res.data);
          setTrainers(res.data);
        })
        .catch(err => {
          console.error('Error loading trainers:', err);
          setTrainers([]);
        });
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
      if (!newClass.name || !newClass.trainerId || !newClass.dateTime) {
        alert('Proszę wypełnić wszystkie pola');
        return;
      }

      await api.post('/gym/classes', newClass);
      alert('Dodano nowe zajęcia!');
      setNewClass({ name: '', trainerId: null, dateTime: '', capacity: 20 });
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
    if (showProfile) {
      return <ProfilePage token={token} setToken={setToken} setShowProfile={setShowProfile} />;
    }

    if (showMembership) {
      return (
        <div>
          <div style={{ padding: '20px', background: '#f5f5f5', borderBottom: '1px solid #ddd' }}>
            <button
              onClick={() => setShowMembership(false)}
              style={{ padding: '10px 20px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
            >
              ← Powrót do Dashboard
            </button>
          </div>
          <MembershipPanel userId={userId} />
        </div>
      );
    }

    return (
      <div style={{ padding: '20px', fontFamily: 'Arial' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h1>FitFlow Dashboard</h1>
            <div style={{ display: 'flex', gap: '10px' }}>
              <button onClick={() => setShowMembership(true)} style={{ padding: '10px 20px', background: '#28a745', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>Mój Karnet</button>
              <button onClick={() => setShowProfile(true)} style={{ padding: '10px 20px', background: '#17a2b8', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>Mój Profil</button>
              <button onClick={() => { localStorage.removeItem('token'); setToken(null); setIsTrainer(false); }} style={{ padding: '10px 20px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>Wyloguj</button>
            </div>
        </div>

        {isTrainer && (
          <div style={{ background: '#fff3cd', padding: '20px', borderRadius: '10px', marginBottom: '30px', border: '1px solid #ffeeba' }}>
            <h3>Panel Trenera: Dodaj Zajęcia</h3>
            <form onSubmit={handleAddClass} style={{ display: 'flex', gap: '10px', flexWrap: 'wrap', alignItems: 'flex-start' }}>
              <input placeholder="Nazwa" value={newClass.name} onChange={e => setNewClass({...newClass, name: e.target.value})} required style={{ padding: '8px' }} />

              <select
                value={newClass.trainerId || ''}
                onChange={e => setNewClass({...newClass, trainerId: e.target.value ? parseInt(e.target.value) : null})}
                required
                style={{ padding: '8px', minWidth: '150px' }}
              >
                <option value="">-- Wybierz trenera --</option>
                {trainers.map(trainer => (
                  <option key={trainer.id} value={trainer.id}>
                    {trainer.firstName} {trainer.lastName}
                  </option>
                ))}
              </select>

              <input type="datetime-local" value={newClass.dateTime} onChange={e => setNewClass({...newClass, dateTime: e.target.value})} required style={{ padding: '8px' }} />
              <input type="number" placeholder="Miejsca" value={newClass.capacity} onChange={e => setNewClass({...newClass, capacity: parseInt(e.target.value) || 20})} required style={{ padding: '8px' }} />
              <button type="submit" style={{ background: '#856404', color: 'white', border: 'none', padding: '8px 15px', borderRadius: '5px', cursor: 'pointer' }}>Opublikuj</button>
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