<template>
  <div class="admin-page">
    <div class="container py-5">
      <header class="admin-header">
        <div class="admin-header__main">
          <div class="admin-header__mark" aria-hidden="true">
            <i class="bi bi-shield-lock"></i>
          </div>
          <div class="admin-header__copy">
            <span class="admin-header__eyebrow">{{ t("admin.header.eyebrow") }}</span>
            <h1>{{ t("admin.header.title") }}</h1>
            <p>{{ t("admin.header.subtitle") }}</p>
          </div>
        </div>
      </header>

      <div v-if="pageNotice" class="admin-notice" :class="`admin-notice--${pageNotice.type}`">
        {{ pageNotice.message }}
      </div>

      <section v-if="!isAdmin" class="admin-panel admin-panel--403">
        <div class="admin-forbidden">
          <div class="admin-forbidden__icon">
            <i class="bi bi-shield-lock"></i>
          </div>
          <span class="admin-forbidden__code">403</span>
          <h2>{{ t("admin.forbidden.title") }}</h2>
          <p>{{ t("admin.forbidden.text") }}</p>
          <button type="button" class="btn btn-success" @click="router.push({ name: 'Home' })">
            {{ t("admin.forbidden.action") }}
          </button>
        </div>
      </section>

      <template v-else>
        <section class="admin-metrics">
          <AdminMetricCard
            v-for="card in metricCards"
            :key="card.id"
            :label="card.label"
            :value="card.value"
            :subtitle="card.subtitle"
            :icon="card.icon"
            :tone="card.tone"
            :loading="card.loading"
          />
        </section>

        <div v-if="overviewState.error" class="admin-inline-error">
          {{ overviewState.error }}
        </div>

        <section class="admin-shell">
          <AdminSectionTabs
            :model-value="activeSection"
            :sections="sectionItems"
            :label="t('admin.sections.navigation')"
            @update:model-value="handleSectionChange"
          />

          <section class="admin-panel">
            <template v-if="activeSection === 'overview'">
              <div class="admin-section__header">
                <div>
                  <span class="admin-section__eyebrow">{{ t("admin.sections.overview") }}</span>
                  <h2>{{ t("admin.overview.title") }}</h2>
                  <p>{{ t("admin.overview.subtitle") }}</p>
                </div>
              </div>

              <div v-if="initialLoading" class="admin-state-card">
                <div class="spinner-border text-success" role="status"></div>
                <p>{{ t("admin.states.loadingOverview") }}</p>
              </div>

              <div v-else class="admin-summary-grid">
                <article class="admin-summary-card">
                  <div class="admin-summary-card__header">
                    <h3>{{ t("admin.overview.userBreakdown") }}</h3>
                    <AdminStatusBadge
                      :label="buildVisibleSummary(filteredUsers.length, usersState.items.length)"
                      tone="neutral"
                    />
                  </div>
                  <div v-if="usersState.error" class="admin-summary-card__message">
                    {{ usersState.error }}
                  </div>
                  <ul v-else class="admin-summary-list">
                    <li>
                      <span>{{ t("admin.roles.ADMIN") }}</span>
                      <strong>{{ userRoleCounts.ADMIN || 0 }}</strong>
                    </li>
                    <li>
                      <span>{{ t("admin.roles.USER") }}</span>
                      <strong>{{ userRoleCounts.USER || 0 }}</strong>
                    </li>
                    <li>
                      <span>{{ t("admin.badges.inactiveUsers") }}</span>
                      <strong>{{ inactiveUsersCount }}</strong>
                    </li>
                  </ul>
                </article>

                <article class="admin-summary-card">
                  <div class="admin-summary-card__header">
                    <h3>{{ t("admin.overview.spaceBreakdown") }}</h3>
                    <AdminStatusBadge
                      :label="buildVisibleSummary(filteredSpaces.length, spacesState.items.length)"
                      tone="neutral"
                    />
                  </div>
                  <div v-if="spacesState.error" class="admin-summary-card__message">
                    {{ spacesState.error }}
                  </div>
                  <ul v-else class="admin-summary-list">
                    <li>
                      <span>{{ t("admin.approvalStatuses.PENDING") }}</span>
                      <strong>{{ pendingSpacesCount }}</strong>
                    </li>
                    <li>
                      <span>{{ t("admin.approvalStatuses.APPROVED") }}</span>
                      <strong>{{ approvedSpacesCount }}</strong>
                    </li>
                    <li>
                      <span>{{ t("admin.approvalStatuses.REJECTED") }}</span>
                      <strong>{{ rejectedSpacesCount }}</strong>
                    </li>
                  </ul>
                </article>

                <article class="admin-summary-card">
                  <div class="admin-summary-card__header">
                    <h3>{{ t("admin.overview.reservationBreakdown") }}</h3>
                    <AdminStatusBadge
                      :label="
                        buildVisibleSummary(
                          filteredReservations.length,
                          reservationsState.items.length
                        )
                      "
                      tone="neutral"
                    />
                  </div>
                  <div v-if="reservationsState.error" class="admin-summary-card__message">
                    {{ reservationsState.error }}
                  </div>
                  <ul v-else class="admin-summary-list">
                    <li v-for="state in reservationBreakdownOrder" :key="state">
                      <span>{{ t(`reservations.statuses.${state}`) }}</span>
                      <strong>{{ reservationStateCounts[state] || 0 }}</strong>
                    </li>
                  </ul>
                </article>

                <article class="admin-summary-card">
                  <div class="admin-summary-card__header">
                    <h3>{{ t("admin.overview.securityTitle") }}</h3>
                    <AdminStatusBadge :label="t('admin.header.roleBadge')" tone="success" />
                  </div>
                  <p class="admin-summary-card__message">
                    {{ t("admin.overview.securityText", { email: currentUserEmail }) }}
                  </p>
                  <button
                    type="button"
                    class="btn btn-outline-light btn-sm"
                    @click="handleSectionChange('users')"
                  >
                    {{ t("admin.overview.securityAction") }}
                  </button>
                </article>
              </div>
            </template>

            <template v-else-if="activeSection === 'users'">
              <div class="admin-section__header">
                <div>
                  <span class="admin-section__eyebrow">{{ t("admin.sections.users") }}</span>
                  <h2>{{ t("admin.users.title") }}</h2>
                  <p>{{ t("admin.users.subtitle") }}</p>
                </div>
                <div class="admin-section__meta">
                  {{ buildVisibleSummary(filteredUsers.length, usersState.items.length) }}
                </div>
              </div>

              <div class="admin-toolbar">
                <input
                  v-model="usersState.filters.text"
                  type="search"
                  class="form-control"
                  :placeholder="t('admin.users.filters.search')"
                />
                <AdminSelect
                  v-model="usersState.filters.role"
                  :options="userRoleFilterOptions"
                  :label="t('admin.filters.allRoles')"
                />
                <AdminSelect
                  v-model="usersState.filters.active"
                  :options="userActiveFilterOptions"
                  :label="t('admin.filters.allStates')"
                />
                <button type="button" class="btn btn-outline-light btn-sm" @click="loadUsers(true)">
                  {{ t("admin.actions.refresh") }}
                </button>
              </div>

              <div v-if="usersState.loading" class="admin-state-card">
                <div class="spinner-border text-success" role="status"></div>
                <p>{{ t("admin.states.loadingUsers") }}</p>
              </div>

              <div v-else-if="usersState.error" class="admin-state-card admin-state-card--error">
                <strong>{{ t("admin.states.errorTitle") }}</strong>
                <p>{{ usersState.error }}</p>
                <button type="button" class="btn btn-outline-light btn-sm" @click="loadUsers(true)">
                  {{ t("admin.actions.retry") }}
                </button>
              </div>

              <AdminEmptyState
                v-else-if="!filteredUsers.length"
                :title="t('admin.users.emptyTitle')"
                :text="t('admin.users.emptyText')"
                :action-label="t('admin.actions.resetFilters')"
                @action="resetUsersFilters"
              />

              <div v-else class="admin-table-wrapper">
                <table class="admin-table">
                  <thead>
                    <tr>
                      <th>{{ t("admin.users.columns.name") }}</th>
                      <th>{{ t("admin.users.columns.email") }}</th>
                      <th>{{ t("admin.users.columns.phone") }}</th>
                      <th>{{ t("admin.users.columns.role") }}</th>
                      <th>{{ t("admin.users.columns.status") }}</th>
                      <th>{{ t("admin.users.columns.createdAt") }}</th>
                      <th>{{ t("admin.users.columns.actions") }}</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="user in filteredUsers" :key="user.id">
                      <td :data-label="t('admin.users.columns.name')">
                        <div class="admin-user-cell">
                          <div class="admin-user-cell__avatar">
                            <AppImage
                              :src="user.profileImage"
                              :alt="buildUserName(user) || user.email || t('admin.sections.users')"
                              :fallback-src="avatarPlaceholder"
                              icon-class="bi bi-person"
                            />
                          </div>
                          <strong>{{ buildUserName(user) || "--" }}</strong>
                        </div>
                      </td>
                      <td :data-label="t('admin.users.columns.email')">{{ user.email || "--" }}</td>
                      <td :data-label="t('admin.users.columns.phone')">{{ user.phone || "--" }}</td>
                      <td :data-label="t('admin.users.columns.role')">
                        <AdminStatusBadge
                          :label="t(`admin.roles.${user.platformRole || 'USER'}`)"
                          tone="info"
                        />
                      </td>
                      <td :data-label="t('admin.users.columns.status')">
                        <AdminStatusBadge
                          :label="
                            user.active ? t('admin.badges.active') : t('admin.badges.inactive')
                          "
                          :tone="user.active ? 'success' : 'danger'"
                        />
                      </td>
                      <td :data-label="t('admin.users.columns.createdAt')">
                        {{ formatDateTime(user.createdAt) }}
                      </td>
                      <td :data-label="t('admin.users.columns.actions')">
                        <div class="admin-row-actions" @click.stop @keydown.stop>
                          <button
                            v-if="canPromoteUser(user)"
                            type="button"
                            class="btn btn-outline-success btn-sm"
                            :disabled="usersState.actionId === user.id"
                            @click="promoteUserToAdmin(user)"
                          >
                            {{ t("admin.users.actions.promoteToAdmin") }}
                          </button>
                          <button
                            v-if="canRevokeAdminRole(user)"
                            type="button"
                            class="btn btn-outline-warning btn-sm"
                            :disabled="usersState.actionId === user.id"
                            @click="revokeAdminRole(user)"
                          >
                            {{ t("admin.users.actions.removeAdminRole") }}
                          </button>
                          <button
                            v-if="isCurrentUser(user)"
                            type="button"
                            class="btn btn-outline-light btn-sm"
                            disabled
                            :title="t('admin.users.selfActionBlocked')"
                          >
                            {{ t("admin.users.actions.currentUser") }}
                          </button>
                          <button
                            v-else
                            type="button"
                            class="btn btn-sm"
                            :class="user.active ? 'btn-outline-danger' : 'btn-success'"
                            :disabled="usersState.actionId === user.id"
                            @click="toggleUserActive(user)"
                          >
                            <span
                              v-if="usersState.actionId === user.id"
                              class="spinner-border spinner-border-sm me-2"
                              role="status"
                            ></span>
                            {{
                              user.active
                                ? t("admin.users.actions.deactivate")
                                : t("admin.users.actions.activate")
                            }}
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </template>

            <template v-else-if="activeSection === 'spaces'">
              <div class="admin-section__header">
                <div>
                  <span class="admin-section__eyebrow">{{ t("admin.sections.spaces") }}</span>
                  <h2>{{ t("admin.spaces.title") }}</h2>
                  <p>{{ t("admin.spaces.subtitle") }}</p>
                </div>
                <div class="admin-section__meta">
                  {{ buildVisibleSummary(filteredSpaces.length, spacesState.items.length) }}
                </div>
              </div>

              <div class="admin-toolbar">
                <input
                  v-model="spacesState.filters.text"
                  type="search"
                  class="form-control"
                  :placeholder="t('admin.spaces.filters.search')"
                />
                <AdminSelect
                  v-model="spacesState.filters.status"
                  :options="spaceStatusFilterOptions"
                  :label="t('admin.filters.allStatuses')"
                />
                <input
                  v-model="spacesState.filters.city"
                  type="text"
                  class="form-control"
                  :placeholder="t('admin.spaces.filters.city')"
                />
                <button
                  type="button"
                  class="btn btn-outline-light btn-sm"
                  @click="loadSpaces(true)"
                >
                  {{ t("admin.actions.refresh") }}
                </button>
              </div>

              <div v-if="spacesState.loading" class="admin-state-card">
                <div class="spinner-border text-success" role="status"></div>
                <p>{{ t("admin.states.loadingSpaces") }}</p>
              </div>

              <div v-else-if="spacesState.error" class="admin-state-card admin-state-card--error">
                <strong>{{ t("admin.states.errorTitle") }}</strong>
                <p>{{ spacesState.error }}</p>
                <button
                  type="button"
                  class="btn btn-outline-light btn-sm"
                  @click="loadSpaces(true)"
                >
                  {{ t("admin.actions.retry") }}
                </button>
              </div>

              <AdminEmptyState
                v-else-if="!filteredSpaces.length"
                :title="t('admin.spaces.emptyTitle')"
                :text="t('admin.spaces.emptyText')"
                :action-label="t('admin.actions.resetFilters')"
                @action="resetSpacesFilters"
              />

              <div v-else class="admin-table-wrapper">
                <table class="admin-table">
                  <thead>
                    <tr>
                      <th>{{ t("admin.spaces.columns.name") }}</th>
                      <th>{{ t("admin.spaces.columns.city") }}</th>
                      <th>{{ t("admin.spaces.columns.manager") }}</th>
                      <th>{{ t("admin.spaces.columns.type") }}</th>
                      <th>{{ t("admin.spaces.columns.approvalStatus") }}</th>
                      <th>{{ t("admin.spaces.columns.active") }}</th>
                      <th>{{ t("admin.spaces.columns.actions") }}</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="space in filteredSpaces" :key="space.id">
                      <td :data-label="t('admin.spaces.columns.name')">
                        <strong>{{ space.name || "--" }}</strong>
                      </td>
                      <td :data-label="t('admin.spaces.columns.city')">
                        {{ formatSpaceCity(space) }}
                      </td>
                      <td :data-label="t('admin.spaces.columns.manager')">
                        {{ buildUserName(space.manager) || "--" }}
                      </td>
                      <td :data-label="t('admin.spaces.columns.type')">
                        {{ t(`spaceDetail.spaceTypeLabels.${space.spaceType || "OTHER"}`) }}
                      </td>
                      <td :data-label="t('admin.spaces.columns.approvalStatus')">
                        <AdminStatusBadge
                          :label="t(`admin.approvalStatuses.${space.approvalStatus || 'PENDING'}`)"
                          :tone="getSpaceApprovalTone(space.approvalStatus)"
                        />
                      </td>
                      <td :data-label="t('admin.spaces.columns.active')">
                        <AdminStatusBadge
                          :label="
                            space.active ? t('admin.badges.active') : t('admin.badges.inactive')
                          "
                          :tone="space.active ? 'success' : 'danger'"
                        />
                      </td>
                      <td :data-label="t('admin.spaces.columns.actions')">
                        <div class="admin-row-actions" @click.stop @keydown.stop>
                          <RouterLink
                            v-if="space.active && space.approvalStatus === 'APPROVED'"
                            class="btn btn-outline-light btn-sm"
                            :to="{ name: 'MusicalSpaceAvailabilityManage', params: { id: space.id } }"
                          >
                            {{ t("admin.spaces.actions.availability") }}
                          </RouterLink>
                          <button
                            type="button"
                            class="btn btn-outline-success btn-sm"
                            :disabled="
                              spacesState.actionId === space.id ||
                              space.approvalStatus === 'APPROVED'
                            "
                            @click.stop="updateSpaceApprovalStatus(space, 'APPROVED')"
                          >
                            {{ t("admin.spaces.actions.approve") }}
                          </button>
                          <button
                            type="button"
                            class="btn btn-outline-danger btn-sm"
                            :disabled="
                              spacesState.actionId === space.id ||
                              space.approvalStatus === 'REJECTED'
                            "
                            @click.stop="updateSpaceApprovalStatus(space, 'REJECTED')"
                          >
                            {{ t("admin.spaces.actions.reject") }}
                          </button>
                          <button
                            type="button"
                            class="btn btn-outline-warning btn-sm"
                            :disabled="
                              spacesState.actionId === space.id ||
                              space.approvalStatus === 'PENDING'
                            "
                            @click.stop="updateSpaceApprovalStatus(space, 'PENDING')"
                          >
                            {{ t("admin.spaces.actions.markPending") }}
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </template>

            <template v-else-if="activeSection === 'reservations'">
              <div class="admin-section__header">
                <div>
                  <span class="admin-section__eyebrow">{{ t("admin.sections.reservations") }}</span>
                  <h2>{{ t("admin.reservations.title") }}</h2>
                  <p>{{ t("admin.reservations.subtitle") }}</p>
                </div>
                <div class="admin-section__meta">
                  {{
                    buildVisibleSummary(filteredReservations.length, reservationsState.items.length)
                  }}
                </div>
              </div>

              <div class="admin-toolbar">
                <input
                  v-model="reservationsState.filters.text"
                  type="search"
                  class="form-control"
                  :placeholder="t('admin.reservations.filters.search')"
                />
                <AdminSelect
                  v-model="reservationsState.filters.status"
                  :options="reservationStatusFilterOptions"
                  :label="t('admin.filters.allStatuses')"
                />
                <input v-model="reservationsState.filters.date" type="date" class="form-control" />
                <button
                  type="button"
                  class="btn btn-outline-light btn-sm"
                  @click="loadReservations(true)"
                >
                  {{ t("admin.actions.refresh") }}
                </button>
              </div>

              <div v-if="reservationsState.loading" class="admin-state-card">
                <div class="spinner-border text-success" role="status"></div>
                <p>{{ t("admin.states.loadingReservations") }}</p>
              </div>

              <div
                v-else-if="reservationsState.error"
                class="admin-state-card admin-state-card--error"
              >
                <strong>{{ t("admin.states.errorTitle") }}</strong>
                <p>{{ reservationsState.error }}</p>
                <button
                  type="button"
                  class="btn btn-outline-light btn-sm"
                  @click="loadReservations(true)"
                >
                  {{ t("admin.actions.retry") }}
                </button>
              </div>

              <AdminEmptyState
                v-else-if="!filteredReservations.length"
                :title="t('admin.reservations.emptyTitle')"
                :text="t('admin.reservations.emptyText')"
                :action-label="t('admin.actions.resetFilters')"
                @action="resetReservationsFilters"
              />

              <div v-else class="admin-table-wrapper">
                <table class="admin-table">
                  <thead>
                    <tr>
                      <th>{{ t("admin.reservations.columns.space") }}</th>
                      <th>{{ t("admin.reservations.columns.user") }}</th>
                      <th>{{ t("admin.reservations.columns.date") }}</th>
                      <th>{{ t("admin.reservations.columns.time") }}</th>
                      <th>{{ t("admin.reservations.columns.status") }}</th>
                      <th>{{ t("admin.reservations.columns.price") }}</th>
                      <th>{{ t("admin.reservations.columns.attendees") }}</th>
                      <th>{{ t("admin.reservations.columns.actions") }}</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="reservation in filteredReservations" :key="reservation.id">
                      <td :data-label="t('admin.reservations.columns.space')">
                        <strong>{{ reservation.musicalSpace?.name || "--" }}</strong>
                        <div class="admin-cell__subtext">
                          {{ formatSpaceCity(reservation.musicalSpace) }}
                        </div>
                      </td>
                      <td :data-label="t('admin.reservations.columns.user')">
                        <strong>{{ buildUserName(reservation.user) || "--" }}</strong>
                        <div v-if="reservation.band?.name" class="admin-cell__subtext">
                          {{ reservation.band.name }}
                        </div>
                      </td>
                      <td :data-label="t('admin.reservations.columns.date')">
                        {{ formatDate(reservation.sessionDate) }}
                      </td>
                      <td :data-label="t('admin.reservations.columns.time')">
                        {{ formatTimeRange(reservation.startTime, reservation.endTime) }}
                      </td>
                      <td :data-label="t('admin.reservations.columns.status')">
                        <AdminStatusBadge
                          :label="t(`reservations.statuses.${reservation.state || 'PENDING'}`)"
                          :tone="getReservationTone(reservation.state)"
                        />
                        <div v-if="reservation.cancellationReason" class="admin-cell__subtext">
                          {{ reservation.cancellationReason }}
                        </div>
                      </td>
                      <td :data-label="t('admin.reservations.columns.price')">
                        {{ formatMoney(reservation.finalPrice) }}
                      </td>
                      <td :data-label="t('admin.reservations.columns.attendees')">
                        {{ reservation.attendeesCount ?? "--" }}
                      </td>
                      <td :data-label="t('admin.reservations.columns.actions')">
                        <div
                          v-if="canCancelReservation(reservation)"
                          class="admin-row-actions"
                          @click.stop
                          @keydown.stop
                        >
                          <button
                            type="button"
                            class="btn btn-outline-danger btn-sm"
                            :disabled="reservationsState.actionId === reservation.id"
                            @click="openReservationCancelDialog(reservation)"
                          >
                            {{ t("admin.reservations.actions.cancel") }}
                          </button>
                        </div>
                        <span v-else class="admin-cell__subtext">--</span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </template>

            <template v-else-if="activeSection === 'reviews'">
              <div class="admin-section__header">
                <div>
                  <span class="admin-section__eyebrow">{{ t("admin.sections.reviews") }}</span>
                  <h2>{{ t("admin.reviews.title") }}</h2>
                  <p>{{ t("admin.reviews.subtitle") }}</p>
                </div>
                <div class="admin-section__meta">
                  {{ buildVisibleSummary(filteredReviews.length, reviewsState.items.length) }}
                </div>
              </div>

              <div class="admin-toolbar">
                <input
                  v-model="reviewsState.filters.text"
                  type="search"
                  class="form-control"
                  :placeholder="t('admin.reviews.filters.search')"
                />
                <AdminSelect
                  v-model="reviewsState.filters.rating"
                  :options="reviewRatingFilterOptions"
                  :label="t('admin.filters.allRatings')"
                />
                <button
                  type="button"
                  class="btn btn-outline-light btn-sm"
                  @click="loadReviews(true)"
                >
                  {{ t("admin.actions.refresh") }}
                </button>
              </div>

              <div v-if="reviewsState.loading" class="admin-state-card">
                <div class="spinner-border text-success" role="status"></div>
                <p>{{ t("admin.states.loadingReviews") }}</p>
              </div>

              <div v-else-if="reviewsState.error" class="admin-state-card admin-state-card--error">
                <strong>{{ t("admin.states.errorTitle") }}</strong>
                <p>{{ reviewsState.error }}</p>
                <button
                  type="button"
                  class="btn btn-outline-light btn-sm"
                  @click="loadReviews(true)"
                >
                  {{ t("admin.actions.retry") }}
                </button>
              </div>

              <AdminEmptyState
                v-else-if="!filteredReviews.length"
                :title="t('admin.reviews.emptyTitle')"
                :text="t('admin.reviews.emptyText')"
                :action-label="t('admin.actions.resetFilters')"
                @action="resetReviewsFilters"
              />

              <div v-else class="admin-table-wrapper">
                <table class="admin-table">
                  <thead>
                    <tr>
                      <th>{{ t("admin.reviews.columns.type") }}</th>
                      <th>{{ t("admin.reviews.columns.space") }}</th>
                      <th>{{ t("admin.reviews.columns.user") }}</th>
                      <th>{{ t("admin.reviews.columns.rating") }}</th>
                      <th>{{ t("admin.reviews.columns.comment") }}</th>
                      <th>{{ t("admin.reviews.columns.date") }}</th>
                      <th>{{ t("admin.reviews.columns.actions") }}</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="review in filteredReviews" :key="`${review.reviewType}-${review.id}`">
                      <td :data-label="t('admin.reviews.columns.type')">
                        <AdminStatusBadge
                          :label="t(`admin.reviews.types.${review.reviewType || 'SPACE'}`)"
                          tone="info"
                        />
                      </td>
                      <td :data-label="t('admin.reviews.columns.space')">
                        <strong>{{ getReviewTargetLabel(review) }}</strong>
                        <div
                          v-if="review.reviewType === 'USER' && review.musicalSpace?.name"
                          class="admin-cell__subtext"
                        >
                          {{ review.musicalSpace.name }}
                        </div>
                      </td>
                      <td :data-label="t('admin.reviews.columns.user')">
                        {{ buildUserName(getReviewAuthor(review)) || "--" }}
                      </td>
                      <td :data-label="t('admin.reviews.columns.rating')">
                        <AdminStatusBadge
                          :label="t('admin.reviews.ratingLabel', { value: review.overallRating })"
                          :tone="getRatingTone(review.overallRating)"
                        />
                      </td>
                      <td :data-label="t('admin.reviews.columns.comment')">
                        {{ review.comment || t("admin.reviews.noComment") }}
                      </td>
                      <td :data-label="t('admin.reviews.columns.date')">
                        {{ formatDateTime(review.createdAt) }}
                      </td>
                      <td :data-label="t('admin.reviews.columns.actions')">
                        <div class="admin-row-actions" @click.stop @keydown.stop>
                          <button
                            type="button"
                            class="btn btn-outline-danger btn-sm"
                            :disabled="reviewsState.actionId === `${review.reviewType}-${review.id}`"
                            @click.stop="deleteReview(review)"
                          >
                            {{ t("admin.reviews.actions.delete") }}
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </template>

            <template v-else-if="activeSection === 'events'">
              <div class="admin-section__header">
                <div>
                  <span class="admin-section__eyebrow">{{ t("admin.sections.events") }}</span>
                  <h2>{{ t("admin.events.title") }}</h2>
                  <p>{{ t("admin.events.subtitle") }}</p>
                </div>
                <div class="admin-row-actions">
                  <button
                    type="button"
                    class="btn btn-outline-light btn-sm"
                    @click="loadEvents(true)"
                  >
                    {{ t("admin.actions.refresh") }}
                  </button>
                  <button
                    type="button"
                    class="btn btn-outline-success btn-sm"
                    @click="openTicketmasterModal"
                  >
                    {{ t("admin.events.actions.importTicketmaster") }}
                  </button>
                  <button
                    type="button"
                    class="btn btn-success btn-sm"
                    @click="openCreateEventModal"
                  >
                    {{ t("admin.events.actions.create") }}
                  </button>
                </div>
              </div>

              <div class="admin-toolbar">
                <input
                  v-model="eventsState.filters.text"
                  type="search"
                  class="form-control"
                  :placeholder="t('admin.events.filters.search')"
                />
                <AdminSelect
                  v-model="eventsState.filters.status"
                  :options="eventStatusFilterOptions"
                  :label="t('admin.filters.allStatuses')"
                />
                <AdminSelect
                  v-model="eventsState.filters.source"
                  :options="eventSourceFilterOptions"
                  :label="t('admin.filters.allSources')"
                />
                <input
                  v-model="eventsState.filters.city"
                  type="text"
                  class="form-control"
                  :placeholder="t('admin.events.filters.city')"
                />
              </div>

              <div v-if="eventsState.loading" class="admin-state-card">
                <div class="spinner-border text-success" role="status"></div>
                <p>{{ t("admin.states.loadingEvents") }}</p>
              </div>

              <div v-else-if="eventsState.error" class="admin-state-card admin-state-card--error">
                <strong>{{ t("admin.states.errorTitle") }}</strong>
                <p>{{ eventsState.error }}</p>
                <button
                  type="button"
                  class="btn btn-outline-light btn-sm"
                  @click="loadEvents(true)"
                >
                  {{ t("admin.actions.retry") }}
                </button>
              </div>

              <AdminEmptyState
                v-else-if="!filteredEvents.length"
                :title="t('admin.events.emptyTitle')"
                :text="t('admin.events.emptyText')"
                :action-label="t('admin.actions.resetFilters')"
                @action="resetEventsFilters"
              />

              <div v-else class="admin-table-wrapper">
                <table class="admin-table">
                  <thead>
                    <tr>
                      <th>{{ t("admin.events.columns.title") }}</th>
                      <th>{{ t("admin.events.columns.date") }}</th>
                      <th>{{ t("admin.events.columns.city") }}</th>
                      <th>{{ t("admin.events.columns.source") }}</th>
                      <th>{{ t("admin.events.columns.status") }}</th>
                      <th>{{ t("admin.events.columns.type") }}</th>
                      <th>{{ t("admin.events.columns.actions") }}</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="event in filteredEvents" :key="event.id">
                      <td :data-label="t('admin.events.columns.title')">
                        <strong>{{ event.title || "--" }}</strong>
                        <div class="admin-cell__subtext">{{ event.venueName || "--" }}</div>
                      </td>
                      <td :data-label="t('admin.events.columns.date')">
                        {{ formatDate(event.eventDate) }}
                      </td>
                      <td :data-label="t('admin.events.columns.city')">{{ event.city || "--" }}</td>
                      <td :data-label="t('admin.events.columns.source')">
                        <AdminStatusBadge
                          :label="t(`events.sources.${event.source || 'INTERNAL'}`)"
                          :tone="event.source === 'EXTERNAL' ? 'info' : 'neutral'"
                        />
                      </td>
                      <td :data-label="t('admin.events.columns.status')">
                        <AdminStatusBadge
                          :label="t(`events.statuses.${event.status || 'DRAFT'}`)"
                          :tone="getEventTone(event.status)"
                        />
                      </td>
                      <td :data-label="t('admin.events.columns.type')">
                        {{ t(`events.types.${event.eventType || "OTHER"}`) }}
                      </td>
                      <td :data-label="t('admin.events.columns.actions')">
                        <div class="admin-row-actions" @click.stop @keydown.stop>
                          <button
                            v-if="event.status === 'DRAFT'"
                            type="button"
                            class="btn btn-outline-success btn-sm"
                            :disabled="eventsState.actionId === event.id"
                            @click.stop="publishEvent(event)"
                          >
                            {{ t("admin.events.actions.publish") }}
                          </button>
                          <button
                            type="button"
                            class="btn btn-outline-success btn-sm"
                            :disabled="eventsState.actionId === event.id"
                            @click.stop="openEditEventModal(event)"
                          >
                            {{ t("admin.events.actions.edit") }}
                          </button>
                          <button
                            type="button"
                            class="btn btn-outline-danger btn-sm"
                            :disabled="eventsState.actionId === event.id"
                            @click.stop="archiveEvent(event)"
                          >
                            {{ t("admin.events.actions.archive") }}
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </template>

            <template v-else-if="activeSection === 'recruitments'">
              <div class="admin-section__header">
                <div>
                  <span class="admin-section__eyebrow">{{ t("admin.sections.recruitments") }}</span>
                  <h2>{{ t("admin.recruitments.title") }}</h2>
                  <p>{{ t("admin.recruitments.subtitle") }}</p>
                </div>
                <div class="admin-section__meta">
                  {{
                    buildVisibleSummary(filteredRecruitments.length, recruitmentsState.items.length)
                  }}
                </div>
              </div>

              <div class="admin-toolbar">
                <input
                  v-model="recruitmentsState.filters.text"
                  type="search"
                  class="form-control"
                  :placeholder="t('admin.recruitments.filters.search')"
                />
                <AdminSelect
                  v-model="recruitmentsState.filters.status"
                  :options="recruitmentStatusFilterOptions"
                  :label="t('admin.filters.allStatuses')"
                />
                <AdminSelect
                  v-model="recruitmentsState.filters.instrument"
                  :options="recruitmentInstrumentFilterOptions"
                  :label="t('admin.filters.allInstruments')"
                />
                <button
                  type="button"
                  class="btn btn-outline-light btn-sm"
                  @click="loadRecruitments(true)"
                >
                  {{ t("admin.actions.refresh") }}
                </button>
              </div>

              <div v-if="recruitmentsState.loading" class="admin-state-card">
                <div class="spinner-border text-success" role="status"></div>
                <p>{{ t("admin.states.loadingRecruitments") }}</p>
              </div>

              <div
                v-else-if="recruitmentsState.error"
                class="admin-state-card admin-state-card--error"
              >
                <strong>{{ t("admin.states.errorTitle") }}</strong>
                <p>{{ recruitmentsState.error }}</p>
                <button
                  type="button"
                  class="btn btn-outline-light btn-sm"
                  @click="loadRecruitments(true)"
                >
                  {{ t("admin.actions.retry") }}
                </button>
              </div>

              <AdminEmptyState
                v-else-if="!filteredRecruitments.length"
                :title="t('admin.recruitments.emptyTitle')"
                :text="t('admin.recruitments.emptyText')"
                :action-label="t('admin.actions.resetFilters')"
                @action="resetRecruitmentsFilters"
              />

              <div v-else class="admin-table-wrapper">
                <table class="admin-table">
                  <thead>
                    <tr>
                      <th>{{ t("admin.recruitments.columns.title") }}</th>
                      <th>{{ t("admin.recruitments.columns.band") }}</th>
                      <th>{{ t("admin.recruitments.columns.instrument") }}</th>
                      <th>{{ t("admin.recruitments.columns.level") }}</th>
                      <th>{{ t("admin.recruitments.columns.city") }}</th>
                      <th>{{ t("admin.recruitments.columns.status") }}</th>
                      <th>{{ t("admin.recruitments.columns.publicationDate") }}</th>
                      <th>{{ t("admin.recruitments.columns.actions") }}</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="recruitment in filteredRecruitments" :key="recruitment.id">
                      <td :data-label="t('admin.recruitments.columns.title')">
                        <strong>{{ recruitment.title || "--" }}</strong>
                        <div class="admin-cell__subtext">{{ recruitment.roleWanted || "--" }}</div>
                      </td>
                      <td :data-label="t('admin.recruitments.columns.band')">
                        {{ recruitment.band?.name || "--" }}
                      </td>
                      <td :data-label="t('admin.recruitments.columns.instrument')">
                        {{ recruitment.instrument?.name || "--" }}
                      </td>
                      <td :data-label="t('admin.recruitments.columns.level')">
                        {{ t(`bands.levels.${recruitment.levelRequired || "BEGINNER"}`) }}
                      </td>
                      <td :data-label="t('admin.recruitments.columns.city')">
                        {{ recruitment.city || "--" }}
                      </td>
                      <td :data-label="t('admin.recruitments.columns.status')">
                        <AdminStatusBadge
                          :label="t(`admin.recruitments.statuses.${recruitment.status || 'OPEN'}`)"
                          :tone="recruitment.status === 'OPEN' ? 'success' : 'neutral'"
                        />
                      </td>
                      <td :data-label="t('admin.recruitments.columns.publicationDate')">
                        {{ formatDateTime(recruitment.publicationDate) }}
                      </td>
                      <td :data-label="t('admin.recruitments.columns.actions')">
                        <div class="admin-row-actions" @click.stop @keydown.stop>
                          <button
                            type="button"
                            class="btn btn-outline-danger btn-sm"
                            :disabled="recruitmentsState.actionId === recruitment.id"
                            @click.stop="deleteRecruitment(recruitment)"
                          >
                            {{ t("admin.recruitments.actions.delete") }}
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </template>
          </section>
        </section>
      </template>
    </div>

    <AdminConfirmDialog
      :open="confirmState.open"
      :title="confirmState.title"
      :message="confirmState.message"
      :confirm-label="confirmState.confirmLabel"
      :cancel-label="confirmState.cancelLabel"
      :loading="confirmState.loading"
      :variant="confirmState.variant"
      :require-text="confirmState.requireText"
      :input-label="confirmState.inputLabel"
      :input-placeholder="confirmState.inputPlaceholder"
      :model-value="confirmState.modelValue"
      :error-message="confirmState.errorMessage"
      :eyebrow="t('admin.confirm.eyebrow')"
      @close="closeConfirmDialog"
      @confirm="submitConfirmDialog"
      @update:model-value="confirmState.modelValue = $event"
    />

    <EventAdminModal
      :open="eventModalOpen"
      :model-value="eventForm"
      :editing="Boolean(editingEventId)"
      :submitting="eventModalSubmitting"
      :error-message="eventModalError"
      @close="closeEventModal"
      @submit="submitEventModal"
      @update:model-value="eventForm = $event"
    />

    <TicketmasterImportModal
      :open="ticketmasterModalOpen"
      :model-value="ticketmasterFilters"
      :minimum-start-date="ticketmasterMinimumStartDate"
      :results="ticketmasterResults"
      :searching="ticketmasterSearching"
      :bulk-importing="ticketmasterBulkImporting"
      :importing-id="ticketmasterImportingId"
      :error-message="ticketmasterError"
      :has-searched="ticketmasterHasSearched"
      @close="closeTicketmasterModal"
      @search="searchTicketmaster"
      @import="importTicketmasterEvent"
      @import-all="importTicketmasterResults"
      @update:model-value="ticketmasterFilters = $event"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import auth from "@/common/auth";
