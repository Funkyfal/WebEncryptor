// src/App.jsx
import React, { useState } from 'react';
import TopTabs from './components/TopTabs';
import CryptoPanel from './CryptoPanel';
import HashPanel from './components/HashPanel';
import MACPanel from './components/MACPanel';

export default function App() {
    const [active, setActive] = useState('crypto');
    return (
        <div className="app">
            <div className="header">
                <h1>WebEncryptor</h1>
            </div>

            <div className="top-tabs">
                <button className={`tab-btn ${active==='crypto' ? 'active' : ''}`} onClick={()=>setActive('crypto')}>Encrypt / Decrypt</button>
                <button className={`tab-btn ${active==='hash' ? 'active' : ''}`} onClick={()=>setActive('hash')}>Hash</button>
                <button className={`tab-btn ${active==='mac' ? 'active' : ''}`} onClick={()=>setActive('mac')}>MAC</button>
            </div>

            {active === 'crypto' && <CryptoPanel />}
            {active === 'hash' && <HashPanel />}
            {active === 'mac' && <MACPanel />}
        </div>
    );
}
