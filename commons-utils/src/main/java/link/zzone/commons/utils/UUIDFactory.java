package link.zzone.commons.utils;

import java.util.UUID;

/**
 * Common interface to sources of various versions of UUIDs.
 */
public interface UUIDFactory {
    /**
     * Generates a new version 4 UUID.
     *
     * @return the newly generated UUID
     */
    UUID generateRandomUuid();
}