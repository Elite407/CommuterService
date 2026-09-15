-- =====================================================================
-- MIGRATION SCRIPT: Old Schema → Rebranded Schema (rp_*)
-- =====================================================================
-- This script migrates data from the original RideVault/CommuterService
-- table names to the new rp_* prefixed tables with UUID primary keys.
--
-- IMPORTANT: 
--   1. Run DDL_v2.sql FIRST to create the new tables.
--   2. Both old and new tables must exist in the same database.
--   3. This script uses gen_random_uuid() for new UUIDs.
--   4. After verifying the migration, you can DROP the old tables.
-- =====================================================================

-- Step 1: Create temporary mapping tables to track old INT → new UUID mappings

CREATE TEMP TABLE _map_passengers (old_id INTEGER PRIMARY KEY, new_id UUID NOT NULL);
CREATE TEMP TABLE _map_partners  (old_id INTEGER PRIMARY KEY, new_id UUID NOT NULL);
CREATE TEMP TABLE _map_fare_rules(old_id INTEGER PRIMARY KEY, new_id UUID NOT NULL);
CREATE TEMP TABLE _map_surge     (old_id INTEGER PRIMARY KEY, new_id UUID NOT NULL);
CREATE TEMP TABLE _map_offers    (old_id INTEGER PRIMARY KEY, new_id UUID NOT NULL);
CREATE TEMP TABLE _map_vehicles  (old_id INTEGER PRIMARY KEY, new_id UUID NOT NULL);
CREATE TEMP TABLE _map_wallets   (old_id INTEGER PRIMARY KEY, new_id UUID NOT NULL);
CREATE TEMP TABLE _map_trips     (old_id INTEGER PRIMARY KEY, new_id UUID NOT NULL);

-- Step 2: Migrate passengers (was: Rider)
INSERT INTO rp_passengers (passenger_id, first_name, middle_name, last_name, email, phone_no, password_hash, is_verified, created_at)
SELECT gen_random_uuid(), firstname, middlename, lastname, email, phone_no, password_hash, is_verified, created_at
FROM Rider;

INSERT INTO _map_passengers (old_id, new_id)
SELECT r.rider_id, p.passenger_id
FROM Rider r
JOIN rp_passengers p ON r.email = p.email;

-- Step 3: Migrate partners (was: Driver)
INSERT INTO rp_partners (partner_id, first_name, middle_name, last_name, email, phone_no, password_hash, license_no, avg_rating, total_trips, is_verified, created_at)
SELECT gen_random_uuid(), firstname, middlename, lastname, email, phone_no, password_hash, license_no, avg_rating, total_rides, is_verified, created_at
FROM Driver;

INSERT INTO _map_partners (old_id, new_id)
SELECT d.driver_id, p.partner_id
FROM Driver d
JOIN rp_partners p ON d.email = p.email;

-- Step 4: Migrate fare rules
INSERT INTO rp_fare_rules (fare_id, vehicle_category, base_fare, per_km_rate, per_min_rate, min_fare)
SELECT gen_random_uuid(), vehicle_category, base_fare, per_km_rate, per_min_rate, min_fare
FROM FARE_RULES;

INSERT INTO _map_fare_rules (old_id, new_id)
SELECT f.fare_id, r.fare_id
FROM FARE_RULES f
JOIN rp_fare_rules r ON f.vehicle_category = r.vehicle_category AND f.base_fare = r.base_fare;

-- Step 5: Migrate surge pricing
INSERT INTO rp_surge_pricing (surge_id, zone_name, lat_min, lat_max, lng_min, lng_max, day_of_week, start_time, end_time, multiplier, is_active)
SELECT gen_random_uuid(), zone_name, lat_min, lat_max, lng_min, lng_max, day_of_week, start_time, end_time, multiplier, is_active
FROM SURGE_PRICING;

INSERT INTO _map_surge (old_id, new_id)
SELECT s.surge_id, r.surge_id
FROM SURGE_PRICING s
JOIN rp_surge_pricing r ON s.zone_name = r.zone_name AND s.day_of_week = r.day_of_week AND s.start_time = r.start_time;

