-- V16: Create tickets, ticket_comments and ticket_evidences tables

CREATE TABLE tickets (
    id VARCHAR(36) PRIMARY KEY,
    reporter_id VARCHAR(36) NOT NULL REFERENCES users(id),
    handler_id VARCHAR(36) REFERENCES users(id),
    summary VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    resolution_note TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(36),
    updated_by VARCHAR(36)
);

CREATE INDEX idx_tickets_reporter_id ON tickets(reporter_id);
CREATE INDEX idx_tickets_handler_id ON tickets(handler_id);
CREATE INDEX idx_tickets_status ON tickets(status);

CREATE TABLE ticket_comments (
    id VARCHAR(36) PRIMARY KEY,
    ticket_id VARCHAR(36) NOT NULL REFERENCES tickets(id),
    author_id VARCHAR(36) NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(36),
    updated_by VARCHAR(36)
);

CREATE INDEX idx_ticket_comments_ticket_id ON ticket_comments(ticket_id);

CREATE TABLE ticket_evidences (
    id SERIAL PRIMARY KEY,
    ticket_id VARCHAR(36) REFERENCES tickets(id),
    comment_id VARCHAR(36) REFERENCES ticket_comments(id),
    file_id VARCHAR(36) NOT NULL REFERENCES files(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(36),
    updated_by VARCHAR(36)
);

CREATE INDEX idx_ticket_evidences_ticket_id ON ticket_evidences(ticket_id);
CREATE INDEX idx_ticket_evidences_comment_id ON ticket_evidences(comment_id);
CREATE INDEX idx_ticket_evidences_file_id ON ticket_evidences(file_id);
