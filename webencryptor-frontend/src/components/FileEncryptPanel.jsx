import React, { useState } from 'react'
import { postFileDownload } from '../api'

export default function FileEncryptPanel() {
    const [file, setFile] = useState(null);
    const [key, setKey] = useState('');
    const [iv, setIv] = useState('');
    const [algorithm, setAlgorithm] = useState('BeltCTR');

    async function encryptFile() {
        if (!file) return alert('Choose file');
        try {
            const { blob, filename } = await postFileDownload('/crypto/encrypt-file-stream/belt-ctr', { algorithm, keyBase64: key, ivBase64: iv }, file);
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url; a.download = filename;
            document.body.appendChild(a);
            a.click();
            a.remove();
            URL.revokeObjectURL(url);
        } catch (e) {
            alert(e.message);
        }
    }

    return (
        <div>
            <h2>File encrypt (CTR)</h2>
            <div>
                <input type="file" onChange={e=>setFile(e.target.files[0])} />
            </div>
            <div>
                Algorithm:
                <select value={algorithm} onChange={e=>setAlgorithm(e.target.value)}>
                    <option value="BeltCTR">BeltCTR</option>
                    <option value="BeltCFB">BeltCFB</option>
                    <option value="BeltCBC">BeltCBC</option>
                    <option value="BeltECB">BeltECB</option>
                </select>
            </div>
            <div>
                Key Base64: <input value={key} onChange={e=>setKey(e.target.value)} size={70}/>
            </div>
            <div>
                IV Base64: <input value={iv} onChange={e=>setIv(e.target.value)} size={70}/>
            </div>
            <div>
                <button onClick={encryptFile}>Encrypt & download</button>
            </div>
        </div>
    )
}
