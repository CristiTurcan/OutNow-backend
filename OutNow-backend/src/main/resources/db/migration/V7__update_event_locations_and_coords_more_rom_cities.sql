-- V7__update_event_locations_and_coords_more_rom_cities.sql

-- 1) Update Romanian events (39–238) across 25 cities
WITH romanian_indexed AS (
    SELECT city, country, latitude, longitude,
           ROW_NUMBER() OVER () AS idx
    FROM (VALUES
              ('Bucharest',       'Romania', 44.4268,  26.1025),
              ('Cluj-Napoca',     'Romania', 46.7712,  23.6236),
              ('Timișoara',       'Romania', 45.7489,  21.2087),
              ('Iași',            'Romania', 47.1585,  27.6014),
              ('Constanța',       'Romania', 44.1598,  28.6348),
              ('Brașov',          'Romania', 45.6580,  25.6012),
              ('Craiova',         'Romania', 44.3302,  23.7949),
              ('Galați',          'Romania', 45.4355,  28.0074),
              ('Ploiești',        'Romania', 44.9460,  26.0138),
              ('Oradea',          'Romania', 47.0722,  21.9217),
              ('Brăila',          'Romania', 45.2693,  27.9576),
              ('Arad',            'Romania', 46.1866,  21.3123),
              ('Pitești',         'Romania', 44.8566,  24.8692),
              ('Sibiu',           'Romania', 45.7983,  24.1256),
              ('Târgu Mureș',     'Romania', 46.5423,  24.5570),
              ('Baia Mare',       'Romania', 47.6584,  23.5823),
              ('Buzău',           'Romania', 45.1500,  26.8333),
              ('Botoșani',        'Romania', 47.7485,  26.6665),
              ('Satu Mare',       'Romania', 47.7926,  22.8858),
              ('Suceava',         'Romania', 47.6514,  26.2550),
              ('Râmnicu Vâlcea',  'Romania', 45.1003,  24.3692),
              ('Târgu Jiu',       'Romania', 45.0333,  23.2833),
              ('Zalău',           'Romania', 47.2027,  23.0575),
              ('Focșani',         'Romania', 45.6860,  27.2731),
              ('Sfântu Gheorghe', 'Romania', 45.8623,  25.7830)
         ) AS t(city, country, latitude, longitude)
),
     romanian_events AS (
         SELECT event_id,
                ((event_id - 39) % 25) + 1 AS idx
FROM event
WHERE event_id BETWEEN 39 AND 238
    )
UPDATE event
SET
    location  = ri.city      || ', ' || ri.country,
    latitude  = ri.latitude,
    longitude = ri.longitude
    FROM romanian_events re
JOIN romanian_indexed ri
ON ri.idx = re.idx
WHERE event.event_id = re.event_id;

-- 2) Update non-Romanian events (239–288) across 10 cities
WITH non_ro_indexed AS (
    SELECT city, country, latitude, longitude,
           ROW_NUMBER() OVER () AS idx
    FROM (VALUES
              ('London',   'UK',        51.5074,   -0.1278),
              ('Paris',    'France',    48.8566,    2.3522),
              ('Berlin',   'Germany',   52.5200,   13.4050),
              ('New York','USA',        40.7128,  -74.0060),
              ('Tokyo',    'Japan',     35.6895,  139.6917),
              ('Sydney',   'Australia', -33.8688, 151.2093),
              ('Madrid',   'Spain',     40.4168,   -3.7038),
              ('Rome',     'Italy',     41.9028,   12.4964),
              ('Dubai',    'UAE',       25.2048,   55.2708),
              ('Toronto',  'Canada',    43.6532,  -79.3832)
         ) AS t(city, country, latitude, longitude)
),
     non_ro_events AS (
         SELECT event_id,
                ((event_id - 239) % 10) + 1 AS idx
FROM event
WHERE event_id BETWEEN 239 AND 288
    )
UPDATE event
SET
    location  = nri.city     || ', ' || nri.country,
    latitude  = nri.latitude,
    longitude = nri.longitude
    FROM non_ro_events ne
JOIN non_ro_indexed nri
ON nri.idx = ne.idx
WHERE event.event_id = ne.event_id;
