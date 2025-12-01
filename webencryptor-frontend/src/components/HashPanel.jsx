// src/components/HashPanel.jsx
import React, { useState } from 'react';
import { postJson, postFileReturnJson } from '../api';

export default function HashPanel() {
    const [algo, setAlgo] = useState('bash256'); // bash256, bash384, bash512, belt
    const [text, setText] = useState('Hello world');
    const [result, setResult] = useState(null);
    const [file, setFile] = useState(null);
    const [verifyHash, setVerifyHash] = useState('');

    function endpointForText(a) {
        if (a === 'belt') return '/crypto/hash/belt';
        return `/crypto/hash/${a}`;
    }
    function endpointForFile(a) {
        if (a === 'belt') return '/crypto/hash/belt-file';
        return `/crypto/hash/${a}-file`;
    }
    function endpointForVerifyFile(a) {
        if (a === 'belt') return `/crypto/hash/belt/verify-file`;
        return `/crypto/hash/${a}/verify-file`;
    }

    async function hashText() {
        try {
            const res = await postJson(endpointForText(algo), { data: text });
            setResult(res);
        } catch (e) { alert(e.message || e) }
    }

    async function hashFile() {
        if (!file) return alert('Choose file');
        try {
            const r = await postFileReturnJson(endpointForFile(algo), null, file);
            setResult(r);
        } catch (e) { alert(e.message || e) }
    }

    async function verifyFile() {
        if (!file) return alert('Choose file');
        try {
            const url = endpointForVerifyFile(algo) + '?hashBase64=' + encodeURIComponent(verifyHash);
            const r = await postFileReturnJson(url, null, file);
            setResult(r);
        } catch (e) { alert(e.message || e) }
    }

    return (
        <div className="panel">
            <h2>Hash</h2>

            <div className="row">
                <div className="label">Algorithm</div>
                <select value={algo} onChange={e=>setAlgo(e.target.value)} className="input">
                    <option value="bash256">Bash256</option>
                    <option value="bash384">Bash384</option>
                    <option value="bash512">Bash512</option>
                    <option value="belt">BeltHash</option>
                </select>
            </div>

            <div className="row">
                <textarea className="input" value={text} onChange={e=>setText(e.target.value)} rows={3} />
                <button className="btn" onClick={hashText}>Hash text</button>
            </div>

            <hr />

            <div className="row">
                <div className="label">File</div>
                <input type="file" onChange={e=>setFile(e.target.files[0])} />
                <button className="btn" onClick={hashFile}>Hash file</button>
            </div>

            <div className="row" style={{marginTop:8}}>
                <input placeholder="hash base64 to verify" value={verifyHash} onChange={e=>setVerifyHash(e.target.value)} className="input" />
                <button className="btn secondary" onClick={verifyFile}>Verify file</button>
            </div>

            <pre style={{whiteSpace:'pre-wrap', marginTop:10}}>{result ? JSON.stringify(result, null, 2) : ''}</pre>
        </div>
    );
}
