-- User table
CREATE TABLE IF NOT EXISTS _user (
    id UUID PRIMARY KEY NOT NULL,
    username VARCHAR(32) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- Calendar table
CREATE TABLE IF NOT EXISTS calendar (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    emoji VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    UNIQUE (user_id, name),
    FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE
);

-- Category table
CREATE TABLE IF NOT EXISTS category (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    color CHAR(7) NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    UNIQUE (user_id, name),
    FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE
);

-- Event table
CREATE TABLE IF NOT EXISTS event (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(4096),
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    recurring_pattern VARCHAR(32) NOT NULL,
    calendar_id UUID NOT NULL,
    category_id UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (calendar_id) REFERENCES calendar(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

-- Task table
CREATE TABLE IF NOT EXISTS task (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(4096),
    status VARCHAR(32) NOT NULL,
    calendar_id UUID NOT NULL,
    category_id UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (calendar_id) REFERENCES calendar(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

-- Note table
CREATE TABLE IF NOT EXISTS note (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255),
    description VARCHAR(4096) NOT NULL,
    calendar_id UUID NOT NULL,
    category_id UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (calendar_id) REFERENCES calendar(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);
