-- Table: calendar
CREATE TABLE IF NOT EXISTS calendar (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL UNIQUE,
    emoji TEXT NOT NULL
);

-- Table: category
CREATE TABLE IF NOT EXISTS category (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL UNIQUE,
    color TEXT NOT NULL
);

-- Table: event
CREATE TABLE IF NOT EXISTS event (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    start_date TEXT NOT NULL,
    end_date TEXT NOT NULL,
    recurring_pattern TEXT NOT NULL,
    calendar_id TEXT NOT NULL,
    category_id TEXT,
    FOREIGN KEY (calendar_id) REFERENCES calendar(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

-- Table: task
CREATE TABLE IF NOT EXISTS task (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL,
    calendar_id TEXT NOT NULL,
    category_id TEXT,
    FOREIGN KEY (calendar_id) REFERENCES calendar(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

-- Table: note
CREATE TABLE IF NOT EXISTS note (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT,
    description TEXT NOT NULL,
    calendar_id TEXT NOT NULL,
    category_id TEXT,
    FOREIGN KEY (calendar_id) REFERENCES calendar(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);
