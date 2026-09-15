-- =====================================================================
-- CommuterService / RidePlatform — Rebranded Schema (v2)
-- =====================================================================
-- Changes from original DDL:
--   1. All tables prefixed with 'rp_' and use new domain language
--   2. All primary keys are UUIDs (gen_random_uuid())
--   3. All tables have created_at + updated_at audit columns
--   4. Column names use new domain language (rider→passenger, driver→partner, ride→trip)
--   5. All foreign keys updated to match new naming
-- =====================================================================

CREATE DATABASE "CommuterService";

-- Enable UUID generation (PostgreSQL 13+ has gen_random_uuid() built-in)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =====================================================================
-- PASSENGERS (was: Rider)
-- =====================================================================
CREATE TABLE rp_passengers (
    passenger_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name      VARCHAR(100) NOT NULL,
    middle_name     VARCHAR(100),
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255) UNIQUE NOT NULL,
    phone_no        VARCHAR(20) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    is_verified     BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

-- =====================================================================
-- PARTNERS (was: Driver)
-- =====================================================================
CREATE TABLE rp_partners (
    partner_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name      VARCHAR(100) NOT NULL,
    middle_name     VARCHAR(100),
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255) UNIQUE NOT NULL,
    phone_no        VARCHAR(20) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    license_no      VARCHAR(100) UNIQUE NOT NULL,
    avg_rating      DECIMAL(3, 2) DEFAULT 0.00,
    total_trips     INTEGER DEFAULT 0,
    is_verified     BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

-- =====================================================================
-- FARE RULES
-- =====================================================================
CREATE TABLE rp_fare_rules (
    fare_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_category    VARCHAR(50) NOT NULL,
    base_fare           DECIMAL(10, 2) NOT NULL,
    per_km_rate         DECIMAL(10, 2) NOT NULL,
    per_min_rate        DECIMAL(10, 2) NOT NULL,
    min_fare            DECIMAL(10, 2) NOT NULL,
    created_at          TIMESTAMPTZ DEFAULT NOW(),
    updated_at          TIMESTAMPTZ DEFAULT NOW()
);

