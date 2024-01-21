CREATE TABLE customers (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    email VARCHAR(50),
    address VARCHAR(100),
    encryptedPassword VARCHAR(100)
);

CREATE TABLE shipments (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    trackingNumber VARCHAR(20),
    origin VARCHAR(100),
    destination VARCHAR(100),
    status VARCHAR(20),
    customerId INT,
    detailed_description TEXT,
    FOREIGN KEY (customerId) REFERENCES customers(id)
);

CREATE TABLE parcels (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    weight DECIMAL(10, 2),
    description VARCHAR(100),
    shipmentId INT,
    FOREIGN KEY (shipmentId) REFERENCES shipments(id)
);

CREATE TABLE deliveryAgents (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    vehicleType VARCHAR(50),
    availability BOOLEAN
);

CREATE TABLE deliveryRoutes (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    routeName VARCHAR(50),
    agentId INT,
    shipmentId INT,
    FOREIGN KEY (agentId) REFERENCES deliveryAgents(id),
    FOREIGN KEY (shipmentId) REFERENCES shipments(id)
);
