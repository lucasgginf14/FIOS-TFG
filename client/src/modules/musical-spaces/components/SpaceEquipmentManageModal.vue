<template>
  <div v-if="open" class="equipment-modal-backdrop" @click.self="handleClose">
    <section class="equipment-modal" role="dialog" aria-modal="true">
      <header class="equipment-modal__header">
        <div>
          <span class="equipment-modal__eyebrow">{{ t("spaceEquipmentManage.header.eyebrow") }}</span>
          <h2>{{ t("spaceEquipmentManage.header.title") }}</h2>
          <p v-if="spaceName">{{ spaceName }}</p>
        </div>
        <button
          type="button"
          class="equipment-modal__close"
          :aria-label="t('common.actions.close')"
          :disabled="busy"
          @click="handleClose"
        >
          <i class="bi bi-x-lg" aria-hidden="true"></i>
        </button>
      </header>

      <div v-if="notice" class="equipment-modal__notice" :class="notice.type">
        {{ notice.message }}
      </div>

      <div v-if="errorMessage" class="equipment-modal__notice error">
        {{ errorMessage }}
      </div>

      <div v-if="loading" class="equipment-modal__state">
        <div class="spinner-border text-success" role="status"></div>
        <span>{{ t("spaceEquipmentManage.states.loading") }}</span>
      </div>

      <template v-else>
        <form class="equipment-modal__form" @submit.prevent="submitForm">
          <div class="equipment-modal__form-header">
            <div>
              <span>{{ t("spaceEquipmentManage.form.eyebrow") }}</span>
              <h3>{{ formTitle }}</h3>
            </div>
            <button
              v-if="editingId"
              type="button"
              class="btn btn-outline-light btn-sm"
              :disabled="busy"
              @click="startCreate"
            >
              {{ t("spaceEquipmentManage.actions.cancelEdit") }}
            </button>
          </div>

          <div class="row g-3">
            <div class="col-12 col-lg-5">
              <label class="form-label">{{ t("spaceEquipmentManage.form.equipment") }}</label>
              <AppSelect
                :model-value="form.equipmentId"
                :options="equipmentOptions"
                :label="t('spaceEquipmentManage.form.equipment')"
                :disabled="busy"
                @update:model-value="updateForm('equipmentId', $event)"
              />
            </div>

            <div v-if="isCustomEquipment" class="col-12 col-lg-7">
              <label class="form-label" for="space-equipment-custom-name">
                {{ t("spaceEquipmentManage.form.customEquipment") }}
              </label>
              <input
                id="space-equipment-custom-name"
                :value="form.customEquipmentName"
                type="text"
                maxlength="120"
                class="form-control"
                :placeholder="t('spaceEquipmentManage.form.customEquipmentPlaceholder')"
                :disabled="busy"
                @input="updateForm('customEquipmentName', $event.target.value)"
              />
            </div>

            <div class="col-12 col-sm-6 col-lg-3">
              <label class="form-label" for="space-equipment-quantity">
                {{ t("spaceEquipmentManage.form.quantity") }}
              </label>
              <input
                id="space-equipment-quantity"
                :value="form.quantity"
                type="number"
                min="1"
                step="1"
                class="form-control"
                :disabled="busy"
                @input="updateForm('quantity', $event.target.value)"
              />
            </div>

            <div class="col-12 col-sm-6 col-lg-4">
              <label class="form-label">{{ t("spaceEquipmentManage.form.state") }}</label>
              <AppSelect
                :model-value="form.state"
                :options="stateOptions"
                :label="t('spaceEquipmentManage.form.state')"
                :disabled="busy"
                @update:model-value="updateForm('state', $event)"
              />
            </div>

            <div class="col-12">
              <label class="form-label equipment-modal__required-label" for="space-equipment-observations">
                <span>{{ t("spaceEquipmentManage.form.observations") }}</span>
                <small v-if="isCustomEquipment">{{ t("spaceEquipmentManage.form.requiredForCustom") }}</small>
              </label>
              <textarea
                id="space-equipment-observations"
                :value="form.observations"
                class="form-control"
                rows="3"
                maxlength="1000"
                :placeholder="observationsPlaceholder"
                :disabled="busy"
                @input="updateForm('observations', $event.target.value)"
              ></textarea>
            </div>
          </div>

          <footer class="equipment-modal__form-footer">
            <button
              type="submit"
              class="btn btn-success"
              :disabled="!canSubmit"
            >
              <span v-if="saving" class="spinner-border spinner-border-sm me-2" role="status"></span>
              {{ submitLabel }}
            </button>
          </footer>
        </form>

        <section class="equipment-modal__list">
          <div class="equipment-modal__list-header">
            <div>
              <span>{{ t("spaceEquipmentManage.list.eyebrow") }}</span>
              <h3>{{ t("spaceEquipmentManage.list.title") }}</h3>
            </div>
            <button
              type="button"
              class="btn btn-outline-light btn-sm"
              :disabled="busy"
              @click="reloadEquipment(true)"
            >
              <i class="bi bi-arrow-clockwise" aria-hidden="true"></i>
              {{ t("spaceEquipmentManage.actions.reload") }}
            </button>
          </div>

          <div v-if="!localEquipment.length" class="equipment-modal__empty">
            {{ t("spaceEquipmentManage.states.empty") }}
          </div>

          <div v-else class="equipment-modal__items">
            <article
              v-for="item in localEquipment"
              :key="item.id"
              class="equipment-modal__item"
              :class="{ 'is-editing': Number(item.id) === Number(editingId) }"
            >
              <div class="equipment-modal__item-main">
                <span class="equipment-modal__item-icon">
                  <i :class="equipmentIcon(item.equipment?.category)" aria-hidden="true"></i>
                </span>
                <div>
                  <strong>{{ item.equipment?.name || t("spaceDetail.equipment.unknown") }}</strong>
                  <span>{{ categoryLabel(item.equipment?.category) }}</span>
                </div>
              </div>

              <div class="equipment-modal__item-meta">
                <span>{{ t("spaceDetail.equipment.quantity", { value: item.quantity }) }}</span>
                <span>{{ t(`spaceDetail.equipment.states.${item.state || 'AVAILABLE'}`) }}</span>
              </div>

              <p v-if="item.observations">{{ item.observations }}</p>

              <div class="equipment-modal__item-actions">
                <button
                  type="button"
                  class="btn btn-outline-light btn-sm"
                  :disabled="busy"
                  @click="startEdit(item)"
                >
                  {{ t("spaceEquipmentManage.actions.edit") }}
                </button>
                <button
                  type="button"
                  class="btn btn-outline-danger btn-sm"
                  :disabled="busy"
                  @click="openDeleteConfirm(item)"
                >
                  {{ t("spaceEquipmentManage.actions.delete") }}
                </button>
              </div>
            </article>
          </div>
        </section>
      </template>
    </section>

    <div
      v-if="deleteCandidate"
      class="equipment-confirm-backdrop"
      @click.self="closeDeleteConfirm"
    >
      <section class="equipment-confirm" role="dialog" aria-modal="true">
        <header class="equipment-confirm__header">
          <div>
            <span>{{ t("spaceEquipmentManage.confirm.eyebrow") }}</span>
            <h3>{{ t("spaceEquipmentManage.confirm.deleteTitle") }}</h3>
          </div>
          <button
            type="button"
            class="equipment-modal__close"
            :aria-label="t('common.actions.close')"
            :disabled="Boolean(deletingId)"
            @click="closeDeleteConfirm"
          >
            <i class="bi bi-x-lg" aria-hidden="true"></i>
          </button>
        </header>

        <p>
          {{
            t("spaceEquipmentManage.confirm.deleteText", {
              name: deleteCandidate.equipment?.name || t("spaceDetail.equipment.unknown")
            })
          }}
        </p>

        <footer class="equipment-confirm__footer">
          <button
            type="button"
            class="btn btn-outline-light"
            :disabled="Boolean(deletingId)"
            @click="closeDeleteConfirm"
          >
            {{ t("spaceEquipmentManage.actions.cancel") }}
          </button>
          <button
            type="button"
            class="btn btn-danger"
            :disabled="Boolean(deletingId)"
            @click="confirmDelete"
          >
            <span v-if="deletingId" class="spinner-border spinner-border-sm me-2" role="status"></span>
            {{ t("spaceEquipmentManage.actions.confirmDelete") }}
          </button>
        </footer>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { getApiErrorMessage } from "@/common/apiErrors";