import AdminConfirmDialog from "../components/AdminConfirmDialog.vue";
import AdminEmptyState from "../components/AdminEmptyState.vue";
import AdminMetricCard from "../components/AdminMetricCard.vue";
import AdminSectionTabs from "../components/AdminSectionTabs.vue";
import AdminSelect from "../components/AdminSelect.vue";
import AdminStatusBadge from "../components/AdminStatusBadge.vue";
import AppImage from "@/common/components/AppImage.vue";
import EventAdminModal from "@/modules/events/components/EventAdminModal.vue";
import TicketmasterImportModal from "@/modules/events/components/TicketmasterImportModal.vue";
import { getApiErrorMessage } from "@/common/apiErrors";
import avatarPlaceholder from "@/assets/placeholders/avatar-placeholder.svg";
import {
  EVENT_SOURCE_KEYS,
  EVENT_STATUS_KEYS,
  TICKETMASTER_MIN_START_DATE,
  buildAdminEventPayload,
  createEmptyEventForm,
  formatEventMoney,
  getEventTicketValidationKey,
  mapEventToForm,
  normalizeTicketmasterSearch,
  resolveTicketmasterErrorMessage
} from "@/modules/events/eventUtils";
import AdminRepository from "@/repositories/AdminRepository";
import EventRepository from "@/repositories/EventRepository";
import TicketmasterRepository from "@/repositories/TicketmasterRepository";
import { getStore } from "@/common/store";

