<template>
  <form class="search-topbar" @submit.prevent="$emit('submit')">
    <div class="search-topbar__shell">
      <span class="search-topbar__icon" aria-hidden="true">
        <i class="bi bi-search"></i>
      </span>
      <input
        :value="modelValue"
        type="text"
        class="form-control search-topbar__input"
        :placeholder="placeholder"
        @input="$emit('update:modelValue', $event.target.value)"
      />
      <button class="btn search-topbar__button" type="submit" :disabled="loading">
        {{ loading ? loadingLabel : submitLabel }}
      </button>
    </div>
  </form>
</template>

<script setup>
defineProps({
  modelValue: {
    type: String,
    default: ""
  },
  placeholder: {
    type: String,
    default: ""
  },
  submitLabel: {
    type: String,
    default: ""
  },
  loadingLabel: {
    type: String,
    default: ""
  },
  loading: {
    type: Boolean,
    default: false
  }
});

defineEmits(["submit", "update:modelValue"]);
</script>

<style scoped>
.search-topbar {
  width: min(100%, 980px);
  margin: 0 auto;
}

.search-topbar__shell {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  padding: 0.75rem;
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  box-shadow: 0 28px 80px rgba(0, 0, 0, 0.28);
}

.search-topbar__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 18px;
  background: rgba(29, 185, 84, 0.14);
  color: #1db954;
  font-size: 1rem;
}

.search-topbar__input {
  min-height: 60px;
  padding: 0 0.25rem;
  border: 0;
  background: transparent;
  color: #ffffff;
  font-size: 1.02rem;
}

.search-topbar__input::placeholder {
  color: #8e8e8e;
}

.search-topbar__input:focus {
  color: #ffffff;
  background: transparent;
  box-shadow: none;
}

.search-topbar__button {
  min-width: 140px;
  min-height: 54px;
  padding: 0 1.4rem;
  border: 0;
  border-radius: 18px;
  background: linear-gradient(135deg, #1db954, #25d366);
  color: #041106;
  font-weight: 700;
}

.search-topbar__button:disabled {
  opacity: 0.7;
}

@media (max-width: 767.98px) {
  .search-topbar__shell {
    flex-direction: column;
    align-items: stretch;
  }

  .search-topbar__icon {
    width: 100%;
    height: 42px;
    border-radius: 14px;
  }

  .search-topbar__button {
    width: 100%;
  }
}
</style>
