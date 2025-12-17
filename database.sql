-- Database Creation
CREATE DATABASE IF NOT EXISTS coworking_db;
USE coworking_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER' -- 'USER' or 'ADMIN'
);

-- Rooms Table
CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL, -- MEETING, PHONE_BOX, DESK, etc.
    capacity INT NOT NULL,
    zone VARCHAR(50) NOT NULL, -- SILENT, CASUAL
    equipment TEXT,
    price_per_hour DOUBLE NOT NULL DEFAULT 0.0
);

-- Reservations Table
CREATE TABLE IF NOT EXISTS reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    room_id INT NOT NULL,
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED', -- CONFIRMED, CANCELLED
    selected_equipments TEXT,
    total_price DOUBLE NOT NULL DEFAULT 0.0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

-- Insert Default Admin (Optional)
-- Password should be hashed in production, but here plain text for demo if app uses plain text
INSERT INTO users (username, password, email, role) VALUES ('admin', 'admin123', 'admin@cospace.com', 'ADMIN');
