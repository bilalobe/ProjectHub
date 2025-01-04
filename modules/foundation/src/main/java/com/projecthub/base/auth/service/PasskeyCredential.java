import com.projecthub.base.user.domain.entity.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "passkey_credentials")
@Getter
@Setter
public class PasskeyCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false, unique = true)
    private String credentialId;

    @Column(nullable = false)
    private String publicKey;

    @Column(nullable = false)
    private String aaguid;

    @Column(nullable = false)
    private Long signatureCount;

    @Column(nullable = false)
    private LocalDateTime registrationTime;

    @Column(nullable = false)
    private LocalDateTime lastUsedTime;

    private String deviceName;
}
