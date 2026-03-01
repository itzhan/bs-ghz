/**
 * 图片占位符工具
 * 当数据库中图片字段为 NULL 时，返回内联 SVG 占位图
 */

function createPlaceholderSvg(text: string, width: number, height: number): string {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" viewBox="0 0 ${width} ${height}">
    <rect width="100%" height="100%" fill="#f1f5f9"/>
    <text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-family="sans-serif" font-size="14" fill="#94a3b8">${text}</text>
  </svg>`
  return `data:image/svg+xml,${encodeURIComponent(svg)}`
}

export const PLACEHOLDER_STALL = createPlaceholderSvg('档口图片', 400, 200)
export const PLACEHOLDER_DISH = createPlaceholderSvg('菜品图片', 300, 200)
export const PLACEHOLDER_SMALL = createPlaceholderSvg('菜品', 120, 120)
