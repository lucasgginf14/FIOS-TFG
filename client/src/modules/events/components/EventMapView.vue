<template>
  <section class="event-map-card" :class="{ 'is-empty': !items.length }">
    <div ref="mapElement" class="event-map-card__canvas"></div>

    <div v-if="!items.length" class="event-map-card__empty">
      <i class="bi bi-map"></i>
      <strong>{{ emptyTitle }}</strong>
      <p>{{ emptyText }}</p>
    </div>

    <div v-if="items.length" class="event-map-card__list">
      <button
        v-for="item in items"
        :key="item.id"
        type="button"
        class="event-map-card__item"
        :class="{ 'is-active': selectedId === item.id }"
        @click="emit('select', item)"
      >
        <strong>{{ item.title }}</strong>
        <span>{{ item.venueName || item.city }}</span>
      </button>
    </div>
  </section>
</template>

<script setup>
import { nextTick, onMounted, onUnmounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import L from "leaflet";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";
import { formatEventDateTime } from "../eventUtils";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow
});

const props = defineProps({
  items: {
    type: Array,
    default: () => []
  },
  selectedId: {
    type: Number,
    default: null
  },
  emptyTitle: {
    type: String,
    default: ""
  },
  emptyText: {
    type: String,
    default: ""
  }
});

const emit = defineEmits(["select"]);
const { locale, t } = useI18n();
const mapElement = ref(null);

let mapInstance = null;
let markers = [];
const popupSeparator = ` ${String.fromCharCode(183)} `;

onMounted(() => {
  syncMap();
});

onUnmounted(() => {
  if (mapInstance) {
    mapInstance.remove();
    mapInstance = null;
  }

  markers = [];
});

watch(
  () => props.items,
  () => syncMap(),
  { deep: true }
);

async function syncMap() {
  await initializeMap();
  await nextTick();

  if (!mapInstance) {
    return;
  }

  mapInstance.invalidateSize();
  updateMarkers();
}

async function initializeMap() {
  if (mapInstance) {
    return;
  }

  await nextTick();

  if (!mapElement.value || mapInstance) {
    return;
  }

  mapInstance = L.map(mapElement.value, { zoomControl: true }).setView([42.88, -8.54], 7);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "&copy; OpenStreetMap contributors"
  }).addTo(mapInstance);
}

function updateMarkers() {
  if (!mapInstance) {
    return;
  }

  markers.forEach((marker) => mapInstance.removeLayer(marker));
  markers = [];

  if (!props.items.length) {
    return;
  }

  const bounds = L.latLngBounds();

  props.items.forEach((item) => {
    if (typeof item.latitude !== "number" || typeof item.longitude !== "number") {
      return;
    }

    const popup = createEventPopup(item);
    const marker = L.marker([item.latitude, item.longitude]).bindPopup(popup).addTo(mapInstance);
    marker.on("click", () => emit("select", item));

    markers.push(marker);
    bounds.extend([item.latitude, item.longitude]);
  });

  if (markers.length) {
    mapInstance.fitBounds(bounds, { padding: [48, 48] });
  }
}

function createEventPopup(item) {
  const popup = document.createElement("div");
  popup.className = "event-map-popup event-map-popup--clickable";
  popup.setAttribute("role", "link");
  popup.setAttribute("tabindex", "0");
  popup.setAttribute("aria-label", `${t("events.actions.view")}: ${item.title ?? ""}`);
  popup.addEventListener("click", () => emit("select", item));
  popup.addEventListener("keydown", (event) => {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      emit("select", item);
    }
  });

  const title = document.createElement("strong");
  title.textContent = item.title ?? "";
  popup.appendChild(title);

  const date = document.createElement("div");
  date.textContent = formatEventDateTime(item, locale.value);
  popup.appendChild(date);

  const location = document.createElement("div");
  location.textContent = [item.venueName, item.city].filter(Boolean).join(popupSeparator);
  popup.appendChild(location);

  const indicator = document.createElement("span");
  indicator.className = "event-map-popup__indicator";
  const indicatorIcon = document.createElement("i");
  indicatorIcon.className = "bi bi-arrow-up-right";
  indicator.appendChild(indicatorIcon);
  popup.appendChild(indicator);

  return popup;
}
</script>

<style scoped>
.event-map-card {
  position: relative;
  overflow: hidden;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}

.event-map-card__canvas {
  height: 430px;
}

.event-map-card.is-empty .event-map-card__canvas {
  height: 460px;
}

.event-map-card__list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 0.8rem;
  padding: 1rem;
}

.event-map-card__item {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  padding: 0.95rem 1rem;
  text-align: left;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: #ffffff;
}

.event-map-card__item span {
  color: #b6b6b6;
}

.event-map-card__item.is-active {
  border-color: rgba(29, 185, 84, 0.24);
  background: rgba(29, 185, 84, 0.12);
}

.event-map-card__empty {
  position: absolute;
  inset: 0;
  z-index: 1001;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  min-height: 460px;
  padding: 2rem;
  text-align: center;
  color: #cbcbcb;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
}

.event-map-card__empty i {
  color: #1db954;
  font-size: 2rem;
}

:deep(.event-map-popup) {
  display: grid;
  gap: 0.45rem;
  min-width: 190px;
}

:deep(.event-map-popup--clickable) {
  cursor: pointer;
}

:deep(.event-map-popup__indicator) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  margin-top: 0.2rem;
  border-radius: 50%;
  border: 1px solid rgba(29, 185, 84, 0.35);
  background: rgba(29, 185, 84, 0.14);
  color: #111111;
}
</style>
