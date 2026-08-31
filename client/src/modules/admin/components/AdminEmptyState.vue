<template>
  <div class="admin-empty-state">
    <div class="admin-empty-state__icon">
      <i class="bi bi-inbox"></i>
    </div>
    <strong>{{ title }}</strong>
    <p>{{ text }}</p>
    <button
      v-if="actionLabel"
      type="button"
      class="btn btn-outline-light btn-sm"
      @click="$emit('action')"
    >
      {{ actionLabel }}
    </button>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  text: {
    type: String,
    required: true
  },
  actionLabel: {
    type: String,
    default: ""
  }
});

defineEmits(["action"]);
</script>

<style scoped>
.admin-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.8rem;
  margin-top: 1.1rem;
  padding: 2.6rem 1.2rem;
  border-radius: 24px;
  border: 1px dashed rgba(29, 185, 84, 0.24);
  background:
    radial-gradient(circle at 50% 0%, rgba(29, 185, 84, 0.13), transparent 34%),
    rgba(255, 255, 255, 0.03);
  color: #d8d8d8;
  text-align: center;
}

.admin-empty-state__icon {
  width: 58px;
  height: 58px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(29, 185, 84, 0.22), rgba(13, 202, 240, 0.1));
  color: #8af0b2;
  font-size: 1.45rem;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.06);
}

.admin-empty-state strong {
  color: #ffffff;
  font-size: 1.05rem;
}

.admin-empty-state p {
  max-width: 520px;
  margin: 0;
  color: #aeb0b3;
}

.admin-empty-state .btn {
  border-radius: 999px;
  font-weight: 800;
  padding-inline: 0.9rem;
}
</style>