const SECTION_KEYS = [
  "overview",
  "users",
  "spaces",
  "reservations",
  "reviews",
  "events",
  "recruitments"
];
const RESERVATION_BREAKDOWN_ORDER = ["PENDING", "ACCEPTED", "COMPLETED", "CANCELLED", "REJECTED"];
const SPACE_APPROVAL_OPTIONS = ["PENDING", "APPROVED", "REJECTED"];
const CANCELLABLE_RESERVATION_STATES = ["PENDING", "ACCEPTED"];

const route = useRoute();
const router = useRouter();
const { t, locale } = useI18n();

const initialLoading = ref(true);
const pageNotice = ref(null);
const activeSection = ref(normalizeSection(route.query.section));

const overviewState = reactive({
  data: null,
  loading: false,
  loaded: false,
  error: ""
});

const usersState = createListState({
  text: "",
  role: "",
  active: ""
});

const spacesState = createListState({
  text: "",
  status: "",
  city: ""
});

const reservationsState = createListState({
  text: "",
  status: "",
  date: ""
});

const reviewsState = createListState({
  text: "",
  rating: ""
});

const eventsState = createListState({
  text: "",
  status: "",
  source: "",
  city: ""
});

const recruitmentsState = createListState({
  text: "",
  status: "",
  instrument: ""
});

