-- Data used in automated tests
INSERT INTO booking (user_reference_id, created_date, updated_date, start_time, duration_in_minutes, status, comments)
VALUES
    (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '2024-10-11 12:00:00+00:00', 30, 'PENDING', 'This is a pending booking'),
    (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '2024-10-12 15:00:00+00:00', 60, 'CONFIRMED', 'This is a confirmed booking'),
    (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '2024-10-13 10:00:00+00:00', 45, 'CANCELED', 'This booking was canceled'),
    (3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '2024-10-14 18:00:00+00:00', 90, 'PENDING', NULL),
    (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '2024-10-15 11:00:00+00:00', 60, 'CONFIRMED', 'Another confirmed booking');