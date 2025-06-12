-- User table
CREATE TABLE IF NOT EXISTS _user (
    id UUID PRIMARY KEY NOT NULL,
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Calendar table
CREATE TABLE IF NOT EXISTS calendar (
    id UUID PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    emoji TEXT NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE (user_id, name),
    FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE
);

-- Category table
CREATE TABLE IF NOT EXISTS category (
    id UUID PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    color TEXT NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE (user_id, name),
    FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE
);

-- Event table
CREATE TABLE IF NOT EXISTS event (
    id UUID PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    recurring_pattern TEXT NOT NULL,
    calendar_id UUID NOT NULL,
    category_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (calendar_id) REFERENCES calendar(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

-- Task table
CREATE TABLE IF NOT EXISTS task (
    id UUID PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL,
    calendar_id UUID NOT NULL,
    category_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (calendar_id) REFERENCES calendar(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

-- Note table
CREATE TABLE IF NOT EXISTS note (
    id UUID PRIMARY KEY NOT NULL,
    name TEXT,
    description TEXT NOT NULL,
    calendar_id UUID NOT NULL,
    category_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (calendar_id) REFERENCES calendar(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);