const confirmState = reactive({
  open: false,
  title: "",
  message: "",
  confirmLabel: "",
  cancelLabel: "",
  variant: "danger",
  requireText: false,
  inputLabel: "",
  inputPlaceholder: "",
  modelValue: "",
  loading: false,
  errorMessage: ""
});

const confirmAction = ref(null);

const eventModalOpen = ref(false);
const eventModalSubmitting = ref(false);
const eventModalError = ref("");
const editingEventId = ref(null);
const eventForm = ref(createEmptyEventForm());

const ticketmasterModalOpen = ref(false);
const ticketmasterSearching = ref(false);
const ticketmasterBulkImporting = ref(false);
const ticketmasterImportingId = ref("");
const ticketmasterError = ref("");
const ticketmasterResults = ref([]);
const ticketmasterHasSearched = ref(false);
const ticketmasterFilters = ref(createTicketmasterFiltersFromAdminEvents());
const ticketmasterMinimumStartDate = TICKETMASTER_MIN_START_DATE;

const isAdmin = computed(() => auth.isAdmin());
const currentLocale = computed(() => locale.value || "es");
const currentUserId = computed(() => getStore().state.user.id);
const currentUserEmail = computed(
  () => getStore().state.user.email || t("admin.fallbacks.currentSession")
);

const userRoleCounts = computed(() => countBy(usersState.items, "platformRole"));
const reservationStateCounts = computed(() => countBy(reservationsState.items, "state"));
const overviewReviewsCount = computed(() => {
  const spaceReviewsCount = Number(overviewState.data?.spaceReviewsCount);
  const userReviewsCount = Number(overviewState.data?.userReviewsCount);

  if (Number.isFinite(spaceReviewsCount) || Number.isFinite(userReviewsCount)) {
    return (Number.isFinite(spaceReviewsCount) ? spaceReviewsCount : 0)
      + (Number.isFinite(userReviewsCount) ? userReviewsCount : 0);
  }

  return undefined;
});

const inactiveUsersCount = computed(() => usersState.items.filter((item) => !item.active).length);
const pendingSpacesCount = computed(
  () => spacesState.items.filter((item) => item.approvalStatus === "PENDING").length
);
const approvedSpacesCount = computed(
  () => spacesState.items.filter((item) => item.approvalStatus === "APPROVED").length
);
const rejectedSpacesCount = computed(
  () => spacesState.items.filter((item) => item.approvalStatus === "REJECTED").length
);
const activeReservationsCount = computed(
  () => reservationsState.items.filter((item) => item.state === "ACCEPTED").length
);
const pendingReservationsCount = computed(
  () => reservationsState.items.filter((item) => item.state === "PENDING").length
);

const userRoleOptions = computed(() => {
  const roles = [...new Set(usersState.items.map((item) => item.platformRole).filter(Boolean))];
  return roles.sort((left, right) => left.localeCompare(right));
});

const reviewRatingOptions = computed(() => {
  const ratings = [
    ...new Set(
      reviewsState.items.map((item) => item.overallRating).filter((value) => value != null)
    )
  ];
  return ratings.sort((left, right) => right - left);
});

const recruitmentStatusOptions = computed(() => {
  const statuses = [...new Set(recruitmentsState.items.map((item) => item.status).filter(Boolean))];
  return statuses.sort((left, right) => left.localeCompare(right));
});

