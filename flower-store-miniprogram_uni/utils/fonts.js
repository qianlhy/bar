/**
 * 微信小程序字体：从 bar.twst.work 下载 → loadFontFace
 */
const FONT_BASE = 'https://bar.twst.work/api/fonts';

const FONT_LIST = [
    { family: 'Anton', file: 'Anton-Regular.ttf', weight: 'normal', timeout: 30000 },
    { family: 'BebasNeue', file: 'BebasNeue-Regular.ttf', weight: 'normal', timeout: 30000 },
    { family: 'DouyinSans', file: 'DouyinSansBold.ttf', weight: 'bold', timeout: 120000 }
];

const loadedFonts = new Set();
let loading = null;

function downloadFont(url, timeout) {
    return new Promise((resolve, reject) => {
        uni.downloadFile({
            url,
            timeout: timeout || 60000,
            success: (res) => {
                if (res.statusCode === 200 && res.tempFilePath) {
                    resolve(res.tempFilePath);
                } else {
                    reject(new Error('download status ' + res.statusCode));
                }
            },
            fail: reject
        });
    });
}

function loadOneFont(family, tempPath, weight) {
    return new Promise((resolve) => {
        uni.loadFontFace({
            global: true,
            family,
            source: 'url("' + tempPath + '")',
            scopes: ['webview', 'native'],
            desc: { style: 'normal', weight: weight || 'normal' },
            success: () => {
                loadedFonts.add(family);
                console.log('[font] ok ' + family);
                resolve(family);
            },
            fail: (e) => {
                console.warn('[font] load fail ' + family, e);
                resolve(null);
            }
        });
    });
}

export function loadAppFonts(onEach) {
    // #ifndef MP-WEIXIN
    return Promise.resolve([]);
    // #endif

    // #ifdef MP-WEIXIN
    const pending = FONT_LIST.filter((f) => !loadedFonts.has(f.family));
    if (!pending.length) return Promise.resolve([...loadedFonts]);

    if (loading) return loading;

    loading = Promise.all(
        pending.map((f) => {
            const url = FONT_BASE + '/' + f.file;
            return downloadFont(url, f.timeout)
                .then((path) => loadOneFont(f.family, path, f.weight))
                .then((family) => {
                    // 每个字体各自加载完就立刻通知，不等最慢的
                    if (family && typeof onEach === 'function') onEach(family);
                    return family;
                })
                .catch((err) => {
                    console.warn('[font] download fail ' + f.family + ' ' + url, err);
                    return null;
                });
        })
    ).finally(() => {
        loading = null;
    });

    return loading;
    // #endif
}
