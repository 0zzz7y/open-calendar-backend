-- Table: calendar
CREATE TABLE IF NOT EXISTS calendar (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    emoji TEXT NOT NULL
);

-- Table: category
CREATE TABLE IF NOT EXISTS category (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    color TEXT NOT NULL
);

-- Table: event
CREATE TABLE IF NOT EXISTS event (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    recurring_pattern TEXT NOT NULL,
    calendar_id UUID NOT NULL,
    category_id UUID,
    CONSTRAINT fk_event_calendar FOREIGN KEY (calendar_id) REFERENCES calendar(id) ON DELETE CASCADE,
    CONSTRAINT fk_event_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

-- Table: task
CREATE TABLE IF NOT EXISTS task (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL,
    calendar_id UUID NOT NULL,
    category_id UUID,
    CONSTRAINT fk_task_calendar FOREIGN KEY (calendar_id) REFERENCES calendar(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

-- Table: note
CREATE TABLE IF NOT EXISTS note (
    id UUID PRIMARY KEY,
    name TEXT,
    description TEXT NOT NULL,
    calendar_id UUID NOT NULL,
    category_id UUID,
    CONSTRAINT fk_note_calendar FOREIGN KEY (calendar_id) REFERENCES calendar(id) ON DELETE CASCADE,
    CONSTRAINT fk_note_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);
