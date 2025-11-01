CREATE TABLE flight_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flight_id UUID NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_flight
        FOREIGN KEY (flight_id)
        REFERENCES flight (id)
        ON DELETE CASCADE
);