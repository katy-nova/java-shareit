DELETE FROM bookings;
DELETE FROM items;
DELETE FROM users;
DELETE FROM requests;
DELETE FROM comments;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE requests ALTER COLUMN id RESTART WITH 1;
ALTER TABLE items ALTER COLUMN id RESTART WITH 1;
ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1;
ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;

INSERT INTO users (email, name) VALUES
                                    ('user1@example.com', 'Иван Иванов'),
                                    ('user2@example.com', 'Петр Петров'),
                                    ('user3@example.com', 'Сергей Сергеев');

INSERT INTO requests (description, user_id, created) VALUES
    ('Нужна дрель для ремонта квартиры', 2, '2023-05-28 14:30:00');

INSERT INTO items (name, description, available, owner_id, request_id) VALUES
                                                               ('Дрель', 'Мощная дрель с набором сверл', true, 1, 1);

INSERT INTO items (name, description, available, owner_id) VALUES
                                                               ('Велосипед', 'Горный велосипед, 21 скорость', false, 1),
                                                               ('Палатка', '4-х местная палатка с москитной сеткой', true, 1),
                                                               ('Проектор', 'Full HD проектор с HDMI входом', true, 2),
                                                               ('Мангал', 'Стальной мангал с шампурами', true, 2),
                                                               ('Каноэ', 'Двухместное каноэ с веслами', true, 2),
                                                               ('Фотоаппарат', 'Зеркальный фотоаппарат Canon EOS', true, 3),
                                                               ('Гриль', 'Электрический гриль с регулировкой температуры', true, 3),
                                                               ('Гитара', 'Акустическая гитара Fender', true, 3);

INSERT INTO bookings (start, end_time, user_id, item_id, status) VALUES
                                                                     ('2023-06-01 10:00:00', '2023-06-03 18:00:00', 1, 4, 1),  -- Проектор (id=4)
                                                                     ('2023-06-10 09:00:00', '2023-06-12 20:00:00', 1, 7, 2),  -- Фотоаппарат (id=7)
                                                                     ('2023-06-15 08:00:00', '2023-06-17 17:00:00', 1, 5, 1), -- Мангал (id=5)
                                                                     ('2023-06-05 11:00:00', '2023-06-07 19:00:00', 2, 1, 1),  -- Дрель (id=1)
                                                                     ('2023-06-08 14:00:00', '2023-06-10 16:00:00', 2, 9, 0),  -- Гитара (id=9)
                                                                     ('2023-06-20 10:00:00', '2023-06-22 18:00:00', 2, 3, 1);  -- Палатка (id=3)

-- Добавляем комментарии для первых трёх вещей
INSERT INTO comments (text, item_id, user_id, created) VALUES
                                                           ('Отличная дрель, помогла быстро завершить ремонт!', 1, 2, '2023-06-08 10:15:00'),
                                                           ('Велосипед в хорошем состоянии, но требует небольшой настройки передач', 2, 3, '2023-05-20 18:45:00'),
                                                           ('Палатка просторная, удобная, отлично защищает от дождя', 3, 2, '2023-06-23 09:30:00');