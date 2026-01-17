import React, { useState, useEffect } from 'react';
import api from './api';

function ProfilePage({ token, setToken, setShowProfile }) {
  const [profile, setProfile] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [showPasswordChange, setShowPasswordChange] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    phoneNumber: '',
    bio: '',
    preferredTrainingType: '',
    experienceLevel: '',
    trainingGoal: '',
    trainingDaysPerWeek: 0,
    notificationsEnabled: true,
  });

  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  // Pobieranie profilu przy załadowaniu
  useEffect(() => {
    fetchProfile();
  }, [token]);

  const fetchProfile = async () => {
    try {
      const res = await api.get('/auth/profile', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setProfile(res.data);
      setFormData(res.data);
    } catch (err) {
      console.error('Błąd pobierania profilu:', err);
      alert('Błąd podczas pobierania profilu');
    }
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value,
    });
  };

  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordData({
      ...passwordData,
      [name]: value,
    });
  };

  const handleSaveProfile = async (e) => {
    e.preventDefault();
    try {
      const res = await api.put('/auth/profile', formData, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      alert('Profil zaktualizowany pomyślnie!');
      setProfile(res.data);
      setIsEditing(false);
    } catch (err) {
      alert('Błąd: ' + (err.response?.data || err.message));
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      alert('Nowe hasła się nie zgadzają!');
      return;
    }
    try {
      await api.post('/auth/profile/change-password', passwordData, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      alert('Hasło zmienione pomyślnie!');
      setShowPasswordChange(false);
      setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' });
    } catch (err) {
      alert('Błąd: ' + (err.response?.data || err.message));
    }
  };

  const handleDeleteAccount = async () => {
    if (!window.confirm('Czy na pewno chcesz usunąć konto? Ta akcja jest nieodwracalna!')) {
      return;
    }
    try {
      await api.delete('/auth/profile', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      alert('Konto usunięte pomyślnie!');
      localStorage.removeItem('token');
      setToken(null);
      setShowProfile(false);
    } catch (err) {
      alert('Błąd: ' + (err.response?.data || err.message));
    }
  };

  if (!profile) {
    return <div style={{ padding: '20px', textAlign: 'center' }}>Ładowanie profilu...</div>;
  }

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial', maxWidth: '800px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
        <h1>Mój Profil</h1>
        <button onClick={() => setShowProfile(false)} style={{ padding: '10px 20px', background: '#666', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>
          Powrót
        </button>
      </div>

      {!isEditing && !showPasswordChange ? (
        // Widok profilu
        <div style={{ background: '#f5f5f5', padding: '20px', borderRadius: '10px', marginBottom: '20px' }}>
          <div style={{ marginBottom: '15px' }}>
            <strong>Email:</strong> {profile.email}
          </div>
          <div style={{ marginBottom: '15px' }}>
            <strong>Imię:</strong> {profile.firstName || 'Nie podane'}
          </div>
          <div style={{ marginBottom: '15px' }}>
            <strong>Nazwisko:</strong> {profile.lastName || 'Nie podane'}
          </div>
          <div style={{ marginBottom: '15px' }}>
            <strong>Numer telefonu:</strong> {profile.phoneNumber || 'Nie podane'}
          </div>
          <div style={{ marginBottom: '15px' }}>
            <strong>O mnie:</strong> {profile.bio || 'Nie podano opisu'}
          </div>

          <h3 style={{ marginTop: '30px', marginBottom: '15px', borderBottom: '2px solid #007bff', paddingBottom: '10px' }}>
            Preferencje Treningowe
          </h3>
          <div style={{ marginBottom: '15px' }}>
            <strong>Rodzaj treningu:</strong> {profile.preferredTrainingType || 'Nie wybrano'}
          </div>
          <div style={{ marginBottom: '15px' }}>
            <strong>Poziom doświadczenia:</strong> {profile.experienceLevel || 'Nie wybrano'}
          </div>
          <div style={{ marginBottom: '15px' }}>
            <strong>Cel treningu:</strong> {profile.trainingGoal || 'Nie wybrano'}
          </div>
          <div style={{ marginBottom: '15px' }}>
            <strong>Dni treningu na tydzień:</strong> {profile.trainingDaysPerWeek || 0}
          </div>
          <div style={{ marginBottom: '15px' }}>
            <strong>Powiadomienia:</strong> {profile.notificationsEnabled ? 'Włączone' : 'Wyłączone'}
          </div>

          <div style={{ display: 'flex', gap: '10px', marginTop: '30px' }}>
            <button
              onClick={() => setIsEditing(true)}
              style={{ flex: 1, padding: '10px', background: '#007bff', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
            >
              Edytuj Profil
            </button>
            <button
              onClick={() => setShowPasswordChange(true)}
              style={{ flex: 1, padding: '10px', background: '#28a745', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
            >
              Zmień Hasło
            </button>
            <button
              onClick={handleDeleteAccount}
              style={{ flex: 1, padding: '10px', background: '#dc3545', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
            >
              Usuń Konto
            </button>
          </div>
        </div>
      ) : isEditing ? (
        // Formularz edycji profilu
        <form onSubmit={handleSaveProfile} style={{ background: '#f5f5f5', padding: '20px', borderRadius: '10px', marginBottom: '20px' }}>
          <h3>Edytuj Profil</h3>

          <div style={{ marginBottom: '15px' }}>
            <label>Imię:</label>
            <input
              type="text"
              name="firstName"
              value={formData.firstName}
              onChange={handleInputChange}
              style={{ display: 'block', width: '100%', padding: '10px', marginTop: '5px', borderRadius: '5px', border: '1px solid #ddd' }}
            />
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label>Nazwisko:</label>
            <input
              type="text"
              name="lastName"
              value={formData.lastName}
              onChange={handleInputChange}
              style={{ display: 'block', width: '100%', padding: '10px', marginTop: '5px', borderRadius: '5px', border: '1px solid #ddd' }}
            />
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label>Numer telefonu:</label>
            <input
              type="tel"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleInputChange}
              style={{ display: 'block', width: '100%', padding: '10px', marginTop: '5px', borderRadius: '5px', border: '1px solid #ddd' }}
            />
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label>O mnie:</label>
            <textarea
              name="bio"
              value={formData.bio}
              onChange={handleInputChange}
              rows="4"
              style={{ display: 'block', width: '100%', padding: '10px', marginTop: '5px', borderRadius: '5px', border: '1px solid #ddd', fontFamily: 'Arial' }}
            />
          </div>

          <h3 style={{ marginTop: '25px', marginBottom: '15px' }}>Preferencje Treningowe</h3>

          <div style={{ marginBottom: '15px' }}>
            <label>Rodzaj treningu:</label>
            <select
              name="preferredTrainingType"
              value={formData.preferredTrainingType}
              onChange={handleInputChange}
              style={{ display: 'block', width: '100%', padding: '10px', marginTop: '5px', borderRadius: '5px', border: '1px solid #ddd' }}
            >
              <option value="">Wybierz rodzaj treningu</option>
              <option value="Cardio">Cardio</option>
              <option value="Strength">Siła</option>
              <option value="Yoga">Joga</option>
              <option value="Mixed">Mieszane</option>
              <option value="Pilates">Pilates</option>
            </select>
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label>Poziom doświadczenia:</label>
            <select
              name="experienceLevel"
              value={formData.experienceLevel}
              onChange={handleInputChange}
              style={{ display: 'block', width: '100%', padding: '10px', marginTop: '5px', borderRadius: '5px', border: '1px solid #ddd' }}
            >
              <option value="">Wybierz poziom</option>
              <option value="Beginner">Początkujący</option>
              <option value="Intermediate">Średniozaawansowany</option>
              <option value="Advanced">Zaawansowany</option>
            </select>
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label>Cel treningu:</label>
            <select
              name="trainingGoal"
              value={formData.trainingGoal}
              onChange={handleInputChange}
              style={{ display: 'block', width: '100%', padding: '10px', marginTop: '5px', borderRadius: '5px', border: '1px solid #ddd' }}
            >
              <option value="">Wybierz cel</option>
              <option value="Weight Loss">Utrata wagi</option>
              <option value="Muscle Gain">Budowa masy</option>
              <option value="Flexibility">Elastyczność</option>
              <option value="General Fitness">Ogólna sprawność</option>
              <option value="Endurance">Wytrzymałość</option>
            </select>
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label>Ile dni w tygodniu chcesz trenować:</label>
            <input
              type="number"
              name="trainingDaysPerWeek"
              min="0"
              max="7"
              value={formData.trainingDaysPerWeek}
              onChange={handleInputChange}
              style={{ display: 'block', width: '100%', padding: '10px', marginTop: '5px', borderRadius: '5px', border: '1px solid #ddd' }}
            />
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label>
              <input
                type="checkbox"
                name="notificationsEnabled"
                checked={formData.notificationsEnabled}
                onChange={handleInputChange}
              />
              {' '}Otrzymuj powiadomienia o nowych zajęciach
            </label>
          </div>

          <div style={{ display: 'flex', gap: '10px' }}>
            <button
              type="submit"
              style={{ flex: 1, padding: '10px', background: '#28a745', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
            >
              Zapisz zmiany
            </button>
            <button
              type="button"
              onClick={() => setIsEditing(false)}
              style={{ flex: 1, padding: '10px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
            >
              Anuluj
            </button>
          </div>
        </form>
      ) : showPasswordChange ? (
        // Formularz zmiany hasła
        <form onSubmit={handleChangePassword} style={{ background: '#f5f5f5', padding: '20px', borderRadius: '10px', marginBottom: '20px' }}>
          <h3>Zmiana Hasła</h3>

          <div style={{ marginBottom: '15px' }}>
            <label>Obecne hasło:</label>
            <input
              type="password"
              name="currentPassword"
              value={passwordData.currentPassword}
              onChange={handlePasswordChange}
              required
              style={{ display: 'block', width: '100%', padding: '10px', marginTop: '5px', borderRadius: '5px', border: '1px solid #ddd' }}
            />
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label>Nowe hasło:</label>
            <input
              type="password"
              name="newPassword"
              value={passwordData.newPassword}
              onChange={handlePasswordChange}
              required
              style={{ display: 'block', width: '100%', padding: '10px', marginTop: '5px', borderRadius: '5px', border: '1px solid #ddd' }}
            />
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label>Powtórz nowe hasło:</label>
            <input
              type="password"
              name="confirmPassword"
              value={passwordData.confirmPassword}
              onChange={handlePasswordChange}
              required
              style={{ display: 'block', width: '100%', padding: '10px', marginTop: '5px', borderRadius: '5px', border: '1px solid #ddd' }}
            />
          </div>

          <div style={{ display: 'flex', gap: '10px' }}>
            <button
              type="submit"
              style={{ flex: 1, padding: '10px', background: '#28a745', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
            >
              Zmień hasło
            </button>
            <button
              type="button"
              onClick={() => setShowPasswordChange(false)}
              style={{ flex: 1, padding: '10px', background: '#6c757d', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
            >
              Anuluj
            </button>
          </div>
        </form>
      ) : null}
    </div>
  );
}

export default ProfilePage;
