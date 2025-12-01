-- ============================================================
-- Address Book Application - PostgreSQL Seed Data
-- ============================================================
-- Sample data for Production/Staging environments
-- Uses INSERT ... ON CONFLICT for idempotency
-- ============================================================

-- Insert Address Books
INSERT INTO addressbooks (id, name, description, created_at, updated_at) VALUES
(1, 'Personal Contacts', 'My personal friends and family contacts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Work Colleagues', 'Office and work related contacts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Customers - Sydney', 'Customer contacts from Sydney region', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Customers - Melbourne', 'Customer contacts from Melbourne region', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Customers - Brisbane', 'Customer contacts from Brisbane region', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Suppliers', 'Supplier and vendor contacts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'VIP Clients', 'High-value VIP client contacts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Emergency Contacts', 'Emergency and urgent contacts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'Contractors', 'Freelance and contractor contacts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'Archived Contacts', 'Old and archived customer contacts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Reset sequence
SELECT setval('addressbooks_id_seq', 10, true);

-- ============================================================
-- Address Book 1: Personal Contacts
-- ============================================================
INSERT INTO contacts (id, name, phone_number, address_book_id, created_at) VALUES
(1, 'John Smith', '+61 412 345 678', 1, CURRENT_TIMESTAMP),
(2, 'Sarah Johnson', '+61 423 456 789', 1, CURRENT_TIMESTAMP),
(3, 'Michael Brown', '+61 434 567 890', 1, CURRENT_TIMESTAMP),
(4, 'Emily Davis', '+61 445 678 901', 1, CURRENT_TIMESTAMP),
(5, 'David Wilson', '+61 456 789 012', 1, CURRENT_TIMESTAMP),
(6, 'Jessica Taylor', '+61 467 890 123', 1, CURRENT_TIMESTAMP),
(7, 'Daniel Anderson', '+61 478 901 234', 1, CURRENT_TIMESTAMP),
(8, 'Ashley Thomas', '+61 489 012 345', 1, CURRENT_TIMESTAMP),
(9, 'James Jackson', '+61 490 123 456', 1, CURRENT_TIMESTAMP),
(10, 'Olivia White', '+61 401 234 567', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- Address Book 2: Work Colleagues
-- ============================================================
INSERT INTO contacts (id, name, phone_number, address_book_id, created_at) VALUES
(11, 'Robert Harris', '+61 412 111 222', 2, CURRENT_TIMESTAMP),
(12, 'Jennifer Martin', '+61 423 222 333', 2, CURRENT_TIMESTAMP),
(13, 'William Garcia', '+61 434 333 444', 2, CURRENT_TIMESTAMP),
(14, 'Elizabeth Martinez', '+61 445 444 555', 2, CURRENT_TIMESTAMP),
(15, 'Christopher Robinson', '+61 456 555 666', 2, CURRENT_TIMESTAMP),
(16, 'Amanda Clark', '+61 467 666 777', 2, CURRENT_TIMESTAMP),
(17, 'Matthew Rodriguez', '+61 478 777 888', 2, CURRENT_TIMESTAMP),
(18, 'Stephanie Lewis', '+61 489 888 999', 2, CURRENT_TIMESTAMP),
(19, 'Andrew Lee', '+61 490 999 000', 2, CURRENT_TIMESTAMP),
(20, 'Nicole Walker', '+61 401 000 111', 2, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- Address Book 3: Customers - Sydney
-- ============================================================
INSERT INTO contacts (id, name, phone_number, address_book_id, created_at) VALUES
(21, 'Peter Hall', '+61 402 111 001', 3, CURRENT_TIMESTAMP),
(22, 'Susan Allen', '+61 402 111 002', 3, CURRENT_TIMESTAMP),
(23, 'Kevin Young', '+61 402 111 003', 3, CURRENT_TIMESTAMP),
(24, 'Laura Hernandez', '+61 402 111 004', 3, CURRENT_TIMESTAMP),
(25, 'Brian King', '+61 402 111 005', 3, CURRENT_TIMESTAMP),
(26, 'Michelle Wright', '+61 402 111 006', 3, CURRENT_TIMESTAMP),
(27, 'Steven Lopez', '+61 402 111 007', 3, CURRENT_TIMESTAMP),
(28, 'Kimberly Hill', '+61 402 111 008', 3, CURRENT_TIMESTAMP),
(29, 'Edward Scott', '+61 402 111 009', 3, CURRENT_TIMESTAMP),
(30, 'Donna Green', '+61 402 111 010', 3, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- Address Book 4: Customers - Melbourne
-- ============================================================
INSERT INTO contacts (id, name, phone_number, address_book_id, created_at) VALUES
(31, 'George Adams', '+61 403 222 001', 4, CURRENT_TIMESTAMP),
(32, 'Patricia Baker', '+61 403 222 002', 4, CURRENT_TIMESTAMP),
(33, 'Ronald Gonzalez', '+61 403 222 003', 4, CURRENT_TIMESTAMP),
(34, 'Barbara Nelson', '+61 403 222 004', 4, CURRENT_TIMESTAMP),
(35, 'Timothy Carter', '+61 403 222 005', 4, CURRENT_TIMESTAMP),
(36, 'Sandra Mitchell', '+61 403 222 006', 4, CURRENT_TIMESTAMP),
(37, 'Jason Perez', '+61 403 222 007', 4, CURRENT_TIMESTAMP),
(38, 'Betty Roberts', '+61 403 222 008', 4, CURRENT_TIMESTAMP),
(39, 'Jeffrey Turner', '+61 403 222 009', 4, CURRENT_TIMESTAMP),
(40, 'Dorothy Phillips', '+61 403 222 010', 4, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- Address Book 5: Customers - Brisbane
-- ============================================================
INSERT INTO contacts (id, name, phone_number, address_book_id, created_at) VALUES
(41, 'Gary Campbell', '+61 404 333 001', 5, CURRENT_TIMESTAMP),
(42, 'Margaret Parker', '+61 404 333 002', 5, CURRENT_TIMESTAMP),
(43, 'Frank Evans', '+61 404 333 003', 5, CURRENT_TIMESTAMP),
(44, 'Lisa Edwards', '+61 404 333 004', 5, CURRENT_TIMESTAMP),
(45, 'Raymond Collins', '+61 404 333 005', 5, CURRENT_TIMESTAMP),
(46, 'Helen Stewart', '+61 404 333 006', 5, CURRENT_TIMESTAMP),
(47, 'Henry Sanchez', '+61 404 333 007', 5, CURRENT_TIMESTAMP),
(48, 'Samantha Morris', '+61 404 333 008', 5, CURRENT_TIMESTAMP),
(49, 'Jack Rogers', '+61 404 333 009', 5, CURRENT_TIMESTAMP),
(50, 'Deborah Reed', '+61 404 333 010', 5, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- Address Book 6: Suppliers
-- ============================================================
INSERT INTO contacts (id, name, phone_number, address_book_id, created_at) VALUES
(51, 'Arthur Cook', '+61 405 444 001', 6, CURRENT_TIMESTAMP),
(52, 'Ruth Morgan', '+61 405 444 002', 6, CURRENT_TIMESTAMP),
(53, 'Carl Bell', '+61 405 444 003', 6, CURRENT_TIMESTAMP),
(54, 'Sharon Murphy', '+61 405 444 004', 6, CURRENT_TIMESTAMP),
(55, 'Dennis Bailey', '+61 405 444 005', 6, CURRENT_TIMESTAMP),
(56, 'Cynthia Rivera', '+61 405 444 006', 6, CURRENT_TIMESTAMP),
(57, 'Jerry Cooper', '+61 405 444 007', 6, CURRENT_TIMESTAMP),
(58, 'Angela Richardson', '+61 405 444 008', 6, CURRENT_TIMESTAMP),
(59, 'Philip Cox', '+61 405 444 009', 6, CURRENT_TIMESTAMP),
(60, 'Virginia Howard', '+61 405 444 010', 6, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- Address Book 7: VIP Clients
-- ============================================================
INSERT INTO contacts (id, name, phone_number, address_book_id, created_at) VALUES
(61, 'Albert Ward', '+61 406 555 001', 7, CURRENT_TIMESTAMP),
(62, 'Marie Torres', '+61 406 555 002', 7, CURRENT_TIMESTAMP),
(63, 'Eugene Peterson', '+61 406 555 003', 7, CURRENT_TIMESTAMP),
(64, 'Diane Gray', '+61 406 555 004', 7, CURRENT_TIMESTAMP),
(65, 'Russell Ramirez', '+61 406 555 005', 7, CURRENT_TIMESTAMP),
(66, 'Frances James', '+61 406 555 006', 7, CURRENT_TIMESTAMP),
(67, 'Louis Watson', '+61 406 555 007', 7, CURRENT_TIMESTAMP),
(68, 'Joyce Brooks', '+61 406 555 008', 7, CURRENT_TIMESTAMP),
(69, 'Ralph Kelly', '+61 406 555 009', 7, CURRENT_TIMESTAMP),
(70, 'Judith Sanders', '+61 406 555 010', 7, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- Address Book 8: Emergency Contacts
-- ============================================================
INSERT INTO contacts (id, name, phone_number, address_book_id, created_at) VALUES
(71, 'Roy Price', '+61 407 666 001', 8, CURRENT_TIMESTAMP),
(72, 'Ann Bennett', '+61 407 666 002', 8, CURRENT_TIMESTAMP),
(73, 'Eugene Wood', '+61 407 666 003', 8, CURRENT_TIMESTAMP),
(74, 'Theresa Barnes', '+61 407 666 004', 8, CURRENT_TIMESTAMP),
(75, 'Wayne Ross', '+61 407 666 005', 8, CURRENT_TIMESTAMP),
(76, 'Gloria Henderson', '+61 407 666 006', 8, CURRENT_TIMESTAMP),
(77, 'Alan Coleman', '+61 407 666 007', 8, CURRENT_TIMESTAMP),
(78, 'Kathryn Jenkins', '+61 407 666 008', 8, CURRENT_TIMESTAMP),
(79, 'Lawrence Perry', '+61 407 666 009', 8, CURRENT_TIMESTAMP),
(80, 'Jean Powell', '+61 407 666 010', 8, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- Address Book 9: Contractors
-- ============================================================
INSERT INTO contacts (id, name, phone_number, address_book_id, created_at) VALUES
(81, 'Willie Long', '+61 408 777 001', 9, CURRENT_TIMESTAMP),
(82, 'Alice Patterson', '+61 408 777 002', 9, CURRENT_TIMESTAMP),
(83, 'Billy Hughes', '+61 408 777 003', 9, CURRENT_TIMESTAMP),
(84, 'Julie Flores', '+61 408 777 004', 9, CURRENT_TIMESTAMP),
(85, 'Jesse Washington', '+61 408 777 005', 9, CURRENT_TIMESTAMP),
(86, 'Teresa Butler', '+61 408 777 006', 9, CURRENT_TIMESTAMP),
(87, 'Terry Simmons', '+61 408 777 007', 9, CURRENT_TIMESTAMP),
(88, 'Doris Foster', '+61 408 777 008', 9, CURRENT_TIMESTAMP),
(89, 'Gerald Gonzales', '+61 408 777 009', 9, CURRENT_TIMESTAMP),
(90, 'Janice Bryant', '+61 408 777 010', 9, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- Address Book 10: Archived Contacts
-- ============================================================
INSERT INTO contacts (id, name, phone_number, address_book_id, created_at) VALUES
(91, 'Harold Alexander', '+61 409 888 001', 10, CURRENT_TIMESTAMP),
(92, 'Evelyn Russell', '+61 409 888 002', 10, CURRENT_TIMESTAMP),
(93, 'Howard Griffin', '+61 409 888 003', 10, CURRENT_TIMESTAMP),
(94, 'Cheryl Diaz', '+61 409 888 004', 10, CURRENT_TIMESTAMP),
(95, 'Joe Hayes', '+61 409 888 005', 10, CURRENT_TIMESTAMP),
(96, 'Martha Myers', '+61 409 888 006', 10, CURRENT_TIMESTAMP),
(97, 'Ernest Ford', '+61 409 888 007', 10, CURRENT_TIMESTAMP),
(98, 'Katherine Hamilton', '+61 409 888 008', 10, CURRENT_TIMESTAMP),
(99, 'Johnny Graham', '+61 409 888 009', 10, CURRENT_TIMESTAMP),
(100, 'Sara Sullivan', '+61 409 888 010', 10, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Reset sequence
SELECT setval('contacts_id_seq', 100, true);
