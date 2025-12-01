// src/components/FileEncryptPanel.jsx
import React, { useState } from 'react';
import { postFileDownload } from '../api';
import { generateBase64Key, generateBase64Iv, copyToClipboard, humanFileSize, validateKeyIv } from '../utils';

export default function FileEncryptPanel({ maxFileSize = 4 * 1024 * 1024, defaultAlgorithm = 'BeltCTR' }) {
    const [file, setFile] = useState(null);
    const [key, setKey] = useState('');
    const [iv, setIv] = useState('');
    const [algorithm, setAlgorithm] = useState(defaultAlgorithm);
    const [busy, setBusy] = useState(false);

    function onFileChange(e) {
        const f = e.target.files[0];
        setFile(f);
    }

    function endpointForAlg(mode, alg) {
        const a = alg.toLowerCase();
        // NOTE: adjust these if your backend uses different paths.
        if (a.includes('ctr')) return `/crypto/${mode}-file-stream/belt-ctr`.replace('-file-stream','file-stream').replace('/file-stream','/encrypt-file-stream').replace('/decrypt-file-stream','/decrypt-file-stream').replace('/encrypt-file-stream','/crypto/encrypt-file-stream/belt-ctr').replace('/decrypt-file-stream','/crypto/decrypt-file-stream/belt-ctr');
        // simpler, explicit map:
    }

    // we'll use an explicit map — safer and easier to read:
    const fileEndpointMap = {
        encrypt: {
            'beltctr': '/crypto/encrypt-file-stream/belt-ctr',
            'beltcfb': '/crypto/encrypt-file/belt-cfb',
            'beltcbc': '/crypto/encrypt-file/belt-cbc',
            'beltecb': '/crypto/encrypt-file/belt-ecb'
        },
        decrypt: {
            'beltctr': '/crypto/decrypt-file-stream/belt-ctr',
            'beltcfb': '/crypto/decrypt-file/belt-cfb',
            'beltcbc': '/crypto/decrypt-file/belt-cbc',
            'beltecb': '/crypto/decrypt-file/belt-ecb'
        }
    };

    function resolveEndpoint(mode) {
        const key = algorithm.toLowerCase().replace(/[^a-z0-9]/g,'');
        // normalize mapping keys
        if (fileEndpointMap[mode] && fileEndpointMap[mode][key]) return fileEndpointMap[mode][key];
        // fallback to CTR stream endpoints
        return (mode === 'encrypt')
            ? '/crypto/encrypt-file-stream/belt-ctr'
            : '/crypto/decrypt-file-stream/belt-ctr';
    }

    async function doFileOp(mode) {
        if (!file) return alert('Choose file');
        if (file.size > maxFileSize) return alert(`File too big: ${humanFileSize(file.size)} (max ${humanFileSize(maxFileSize)})`);

        const { ok, messages } = validateKeyIv(algorithm, key, iv);
        if (!ok) {
            if (!confirm('Validation issues:\n' + messages.join('\n') + '\n\nTry anyway?')) return;
        }

        try {
            setBusy(true);
            const params = { algorithm, keyBase64: key, ivBase64: iv };
            const endpoint = resolveEndpoint(mode);
            // postFileDownload expects path like '/crypto/encrypt-file-stream/belt-ctr' + params
            const { blob, filename } = await postFileDownload(endpoint, params, file);
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename || (mode === 'encrypt' ? file.name + '.enc' : file.name + '.dec');
            document.body.appendChild(a);
            a.click();
            a.remove();
            URL.revokeObjectURL(url);
        } catch (e) {
            alert('Error: ' + (e.message || e));
        } finally { setBusy(false); }
    }

    const keyIvStatus = validateKeyIv(algorithm, key, iv);

    return (
        <div className="panel">
            <h3>File</h3>

            <div className="row">
                <div className="label">File</div>
                <input type="file" onChange={onFileChange} className="input"/>
                {file && <div className="small">{file.name} — {humanFileSize(file.size)}</div>}
            </div>

            <div className="row">
                <div className="label">Algorithm</div>
                <select value={algorithm} onChange={e=>setAlgorithm(e.target.value)} className="input">
                    <option value="BeltCTR">BeltCTR</option>
                    <option value="BeltCFB">BeltCFB</option>
                    <option value="BeltCBC">BeltCBC</option>
                    <option value="BeltECB">BeltECB</option>
                </select>
            </div>

            <div className="row">
                <div className="label">Key Base64</div>
                <input className="input" value={key} onChange={e=>setKey(e.target.value)} />
                <button className="btn small secondary" onClick={()=>setKey(generateBase64Key())}>Gen</button>
                <button className="btn small secondary" onClick={()=>copyToClipboard(key)}>Copy</button>
            </div>
            <div className="helper">
                {keyIvStatus.messages.length > 0 && keyIvStatus.messages[0].toLowerCase().includes('key') ? (
                    <div className={"hint-err"}>{keyIvStatus.messages.filter(m=>m.toLowerCase().includes('key')).join('; ')}</div>
                ) : (
                    <div className={"hint-ok"}>Key looks OK (must be 32 bytes -> Base64 length 44)</div>
                )}
            </div>

            <div className="row" style={{marginTop:6}}>
                <div className="label">IV Base64</div>
                <input className="input" value={iv} onChange={e=>setIv(e.target.value)} disabled={algorithm.toUpperCase().includes('ECB')} />
                <button className="btn small secondary" onClick={()=>setIv(generateBase64Iv())} disabled={algorithm.toUpperCase().includes('ECB')}>Gen</button>
                <button className="btn small secondary" onClick={()=>copyToClipboard(iv)} disabled={algorithm.toUpperCase().includes('ECB')}>Copy</button>
            </div>
            <div className="helper">
                {algorithm.toUpperCase().includes('ECB') ? (
                    <div className="small">ECB doesn't use IV.</div>
                ) : (keyIvStatus.messages.length > 0 && keyIvStatus.messages.some(m=>m.toLowerCase().includes('iv')) ? (
                    <div className="hint-err">{keyIvStatus.messages.filter(m=>m.toLowerCase().includes('iv')).join('; ')}</div>
                ) : (
                    <div className="hint-ok">IV looks OK (must be 16 bytes -> Base64 length 24)</div>
                ))}
            </div>

            <div className="row" style={{marginTop:12}}>
                <button className="btn" onClick={()=>doFileOp('encrypt')} disabled={busy}>Encrypt & Download</button>
                <button className="btn secondary" onClick={()=>doFileOp('decrypt')} disabled={busy}>Decrypt & Download</button>
            </div>

            <div className="footer-note">Max file size: {humanFileSize(maxFileSize)} (frontend check).</div>
        </div>
    );
}
