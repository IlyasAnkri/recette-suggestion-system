-- Event Tracking Table
CREATE TABLE event_tracking (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    user_id VARCHAR(255),
    session_id VARCHAR(255),
    recipe_id VARCHAR(255),
    metadata TEXT
);

CREATE INDEX idx_event_type ON event_tracking(event_type);
CREATE INDEX idx_user_id ON event_tracking(user_id);
CREATE INDEX idx_timestamp ON event_tracking(timestamp);

-- Daily Active Users Table
CREATE TABLE daily_active_users (
    date DATE PRIMARY KEY,
    count INTEGER NOT NULL
);

-- Daily Searches Table
CREATE TABLE daily_searches (
    date DATE PRIMARY KEY,
    count INTEGER NOT NULL,
    avg_ingredients_per_search DECIMAL(10, 2)
);

-- Popular Recipes Table
CREATE TABLE popular_recipes (
    id BIGSERIAL PRIMARY KEY,
    recipe_id VARCHAR(255) NOT NULL,
    recipe_name VARCHAR(500) NOT NULL,
    view_count INTEGER NOT NULL,
    aggregation_date DATE NOT NULL,
    rank INTEGER
);

CREATE INDEX idx_aggregation_date ON popular_recipes(aggregation_date);

-- Substitution Stats Table
CREATE TABLE substitution_stats (
    date DATE PRIMARY KEY,
    count INTEGER NOT NULL,
    avg_substitutions_per_request INTEGER
);
