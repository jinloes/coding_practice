START TRANSACTION;

INSERT INTO users(email)
SELECT
  'user_' || seq || '@test.com' AS email
FROM GENERATE_SERIES(1, 5) seq;

COMMIT;