-- =====================================================================
-- SURGE PRICING
-- =====================================================================
CREATE TABLE rp_surge_pricing (
    surge_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    zone_name       VARCHAR(100) NOT NULL,
    lat_min         DECIMAL(9, 6) NOT NULL,
    lat_max         DECIMAL(9, 6) NOT NULL,
    lng_min         DECIMAL(9, 6) NOT NULL,
    lng_max         DECIMAL(9, 6) NOT NULL,
    day_of_week     VARCHAR(15) NOT NULL,
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    multiplier      DECIMAL(3, 2) NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

-- =====================================================================
-- OFFERS
-- =====================================================================
CREATE TABLE rp_offers (
    offer_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    promo_code      VARCHAR(50) UNIQUE NOT NULL,
    discount_pct    DECIMAL(5, 2),
    flat_discount   DECIMAL(10, 2),
    max_discount    DECIMAL(10, 2) NOT NULL,
    min_trip_value  DECIMAL(10, 2) NOT NULL,
    valid_from      TIMESTAMPTZ NOT NULL,
    valid_until     TIMESTAMPTZ NOT NULL,
    usage_limit     INTEGER NOT NULL,
    total_used      INTEGER DEFAULT 0,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

-- =====================================================================
-- VEHICLES
-- =====================================================================
CREATE TABLE rp_vehicles (
    vehicle_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    partner_id              UUID NOT NULL,
    model                   VARCHAR(100) NOT NULL,
    year                    INTEGER NOT NULL,
    plate_no                VARCHAR(50) UNIQUE NOT NULL,
    category                VARCHAR(50) NOT NULL,
    is_active               BOOLEAN DEFAULT TRUE,
    is_verified             BOOLEAN DEFAULT FALSE,
    insurance_policy_no     VARCHAR(100) NOT NULL,
    insurance_expiry_date   DATE NOT NULL,
    created_at              TIMESTAMPTZ DEFAULT NOW(),
    updated_at              TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT fk_vehicle_partner FOREIGN KEY (partner_id) REFERENCES rp_partners(partner_id) ON DELETE CASCADE
);

-- =====================================================================
-- WALLETS (was: WALLET)
-- =====================================================================
CREATE TABLE rp_wallets (
    wallet_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    partner_id      UUID UNIQUE NOT NULL,
    balance         DECIMAL(12, 2) DEFAULT 0.00,
    version         INTEGER DEFAULT 0,  -- for optimistic locking
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT fk_wallet_partner FOREIGN KEY (partner_id) REFERENCES rp_partners(partner_id) ON DELETE CASCADE
);

-- =====================================================================
-- PARTNER SESSIONS (was: Driver_Session)
-- =====================================================================
CREATE TABLE rp_partner_sessions (
    session_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    partner_id      UUID UNIQUE NOT NULL,
    status          VARCHAR(50) NOT NULL,
    last_known_lat  DECIMAL(9, 6),
    last_known_lng  DECIMAL(9, 6),
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT fk_session_partner FOREIGN KEY (partner_id) REFERENCES rp_partners(partner_id) ON DELETE CASCADE
);

-- =====================================================================
-- TRIPS (was: Ride)
-- =====================================================================
CREATE TABLE rp_trips (
    trip_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    passenger_id        UUID NOT NULL,
    partner_id          UUID,
    vehicle_id          UUID,
    fare_id             UUID,
    surge_id            UUID,
    offer_id            UUID,
    status              VARCHAR(50) NOT NULL,
    requested_at        TIMESTAMPTZ DEFAULT NOW(),
    completed_at        TIMESTAMPTZ,
    cancelled_at        TIMESTAMPTZ,
    cancel_reason       VARCHAR(255),
    final_fare          DECIMAL(10, 2),
    partner_earning     DECIMAL(10, 2),
    distance_km         DECIMAL(8, 2),
    duration_min        INTEGER,
    pickup_address      TEXT NOT NULL,
    pickup_lat          DECIMAL(9, 6) NOT NULL,
    pickup_lng          DECIMAL(9, 6) NOT NULL,
    dropoff_address     TEXT NOT NULL,
    dropoff_lat         DECIMAL(9, 6) NOT NULL,
    dropoff_lng         DECIMAL(9, 6) NOT NULL,
    rated_by_passenger  BOOLEAN DEFAULT FALSE,
    rated_by_partner    BOOLEAN DEFAULT FALSE,
    created_at          TIMESTAMPTZ DEFAULT NOW(),
    updated_at          TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT fk_trip_passenger FOREIGN KEY (passenger_id) REFERENCES rp_passengers(passenger_id),
    CONSTRAINT fk_trip_partner   FOREIGN KEY (partner_id)   REFERENCES rp_partners(partner_id),
    CONSTRAINT fk_trip_vehicle   FOREIGN KEY (vehicle_id)   REFERENCES rp_vehicles(vehicle_id),
    CONSTRAINT fk_trip_fare      FOREIGN KEY (fare_id)      REFERENCES rp_fare_rules(fare_id),
    CONSTRAINT fk_trip_surge     FOREIGN KEY (surge_id)     REFERENCES rp_surge_pricing(surge_id),
    CONSTRAINT fk_trip_offer     FOREIGN KEY (offer_id)     REFERENCES rp_offers(offer_id)
);

-- =====================================================================
-- PAYMENTS
-- =====================================================================
CREATE TABLE rp_payments (
    payment_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trip_id         UUID UNIQUE NOT NULL,
    passenger_id    UUID NOT NULL,
    method          VARCHAR(50) NOT NULL,
    amount          DECIMAL(10, 2) NOT NULL,
    status          VARCHAR(50) NOT NULL,
    transaction_ref VARCHAR(100) UNIQUE,
    paid_at         TIMESTAMPTZ DEFAULT NOW(),
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT fk_payment_trip      FOREIGN KEY (trip_id)      REFERENCES rp_trips(trip_id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_passenger FOREIGN KEY (passenger_id) REFERENCES rp_passengers(passenger_id)
);

-- =====================================================================
-- WALLET TRANSACTIONS
-- =====================================================================
CREATE TABLE rp_wallet_transactions (
    txn_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id       UUID NOT NULL,
    trip_id         UUID,
    movement        VARCHAR(20) NOT NULL CHECK (movement IN ('CREDIT', 'DEBIT')),
    purpose         VARCHAR(100) NOT NULL,
    amount          DECIMAL(10, 2) NOT NULL,
    balance_after   DECIMAL(12, 2) NOT NULL,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT fk_wallet_txn_wallet FOREIGN KEY (wallet_id) REFERENCES rp_wallets(wallet_id) ON DELETE CASCADE,
    CONSTRAINT fk_wallet_txn_trip   FOREIGN KEY (trip_id)   REFERENCES rp_trips(trip_id)
);

-- =====================================================================
-- RATINGS
-- =====================================================================
CREATE TABLE rp_ratings (
    trip_id                 UUID PRIMARY KEY,
    rating_for_partner      INTEGER CHECK (rating_for_partner BETWEEN 1 AND 5),
    partner_comment         TEXT,
    rating_for_passenger    INTEGER CHECK (rating_for_passenger BETWEEN 1 AND 5),
    passenger_comment       TEXT,
    created_at              TIMESTAMPTZ DEFAULT NOW(),
    updated_at              TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT fk_rating_trip FOREIGN KEY (trip_id) REFERENCES rp_trips(trip_id) ON DELETE CASCADE
);

-- =====================================================================
-- TRIP STATUS LOG (was: Ride_Status_Log)
-- =====================================================================
CREATE TABLE rp_trip_status_log (
    log_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trip_id         UUID NOT NULL,
    status          VARCHAR(50) NOT NULL,
    changed_by      VARCHAR(50) NOT NULL,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT fk_status_log_trip FOREIGN KEY (trip_id) REFERENCES rp_trips(trip_id) ON DELETE CASCADE
);

-- =====================================================================
-- INDEXES
-- =====================================================================
CREATE INDEX idx_trip_passenger      ON rp_trips(passenger_id);
CREATE INDEX idx_trip_partner        ON rp_trips(partner_id);
CREATE INDEX idx_trip_status         ON rp_trips(status);
CREATE INDEX idx_vehicle_category    ON rp_vehicles(category);
CREATE INDEX idx_wallet_txn_wallet   ON rp_wallet_transactions(wallet_id);
CREATE INDEX idx_payment_passenger   ON rp_payments(passenger_id);
CREATE INDEX idx_partner_email       ON rp_partners(email);
CREATE INDEX idx_passenger_email     ON rp_passengers(email);

-- =====================================================================
-- AUTO-UPDATE updated_at TRIGGER
-- =====================================================================
CREATE OR REPLACE FUNCTION rp_update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply trigger to all tables with updated_at
DO $$
DECLARE
    tbl TEXT;
BEGIN
    FOR tbl IN
        SELECT table_name FROM information_schema.columns
        WHERE table_schema = 'public'
          AND column_name = 'updated_at'
          AND table_name LIKE 'rp_%'
    LOOP
        EXECUTE format(
            'CREATE TRIGGER trg_%s_updated_at BEFORE UPDATE ON %I FOR EACH ROW EXECUTE FUNCTION rp_update_timestamp();',
            tbl, tbl
        );
    END LOOP;
END;
$$;
