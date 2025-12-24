-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    avatar_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    is_guest BOOLEAN NOT NULL DEFAULT FALSE,
    auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL' CHECK (auth_provider IN ('LOCAL', 'GOOGLE', 'GITHUB'))
);

-- Create user_preferences table
CREATE TABLE user_preferences (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    skill_level VARCHAR(20) NOT NULL DEFAULT 'BEGINNER',
    household_size INT NOT NULL DEFAULT 1,
    measurement_system VARCHAR(10) NOT NULL DEFAULT 'METRIC',
    default_servings INT NOT NULL DEFAULT 4
);

-- Create collection tables for arrays
CREATE TABLE user_dietary_restrictions (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    restriction VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, restriction)
);

CREATE TABLE user_allergies (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    allergy VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_id, allergy)
);

CREATE TABLE user_cuisine_preferences (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    cuisine VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_id, cuisine)
);

-- Create indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_auth_provider ON users(auth_provider);
CREATE INDEX idx_users_is_guest ON users(is_guest);