-- Step 6: Migrate offers
INSERT INTO rp_offers (offer_id, promo_code, discount_pct, flat_discount, max_discount, min_trip_value, valid_from, valid_until, usage_limit, total_used, is_active)
SELECT gen_random_uuid(), promo_code, discount_pct, flat_discount, max_discount, min_ride_value, valid_from, valid_until, usage_limit, total_used, is_active
FROM Offers;

INSERT INTO _map_offers (old_id, new_id)
SELECT o.offer_id, r.offer_id
FROM Offers o
JOIN rp_offers r ON o.promo_code = r.promo_code;

-- Step 7: Migrate vehicles
INSERT INTO rp_vehicles (vehicle_id, partner_id, model, year, plate_no, category, is_active, is_verified, insurance_policy_no, insurance_expiry_date)
SELECT gen_random_uuid(), mp.new_id, v.model, v.year, v.plate_no, v.category, v.is_active, v.is_verified, v.insurance_policy_no, v.insurance_expiry_date
FROM VEHICLE v
JOIN _map_partners mp ON v.driver_id = mp.old_id;

INSERT INTO _map_vehicles (old_id, new_id)
SELECT v.vehicle_id, r.vehicle_id
FROM VEHICLE v
JOIN rp_vehicles r ON v.plate_no = r.plate_no;

-- Step 8: Migrate wallets
INSERT INTO rp_wallets (wallet_id, partner_id, balance)
SELECT gen_random_uuid(), mp.new_id, w.balance
FROM WALLET w
JOIN _map_partners mp ON w.driver_id = mp.old_id;

INSERT INTO _map_wallets (old_id, new_id)
SELECT w.wallet_id, r.wallet_id
FROM WALLET w
JOIN _map_partners mp ON w.driver_id = mp.old_id
JOIN rp_wallets r ON r.partner_id = mp.new_id;

-- Step 9: Migrate partner sessions
INSERT INTO rp_partner_sessions (session_id, partner_id, status, last_known_lat, last_known_lng)
SELECT gen_random_uuid(), mp.new_id, ds.status, ds.last_known_lat, ds.last_known_lng
FROM Driver_Session ds
JOIN _map_partners mp ON ds.driver_id = mp.old_id;

-- Step 10: Migrate trips (was: Ride) — the big one with many FKs
INSERT INTO rp_trips (
    trip_id, passenger_id, partner_id, vehicle_id, fare_id, surge_id, offer_id,
    status, requested_at, completed_at, cancelled_at, cancel_reason,
    final_fare, partner_earning, distance_km, duration_min,
    pickup_address, pickup_lat, pickup_lng,
    dropoff_address, dropoff_lat, dropoff_lng,
    rated_by_passenger, rated_by_partner
)
SELECT
    gen_random_uuid(),
    mp.new_id,
    mpr.new_id,
    mv.new_id,
    mf.new_id,
    ms.new_id,
    mo.new_id,
    r.status, r.requested_at, r.completed_at, r.cancelled_at, r.cancel_reason,
    r.final_fare, r.driver_earning, r.distance_km, r.duration_min,
    r.pickup_address, r.pickup_lat, r.pickup_lng,
    r.dropoff_address, r.dropoff_lat, r.dropoff_lng,
    r.rated_by_rider, r.rated_by_driver
FROM Ride r
JOIN _map_passengers mp  ON r.rider_id  = mp.old_id
LEFT JOIN _map_partners mpr ON r.driver_id = mpr.old_id
LEFT JOIN _map_vehicles mv  ON r.vehicle_id = mv.old_id
LEFT JOIN _map_fare_rules mf ON r.fare_id = mf.old_id
LEFT JOIN _map_surge ms     ON r.surge_id = ms.old_id
LEFT JOIN _map_offers mo    ON r.offer_id = mo.old_id;

INSERT INTO _map_trips (old_id, new_id)
SELECT r.ride_id, t.trip_id
FROM Ride r
JOIN _map_passengers mp ON r.rider_id = mp.old_id
JOIN rp_trips t ON t.passenger_id = mp.new_id AND t.requested_at = r.requested_at;

