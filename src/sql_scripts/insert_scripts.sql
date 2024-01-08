INSERT INTO customers (name, email, address, encryptedPassword) VALUES
    ('John Doe', 'john@example.com', '123 Main St, Anytown', 'hashedPassword1'),
    ('Jane Smith', 'jane@example.com', '456 Elm St, Othertown', 'hashedPassword2'),
    ('Alice Johnson', 'alice@example.com', '789 Oak St, Anycity', 'hashedPassword3'),
    ('Bob Brown', 'bob@example.com', '101 Pine St, Othercity', 'hashedPassword4'),
    ('Emma White', 'emma@example.com', '555 Cedar St, Anothercity', 'hashedPassword5'),
    ('William Davis', 'william@example.com', '321 Maple St, Yetanothercity', 'hashedPassword6'),
    ('Olivia Miller', 'olivia@example.com', '909 Walnut St, Newtown', 'hashedPassword7'),
    ('Liam Wilson', 'liam@example.com', '222 Birch St, Smalltown', 'hashedPassword8'),
    ('Sophia Garcia', 'sophia@example.com', '777 Spruce St, Hometown', 'hashedPassword9'),
    ('Mason Martinez', 'mason@example.com', '444 Elm St, Village', 'hashedPassword10');

INSERT INTO shipments (trackingNumber, origin, destination, status, customerId) VALUES
    ('ABC123', 'New York', 'Los Angeles', 'In Transit', 1),
    ('DEF456', 'London', 'Paris', 'Delivered', 2),
    ('GHI789', 'Tokyo', 'Sydney', 'In Transit', 3),
    ('JKL012', 'Berlin', 'Rome', 'Pending', 4),
    ('MNO345', 'Moscow', 'Beijing', 'Delivered', 5),
    ('PQR678', 'Dubai', 'Cairo', 'In Transit', 6),
    ('STU901', 'Seoul', 'Mumbai', 'Pending', 7),
    ('VWX234', 'Madrid', 'Toronto', 'In Transit', 8),
    ('YZA567', 'Rio de Janeiro', 'Singapore', 'Delivered', 9),
    ('BCD890', 'Amsterdam', 'Buenos Aires', 'Pending', 10);

INSERT INTO parcels (weight, description, shipmentId) VALUES
    (12.5, 'Electronics', 1),
    (8.0, 'Clothing', 2),
    (5.5, 'Books', 3),
    (10.0, 'Toys', 4),
    (15.3, 'Furniture', 5),
    (6.7, 'Jewelry', 6),
    (9.8, 'Tools', 7),
    (7.2, 'Sports Equipment', 8),
    (11.1, 'Appliances', 9),
    (13.0, 'Medical Supplies', 10);

INSERT INTO deliveryAgents (name, vehicleType, availability) VALUES
    ('Mike Johnson', 'Truck', true),
    ('Sarah Davis', 'Van', false),
    ('Emily Wilson', 'Bike', true),
    ('David Lee', 'Car', true),
    ('Sophie Brown', 'Truck', false),
    ('James Miller', 'Van', true),
    ('Lily Garcia', 'Bike', true),
    ('Matthew Martinez', 'Car', false),
    ('Grace White', 'Truck', true),
    ('Ethan Thomas', 'Van', true);

INSERT INTO deliveryRoutes (routeName, agentId, shipmentId) VALUES
    ('Route 1', 1, 1),
    ('Route 2', 2, 2),
    ('Route 3', 3, 3),
    ('Route 4', 4, 4),
    ('Route 5', 5, 5),
    ('Route 6', 6, 6),
    ('Route 7', 7, 7),
    ('Route 8', 8, 8),
    ('Route 9', 9, 9),
    ('Route 10', 10, 10);
