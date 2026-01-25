import React from 'react';

function ClassesList({ classes, onBook, isTrainer, onAddClass }) {
  return (
    <div className="classes-list">
      <div className="classes-header">
        <h2>Dostępne Zajęcia</h2>
        {isTrainer && (
          <button className="btn-add" onClick={onAddClass}>
            + Dodaj Zajęcia
          </button>
        )}
      </div>

      {classes.length === 0 ? (
        <p className="no-classes">Brak dostępnych zajęć</p>
      ) : (
        <div className="classes-grid">
          {classes.map(gym_class => (
            <div key={gym_class.id} className="class-card">
              <div className="class-header">
                <h3>{gym_class.name}</h3>
                <span className="class-capacity">
                  {gym_class.availableSpots}/{gym_class.capacity} miejsc
                </span>
              </div>

              <div className="class-details">
                <div className="detail-row">
                  <span className="label">Trener:</span>
                  <span className="value">
                    {gym_class.trainerName || 'Wczytywanie...'}
                  </span>
                </div>

                <div className="detail-row">
                  <span className="label">Data i godzina:</span>
                  <span className="value">
                    {new Date(gym_class.dateTime).toLocaleString('pl-PL')}
                  </span>
                </div>

                <div className="detail-row">
                  <span className="label">Pojemność:</span>
                  <span className="value">{gym_class.capacity} osób</span>
                </div>
              </div>

              <div className="class-actions">
                {gym_class.availableSpots > 0 ? (
                  <button
                    className="btn-book"
                    onClick={() => onBook(gym_class.id)}
                  >
                    Zapisz się
                  </button>
                ) : (
                  <button className="btn-book" disabled>
                    Brak miejsc
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      <style jsx>{`
        .classes-list {
          padding: 20px;
        }

        .classes-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;
        }

        .classes-header h2 {
          margin: 0;
          color: #333;
        }

        .btn-add {
          background: #28a745;
          color: white;
          padding: 10px 20px;
          border: none;
          border-radius: 4px;
          cursor: pointer;
          font-size: 14px;
          font-weight: bold;
        }

        .btn-add:hover {
          background: #218838;
        }

        .no-classes {
          text-align: center;
          color: #666;
          padding: 40px 20px;
          font-size: 16px;
        }

        .classes-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
          gap: 20px;
        }

        .class-card {
          background: white;
          border: 1px solid #ddd;
          border-radius: 8px;
          padding: 20px;
          box-shadow: 0 2px 8px rgba(0,0,0,0.1);
          display: flex;
          flex-direction: column;
        }

        .class-header {
          display: flex;
          justify-content: space-between;
          align-items: start;
          margin-bottom: 15px;
        }

        .class-header h3 {
          margin: 0;
          color: #007bff;
          flex: 1;
        }

        .class-capacity {
          background: #e7f3ff;
          color: #0056b3;
          padding: 4px 12px;
          border-radius: 20px;
          font-size: 12px;
          font-weight: bold;
          white-space: nowrap;
          margin-left: 10px;
        }

        .class-details {
          flex: 1;
          margin-bottom: 15px;
        }

        .detail-row {
          display: flex;
          justify-content: space-between;
          margin-bottom: 8px;
          font-size: 14px;
        }

        .detail-row .label {
          font-weight: bold;
          color: #666;
        }

        .detail-row .value {
          color: #333;
        }

        .class-actions {
          display: flex;
          gap: 10px;
        }

        .btn-book {
          flex: 1;
          background: #007bff;
          color: white;
          padding: 10px;
          border: none;
          border-radius: 4px;
          cursor: pointer;
          font-size: 14px;
          font-weight: bold;
        }

        .btn-book:hover:not(:disabled) {
          background: #0056b3;
        }

        .btn-book:disabled {
          background: #ccc;
          cursor: not-allowed;
        }

        @media (max-width: 768px) {
          .classes-grid {
            grid-template-columns: 1fr;
          }

          .classes-header {
            flex-direction: column;
            align-items: flex-start;
          }

          .btn-add {
            margin-top: 10px;
          }
        }
      `}</style>
    </div>
  );
}

export default ClassesList;
