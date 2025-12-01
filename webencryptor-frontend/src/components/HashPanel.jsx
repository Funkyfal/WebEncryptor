import React, { useState } from 'react'
import { postJson, postFileReturnJson } from '../api'

export default function HashPanel() {
    const [text, setText] = useState('Hello world');
    const [result, setResult] = useState(null);
    const [file, setFile] = useState(null);
    const [verifyHash, setVerifyHash] = useState('');

    async function hashText() {
        try {
            const r = await postJson('/crypto/hash/bash256', { data: text });
            setResult(r);
        } catch (e) { alert(e.message) }
    }

    async function hashFile() {
        if (!file) return alert('Choose file');
        try {
            const r = await postFileReturnJson('/crypto/hash/bash256-file', null, file);
            setResult(r);
        } catch (e) { alert(e.message) }
    }

    async function verifyFile() {
        if (!file) return alert('Choose file');
        try {
            const r = await postFileReturnJson('/crypto/hash/bash256/verify-file?hashBase64=' + encodeURIComponent(verifyHash), null, file);
            setResult(r);
        } catch (e) { alert(e.message) }
    }

    return (
        <div>
            <h2>Hash (Bash256)</h2>
            <div>
                <textarea value={text} onChange={e=>setText(e.target.value)} rows={3} style={{width:'100%'}}/>
                <button onClick={hashText}>Hash text</button>
            </div>

            <hr/>
            <div>
                <input type="file" onChange={e=>setFile(e.target.files[0])}/>
                <button onClick={hashFile}>Hash file</button>
            </div>

            <div>
                <input placeholder="hash base64 to verify" value={verifyHash} onChange={e=>setVerifyHash(e.target.value)} style={{width: '60%'}}/>
                <button onClick={verifyFile}>Verify file</button>
            </div>

            <pre style={{whiteSpace:'pre-wrap'}}>{result ? JSON.stringify(result, null, 2) : ''}</pre>
        </div>
    )
}
