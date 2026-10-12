--tabelle für user, musste app_user nutzen weil user keyword reserviert ist
CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    studiengang VARCHAR(255)
);

-- tabelle für verfügbare termine
CREATE TABLE user_verfuegbare_termine (
    user_id UUID NOT NULL,
    verfuegbare_termine TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_termine FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

--tabelle offers
CREATE TABLE offer (
    id UUID PRIMARY KEY,
    titel VARCHAR(255) NOT NULL,
    beschreibung TEXT,
    erstellt_am TIMESTAMP NOT NULL,
    user_id UUID,
    CONSTRAINT fk_offer_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE SET NULL
);

-- tabelle für gesuche
CREATE TABLE enquiry (
    id UUID PRIMARY KEY,
    titel VARCHAR(255) NOT NULL,
    beschreibung TEXT,
    erstellt_am TIMESTAMP NOT NULL,
    user_id UUID,
    CONSTRAINT fk_enquiry_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE SET NULL
);

--tabelle für matches
CREATE TABLE matches (
    id UUID PRIMARY KEY,
    matchscore INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    offer_id UUID,
    enquiry_id UUID,
    vereinbarter_termin TIMESTAMP,
    CONSTRAINT fk_match_offer FOREIGN KEY (offer_id) REFERENCES offer(id) ON DELETE SET NULL,
    CONSTRAINT fk_match_enquiry FOREIGN KEY (enquiry_id) REFERENCES enquiry(id) ON DELETE SET NULL
);