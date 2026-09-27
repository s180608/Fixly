INSERT INTO services (name, description, price, category)
SELECT 'Emergency Plumbing', 'Professional plumbing repairs for leaks, pipes and household plumbing problems.', 65.00, 'PLUMBING'
WHERE NOT EXISTS (
    SELECT 1 FROM services WHERE name = 'Emergency Plumbing'
);

INSERT INTO services (name, description, price, category)
SELECT 'Electrical Repair', 'Qualified electrical support for sockets, lighting and common electrical issues.', 70.00, 'ELECTRICAL'
WHERE NOT EXISTS (
    SELECT 1 FROM services WHERE name = 'Electrical Repair'
);

INSERT INTO services (name, description, price, category)
SELECT 'Home Cleaning', 'Reliable home cleaning service for kitchens, bathrooms and living areas.', 45.00, 'CLEANING'
WHERE NOT EXISTS (
    SELECT 1 FROM services WHERE name = 'Home Cleaning'
);

INSERT INTO services (name, description, price, category)
SELECT 'Heating Service', 'Heating inspection and repair service to keep your home warm and comfortable.', 80.00, 'HEATING'
WHERE NOT EXISTS (
    SELECT 1 FROM services WHERE name = 'Heating Service'
);

INSERT INTO services (name, description, price, category)
SELECT 'Garden Maintenance', 'Garden maintenance including general tidy-up and outdoor care.', 50.00, 'GARDENING'
WHERE NOT EXISTS (
    SELECT 1 FROM services WHERE name = 'Garden Maintenance'
);

INSERT INTO services (name, description, price, category)
SELECT 'Appliance Repair', 'Diagnosis and repair service for common household appliances.', 60.00, 'APPLIANCE_REPAIR'
WHERE NOT EXISTS (
    SELECT 1 FROM services WHERE name = 'Appliance Repair'
);
