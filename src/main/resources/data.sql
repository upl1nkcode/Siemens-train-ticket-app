-- Stations
INSERT INTO stations (name) VALUES ('Bucharest');
INSERT INTO stations (name) VALUES ('Brasov');
INSERT INTO stations (name) VALUES ('Cluj-Napoca');
INSERT INTO stations (name) VALUES ('Timisoara');
INSERT INTO stations (name) VALUES ('Sibiu');
INSERT INTO stations (name) VALUES ('Iasi');

-- Routes
INSERT INTO routes (name) VALUES ('Bucharest - Brasov Express');
INSERT INTO routes (name) VALUES ('Brasov - Cluj via Sibiu');
INSERT INTO routes (name) VALUES ('Bucharest - Iasi Direct');

-- Route stops (ordered)
-- Route 1: Bucharest -> Brasov
INSERT INTO route_stops (route_id, station_id, stop_order) VALUES (1, 1, 1);
INSERT INTO route_stops (route_id, station_id, stop_order) VALUES (1, 2, 2);

-- Route 2: Brasov -> Sibiu -> Cluj-Napoca
INSERT INTO route_stops (route_id, station_id, stop_order) VALUES (2, 2, 1);
INSERT INTO route_stops (route_id, station_id, stop_order) VALUES (2, 5, 2);
INSERT INTO route_stops (route_id, station_id, stop_order) VALUES (2, 3, 3);

-- Route 3: Bucharest -> Iasi
INSERT INTO route_stops (route_id, station_id, stop_order) VALUES (3, 1, 1);
INSERT INTO route_stops (route_id, station_id, stop_order) VALUES (3, 6, 2);

-- Trains
INSERT INTO trains (name, total_seats, status, delay_minutes) VALUES ('IR 1581', 200, 'ON_TIME', 0);
INSERT INTO trains (name, total_seats, status, delay_minutes) VALUES ('IR 1732', 150, 'ON_TIME', 0);
INSERT INTO trains (name, total_seats, status, delay_minutes) VALUES ('IR 1990', 180, 'ON_TIME', 0);

-- Schedules
INSERT INTO schedules (train_id, route_id, departure_time) VALUES (1, 1, '2026-06-15 08:30:00');
INSERT INTO schedules (train_id, route_id, departure_time) VALUES (2, 2, '2026-06-15 12:00:00');
INSERT INTO schedules (train_id, route_id, departure_time) VALUES (3, 3, '2026-06-15 06:45:00');
