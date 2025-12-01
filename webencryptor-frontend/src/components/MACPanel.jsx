// src/components/MACPanel.jsx
import React, { useState } from 'react';
import { postJson, postFileDownload, postFileReturnJson } from '../api';
import { generateBase64Key, copyToClipboard } from '../utils';

export default function MACPanel() {
    const [key, setKey] = useState('');
    const [text, setText] = useState('Hello MAC world');
    const [mac, setMac] = useState('');
    const [file, setFile] = useState(null);
    const [verifyMac, setVerifyMac] = useState(null);
    const [busy, setBusy] = useState(false);

    function validateKey(keyBase64) {
        const msgs = [];
        if (!keyBase64 || keyBase64.trim().length === 0) {
            msgs.push('Key is empty');
        } else if (keyBase64.length !== 44) {
            msgs.push(`Key should be 32 bytes (Base64 length 44). Current length ${keyBase64.length}`);
        }
        return { ok: msgs.length === 0, messages: msgs };
    }

    async function computeMac() {
        const status = validateKey(key);
        if (!status.ok && !confirm('Validation problems:\n' + status.messages.join('\n') + '\n\nTry anyway?')) return;

        try {
            setBusy(true);
            const res = await postJson('/crypto/mac/belt', { keyBase64: key, data: text });
            // backend may return { macBase64: '...' } or just string; try to handle both
            const macVal = res && (res.macBase64 || res.mac || res);
            setMac(typeof macVal === 'string' ? macVal : JSON.stringify(macVal));
        } catch (e) {
            alert('Error: ' + (e.message || e));
        } finally {
            setBusy(false);
        }
    }

    async function verifyMacText() {
        const status = validateKey(key);
        if (!status.ok && !confirm('Validation problems:\n' + status.messages.join('\n') + '\n\nTry anyway?')) return;

        try {
            setBusy(true);
            const res = await postJson('/crypto/mac/belt/verify', { keyBase64: key, data: text, macBase64: mac });
            // backend returns { valid: true/false } or boolean
            const valid = (res && (res.valid !== undefined ? res.valid : res)) ?? false;
            setVerifyMac(valid);
        } catch (e) {
            alert('Error: ' + (e.message || e));
        } finally {
            setBusy(false);
        }
    }

    async function computeMacFile() {
        if (!file) return alert('Choose file');
        const status = validateKey(key);
        if (!status.ok && !confirm('Validation problems:\n' + status.messages.join('\n') + '\n\nTry anyway?')) return;

        try {
            setBusy(true);
            // Your backend currently returns binary mac file; we download it.
            const { blob, filename } = await postFileDownload('/crypto/mac/belt-file', { keyBase64: key }, file);
            // Save blob
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename || ((file && file.name) ? (file.name + '.mac') : 'mac.bin');
            document.body.appendChild(a);
            a.click();
            a.remove();
            URL.revokeObjectURL(url);
        } catch (e) {
            alert('Error: ' + (e.message || e));
        } finally {
            setBusy(false);
        }
    }

    const keyStatus = validateKey(key);

    return (
        <div className="panel">
            <h2>Belt MAC</h2>

            <div className="row">
                <div className="label">Key</div>
                <input className="input" value={key} onChange={e=>setKey(e.target.value)} />
                <button className="btn small secondary" onClick={()=>setKey(generateBase64Key())}>Gen</button>
                <button className="btn small secondary" onClick={()=>copyToClipboard(key)}>Copy</button>
            </div>

            <div className="helper">
                {keyStatus.ok ? (
                    <div className="hint-ok">Key looks OK (32 bytes → Base64 length 44)</div>
                ) : (
                    <div className="hint-err">{keyStatus.messages.join('; ')}</div>
                )}
            </div>

            <div className="row" style={{marginTop:8}}>
                <textarea className="input" rows={3} value={text} onChange={e=>setText(e.target.value)} />
                <button className="btn" onClick={computeMac} disabled={busy}>{busy ? 'Working...' : 'Compute MAC'}</button>
            </div>

            <div className="row" style={{marginTop:8}}>
                <div className="label">MAC</div>
                <input className="input" value={mac || ''} onChange={e=>setMac(e.target.value)} />
                <button className="btn small secondary" onClick={()=>copyToClipboard(mac)}>Copy</button>
                <button className="btn secondary" onClick={verifyMacText} disabled={busy}>{busy ? 'Working...' : 'Verify'}</button>
            </div>

            <div style={{marginTop:8}}>{verifyMac !== null ? <div>Verify result: <b>{String(verifyMac)}</b></div> : null}</div>

            <hr style={{margin:'12px 0'}}/>

            <div className="row">
                <div className="label">File</div>
                <input type="file" onChange={e=>setFile(e.target.files[0])} />
                <button className="btn" onClick={computeMacFile} disabled={busy}>{busy ? 'Working...' : 'Compute MAC (file)'}</button>
            </div>
        </div>
    );
}