const recruitmentInstrumentOptions = computed(() => {
  const instruments = [
    ...new Set(recruitmentsState.items.map((item) => item.instrument?.name).filter(Boolean))
  ];
  return instruments.sort((left, right) => left.localeCompare(right));
});

const userRoleFilterOptions = computed(() => [
  { value: "", label: t("admin.filters.allRoles") },
  ...userRoleOptions.value.map((role) => ({
    value: role,
    label: t(`admin.roles.${role}`)
  }))
]);

const userActiveFilterOptions = computed(() => [
  { value: "", label: t("admin.filters.allStates") },
  { value: "active", label: t("admin.badges.active") },
  { value: "inactive", label: t("admin.badges.inactive") }
]);

const spaceStatusFilterOptions = computed(() => [
  { value: "", label: t("admin.filters.allStatuses") },
  ...spaceApprovalOptions.map((status) => ({
    value: status,
    label: t(`admin.approvalStatuses.${status}`)
  }))
]);

const reservationStatusFilterOptions = computed(() => [
  { value: "", label: t("admin.filters.allStatuses") },
  ...reservationBreakdownOrder.map((status) => ({
    value: status,
    label: t(`reservations.statuses.${status}`)
  }))
]);

const reviewRatingFilterOptions = computed(() => [
  { value: "", label: t("admin.filters.allRatings") },
  ...reviewRatingOptions.value.map((rating) => ({
    value: String(rating),
    label: t("admin.reviews.filters.ratingValue", { value: rating })
  }))
]);

const eventStatusFilterOptions = computed(() => [
  { value: "", label: t("admin.filters.allStatuses") },
  ...EVENT_STATUS_KEYS.map((status) => ({
    value: status,
    label: t(`events.statuses.${status}`)
  }))
]);

const eventSourceFilterOptions = computed(() => [
  { value: "", label: t("admin.filters.allSources") },
  ...EVENT_SOURCE_KEYS.map((source) => ({
    value: source,
    label: t(`events.sources.${source}`)
  }))
]);

const recruitmentStatusFilterOptions = computed(() => [
  { value: "", label: t("admin.filters.allStatuses") },
  ...recruitmentStatusOptions.value.map((status) => ({
    value: status,
    label: t(`admin.recruitments.statuses.${status}`)
  }))
]);

const recruitmentInstrumentFilterOptions = computed(() => [
  { value: "", label: t("admin.filters.allInstruments") },
  ...recruitmentInstrumentOptions.value.map((instrument) => ({
    value: instrument,
    label: instrument
  }))
]);

const sectionItems = computed(() => [
  { id: "overview", label: t("admin.sections.overview"), count: null },
  {
    id: "users",
    label: t("admin.sections.users"),
    count: resolveCount(overviewState.data?.usersCount, usersState.items.length, usersState.loaded)
  },
  {
    id: "spaces",
    label: t("admin.sections.spaces"),
    count: resolveCount(
      overviewState.data?.musicalSpacesCount,
      spacesState.items.length,
      spacesState.loaded
    )
  },
  {
    id: "reservations",
    label: t("admin.sections.reservations"),
    count: resolveCount(
      overviewState.data?.reservationsCount,
      reservationsState.items.length,
      reservationsState.loaded
    )
  },
  {
    id: "reviews",
    label: t("admin.sections.reviews"),
    count: resolveCount(
      overviewReviewsCount.value,
      reviewsState.items.length,
      reviewsState.loaded
    )
  },
  {
    id: "events",
    label: t("admin.sections.events"),
    count: resolveCount(
      overviewState.data?.eventsCount,
      eventsState.items.length,
      eventsState.loaded
    )
  },
  {
    id: "recruitments",
    label: t("admin.sections.recruitments"),
    count: resolveCount(
      overviewState.data?.bandRecruitmentsCount,
      recruitmentsState.items.length,
      recruitmentsState.loaded
    )
  }
]);

const metricCards = computed(() => [
  {
    id: "users",
    label: t("admin.metrics.users"),
    value: resolveCount(overviewState.data?.usersCount, usersState.items.length, usersState.loaded),
    subtitle: t("admin.metrics.usersSubtitle", { count: userRoleCounts.value.ADMIN || 0 }),
    icon: "bi bi-people",
    tone: "info",
    loading: overviewState.loading && !overviewState.loaded
  },
  {
    id: "pendingSpaces",
    label: t("admin.metrics.pendingSpaces"),
    value: spacesState.loaded ? pendingSpacesCount.value : "--",
    subtitle: t("admin.metrics.pendingSpacesSubtitle", { count: approvedSpacesCount.value }),
    icon: "bi bi-building-check",
    tone: "warning",
    loading: spacesState.loading && !spacesState.loaded
  },
  {
    id: "activeReservations",
    label: t("admin.metrics.activeReservations"),
    value: reservationsState.loaded ? activeReservationsCount.value : "--",
    subtitle: t("admin.metrics.activeReservationsSubtitle", {
      count: pendingReservationsCount.value
    }),
    icon: "bi bi-calendar2-check",
    tone: "success",
    loading: reservationsState.loading && !reservationsState.loaded
  },
  {
    id: "reviews",
    label: t("admin.metrics.reviews"),
    value: resolveCount(
      overviewReviewsCount.value,
      reviewsState.items.length,
      reviewsState.loaded
    ),
    subtitle: t("admin.metrics.reviewsSubtitle"),
    icon: "bi bi-stars",
    tone: "neutral",
    loading: overviewState.loading && !overviewState.loaded
  },
  {
    id: "events",
    label: t("admin.metrics.events"),
    value: resolveCount(
      overviewState.data?.eventsCount,
      eventsState.items.length,
      eventsState.loaded
    ),
    subtitle: t("admin.metrics.eventsSubtitle"),
    icon: "bi bi-calendar-event",
    tone: "info",
    loading: overviewState.loading && !overviewState.loaded
  },
  {
    id: "recruitments",
    label: t("admin.metrics.recruitments"),
    value: resolveCount(
      overviewState.data?.bandRecruitmentsCount,
      recruitmentsState.items.length,
      recruitmentsState.loaded
    ),
    subtitle: t("admin.metrics.recruitmentsSubtitle"),
    icon: "bi bi-person-lines-fill",
    tone: "success",
    loading: overviewState.loading && !overviewState.loaded
  }
]);

const filteredUsers = computed(() => {
  const text = normalizeText(usersState.filters.text);

  return usersState.items.filter((user) => {
    if (usersState.filters.role && user.platformRole !== usersState.filters.role) {
      return false;
    }

    if (usersState.filters.active === "active" && !user.active) {
      return false;
    }

    if (usersState.filters.active === "inactive" && user.active) {
      return false;
    }

    if (!text) {
      return true;
    }

    return normalizeText(
      [buildUserName(user), user.email, user.phone].filter(Boolean).join(" ")
    ).includes(text);
  });
});

const filteredSpaces = computed(() => {
  const text = normalizeText(spacesState.filters.text);
  const cityFilter = normalizeText(spacesState.filters.city);

  return spacesState.items.filter((space) => {
    if (spacesState.filters.status && space.approvalStatus !== spacesState.filters.status) {
      return false;
    }

    if (cityFilter && !normalizeText(space.city).includes(cityFilter)) {
      return false;
    }

    if (!text) {
      return true;
    }

    return normalizeText(
      [space.name, space.city, space.province, space.spaceType, buildUserName(space.manager)]
        .filter(Boolean)
        .join(" ")
    ).includes(text);
  });
});

const filteredReservations = computed(() => {
  const text = normalizeText(reservationsState.filters.text);

  return reservationsState.items.filter((reservation) => {
    if (
      reservationsState.filters.status &&
      reservation.state !== reservationsState.filters.status
    ) {
      return false;
    }

    if (
      reservationsState.filters.date &&
      reservation.sessionDate !== reservationsState.filters.date
    ) {
      return false;
    }

    if (!text) {
      return true;
    }

    return normalizeText(
      [
        reservation.musicalSpace?.name,
        reservation.musicalSpace?.city,
        buildUserName(reservation.user),
        reservation.band?.name,
        reservation.observations
      ]
        .filter(Boolean)
        .join(" ")
    ).includes(text);
  });
});

const filteredReviews = computed(() => {
  const text = normalizeText(reviewsState.filters.text);

  return reviewsState.items.filter((review) => {
    if (
      reviewsState.filters.rating &&
      String(review.overallRating) !== reviewsState.filters.rating
    ) {
      return false;
    }

    if (!text) {
      return true;
    }

    return normalizeText(
      [
        review.musicalSpace?.name,
        buildUserName(review.user),
        buildUserName(review.reviewer),
        buildUserName(review.reviewedUser),
        review.comment
      ]
        .filter(Boolean)
        .join(" ")
    ).includes(text);
  });
});

const filteredEvents = computed(() => {
  const text = normalizeText(eventsState.filters.text);
  const cityFilter = normalizeText(eventsState.filters.city);

  return eventsState.items.filter((event) => {
    if (eventsState.filters.status && event.status !== eventsState.filters.status) {
      return false;
    }

    if (eventsState.filters.source && event.source !== eventsState.filters.source) {
      return false;
    }

    if (cityFilter && !normalizeText(event.city).includes(cityFilter)) {
      return false;
    }

    if (!text) {
      return true;
    }

    return normalizeText(
      [event.title, event.city, event.venueName, event.musicalGenre, event.eventType]
        .filter(Boolean)
        .join(" ")
    ).includes(text);
  });
});

const filteredRecruitments = computed(() => {
  const text = normalizeText(recruitmentsState.filters.text);

  return recruitmentsState.items.filter((recruitment) => {
    if (
      recruitmentsState.filters.status &&
      recruitment.status !== recruitmentsState.filters.status
    ) {
      return false;
    }

    if (
      recruitmentsState.filters.instrument &&
      recruitment.instrument?.name !== recruitmentsState.filters.instrument
    ) {
      return false;
    }

    if (!text) {
      return true;
    }

    return normalizeText(
      [
        recruitment.title,
        recruitment.band?.name,
        recruitment.instrument?.name,
        recruitment.city,
        recruitment.roleWanted
      ]
        .filter(Boolean)
        .join(" ")
    ).includes(text);
  });
});

const reservationBreakdownOrder = RESERVATION_BREAKDOWN_ORDER;
const spaceApprovalOptions = SPACE_APPROVAL_OPTIONS;

onMounted(async () => {
  await loadInitialData();
});

watch(
  () => route.query.section,
  async (section) => {
    const normalized = normalizeSection(section);

    if (normalized === activeSection.value) {
      return;
    }

    activeSection.value = normalized;
    await ensureSectionLoaded(normalized);
  }
);

async function loadInitialData() {
  initialLoading.value = true;

  const tasks = [loadOverview(true), loadUsers(true), loadSpaces(true), loadReservations(true)];

  if (["reviews", "events", "recruitments"].includes(activeSection.value)) {
    tasks.push(ensureSectionLoaded(activeSection.value, true));
  }

  await Promise.allSettled(tasks);
  initialLoading.value = false;
}

