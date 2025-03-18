package foundation.core.domain.common.vo;

import java.util.UUID;

public class Identifier {
    private UUID id;

    public Identifier() {
        this.id = UUID.randomUUID();
    }

    public Identifier(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
