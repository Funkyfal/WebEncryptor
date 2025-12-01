// src/CryptoPanel.jsx
import React, { useState } from 'react';
import FileEncryptPanel from './components/FileEncryptPanel';
import { postJson } from './api';
import { generateBase64Key, generateBase64Iv, copyToClipboard, validateKeyIv } from './utils';

export default function CryptoPanel() {
    const [mode, setMode] = useState('encrypt'); // encrypt / decrypt
    const [algo, setAlgo] = useState('BeltCTR');
    const [text, setText] = useState('');
    const [key, setKey] = useState('');
    const [iv, setIv] = useState('');
    const [result, setResult] = useState('');
    const [loading, setLoading] = useState(false);
    const maxFileSize = 4 * 1024 * 1024;

    function algorithmToPath(a) {
        const up = a.toLowerCase();
        if (up.includes('ctr')) return 'belt-ctr';
        if (up.includes('cfb')) return 'belt-cfb';
        if (up.includes('cbc')) return 'belt-cbc';
        if (up.includes('ecb')) return 'belt-ecb';
        return a;
    }

    async function onExecute() {
        const { ok, messages } = validateKeyIv(algo, key, iv);
        if (!ok && !confirm('Validation problems:\n' + messages.join('\n') + '\n\nTry anyway?')) return;

        const body = {};
        if (mode === 'encrypt') {
            body.plaintext = text;
            if (algo === 'BeltCTR' || algo === 'BeltCFB') body.algorithm = algo;
            body.keyBase64 = key;
            if (!algo.toUpperCase().includes('ECB')) body.ivBase64 = iv;
        } else {
            body.ciphertextBase64 = text;
            if (algo === 'BeltCTR' || algo === 'BeltCFB') body.algorithm = algo;
            body.keyBase64 = key;
            if (!algo.toUpperCase().includes('ECB')) body.ivBase64 = iv;
        }

        const endpoint = `/crypto/${mode}/${algorithmToPath(algo)}`;
        setLoading(true);
        try {
            const res = await postJson(endpoint, body);
            if (res.ciphertext) setResult(res.ciphertext);
            else if (res.plaintext) setResult(res.plaintext);
            else setResult(JSON.stringify(res));
        } catch (e) {
            alert('Request error: ' + (e.message || e));
        } finally { setLoading(false); }
    }

    const status = validateKeyIv(algo, key, iv);

    return (
        <div className="panel">
            <h2>Crypto</h2>

            <div className="row">
                <label className="label"><input type="radio" checked={mode==='encrypt'} onChange={()=>setMode('encrypt')} /> Encrypt</label>
                <label style={{marginLeft:12}} className="label"><input type="radio" checked={mode==='decrypt'} onChange={()=>setMode('decrypt')} /> Decrypt</label>
            </div>

            <div className="row">
                <div className="label">Algorithm</div>
                <select value={algo} onChange={e=>setAlgo(e.target.value)} className="input">
                    <option value="BeltCTR">BeltCTR</option>
                    <option value="BeltCFB">BeltCFB</option>
                    <option value="BeltCBC">BeltCBC</option>
                    <option value="BeltECB">BeltECB</option>
                </select>
            </div>

            <div className="row">
                <textarea className="input" value={text} onChange={e=>setText(e.target.value)} rows={4} placeholder={mode==='encrypt' ? 'Plaintext' : 'Ciphertext Base64'} />
            </div>

            <div className="row">
                <div className="label">Key Base64</div>
                <input className="input" value={key} onChange={e=>setKey(e.target.value)} />
                <button className="btn small secondary" onClick={()=>setKey(generateBase64Key())}>Gen</button>
                <button className="btn small secondary" onClick={()=>copyToClipboard(key)}>Copy</button>
            </div>
            <div className="helper">
                {status.messages.some(m=>m.toLowerCase().includes('key')) ? (
                    <div className="hint-err">{status.messages.filter(m=>m.toLowerCase().includes('key')).join('; ')}</div>
                ) : (
                    <div className="hint-ok">Key should be 32 bytes (Base64 length 44)</div>
                )}
            </div>

            <div className="row" style={{marginTop:6}}>
                <div className="label">IV Base64</div>
                <input className="input" value={iv} onChange={e=>setIv(e.target.value)} disabled={algo.toUpperCase().includes('ECB')} />
                <button className="btn small secondary" onClick={()=>setIv(generateBase64Iv())} disabled={algo.toUpperCase().includes('ECB')}>Gen</button>
                <button className="btn small secondary" onClick={()=>copyToClipboard(iv)} disabled={algo.toUpperCase().includes('ECB')}>Copy</button>
            </div>
            <div className="helper">
                {algo.toUpperCase().includes('ECB') ? (
                    <div className="small">ECB does not use IV</div>
                ) : (status.messages.some(m=>m.toLowerCase().includes('iv')) ? (
                    <div className="hint-err">{status.messages.filter(m=>m.toLowerCase().includes('iv')).join('; ')}</div>
                ) : (
                    <div className="hint-ok">IV should be 16 bytes (Base64 length 24)</div>
                ))}
            </div>

            <div className="row" style={{marginTop:12}}>
                <button className="btn" onClick={onExecute} disabled={loading}>{loading? 'Working...' : (mode==='encrypt'? 'Encrypt':'Decrypt')}</button>
            </div>

            <div className="result-row">
                <label className="label">Result</label>
                <input readOnly value={result || ''} className="input"/>
                <button className="btn small secondary" onClick={()=>copyToClipboard(result)}>Copy</button>
            </div>

            <div style={{marginTop:12}} />
            <FileEncryptPanel maxFileSize={maxFileSize} defaultAlgorithm={algo} />
        </div>
    );
}
