package com.luokuiai.forga.core.eval;

/**
 * Subject shape used for reverse relationship lookups.
 */
public sealed interface ReverseLookupSubject
    permits DirectReverseLookupSubject, SubjectSetReverseLookupSubject {
}
