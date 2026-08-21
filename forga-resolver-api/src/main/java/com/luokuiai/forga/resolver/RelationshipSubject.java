package com.luokuiai.forga.resolver;

/**
 * Subject value returned by relationship resolution.
 */
public sealed interface RelationshipSubject permits DirectSubject, SubjectSetSubject {
}