import AppSelect from "@/common/components/AppSelect.vue";
import EquipmentRepository from "@/repositories/EquipmentRepository";
import MusicalSpaceRepository from "@/repositories/MusicalSpaceRepository";

const EQUIPMENT_STATES = ["AVAILABLE", "LIMITED", "OUT_OF_SERVICE"];
const CUSTOM_EQUIPMENT_VALUE = "__OTHER__";

const props = defineProps({
  open: { type: Boolean, default: false },
  spaceId: { type: [Number, String], default: "" },
  spaceName: { type: String, default: "" },
  items: { type: Array, default: () => [] }
});

const emit = defineEmits(["close", "updated"]);
const { t } = useI18n();

const catalog = ref([]);
const localEquipment = ref([]);
const loading = ref(false);
const saving = ref(false);
const deletingId = ref(null);
const editingId = ref(null);
const errorMessage = ref("");
const notice = ref(null);
const deleteCandidate = ref(null);
const form = ref(createEmptyForm());

const busy = computed(() => loading.value || saving.value || Boolean(deletingId.value));
const assignedEquipmentIds = computed(
  () => new Set(localEquipment.value.map((item) => Number(item.equipment?.id)).filter(Boolean))
);
const catalogWithLocalEquipment = computed(() => {
  const byId = new Map(catalog.value.map((item) => [Number(item.id), item]));

  localEquipment.value
    .map((item) => item.equipment)
    .filter((item) => item?.id)
    .forEach((item) => {
      if (!byId.has(Number(item.id))) {
        byId.set(Number(item.id), item);
      }
    });

  return [...byId.values()];
});
const sortedCatalog = computed(() =>
  [...catalogWithLocalEquipment.value].sort((left, right) => `${left.name || ""}`.localeCompare(`${right.name || ""}`))
);
const availableCatalog = computed(() =>
  sortedCatalog.value.filter((item) => !assignedEquipmentIds.value.has(Number(item.id)))
);
const equipmentOptions = computed(() => {
  const options = [
    {
      value: "",
      label: t("spaceEquipmentManage.form.selectEquipment"),
      disabled: true
    }
  ];

  return [
    ...options,
    ...sortedCatalog.value.map((item) => {
      const itemId = Number(item.id);
      const isCurrentEditingEquipment =
        editingId.value &&
        localEquipment.value.some(
          (equipmentItem) =>
            Number(equipmentItem.id) === Number(editingId.value) &&
            Number(equipmentItem.equipment?.id) === itemId
        );
      const isAssigned = assignedEquipmentIds.value.has(itemId);

      return {
        value: String(item.id),
        label: item.name,
        description: categoryLabel(item.category),
        disabled: isAssigned && !isCurrentEditingEquipment
      };
    }),
    {
      value: CUSTOM_EQUIPMENT_VALUE,
      label: t("spaceEquipmentManage.form.customEquipmentOption"),
      description: t("spaceEquipmentManage.categories.OTHER")
    }
  ];
});
const stateOptions = computed(() =>
  EQUIPMENT_STATES.map((state) => ({
    value: state,
    label: t(`spaceDetail.equipment.states.${state}`)
  }))
);
const formTitle = computed(() =>
  editingId.value ? t("spaceEquipmentManage.form.editTitle") : t("spaceEquipmentManage.form.addTitle")
);
const submitLabel = computed(() => {
  if (saving.value) {
    return editingId.value ? t("spaceEquipmentManage.actions.updating") : t("spaceEquipmentManage.actions.adding");
  }

  return editingId.value ? t("spaceEquipmentManage.actions.update") : t("spaceEquipmentManage.actions.add");
});
const isCustomEquipment = computed(() => form.value.equipmentId === CUSTOM_EQUIPMENT_VALUE);
const observationsPlaceholder = computed(() =>
  isCustomEquipment.value
    ? t("spaceEquipmentManage.form.customObservationsPlaceholder")
    : t("spaceEquipmentManage.form.observationsPlaceholder")
);
const canSubmit = computed(() => {
  const quantity = Number(form.value.quantity);
  const hasCatalogEquipment = Number(form.value.equipmentId) > 0;
  const hasCustomEquipment =
    isCustomEquipment.value &&
    form.value.customEquipmentName.trim().length > 0 &&
    form.value.observations.trim().length > 0;

  return Boolean(
    !busy.value &&
    (hasCatalogEquipment || hasCustomEquipment) &&
    Number.isInteger(quantity) &&
    quantity >= 1 &&
    EQUIPMENT_STATES.includes(form.value.state)
  );
});

