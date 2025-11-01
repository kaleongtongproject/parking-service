DROP TABLE IF EXISTS flight_status_history;

CREATE TABLE flight_status_history (
    id UUID PRIMARY KEY,
    flight_id UUID REFERENCES flight(id),
    changed_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL
);