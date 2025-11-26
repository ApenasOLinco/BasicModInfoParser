package me.andreasmelone.basicmodinfoparser.platform.dependency.version;

import java.util.Optional;

@FunctionalInterface
public interface VersionFactory<T extends Version> {
    Optional<T> parseVersion(final String versionString);
}
