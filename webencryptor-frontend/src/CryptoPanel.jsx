// src/CryptoPanel.jsx
import React, { useState } from 'react';
import FileEncryptPanel from './components/FileEncryptPanel';
import { postJson } from './api';
import { generateBase64Key, generateBase64Iv, copyToClipboard, validateBase64LengthForAlgorithm } from './utils';

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
        const errs = validateBase64LengthForAlgorithm(algo, key, iv);
        if (errs.length) return alert('Validation: ' + errs.join('; '));

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

    return (
        <div>
            <h2>Crypto</h2>

            <div>
                <label><input type="radio" checked={mode==='encrypt'} onChange={()=>setMode('encrypt')} /> Encrypt</label>
                <label style={{marginLeft:12}}><input type="radio" checked={mode==='decrypt'} onChange={()=>setMode('decrypt')} /> Decrypt</label>
            </div>

            <div style={{marginTop:10}}>
                Algorithm:
                <select value={algo} onChange={e=>setAlgo(e.target.value)} style={{marginLeft:8}}>
                    <option value="BeltCTR">BeltCTR</option>
                    <option value="BeltCFB">BeltCFB</option>
                    <option value="BeltCBC">BeltCBC</option>
                    <option value="BeltECB">BeltECB</option>
                </select>
            </div>

            <div style={{marginTop:10}}>
                <textarea value={text} onChange={e=>setText(e.target.value)} rows={4} style={{width:'100%'}} placeholder={mode==='encrypt' ? 'Plaintext' : 'Ciphertext Base64'} />
            </div>

            <div style={{marginTop:8}}>
                Key Base64:
                <input value={key} onChange={e=>setKey(e.target.value)} size={60} style={{marginLeft:8}}/>
                <button style={{marginLeft:6}} onClick={()=>setKey(generateBase64Key())}>Gen</button>
                <button style={{marginLeft:6}} onClick={()=>copyToClipboard(key)}>Copy</button>
            </div>

            <div style={{marginTop:8}}>
                IV Base64:
                <input value={iv} onChange={e=>setIv(e.target.value)} size={60} style={{marginLeft:8}} disabled={algo.toUpperCase().includes('ECB')} />
                <button style={{marginLeft:6}} onClick={()=>setIv(generateBase64Iv())} disabled={algo.toUpperCase().includes('ECB')}>Gen</button>
                <button style={{marginLeft:6}} onClick={()=>copyToClipboard(iv)} disabled={algo.toUpperCase().includes('ECB')}>Copy</button>
            </div>

            <div style={{marginTop:10}}>
                <button onClick={onExecute} disabled={loading}>{loading ? 'Working...' : (mode==='encrypt' ? 'Encrypt' : 'Decrypt')}</button>
            </div>

            <div style={{marginTop:12}}>
                <label>Result:</label>
                <div style={{display:'flex', gap:8, alignItems:'center', marginTop:6}}>
                    <input readOnly value={result || ''} style={{width:'80%'}}/>
                    <button onClick={()=>copyToClipboard(result)}>Copy</button>
                </div>
            </div>

            <hr style={{margin:'20px 0'}} />
            <FileEncryptPanel maxFileSize={maxFileSize} defaultAlgorithm={algo}/>
        </div>
    );
}
