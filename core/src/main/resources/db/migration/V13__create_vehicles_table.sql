-- V13: Create vehicles table

CREATE TABLE IF NOT EXISTS vehicles (
    id VARCHAR(36) PRIMARY KEY,
    license_plate VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    max_weight DOUBLE PRECISION NOT NULL,
    max_volume DOUBLE PRECISION NOT NULL,
    shipper_id VARCHAR(36),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),

    CONSTRAINT fk_vehicles_shipper FOREIGN KEY (shipper_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_vehicle_shipper ON vehicles(shipper_id);