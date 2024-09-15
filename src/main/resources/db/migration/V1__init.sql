-- initialize booking table
CREATE TABLE booking
(
    booking_id          BIGSERIAL PRIMARY KEY,
    user_reference_id   BIGINT NOT NULL,
    created_date        TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_date        TIMESTAMP WITH TIME ZONE NOT NULL,
    start_time          TIMESTAMP WITH TIME ZONE NOT NULL,
    duration_in_minutes INTEGER NOT NULL,
    status              VARCHAR(255) NOT NULL,
    comments            TEXT,
    version             BIGINT
);