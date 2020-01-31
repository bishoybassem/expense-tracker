CREATE TABLE tracker_v1.transaction (
  id UUID PRIMARY KEY,
  transaction_type BOOLEAN NOT NULL,
  amount DECIMAL(20, 2) NOT NULL,
  category VARCHAR(30),
  transaction_date DATE NOT NULL,
  comment TEXT
);