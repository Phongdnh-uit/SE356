-- V12: Create depots table

-- Create the depots table
CREATE TABLE depots (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lng DOUBLE PRECISION NOT NULL,
    is_start_node BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(36),
    updated_by VARCHAR(36)
);

-- Add indexes for better query performance
CREATE INDEX idx_depots_name ON depots(name);
CREATE INDEX idx_depots_type ON depots(type);
CREATE INDEX idx_depots_is_start_node ON depots(is_start_node);
CREATE INDEX idx_depots_created_at ON depots(created_at);
