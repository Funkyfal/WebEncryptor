// src/utils.js
export function generateRandomBytes(len) {
    const arr = new Uint8Array(len);
    crypto.getRandomValues(arr);
    return arr;
}

function uint8ToBase64(u8) {
    let binary = '';
    for (let i = 0; i < u8.length; i++) {
        binary += String.fromCharCode(u8[i]);
    }
    return btoa(binary);
}

export function generateBase64Key() {
    return uint8ToBase64(generateRandomBytes(32));
}

export function generateBase64Iv() {
    return uint8ToBase64(generateRandomBytes(16));
}

export async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        return true;
    } catch (e) {
        const ta = document.createElement('textarea');
        ta.value = text;
        document.body.appendChild(ta);
        ta.select();
        try { document.execCommand('copy'); } catch (ex) {}
        ta.remove();
        return false;
    }
}

export function validateBase64LengthForAlgorithm(algorithm, keyBase64, ivBase64) {
    const errors = [];
    if (!keyBase64 || keyBase64.length === 0) {
        errors.push('Key is empty');
    } else if (keyBase64.length !== 44) {
        errors.push('Key must be 32 bytes (Base64 length 44)');
    }
    if (!algorithm.toUpperCase().includes('ECB')) {
        if (!ivBase64 || ivBase64.length !== 24) {
            errors.push('IV must be 16 bytes (Base64 length 24)');
        }
    }
    return errors;
}

export function humanFileSize(bytes) {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024*1024) return (bytes/1024).toFixed(1) + ' KB';
    return (bytes/(1024*1024)).toFixed(2) + ' MB';
}
