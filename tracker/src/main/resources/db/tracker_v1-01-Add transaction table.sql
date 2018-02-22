CREATE TABLE tracker_v1.transaction (
  id UUID PRIMARY KEY,
  transaction_type BOOLEAN,
  amount DECIMAL(20, 2),
  category VARCHAR(30),
  transaction_date DATE,
  comment TEXT
);