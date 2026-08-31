/**
 * 全站共用的 Tailwind Play CDN 設定（覆寫 slate / blue 色盤為品牌用的靛紫色調）。
 * 需放在 <script src="https://cdn.tailwindcss.com"> 之後載入。
 */
tailwind.config = {
    theme: {
        extend: {
            fontFamily: { sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'] },
            colors: {
                slate: { 50: '#f3f4f8', 100: '#e2e4ee', 200: '#c3c6da', 300: '#9498b8', 400: '#6a6f95', 500: '#4d5175', 600: '#383b58', 700: '#262a44', 800: '#181b30', 900: '#0f1122', 950: '#090a17' },
                blue: { 300: '#b3aaff', 400: '#9b8fff', 500: '#8171ff', 600: '#6c5ce7', 700: '#5847c4', 800: '#453798', 900: '#332a70' },
            },
        },
    },
};
