/**
 * 全站共用的頂部導覽列元件。
 * 標記與樣式都放在 layout/header.html（自帶 <style>），本檔案負責載入並注入頁面。
 * 用法：頁面 <body> 開頭放一個 <div id="app-header"></div>，
 * 載入本檔案後呼叫 renderAppHeader({ active, roomId }) 即可。
 */
(function () {
    const API = '/api';
    const STYLE_HREFS = [`${API}/common/theme.css`];
    const FRAGMENT_URL = `${API}/layout/header.html`;

    function ensureStyles() {
        STYLE_HREFS.forEach(href => {
            if (document.querySelector(`link[href="${href}"]`)) return;
            const link = document.createElement('link');
            link.rel = 'stylesheet';
            link.href = href;
            document.head.appendChild(link);
        });
    }

    window.renderAppHeader = async function (opts) {
        opts = opts || {};
        ensureStyles();

        let container = document.getElementById('app-header');
        if (!container) {
            container = document.createElement('div');
            container.id = 'app-header';
            document.body.insertBefore(container, document.body.firstChild);
        }

        const res = await fetch(FRAGMENT_URL);
        container.innerHTML = await res.text();

        if (opts.active) {
            const link = container.querySelector(`[data-nav="${opts.active}"]`);
            if (link) link.classList.add('active');
        }
        if (opts.roomId) {
            const broadcastLink = document.getElementById('headerBroadcastLink');
            if (broadcastLink) {
                broadcastLink.href = `${API}/live/broadcast.html?room=${encodeURIComponent(opts.roomId)}`;
            }
        }
    };
})();