watch(
  () => props.open,
  (open) => {
    if (open) {
      initialize();
    } else {
      resetModal();
    }
  },
  { immediate: true }
);

watch(
  () => props.items,
  (items) => {
    if (props.open && !loading.value) {
      localEquipment.value = items ?? [];
    }
  },
  { deep: true }
);

async function initialize() {
  if (!props.spaceId) {
    return;
  }

  loading.value = true;
  errorMessage.value = "";
  notice.value = null;
  deleteCandidate.value = null;

  try {
    const [catalogResponse, equipmentResponse] = await Promise.all([
      EquipmentRepository.getAll(),
      MusicalSpaceRepository.getEquipment(props.spaceId)
    ]);

    catalog.value = catalogResponse ?? [];
    localEquipment.value = equipmentResponse ?? [];
    emitUpdated();
    startCreate();
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "spaceEquipmentManage.states.error");
  } finally {
    loading.value = false;
  }
}

async function reloadEquipment(showLoading = false) {
  if (!props.spaceId) {
    return;
  }

  if (showLoading) {
    loading.value = true;
  }

  errorMessage.value = "";

  try {
    localEquipment.value = await MusicalSpaceRepository.getEquipment(props.spaceId);
    emitUpdated();
    return true;
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "spaceEquipmentManage.states.error");
    return false;
  } finally {
    if (showLoading) {
      loading.value = false;
    }
  }
}

