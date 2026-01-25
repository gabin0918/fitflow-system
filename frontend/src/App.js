import React, { useState, useEffect } from 'react';
import api from './api';
import ProfilePage from './ProfilePage';
import MembershipPanel from './MembershipPanel';

function App() {
  const [formData, setFormData] = useState({
    email: '', password: '', firstName: '', lastName: '', isTrainer: false, adminCode: ''
  });
  const [isLogin, setIsLogin] = useState(true);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [classes, setClasses] = useState([]);
  const [myBookings, setMyBookings] = useState([]);
  const [isTrainer, setIsTrainer] = useState(false);
  const [newClass, setNewClass] = useState({ name: '', trainerId: '', dateTime: '', capacity: 20 });
  const [showProfile, setShowProfile] = useState(false);
  const [showMembership, setShowMembership] = useState(false);
  const [trainers, setTrainers] = useState([]); // Lista trenerów do wyboru
  const [userId, setUserId] = useState(null);

  const fetchData = async () => {
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        if (payload.userId) setUserId(payload.userId);
        if (payload.roles && (payload.roles.includes('ROLE_TRAINER') || payload.roles.includes('TRAINER'))) {
          setIsTrainer(true);
          // Jeśli jest trenerem, pobierz listę trenerów do formularza
          api.get('/gym/classes/available-trainers').then(res => setTrainers(res.data)).catch(e => console.log("Brak trenerów"));
        }
      } catch(e) { console.error("Błąd tokena", e); }

      api.get('/gym/classes').then(res => setClasses(res.data));
      api.get('/gym/bookings/my').then(res => setMyBookings(res.data));
    }
  };

  useEffect(() => { fetchData(); }, [token]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isLogin) {
        const res = await api.post('/auth/login', { email: formData.email, password: formData.password });
        localStorage.setItem('token', res.data);
        setToken(res.data);
      } else {
        await api.post('/auth/register', formData);
        setIsLogin(true);
        alert('Zarejestrowano pomyślnie! Teraz możesz się zalogować.');
      }
    } catch (err) { alert('Błąd: ' + (err.response?.data || err.message)); }
  };

  const handleAddClass = async (e) => {
    e.preventDefault();
    try {
      await api.post('/gym/classes', newClass);
      alert('Dodano nowe zajęcia!');
      setNewClass({ name: '', trainerId: '', dateTime: '', capacity: 20 });
      fetchData();
    } catch (err) { alert('Błąd: ' + (err.response?.data || err.message)); }
  };

  const handleBook = async (classId) => {
    try {
      await api.post('/gym/bookings', { gymClassId: classId });
      alert('Zapisano na zajęcia!');
      fetchData();
    } catch (err) { alert('Błąd zapisu: ' + (err.response?.data || err.message)); }
  };

  const handleCancel = async (bookingId) => {
    try {
      await api.delete(`/gym/bookings/${bookingId}`);
      alert('Odwołano rezerwację');
      fetchData();
    } catch (err) { alert('Błąd: ' + (err.response?.data || err.message)); }
  };

  if (token) {
    if (showProfile) return <ProfilePage token={token} setToken={setToken} setShowProfile={setShowProfile} />;
    if (showMembership) return (
        <div style={{fontFamily: 'Arial'}}>
          <div style={{ padding: '20px', background: '#f5f5f5', borderBottom: '1px solid #ddd' }}>
            <button onClick={() => setShowMembership(false)} style={{ padding: '10px 20px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>
              ← Powrót do Dashboard
            </button>
          </div>
          <MembershipPanel userId={userId} />
        </div>
    );

    return (
      <div style={{ padding: '40px', fontFamily: 'Arial', backgroundColor: '#f9f9f9', minHeight: '100vh' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h1>FitFlow Dashboard</h1>
            <div style={{ display: 'flex', gap: '10px' }}>
              <button onClick={() => setShowMembership(true)} style={{ padding: '10px 20px', background: '#28a745', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>Mój Karnet</button>
              <button onClick={() => setShowProfile(true)} style={{ padding: '10px 20px', background: '#17a2b8', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>Mój Profil</button>
              <button onClick={() => { localStorage.clear(); window.location.reload(); }} style={{ padding: '10px 20px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>Wyloguj</button>
            </div>
        </div>

        <hr />

        {isTrainer && (
          <div style={{ background: '#fff3cd', padding: '20px', borderRadius: '10px', marginBottom: '30px', border: '1px solid #ffeeba' }}>
            <h3>Panel Trenera: Dodaj Zajęcia</h3>
            <form onSubmit={handleAddClass} style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
              <input placeholder="Nazwa zajęć" style={{padding:'8px'}} value={newClass.name} onChange={e => setNewClass({...newClass, name: e.target.value})} required />

              <select
                style={{padding:'8px'}}
                value={newClass.trainerId}
                onChange={e => setNewClass({...newClass, trainerId: e.target.value})}
                required
              >
                <option value="">Wybierz trenera</option>
                {trainers.map(t => <option key={t.id} value={t.id}>{t.firstName} {t.lastName}</option>)}
              </select>

              <input type="datetime-local" style={{padding:'8px'}} value={newClass.dateTime} onChange={e => setNewClass({...newClass, dateTime: e.target.value})} required />
              <button type="submit" style={{ background: '#856404', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '5px', cursor: 'pointer' }}>Opublikuj</button>
            </form>
          </div>
        )}

        <section style={{ marginBottom: '40px' }}>
            <h3 style={{ color: '#007bff' }}>Twoje Rezerwacje</h3>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '15px' }}>
                {myBookings.length === 0 ? <p>Brak aktywnych rezerwacji.</p> : myBookings.map((book) => (
                    <div key={book.id} style={{ background: '#e7f3ff', padding: '15px', borderRadius: '8px', border: '1px solid #b3d7ff', minWidth: '200px' }}>
                        <h4 style={{ margin: '0 0 5px 0' }}>{book.gymClass.name}</h4>
                        <small>Data: {new Date(book.gymClass.dateTime).toLocaleString()}</small><br/>
                        <button onClick={() => handleCancel(book.id)} style={{ marginTop: '10px', background: '#ff4d4d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', padding: '5px 10px' }}>Wypisz się</button>
                    </div>
                ))}
            </div>
        </section>

        <hr />

        <h3>Dostępne Zajęcia (Grafik)</h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '20px' }}>
          {classes.map((item) => (
            <div key={item.id} style={{ border: '1px solid #ddd', padding: '20px', borderRadius: '10px', backgroundColor: 'white', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
              <h2 style={{ margin: '0 0 10px 0', color: '#333' }}>{item.name}</h2>
              <p><strong>Trener:</strong> {item.trainerName || "Ładowanie..."}</p>
              <p><strong>Data:</strong> {new Date(item.dateTime).toLocaleString()}</p>
              <p><strong>Dostępne miejsca:</strong> {item.availableSpots}</p>
              <button
                onClick={() => handleBook(item.id)}
                disabled={item.availableSpots <= 0}
                style={{ width: '100%', background: item.availableSpots <= 0 ? '#ccc' : '#28a745', color: 'white', border: 'none', padding: '10px', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}
              >
                {item.availableSpots <= 0 ? 'Brak miejsc' : 'Zapisz się'}
              </button>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div style={{ padding: '40px', fontFamily: 'Arial', maxWidth: '400px', margin: 'auto' }}>
      <h1 style={{textAlign: 'center'}}>FitFlow System</h1>
      <div style={{ border: '1px solid #ccc', padding: '30px', borderRadius: '10px', backgroundColor: 'white', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }}>
        <h3 style={{marginTop: 0, textAlign: 'center'}}>{isLogin ? 'Logowanie' : 'Rejestracja'}</h3>
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
            {!isLogin && (
            <>
                <input placeholder="Imię" style={{ padding: '10px' }} onChange={e => setFormData({...formData, firstName: e.target.value})} required />
                <input placeholder="Nazwisko" style={{ padding: '10px' }} onChange={e => setFormData({...formData, lastName: e.target.value})} required />
                <label style={{fontSize: '14px'}}>
                    <input type="checkbox" onChange={e => setFormData({...formData, isTrainer: e.target.checked})} /> Zarejestruj jako Trener
                </label>
                {formData.isTrainer && (
                    <input
                        placeholder="Kod autoryzacji trenera"
                        type="password"
                        style={{ padding: '10px', border: '2px solid #ffc107', borderRadius: '4px' }}
                        onChange={e => setFormData({...formData, adminCode: e.target.value})}
                        required
                    />
                )}
            </>
            )}
            <input placeholder="Email" type="email" style={{ padding: '10px' }} onChange={e => setFormData({...formData, email: e.target.value})} required />
            <input placeholder="Hasło" type="password" style={{ padding: '10px' }} onChange={e => setFormData({...formData, password: e.target.value})} required />
            <button type="submit" style={{ padding: '12px', background: '#007bff', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}>
                {isLogin ? 'Zaloguj' : 'Zarejestruj'}
            </button>
        </form>
        <button onClick={() => setIsLogin(!isLogin)} style={{ marginTop: '20px', background: 'none', border: 'none', color: '#007bff', cursor: 'pointer', textDecoration: 'underline', width: '100%', textAlign: 'center' }}>
            {isLogin ? 'Nie masz konta? Zarejestruj się' : 'Masz już konto? Zaloguj się'}
        </button>
      </div>
    </div>
  );
}

export default App;