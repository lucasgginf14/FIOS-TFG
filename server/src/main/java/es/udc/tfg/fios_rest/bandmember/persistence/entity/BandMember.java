package es.udc.tfg.fios_rest.bandmember.persistence.entity;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
  name = "band_member",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_band_member_band_user", columnNames = {"band_id", "user_id"})
  },
  indexes = {
    @Index(name = "idx_band_member_band", columnList = "band_id"),
    @Index(name = "idx_band_member_user", columnList = "user_id"),
    @Index(name = "idx_band_member_active", columnList = "active"),
    @Index(name = "idx_band_member_role", columnList = "role_in_band")
  }
)
public class BandMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "band_id", nullable = false)
  private Band band;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "role_in_band", nullable = false, length = 30)
  private BandMemberRole roleInBand;

  @Column(name = "join_date", nullable = false, updatable = false)
  private LocalDateTime joinDate;

  @Column(name = "leave_date")
  private LocalDateTime leaveDate;

  @Column(name = "active", nullable = false)
  private boolean active;

  protected BandMember() {
    // Constructor vacio requerido por JPA
  }

  public BandMember(Band band, User user, BandMemberRole roleInBand) {
    this.band = Objects.requireNonNull(band, "band no puede ser null");
    this.user = Objects.requireNonNull(user, "user no puede ser null");
    this.roleInBand = Objects.requireNonNull(roleInBand, "roleInBand no puede ser null");
    this.active = true;
  }

  @PrePersist
  protected void onCreate() {
    if (joinDate == null) {
      joinDate = LocalDateTime.now();
    }
  }

  public Long getId() {
    return id;
  }

  public Band getBand() {
    return band;
  }

  public void setBand(Band band) {
    this.band = Objects.requireNonNull(band, "band no puede ser null");
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = Objects.requireNonNull(user, "user no puede ser null");
  }

  public BandMemberRole getRoleInBand() {
    return roleInBand;
  }

  public void setRoleInBand(BandMemberRole roleInBand) {
    this.roleInBand = Objects.requireNonNull(roleInBand, "roleInBand no puede ser null");
  }

  public LocalDateTime getJoinDate() {
    return joinDate;
  }

  public LocalDateTime getLeaveDate() {
    return leaveDate;
  }

  public boolean isActive() {
    return active;
  }

  public void deactivate() {
    active = false;
    if (leaveDate == null) {
      leaveDate = LocalDateTime.now();
    }
  }

  public void reactivate(BandMemberRole roleInBand) {
    this.roleInBand = Objects.requireNonNull(roleInBand, "roleInBand no puede ser null");
    this.active = true;
    this.leaveDate = null;
  }

  public boolean isLeader() {
    return BandMemberRole.LEADER.equals(roleInBand);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BandMember other)) return false;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
