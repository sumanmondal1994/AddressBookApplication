-- Schema for Address Book Application (PostgreSQL)
-- This file creates tables, indexes, and constraints (only if they don't exist)

-- Create addressbooks table
CREATE TABLE IF NOT EXISTS addressbooks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create contacts table
CREATE TABLE IF NOT EXISTS contacts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    address_book_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contact_addressbook FOREIGN KEY (address_book_id) REFERENCES addressbooks(id) ON DELETE CASCADE,
    CONSTRAINT uk_phone_addressbook UNIQUE (phone_number, address_book_id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_addressbook_name ON addressbooks(name);
CREATE INDEX IF NOT EXISTS idx_addressbook_created_at ON addressbooks(created_at);
CREATE INDEX IF NOT EXISTS idx_contact_phone ON contacts(phone_number);
CREATE INDEX IF NOT EXISTS idx_contact_addressbook ON contacts(address_book_id);
CREATE INDEX IF NOT EXISTS idx_contact_name ON contacts(name);
