<template>
  <section class="map-card">
    <div class="map-card__header">
      <div class="map-card__eyebrow">{{ eyebrow }}</div>
      <h2>{{ title }}</h2>
      <p>{{ subtitle }}</p>
    </div>

    <div v-if="!hasCoordinates" class="map-card__empty">
      <i class="bi bi-geo-alt"></i>
      <span>{{ emptyLabel }}</span>
    </div>
    <div v-else ref="mapElement" class="map-card__canvas"></div>
  </section>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";
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
  eyebrow: { type: String, default: "" },
  title: { type: String, default: "" },
  subtitle: { type: String, default: "" },
  emptyLabel: { type: String, default: "" },
  markerTitle: { type: String, default: "" },
  latitude: { type: Number, default: null },
  longitude: { type: Number, default: null }
});

const mapElement = ref(null);
const hasCoordinates = computed(
  () => typeof props.latitude === "number" && typeof props.longitude === "number"
);

let mapInstance;
let markerInstance;

onMounted(() => {
  syncMap();
});

onBeforeUnmount(() => {
  if (mapInstance) {
    mapInstance.remove();
  }
});

watch(
  () => [props.latitude, props.longitude],
  () => {
    syncMap();
  }
);

async function syncMap() {
  await nextTick();

  if (!hasCoordinates.value) {
    resetMap();
    return;
  }

  buildMap();
  refreshMapSize();
}

function resetMap() {
  if (!mapInstance) {
    return;
  }

  mapInstance.remove();
  mapInstance = null;
  markerInstance = null;
}

function buildMap() {
  if (!mapElement.value) return;

  if (!mapInstance) {
    mapInstance = L.map(mapElement.value, {
      scrollWheelZoom: false,
      zoomControl: true
    }).setView(
      [props.latitude, props.longitude],
      14
    );
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: "&copy; OpenStreetMap contributors",
      maxZoom: 19
    }).addTo(mapInstance);
  } else {
    mapInstance.setView([props.latitude, props.longitude], 14);
  }

  if (markerInstance) {
    markerInstance.remove();
  }

  markerInstance = L.marker([props.latitude, props.longitude]).addTo(mapInstance);
  markerInstance.bindPopup(createMapPopup());
}

function refreshMapSize() {
  if (!mapInstance) {
    return;
  }

  window.requestAnimationFrame(() => {
    mapInstance?.invalidateSize();
    window.setTimeout(() => mapInstance?.invalidateSize(), 140);
  });
}

function createMapPopup() {
  const popup = document.createElement("div");
  popup.className = "space-map-popup";

  const title = document.createElement("strong");
  title.textContent = props.markerTitle || props.title;
  popup.appendChild(title);

  return popup;
}
</script>

<style scoped>
.map-card {
  overflow: hidden;
  border-radius: 28px;
  background: linear-gradient(180deg, rgba(17, 17, 17, 0.96) 0%, rgba(24, 24, 24, 0.96) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #ffffff;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.22);
}
.map-card__header {
  padding: 1.25rem 1.25rem 1rem;
}
.map-card__eyebrow {
  color: #1db954;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}
.map-card h2 {
  margin: 0.35rem 0 0.2rem;
  color: #ffffff;
  font-size: 1.18rem;
  font-weight: 700;
}
.map-card p {
  margin: 0;
  color: #bdbdbd;
  line-height: 1.45;
}
.map-card__canvas {
  height: 360px;
  margin: 0 1rem 1rem;
  overflow: hidden;
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}
.map-card__empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  min-height: 300px;
  margin: 0 1rem 1rem;
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background:
    radial-gradient(circle at top, rgba(29, 185, 84, 0.16), transparent 42%),
    rgba(255, 255, 255, 0.04);
  color: #d1d1d1;
}
.map-card__empty i {
  color: #1db954;
  font-size: 2rem;
}

.map-card :deep(.leaflet-container) {
  background: #111111;
  font-family: inherit;
}

.map-card :deep(.leaflet-tile) {
  filter: saturate(0.88) contrast(1.04);
}

.map-card :deep(.leaflet-control-zoom a) {
  background: rgba(17, 17, 17, 0.9);
  border-color: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.map-card :deep(.leaflet-control-zoom a:hover) {
  background: rgba(29, 185, 84, 0.18);
  color: #ffffff;
}

.map-card :deep(.leaflet-control-attribution) {
  background: rgba(17, 17, 17, 0.78);
  color: #d1d1d1;
}

.map-card :deep(.leaflet-control-attribution a) {
  color: #1db954;
}

.map-card :deep(.leaflet-popup-content-wrapper),
.map-card :deep(.leaflet-popup-tip) {
  background: #111111;
  color: #ffffff;
  border: 1px solid rgba(29, 185, 84, 0.24);
}

.map-card :deep(.space-map-popup) {
  min-width: 160px;
  color: #ffffff;
}

.map-card :deep(.space-map-popup strong) {
  color: #ffffff;
}
</style>