async function handleSectionChange(section) {
  const normalized = normalizeSection(section);

  if (normalized !== activeSection.value) {
    activeSection.value = normalized;
  }

  await ensureSectionLoaded(normalized);
  await syncSectionQuery();
}

async function syncSectionQuery() {
  const query = { ...route.query };

  if (activeSection.value === "overview") {
    delete query.section;
  } else {
    query.section = activeSection.value;
  }

  await router.replace({ query });
}

async function ensureSectionLoaded(section, force = false) {
  if (section === "users") return loadUsers(force);
  if (section === "spaces") return loadSpaces(force);
  if (section === "reservations") return loadReservations(force);
  if (section === "reviews") return loadReviews(force);
  if (section === "events") return loadEvents(force);
  if (section === "recruitments") return loadRecruitments(force);
  return loadOverview(force);
}

async function loadOverview(force = false) {
  if (overviewState.loading || (overviewState.loaded && !force)) {
    return;
  }

  overviewState.loading = true;
  overviewState.error = "";

  try {
    overviewState.data = await AdminRepository.getOverview();
  } catch (error) {
    overviewState.error = getErrorMessage(error, t("admin.states.overviewError"));
  } finally {
    overviewState.loading = false;
    overviewState.loaded = true;
  }
}

async function loadUsers(force = false) {
  if (usersState.loading || (usersState.loaded && !force)) {
    return;
  }

  usersState.loading = true;
  usersState.error = "";

  try {
    const items = await AdminRepository.getUsers();
    usersState.items = await enrichUsers(items);
  } catch (error) {
    usersState.items = [];
    usersState.error = getErrorMessage(error, t("admin.states.usersError"));
  } finally {
    usersState.loading = false;
    usersState.loaded = true;
  }
}

async function loadSpaces(force = false) {
  if (spacesState.loading || (spacesState.loaded && !force)) {
    return;
  }

  spacesState.loading = true;
  spacesState.error = "";

  try {
    const items = await AdminRepository.getMusicalSpaces();
    spacesState.items = await enrichSpaces(items);
  } catch (error) {
    spacesState.items = [];
    spacesState.error = getErrorMessage(error, t("admin.states.spacesError"));
  } finally {
    spacesState.loading = false;
    spacesState.loaded = true;
  }
}

async function loadReservations(force = false) {
  if (reservationsState.loading || (reservationsState.loaded && !force)) {
    return;
  }

  reservationsState.loading = true;
  reservationsState.error = "";

  try {
    reservationsState.items = await AdminRepository.getReservations();
  } catch (error) {
    reservationsState.items = [];
    reservationsState.error = getErrorMessage(error, t("admin.states.reservationsError"));
  } finally {
    reservationsState.loading = false;
    reservationsState.loaded = true;
  }
}

async function loadReviews(force = false) {
  if (reviewsState.loading || (reviewsState.loaded && !force)) {
    return;
  }

  reviewsState.loading = true;
  reviewsState.error = "";

  try {
    const [spaceReviews, userReviews] = await Promise.all([
      AdminRepository.getSpaceReviews(),
      AdminRepository.getUserReviews()
    ]);

    reviewsState.items = [
      ...(spaceReviews ?? []).map((review) => ({ ...review, reviewType: "SPACE" })),
      ...(userReviews ?? []).map((review) => ({ ...review, reviewType: "USER" }))
    ];
  } catch (error) {
    reviewsState.items = [];
    reviewsState.error = getErrorMessage(error, t("admin.states.reviewsError"));
  } finally {
    reviewsState.loading = false;
    reviewsState.loaded = true;
  }
}

async function loadEvents(force = false) {
  if (eventsState.loading || (eventsState.loaded && !force)) {
    return;
  }

  eventsState.loading = true;
  eventsState.error = "";

  try {
    eventsState.items = await AdminRepository.getEvents();
  } catch (error) {
    eventsState.items = [];
    eventsState.error = getErrorMessage(error, t("admin.states.eventsError"));
  } finally {
    eventsState.loading = false;
    eventsState.loaded = true;
  }
}

async function loadRecruitments(force = false) {
  if (recruitmentsState.loading || (recruitmentsState.loaded && !force)) {
    return;
  }

  recruitmentsState.loading = true;
  recruitmentsState.error = "";

  try {
    recruitmentsState.items = await AdminRepository.getBandRecruitments();
  } catch (error) {
    recruitmentsState.items = [];
    recruitmentsState.error = getErrorMessage(error, t("admin.states.recruitmentsError"));
  } finally {
    recruitmentsState.loading = false;
    recruitmentsState.loaded = true;
  }
}

async function enrichUsers(items) {
  if (!Array.isArray(items) || items.length === 0) {
    return [];
  }

  const details = await Promise.allSettled(
    items.map((item) => AdminRepository.getUserById(item.id))
  );

  return items.map((item, index) => {
    const detail = details[index]?.status === "fulfilled" ? details[index].value : null;

    return {
      ...item,
      profileImage: detail?.profileImage ?? item.profileImage ?? "",
      phone: detail?.phone ?? "",
      birthDate: detail?.birthDate ?? null,
      instruments: detail?.instruments ?? []
    };
  });
}

async function enrichSpaces(items) {
  if (!Array.isArray(items) || items.length === 0) {
    return [];
  }

  const details = await Promise.allSettled(
    items.map((item) => AdminRepository.getMusicalSpaceById(item.id))
  );

  return items.map((item, index) => {
    const detail = details[index]?.status === "fulfilled" ? details[index].value : null;

    return {
      ...item,
      city: detail?.location?.city ?? item.city,
      province: detail?.location?.province ?? item.province,
      manager: detail?.manager ?? null
    };
  });
}

function resetUsersFilters() {
  usersState.filters.text = "";
  usersState.filters.role = "";
  usersState.filters.active = "";
}

function resetSpacesFilters() {
  spacesState.filters.text = "";
  spacesState.filters.status = "";
  spacesState.filters.city = "";
}

function resetReservationsFilters() {
  reservationsState.filters.text = "";
  reservationsState.filters.status = "";
  reservationsState.filters.date = "";
}

function resetReviewsFilters() {
  reviewsState.filters.text = "";
  reviewsState.filters.rating = "";
}

function resetEventsFilters() {
  eventsState.filters.text = "";
  eventsState.filters.status = "";
  eventsState.filters.source = "";
  eventsState.filters.city = "";
}

function resetRecruitmentsFilters() {
  recruitmentsState.filters.text = "";
  recruitmentsState.filters.status = "";
  recruitmentsState.filters.instrument = "";
}

function isCurrentUser(user) {
  return user?.id != null && currentUserId.value != null && String(user.id) === String(currentUserId.value);
}

function canPromoteUser(user) {
  return !isCurrentUser(user) && user?.platformRole !== "ADMIN";
}

function canRevokeAdminRole(user) {
  return !isCurrentUser(user) && user?.platformRole === "ADMIN";
}

function toggleUserActive(user) {
  if (isCurrentUser(user)) {
    setNotice("warning", t("admin.users.selfActionBlocked"));
    return;
  }

  const nextActive = !user.active;

  openConfirmDialog({
    title: t(
      nextActive ? "admin.users.confirmActivateTitle" : "admin.users.confirmDeactivateTitle"
    ),
    message: t(
      nextActive ? "admin.users.confirmActivateText" : "admin.users.confirmDeactivateText",
      {
        name: buildUserName(user) || user.email
      }
    ),
    confirmLabel: t(nextActive ? "admin.users.actions.activate" : "admin.users.actions.deactivate"),
    variant: nextActive ? "success" : "danger",
    action: async () => {
      await runRowAction(usersState, user.id, async () => {
        await AdminRepository.setUserActive(user.id, nextActive);
        await loadUsers(true);
      });
      setNotice(
        "success",
        t(nextActive ? "admin.users.activatedSuccess" : "admin.users.deactivatedSuccess")
      );
    }
  });
}

function promoteUserToAdmin(user) {
  if (!canPromoteUser(user)) {
    return;
  }

  openConfirmDialog({
    title: t("admin.users.confirmPromoteTitle"),
    message: t("admin.users.confirmPromoteText", {
      name: buildUserName(user) || user.email
    }),
    confirmLabel: t("admin.users.actions.promoteToAdmin"),
    variant: "success",
    action: async () => {
      await runRowAction(usersState, user.id, async () => {
        await AdminRepository.promoteUserToAdmin(user.id);
        await loadUsers(true);
      });
      setNotice("success", t("admin.users.promotedSuccess"));
    }
  });
}

function revokeAdminRole(user) {
  if (!canRevokeAdminRole(user)) {
    return;
  }

  openConfirmDialog({
    title: t("admin.users.confirmRevokeAdminTitle"),
    message: t("admin.users.confirmRevokeAdminText", {
      name: buildUserName(user) || user.email
    }),
    confirmLabel: t("admin.users.actions.removeAdminRole"),
    variant: "danger",
    action: async () => {
      await runRowAction(usersState, user.id, async () => {
        await AdminRepository.revokeAdminRole(user.id);
        await loadUsers(true);
      });
      setNotice("success", t("admin.users.adminRoleRevokedSuccess"));
    }
  });
}

function updateSpaceApprovalStatus(space, approvalStatus) {
  openConfirmDialog({
    title: t("admin.spaces.confirmTitle"),
    message: t("admin.spaces.confirmText", {
      name: space.name,
      status: t(`admin.approvalStatuses.${approvalStatus}`)
    }),
    confirmLabel: t("admin.spaces.actions.applyStatus"),
    variant:
      approvalStatus === "REJECTED"
        ? "danger"
        : approvalStatus === "APPROVED"
          ? "success"
          : "warning",
    action: async () => {
      await runRowAction(spacesState, space.id, async () => {
        await AdminRepository.updateMusicalSpaceApprovalStatus(space.id, approvalStatus);
        await loadSpaces(true);
      });
      setNotice("success", t("admin.spaces.updatedSuccess"));
    }
  });
}

function openReservationCancelDialog(reservation) {
  openConfirmDialog({
    title: t("admin.reservations.cancelTitle"),
    message: t("admin.reservations.cancelText", {
      name: reservation.musicalSpace?.name || t("admin.fallbacks.space")
    }),
    confirmLabel: t("admin.reservations.actions.cancel"),
    variant: "danger",
    requireText: true,
    inputLabel: t("admin.reservations.cancelReasonLabel"),
    inputPlaceholder: t("admin.reservations.cancelReasonPlaceholder"),
    action: async (value) => {
      const reason = value.trim();

      if (!reason) {
        throw new Error(t("admin.reservations.cancelReasonRequired"));
      }

      await runRowAction(reservationsState, reservation.id, async () => {
        await AdminRepository.cancelReservation(reservation.id, reason);
        await loadReservations(true);
      });
      setNotice("success", t("admin.reservations.cancelSuccess"));
    }
  });
}

