<template>
  <section class="map-card">
    <div ref="mapElement" class="map-card__canvas"></div>

    <div v-if="!items.length" class="map-card__empty">
      <i class="bi bi-map"></i>
      <strong>{{ emptyTitle }}</strong>
      <p>{{ emptyText }}</p>
    </div>
  </section>
</template>

<script setup>
import { nextTick, onMounted, onUnmounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import L from "leaflet";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

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
  emptyTitle: {
    type: String,
    default: ""
  },
  emptyText: {
    type: String,
    default: ""
  }
});

const router = useRouter();
const { t } = useI18n();
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

    const popup = createSearchPopup(item);
    const marker = L.marker([item.latitude, item.longitude]).bindPopup(popup).addTo(mapInstance);
    markers.push(marker);
    bounds.extend([item.latitude, item.longitude]);
  });

  if (markers.length) {
    mapInstance.fitBounds(bounds, { padding: [48, 48] });
  }
}

function createSearchPopup(item) {
  const popup = document.createElement("div");
  popup.className = "search-map-popup";

  const title = document.createElement("strong");
  title.textContent = item.title ?? "";
  popup.appendChild(title);

  const location = document.createElement("div");
  location.textContent = [item.city, item.location].filter(Boolean).join(popupSeparator);
  popup.appendChild(location);

  const button = document.createElement("button");
  button.type = "button";
  button.textContent = t("search.map.popupAction");
  button.addEventListener("click", () => openItem(item));
  popup.appendChild(button);

  return popup;
}

function openItem(item) {
  if (item.type === "MUSICAL_SPACE") {
    router.push({ name: "MusicalSpaceDetail", params: { id: item.id } });
    return;
  }

  router.push({ name: "EventDetail", params: { id: item.id } });
}
</script>

<style scoped>
.map-card {
  position: relative;
  min-height: 520px;
  overflow: hidden;
  border-radius: 28px;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 26px 60px rgba(0, 0, 0, 0.22);
}

.map-card__canvas {
  height: 520px;
}

.map-card__empty {
  position: absolute;
  inset: 0;
  z-index: 1001;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.85rem;
  min-height: 520px;
  padding: 2rem;
  color: #c9c9c9;
  text-align: center;
  background: linear-gradient(180deg, #111111 0%, #181818 100%);
}

.map-card__empty i {
  color: #1db954;
  font-size: 2rem;
}

:deep(.search-map-popup) {
  display: grid;
  gap: 0.45rem;
  min-width: 190px;
}

:deep(.search-map-popup button) {
  min-height: 34px;
  border: 0;
  border-radius: 10px;
  background: #1db954;
  color: #041106;
  font-weight: 700;
}
</style>
