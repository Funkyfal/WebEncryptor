// src/components/FileEncryptPanel.jsx
import React, { useState } from 'react';
import { postFileDownload } from '../api';
import { generateBase64Key, generateBase64Iv, copyToClipboard, humanFileSize } from '../utils';

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

    async function encryptFile() {
        if (!file) return alert('Choose file');
        if (file.size > maxFileSize) return alert(`File too big: ${humanFileSize(file.size)} (max ${humanFileSize(maxFileSize)})`);
        try {
            setBusy(true);
            const params = { algorithm, keyBase64: key, ivBase64: iv };
            const { blob, filename } = await postFileDownload('/crypto/encrypt-file-stream/belt-ctr', params, file);
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename || file.name + '.enc';
            document.body.appendChild(a);
            a.click();
            a.remove();
            URL.revokeObjectURL(url);
        } catch (e) {
            alert('Error: ' + (e.message || e));
        } finally { setBusy(false); }
    }

    async function decryptFile() {
        if (!file) return alert('Choose file');
        if (file.size > maxFileSize) return alert(`File too big: ${humanFileSize(file.size)} (max ${humanFileSize(maxFileSize)})`);
        try {
            setBusy(true);
            const params = { algorithm, keyBase64: key, ivBase64: iv };
            const { blob, filename } = await postFileDownload('/crypto/decrypt-file-stream/belt-ctr', params, file);
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename || file.name + '.dec';
            document.body.appendChild(a);
            a.click();
            a.remove();
            URL.revokeObjectURL(url);
        } catch (e) {
            alert('Error: ' + (e.message || e));
        } finally { setBusy(false); }
    }

    return (
        <div>
            <h3>File</h3>
            <div>
                <input type="file" onChange={onFileChange} />
                {file && <small style={{marginLeft:8}}> {file.name} — {humanFileSize(file.size)}</small>}
            </div>

            <div style={{marginTop:8}}>
                Algorithm:
                <select value={algorithm} onChange={e=>setAlgorithm(e.target.value)} style={{marginLeft:8}}>
                    <option value="BeltCTR">BeltCTR</option>
                    <option value="BeltCFB">BeltCFB</option>
                    <option value="BeltCBC">BeltCBC</option>
                    <option value="BeltECB">BeltECB</option>
                </select>
            </div>

            <div style={{marginTop:8}}>
                Key Base64:
                <input value={key} onChange={e=>setKey(e.target.value)} size={60} style={{marginLeft:8}}/>
                <button style={{marginLeft:6}} onClick={()=>setKey(generateBase64Key())}>Gen</button>
                <button style={{marginLeft:6}} onClick={()=>copyToClipboard(key)}>Copy</button>
            </div>

            <div style={{marginTop:8}}>
                IV Base64:
                <input value={iv} onChange={e=>setIv(e.target.value)} size={60} style={{marginLeft:8}} disabled={algorithm.toUpperCase().includes('ECB')}/>
                <button style={{marginLeft:6}} onClick={()=>setIv(generateBase64Iv())} disabled={algorithm.toUpperCase().includes('ECB')}>Gen</button>
                <button style={{marginLeft:6}} onClick={()=>copyToClipboard(iv)} disabled={algorithm.toUpperCase().includes('ECB')}>Copy</button>
            </div>

            <div style={{marginTop:10, display:'flex', gap:10}}>
                <button onClick={encryptFile} disabled={busy}>Encrypt & Download</button>
                <button onClick={decryptFile} disabled={busy}>Decrypt & Download</button>
            </div>

            <div style={{marginTop:8, color:'#666', fontSize:12}}>
                Max file size: {humanFileSize(maxFileSize)} (frontend check). If backend rejects bigger files — increase server limits.
            </div>
        </div>
    );
}