function deleteReview(review) {
  openConfirmDialog({
    title: t("admin.reviews.confirmDeleteTitle"),
    message: t("admin.reviews.confirmDeleteText", {
      target: getReviewTargetLabel(review)
    }),
    confirmLabel: t("admin.reviews.actions.delete"),
    variant: "danger",
    action: async () => {
      await runRowAction(reviewsState, `${review.reviewType}-${review.id}`, async () => {
        if (review.reviewType === "USER") {
          await AdminRepository.deleteUserReview(review.id);
        } else {
          await AdminRepository.deleteSpaceReview(review.id);
        }
        await Promise.all([loadReviews(true), loadOverview(true)]);
      });
      setNotice("success", t("admin.reviews.deletedSuccess"));
    }
  });
}

function deleteRecruitment(recruitment) {
  openConfirmDialog({
    title: t("admin.recruitments.confirmDeleteTitle"),
    message: t("admin.recruitments.confirmDeleteText", {
      title: recruitment.title || t("admin.fallbacks.recruitment")
    }),
    confirmLabel: t("admin.recruitments.actions.delete"),
    variant: "danger",
    action: async () => {
      await runRowAction(recruitmentsState, recruitment.id, async () => {
        await AdminRepository.deleteBandRecruitment(recruitment.id);
        await Promise.all([loadRecruitments(true), loadOverview(true)]);
      });
      setNotice("success", t("admin.recruitments.deletedSuccess"));
    }
  });
}

function archiveEvent(event) {
  openConfirmDialog({
    title: t("admin.events.confirmArchiveTitle"),
    message: t("admin.events.confirmArchiveText", {
      title: event.title || t("admin.fallbacks.event")
    }),
    confirmLabel: t("admin.events.actions.archive"),
    variant: "danger",
    action: async () => {
      await runRowAction(eventsState, event.id, async () => {
        await EventRepository.delete(event.id);
        await Promise.all([loadEvents(true), loadOverview(true)]);
      });
      setNotice("success", t("admin.events.archivedSuccess"));
    }
  });
}

async function publishEvent(event) {
  await runRowAction(eventsState, event.id, async () => {
    const detail = await AdminRepository.getEventById(event.id);
    const payload = buildAdminEventPayload({
      ...mapEventToForm(detail),
      status: "PUBLISHED"
    });

    await EventRepository.update(event.id, payload);
    await Promise.all([loadEvents(true), loadOverview(true)]);
  });
  setNotice("success", t("admin.events.publishedSuccess"));
}

function openCreateEventModal() {
  editingEventId.value = null;
  eventForm.value = createEmptyEventForm();
  eventModalError.value = "";
  eventModalOpen.value = true;
}

async function openEditEventModal(event) {
  await runRowAction(eventsState, event.id, async () => {
    const detail = await AdminRepository.getEventById(event.id);
    editingEventId.value = event.id;
    eventForm.value = mapEventToForm(detail);
    eventModalError.value = "";
    eventModalOpen.value = true;
  });
}

function closeEventModal() {
  eventModalOpen.value = false;
  eventModalError.value = "";
}

async function submitEventModal() {
  const payload = buildAdminEventPayload(eventForm.value);

  if (
    !payload.title ||
    !payload.eventDate ||
    !payload.venueName ||
    !payload.city ||
    !payload.country
  ) {
    eventModalError.value = t("events.admin.validation");
    return;
  }

  const ticketValidationKey = getEventTicketValidationKey(payload);

  if (ticketValidationKey) {
    eventModalError.value = t(ticketValidationKey);
    return;
  }

  eventModalSubmitting.value = true;
  eventModalError.value = "";

  try {
    if (editingEventId.value) {
      await EventRepository.update(editingEventId.value, payload);
      setNotice("success", t("admin.events.updatedSuccess"));
    } else {
      await EventRepository.create(payload);
      setNotice("success", t("admin.events.createdSuccess"));
    }

    closeEventModal();
    await Promise.all([loadEvents(true), loadOverview(true)]);
  } catch (error) {
    eventModalError.value = getErrorMessage(error, t("admin.events.saveError"));
  } finally {
    eventModalSubmitting.value = false;
  }
}

function openTicketmasterModal() {
  ticketmasterFilters.value = createTicketmasterFiltersFromAdminEvents();
  ticketmasterModalOpen.value = true;
  ticketmasterError.value = "";
  ticketmasterResults.value = [];
  ticketmasterHasSearched.value = false;
}

function closeTicketmasterModal() {
  ticketmasterModalOpen.value = false;
  ticketmasterError.value = "";
}

async function searchTicketmaster(nextFilters = null) {
  if (nextFilters) {
    ticketmasterFilters.value = nextFilters;
  }

  ticketmasterSearching.value = true;
  ticketmasterError.value = "";
  ticketmasterHasSearched.value = true;

  try {
    ticketmasterResults.value = await TicketmasterRepository.getEvents(
      normalizeTicketmasterSearch(ticketmasterFilters.value)
    );
  } catch (error) {
    ticketmasterResults.value = [];
    ticketmasterError.value = resolveTicketmasterErrorMessage(error, t, t("events.ticketmaster.error"));
  } finally {
    ticketmasterSearching.value = false;
  }
}

async function importTicketmasterEvent(item) {
  ticketmasterImportingId.value = item.externalId;
  ticketmasterError.value = "";

  try {
    await TicketmasterRepository.importEvent(item.externalId);
    setNotice("success", t("events.ticketmaster.imported"));
    closeTicketmasterModal();
    await Promise.all([loadEvents(true), loadOverview(true)]);
  } catch (error) {
    ticketmasterError.value = resolveTicketmasterErrorMessage(error, t, t("events.ticketmaster.importError"));
  } finally {
    ticketmasterImportingId.value = "";
  }
}

async function importTicketmasterResults() {
  ticketmasterBulkImporting.value = true;
  ticketmasterError.value = "";

  try {
    const result = await TicketmasterRepository.importEvents(
      normalizeTicketmasterSearch(ticketmasterFilters.value)
    );
    setNotice(
      "success",
      t("events.ticketmaster.bulkImported", {
        count: result.importedCount,
        existing: result.alreadyImportedCount
      })
    );
    closeTicketmasterModal();
    await Promise.all([loadEvents(true), loadOverview(true)]);
  } catch (error) {
    ticketmasterError.value = resolveTicketmasterErrorMessage(error, t, t("events.ticketmaster.importError"));
  } finally {
    ticketmasterBulkImporting.value = false;
  }
}

function createTicketmasterFiltersFromAdminEvents() {
  return {
    city: eventsState.filters.city || "",
    keyword: eventsState.filters.text || "",
    musicalGenre: "",
    startDate: TICKETMASTER_MIN_START_DATE,
    endDate: "",
    countryCode: "ES"
  };
}

function openConfirmDialog(options) {
  confirmAction.value = options.action;
  confirmState.open = true;
  confirmState.title = options.title;
  confirmState.message = options.message;
  confirmState.confirmLabel = options.confirmLabel;
  confirmState.cancelLabel = t("admin.actions.close");
  confirmState.variant = options.variant || "danger";
  confirmState.requireText = Boolean(options.requireText);
  confirmState.inputLabel = options.inputLabel || "";
  confirmState.inputPlaceholder = options.inputPlaceholder || "";
  confirmState.modelValue = options.modelValue || "";
  confirmState.errorMessage = "";
}

function closeConfirmDialog() {
  if (confirmState.loading) {
    return;
  }

  confirmState.open = false;
  confirmState.title = "";
  confirmState.message = "";
  confirmState.confirmLabel = "";
  confirmState.variant = "danger";
  confirmState.requireText = false;
  confirmState.inputLabel = "";
  confirmState.inputPlaceholder = "";
  confirmState.modelValue = "";
  confirmState.errorMessage = "";
  confirmAction.value = null;
}

async function submitConfirmDialog() {
  if (!confirmAction.value) {
    return;
  }

  confirmState.loading = true;
  confirmState.errorMessage = "";

  try {
    await confirmAction.value(confirmState.modelValue || "");
    closeConfirmDialog();
  } catch (error) {
    confirmState.errorMessage =
      error?.message || getErrorMessage(error, t("admin.states.actionError"));
  } finally {
    confirmState.loading = false;
  }
}

async function runRowAction(state, id, action) {
  state.actionId = id;

  try {
    await action();
  } finally {
    state.actionId = null;
  }
}

function canCancelReservation(reservation) {
  return CANCELLABLE_RESERVATION_STATES.includes(reservation.state);
}

function buildVisibleSummary(visible, total) {
  return t("admin.summary.visibleOfTotal", { visible, total });
}

function buildUserName(user) {
  return [user?.name, user?.firstSurname, user?.secondSurname].filter(Boolean).join(" ");
}

function getReviewAuthor(review) {
  return review.reviewType === "USER" ? review.reviewer : review.user;
}

function getReviewTargetLabel(review) {
  if (review.reviewType === "USER") {
    return buildUserName(review.reviewedUser) || t("admin.fallbacks.user");
  }

  return review.musicalSpace?.name || t("admin.fallbacks.space");
}

function formatDate(value) {
  if (!value) {
    return "--";
  }

  const date = new Date(`${value}T00:00:00`);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat(currentLocale.value, {
    day: "numeric",
    month: "short",
    year: "numeric"
  }).format(date);
}

function formatDateTime(value) {
  if (!value) {
    return "--";
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat(currentLocale.value, {
    day: "numeric",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  }).format(date);
}

function formatTimeRange(start, end) {
  const values = [formatTime(start), formatTime(end)].filter(Boolean);
  return values.length ? values.join(" - ") : "--";
}

function formatTime(value) {
  if (!value) {
    return "";
  }

  return String(value).slice(0, 5);
}

function formatMoney(value) {
  return formatEventMoney(value, currentLocale.value, "--", t("common.labels.free"));
}

function formatSpaceCity(space) {
  return [space?.city, space?.province].filter(Boolean).join(", ") || "--";
}

function getSpaceApprovalTone(status) {
  if (status === "APPROVED") return "success";
  if (status === "REJECTED") return "danger";
  return "warning";
}

function getReservationTone(status) {
  if (status === "ACCEPTED" || status === "COMPLETED") return "success";
  if (status === "PENDING") return "warning";
  if (status === "REJECTED" || status === "CANCELLED") return "danger";
  return "neutral";
}

function getEventTone(status) {
  if (status === "PUBLISHED") return "success";
  if (status === "DRAFT") return "warning";
  if (status === "CANCELLED") return "danger";
  return "neutral";
}

function getRatingTone(rating) {
  if (rating >= 4) return "success";
  if (rating >= 3) return "warning";
  return "danger";
}

function setNotice(type, message) {
  pageNotice.value = { type, message };
}

function getErrorMessage(error, fallback) {
  return getApiErrorMessage(error, t, fallback);
}

function normalizeText(value) {
  return typeof value === "string" ? value.trim().toLowerCase() : "";
}

function resolveCount(primary, fallback, hasFallback) {
  if (typeof primary === "number") {
    return primary;
  }

  return hasFallback ? fallback : "--";
}

function countBy(items, key) {
  return (items || []).reduce((accumulator, item) => {
    const value = item?.[key];

    if (value) {
      accumulator[value] = (accumulator[value] || 0) + 1;
    }

    return accumulator;
  }, {});
}

function normalizeSection(section) {
  return SECTION_KEYS.includes(section) ? section : "overview";
}

function createListState(filters) {
  return reactive({
    items: [],
    loading: false,
    loaded: false,
    error: "",
    actionId: null,
    filters: { ...filters }
  });
}
</script>

<style scoped>
.admin-page {
  min-height: calc(100vh - 72px);
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.14), transparent 22%),
    radial-gradient(circle at top right, rgba(13, 202, 240, 0.08), transparent 18%),
    linear-gradient(180deg, #040404 0%, #0b0b0b 18%, #050505 100%);
  color: #ffffff;
}