-- Step 11: Migrate payments
INSERT INTO rp_payments (payment_id, trip_id, passenger_id, method, amount, status, transaction_ref, paid_at)
SELECT gen_random_uuid(), mt.new_id, mp.new_id, p.method, p.amount, p.status, p.transaction_ref, p.paid_at
FROM Payment p
JOIN _map_trips mt ON p.ride_id = mt.old_id
JOIN _map_passengers mp ON p.rider_id = mp.old_id;

-- Step 12: Migrate wallet transactions
INSERT INTO rp_wallet_transactions (txn_id, wallet_id, trip_id, movement, purpose, amount, balance_after)
SELECT gen_random_uuid(), mw.new_id, mt.new_id, wt.movement, wt.purpose, wt.amount, wt.balance_after
FROM WALLET_TRANSACTIONS wt
JOIN _map_wallets mw ON wt.wallet_id = mw.old_id
LEFT JOIN _map_trips mt ON wt.ride_id = mt.old_id;

-- Step 13: Migrate ratings
INSERT INTO rp_ratings (trip_id, rating_for_partner, partner_comment, rating_for_passenger, passenger_comment)
SELECT mt.new_id, r.rating_for_driver, r.driver_comment, r.rating_for_rider, r.rider_comment
FROM Rating r
JOIN _map_trips mt ON r.ride_id = mt.old_id;

-- Step 14: Migrate trip status log
INSERT INTO rp_trip_status_log (log_id, trip_id, status, changed_by, created_at)
SELECT gen_random_uuid(), mt.new_id, rsl.status, rsl.change_by, rsl.created_at
FROM Ride_Status_Log rsl
JOIN _map_trips mt ON rsl.ride_id = mt.old_id;

-- =====================================================================
-- VERIFICATION: Compare row counts
-- =====================================================================
SELECT 'Passengers' AS entity, (SELECT COUNT(*) FROM Rider) AS old_count, (SELECT COUNT(*) FROM rp_passengers) AS new_count
UNION ALL
SELECT 'Partners',  (SELECT COUNT(*) FROM Driver),  (SELECT COUNT(*) FROM rp_partners)
UNION ALL
SELECT 'Trips',     (SELECT COUNT(*) FROM Ride),     (SELECT COUNT(*) FROM rp_trips)
UNION ALL
SELECT 'Wallets',   (SELECT COUNT(*) FROM WALLET),   (SELECT COUNT(*) FROM rp_wallets)
UNION ALL
SELECT 'Vehicles',  (SELECT COUNT(*) FROM VEHICLE),  (SELECT COUNT(*) FROM rp_vehicles)
UNION ALL
SELECT 'Payments',  (SELECT COUNT(*) FROM Payment),  (SELECT COUNT(*) FROM rp_payments)
UNION ALL
SELECT 'Ratings',   (SELECT COUNT(*) FROM Rating),   (SELECT COUNT(*) FROM rp_ratings);

-- =====================================================================
-- CLEANUP: After verifying migration, uncomment to drop old tables
-- =====================================================================
-- DROP TABLE IF EXISTS Ride_Status_Log CASCADE;
-- DROP TABLE IF EXISTS Rating CASCADE;
-- DROP TABLE IF EXISTS WALLET_TRANSACTIONS CASCADE;
-- DROP TABLE IF EXISTS Payment CASCADE;
-- DROP TABLE IF EXISTS Ride CASCADE;
-- DROP TABLE IF EXISTS Driver_Session CASCADE;
-- DROP TABLE IF EXISTS WALLET CASCADE;
-- DROP TABLE IF EXISTS VEHICLE CASCADE;
-- DROP TABLE IF EXISTS Offers CASCADE;
-- DROP TABLE IF EXISTS SURGE_PRICING CASCADE;
-- DROP TABLE IF EXISTS FARE_RULES CASCADE;
-- DROP TABLE IF EXISTS Driver CASCADE;
-- DROP TABLE IF EXISTS Rider CASCADE;
