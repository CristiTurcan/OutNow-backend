-- V3__add_event_fields_and_tickets.sql

-- 1. add the new columns if they’re not already there
ALTER TABLE event
    ADD COLUMN IF NOT EXISTS end_date       DATE,
    ADD COLUMN IF NOT EXISTS end_time       TIME,
    ADD COLUMN IF NOT EXISTS total_tickets  INTEGER;

-- 2. backfill only missing values
UPDATE event
SET end_date = event_date
WHERE end_date IS NULL;

UPDATE event
SET end_time = event_time
WHERE end_time IS NULL;