.admin-header,
.admin-section__header,
.admin-header__main,
.admin-summary-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.admin-header {
  padding: 1.35rem;
  border: 1px solid rgba(29, 185, 84, 0.2);
  border-radius: 30px;
  background:
    radial-gradient(circle at 8% 20%, rgba(29, 185, 84, 0.18), transparent 32%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.075), rgba(255, 255, 255, 0.025));
  box-shadow: 0 28px 80px rgba(0, 0, 0, 0.28);
}

.admin-header__main {
  min-width: 0;
  justify-content: flex-start;
}

.admin-header__mark {
  flex: 0 0 auto;
  width: 58px;
  height: 58px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  color: #061208;
  background: linear-gradient(135deg, #1ed760 0%, #16c65a 100%);
  box-shadow:
    0 18px 38px rgba(29, 185, 84, 0.22),
    inset 0 0 0 1px rgba(255, 255, 255, 0.28);
  font-size: 1.45rem;
}

.admin-header__copy {
  max-width: 720px;
}

.admin-header__eyebrow,
.admin-section__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.admin-header h1 {
  margin: 0.25rem 0 0.55rem;
  font-size: clamp(2rem, 4vw, 3.2rem);
  letter-spacing: -0.04em;
}

.admin-header p,
.admin-section__header p {
  margin: 0;
  color: #b6b6b6;
}

.admin-section__header p {
  max-width: 780px;
  color: #c9c9c9;
  font-size: 1rem;
  line-height: 1.55;
}

.admin-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1rem;
  margin-top: 1.5rem;
}

.admin-inline-error,
.admin-notice,
.admin-state-card {
  margin-top: 1rem;
  padding: 1rem 1.1rem;
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.admin-inline-error,
.admin-notice--error,
.admin-state-card--error {
  color: #ffbec7;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}

.admin-notice--success {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
}

.admin-notice--warning {
  color: #fff2c5;
  border-color: rgba(255, 193, 7, 0.18);
  background: rgba(255, 193, 7, 0.08);
}

.admin-shell {
  margin-top: 1.3rem;
}

.admin-panel {
  margin-top: 1rem;
  padding: 1.45rem;
  overflow: hidden;
  border-radius: 30px;
  background:
    radial-gradient(circle at top left, rgba(29, 185, 84, 0.09), transparent 32%),
    linear-gradient(180deg, rgba(18, 18, 18, 0.98) 0%, rgba(12, 12, 12, 0.98) 100%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow:
    0 28px 80px rgba(0, 0, 0, 0.34),
    inset 0 1px 0 rgba(255, 255, 255, 0.04);
}

.admin-panel--403 {
  display: flex;
  justify-content: center;
  margin-top: 1.2rem;
}

.admin-forbidden {
  max-width: 560px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.9rem;
  text-align: center;
}

.admin-forbidden__icon {
  width: 74px;
  height: 74px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 22px;
  background: rgba(220, 53, 69, 0.14);
  color: #ff9ba7;
  font-size: 1.9rem;
}

.admin-forbidden__code {
  color: #1db954;
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.12em;
}

.admin-forbidden h2 {
  margin: 0;
  font-size: 1.7rem;
}

.admin-section__header {
  padding-bottom: 1.1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.07);
}

.admin-section__header > div:first-child {
  min-width: 0;
}

.admin-section__header h2 {
  margin: 0.5rem 0 0.45rem;
  font-size: clamp(1.55rem, 2.4vw, 2rem);
  letter-spacing: -0.03em;
}

.admin-section__meta {
  align-self: flex-start;
  padding: 0.55rem 0.8rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.045);
  color: #e3e3e3;
  font-size: 0.92rem;
  font-weight: 700;
  white-space: nowrap;
}

.admin-section__eyebrow {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 0.25rem 0.55rem;
  border: 1px solid rgba(29, 185, 84, 0.18);
  border-radius: 999px;
  background: rgba(29, 185, 84, 0.08);
}

.admin-toolbar {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 0.8rem;
  margin-top: 1.1rem;
  padding: 0.85rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.055), rgba(255, 255, 255, 0.025)),
    rgba(255, 255, 255, 0.02);
}

.admin-toolbar :deep(.form-control) {
  min-height: 50px;
  border-radius: 16px;
  border-color: rgba(255, 255, 255, 0.1);
  background-color: rgba(7, 8, 8, 0.72);
  color: #ffffff;
  font-weight: 650;
  box-shadow: none;
}

.admin-toolbar :deep(.form-control::placeholder) {
  color: rgba(255, 255, 255, 0.42);
}

.admin-toolbar :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.75);
  background-color: rgba(12, 14, 13, 0.92);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.14);
}

.admin-toolbar .btn {
  min-height: 50px;
  border-radius: 16px;
  font-weight: 800;
}

.admin-state-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.85rem;
  min-height: 180px;
  justify-content: center;
  text-align: center;
}

.admin-summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.admin-summary-card {
  padding: 1.2rem;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.09);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.055), rgba(255, 255, 255, 0.025)),
    rgba(255, 255, 255, 0.02);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.04);
  transition:
    border-color 0.18s ease,
    transform 0.18s ease,
    background-color 0.18s ease;
}

.admin-summary-card:hover {
  transform: translateY(-2px);
  border-color: rgba(29, 185, 84, 0.2);
  background-color: rgba(255, 255, 255, 0.035);
}

.admin-summary-card h3 {
  margin: 0;
  font-size: 1.02rem;
}

.admin-summary-card__message {
  margin: 1rem 0 0;
  color: #c5c5c5;
}

.admin-summary-list {
  list-style: none;
  margin: 1rem 0 0;
  padding: 0;
}

.admin-summary-list li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.7rem 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.admin-summary-list li:last-child {
  padding-bottom: 0;
  border-bottom: 0;
}

.admin-table-wrapper {
  margin-top: 1.1rem;
  overflow: auto;
  border: 1px solid rgba(255, 255, 255, 0.075);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.025);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.035);
}

.admin-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  color: #f5f5f5;
  min-width: 920px;
}

.admin-table th:last-child,
.admin-table td:last-child {
  min-width: 230px;
  text-align: center;
}

.admin-table thead th {
  position: sticky;
  top: 0;
  z-index: 1;
  padding: 1rem 1.05rem;
  color: #b6b6b6;
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  background: rgba(13, 14, 14, 0.98);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.admin-table tbody tr {
  transition:
    background-color 0.18s ease,
    box-shadow 0.18s ease;
}

.admin-table tbody tr:hover {
  background: rgba(255, 255, 255, 0.035);
}

.admin-table tbody td {
  padding: 1.05rem;
  vertical-align: middle;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.admin-table tbody strong {
  color: #ffffff;
  font-weight: 800;
}

.admin-table tbody tr:last-child td {
  border-bottom: 0;
}

.admin-user-cell {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  min-width: 0;
}

.admin-user-cell__avatar {
  flex: 0 0 auto;
  overflow: hidden;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.admin-user-cell__avatar :deep(.app-image),
.admin-user-cell__avatar :deep(.app-image__img) {
  width: 100%;
  height: 100%;
  min-height: 0;
}

.admin-user-cell__avatar :deep(.app-image) {
  background: linear-gradient(145deg, #14351f 0%, #050505 100%);
}

.admin-row-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.45rem;
}

.admin-table .admin-row-actions {
  width: min(100%, 245px);
  margin-inline: auto;
  justify-content: center;
  gap: 0.55rem;
}

.admin-row-actions .btn,
.admin-table .btn,
.admin-summary-card .btn,
.admin-state-card .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  font-weight: 800;
  padding-inline: 0.8rem;
  text-align: center;
  white-space: nowrap;
}

.admin-table .admin-row-actions .btn {
  min-width: 106px;
  min-height: 38px;
}

.admin-row-actions .btn-outline-danger,
.admin-table .btn-outline-danger {
  background: rgba(220, 53, 69, 0.08);
}

.admin-row-actions .btn-outline-success,
.admin-table .btn-outline-success {
  background: rgba(29, 185, 84, 0.08);
}

.admin-row-actions .btn-outline-warning,
.admin-table .btn-outline-warning {
  background: rgba(255, 193, 7, 0.08);
}

.admin-cell__subtext {
  margin-top: 0.28rem;
  color: #a8a8a8;
  font-size: 0.82rem;
  line-height: 1.45;
}

@media (max-width: 1199.98px) {
  .admin-metrics,
  .admin-summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .admin-toolbar {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 991.98px) {
  .admin-header,
  .admin-section__header {
    flex-direction: column;
    align-items: stretch;
  }
}

@media (max-width: 767.98px) {
  .admin-metrics,
  .admin-summary-grid,
  .admin-toolbar {
    grid-template-columns: 1fr;
  }

  .admin-table thead {
    display: none;
  }

  .admin-table,
  .admin-table tbody,
  .admin-table tr,
  .admin-table td {
    display: block;
    width: 100%;
    min-width: 0;
  }

  .admin-table tr {
    padding: 0.95rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  }

  .admin-table tbody tr:last-child {
    border-bottom: 0;
  }

  .admin-table tbody td {
    display: flex;
    flex-direction: column;
    align-items: stretch;
    gap: 0.35rem;
    padding: 0.55rem 0;
    border-bottom: 0;
    text-align: left;
  }

  .admin-table th:last-child,
  .admin-table td:last-child {
    min-width: 0;
    text-align: left;
  }

  .admin-table tbody td::before {
    content: attr(data-label);
    color: #8f8f8f;
    font-size: 0.72rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
  }

  .admin-row-actions {
    justify-content: center;
  }

  .admin-table .admin-row-actions {
    width: 100%;
    max-width: 420px;
    margin-inline: auto;
  }

  .admin-row-actions .btn {
    width: 100%;
  }
}

</style>
