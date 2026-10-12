package de.fhmuenster.skillswap.model;

public enum MatchStatus {
    PENDING, // Matching-Algo hat ein Match gefunden, aber es haben noch nicht beide Studierenden zugestimmt
    ACCEPTED, // Beide Studierenden haben dem Match zugestimmt. Der Termin steht.
    REJECTED, // Mindestens einer der beiden Studierenden hat das Match abgelehnt
    COMPLETED, // OPTIONAL: Termin war in der Vergangenheit und lief gut.
    EXPIRED // OPTIONAL: Datum des vorgeschlagenen Termins ist verstrichen, ohne dass die Studierenden auf ACCEPTED oder REJECTED geklickt haben
}