async function submitForm() {
  const validationError = validateForm();

  if (validationError) {
    errorMessage.value = validationError;
    notice.value = null;
    return;
  }

  saving.value = true;
  errorMessage.value = "";
  notice.value = null;

  try {
    const payload = buildPayload();

    if (editingId.value) {
      await MusicalSpaceRepository.updateSpaceEquipment(editingId.value, payload);
      notice.value = { type: "success", message: t("spaceEquipmentManage.notices.updated") };
    } else {
      await MusicalSpaceRepository.addSpaceEquipment(props.spaceId, payload);
      notice.value = { type: "success", message: t("spaceEquipmentManage.notices.added") };
    }

    const reloaded = await reloadEquipment(false);

    if (reloaded) {
      startCreate();
    }
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "spaceEquipmentManage.states.saveError");
  } finally {
    saving.value = false;
  }
}

function validateForm() {
  const equipmentId = Number(form.value.equipmentId);
  const quantity = Number(form.value.quantity);
  const customEquipmentName = form.value.customEquipmentName.trim();
  const customObservations = form.value.observations.trim();

  if (isCustomEquipment.value) {
    if (!customEquipmentName) {
      return t("spaceEquipmentManage.validation.customEquipment");
    }

    if (!customObservations) {
      return t("spaceEquipmentManage.validation.customObservations");
    }
  } else if (!equipmentId || !sortedCatalog.value.some((item) => Number(item.id) === equipmentId)) {
    return t("spaceEquipmentManage.validation.equipment");
  }

  if (!Number.isInteger(quantity) || quantity < 1) {
    return t("spaceEquipmentManage.validation.quantity");
  }

  if (!EQUIPMENT_STATES.includes(form.value.state)) {
    return t("spaceEquipmentManage.validation.state");
  }

  const duplicated = localEquipment.value.some(
    (item) =>
      ((isCustomEquipment.value &&
        normalizeEquipmentName(item.equipment?.name) === normalizeEquipmentName(customEquipmentName)) ||
        (!isCustomEquipment.value && Number(item.equipment?.id) === equipmentId)) &&
      (!editingId.value || Number(item.id) !== Number(editingId.value))
  );

  return duplicated ? t("spaceEquipmentManage.validation.duplicate") : "";
}

function buildPayload() {
  const payload = {
    quantity: Number(form.value.quantity),
    state: form.value.state,
    observations: form.value.observations.trim() || null
  };

  if (isCustomEquipment.value) {
    payload.customEquipmentName = form.value.customEquipmentName.trim();
  } else {
    payload.equipmentId = Number(form.value.equipmentId);
  }

  return payload;
}

