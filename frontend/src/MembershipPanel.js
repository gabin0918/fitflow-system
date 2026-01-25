import React, { useState, useEffect } from 'react';
import { membershipAPI } from './api';
import './MembershipPanel.css';

const MembershipPanel = ({ userId }) => {
    const [plans, setPlans] = useState([]);
    const [userMemberships, setUserMemberships] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);

    useEffect(() => {
        loadData();
    }, [userId]);

    const loadData = async () => {
        try {
            setLoading(true);
            const [plansRes, membershipsRes] = await Promise.all([
                membershipAPI.getPlans(),
                membershipAPI.getUserMemberships(userId)
            ]);
            setPlans(plansRes.data);
            setUserMemberships(membershipsRes.data);
            setError(null);
        } catch (err) {
            setError('Nie udało się załadować danych: ' + err.message);
        } finally {
            setLoading(false);
        }
    };

    const handlePurchase = async (planId) => {
        try {
            await membershipAPI.purchaseMembership({
                userId: userId,
                planId: planId,
                paymentMethod: 'CREDIT_CARD'
            });
            setSuccessMessage('Karnet został zakupiony!');
            loadData();
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err) {
            setError('Błąd podczas zakupu: ' + err.response?.data?.message || err.message);
        }
    };

    const handleSuspend = async (membershipId) => {
        try {
            await membershipAPI.suspendMembership(membershipId);
            setSuccessMessage('Karnet został zawieszony');
            loadData();
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err) {
            setError('Błąd podczas zawieszania: ' + err.response?.data?.message || err.message);
        }
    };

    const handleResume = async (membershipId) => {
        try {
            await membershipAPI.resumeMembership(membershipId);
            setSuccessMessage('Karnet został wznowiony');
            loadData();
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err) {
            setError('Błąd podczas wznawiania: ' + err.response?.data?.message || err.message);
        }
    };

    const handleCancel = async (membershipId) => {
        if (window.confirm('Czy na pewno chcesz anulować karnet?')) {
            try {
                await membershipAPI.cancelMembership(membershipId);
                setSuccessMessage('Karnet został anulowany');
                loadData();
                setTimeout(() => setSuccessMessage(null), 3000);
            } catch (err) {
                setError('Błąd podczas anulowania: ' + err.response?.data?.message || err.message);
            }
        }
    };

    const getStatusBadge = (status) => {
        const statusMap = {
            'ACTIVE': { label: 'Aktywny', class: 'status-active' },
            'SUSPENDED': { label: 'Zawieszony', class: 'status-suspended' },
            'CANCELLED': { label: 'Anulowany', class: 'status-cancelled' },
            'EXPIRED': { label: 'Wygasły', class: 'status-expired' }
        };
        const statusInfo = statusMap[status] || { label: status, class: '' };
        return <span className={`status-badge ${statusInfo.class}`}>{statusInfo.label}</span>;
    };

    if (loading) return <div className="loading">Ładowanie...</div>;

    return (
        <div className="membership-panel">
            <h2>Mój Karnet</h2>

            {error && <div className="error-message">{error}</div>}
            {successMessage && <div className="success-message">{successMessage}</div>}

            {/* Aktywny karnet */}
            <div className="my-membership-section">
                <h3>Twoje członkostwo</h3>
                {userMemberships.length > 0 ? (
                    <div className="memberships-list">
                        {userMemberships.map(membership => (
                            <div key={membership.id} className="membership-card">
                                <div className="membership-header">
                                    <h4>{membership.type}</h4>
                                    {getStatusBadge(membership.status)}
                                </div>
                                <div className="membership-details">
                                    <p><strong>Cena:</strong> {membership.price} zł</p>
                                    <p><strong>Data rozpoczęcia:</strong> {new Date(membership.startDate).toLocaleDateString()}</p>
                                    <p><strong>Data zakończenia:</strong> {new Date(membership.endDate).toLocaleDateString()}</p>
                                </div>
                                <div className="membership-actions">
                                    {membership.status === 'ACTIVE' && (
                                        <button onClick={() => handleSuspend(membership.id)} className="btn-warning">
                                            Zawieś karnet
                                        </button>
                                    )}
                                    {membership.status === 'SUSPENDED' && (
                                        <button onClick={() => handleResume(membership.id)} className="btn-success">
                                            Wznów karnet
                                        </button>
                                    )}
                                    {(membership.status === 'ACTIVE' || membership.status === 'SUSPENDED') && (
                                        <button onClick={() => handleCancel(membership.id)} className="btn-danger">
                                            Anuluj karnet
                                        </button>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <p className="no-membership">Nie posiadasz aktywnego karnetu</p>
                )}
            </div>

            {/* Dostępne plany */}
            <div className="available-plans-section">
                <h3>Dostępne plany karnetów</h3>
                <div className="plans-grid">
                    {plans.map(plan => (
                        <div key={plan.id} className="plan-card">
                            <h4>{plan.name}</h4>
                            <p className="plan-price">{plan.price} zł / miesiąc</p>
                            <p className="plan-description">{plan.description}</p>
                            <div className="plan-features">
                                {plan.features && plan.features.split(',').map((feature, idx) => (
                                    <div key={idx} className="feature-item">✓ {feature.trim()}</div>
                                ))}
                            </div>
                            <button
                                onClick={() => handlePurchase(plan.id)}
                                className="btn-primary"
                                disabled={userMemberships.some(m => m.status === 'ACTIVE')}
                            >
                                {userMemberships.some(m => m.status === 'ACTIVE') ? 'Masz już karnet' : 'Kup'}
                            </button>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default MembershipPanel;
