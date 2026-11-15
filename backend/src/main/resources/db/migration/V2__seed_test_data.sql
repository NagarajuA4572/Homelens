-- V2__seed_test_data.sql
-- HomeLens Initial Seed Data

-- 1. Demo Users (Password: Password123!)
INSERT INTO users (id, email, password, full_name, phone, role, created_at, updated_at)
VALUES
(1, 'admin@homelens.com', '$2a$10$nq4Kb66AbHS6k7B58KAsEuhJKQwWPYRVOe6UqrWPjN8qMigWlx.M2', 'System Administrator', '+1-555-010-0001', 'ROLE_ADMIN', NOW(), NOW()),
(2, 'sarah.agent@homelens.com', '$2a$10$nq4Kb66AbHS6k7B58KAsEuhJKQwWPYRVOe6UqrWPjN8qMigWlx.M2', 'Sarah Jenkins', '+1-555-019-2831', 'ROLE_AGENT', NOW(), NOW()),
(3, 'alex.buyer@gmail.com', '$2a$10$nq4Kb66AbHS6k7B58KAsEuhJKQwWPYRVOe6UqrWPjN8qMigWlx.M2', 'Alex Rivera', '+1-555-014-8842', 'ROLE_BUYER', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- 2. Properties
INSERT INTO properties (id, title, description, price, property_type, listing_type, status, bedrooms, bathrooms, area_sqft, address, city, state, postal_code, latitude, longitude, agent_id, created_at, updated_at)
VALUES
(
    1,
    'Modern Skyline Penthouse',
    'Spectacular panoramic bay views with private terrace, floor-to-ceiling glass, Italian marble countertops, smart home automation, and 24/7 concierge services.',
    1850000.00,
    'APARTMENT',
    'FOR_SALE',
    'AVAILABLE',
    3,
    3,
    2200.00,
    '450 Mission Street, Unit 42A',
    'San Francisco',
    'CA',
    '94105',
    37.7905,
    -122.3990,
    2,
    NOW(),
    NOW()
),
(
    2,
    'Craftsman Oak Haven Residence',
    'Charming updated craftsman residence located minutes from downtown Austin. Features original hardwood floors, open chef kitchen, custom deck, and spacious private backyard with heritage oaks.',
    695000.00,
    'HOUSE',
    'FOR_SALE',
    'AVAILABLE',
    4,
    3,
    2850.00,
    '1204 South Congress Ave',
    'Austin',
    'TX',
    '78704',
    30.2520,
    -97.7490,
    2,
    NOW(),
    NOW()
),
(
    3,
    'Luxury Biscayne Waterfront Villa',
    'Private gated coastal estate boasting 100 feet of prime water frontage, private boat dock, infinity swimming pool, outdoor summer kitchen, and bespoke wine cellar.',
    3450000.00,
    'VILLA',
    'FOR_SALE',
    'AVAILABLE',
    5,
    6,
    5400.00,
    '880 Biscayne Island Way',
    'Miami',
    'FL',
    '33132',
    25.7820,
    -80.1870,
    2,
    NOW(),
    NOW()
),
(
    4,
    'Chelsea High-Line Designer Loft',
    'Sun-drenched architectural corner loft overlooking the High Line. Soaring 12-foot beamed ceilings, exposed brick accents, designer fixtures, and spa-grade soaking tub.',
    5800.00,
    'CONDO',
    'FOR_RENT',
    'AVAILABLE',
    2,
    2,
    1350.00,
    '210 10th Avenue, Apt 5B',
    'New York',
    'NY',
    '10011',
    40.7470,
    -74.0040,
    2,
    NOW(),
    NOW()
),
(
    5,
    'Pacific Northwest Contemporary Townhouse',
    'Eco-conscious modern townhouse steps away from Lake Union. Private rooftop terrace with Mt. Rainier views, EV charging station in garage, and high-efficiency radiant heating.',
    3950.00,
    'TOWNHOUSE',
    'FOR_RENT',
    'AVAILABLE',
    3,
    3,
    1900.00,
    '1412 Westlake Ave N',
    'Seattle',
    'WA',
    '98109',
    47.6320,
    -122.3380,
    2,
    NOW(),
    NOW()
),
(
    6,
    'Beacon Hill Historic Brownstone',
    'Distinguished 4-story historic brick brownstone retaining ornate period crown mouldings, 4 working fireplaces, modern chef appliances, and a secluded private garden patio.',
    1420000.00,
    'HOUSE',
    'FOR_SALE',
    'AVAILABLE',
    4,
    4,
    3100.00,
    '74 Pinckney Street',
    'Boston',
    'MA',
    '02114',
    42.3590,
    -71.0690,
    2,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 3. Property Images
INSERT INTO property_images (id, property_id, image_url, is_primary, display_order, created_at)
VALUES
(1, 1, 'https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=1200&q=80', TRUE, 0, NOW()),
(2, 1, 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=1200&q=80', FALSE, 1, NOW()),
(3, 2, 'https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?auto=format&fit=crop&w=1200&q=80', TRUE, 0, NOW()),
(4, 2, 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1200&q=80', FALSE, 1, NOW()),
(5, 3, 'https://images.unsplash.com/photo-1613977257363-707ba9348227?auto=format&fit=crop&w=1200&q=80', TRUE, 0, NOW()),
(6, 3, 'https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&w=1200&q=80', FALSE, 1, NOW()),
(7, 4, 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=1200&q=80', TRUE, 0, NOW()),
(8, 5, 'https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=1200&q=80', TRUE, 0, NOW()),
(9, 6, 'https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=1200&q=80', TRUE, 0, NOW())
ON CONFLICT (id) DO NOTHING;

-- 4. Initial Inquiries
INSERT INTO inquiries (id, property_id, user_id, name, email, phone, message, status, created_at)
VALUES
(1, 1, 3, 'Alex Rivera', 'alex.buyer@gmail.com', '+1-555-014-8842', 'I am interested in scheduling a private weekend showing for the Skyline Penthouse. Is Saturday morning available?', 'NEW', NOW())
ON CONFLICT (id) DO NOTHING;

-- Reset identity sequence counters to prevent key collisions on new inserts
SELECT setval(pg_get_serial_sequence('users', 'id'), coalesce(max(id), 1)) FROM users;
SELECT setval(pg_get_serial_sequence('properties', 'id'), coalesce(max(id), 1)) FROM properties;
SELECT setval(pg_get_serial_sequence('property_images', 'id'), coalesce(max(id), 1)) FROM property_images;
SELECT setval(pg_get_serial_sequence('inquiries', 'id'), coalesce(max(id), 1)) FROM inquiries;
