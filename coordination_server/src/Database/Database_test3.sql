SET SQL_SAFE_UPDATES = 0;

use database_test;

-- test insert transactions
/*
insert into transaction (bicycle_id, user_id, taken_locker, taken_timestamp)
values 
("AAITBACZ89", 10101010, 34, Now() );
*/

-- test update transactions
/*
update transaction set returned_locker = 34, returned_timestamp = NOW()
where bicycle_id = "AAITBACZ89" AND user_id = 10101010;
*/

-- test update transactions in time order
/*
update transaction set returned_locker = 34, returned_timestamp = NOW()
WHERE bicycle_id = "AAITBACZ89" AND ISNULL(returned_timestamp);
*/

-- select id from locker_set where port = 8003;

-- test select lockers
-- SELECT * FROM locker_set WHERE Coordinator_id = 3;

-- test with
-- WITH test as (select name from locker_set)
-- select name from test;

-- select max(taken_timestamp) from transaction;

select * from locker_set;
select * from coordinator;
select * from bicycle;
select * from transaction;

/*
SELECT
  c1.*
FROM
  (SELECT
     c.id AS id,
     c.ip AS ip,
     c.port AS port,
     AVG(l.location_latitude) AS location_latitude,
     AVG(l.location_longitude) AS location_longitude
   FROM
     coordinator AS c
     JOIN locker_set AS l ON (l.coordinator_id = c.id)
   GROUP BY c.id) AS c1,
  coordinator AS c2
  JOIN locker_set AS l2 ON (l2.coordinator_id = c2.id)
WHERE
  c2.id = 1 AND
  c2.id <> c1.id
GROUP BY
  c1.id, c2.id
ORDER BY
  POW(c1.location_longitude-AVG(l2.location_longitude),2)+POW(c1.location_latitude-AVG(l2.location_latitude),2)
  DESC
LIMIT 2;
*/

-- update locker_set set coordinator_id = null where id = 62;
/*
SELECT ip, port
FROM locker_set 
UNION
SELECT ip, port 
FROM coordinator;
*/
update locker_set set coordinator_id = null;