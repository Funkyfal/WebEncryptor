const BASE = import.meta.env.VITE_API_BASE || 'http://127.0.0.1:8080';

export async function postJson(path, body) {
    const resp = await fetch(BASE + path, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    const ct = resp.headers.get('content-type') || '';
    if (ct.includes('application/json') || ct.includes('text')) {
        return resp.json().catch(() => resp.text());
    }
    return resp;
}

export async function postFileReturnJson(path, paramsObj, file) {
    const params = new URLSearchParams(paramsObj || {}).toString();
    const url = BASE + path + (params ? '?' + params : '');
    const fd = new FormData();
    fd.append('file', file);
    const resp = await fetch(url, { method: 'POST', body: fd });
    if (!resp.ok) throw new Error(await resp.text());
    return resp.json();
}

export async function postFileDownload(path, paramsObj, file) {
    const params = new URLSearchParams(paramsObj || {}).toString();
    const url = BASE + path + (params ? '?' + params : '');
    const fd = new FormData();
    fd.append('file', file);
    const resp = await fetch(url, { method: 'POST', body: fd });
    if (!resp.ok) throw new Error(await resp.text());
    const blob = await resp.blob();
    let filename = file.name;
    const cd = resp.headers.get('content-disposition');
    if (cd) {
        const m = /filename="?([^"]+)"?/.exec(cd);
        if (m) filename = m[1];
    }
    return { blob, filename };
}
