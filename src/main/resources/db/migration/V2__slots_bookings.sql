

CREATE TABLE slots(
    id BIGSERIAL PRIMARY KEY,
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_slot_time CHECK (ends_at > starts_at),
    CONSTRAINT uq_slot_start UNIQUE (starts_at)
);

CREATE TABLE bookings(
    id BIGSERIAL PRIMARY KEY,
    slot_id BIGINT  NOT NULL REFERENCES slots(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_booking_slots UNIQUE (slot_id)
);

CREATE INDEX idx_bookings_user ON bookings(user_id)




