function startCreate() {
  editingId.value = null;
  form.value = createEmptyForm(availableCatalog.value[0]?.id ?? CUSTOM_EQUIPMENT_VALUE);
  errorMessage.value = "";
}

function startEdit(item) {
  editingId.value = item.id;
  form.value = {
    equipmentId: item.equipment?.id ? String(item.equipment.id) : "",
    customEquipmentName: "",
    quantity: String(item.quantity ?? 1),
    state: item.state || "AVAILABLE",
    observations: item.observations || ""
  };
  errorMessage.value = "";
  notice.value = null;
}

function openDeleteConfirm(item) {
  deleteCandidate.value = item;
  errorMessage.value = "";
}

function closeDeleteConfirm() {
  if (deletingId.value) {
    return;
  }

  deleteCandidate.value = null;
}

async function confirmDelete() {
  if (!deleteCandidate.value) {
    return;
  }

  const item = deleteCandidate.value;
  deletingId.value = item.id;
  errorMessage.value = "";
  notice.value = null;

  try {
    await MusicalSpaceRepository.deleteSpaceEquipment(item.id);
    notice.value = { type: "success", message: t("spaceEquipmentManage.notices.deleted") };
    deleteCandidate.value = null;

    const wasEditingDeletedItem = Number(editingId.value) === Number(item.id);
    const reloaded = await reloadEquipment(false);

    if (wasEditingDeletedItem && reloaded) {
      startCreate();
    }
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error, t, "spaceEquipmentManage.states.deleteError");
  } finally {
    deletingId.value = null;
  }
}

function updateForm(field, value) {
  const nextForm = {
    ...form.value,
    [field]: value
  };

  if (field === "equipmentId" && value !== CUSTOM_EQUIPMENT_VALUE) {
    nextForm.customEquipmentName = "";
  }

  form.value = nextForm;
}

function handleClose() {
  if (busy.value) {
    return;
  }

  emit("close");
}

function resetModal() {
  localEquipment.value = props.items ?? [];
  editingId.value = null;
  errorMessage.value = "";
  notice.value = null;
  deleteCandidate.value = null;
  form.value = createEmptyForm();
}

function createEmptyForm(equipmentId = "") {
  return {
    equipmentId: equipmentId ? String(equipmentId) : "",
    customEquipmentName: "",
    quantity: "1",
    state: "AVAILABLE",
    observations: ""
  };
}

function emitUpdated() {
  emit("updated", localEquipment.value ?? []);
}

function categoryLabel(category) {
  return t(`spaceEquipmentManage.categories.${category || "OTHER"}`);
}

function equipmentIcon(category) {
  const icons = {
    INSTRUMENT: "bi bi-music-note-beamed",
    SOUND: "bi bi-speaker",
    LIGHTING: "bi bi-lightbulb",
    RECORDING: "bi bi-mic",
    FURNITURE: "bi bi-lamp",
    ACCESSORY: "bi bi-plugin",
    OTHER: "bi bi-grid"
  };

  return icons[category] || icons.OTHER;
}

function normalizeEquipmentName(value) {
  return String(value || "")
    .normalize("NFD")
    .replaceAll(/\p{Diacritic}/gu, "")
    .toLowerCase()
    .replaceAll(/\s+/g, " ")
    .trim();
}
</script>

<style scoped>
.equipment-modal-backdrop,
.equipment-confirm-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1060;
  display: grid;
  place-items: center;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.72);
  backdrop-filter: blur(10px);
}

.equipment-confirm-backdrop {
  z-index: 1070;
}

.equipment-modal,
.equipment-confirm {
  width: min(100%, 980px);
  max-height: calc(100vh - 2rem);
  overflow: auto;
  padding: 1.25rem;
  border-radius: 8px;
  background: #141414;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #ffffff;
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.34);
}

.equipment-confirm {
  width: min(100%, 560px);
}

