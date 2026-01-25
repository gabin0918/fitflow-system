-- Inicjalizacja planów członkostwa
INSERT INTO membership_plans (name, description, price, duration_days, is_active, features)
VALUES
    ('BASIC', 'Plan podstawowy z dostępem do siłowni', 99.99, 30, true, 'Dostęp do siłowni,Szafka,Wi-Fi'),
    ('PREMIUM', 'Plan premium z dodatkowymi korzyściami', 149.99, 30, true, 'Dostęp do siłowni,Szafka,Wi-Fi,Zajęcia grupowe,Sauna'),
    ('VIP', 'Plan VIP z pełnym dostępem', 249.99, 30, true, 'Dostęp do siłowni,Szafka,Wi-Fi,Zajęcia grupowe,Sauna,Trener personalny,Spa,Drinki proteinowe')
ON CONFLICT (name) DO NOTHING;
