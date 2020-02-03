CREATE TABLE authenticator_v1.app_user (
  id UUID PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  password TEXT NOT NULL
);