.equipment-modal__header,
.equipment-confirm__header,
.equipment-modal__form-header,
.equipment-modal__list-header,
.equipment-confirm__footer {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

.equipment-modal__header {
  margin-bottom: 1rem;
}

.equipment-modal__eyebrow,
.equipment-modal__form-header span,
.equipment-modal__list-header span,
.equipment-confirm__header span {
  color: #1db954;
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.equipment-modal__header h2,
.equipment-confirm__header h3,
.equipment-modal__form-header h3,
.equipment-modal__list-header h3 {
  margin: 0.3rem 0 0;
  color: #ffffff;
}

.equipment-modal__header h2 {
  font-size: 1.45rem;
}

.equipment-modal__header p,
.equipment-confirm p {
  margin: 0.35rem 0 0;
  color: #cfcfcf;
}

.equipment-modal__close {
  display: inline-grid;
  flex: 0 0 auto;
  place-items: center;
  width: 42px;
  height: 42px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
}

.equipment-modal__notice,
.equipment-modal__state,
.equipment-modal__empty {
  margin-bottom: 1rem;
  padding: 0.85rem 0.95rem;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: #d7d7d7;
}

.equipment-modal__notice.success {
  color: #dfffe9;
  border-color: rgba(29, 185, 84, 0.18);
  background: rgba(29, 185, 84, 0.08);
}

.equipment-modal__notice.error {
  color: #ffb3bd;
  border-color: rgba(220, 53, 69, 0.18);
  background: rgba(220, 53, 69, 0.08);
}

.equipment-modal__state {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0;
}

.equipment-modal__form,
.equipment-modal__list {
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.equipment-modal__list {
  margin-top: 1rem;
}

.equipment-modal__form-header,
.equipment-modal__list-header {
  margin-bottom: 1rem;
}

.equipment-modal__form :deep(.form-control) {
  min-height: 46px;
  border-radius: 8px;
  border-color: rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

.equipment-modal__form :deep(.app-select__trigger),
.equipment-modal__form :deep(.app-select__menu),
.equipment-modal__form :deep(.app-select__option) {
  border-radius: 8px;
}

.equipment-modal__form :deep(textarea.form-control) {
  min-height: 104px;
}

.equipment-modal__form :deep(.form-control:focus) {
  border-color: rgba(29, 185, 84, 0.36);
  box-shadow: 0 0 0 0.2rem rgba(29, 185, 84, 0.12);
}

.equipment-modal__form-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}

.equipment-modal__required-label {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
}

.equipment-modal__required-label small {
  color: #1db954;
  font-size: 0.78rem;
  font-weight: 700;
}

.equipment-modal__items {
  display: grid;
  gap: 0.75rem;
}

.equipment-modal__item {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 0.85rem;
  align-items: start;
  padding: 0.85rem;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.equipment-modal__item.is-editing {
  border-color: rgba(29, 185, 84, 0.36);
  background: rgba(29, 185, 84, 0.08);
}

.equipment-modal__item-main {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  min-width: 0;
}

.equipment-modal__item-icon {
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 8px;
  background: rgba(29, 185, 84, 0.14);
  color: #1db954;
}

.equipment-modal__item-main strong,
.equipment-modal__item-main span,
.equipment-modal__item-meta span {
  display: block;
}

.equipment-modal__item-main strong {
  overflow-wrap: anywhere;
}

.equipment-modal__item-main span,
.equipment-modal__item-meta span,
.equipment-modal__item p {
  color: #bdbdbd;
}

.equipment-modal__item-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.45rem;
  grid-column: 1;
}

.equipment-modal__item-meta span {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 0.65rem;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
}

.equipment-modal__item p {
  grid-column: 1;
  margin: 0;
  line-height: 1.5;
}

.equipment-modal__item-actions {
  display: flex;
  grid-column: 2;
  grid-row: 1 / span 3;
  justify-content: flex-end;
  gap: 0.5rem;
  align-self: center;
}

.equipment-modal__item-actions .btn,
.equipment-modal__form-footer .btn,
.equipment-modal__list-header .btn,
.equipment-confirm__footer .btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  min-height: 38px;
  border-radius: 8px;
  white-space: normal;
}

.equipment-confirm__footer {
  margin-top: 1.2rem;
}

@media (max-width: 767.98px) {
  .equipment-modal__header,
  .equipment-confirm__header,
  .equipment-modal__form-header,
  .equipment-modal__list-header,
  .equipment-confirm__footer {
    flex-direction: column;
    align-items: stretch;
  }

  .equipment-modal__item {
    grid-template-columns: 1fr;
  }

  .equipment-modal__item-meta,
  .equipment-modal__item p,
  .equipment-modal__item-actions {
    grid-column: 1;
    grid-row: auto;
  }

  .equipment-modal__item-actions {
    justify-content: flex-start;
  }
}
</style>
