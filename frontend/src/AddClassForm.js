import React, { useState, useEffect } from 'react';
import api from './api';

function AddClassForm({ onClassAdded, setShowForm }) {
  const [formData, setFormData] = useState({
    name: '',
    trainerId: null,  // null zamiast ''
    dateTime: '',
    capacity: 20
  });

  const [trainers, setTrainers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Pobierz listę dostępnych trenerów
  useEffect(() => {
    const fetchTrainers = async () => {
      try {
        setLoading(true);
        setError(''); // Wyczyść poprzednie błędy

        console.log('=== FETCHING TRAINERS ===');
        console.log('Full URL:', window.location.origin + '/api/gym/classes/available-trainers');
        console.log('Fetching trainers from: /api/gym/classes/available-trainers');

        const response = await api.get('/gym/classes/available-trainers');

        console.log('✅ Trainers response received:', response);
        console.log('Response status:', response.status);
        console.log('Response data:', response.data);
        console.log('Data type:', typeof response.data);
        console.log('Is Array?', Array.isArray(response.data));

        if (response.data && Array.isArray(response.data)) {
          console.log('✅ Setting trainers:', response.data);
          setTrainers(response.data);
        } else if (response.data && typeof response.data === 'object') {
          console.warn('⚠️ Response is object, converting to array:', response.data);
          setTrainers(Array.isArray(response.data) ? response.data : []);
        } else {
          console.warn('❌ Unexpected response format:', response.data);
          setTrainers([]);
        }
      } catch (err) {
        console.error('❌ ERROR FETCHING TRAINERS:');
        console.error('Error object:', err);
        console.error('Error response:', err.response);
        console.error('Error message:', err.message);
        console.error('Error code:', err.code);

        let errorMsg = 'Błąd przy pobieraniu listy trenerów: ';
        if (err.response?.status === 500) {
          errorMsg += 'Serwer zwrócił błąd 500 - sprawdź czy auth-service działa na porcie 8081';
        } else if (err.response?.status === 404) {
          errorMsg += 'Endpoint nie znaleziony 404 - sprawdź czy API Gateway działa na porcie 8080';
        } else if (err.code === 'ERR_NETWORK' || err.message.includes('Network')) {
          errorMsg += 'Błąd sieci - sprawdź czy API Gateway działa na porcie 8080';
        } else if (!err.response) {
          errorMsg += 'Brak odpowiedzi z serwera - może być offline?';
        } else {
          errorMsg += err.response?.data?.message || err.response?.data || err.message;
        }

        console.error('Final error message:', errorMsg);
        setError(errorMsg);
        setTrainers([]);
      } finally {
        setLoading(false);
        console.log('=== FETCH FINISHED ===');
      }
    };

    fetchTrainers();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === 'capacity') {
      setFormData(prev => ({
        ...prev,
        [name]: parseInt(value) || 0
      }));
    } else if (name === 'trainerId') {
      // trainerId musi być Long, lub null jeśli pusty
      const trainerId = value ? parseInt(value) : null;
      setFormData(prev => ({
        ...prev,
        [name]: trainerId
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Validacja
    if (!formData.name.trim()) {
      setError('Nazwa zajęć jest wymagana');
      return;
    }

    if (!formData.trainerId || formData.trainerId === null || isNaN(formData.trainerId)) {
      setError('Wybór trenera jest wymagany');
      return;
    }

    if (!formData.dateTime) {
      setError('Data i godzina są wymagane');
      return;
    }

    if (formData.capacity < 1) {
      setError('Pojemność musi być co najmniej 1');
      return;
    }

    try {
      console.log('Sending data:', formData); // Debug log
      const response = await api.post('/gym/classes', formData);
      alert('Zajęcia dodane pomyślnie!');
      setFormData({
        name: '',
        trainerId: null,
        dateTime: '',
        capacity: 20
      });
      if (onClassAdded) onClassAdded();
      setShowForm(false);
    } catch (err) {
      setError('Błąd: ' + (err.response?.data || err.message));
      console.error('Error adding class:', err);
    }
  };

  return (
    <div className="add-class-form">
      <h3>Dodaj Nowe Zajęcia</h3>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Nazwa zajęć *</label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="np. Joga, Crossfit, Pilates"
            required
          />
        </div>

        <div className="form-group">
          <label>Wybierz trenera *</label>
          {loading ? (
            <p style={{ color: '#666', fontStyle: 'italic' }}>
              ⏳ Ładowanie listy trenerów...
            </p>
          ) : trainers && trainers.length > 0 ? (
            <select
              name="trainerId"
              value={formData.trainerId || ''}
              onChange={handleChange}
              required
            >
              <option value="">-- Wybierz trenera --</option>
              {trainers.map(trainer => (
                <option key={trainer.id} value={trainer.id}>
                  {trainer.firstName} {trainer.lastName}
                  {trainer.bio && ` (${trainer.bio})`}
                </option>
              ))}
            </select>
          ) : (
            <p className="warning">
              ⚠️ {error && error.includes('trenerów')
                ? '❌ Nie udało się pobrać listy trenerów. Sprawdź czy auth-service działa.'
                : 'Brak dostępnych trenerów. Proszę dodać trenera w systemie.'}
            </p>
          )}
        </div>

        <div className="form-group">
          <label>Data i Godzina *</label>
          <input
            type="datetime-local"
            name="dateTime"
            value={formData.dateTime}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>Pojemność (max liczba osób)</label>
          <input
            type="number"
            name="capacity"
            value={formData.capacity}
            onChange={handleChange}
            min="1"
            max="100"
          />
        </div>

        <button type="submit" className="btn-primary">
          Dodaj Zajęcia
        </button>
        <button
          type="button"
          className="btn-secondary"
          onClick={() => setShowForm(false)}
        >
          Anuluj
        </button>
      </form>

      <style jsx>{`
        .add-class-form {
          background: #f5f5f5;
          padding: 20px;
          border-radius: 8px;
          margin: 20px 0;
        }

        .add-class-form h3 {
          margin-top: 0;
          color: #333;
        }

        .form-group {
          margin-bottom: 15px;
        }

        .form-group label {
          display: block;
          margin-bottom: 5px;
          font-weight: bold;
          color: #333;
        }

        .form-group input,
        .form-group select {
          width: 100%;
          padding: 8px;
          border: 1px solid #ddd;
          border-radius: 4px;
          font-size: 14px;
          box-sizing: border-box;
        }

        .form-group input:focus,
        .form-group select:focus {
          outline: none;
          border-color: #007bff;
          box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
        }

        .error-message {
          color: #dc3545;
          background: #f8d7da;
          padding: 10px;
          border-radius: 4px;
          margin-bottom: 15px;
          border-left: 4px solid #dc3545;
        }

        .warning {
          color: #856404;
          background: #fff3cd;
          padding: 10px;
          border-radius: 4px;
          border-left: 4px solid #ffc107;
        }

        .btn-primary, .btn-secondary {
          padding: 10px 20px;
          border: none;
          border-radius: 4px;
          cursor: pointer;
          font-size: 14px;
          margin-right: 10px;
        }

        .btn-primary {
          background: #007bff;
          color: white;
        }

        .btn-primary:hover {
          background: #0056b3;
        }

        .btn-secondary {
          background: #6c757d;
          color: white;
        }

        .btn-secondary:hover {
          background: #545b62;
        }
      `}</style>
    </div>
  );
}

export default AddClassForm;
