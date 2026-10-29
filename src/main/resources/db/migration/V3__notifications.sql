
CREATE TABLE processed_messages (
       message_id VARCHAR(100) PRIMARY KEY,
       processed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE notifications (
       id BIGSERIAL PRIMARY KEY,
       message_id VARCHAR(100) NOT NULL,
       booking_id BIGINT NOT NULL,
       user_id BIGINT NOT NULL,
       body TEXT NOT NULL,
       sent_at TIMESTAMPTZ NOT NULL DEFAULT now()
);







