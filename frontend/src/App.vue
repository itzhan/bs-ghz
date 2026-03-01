<template>
  <n-config-provider :theme-overrides="themeOverrides">
    <n-message-provider>
      <n-dialog-provider>
        <n-notification-provider>
          <router-view />
        </n-notification-provider>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import type { GlobalThemeOverrides } from 'naive-ui'

const themeOverrides: GlobalThemeOverrides = {
  common: {
    primaryColor: '#2D6A4F',
    primaryColorHover: '#3A8563',
    primaryColorPressed: '#245A42',
    primaryColorSuppl: '#2D6A4F',
    borderRadius: '8px',
    borderRadiusSmall: '6px',
    fontSize: '14px',
    fontSizeMedium: '14px',
    fontSizeLarge: '16px',
    textColor1: '#1E293B',
    textColor2: '#1E293B',
    textColor3: '#64748B',
    bodyColor: '#F5F5F0',
    cardColor: '#FFFFFF',
    modalColor: '#FFFFFF',
    popoverColor: '#FFFFFF',
  },
  Button: {
    textColorPrimary: '#FFFFFF',
    colorPrimary: '#2D6A4F',
    colorHoverPrimary: '#3A8563',
    colorPressedPrimary: '#245A42',
    borderPrimary: '#2D6A4F',
    borderHoverPrimary: '#3A8563',
    borderPressedPrimary: '#245A42',
    textColorWarning: '#FFFFFF',
    colorWarning: '#E07A5F',
    colorHoverWarning: '#E8957A',
    colorPressedWarning: '#C96A50',
    borderWarning: '#E07A5F',
    borderHoverWarning: '#E8957A',
    borderPressedWarning: '#C96A50',
    borderRadiusMedium: '8px',
    borderRadiusLarge: '10px',
  },
  Card: {
    borderRadius: '12px',
    paddingMedium: '20px',
    boxShadow: '0 1px 3px rgba(0, 0, 0, 0.06), 0 1px 2px rgba(0, 0, 0, 0.04)',
  },
  Input: {
    borderRadius: '8px',
  },
  Tag: {
    borderRadius: '6px',
  },
  Menu: {
    itemTextColorActive: '#2D6A4F',
    itemTextColorActiveHover: '#3A8563',
    itemIconColorActive: '#2D6A4F',
    itemIconColorActiveHover: '#3A8563',
  },
  Tabs: {
    tabTextColorActiveLine: '#2D6A4F',
    tabTextColorHoverLine: '#3A8563',
    barColor: '#2D6A4F',
  },
}
</script>

<style>
#app {
  min-height: 100vh;
}
</style>
